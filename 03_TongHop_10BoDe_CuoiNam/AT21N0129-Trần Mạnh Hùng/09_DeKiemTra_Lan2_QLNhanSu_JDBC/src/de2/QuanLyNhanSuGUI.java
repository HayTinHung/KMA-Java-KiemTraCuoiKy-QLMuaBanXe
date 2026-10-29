package de2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Giao diện Quản lý nhân sự - Kiểm tra lần 2 (Đề 1).
 * Đáp ứng chính xác 100% giao diện và yêu cầu từ đề bài.
 */
public final class QuanLyNhanSuGUI extends JFrame {
    private static final String CLOCK_PATTERN = "M/d/yyyy h:mm:ss a";
    private static final Color REQUIRED = new Color(220, 20, 20);
    private static final Color INVALID = new Color(255, 235, 235);
    private static final Font APP_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final DecimalFormat NUMBER_FORMAT;

    static {
        UIManager.put("Label.font", APP_FONT);
        UIManager.put("Button.font", APP_FONT);
        UIManager.put("TextField.font", APP_FONT);
        UIManager.put("Tree.font", APP_FONT);
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        NUMBER_FORMAT = new DecimalFormat("#,##0.##", symbols);
    }

    private final NhanSuRepository repository;

    // Left controls
    private final JTextField mnsField = createNumericField("", 5);
    private final JTextField tnsField = new JTextField();
    private final JTextField luongField = createNumericField("", 12);
    private final JTextField maCapField = new JTextField();
    private final JTextField tenCapField = new JTextField();
    private final JTextField baoHiemField = new JTextField();

    // Tree Cấp & buttons
    private final DefaultMutableTreeNode capRoot = new DefaultMutableTreeNode("Cấp");
    private final JTree capTree = new JTree(capRoot);
    private final JButton btnThemCap = new JButton("Thêm");
    private final JButton btnXoaCap = new JButton("Xóa");

    // Header controls
    private final JTextField thoiGianHienTai = new JTextField();
    private final JTextField elapsedField = new JTextField("0");
    private final JTextField intervalField = createNumericField("100", 5);

    // Stats controls
    private final JTextField totalField = new JTextField("0");
    private final JTextField averageField = new JTextField("0");

    // Action buttons
    private final JButton btnNhapMoi = new JButton("Nhập mới");
    private final JButton btnSua = new JButton("Sửa");
    private final JButton btnXoa = new JButton("Xóa");
    private final JButton btnThoat = new JButton("Thoát");
    private final JButton btnLuuFile = new JButton("Lưu ra file");

    // Table
    private final EmployeeTableModel tableModel = new EmployeeTableModel();
    private final JTable table = new JTable(tableModel);

    // Status bar
    private final JLabel status = new JLabel("Đang tải dữ liệu...");

    // Timers & state
    private final Timer timer;
    private List<NhanSu> employees = new ArrayList<NhanSu>();
    private List<Cap> caps = new ArrayList<Cap>();
    private boolean busy;
    private boolean closing;
    private int elapsed = 0;
    private int refreshSeconds = 100;
    private int lastValidRefreshSeconds = 100;

    public QuanLyNhanSuGUI() {
        this(NhanSuRepository.fromEnvironment());
    }

    public QuanLyNhanSuGUI(NhanSuRepository repository) {
        if (repository == null) throw new IllegalArgumentException("Repository không được để trống.");
        this.repository = repository;

        String started = new SimpleDateFormat(CLOCK_PATTERN, Locale.US).format(new Date());
        // Thuộc tính Text của Cửa sổ thể hiện ngày giờ bắt đầu chạy đúng theo đề
        setTitle("Bắt đầu lúc: " + started);

        buildUi();
        wireEvents();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(880, 520));
        setSize(940, 560);
        setLocationRelativeTo(null);

        // Khởi động timer đếm giây
        timer = new Timer(1000, e -> onTick());
        timer.start();

        // Nạp dữ liệu lần đầu
        refreshData(false);

        // Yêu cầu a: con trỏ văn bản đặt vào ô Mã Nhân sự
        SwingUtilities.invokeLater(() -> mnsField.requestFocusInWindow());
    }

    private void buildUi() {
        JPanel rootPanel = new JPanel(new BorderLayout(6, 6));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        setContentPane(rootPanel);

        // 1. Header (Thanh tiêu đề trên cùng)
        JPanel heading = new JPanel(new BorderLayout(8, 0));
        heading.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185, 185, 185)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        // Góc trái bên trên: textbox thời gian hiện hành theo giây
        thoiGianHienTai.setText(new SimpleDateFormat(CLOCK_PATTERN, Locale.US).format(new Date()));
        thoiGianHienTai.setEditable(false);
        thoiGianHienTai.setHorizontalAlignment(SwingConstants.CENTER);
        thoiGianHienTai.setPreferredSize(new Dimension(170, 26));
        thoiGianHienTai.setBackground(Color.WHITE);
        heading.add(thoiGianHienTai, BorderLayout.WEST);

        // Giữa: QUẢN LÝ NHÂN SỰ
        JLabel title = new JLabel("QUẢN LÝ NHÂN SỰ", SwingConstants.CENTER);
        title.setForeground(new Color(25, 60, 200));
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.add(title, BorderLayout.CENTER);

        // Góc phải bên trên: 2 textbox xếp dọc
        // Textbox 1 (Enable = false) bên trên để thể hiện số giây tự động tăng lên
        // Textbox 2 bên dưới để nhập số giây cụ thể vào
        JPanel timerPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        elapsedField.setHorizontalAlignment(SwingConstants.CENTER);
        elapsedField.setEditable(false);
        elapsedField.setEnabled(false);
        elapsedField.setPreferredSize(new Dimension(65, 22));

        intervalField.setHorizontalAlignment(SwingConstants.CENTER);
        intervalField.setPreferredSize(new Dimension(65, 22));

        timerPanel.add(elapsedField);
        timerPanel.add(intervalField);
        heading.add(timerPanel, BorderLayout.EAST);

        rootPanel.add(heading, BorderLayout.NORTH);

        // 2. Nội dung chính: Split giữa panel trái và panel phải
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(350);
        split.setResizeWeight(0.35);
        split.setOneTouchExpandable(true);
        rootPanel.add(split, BorderLayout.CENTER);

        // 3. Status bar dưới cùng
        status.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rootPanel.add(status, BorderLayout.SOUTH);

        // Định dạng và bộ lọc kiểm tra dữ liệu từ bàn phím
        setupInputFilters();
    }

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel(new BorderLayout(6, 6));

        // Form thông tin nhân sự
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 4, 3, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Mã Nhân sự
        c.gridy = 0; c.gridx = 0; c.weightx = 0;
        form.add(createRequiredLabel("Mã Nhân sự:"), c);
        c.gridx = 1; c.weightx = 1;
        mnsField.setPreferredSize(new Dimension(140, 26));
        form.add(mnsField, c);

        // Tên Nhân sự
        c.gridy = 1; c.gridx = 0; c.weightx = 0;
        form.add(createRequiredLabel("Tên Nhân sự:"), c);
        c.gridx = 1; c.weightx = 1;
        tnsField.setPreferredSize(new Dimension(140, 26));
        form.add(tnsField, c);

        // Tiền lương
        c.gridy = 2; c.gridx = 0; c.weightx = 0;
        form.add(createRequiredLabel("Tiền lương:"), c);
        c.gridx = 1; c.weightx = 1;
        luongField.setPreferredSize(new Dimension(140, 26));
        form.add(luongField, c);

        // Cấp (3 ô: Mã cấp, Tên cấp, Bảo hiểm)
        c.gridy = 3; c.gridx = 0; c.weightx = 0;
        form.add(createRequiredLabel("Cấp:"), c);
        c.gridx = 1; c.weightx = 1;
        JPanel capRow = new JPanel(new BorderLayout(3, 0));
        maCapField.setPreferredSize(new Dimension(42, 26));
        maCapField.setHorizontalAlignment(SwingConstants.CENTER);
        maCapField.setEditable(false);

        tenCapField.setPreferredSize(new Dimension(110, 26));
        tenCapField.setEditable(false);

        baoHiemField.setPreferredSize(new Dimension(42, 26));
        baoHiemField.setHorizontalAlignment(SwingConstants.CENTER);
        baoHiemField.setEditable(false);

        capRow.add(maCapField, BorderLayout.WEST);
        capRow.add(tenCapField, BorderLayout.CENTER);
        capRow.add(baoHiemField, BorderLayout.EAST);
        form.add(capRow, c);

        left.add(form, BorderLayout.NORTH);

        // Khung Cấp (Cây phân cấp + nút Thêm/Xóa)
        JPanel capContainer = new JPanel(new BorderLayout(4, 4));
        capContainer.setBorder(BorderFactory.createTitledBorder("Cấp:"));

        capTree.setRootVisible(false);
        capTree.setShowsRootHandles(true);
        capTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        capTree.setCellRenderer(renderer);

        capContainer.add(new JScrollPane(capTree), BorderLayout.CENTER);

        JPanel capButtonPanel = new JPanel(new GridLayout(1, 2, 6, 0));
        capButtonPanel.add(btnThemCap);
        capButtonPanel.add(btnXoaCap);
        capContainer.add(capButtonPanel, BorderLayout.SOUTH);

        left.add(capContainer, BorderLayout.CENTER);
        return left;
    }

    private JPanel buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout(6, 6));

        // 1. Khung Thống kê
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Thống kê"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 8, 3, 8);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0; c.gridx = 0; c.weightx = 0;
        statsPanel.add(createRequiredLabel("Tổng Thù lao:"), c);

        c.gridx = 1; c.weightx = 0;
        totalField.setPreferredSize(new Dimension(120, 26));
        totalField.setEditable(false);
        totalField.setBackground(Color.WHITE);
        totalField.setHorizontalAlignment(SwingConstants.LEFT);
        statsPanel.add(totalField, c);

        c.gridx = 2; c.weightx = 0;
        c.insets = new Insets(3, 20, 3, 8);
        statsPanel.add(createRequiredLabel("Trung bình:"), c);

        c.gridx = 3; c.weightx = 0;
        c.insets = new Insets(3, 8, 3, 8);
        averageField.setPreferredSize(new Dimension(120, 26));
        averageField.setEditable(false);
        averageField.setBackground(Color.WHITE);
        averageField.setHorizontalAlignment(SwingConstants.LEFT);
        statsPanel.add(averageField, c);

        c.gridx = 4; c.weightx = 1.0;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        statsPanel.add(spacer, c);

        right.add(statsPanel, BorderLayout.NORTH);

        // 2. Hàng nút thao tác và Bảng dữ liệu chi tiết
        JPanel center = new JPanel(new BorderLayout(0, 6));

        // Hàng 5 nút bấm chuẩn theo ảnh chụp đề
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 5, 6, 0));
        buttonsPanel.add(btnNhapMoi);
        buttonsPanel.add(btnSua);
        buttonsPanel.add(btnXoa);
        buttonsPanel.add(btnThoat);
        buttonsPanel.add(btnLuuFile);

        // Trạng thái ban đầu: Sửa, Xóa, Lưu ra file bị disabled khi chưa chọn dòng
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        btnLuuFile.setEnabled(false);

        JPanel actionWrapper = new JPanel(new BorderLayout());
        actionWrapper.add(buttonsPanel, BorderLayout.WEST);
        center.add(actionWrapper, BorderLayout.NORTH);

        // Bảng dữ liệu chi tiết: 5 cột đúng theo ảnh đề
        table.setRowHeight(24);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // MNS
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(170);          // TNS
        table.getColumnModel().getColumn(3).setPreferredWidth(100);          // Cấp
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Bảo hiểm
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        center.add(new JScrollPane(table), BorderLayout.CENTER);
        right.add(center, BorderLayout.CENTER);

        return right;
    }

    private JLabel createRequiredLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(REQUIRED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private void setupInputFilters() {
        // MNS: tối đa 5 ký tự số
        setNumericFilter(mnsField, 5);
        // Tiền lương: tối đa 12 ký tự số
        setNumericFilter(luongField, 12);
        // Interval: tối đa 5 ký tự số
        setNumericFilter(intervalField, 5);

        // Highlight nếu để trống khi rời khỏi ô nhập
        for (JTextField f : new JTextField[]{mnsField, tnsField, luongField}) {
            f.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if (f.getText().trim().isEmpty()) {
                        f.setBackground(INVALID);
                    } else {
                        f.setBackground(Color.WHITE);
                    }
                }
            });
        }
    }

    private void wireEvents() {
        // Chọn Cấp trên cây -> Đưa lên ô Cấp tương ứng
        capTree.addTreeSelectionListener(e -> {
            Cap c = getSelectedCap();
            if (c != null) showCap(c);
        });

        // Chọn dòng trên bảng dữ liệu -> Đưa lên các ô tương ứng & kích hoạt nút Sửa, Xóa
        table.getSelectionModel().addListSelectionListener(this::onTableSelection);

        // Các nút thao tác
        btnNhapMoi.addActionListener(e -> onNhapMoi());
        btnSua.addActionListener(e -> onSua());
        btnXoa.addActionListener(e -> onXoa());
        btnThoat.addActionListener(e -> confirmExit());
        btnLuuFile.addActionListener(e -> onLuuRaFile());

        btnThemCap.addActionListener(e -> onThemCap());
        btnXoaCap.addActionListener(e -> onXoaCap());

        // Thay đổi chu kỳ giây tự động tải
        intervalField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateInterval(); }
            public void removeUpdate(DocumentEvent e) { validateInterval(); }
            public void changedUpdate(DocumentEvent e) { validateInterval(); }
        });

        intervalField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (refreshSeconds < 1) {
                    intervalField.setText(Integer.toString(lastValidRefreshSeconds));
                    status.setText("Chu kỳ phải lớn hơn 0; đã khôi phục giá trị hợp lệ gần nhất.");
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                timer.stop();
            }
        });
    }

    private void onTick() {
        thoiGianHienTai.setText(new SimpleDateFormat(CLOCK_PATTERN, Locale.US).format(new Date()));
        if (refreshSeconds < 1 || busy) return;

        elapsed++;
        // Khi số giây bằng với Textbox bên dưới: reset về 0 và tự động cập nhật
        if (elapsed >= refreshSeconds) {
            elapsed = 0;
            elapsedField.setText("0");
            refreshData(true);
        } else {
            elapsedField.setText(Integer.toString(elapsed));
        }
    }

    private void validateInterval() {
        try {
            int s = Integer.parseInt(intervalField.getText().trim());
            if (s < 1) throw new NumberFormatException();
            lastValidRefreshSeconds = s;
            refreshSeconds = s;
            elapsed = 0;
            elapsedField.setText("0");
            intervalField.setBackground(Color.WHITE);
        } catch (NumberFormatException ex) {
            refreshSeconds = 0;
            intervalField.setBackground(INVALID);
        }
    }

    public void refreshData(boolean notify) {
        if (busy || closing) return;
        busy = true;
        setFormEnabled(false);

        new SwingWorker<Snapshot, Void>() {
            @Override
            protected Snapshot doInBackground() throws Exception {
                return new Snapshot(repository.listNhanSu(), repository.listCap());
            }

            @Override
            protected void done() {
                busy = false;
                setFormEnabled(true);
                try {
                    Snapshot s = get();
                    employees = s.employees;
                    caps = s.caps;
                    updateDataUi();
                    status.setText("Đã tải dữ liệu từ Database Server thành công.");
                    if (notify) {
                        JOptionPane.showMessageDialog(QuanLyNhanSuGUI.this,
                                "Cập nhật dữ liệu từ Database Server đưa lên Cửa sổ thành công.",
                                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    String msg = rootMessage(ex);
                    status.setText("Không thành công: " + msg);
                    if (notify) {
                        JOptionPane.showMessageDialog(QuanLyNhanSuGUI.this,
                                "Cập nhật dữ liệu từ Database Server không thành công:\n" + msg,
                                "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    private void updateDataUi() {
        // Cập nhật bảng
        tableModel.setRows(employees);

        // Cập nhật cây Cấp
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Cấp");
        for (Cap c : caps) {
            root.add(new DefaultMutableTreeNode(c));
        }
        capTree.setModel(new DefaultTreeModel(root));
        capTree.setRootVisible(false);
        capTree.setShowsRootHandles(true);

        // Cập nhật thống kê
        NhanSuRepository.ThongKe stats = NhanSuRepository.thongKe(employees);
        totalField.setText(formatNumber(stats.getTotal()));
        averageField.setText(formatNumber(stats.getAverage()));

        // Kiểm tra chọn dòng
        int selected = table.getSelectedRow();
        boolean hasSel = selected >= 0 && selected < employees.size();
        btnSua.setEnabled(hasSel);
        btnXoa.setEnabled(hasSel);
        btnLuuFile.setEnabled(!employees.isEmpty());
    }

    private void onTableSelection(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0 && viewRow < employees.size()) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                NhanSu ns = employees.get(modelRow);
                showEmployee(ns);
                btnSua.setEnabled(true);
                btnXoa.setEnabled(true);
                btnLuuFile.setEnabled(true);
            }
        }
    }

    private void showEmployee(NhanSu ns) {
        mnsField.setText(Integer.toString(ns.getMaNhanSu()));
        mnsField.setBackground(Color.WHITE);
        tnsField.setText(ns.getTenNhanSu());
        tnsField.setBackground(Color.WHITE);
        luongField.setText(Integer.toString(ns.getTienLuong()));
        luongField.setBackground(Color.WHITE);

        Cap cap = new Cap(ns.getMaCap(), ns.getTenCap(), ns.getBaoHiem());
        showCap(cap);
        selectCapInTree(ns.getMaCap());
    }

    public void showCap(Cap cap) {
        maCapField.setText(Integer.toString(cap.getMaCap()));
        tenCapField.setText(cap.getTen());
        baoHiemField.setText(Integer.toString(cap.getBaoHiem()));
    }

    private Cap getSelectedCap() {
        TreePath path = capTree.getSelectionPath();
        if (path == null) return null;
        Object obj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        return (obj instanceof Cap) ? (Cap) obj : null;
    }

    public void selectCapInTree(int maCap) {
        DefaultTreeModel model = (DefaultTreeModel) capTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            Object obj = child.getUserObject();
            if (obj instanceof Cap && ((Cap) obj).getMaCap() == maCap) {
                capTree.setSelectionPath(new TreePath(child.getPath()));
                break;
            }
        }
    }

    private void onNhapMoi() {
        // Kiểm tra dữ liệu ràng buộc
        try {
            NhanSu ns = validateAndReadForm();
            // Lưu vào Database Server
            executeDatabaseAction("Nhập mới nhân sự thành công.", () -> repository.themNhanSu(ns));
            clearInputs();
            mnsField.requestFocusInWindow();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kiểm tra dữ liệu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onSua() {
        try {
            if (table.getSelectedRow() < 0) {
                throw new IllegalArgumentException("Hãy chọn dòng nhân sự trong danh sách để sửa.");
            }
            NhanSu ns = validateAndReadForm();
            executeDatabaseAction("Cập nhật thông tin nhân sự thành công.", () -> repository.suaNhanSu(ns));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kiểm tra dữ liệu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onXoa() {
        try {
            String mnsStr = mnsField.getText().trim();
            if (mnsStr.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn nhân sự cần xóa.");
            }
            int mns = Integer.parseInt(mnsStr);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa nhân sự mã " + mns + " không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            executeDatabaseAction("Xóa nhân sự thành công.", () -> repository.xoaNhanSu(mns));
            clearInputs();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kiểm tra dữ liệu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onThemCap() {
        JTextField txtMa = new JTextField();
        JTextField txtTen = new JTextField();
        JTextField txtBh = new JTextField();

        JPanel p = new JPanel(new GridLayout(3, 2, 4, 4));
        p.add(new JLabel("Mã cấp:"));
        p.add(txtMa);
        p.add(new JLabel("Tên cấp:"));
        p.add(txtTen);
        p.add(new JLabel("Bảo hiểm (%):"));
        p.add(txtBh);

        int opt = JOptionPane.showConfirmDialog(this, p, "Thêm cấp mới", JOptionPane.OK_CANCEL_OPTION);
        if (opt != JOptionPane.OK_OPTION) return;

        try {
            int ma = Integer.parseInt(txtMa.getText().trim());
            String ten = txtTen.getText().trim();
            int bh = Integer.parseInt(txtBh.getText().trim());
            if (ten.isEmpty()) throw new IllegalArgumentException("Tên cấp không được để trống.");
            Cap cap = new Cap(ma, ten, bh);
            executeDatabaseAction("Thêm cấp thành công.", () -> repository.themCap(cap));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Thông tin cấp không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onXoaCap() {
        Cap c = getSelectedCap();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cấp cần xóa trong khung Cấp.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa cấp '" + c.getTen() + "' (Mã " + c.getMaCap() + ")?",
                "Xác nhận xóa cấp", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        executeDatabaseAction("Xóa cấp thành công.", () -> repository.xoaCap(c.getMaCap()));
    }

    private void onLuuRaFile() {
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để lưu ra file.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("DanhSachNhanSu.txt"));
        int opt = chooser.showSaveDialog(this);
        if (opt != JFileChooser.APPROVE_OPTION) return;

        File f = chooser.getSelectedFile();
        try (PrintWriter writer = new PrintWriter(f, StandardCharsets.UTF_8.name())) {
            writer.println("=========================================================================");
            writer.println("                       DANH SÁCH NHÂN SỰ & THÙ LAO                       ");
            writer.println("=========================================================================");
            writer.printf("%-6s | %-12s | %-25s | %-12s | %-10s | %-12s%n",
                    "STT", "Mã NS", "Tên nhân sự", "Tiền lương", "Cấp", "Bảo hiểm");
            writer.println("-------------------------------------------------------------------------");
            int stt = 1;
            for (NhanSu ns : employees) {
                writer.printf("%-6d | %-12d | %-25s | %-12d | %-10s | %-10d%%%n",
                        stt++, ns.getMaNhanSu(), ns.getTenNhanSu(), ns.getTienLuong(), ns.getTenCap(), ns.getBaoHiem());
            }
            writer.println("=========================================================================");
            NhanSuRepository.ThongKe stats = NhanSuRepository.thongKe(employees);
            writer.println("Tổng Thù lao : " + formatNumber(stats.getTotal()));
            writer.println("Trung bình   : " + formatNumber(stats.getAverage()));
            writer.println("=========================================================================");
            JOptionPane.showMessageDialog(this, "Đã lưu ra file thành công:\n" + f.getAbsolutePath(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi ghi file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public NhanSu validateAndReadForm() {
        String mnsStr = mnsField.getText().trim();
        if (mnsStr.isEmpty()) {
            mnsField.requestFocusInWindow();
            throw new IllegalArgumentException("Mã Nhân sự không được để trống.");
        }
        int mns;
        try {
            mns = Integer.parseInt(mnsStr);
            if (mns <= 0 || mns > 99999) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mnsField.requestFocusInWindow();
            throw new IllegalArgumentException("Mã Nhân sự phải là số nguyên từ 1 đến 99999 (tối đa 5 chữ số).");
        }

        String tns = tnsField.getText().trim();
        if (tns.isEmpty()) {
            tnsField.requestFocusInWindow();
            throw new IllegalArgumentException("Tên Nhân sự không được để trống.");
        }

        String luongStr = luongField.getText().trim();
        if (luongStr.isEmpty()) {
            luongField.requestFocusInWindow();
            throw new IllegalArgumentException("Tiền lương không được để trống.");
        }
        int luong;
        try {
            luong = Integer.parseInt(luongStr);
            if (luong <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            luongField.requestFocusInWindow();
            throw new IllegalArgumentException("Tiền lương phải là số nguyên dương.");
        }

        String maCapStr = maCapField.getText().trim();
        if (maCapStr.isEmpty()) {
            capTree.requestFocusInWindow();
            throw new IllegalArgumentException("Vui lòng chọn Cấp tương ứng trong khung Cấp.");
        }
        int maCap = Integer.parseInt(maCapStr);
        String tenCap = tenCapField.getText().trim();
        int baoHiem = Integer.parseInt(baoHiemField.getText().trim());

        return new NhanSu(mns, tns, luong, maCap, tenCap, baoHiem);
    }

    private void clearInputs() {
        mnsField.setText("");
        mnsField.setBackground(Color.WHITE);
        tnsField.setText("");
        tnsField.setBackground(Color.WHITE);
        luongField.setText("");
        luongField.setBackground(Color.WHITE);
        maCapField.setText("");
        tenCapField.setText("");
        baoHiemField.setText("");
        capTree.clearSelection();
        table.clearSelection();
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
    }

    private void executeDatabaseAction(String successMessage, SqlTask task) {
        if (busy) return;
        busy = true;
        setFormEnabled(false);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                task.run();
                return null;
            }

            @Override
            protected void done() {
                busy = false;
                setFormEnabled(true);
                try {
                    get();
                    status.setText(successMessage);
                    refreshData(false);
                } catch (Exception ex) {
                    String msg = rootMessage(ex);
                    status.setText("Lỗi: " + msg);
                    JOptionPane.showMessageDialog(QuanLyNhanSuGUI.this, msg, "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void setFormEnabled(boolean enabled) {
        btnNhapMoi.setEnabled(enabled);
        btnThemCap.setEnabled(enabled);
        btnXoaCap.setEnabled(enabled);
        btnThoat.setEnabled(enabled);
        if (!enabled) {
            btnSua.setEnabled(false);
            btnXoa.setEnabled(false);
            btnLuuFile.setEnabled(false);
        } else {
            boolean hasSel = table.getSelectedRow() >= 0;
            btnSua.setEnabled(hasSel);
            btnXoa.setEnabled(hasSel);
            btnLuuFile.setEnabled(!employees.isEmpty());
        }
    }

    private void confirmExit() {
        int answer = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn thoát khỏi chương trình hay không?",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            closing = true;
            timer.stop();
            dispose();
            System.exit(0);
        }
    }

    private static String formatNumber(BigDecimal val) {
        if (val == null || val.compareTo(BigDecimal.ZERO) == 0) return "0";
        synchronized (NUMBER_FORMAT) {
            return NUMBER_FORMAT.format(val);
        }
    }

    private static String rootMessage(Exception ex) {
        Throwable t = ex;
        while (t.getCause() != null) t = t.getCause();
        return t.getMessage() == null ? t.toString() : t.getMessage();
    }

    private static JTextField createNumericField(String initialText, final int maxLength) {
        final JTextField field = new JTextField(initialText) {
            @Override
            public void setText(String t) {
                String val = t == null ? "" : t;
                if (!val.matches("\\d*") || val.length() > maxLength) {
                    return;
                }
                super.setText(val);
            }
        };
        setNumericFilter(field, maxLength);
        return field;
    }

    private static void setNumericFilter(final JTextField field, final int maxLength) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isISOControl(c)) {
                    return;
                }
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                int selectedLen = field.getSelectedText() == null ? 0 : field.getSelectedText().length();
                if (field.getText().length() - selectedLen >= maxLength) {
                    e.consume();
                }
            }
        });
    }

    // Các phương thức Getter chuẩn OOP phục vụ kiểm thử giao diện
    public JTextField getMnsField() { return mnsField; }
    public JTextField getTnsField() { return tnsField; }
    public JTextField getLuongField() { return luongField; }
    public JTextField getMaCapField() { return maCapField; }
    public JTextField getTenCapField() { return tenCapField; }
    public JTextField getBaoHiemField() { return baoHiemField; }
    public JTree getCapTree() { return capTree; }
    public JButton getBtnThemCap() { return btnThemCap; }
    public JButton getBtnXoaCap() { return btnXoaCap; }
    public JTextField getThoiGianHienTaiField() { return thoiGianHienTai; }
    public JTextField getElapsedField() { return elapsedField; }
    public JTextField getIntervalField() { return intervalField; }
    public JTextField getTotalField() { return totalField; }
    public JTextField getAverageField() { return averageField; }
    public JButton getBtnNhapMoi() { return btnNhapMoi; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnThoat() { return btnThoat; }
    public JButton getBtnLuuFile() { return btnLuuFile; }
    public JTable getTable() { return table; }
    public JLabel getStatusLabel() { return status; }
    public boolean isBusy() { return busy; }
    public int getRefreshSeconds() { return refreshSeconds; }
    public int getLastValidRefreshSeconds() { return lastValidRefreshSeconds; }

    private interface SqlTask {
        void run() throws Exception;
    }

    private static final class Snapshot {
        final List<NhanSu> employees;
        final List<Cap> caps;
        Snapshot(List<NhanSu> employees, List<Cap> caps) {
            this.employees = employees;
            this.caps = caps;
        }
    }

    private static final class EmployeeTableModel extends AbstractTableModel {
        private final String[] columns = {"Số thứ tự", "Mã nhân sự", "Tên nhân sự", "Cấp", "Bảo hiểm"};
        private List<NhanSu> rows = new ArrayList<NhanSu>();

        void setRows(List<NhanSu> rows) {
            this.rows = new ArrayList<NhanSu>(rows);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int col) {
            return columns[col];
        }

        @Override
        public Object getValueAt(int r, int c) {
            if (r < 0 || r >= rows.size()) return "";
            NhanSu ns = rows.get(r);
            switch (c) {
                case 0: return r + 1;
                case 1: return ns.getMaNhanSu();
                case 2: return ns.getTenNhanSu();
                case 3: return ns.getTenCap();
                case 4: return ns.getBaoHiem();
                default: return "";
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new QuanLyNhanSuGUI().setVisible(true);
        });
    }
}
