import ontap2.HangHoa;
import ontap2.HangHoaRepository;
import ontap2.LoaiHang;
import ontap2.QuanLyHangHoaGUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Window;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

/** Repeatable live GUI test. Run only against the local QLHangHoa test database. */
public final class GuiEndToEndTest {
    private static final int GOOD = 98761;
    private static final int EXTERNAL_GOOD = 98762;
    private static final String QUALITY = "QA tính chất 20261006";
    private static final String TYPE = "QA loại 20261006";

    private final HangHoaRepository repository = HangHoaRepository.fromEnvironment();
    private QuanLyHangHoaGUI gui;
    private int testCount;
    private Path exportPath;

    public static void main(String[] args) throws Exception {
        new GuiEndToEndTest().run();
    }

    private void run() throws Exception {
        require(repository.listHangHoa().isEmpty(), "Database must start with no goods");
        require(repository.listLoaiHang().size() == 3, "Database must start with 3 seed types");
        require(repository.listTinhChat().size() == 3, "Database must start with 3 seed qualities");
        File base = new File(System.getProperty("java.io.tmpdir"), "ontap2-gui-qa");
        base.mkdirs();
        exportPath = new File(base, "HangHoa-qa.txt").toPath();
        Files.deleteIfExists(exportPath);
        try {
            gui = onEdt(() -> {
                QuanLyHangHoaGUI frame = new QuanLyHangHoaGUI();
                frame.setVisible(true);
                return frame;
            });
            waitFor(() -> !getBoolean(gui, "busy") && text("trangThai").contains("Đã tải"), 20000,
                    "initial GUI load");
            require(table().getRowCount() == 0, "initial table empty");
            require(gui.getTitle().contains("Bắt đầu lúc"), "start time in frame title");
            JTextField startedAt = field("thoiGianBatDau");
            JTextField currentTime = field("thoiGianHienTai");
            require(!startedAt.isEditable() && startedAt.getText().startsWith("Bắt đầu: "),
                    "start time displayed in read-only JTextField");
            require(!currentTime.isEditable() && currentTime.getText().startsWith("Hiện tại: "),
                    "current time displayed in read-only JTextField");
            require(text("thoiGianHienTai").matches("Hiện tại: \\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"),
                    "current clock present");
            String firstClock = text("thoiGianHienTai");
            waitFor(() -> !text("thoiGianHienTai").equals(firstClock), 2500,
                    "current clock advances");
            boolean codeFocused = onEdt(() -> field("maHangField").isFocusOwner());
            System.out.println("OBSERVED STARTUP CODE FOCUS: " + codeFocused);
            pass("Startup load, start time, live clock, table state");

            JTextField period = field("chuKyField");
            onEdt(() -> period.requestFocusInWindow());
            waitFor(period::isFocusOwner, 2000, "period field receives focus");
            onEdt(() -> period.setText("7"));
            require(getInt(gui, "lastValidRefreshSeconds") == 7,
                    "last valid period updated to 7");
            onEdt(() -> period.setText(""));
            require(getInt(gui, "refreshSeconds") == 0, "blank period is invalid while editing");
            onEdt(() -> field("maHangField").requestFocusInWindow());
            waitFor(() -> period.getText().equals("7"), 2000,
                    "blank period restored on focus loss");
            require(getInt(gui, "refreshSeconds") == 7, "restored period re-enables timer");
            onEdt(() -> period.requestFocusInWindow());
            waitFor(period::isFocusOwner, 2000, "period field refocused");
            onEdt(() -> period.setText("0"));
            require(getInt(gui, "refreshSeconds") == 0, "zero period is invalid while editing");
            onEdt(() -> field("maHangField").requestFocusInWindow());
            waitFor(() -> period.getText().equals("7"), 2000,
                    "zero period restored on focus loss");
            require(getInt(gui, "refreshSeconds") == 7, "zero period restores last valid timer");
            onEdt(() -> period.setText("30000"));
            require(getInt(gui, "refreshSeconds") == 30000, "valid period accepted");
            pass("Timer period input validation and focus-loss restoration");

            JTextField code = field("maHangField");
            onEdt(() -> code.setText("abc"));
            require(code.getText().isEmpty(), "code rejects nondigits");
            onEdt(() -> code.setText("123456"));
            require(code.getText().isEmpty(), "code rejects more than 5 digits");
            onEdt(() -> code.setText("1234"));
            clickWithMessage(4, "Mã hàng hóa phải gồm đúng 5 chữ số");
            onEdt(() -> code.setText(Integer.toString(GOOD)));
            clickWithMessage(4, "Tên hàng hóa không được để trống");
            onEdt(() -> field("tenHangField").setText("QA Hàng hóa"));
            clickWithMessage(4, "Hãy chọn một loại hàng");
            selectType(1);
            clickWithMessage(4, "Tính chất không được để trống");
            onEdt(() -> field("tinhChatField").setText("Tính chất lạ"));
            clickWithMessage(4, "Tính chất chưa có trong danh mục");
            require(repository.listHangHoa().isEmpty(), "invalid form never inserts");
            pass("Document filters and required-field validation");

            onEdt(() -> combo().setSelectedItem("Mới"));
            require(field("tinhChatField").getText().equals("Mới"), "combo copies quality");
            selectType(2);
            require(field("maLoaiField").getText().equals("2"), "tree copies type ID");
            require(field("tenLoaiField").getText().equals("Gia dụng"), "tree copies type name");
            require(field("tyLeField").getText().equals("10"), "tree copies rate");
            pass("Combo and tree selection populate controls");

            onEdt(() -> field("tinhChatField").setText(QUALITY));
            clickAndWait(0);
            require(repository.listTinhChat().contains(QUALITY), "quality added through GUI");
            require(comboContains(QUALITY), "quality displayed in combo");
            pass("Add quality GUI → SQL → combo");

            onEdt(() -> {
                field("tenLoaiField").setText(TYPE);
                field("tyLeField").setText("12");
            });
            clickAndWait(2);
            LoaiHang addedType = null;
            for (LoaiHang t : repository.listLoaiHang()) {
                if (t.getTenLoaiHang().equals(TYPE)) {
                    addedType = t;
                    break;
                }
            }
            require(addedType != null, "added type found in DB");
            require(addedType.getMaLoai() > 3, "type ID auto incremented");
            require(addedType.getTyLe() == 12, "type rate stored");
            require(field("maLoaiField").getText().equals(Integer.toString(addedType.getMaLoai())),
                    "new type selected in tree");
            pass("Add type GUI → SQL → tree, auto ID");

            onEdt(() -> {
                code.setText(Integer.toString(GOOD));
                field("tenHangField").setText("QA Hàng hóa");
                combo().setSelectedItem(QUALITY);
            });
            clickAndWait(4);
            boolean foundGood = false;
            for (HangHoa h : repository.listHangHoa()) {
                if (h.getMaHangHoa() == GOOD) {
                    foundGood = true;
                    break;
                }
            }
            require(foundGood, "goods inserted through GUI");
            require(table().getRowCount() == 1, "inserted row shown in JTable");
            require(field("tongGiaTriField").getText().contains("1.120"), "total computed 1120");
            require(field("trungBinhField").getText().contains("1.120"), "average computed 1120");
            pass("Add goods GUI → SQL → JTable and statistics");

            final int addedTypeId = addedType.getMaLoai();
            onEdt(() -> {
                table().clearSelection();
                code.setText("");
                field("tenHangField").setText("");
                field("tinhChatField").setText("");
                table().setRowSelectionInterval(0, 0);
            });
            require(code.getText().equals(Integer.toString(GOOD)), "row selection copies code");
            require(field("tenHangField").getText().equals("QA Hàng hóa"), "row selection copies name");
            require(field("tinhChatField").getText().equals(QUALITY), "row selection copies quality");
            require(field("maLoaiField").getText().equals(Integer.toString(addedTypeId)),
                    "row selection copies type");
            pass("JTable row selection populates entire form");

            onEdt(() -> field("tenHangField").setText("QA cập nhật"));
            clickAndWait(5);
            boolean updatedGood = false;
            for (HangHoa h : repository.listHangHoa()) {
                if (h.getMaHangHoa() == GOOD && "QA cập nhật".equals(h.getTenHangHoa())) {
                    updatedGood = true;
                    break;
                }
            }
            require(updatedGood, "GUI update persists");
            pass("Update goods GUI → SQL");

            selectType(addedType.getMaLoai());
            onEdt(() -> combo().setSelectedItem(QUALITY));
            clickWithConfirmAndMessage(3, false, "Xóa loại hàng");
            require(hasType(TYPE), "type deletion No retains row");
            clickWithConfirmAndMessage(1, false, "Xóa tính chất");
            require(repository.listTinhChat().contains(QUALITY), "quality deletion No retains row");
            clickWithConfirmAndMessage(3, true, "Xóa loại hàng", "đang được sử dụng");
            require(hasType(TYPE), "referenced type cannot be removed");
            clickWithConfirmAndMessage(1, true, "Xóa tính chất", "đang được sử dụng");
            require(repository.listTinhChat().contains(QUALITY), "referenced quality cannot be removed");
            pass("Category delete No and FK-restricted delete messages");

            SwingUtilities.invokeLater(() -> button(8).doClick());
            JFileChooser chooser = awaitChooser(10000);
            onEdt(() -> {
                chooser.setSelectedFile(exportPath.toFile());
                chooser.approveSelection();
            });
            respondDialog("Đã lưu bảng Hàng hóa", true, 15000);
            waitFor(() -> Files.exists(exportPath), 10000, "TXT export");
            List<String> lines = Files.readAllLines(exportPath, StandardCharsets.UTF_8);
            require(lines.size() == 2, "TXT contains header and one row");
            require(lines.get(0).equals("MHH\tTHH\tMaLoai\tTinhChat"), "TXT four schema columns");
            require(lines.get(1).startsWith(GOOD + "\tQA cập nhật\t" + addedType.getMaLoai() + "\t" + QUALITY),
                    "TXT row matches DB");
            pass("Save-as dialog and UTF-8 TXT content");

            clickWithConfirmAndMessage(6, false, "Xóa hàng hóa");
            require(repository.listHangHoa().size() == 1, "goods deletion No retains row");
            clickWithConfirmAndMessage(6, true, "Xóa hàng hóa");
            waitFor(() -> !getBoolean(gui, "busy"), 15000, "goods delete");
            require(repository.listHangHoa().isEmpty(), "GUI delete persists");
            require(table().getRowCount() == 0, "GUI delete updates JTable");
            require(field("tongGiaTriField").getText().contains("0,00"), "GUI delete updates total");
            pass("Delete goods No/Yes and zero statistics");

            selectType(addedType.getMaLoai());
            clickWithConfirmAndMessage(3, true, "Xóa loại hàng");
            waitFor(() -> !getBoolean(gui, "busy"), 15000, "type delete");
            require(!hasType(TYPE), "GUI type deletion persists");
            onEdt(() -> combo().setSelectedItem(QUALITY));
            clickWithConfirmAndMessage(1, true, "Xóa tính chất");
            waitFor(() -> !getBoolean(gui, "busy"), 15000, "quality delete");
            require(!repository.listTinhChat().contains(QUALITY), "GUI quality deletion persists");
            pass("Delete unreferenced type and quality GUI → SQL");

            // Prove automatic refresh reads a change made outside the GUI.
            onEdt(() -> period.setText("5"));
            waitFor(() -> Integer.parseInt(field("demGiayField").getText()) >= 1, 3500,
                    "elapsed-second counter increment");
            repository.themHangHoa(new HangHoa(EXTERNAL_GOOD, "QA từ bên ngoài", 1, "Mới"));
            respondDialog("Đã cập nhật dữ liệu từ Database Server", true, 30000);
            require(field("demGiayField").getText().matches("[0-4]"), "timer reset below period");
            require(table().getRowCount() == 1, "automatic refresh loaded external SQL row");
            pass("Counter tick, period reset, automatic DB reload and success dialog");
            onEdt(() -> period.setText("30000"));
            repository.xoaHangHoa(EXTERNAL_GOOD);

            // Exit uses a Yes/No dialog and respects the choice.
            clickWithConfirmAndMessage(7, false, "Bạn có chắc muốn thoát");
            require(gui.isDisplayable(), "Exit No keeps frame open");
            clickWithConfirmAndMessage(7, true, "Bạn có chắc muốn thoát");
            waitFor(() -> !gui.isDisplayable(), 3000, "Exit Yes disposes frame");
            pass("Exit No/Yes");

            // A deliberately unreachable JDBC endpoint tests error notification from the GUI.
            HangHoaRepository broken = new HangHoaRepository(
                    "jdbc:sqlserver://127.0.0.1:1;databaseName=QLHangHoa;loginTimeout=1", "", "");
            gui = onEdt(() -> {
                QuanLyHangHoaGUI frame = new QuanLyHangHoaGUI(broken);
                frame.setVisible(true);
                return frame;
            });
            respondDialog(null, true, 10000);
            onEdt(() -> field("chuKyField").setText("2"));
            respondDialog(null, true, 12000);
            require(text("trangThai").startsWith("Không thành công"), "timer DB failure in status");
            require(Integer.parseInt(field("demGiayField").getText()) < 2, "failure refresh resets counter");
            onEdt(() -> gui.dispose());
            pass("Automatic refresh failure shown to user");
        } finally {
            if (gui != null && gui.isDisplayable()) {
                onEdt(() -> gui.dispose());
            }
            for (int code : new int[] {GOOD, EXTERNAL_GOOD}) {
                try { repository.xoaHangHoa(code); } catch (Exception ignored) { }
            }
            for (LoaiHang type : repository.listLoaiHang()) {
                if (type.getTenLoaiHang().equals(TYPE)) {
                    try { repository.xoaLoaiHang(type.getMaLoai()); } catch (Exception ignored) { }
                }
            }
            if (repository.listTinhChat().contains(QUALITY)) {
                try { repository.xoaTinhChat(QUALITY); } catch (Exception ignored) { }
            }
            Files.deleteIfExists(exportPath);
            require(repository.listHangHoa().isEmpty(), "DB cleanup: no goods");
            require(repository.listLoaiHang().size() == 3, "DB cleanup: 3 types");
            require(repository.listTinhChat().size() == 3, "DB cleanup: 3 qualities");
            System.out.println("DATABASE CLEANUP PASS: 0 goods / 3 types / 3 qualities");
        }
        System.out.println("GUI E2E PASS: " + testCount + " groups");
    }

    private boolean hasType(String name) throws Exception {
        for (LoaiHang t : repository.listLoaiHang()) {
            if (t.getTenLoaiHang().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void selectType(int id) throws Exception {
        onEdt(() -> {
            JTree tree = gui.getLoaiTree();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                LoaiHang type = (LoaiHang) child.getUserObject();
                if (type.getMaLoai() == id) {
                    tree.setSelectionPath(new TreePath(child.getPath()));
                    return;
                }
            }
            throw new AssertionError("Type ID " + id + " absent in JTree");
        });
    }

    private void clickAndWait(int buttonIndex) throws Exception {
        onEdt(() -> button(buttonIndex).doClick());
        waitFor(() -> !getBoolean(gui, "busy"), 15000, "button " + buttonIndex);
        require(!text("trangThai").startsWith("Không thành công"), "button action successful: " + buttonIndex);
    }

    private void clickWithMessage(int buttonIndex, String expected) throws Exception {
        SwingUtilities.invokeLater(() -> button(buttonIndex).doClick());
        respondDialog(expected, true, 10000);
    }

    private void clickWithConfirmAndMessage(int buttonIndex, boolean yes, String confirmText)
            throws Exception {
        clickWithConfirmAndMessage(buttonIndex, yes, confirmText, null);
    }

    private void clickWithConfirmAndMessage(int buttonIndex, boolean yes, String confirmText,
                                            String followupError) throws Exception {
        SwingUtilities.invokeLater(() -> button(buttonIndex).doClick());
        respondDialog(confirmText, yes, 10000);
        if (followupError != null) {
            respondDialog(followupError, true, 15000);
        } else if (yes && buttonIndex != 7) {
            String completed = buttonIndex == 6 ? "Đã xóa hàng hóa"
                    : buttonIndex == 3 ? "Đã xóa loại hàng" : "Đã xóa tính chất";
            waitFor(() -> !getBoolean(gui, "busy") && text("trangThai").startsWith(completed),
                    15000, "confirmed button " + buttonIndex);
        }
    }

    private JDialog awaitDialog(String expected, long timeoutMillis) throws Exception {
        long end = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < end) {
            for (Window window : Window.getWindows()) {
                if (!(window instanceof JDialog) || !window.isShowing()) continue;
                JDialog dialog = (JDialog) window;
                JOptionPane pane = find(dialog, JOptionPane.class);
                if (pane == null) continue;
                String message = String.valueOf(pane.getMessage());
                if (expected == null || message.contains(expected)) return dialog;
            }
            Thread.sleep(50);
        }
        throw new AssertionError("Timed out waiting for dialog containing: " + expected);
    }

    private void respondDialog(String expected, boolean yes, long timeoutMillis) throws Exception {
        JDialog dialog = awaitDialog(expected, timeoutMillis);
        JOptionPane pane = find(dialog, JOptionPane.class);
        String message = String.valueOf(pane.getMessage());
        if (expected != null) require(message.contains(expected), "dialog text: " + expected);
        onEdt(() -> {
            pane.setValue(yes ? JOptionPane.YES_OPTION : JOptionPane.NO_OPTION);
            dialog.dispose();
        });
    }

    private JFileChooser awaitChooser(long timeoutMillis) throws Exception {
        long end = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < end) {
            for (Window window : Window.getWindows()) {
                if (window instanceof Dialog && window.isShowing()) {
                    JFileChooser chooser = find((Container) window, JFileChooser.class);
                    if (chooser != null) return chooser;
                }
            }
            Thread.sleep(50);
        }
        throw new AssertionError("Timed out waiting for JFileChooser");
    }

    private static <T extends Component> T find(Container parent, Class<T> type) {
        for (Component component : parent.getComponents()) {
            if (type.isInstance(component)) return type.cast(component);
            if (component instanceof Container) {
                T nested = find((Container) component, type);
                if (nested != null) return nested;
            }
        }
        return null;
    }

    private boolean comboContains(String item) {
        for (int i = 0; i < combo().getItemCount(); i++) {
            if (item.equals(combo().getItemAt(i))) return true;
        }
        return false;
    }

    private JButton button(int index) {
        return gui.getActionButtons().get(index);
    }

    private JTextField field(String name) {
        if ("maHangField".equals(name)) return gui.getMaHangField();
        if ("tenHangField".equals(name)) return gui.getTenHangField();
        if ("maLoaiField".equals(name)) return gui.getMaLoaiField();
        if ("tenLoaiField".equals(name)) return gui.getTenLoaiField();
        if ("tyLeField".equals(name)) return gui.getTyLeField();
        if ("tinhChatField".equals(name)) return gui.getTinhChatField();
        if ("tongGiaTriField".equals(name)) return gui.getTongGiaTriField();
        if ("trungBinhField".equals(name)) return gui.getTrungBinhField();
        if ("chuKyField".equals(name)) return gui.getChuKyField();
        if ("demGiayField".equals(name)) return gui.getDemGiayField();
        if ("thoiGianBatDau".equals(name)) return gui.getThoiGianBatDauField();
        if ("thoiGianHienTai".equals(name)) return gui.getThoiGianHienTaiField();
        throw new IllegalArgumentException("Unknown field: " + name);
    }

    private JTable table() { return gui.getHangTable(); }
    private JComboBox<String> combo() { return gui.getTinhChatCombo(); }

    private String text(String name) {
        if ("trangThai".equals(name)) return gui.getTrangThaiLabel().getText();
        return field(name).getText();
    }

    private static boolean getBoolean(QuanLyHangHoaGUI object, String fieldName) {
        if ("busy".equals(fieldName)) return object.isBusy();
        throw new IllegalArgumentException("Unknown boolean field: " + fieldName);
    }

    private static int getInt(QuanLyHangHoaGUI object, String fieldName) {
        if ("refreshSeconds".equals(fieldName)) return object.getRefreshSeconds();
        if ("lastValidRefreshSeconds".equals(fieldName)) return object.getLastValidRefreshSeconds();
        throw new IllegalArgumentException("Unknown int field: " + fieldName);
    }

    private static void waitFor(BooleanSupplier condition, long timeoutMillis, String label)
            throws Exception {
        long end = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < end) {
            if (condition.getAsBoolean()) return;
            Thread.sleep(50);
        }
        throw new AssertionError("Timed out: " + label);
    }

    private static void onEdt(Runnable operation) throws Exception {
        SwingUtilities.invokeAndWait(operation);
    }

    private static <T> T onEdt(Callable<T> operation) throws Exception {
        final Object[] result = new Object[1];
        SwingUtilities.invokeAndWait(() -> {
            try { result[0] = operation.call(); }
            catch (Exception exception) { throw new RuntimeException(exception); }
        });
        @SuppressWarnings("unchecked") T value = (T) result[0];
        return value;
    }

    private void pass(String message) {
        testCount++;
        System.out.println("PASS " + testCount + ": " + message);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
