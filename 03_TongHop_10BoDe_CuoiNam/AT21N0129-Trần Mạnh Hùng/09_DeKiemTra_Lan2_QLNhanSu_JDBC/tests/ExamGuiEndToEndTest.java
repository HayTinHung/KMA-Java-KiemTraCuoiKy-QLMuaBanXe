import de2.Cap;
import de2.NhanSu;
import de2.NhanSuRepository;
import de2.QuanLyNhanSuGUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Live GUI acceptance test for the second examination, subject 1.
 *
 * The test uses the same Swing controls as a user (EDT + button clicks), while
 * the repository is used only for fixture setup, independent DB assertions,
 * and cleanup. Test IDs are intentionally outside normal seed data.
 */
public final class ExamGuiEndToEndTest {
    private static final int EMPLOYEE_1 = 98101;
    private static final int EMPLOYEE_2 = 98102;
    private static final int EMPLOYEE_EXTERNAL = 98103;
    private static final int TEST_CAP = 9001;
    private static final String TEST_CAP_NAME = "QA Cấp 20261013";
    private static final String TEST_NAME = "QA Nhân sự";
    private static final String UPDATED_NAME = "QA Nhân sự đã sửa";

    private final NhanSuRepository repository = NhanSuRepository.fromEnvironment();
    private QuanLyNhanSuGUI gui;
    private int passed;
    private int baselineEmployeeCount;
    private Path exportFile;

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "--exit-child".equals(args[0])) {
            runExitChild();
            return;
        }
        new ExamGuiEndToEndTest().run();
    }

    private void run() throws Exception {
        cleanupFixture();
        List<NhanSu> initial = repository.listNhanSu();
        baselineEmployeeCount = initial.size();
        List<Cap> seedCaps = repository.listCap();
        require(seedCaps.size() >= 1, "Fixture requires at least one Cap row");
        Cap firstCap = seedCaps.get(0);
        Cap secondCap = seedCaps.size() > 1 ? seedCaps.get(1) : firstCap;
        exportFile = new File(System.getProperty("java.io.tmpdir"), "ExamGuiEndToEndTest.txt").toPath();
        Files.deleteIfExists(exportFile);

        try {
            gui = openGui(NhanSuRepository.fromEnvironment());
            waitFor(() -> !busy() && table().getRowCount() == baselineEmployeeCount,
                    20000, "initial database load");

            // a: startup state, focus, source data, and keyboard filters.
            require(call(() -> mnsField().isFocusOwner()), "a: Mã Nhân sự receives focus");
            require(table().getRowCount() == baselineEmployeeCount, "a: source table is shown");
            onEdt(() -> mnsField().setText("abc"));
            require(call(() -> mnsField().getText().isEmpty()), "a: MNS rejects non-digits");
            onEdt(() -> mnsField().setText("123456"));
            require(call(() -> mnsField().getText().isEmpty()), "a: MNS limits five digits");
            pass("a — startup focus, database load, keyboard validation");

            // b: exact formula, including minimum BH = 100, total and average.
            onEdt(() -> {
                mnsField().setText(Integer.toString(EMPLOYEE_1));
                tnsField().setText(TEST_NAME);
                luongField().setText("1000");
            });
            selectCap(firstCap.getMaCap());
            require(call(() -> maCapField().getText().equals(Integer.toString(firstCap.getMaCap()))),
                    "b: selecting Cap fills MaCap");
            require(call(() -> tenCapField().getText().equals(firstCap.getTen())),
                    "b: selecting Cap fills Ten");
            require(call(() -> baoHiemField().getText().equals(Integer.toString(firstCap.getBaoHiem()))),
                    "b: selecting Cap fills BaoHiem");
            NhanSu expectedFirst = new NhanSu(EMPLOYEE_1, TEST_NAME, 1000,
                    firstCap.getMaCap(), firstCap.getTen(), firstCap.getBaoHiem());
            BigDecimal expectedBh = BigDecimal.valueOf(1100L * firstCap.getBaoHiem())
                    .divide(BigDecimal.valueOf(100));
            require(NhanSuRepository.tienBaoHiem(expectedFirst).compareTo(expectedBh) == 0,
                    "b: minimum BH=100 formula");
            click("btnNhapMoi");
            waitFor(() -> !busy() && table().getRowCount() == baselineEmployeeCount + 1,
                    15000, "first insert");
            assertStats("b: first total/average");
            pass("b — Cap selection and total/average with BH minimum 100");

            // c: add, select and delete a Cap via the actual modal dialogs.
            clickModal("btnThemCap");
            fillAddCapDialog(TEST_CAP, TEST_CAP_NAME, 9);
            waitFor(() -> !busy() && hasCapSafe(TEST_CAP), 15000, "add Cap");
            selectCap(TEST_CAP);
            require(call(() -> tenCapField().getText().equals(TEST_CAP_NAME)), "c: added Cap selectable");
            clickModal("btnXoaCap");
            respondConfirm("Xác nhận xóa cấp", false);
            require(hasCap(TEST_CAP), "c: No keeps Cap");
            clickModal("btnXoaCap");
            respondConfirm("Xác nhận xóa cấp", true);
            waitFor(() -> !busy() && !hasCapSafe(TEST_CAP), 15000, "delete Cap");
            pass("c — add/select/delete Cap and Yes/No confirmation");

            // d: Nhập mới second employee and empty-field validation.
            onEdt(() -> {
                mnsField().setText("");
                tnsField().setText("");
                luongField().setText("");
            });
            clickModal("btnNhapMoi");
            respondMessage("Mã Nhân sự không được để trống");
            onEdt(() -> mnsField().setText(Integer.toString(EMPLOYEE_2)));
            clickModal("btnNhapMoi");
            respondMessage("Tên Nhân sự không được để trống");
            onEdt(() -> {
                tnsField().setText("QA Nhân sự 2");
                luongField().setText("2000");
            });
            selectCap(secondCap.getMaCap());
            clickNonModal("btnNhapMoi");
            waitFor(() -> !busy() && table().getRowCount() == baselineEmployeeCount + 2,
                    15000, "second insert");
            assertStats("d: total/average after second insert");
            pass("d — Nhập mới, empty validation, DB persistence");

            // e: Sửa selected row.
            selectEmployee(EMPLOYEE_1);
            onEdt(() -> {
                tnsField().setText(UPDATED_NAME);
                luongField().setText("1100");
            });
            selectCap(secondCap.getMaCap());
            clickNonModal("btnSua");
            waitFor(() -> !busy() && employeeNameSafe(EMPLOYEE_1).equals(UPDATED_NAME), 15000, "update employee");
            assertStats("e: total/average after update");
            pass("e — Sửa selected employee");

            // f: Xóa selected row and reject No first.
            selectEmployee(EMPLOYEE_2);
            clickModal("btnXoa");
            respondConfirm("Xác nhận xóa", false);
            require(hasEmployee(EMPLOYEE_2), "f: No retains employee");
            clickModal("btnXoa");
            respondConfirm("Xác nhận xóa", true);
            waitFor(() -> !busy() && !hasEmployeeSafe(EMPLOYEE_2)
                            && table().getRowCount() == baselineEmployeeCount + 1,
                    15000, "delete employee");
            assertStats("f: total/average after delete");
            pass("f — Xóa selected employee and Yes/No confirmation");

            // g: textboxes, timer fields, tick/reset, and automatic reload.
            JTextField clock = get("thoiGianHienTai", JTextField.class);
            JTextField elapsed = get("elapsedField", JTextField.class);
            JTextField interval = get("intervalField", JTextField.class);
            require(!clock.isEditable() && !elapsed.isEditable() && !elapsed.isEnabled(),
                    "g: current/elapsed time fields are read-only and elapsed disabled");
            require(interval.isEditable(), "g: interval field is editable");
            String clockBefore = clock.getText();
            waitFor(() -> !callUnchecked(() -> clock.getText().equals(clockBefore)), 2500,
                    "clock second tick");
            onEdt(() -> interval.setText("2"));
            require(getInt("refreshSeconds") == 2, "g: interval 2 accepted");
            repository.themNhanSu(new NhanSu(EMPLOYEE_EXTERNAL, "QA ngoài giao diện", 1500,
                    firstCap.getMaCap(), firstCap.getTen(), firstCap.getBaoHiem()));
            respondMessageAsync("Cập nhật dữ liệu từ Database Server đưa lên Cửa sổ thành công", 20000);
            waitFor(() -> !busy() && table().getRowCount() == baselineEmployeeCount + 2
                            && hasEmployeeSafe(EMPLOYEE_EXTERNAL),
                    25000, "automatic reload success");
            require("0".equals(call(() -> elapsed.getText())), "g: elapsed resets at interval");
            onEdt(() -> interval.requestFocusInWindow());
            onEdt(() -> interval.setText("0"));
            require(getInt("refreshSeconds") == 0, "g: zero interval invalid while editing");
            onEdt(() -> mnsField().requestFocusInWindow());
            waitFor(() -> callUnchecked(() -> interval.getText().equals("2")), 2000,
                    "g: invalid interval restores last valid value");
            require(getInt("refreshSeconds") == 2, "g: restored interval restarts timer");
            pass("g — read-only timer fields, clock, counter reset, automatic reload");

            // h: select row fills every corresponding control and export text.
            selectEmployee(EMPLOYEE_1);
            require(call(() -> mnsField().getText().equals(Integer.toString(EMPLOYEE_1))),
                    "h: selection fills MNS");
            require(call(() -> tnsField().getText().equals(UPDATED_NAME)), "h: selection fills TNS");
            require(call(() -> luongField().getText().equals("1100")), "h: selection fills salary");
            require(call(() -> !maCapField().getText().isEmpty() && !tenCapField().getText().isEmpty()),
                    "h: selection fills Cap controls");
            clickModal("btnLuuFile");
            JFileChooser chooser = waitChooser(10000);
            onEdt(() -> {
                chooser.setSelectedFile(exportFile.toFile());
                chooser.approveSelection();
            });
            respondMessage("Đã lưu ra file thành công");
            List<String> exported = Files.readAllLines(exportFile, StandardCharsets.UTF_8);
            boolean hasUpdatedName = false;
            boolean hasTongThuLao = false;
            for (String line : exported) {
                if (line.contains(UPDATED_NAME)) hasUpdatedName = true;
                if (line.contains("Tổng Thù lao")) hasTongThuLao = true;
            }
            require(hasUpdatedName, "h: TXT contains selected employee");
            require(hasTongThuLao, "h: TXT contains total");
            pass("h — row-to-form mapping and TXT export");

            // i: verify Yes/No exit in a child because production Yes calls System.exit(0).
            disposeGui();
            runExitChildProcess();
            pass("i — exit No keeps window, exit Yes closes process");
        } finally {
            disposeGui();
            cleanupFixture();
            Files.deleteIfExists(exportFile);
            require(repository.listNhanSu().size() == baselineEmployeeCount,
                    "cleanup: baseline employee count restored");
            require(!hasCap(TEST_CAP), "cleanup: test Cap removed");
            System.out.println("DATABASE CLEANUP PASS: " + baselineEmployeeCount + " baseline employees / "
                    + repository.listCap().size() + " Caps");
        }
        System.out.println("GUI E2E PASS: " + passed + "/9 sections (a-i)");
    }

    private void assertStats(String label) throws Exception {
        NhanSuRepository.ThongKe expected = NhanSuRepository.thongKe(repository.listNhanSu());
        BigDecimal actualTotal = parseNumber(call(() -> get("totalField", JTextField.class).getText()));
        BigDecimal actualAverage = parseNumber(call(() -> get("averageField", JTextField.class).getText()));
        require(actualTotal.compareTo(expected.getTotal()) == 0, label + ": total mismatch "
                + actualTotal + " != " + expected.getTotal());
        require(actualAverage.compareTo(expected.getAverage()) == 0, label + ": average mismatch "
                + actualAverage + " != " + expected.getAverage());
    }

    private static BigDecimal parseNumber(String value) {
        String cleaned = value == null ? "" : value.trim().replace(",", "");
        return cleaned.isEmpty() ? BigDecimal.ZERO : new BigDecimal(cleaned);
    }

    private void cleanupFixture() throws Exception {
        for (int id : new int[] {EMPLOYEE_1, EMPLOYEE_2, EMPLOYEE_EXTERNAL}) {
            try { repository.xoaNhanSu(id); } catch (Exception ignored) { }
        }
        try { repository.xoaCap(TEST_CAP); } catch (Exception ignored) { }
    }

    private boolean hasEmployee(int id) throws Exception {
        for (NhanSu row : repository.listNhanSu()) {
            if (row.getMaNhanSu() == id) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEmployeeSafe(int id) {
        try { return hasEmployee(id); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }

    private String employeeName(int id) throws Exception {
        for (NhanSu row : repository.listNhanSu()) {
            if (row.getMaNhanSu() == id) return row.getTenNhanSu();
        }
        return "";
    }

    private String employeeNameSafe(int id) {
        try { return employeeName(id); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }

    private boolean hasCap(int id) throws Exception {
        for (Cap cap : repository.listCap()) {
            if (cap.getMaCap() == id) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCapSafe(int id) {
        try { return hasCap(id); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }

    private QuanLyNhanSuGUI openGui(NhanSuRepository repo) throws Exception {
        return call(() -> {
            QuanLyNhanSuGUI frame = new QuanLyNhanSuGUI(repo);
            frame.setVisible(true);
            return frame;
        });
    }

    private void disposeGui() throws Exception {
        if (gui != null && gui.isDisplayable()) {
            onEdt(() -> gui.dispose());
        }
        gui = null;
    }

    private void selectCap(int id) throws Exception {
        onEdt(() -> {
            JTree tree = get("capTree", JTree.class);
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                Cap cap = (Cap) child.getUserObject();
                if (cap.getMaCap() == id) {
                    tree.setSelectionPath(new TreePath(child.getPath()));
                    return;
                }
            }
            throw new AssertionError("Cap ID absent from JTree: " + id);
        });
    }

    private void selectEmployee(int id) throws Exception {
        waitFor(() -> table().getRowCount() > 0, 5000, "table rows before selection");
        onEdt(() -> {
            JTable t = table();
            for (int row = 0; row < t.getRowCount(); row++) {
                Object value = t.getValueAt(row, 1);
                if (Integer.toString(id).equals(String.valueOf(value))) {
                    t.setRowSelectionInterval(row, row);
                    return;
                }
            }
            throw new AssertionError("Employee absent from JTable: " + id);
        });
    }

    private void click(String fieldName) throws Exception {
        onEdt(() -> get(fieldName, JButton.class).doClick());
    }

    private void clickNonModal(String fieldName) throws Exception {
        click(fieldName);
    }

    private void clickModal(String fieldName) {
        SwingUtilities.invokeLater(() -> get(fieldName, JButton.class).doClick());
    }

    private void fillAddCapDialog(int id, String name, int insurance) throws Exception {
        JDialog dialog = waitDialogTitle("Thêm cấp mới", 10000);
        List<JTextField> fields = descendants(dialog, JTextField.class);
        require(fields.size() >= 3, "add Cap dialog has three textboxes");
        onEdt(() -> {
            fields.get(0).setText(Integer.toString(id));
            fields.get(1).setText(name);
            fields.get(2).setText(Integer.toString(insurance));
            JOptionPane pane = find(dialog, JOptionPane.class);
            pane.setValue(JOptionPane.OK_OPTION);
        });
    }

    private void respondConfirm(String title, boolean yes) throws Exception {
        JDialog dialog = waitDialogTitle(title, 10000);
        JOptionPane pane = find(dialog, JOptionPane.class);
        require(pane != null, "confirm dialog has JOptionPane");
        onEdt(() -> pane.setValue(yes ? JOptionPane.YES_OPTION : JOptionPane.NO_OPTION));
        waitFor(() -> !dialog.isShowing(), 3000, "confirmation closes");
    }

    private void respondMessage(String expected) throws Exception {
        JDialog dialog = waitDialog(expected, 15000);
        JOptionPane pane = find(dialog, JOptionPane.class);
        require(pane != null, "message dialog has JOptionPane");
        onEdt(() -> pane.setValue(JOptionPane.OK_OPTION));
        waitFor(() -> !dialog.isShowing(), 3000, "message closes");
    }

    private void respondMessageAsync(String expected, long timeout) throws Exception {
        respondMessage(expected); // The test thread waits while Swing keeps its nested modal loop alive.
    }

    private JFileChooser waitChooser(long timeout) throws Exception {
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            for (Window window : Window.getWindows()) {
                if (window.isShowing() && window instanceof Dialog) {
                    JFileChooser chooser = find((Container) window, JFileChooser.class);
                    if (chooser != null) return chooser;
                }
            }
            Thread.sleep(40);
        }
        throw new AssertionError("Timed out waiting for JFileChooser");
    }

    private JDialog waitDialog(String expected, long timeout) throws Exception {
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            for (Window window : Window.getWindows()) {
                if (!(window instanceof JDialog) || !window.isShowing()) continue;
                JDialog dialog = (JDialog) window;
                JOptionPane pane = find(dialog, JOptionPane.class);
                if (pane != null && componentText(pane).contains(expected)) return dialog;
            }
            Thread.sleep(40);
        }
        throw new AssertionError("Timed out waiting for dialog containing: " + expected);
    }

    private JDialog waitDialogTitle(String title, long timeout) throws Exception {
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog && window.isShowing()
                        && title.equals(((JDialog) window).getTitle())) return (JDialog) window;
            }
            Thread.sleep(40);
        }
        throw new AssertionError("Timed out waiting for dialog title: " + title);
    }

    private static String componentText(Component component) {
        StringBuilder text = new StringBuilder();
        if (component instanceof JLabel && ((JLabel) component).getText() != null) {
            text.append(((JLabel) component).getText()).append(' ');
        }
        if (component instanceof JTextField) text.append(((JTextField) component).getText()).append(' ');
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                text.append(componentText(child));
            }
        }
        return text.toString();
    }

    private static <T extends Component> T find(Container root, Class<T> type) {
        for (Component child : root.getComponents()) {
            if (type.isInstance(child)) return type.cast(child);
            if (child instanceof Container) {
                T nested = find((Container) child, type);
                if (nested != null) return nested;
            }
        }
        return null;
    }

    private static <T extends Component> List<T> descendants(Container root, Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (Component child : root.getComponents()) {
            if (type.isInstance(child)) result.add(type.cast(child));
            if (child instanceof Container) result.addAll(descendants((Container) child, type));
        }
        return result;
    }

    private <T> T get(String fieldName, Class<T> type) {
        return type.cast(readField(gui, fieldName));
    }

    private static Object readField(QuanLyNhanSuGUI target, String fieldName) {
        if (target == null) throw new IllegalStateException("GUI is not open");
        if ("mnsField".equals(fieldName)) return target.getMnsField();
        if ("tnsField".equals(fieldName)) return target.getTnsField();
        if ("luongField".equals(fieldName)) return target.getLuongField();
        if ("maCapField".equals(fieldName)) return target.getMaCapField();
        if ("tenCapField".equals(fieldName)) return target.getTenCapField();
        if ("baoHiemField".equals(fieldName)) return target.getBaoHiemField();
        if ("capTree".equals(fieldName)) return target.getCapTree();
        if ("btnThemCap".equals(fieldName)) return target.getBtnThemCap();
        if ("btnXoaCap".equals(fieldName)) return target.getBtnXoaCap();
        if ("thoiGianHienTai".equals(fieldName)) return target.getThoiGianHienTaiField();
        if ("elapsedField".equals(fieldName)) return target.getElapsedField();
        if ("intervalField".equals(fieldName)) return target.getIntervalField();
        if ("totalField".equals(fieldName)) return target.getTotalField();
        if ("averageField".equals(fieldName)) return target.getAverageField();
        if ("btnNhapMoi".equals(fieldName)) return target.getBtnNhapMoi();
        if ("btnSua".equals(fieldName)) return target.getBtnSua();
        if ("btnXoa".equals(fieldName)) return target.getBtnXoa();
        if ("btnThoat".equals(fieldName)) return target.getBtnThoat();
        if ("btnLuuFile".equals(fieldName)) return target.getBtnLuuFile();
        if ("table".equals(fieldName)) return target.getTable();
        if ("busy".equals(fieldName)) return Boolean.valueOf(target.isBusy());
        if ("refreshSeconds".equals(fieldName)) return Integer.valueOf(target.getRefreshSeconds());
        if ("lastValidRefreshSeconds".equals(fieldName)) return Integer.valueOf(target.getLastValidRefreshSeconds());
        throw new IllegalArgumentException("Unknown field: " + fieldName);
    }

    private JTextField mnsField() { return get("mnsField", JTextField.class); }
    private JTextField tnsField() { return get("tnsField", JTextField.class); }
    private JTextField luongField() { return get("luongField", JTextField.class); }
    private JTextField maCapField() { return get("maCapField", JTextField.class); }
    private JTextField tenCapField() { return get("tenCapField", JTextField.class); }
    private JTextField baoHiemField() { return get("baoHiemField", JTextField.class); }
    private JTable table() { return get("table", JTable.class); }
    private boolean busy() { return get("busy", Boolean.class); }
    private int getInt(String fieldName) { return get(fieldName, Integer.class); }

    private <T> T call(Callable<T> action) throws Exception {
        final Object[] result = new Object[1];
        final Exception[] failure = new Exception[1];
        SwingUtilities.invokeAndWait(() -> {
            try { result[0] = action.call(); }
            catch (Exception ex) { failure[0] = ex; }
        });
        if (failure[0] != null) throw failure[0];
        @SuppressWarnings("unchecked") T value = (T) result[0];
        return value;
    }

    private <T> T callUnchecked(Callable<T> action) {
        try { return call(action); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }

    private void onEdt(Runnable action) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try { action.run(); }
            finally { }
        });
    }

    private void waitFor(BooleanSupplier condition, long timeout, String description) throws Exception {
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            if (condition.getAsBoolean()) return;
            Thread.sleep(40);
        }
        throw new AssertionError("Timed out: " + description);
    }

    private void pass(String description) {
        passed++;
        System.out.println("PASS " + passed + ": " + description);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void runExitChild() throws Exception {
        NhanSuRepository repo = NhanSuRepository.fromEnvironment();
        final QuanLyNhanSuGUI[] holder = new QuanLyNhanSuGUI[1];
        SwingUtilities.invokeAndWait(() -> {
            holder[0] = new QuanLyNhanSuGUI(repo);
            holder[0].setVisible(true);
        });
        QuanLyNhanSuGUI gui = holder[0];
        long end = System.currentTimeMillis() + 15000L;
        while (System.currentTimeMillis() < end && readBoolean(gui, "busy")) Thread.sleep(40);
        JButton exit = gui.getBtnThoat();
        SwingUtilities.invokeLater(exit::doClick);
        JDialog dialog = waitStaticDialog("Xác nhận thoát", 10000);
        JOptionPane pane = find(dialog, JOptionPane.class);
        final JOptionPane noPane = pane;
        SwingUtilities.invokeAndWait(() -> noPane.setValue(JOptionPane.NO_OPTION));
        if (!gui.isDisplayable()) throw new AssertionError("Exit No unexpectedly closed window");
        SwingUtilities.invokeLater(exit::doClick);
        dialog = waitStaticDialog("Xác nhận thoát", 10000);
        pane = find(dialog, JOptionPane.class);
        final JOptionPane yesPane = pane;
        SwingUtilities.invokeAndWait(() -> yesPane.setValue(JOptionPane.YES_OPTION));
        Thread.sleep(500);
        throw new AssertionError("Exit Yes did not terminate child");
    }

    private static boolean readBoolean(QuanLyNhanSuGUI target, String fieldName) throws Exception {
        return (Boolean) readField(target, fieldName);
    }

    private static JDialog waitStaticDialog(String title, long timeout) throws Exception {
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog && window.isShowing()
                        && title.equals(((JDialog) window).getTitle())) return (JDialog) window;
            }
            Thread.sleep(40);
        }
        throw new AssertionError("Timed out waiting for child dialog " + title);
    }

    private void runExitChildProcess() throws Exception {
        String javaExe = new File(System.getProperty("java.home"), "bin/java.exe").getAbsolutePath();
        String classPath = "out;lib\\mssql-jdbc-13.6.0.jre11.jar";
        Process process = new ProcessBuilder(javaExe, "-cp", classPath, "ExamGuiEndToEndTest", "--exit-child")
                .directory(new File("."))
                .redirectErrorStream(true)
                .start();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InputStream in = process.getInputStream()) {
            in.transferTo(output);
        }
        boolean finished = process.waitFor(20, java.util.concurrent.TimeUnit.SECONDS);
        require(finished && process.exitValue() == 0,
                "i: child exit process failed: " + output.toString(StandardCharsets.UTF_8));
    }
}
