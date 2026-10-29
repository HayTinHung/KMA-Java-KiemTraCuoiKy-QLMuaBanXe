package ontap2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.plaf.FontUIResource;

/** Giao diện quản lý hàng hóa của bài Ôn tập lần 2. */
public final class QuanLyHangHoaGUI extends JFrame {
    private static final String CLOCK_PATTERN = "dd/MM/yyyy HH:mm:ss";
    private static final Color REQUIRED_COLOR = new Color(195, 30, 30);
    private static final Color INVALID_COLOR = new Color(255, 235, 235);

    private final HangHoaRepository repository;
    private final JTextField maHangField = createConstrainedField("", 12, 5, true);
    private final JTextField tenHangField = createConstrainedField("", 20, 50, false);
    private final JTextField maLoaiField = new JTextField(4);
    private final JTextField tenLoaiField = createConstrainedField("", 12, 50, false);
    private final JTextField tyLeField = createConstrainedField("", 4, 3, true);
    private final JTextField tinhChatField = createConstrainedField("", 13, 50, false);
    private final JComboBox<String> tinhChatCombo = new JComboBox<String>();
    private final JTextField tongGiaTriField = new JTextField(12);
    private final JTextField trungBinhField = new JTextField(10);
    private final JTextField chuKyField = createConstrainedField("30", 5, 5, true);
    private final JTextField demGiayField = new JTextField("0", 5);
    private final JTextField thoiGianBatDau = new JTextField();
    private final JTextField thoiGianHienTai = new JTextField();
    private final JLabel trangThai = new JLabel("Đang tải dữ liệu...");
    private final DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("Loại hàng");
    private final JTree loaiTree = new JTree(treeRoot);
    private final HangHoaTableModel tableModel = new HangHoaTableModel();
    private final JTable hangTable = new JTable(tableModel);
    private final List<JButton> actionButtons = new ArrayList<JButton>();
    private final Timer clockTimer;

    private List<HangHoa> hangHoa = new ArrayList<HangHoa>();
    private List<LoaiHang> loaiHang = new ArrayList<LoaiHang>();
    private List<String> tinhChat = new ArrayList<String>();
    private boolean updatingUi;
    private boolean busy;
    private boolean pendingAutoRefresh;
    private boolean firstLoad = true;
    private int elapsedSeconds;
    private int refreshSeconds = 30;
    private int lastValidRefreshSeconds = 30;

    public QuanLyHangHoaGUI() {
        this(HangHoaRepository.fromEnvironment());
    }

    public QuanLyHangHoaGUI(HangHoaRepository repository) {
        super("Quản lý hàng hóa");
        if (repository == null) {
            throw new IllegalArgumentException("Repository không được để trống.");
        }
        this.repository = repository;
        String startedAt = new SimpleDateFormat(CLOCK_PATTERN).format(new Date());
        setTitle("Quản lý hàng hóa — Bắt đầu lúc: " + startedAt);
        thoiGianBatDau.setText("Bắt đầu: " + startedAt);
        buildUi();
        wireEvents();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(950, 610));
        // Trên Windows có DPI scaling, kích thước Swing là đơn vị logic.
        // Chia khung màn hình cho hệ số DPI để cửa sổ mở ra không lọt ra ngoài màn hình.
        Rectangle usable = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        double scaleX = getGraphicsConfiguration().getDefaultTransform().getScaleX();
        double scaleY = getGraphicsConfiguration().getDefaultTransform().getScaleY();
        int fitWidth = (int) Math.floor(usable.width / scaleX) - 32;
        int fitHeight = (int) Math.floor(usable.height / scaleY) - 32;
        setSize(Math.min(1180, Math.max(950, fitWidth)),
                Math.min(720, Math.max(610, fitHeight)));
        setLocationRelativeTo(null);

        clockTimer = new Timer(1000, event -> onClockTick());
        clockTimer.start();
        refreshData(false);
        SwingUtilities.invokeLater(() -> maHangField.requestFocusInWindow());
    }

    private void buildUi() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setContentPane(content);

        JPanel heading = new JPanel(new BorderLayout(8, 0));
        heading.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 190)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JPanel timePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        timePanel.setPreferredSize(new Dimension(235, 42));
        for (JTextField field : new JTextField[] {thoiGianBatDau, thoiGianHienTai}) {
            field.setEditable(false);
            field.setFocusable(false);
            field.setOpaque(false);
            field.setBorder(BorderFactory.createEmptyBorder());
            field.setFont(field.getFont().deriveFont(12f));
            timePanel.add(field);
        }
        thoiGianHienTai.setText("Hiện tại: " + new SimpleDateFormat(CLOCK_PATTERN).format(new Date()));
        heading.add(timePanel, BorderLayout.WEST);
        JLabel title = new JLabel("QUẢN LÝ HÀNG HÓA", SwingConstants.CENTER);
        title.setForeground(new Color(20, 45, 190));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 25f));
        heading.add(title, BorderLayout.CENTER);

        JPanel timerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints tc = new GridBagConstraints();
        tc.insets = new Insets(1, 3, 1, 3);
        tc.gridx = 0;
        tc.gridy = 0;
        timerPanel.add(new JLabel("Đã qua (giây):"), tc);
        tc.gridx = 1;
        demGiayField.setEditable(false);
        demGiayField.setHorizontalAlignment(SwingConstants.CENTER);
        timerPanel.add(demGiayField, tc);
        tc.gridx = 0;
        tc.gridy = 1;
        timerPanel.add(new JLabel("Tự tải sau (giây):"), tc);
        tc.gridx = 1;
        chuKyField.setHorizontalAlignment(SwingConstants.CENTER);
        timerPanel.add(chuKyField, tc);
        heading.add(timerPanel, BorderLayout.EAST);
        content.add(heading, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildInputPanel(), buildDataPanel());
        split.setResizeWeight(0.38);
        split.setDividerLocation(430);
        split.setOneTouchExpandable(true);
        content.add(split, BorderLayout.CENTER);

        trangThai.setBorder(BorderFactory.createEmptyBorder(3, 4, 1, 4));
        content.add(trangThai, BorderLayout.SOUTH);

        applyInputConstraint(maHangField, 5, true);
        applyInputConstraint(tenHangField, 50, false);
        applyInputConstraint(tenLoaiField, 50, false);
        applyInputConstraint(tyLeField, 3, true);
        applyInputConstraint(tinhChatField, 50, false);
        applyInputConstraint(chuKyField, 5, true);
        maLoaiField.setEditable(false);
        tongGiaTriField.setEditable(false);
        trungBinhField.setEditable(false);
        loaiTree.setRootVisible(false);
        loaiTree.setShowsRootHandles(true);
        hangTable.setAutoCreateRowSorter(true);
        hangTable.setRowHeight(27);
        hangTable.setFillsViewportHeight(true);
        hangTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        hangTable.getTableHeader().setReorderingAllowed(false);
        hangTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        hangTable.getColumnModel().getColumn(1).setPreferredWidth(85);
        hangTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        hangTable.getColumnModel().getColumn(3).setPreferredWidth(145);
        hangTable.getColumnModel().getColumn(4).setPreferredWidth(140);
    }

    private JPanel buildInputPanel() {
        JPanel left = new JPanel(new BorderLayout(6, 9));
        left.setMinimumSize(new Dimension(350, 400));
        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 3, 5, 3);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.gridy = 0;
        addFieldRow(fields, c, "Mã Hàng hóa:", maHangField);
        c.gridy = 1;
        addFieldRow(fields, c, "Tên Hàng hóa:", tenHangField);
        c.gridy = 2;
        JLabel typeLabel = requiredLabel("Loại hàng:");
        c.gridx = 0;
        c.weightx = 0;
        fields.add(typeLabel, c);
        JPanel typeFields = new JPanel(new BorderLayout(4, 0));
        maLoaiField.setHorizontalAlignment(SwingConstants.CENTER);
        tyLeField.setHorizontalAlignment(SwingConstants.CENTER);
        maLoaiField.setPreferredSize(new Dimension(48, 27));
        tyLeField.setPreferredSize(new Dimension(48, 27));
        typeFields.add(maLoaiField, BorderLayout.WEST);
        typeFields.add(tenLoaiField, BorderLayout.CENTER);
        typeFields.add(tyLeField, BorderLayout.EAST);
        c.gridx = 1;
        c.weightx = 1;
        fields.add(typeFields, c);
        left.add(fields, BorderLayout.NORTH);

        JPanel groups = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0.42;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(0, 0, 0, 4);
        groups.add(buildQualityPanel(), gc);
        gc.gridx = 1;
        gc.weightx = 0.58;
        gc.insets = new Insets(0, 4, 0, 0);
        groups.add(buildTypePanel(), gc);
        left.add(groups, BorderLayout.CENTER);
        return left;
    }

    private JPanel buildQualityPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Tính chất"));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 5, 4, 5);
        c.gridy = 0;
        panel.add(tinhChatField, c);
        c.gridy = 1;
        panel.add(tinhChatCombo, c);
        c.gridy = 2;
        panel.add(button("Thêm", this::addQuality), c);
        c.gridy = 3;
        panel.add(button("Xóa", this::deleteQuality), c);
        c.gridy = 4;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), c);
        return panel;
    }

    private JPanel buildTypePanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Loại hàng"));
        JScrollPane scroll = new JScrollPane(loaiTree);
        scroll.setPreferredSize(new Dimension(175, 270));
        panel.add(scroll, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttons.add(button("Thêm", this::addType));
        buttons.add(button("Xóa", this::deleteType));
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildDataPanel() {
        JPanel right = new JPanel(new BorderLayout(5, 7));
        right.setMinimumSize(new Dimension(450, 400));
        JPanel top = new JPanel(new BorderLayout(4, 6));
        JPanel statistics = new JPanel(new GridBagLayout());
        statistics.setBorder(BorderFactory.createTitledBorder("Thống kê"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 5, 4, 5);
        c.gridy = 0;
        c.gridx = 0;
        statistics.add(requiredLabel("Tổng giá trị:"), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        statistics.add(tongGiaTriField, c);
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        statistics.add(requiredLabel("Trung bình:"), c);
        c.gridx = 3;
        c.weightx = 0.75;
        c.fill = GridBagConstraints.HORIZONTAL;
        statistics.add(trungBinhField, c);
        top.add(statistics, BorderLayout.NORTH);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 1));
        actions.add(button("Nhập mới", this::insertGoods));
        actions.add(button("Sửa", this::updateGoods));
        actions.add(button("Xóa", this::deleteGoods));
        actions.add(button("Thoát", this::confirmExit));
        actions.add(button("Lưu ra file", this::exportGoods));
        top.add(actions, BorderLayout.SOUTH);
        right.add(top, BorderLayout.NORTH);
        right.add(new JScrollPane(hangTable), BorderLayout.CENTER);
        return right;
    }

    private static JLabel requiredLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(REQUIRED_COLOR);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        return label;
    }

    private static void addFieldRow(JPanel parent, GridBagConstraints c, String label, Component field) {
        c.gridx = 0;
        c.weightx = 0;
        parent.add(requiredLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        parent.add(field, c);
    }

    private JButton button(String title, Runnable action) {
        JButton button = new JButton(title);
        button.addActionListener(event -> action.run());
        actionButtons.add(button);
        return button;
    }

    private void wireEvents() {
        hangTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && !updatingUi) {
                HangHoa row = selectedGoods();
                if (row != null) {
                    showGoods(row);
                }
            }
        });
        loaiTree.addTreeSelectionListener(event -> {
            if (!updatingUi) {
                LoaiHang selected = selectedType();
                if (selected != null) {
                    showType(selected);
                }
            }
        });
        tinhChatCombo.addActionListener(event -> {
            if (!updatingUi && tinhChatCombo.getSelectedItem() != null) {
                tinhChatField.setText(tinhChatCombo.getSelectedItem().toString());
            }
        });
        chuKyField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { updatePeriod(); }
            @Override public void removeUpdate(DocumentEvent event) { updatePeriod(); }
            @Override public void changedUpdate(DocumentEvent event) { updatePeriod(); }
        });
        chuKyField.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent event) {
                if (refreshSeconds < 1) {
                    chuKyField.setText(Integer.toString(lastValidRefreshSeconds));
                    trangThai.setText("Chu kỳ tải phải lớn hơn 0; đã khôi phục giá trị hợp lệ gần nhất.");
                }
            }
        });
        for (JTextField field : new JTextField[] {maHangField, tenHangField, tinhChatField}) {
            field.addFocusListener(new FocusAdapter() {
                @Override public void focusLost(FocusEvent event) {
                    field.setBackground(field.getText().trim().isEmpty()
                            ? INVALID_COLOR : UIManager.getColor("TextField.background"));
                }
            });
        }
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent event) { confirmExit(); }
            @Override public void windowClosed(WindowEvent event) { clockTimer.stop(); }
        });
    }

    private void onClockTick() {
        thoiGianHienTai.setText("Hiện tại: " + new SimpleDateFormat(CLOCK_PATTERN).format(new Date()));
        if (refreshSeconds <= 0) {
            return;
        }
        elapsedSeconds++;
        if (elapsedSeconds >= refreshSeconds) {
            elapsedSeconds = 0;
            demGiayField.setText("0");
            refreshData(true);
        } else {
            demGiayField.setText(Integer.toString(elapsedSeconds));
        }
    }

    private void updatePeriod() {
        String value = chuKyField.getText().trim();
        try {
            int seconds = Integer.parseInt(value);
            if (seconds < 1) {
                throw new NumberFormatException();
            }
            lastValidRefreshSeconds = seconds;
            refreshSeconds = seconds;
            elapsedSeconds = 0;
            demGiayField.setText("0");
            chuKyField.setBackground(UIManager.getColor("TextField.background"));
            chuKyField.setToolTipText("Số giây giữa hai lần tải dữ liệu (ít nhất 1).");
        } catch (NumberFormatException exception) {
            refreshSeconds = 0;
            chuKyField.setBackground(INVALID_COLOR);
            chuKyField.setToolTipText("Nhập số giây nguyên lớn hơn 0 để bật tự động tải.");
        }
    }

    private void refreshData(boolean notify) {
        if (busy) {
            if (notify) {
                pendingAutoRefresh = true;
            }
            return;
        }
        Integer code = selectedGoodsCode();
        Integer type = selectedTypeId();
        executeTask(() -> new TaskResult(readSnapshot(), code, type),
                notify ? "Đã cập nhật dữ liệu từ Database Server." : "Đã tải dữ liệu từ Database Server.",
                notify, notify);
    }

    private Snapshot readSnapshot() throws Exception {
        List<HangHoa> goods = repository.listHangHoa();
        List<LoaiHang> types = repository.listLoaiHang();
        List<String> qualities = repository.listTinhChat();
        HangHoaRepository.ThongKe statistics = repository.getThongKe();
        return new Snapshot(goods, types, qualities, statistics);
    }

    private void executeTask(Callable<TaskResult> work, String successMessage,
                             boolean successDialog, boolean preserveDraft) {
        if (busy) {
            trangThai.setText("Đang xử lý dữ liệu, vui lòng chờ...");
            return;
        }
        busy = true;
        for (JButton button : actionButtons) {
            button.setEnabled(false);
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        trangThai.setText("Đang làm việc với Database Server...");
        new SwingWorker<TaskResult, Void>() {
            @Override protected TaskResult doInBackground() throws Exception {
                return work.call();
            }

            @Override protected void done() {
                busy = false;
                for (JButton button : actionButtons) {
                    button.setEnabled(true);
                }
                setCursor(Cursor.getDefaultCursor());
                try {
                    TaskResult result = get();
                    if (result.snapshot != null) {
                        applySnapshot(result.snapshot, result.goodsCode,
                                result.typeCode, preserveDraft);
                    }
                    trangThai.setText(successMessage);
                    if (successDialog) {
                        JOptionPane.showMessageDialog(QuanLyHangHoaGUI.this,
                                successMessage, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showFailure("Thao tác bị gián đoạn.");
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                    showFailure(cause.getMessage() == null ? cause.toString() : cause.getMessage());
                } finally {
                    if (pendingAutoRefresh && isDisplayable()) {
                        pendingAutoRefresh = false;
                        SwingUtilities.invokeLater(() -> refreshData(true));
                    }
                }
            }
        }.execute();
    }

    private void applySnapshot(Snapshot snapshot, Integer wantedGoods,
                               Integer wantedType, boolean preserveDraft) {
        String oldCode = maHangField.getText();
        String oldName = tenHangField.getText();
        String oldQuality = tinhChatField.getText();
        String oldTypeName = tenLoaiField.getText();
        String oldRate = tyLeField.getText();
        String oldSelectedQuality = (String) tinhChatCombo.getSelectedItem();
        LoaiHang oldSelectedType = selectedType();
        Integer oldGoods = selectedGoodsCode();
        Integer oldType = selectedTypeId();
        hangHoa = snapshot.goods;
        loaiHang = snapshot.types;
        tinhChat = snapshot.qualities;

        updatingUi = true;
        try {
            tableModel.fireTableDataChanged();
            tinhChatCombo.removeAllItems();
            for (String quality : tinhChat) {
                tinhChatCombo.addItem(quality);
            }
            treeRoot.removeAllChildren();
            for (LoaiHang type : loaiHang) {
                treeRoot.add(new DefaultMutableTreeNode(type));
            }
            ((DefaultTreeModel) loaiTree.getModel()).reload();
            Integer typeCode = wantedType != null ? wantedType : oldType;
            selectTypeInTree(typeCode);
            Integer goodsCode = wantedGoods != null ? wantedGoods : oldGoods;
            int modelRow = findGoodsRow(goodsCode);
            if (modelRow < 0 && firstLoad && !hangHoa.isEmpty()) {
                modelRow = 0;
            }
            if (modelRow >= 0) {
                int viewRow = hangTable.convertRowIndexToView(modelRow);
                hangTable.setRowSelectionInterval(viewRow, viewRow);
                hangTable.scrollRectToVisible(hangTable.getCellRect(viewRow, 0, true));
            } else {
                hangTable.clearSelection();
            }
            tongGiaTriField.setText(formatAmount(snapshot.statistics.getTongGiaTri()));
            trungBinhField.setText(formatAmount(snapshot.statistics.getTrungBinh()));
        } finally {
            updatingUi = false;
        }

        if (preserveDraft) {
            boolean qualityWasRemoved = oldSelectedQuality != null
                    && !tinhChat.contains(oldSelectedQuality);
            if (qualityWasRemoved && oldQuality.trim().equals(oldSelectedQuality)) {
                oldQuality = "";
            }
            boolean typeWasRemoved = oldSelectedType != null
                    && findType(oldSelectedType.getMaLoai()) == null;
            if (typeWasRemoved
                    && oldTypeName.trim().equals(oldSelectedType.getTenLoaiHang())
                    && oldRate.trim().equals(Integer.toString(oldSelectedType.getTyLe()))) {
                oldTypeName = "";
                oldRate = "";
            }
            maHangField.setText(oldCode);
            tenHangField.setText(oldName);
            tinhChatField.setText(oldQuality);
            tenLoaiField.setText(oldTypeName);
            tyLeField.setText(oldRate);
            selectQualityInCombo(oldQuality);
            LoaiHang currentType = selectedType();
            maLoaiField.setText(currentType == null ? "" : Integer.toString(currentType.getMaLoai()));
        } else {
            HangHoa selected = selectedGoods();
            if (selected != null) {
                showGoods(selected);
            } else {
                clearGoodsFields();
                LoaiHang selectedType = selectedType();
                if (selectedType != null) {
                    showType(selectedType);
                }
            }
        }
        firstLoad = false;
    }

    private static String formatAmount(BigDecimal amount) {
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.2f", amount);
    }

    private void showGoods(HangHoa row) {
        maHangField.setText(Integer.toString(row.getMaHangHoa()));
        tenHangField.setText(row.getTenHangHoa());
        tinhChatField.setText(row.getTinhChat());
        selectQualityInCombo(row.getTinhChat());
        selectTypeInTree(row.getMaLoai());
        LoaiHang selected = selectedType();
        if (selected != null) {
            showType(selected);
        }
    }

    private void clearGoodsFields() {
        maHangField.setText("");
        tenHangField.setText("");
        tinhChatField.setText("");
        maLoaiField.setText("");
        tenLoaiField.setText("");
        tyLeField.setText("");
    }

    private void showType(LoaiHang type) {
        maLoaiField.setText(Integer.toString(type.getMaLoai()));
        tenLoaiField.setText(type.getTenLoaiHang());
        tyLeField.setText(Integer.toString(type.getTyLe()));
    }

    private void selectTypeInTree(Integer code) {
        if (code == null) {
            loaiTree.clearSelection();
            return;
        }
        for (int i = 0; i < treeRoot.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) treeRoot.getChildAt(i);
            LoaiHang type = (LoaiHang) child.getUserObject();
            if (type.getMaLoai() == code) {
                TreePath path = new TreePath(child.getPath());
                loaiTree.setSelectionPath(path);
                loaiTree.scrollPathToVisible(path);
                return;
            }
        }
        loaiTree.clearSelection();
    }

    private void selectQualityInCombo(String quality) {
        updatingUi = true;
        try {
            tinhChatCombo.setSelectedItem(tinhChat.contains(quality) ? quality : null);
        } finally {
            updatingUi = false;
        }
    }

    private int findGoodsRow(Integer code) {
        if (code == null) {
            return -1;
        }
        for (int i = 0; i < hangHoa.size(); i++) {
            if (hangHoa.get(i).getMaHangHoa() == code) {
                return i;
            }
        }
        return -1;
    }

    private LoaiHang findType(int code) {
        for (LoaiHang type : loaiHang) {
            if (type.getMaLoai() == code) {
                return type;
            }
        }
        return null;
    }

    private HangHoa selectedGoods() {
        int viewRow = hangTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = hangTable.convertRowIndexToModel(viewRow);
        return modelRow >= 0 && modelRow < hangHoa.size() ? hangHoa.get(modelRow) : null;
    }

    private Integer selectedGoodsCode() {
        HangHoa selected = selectedGoods();
        return selected == null ? null : selected.getMaHangHoa();
    }

    private LoaiHang selectedType() {
        TreePath path = loaiTree.getSelectionPath();
        if (path == null) {
            return null;
        }
        Object object = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        return object instanceof LoaiHang ? (LoaiHang) object : null;
    }

    private Integer selectedTypeId() {
        LoaiHang selected = selectedType();
        return selected == null ? null : selected.getMaLoai();
    }

    private HangHoa validatedGoods() {
        int code = validatedCode();
        String name = requiredText(tenHangField, "Tên hàng hóa");
        LoaiHang selected = selectedType();
        if (selected == null) {
            failInput(tenLoaiField, "Hãy chọn một loại hàng trên cây Loại hàng.");
        }
        if (!tenLoaiField.getText().trim().equals(selected.getTenLoaiHang())
                || !tyLeField.getText().trim().equals(Integer.toString(selected.getTyLe()))) {
            failInput(tenLoaiField,
                    "Thông tin loại hàng đã thay đổi. Hãy chọn lại trên cây hoặc thêm loại hàng mới.");
        }
        String quality = requiredText(tinhChatField, "Tính chất");
        if (!tinhChat.contains(quality)) {
            failInput(tinhChatField, "Tính chất chưa có trong danh mục. Hãy thêm tính chất trước.");
        }
        return new HangHoa(code, name, selected.getMaLoai(), quality);
    }

    private int validatedCode() {
        String value = maHangField.getText().trim();
        if (!value.matches("[0-9]{5}")) {
            failInput(maHangField, "Mã hàng hóa phải gồm đúng 5 chữ số.");
        }
        int code = Integer.parseInt(value);
        if (code < 10000) {
            failInput(maHangField, "Mã hàng hóa phải là số nguyên gồm đúng 5 chữ số.");
        }
        return code;
    }

    private String requiredText(JTextField field, String label) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            failInput(field, label + " không được để trống.");
        }
        if (value.length() > 50) {
            failInput(field, label + " không được quá 50 ký tự.");
        }
        return value;
    }

    private void failInput(JTextField field, String message) {
        field.setBackground(INVALID_COLOR);
        JOptionPane.showMessageDialog(this, message, "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        field.requestFocusInWindow();
        field.selectAll();
        throw new InvalidInputException(message);
    }

    private void insertGoods() {
        try {
            HangHoa row = validatedGoods();
            executeTask(() -> {
                repository.themHangHoa(row);
                return new TaskResult(readSnapshot(), row.getMaHangHoa(), row.getMaLoai());
            }, "Đã thêm hàng hóa " + row.getMaHangHoa() + ".", false, false);
        } catch (InvalidInputException ignored) {
            // Thông báo và focus đã được xử lý ở failInput.
        }
    }

    private void updateGoods() {
        try {
            HangHoa row = validatedGoods();
            HangHoa selected = selectedGoods();
            if (selected != null && selected.getMaHangHoa() != row.getMaHangHoa()) {
                failInput(maHangField, "Mã hàng đã chọn không thể đổi khi sửa. Hãy dùng Nhập mới cho mã khác.");
            }
            executeTask(() -> {
                repository.suaHangHoa(row);
                return new TaskResult(readSnapshot(), row.getMaHangHoa(), row.getMaLoai());
            }, "Đã sửa hàng hóa " + row.getMaHangHoa() + ".", false, false);
        } catch (InvalidInputException ignored) {
            // Thông báo và focus đã được xử lý ở failInput.
        }
    }

    private void deleteGoods() {
        try {
            int code = validatedCode();
            if (!confirm("Xóa hàng hóa " + code + " khỏi cơ sở dữ liệu?")) {
                return;
            }
            Integer typeCode = selectedTypeId();
            executeTask(() -> {
                repository.xoaHangHoa(code);
                return new TaskResult(readSnapshot(), null, typeCode);
            }, "Đã xóa hàng hóa " + code + ".", false, false);
        } catch (InvalidInputException ignored) {
            // Thông báo và focus đã được xử lý ở failInput.
        }
    }

    private void addQuality() {
        try {
            String quality = requiredText(tinhChatField, "Tính chất");
            Integer goodsCode = selectedGoodsCode();
            Integer typeCode = selectedTypeId();
            executeTask(() -> {
                repository.themTinhChat(quality);
                return new TaskResult(readSnapshot(), goodsCode, typeCode);
            }, "Đã thêm tính chất: " + quality + ".", false, true);
        } catch (InvalidInputException ignored) {
            // Thông báo và focus đã được xử lý ở failInput.
        }
    }

    private void deleteQuality() {
        Object selected = tinhChatCombo.getSelectedItem();
        if (selected == null) {
            showFailure("Hãy chọn tính chất cần xóa trong danh sách.");
            return;
        }
        String quality = selected.toString();
        if (!confirm("Xóa tính chất “" + quality + "”?")) {
            return;
        }
        Integer goodsCode = selectedGoodsCode();
        Integer typeCode = selectedTypeId();
        executeTask(() -> {
            repository.xoaTinhChat(quality);
            return new TaskResult(readSnapshot(), goodsCode, typeCode);
        }, "Đã xóa tính chất: " + quality + ".", false, true);
    }

    private void addType() {
        try {
            String name = requiredText(tenLoaiField, "Tên loại hàng");
            String rateText = tyLeField.getText().trim();
            int rate;
            try {
                rate = Integer.parseInt(rateText);
            } catch (NumberFormatException exception) {
                failInput(tyLeField, "Tỷ lệ phải là số nguyên từ 0 đến 100.");
                return;
            }
            if (rate < 0 || rate > 100) {
                failInput(tyLeField, "Tỷ lệ phải từ 0 đến 100%.");
            }
            final int validatedRate = rate;
            Integer goodsCode = selectedGoodsCode();
            executeTask(() -> {
                LoaiHang created = repository.themLoaiHang(name, validatedRate);
                return new TaskResult(readSnapshot(), goodsCode, created.getMaLoai());
            }, "Đã thêm loại hàng: " + name + ".", false, true);
        } catch (InvalidInputException ignored) {
            // Thông báo và focus đã được xử lý ở failInput.
        }
    }

    private void deleteType() {
        LoaiHang selected = selectedType();
        if (selected == null) {
            showFailure("Hãy chọn loại hàng cần xóa trên cây Loại hàng.");
            return;
        }
        if (!confirm("Xóa loại hàng “" + selected.getTenLoaiHang() + "”?")) {
            return;
        }
        Integer goodsCode = selectedGoodsCode();
        executeTask(() -> {
            repository.xoaLoaiHang(selected.getMaLoai());
            return new TaskResult(readSnapshot(), goodsCode, null);
        }, "Đã xóa loại hàng: " + selected.getTenLoaiHang() + ".", false, false);
    }

    private void exportGoods() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu bảng Hàng hóa ra tệp TXT (UTF-8)");
        chooser.setSelectedFile(new File("HangHoa.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".txt")) {
            file = new File(file.getParentFile(), file.getName() + ".txt");
        }
        if (file.exists() && !confirm("Tệp đã tồn tại. Ghi đè “" + file.getName() + "”?")) {
            return;
        }
        Path path = file.toPath();
        executeTask(() -> {
            repository.xuatHangHoa(path);
            return new TaskResult(null, null, null);
        }, "Đã lưu bảng Hàng hóa vào " + path.toAbsolutePath() + ".", true, false);
    }

    private boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void confirmExit() {
        if (confirm("Bạn có chắc muốn thoát chương trình?")) {
            clockTimer.stop();
            dispose();
        }
    }

    private void showFailure(String message) {
        trangThai.setText("Không thành công: " + message);
        JOptionPane.showMessageDialog(this, message, "Không thành công", JOptionPane.ERROR_MESSAGE);
    }

    private final class HangHoaTableModel extends AbstractTableModel {
        private final String[] columns = {"STT", "Mã hàng", "Tên hàng", "Loại hàng", "Tính chất"};

        @Override public int getRowCount() { return hangHoa.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }
        @Override public Class<?> getColumnClass(int column) {
            return column == 0 || column == 1 ? Integer.class : String.class;
        }
        @Override public boolean isCellEditable(int row, int column) { return false; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            HangHoa row = hangHoa.get(rowIndex);
            switch (columnIndex) {
                case 0: return rowIndex + 1;
                case 1: return row.getMaHangHoa();
                case 2: return row.getTenHangHoa();
                case 3:
                    for (LoaiHang type : loaiHang) {
                        if (type.getMaLoai() == row.getMaLoai()) {
                            return type.getTenLoaiHang();
                        }
                    }
                    return Integer.toString(row.getMaLoai());
                case 4: return row.getTinhChat();
                default: return "";
            }
        }
    }

    private static final class Snapshot {
        final List<HangHoa> goods;
        final List<LoaiHang> types;
        final List<String> qualities;
        final HangHoaRepository.ThongKe statistics;

        Snapshot(List<HangHoa> goods, List<LoaiHang> types, List<String> qualities,
                 HangHoaRepository.ThongKe statistics) {
            this.goods = goods;
            this.types = types;
            this.qualities = qualities;
            this.statistics = statistics;
        }
    }

    private static final class TaskResult {
        final Snapshot snapshot;
        final Integer goodsCode;
        final Integer typeCode;

        TaskResult(Snapshot snapshot, Integer goodsCode, Integer typeCode) {
            this.snapshot = snapshot;
            this.goodsCode = goodsCode;
            this.typeCode = typeCode;
        }
    }

    private static final class InvalidInputException extends RuntimeException {
        InvalidInputException(String message) { super(message); }
    }

    private static JTextField createConstrainedField(String initialText, int columns, final int limit, final boolean digitsOnly) {
        final JTextField field = new JTextField(initialText, columns) {
            @Override
            public void setText(String t) {
                String val = t == null ? "" : t;
                if (val.length() > limit) {
                    return;
                }
                if (digitsOnly && !val.matches("[0-9]*")) {
                    return;
                }
                super.setText(val);
            }
        };
        applyInputConstraint(field, limit, digitsOnly);
        return field;
    }

    private static void applyInputConstraint(final JTextField field, final int limit, final boolean digitsOnly) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isISOControl(c)) {
                    return;
                }
                if (digitsOnly && !Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                int selectedLen = field.getSelectedText() == null ? 0 : field.getSelectedText().length();
                if (field.getText().length() - selectedLen >= limit) {
                    e.consume();
                }
            }
        });
    }

    // Các phương thức Getter chuẩn OOP phục vụ kiểm thử giao diện
    public JTextField getMaHangField() { return maHangField; }
    public JTextField getTenHangField() { return tenHangField; }
    public JTextField getMaLoaiField() { return maLoaiField; }
    public JTextField getTenLoaiField() { return tenLoaiField; }
    public JTextField getTyLeField() { return tyLeField; }
    public JTextField getTinhChatField() { return tinhChatField; }
    public JComboBox<String> getTinhChatCombo() { return tinhChatCombo; }
    public JTextField getTongGiaTriField() { return tongGiaTriField; }
    public JTextField getTrungBinhField() { return trungBinhField; }
    public JTextField getChuKyField() { return chuKyField; }
    public JTextField getDemGiayField() { return demGiayField; }
    public JTextField getThoiGianBatDauField() { return thoiGianBatDau; }
    public JTextField getThoiGianHienTaiField() { return thoiGianHienTai; }
    public JLabel getTrangThaiLabel() { return trangThai; }
    public JTree getLoaiTree() { return loaiTree; }
    public JTable getHangTable() { return hangTable; }
    public List<JButton> getActionButtons() { return actionButtons; }
    public boolean isBusy() { return busy; }
    public int getRefreshSeconds() { return refreshSeconds; }
    public int getLastValidRefreshSeconds() { return lastValidRefreshSeconds; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Giao diện mặc định của Swing vẫn sử dụng được.
            }
            FontUIResource baseFont = new FontUIResource("Segoe UI", Font.PLAIN, 14);
            for (String key : new String[] {"Label.font", "TextField.font", "Button.font",
                    "ComboBox.font", "Tree.font", "Table.font", "TableHeader.font",
                    "TitledBorder.font"}) {
                UIManager.put(key, baseFont);
            }
            new QuanLyHangHoaGUI().setVisible(true);
        });
    }
}
