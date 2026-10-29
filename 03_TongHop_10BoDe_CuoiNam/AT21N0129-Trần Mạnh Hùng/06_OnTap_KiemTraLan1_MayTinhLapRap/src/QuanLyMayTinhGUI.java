import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Giao diện chương trình Máy Tính Lắp Ráp – Đề Ôn Tập Lần 1.
 *
 * Thiết kế bám sát 100% hình mẫu trong đề bài on lan 1.pdf và đáp ứng trọn vẹn
 * toàn bộ yêu cầu Câu 1 (2.5 đ), Câu 2 (1.0 đ) và Câu 3 từ a đến h (6.5 đ).
 */
@SuppressWarnings("serial")
public final class QuanLyMayTinhGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    public static boolean isTestMode = false;

    final List<MayTinh> dsmt = new ArrayList<>();
    private int currentIndex = -1;
    private boolean isProgrammaticUpdate = false;

    // 7 nhãn quan trọng trên cửa sổ (hiển thị màu đỏ mặc định và đổi màu theo radio Màu sắc)
    JLabel lblMaMay;
    JLabel lblTenMay;
    JLabel lblDonGia;
    JLabel lblSoLuong;
    JLabel lblGiaTri;
    JLabel lblTongGiaTri;
    JLabel lblTongLuong;
    JLabel lblViTri;
    List<JLabel> nhanQuanTrong;

    // Các ô văn bản nhập liệu và hiển thị kết quả
    JTextField txtMaMay;
    JTextField txtTenMay;
    JTextField txtDonGia;
    JTextField txtSoLuong;
    JTextField txtGiaTri;
    JTextField txtTongGiaTri;
    JTextField txtTongLuong;

    // Lựa chọn Loại máy và Phụ kiện
    JRadioButton radLapRap;
    JRadioButton radNhapKhau;
    private ButtonGroup bgLoaiMay;
    JCheckBox chkVGA;
    JCheckBox chkDVD;
    JCheckBox chkLoa;

    // Nhóm nút chức năng và điều hướng
    JButton btnNhapMay;
    JButton btnNhapMoi;
    JButton btnXoa;
    JButton btnThoat;
    JButton btnVeDau;
    JButton btnVeTruoc;
    JButton btnVeSau;
    JButton btnVeCuoi;

    // Lựa chọn Màu sắc
    JRadioButton radDo;
    JRadioButton radXanh;
    JRadioButton radTim;
    private ButtonGroup bgMauSac;

    public QuanLyMayTinhGUI() {
        super("Giao diện");
        khoiTaoGiaoDien();
        khoiTaoSuKien();
        khoiTaoTrangThaiBanDau();
    }

    /**
     * Câu 1 (2.5 đ): Khởi tạo cấu trúc giao diện chính của cửa sổ.
     */
    private void khoiTaoGiaoDien() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                xuLyThoat();
            }
        });

        Font fontTieuDe = new Font("Times New Roman", Font.BOLD, 24);
        Font fontNhan = new Font("Times New Roman", Font.BOLD, 17);
        Font fontThuong = new Font("Segoe UI", Font.PLAIN, 14);
        Color nen = new Color(242, 242, 242);

        JPanel mainPanel = new JPanel(new BorderLayout(6, 6));
        mainPanel.setBorder(new EmptyBorder(6, 8, 8, 8));
        mainPanel.setBackground(nen);

        // 1. Khung tiêu đề trên cùng: MÁY TÍNH LẮP RÁP
        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setPreferredSize(new Dimension(740, 56));
        pnlTitle.setBackground(nen);
        pnlTitle.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JLabel lblTitle = new JLabel("MÁY TÍNH LẮP RÁP", SwingConstants.CENTER);
        lblTitle.setFont(fontTieuDe);
        lblTitle.setForeground(new Color(0, 0, 230));
        pnlTitle.add(lblTitle, BorderLayout.CENTER);
        mainPanel.add(pnlTitle, BorderLayout.NORTH);

        // 2. Khu vực trung tâm chia 2 cột đều nhau
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 6, 0));
        pnlCenter.setBackground(nen);
        pnlCenter.add(taoPanelBenTrai(fontNhan, fontThuong, nen));
        pnlCenter.add(taoPanelBenPhai(fontNhan, fontThuong, nen));
        mainPanel.add(pnlCenter, BorderLayout.CENTER);

        // Danh sách 7 nhãn quan trọng trên cửa sổ
        nhanQuanTrong = new ArrayList<>();
        nhanQuanTrong.add(lblMaMay);
        nhanQuanTrong.add(lblTenMay);
        nhanQuanTrong.add(lblDonGia);
        nhanQuanTrong.add(lblSoLuong);
        nhanQuanTrong.add(lblGiaTri);
        nhanQuanTrong.add(lblTongGiaTri);
        nhanQuanTrong.add(lblTongLuong);
        doiMauQuanTrong(Color.RED);

        setContentPane(mainPanel);
        setSize(760, 470);
        setMinimumSize(new Dimension(740, 450));
        setLocationRelativeTo(null);
    }

    /**
     * Tạo cột bên trái gồm 4 ô nhập thông tin máy tính, khung Loại máy và khung Phụ kiện.
     */
    private JPanel taoPanelBenTrai(Font fontNhan, Font fontThuong, Color nen) {
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.setBackground(nen);
        pnlLeft.setBorder(new CompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                new EmptyBorder(12, 12, 12, 12)));

        JPanel pnlInputs = new JPanel(new GridBagLayout());
        pnlInputs.setBackground(nen);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblMaMay = new JLabel("Mã máy:");
        lblTenMay = new JLabel("Tên máy:");
        lblDonGia = new JLabel("Đơn giá:");
        lblSoLuong = new JLabel("Số lượng:");

        lblMaMay.setFont(fontNhan);
        lblTenMay.setFont(fontNhan);
        lblDonGia.setFont(fontNhan);
        lblSoLuong.setFont(fontNhan);

        txtMaMay = new JTextField();
        txtTenMay = new JTextField();
        txtDonGia = new JTextField();
        txtSoLuong = new JTextField();

        txtMaMay.setFont(fontThuong);
        txtTenMay.setFont(fontThuong);
        txtDonGia.setFont(fontThuong);
        txtSoLuong.setFont(fontThuong);

        themDongNhap(pnlInputs, gbc, 0, lblMaMay, txtMaMay);
        themDongNhap(pnlInputs, gbc, 1, lblTenMay, txtTenMay);
        themDongNhap(pnlInputs, gbc, 2, lblDonGia, txtDonGia);
        themDongNhap(pnlInputs, gbc, 3, lblSoLuong, txtSoLuong);
        pnlLeft.add(pnlInputs, BorderLayout.NORTH);

        JPanel pnlLuaChon = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlLuaChon.setBackground(nen);
        pnlLuaChon.add(taoPanelLoaiMay(fontThuong, nen));
        pnlLuaChon.add(taoPanelPhuKien(fontThuong, nen));
        pnlLeft.add(pnlLuaChon, BorderLayout.CENTER);

        return pnlLeft;
    }

    /**
     * Tạo khung chọn Loại máy (Lắp ráp 110%, Nhập khẩu 120%).
     */
    private JPanel taoPanelLoaiMay(Font fontThuong, Color nen) {
        JPanel pnlLoai = new JPanel();
        pnlLoai.setLayout(new BoxLayout(pnlLoai, BoxLayout.Y_AXIS));
        pnlLoai.setBackground(nen);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Loại máy:");
        border.setTitleFont(new Font("Segoe UI", Font.PLAIN, 15));
        pnlLoai.setBorder(new CompoundBorder(border, new EmptyBorder(8, 8, 8, 8)));

        radLapRap = new JRadioButton("Lắp ráp", true);
        radNhapKhau = new JRadioButton("Nhập khẩu");
        radLapRap.setFont(fontThuong);
        radNhapKhau.setFont(fontThuong);
        radLapRap.setBackground(nen);
        radNhapKhau.setBackground(nen);

        bgLoaiMay = new ButtonGroup();
        bgLoaiMay.add(radLapRap);
        bgLoaiMay.add(radNhapKhau);

        pnlLoai.add(radLapRap);
        pnlLoai.add(Box.createVerticalStrut(12));
        pnlLoai.add(radNhapKhau);
        return pnlLoai;
    }

    /**
     * Tạo khung chọn Phụ kiện (VGA 2 GB 30%, DVD 5%, LOA 10%).
     */
    private JPanel taoPanelPhuKien(Font fontThuong, Color nen) {
        JPanel pnlPK = new JPanel();
        pnlPK.setLayout(new BoxLayout(pnlPK, BoxLayout.Y_AXIS));
        pnlPK.setBackground(nen);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Phụ kiện");
        border.setTitleFont(new Font("Segoe UI", Font.PLAIN, 15));
        pnlPK.setBorder(new CompoundBorder(border, new EmptyBorder(6, 8, 6, 8)));

        chkVGA = new JCheckBox("VGA 2 GB");
        chkDVD = new JCheckBox("DVD");
        chkLoa = new JCheckBox("LOA");

        chkVGA.setFont(fontThuong);
        chkDVD.setFont(fontThuong);
        chkLoa.setFont(fontThuong);
        chkVGA.setBackground(nen);
        chkDVD.setBackground(nen);
        chkLoa.setBackground(nen);

        pnlPK.add(chkVGA);
        pnlPK.add(Box.createVerticalStrut(10));
        pnlPK.add(chkDVD);
        pnlPK.add(Box.createVerticalStrut(10));
        pnlPK.add(chkLoa);
        return pnlPK;
    }

    /**
     * Tạo cột bên phải gồm nhóm nút Nhập, Giá trị, Thống kê, Điều hướng, Màu sắc và Xóa/Thoát.
     */
    private JPanel taoPanelBenPhai(Font fontNhan, Font fontThuong, Color nen) {
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBackground(nen);
        pnlRight.setBorder(new CompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                new EmptyBorder(8, 10, 8, 10)));

        // Hàng nút trên cùng: [Nhập máy] [Nhập mới]
        JPanel pnlTopBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        pnlTopBtn.setBackground(nen);
        btnNhapMay = new JButton("Nhập máy");
        btnNhapMoi = new JButton("Nhập mới");
        Font fontBtn = new Font("Segoe UI", Font.PLAIN, 12);
        btnNhapMay.setFont(fontBtn);
        btnNhapMoi.setFont(fontBtn);
        btnNhapMay.setPreferredSize(new Dimension(92, 28));
        btnNhapMoi.setPreferredSize(new Dimension(92, 28));
        pnlTopBtn.add(btnNhapMay);
        pnlTopBtn.add(btnNhapMoi);
        pnlRight.add(pnlTopBtn);
        pnlRight.add(Box.createVerticalStrut(4));

        // Khối Giá trị & Thống kê
        pnlRight.add(taoPanelGiaTriVaThongKe(fontNhan, fontThuong, nen));
        pnlRight.add(Box.createVerticalStrut(6));

        // Thanh điều hướng: [Về đầu] [Về trước] [0/0] [Về sau] [Về cuối]
        pnlRight.add(taoPanelDieuHuong(nen));
        pnlRight.add(Box.createVerticalStrut(6));

        // Hàng dưới cùng: Màu sắc & Nút Xóa/Thoát
        pnlRight.add(taoPanelMauVaThoat( nen));
        return pnlRight;
    }

    /**
     * Tạo khu vực hiển thị Giá trị máy hiện tại và khung Thống kê (Tổng giá trị, Tổng lượng).
     */
    private JPanel taoPanelGiaTriVaThongKe(Font fontNhan, Font fontThuong, Color nen) {
        JPanel pnlWrapper = new JPanel(new BorderLayout(0, 6));
        pnlWrapper.setBackground(nen);

        // Dòng Giá trị
        JPanel pnlGiaTri = new JPanel(new GridBagLayout());
        pnlGiaTri.setBackground(nen);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblGiaTri = new JLabel("Giá trị:");
        lblGiaTri.setFont(fontNhan);
        txtGiaTri = new JTextField();
        txtGiaTri.setFont(fontThuong);
        txtGiaTri.setEditable(false);
        txtGiaTri.setBackground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.28;
        pnlGiaTri.add(lblGiaTri, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.72;
        pnlGiaTri.add(txtGiaTri, gbc);
        pnlWrapper.add(pnlGiaTri, BorderLayout.NORTH);

        // Khung Thống kê
        JPanel pnlThongKe = new JPanel(new GridBagLayout());
        pnlThongKe.setBackground(nen);
        TitledBorder borderTK = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Thống kê");
        borderTK.setTitleFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlThongKe.setBorder(new CompoundBorder(borderTK, new EmptyBorder(4, 6, 6, 6)));

        lblTongGiaTri = new JLabel("Tổng giá trị:");
        lblTongLuong = new JLabel("Tổng lượng:");
        lblTongGiaTri.setFont(fontNhan);
        lblTongLuong.setFont(fontNhan);

        txtTongGiaTri = new JTextField();
        txtTongLuong = new JTextField();
        txtTongGiaTri.setFont(fontThuong);
        txtTongLuong.setFont(fontThuong);
        txtTongGiaTri.setEditable(false);
        txtTongLuong.setEditable(false);
        txtTongGiaTri.setBackground(Color.WHITE);
        txtTongLuong.setBackground(Color.WHITE);

        GridBagConstraints gbcTK = new GridBagConstraints();
        gbcTK.insets = new Insets(4, 4, 4, 4);
        gbcTK.fill = GridBagConstraints.HORIZONTAL;
        themDongNhap(pnlThongKe, gbcTK, 0, lblTongGiaTri, txtTongGiaTri);
        themDongNhap(pnlThongKe, gbcTK, 1, lblTongLuong, txtTongLuong);

        pnlWrapper.add(pnlThongKe, BorderLayout.CENTER);
        return pnlWrapper;
    }

    /**
     * Tạo thanh điều hướng bản ghi: Về đầu, Về trước, Chỉ số hiện hành (0/0), Về sau, Về cuối.
     */
    private JPanel taoPanelDieuHuong(Color nen) {
        JPanel pnlNav = new JPanel(new GridLayout(1, 5, 4, 0));
        pnlNav.setBackground(nen);

        btnVeDau = new JButton("Về đầu");
        btnVeTruoc = new JButton("Về trước");
        lblViTri = new JLabel("0/0", SwingConstants.CENTER);
        btnVeSau = new JButton("Về sau");
        btnVeCuoi = new JButton("Về cuối");

        Font fontNav = new Font("Segoe UI", Font.PLAIN, 11);
        Insets navMargin = new Insets(2, 2, 2, 2);
        for (JButton b : new JButton[]{btnVeDau, btnVeTruoc, btnVeSau, btnVeCuoi}) {
            b.setFont(fontNav);
            b.setMargin(navMargin);
        }

        lblViTri.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblViTri.setOpaque(true);
        lblViTri.setBackground(Color.WHITE);
        lblViTri.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        pnlNav.add(btnVeDau);
        pnlNav.add(btnVeTruoc);
        pnlNav.add(lblViTri);
        pnlNav.add(btnVeSau);
        pnlNav.add(btnVeCuoi);
        return pnlNav;
    }

    /**
     * Tạo khung chọn Màu sắc (Đỏ, Xanh, Tím) và cặp nút Xóa, Thoát.
     */
    private JPanel taoPanelMauVaThoat(Color nen) {
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 0));
        pnlBottom.setBackground(nen);

        JPanel pnlMau = new JPanel();
        pnlMau.setLayout(new BoxLayout(pnlMau, BoxLayout.Y_AXIS));
        pnlMau.setBackground(nen);
        TitledBorder borderMau = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Màu sắc");
        borderMau.setTitleFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlMau.setBorder(new CompoundBorder(borderMau, new EmptyBorder(2, 8, 4, 16)));

        Font fontMau = new Font("Segoe UI", Font.PLAIN, 12);
        radDo = new JRadioButton("Đỏ", true);
        radXanh = new JRadioButton("Xanh");
        radTim = new JRadioButton("Tím");
        for (JRadioButton r : new JRadioButton[]{radDo, radXanh, radTim}) {
            r.setFont(fontMau);
            r.setBackground(nen);
            pnlMau.add(r);
        }

        bgMauSac = new ButtonGroup();
        bgMauSac.add(radDo);
        bgMauSac.add(radXanh);
        bgMauSac.add(radTim);
        pnlBottom.add(pnlMau, BorderLayout.WEST);

        JPanel pnlRightBtns = new JPanel(new BorderLayout());
        pnlRightBtns.setBackground(nen);
        JPanel pnlBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlBtnRow.setBackground(nen);

        btnXoa = new JButton("Xóa");
        btnThoat = new JButton("Thoát");
        Font fontBtn = new Font("Segoe UI", Font.PLAIN, 12);
        btnXoa.setFont(fontBtn);
        btnThoat.setFont(fontBtn);
        btnXoa.setPreferredSize(new Dimension(68, 28));
        btnThoat.setPreferredSize(new Dimension(68, 28));

        pnlBtnRow.add(btnXoa);
        pnlBtnRow.add(btnThoat);
        pnlRightBtns.add(pnlBtnRow, BorderLayout.SOUTH);
        pnlBottom.add(pnlRightBtns, BorderLayout.CENTER);

        return pnlBottom;
    }

    private void themDongNhap(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JTextField textField) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.32;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.68;
        panel.add(textField, gbc);
    }

    /**
     * Đăng ký toàn bộ Listener cho ô nhập, radio, checkbox và các nút chức năng.
     */
    private void khoiTaoSuKien() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { capNhatTrangThaiNutNhap(); }
            @Override
            public void removeUpdate(DocumentEvent e) { capNhatTrangThaiNutNhap(); }
            @Override
            public void changedUpdate(DocumentEvent e) { capNhatTrangThaiNutNhap(); }
        };

        txtMaMay.getDocument().addDocumentListener(docListener);
        txtTenMay.getDocument().addDocumentListener(docListener);
        txtDonGia.getDocument().addDocumentListener(docListener);
        txtSoLuong.getDocument().addDocumentListener(docListener);

        ActionListener checkListener = e -> capNhatTrangThaiNutNhap();
        radLapRap.addActionListener(checkListener);
        radNhapKhau.addActionListener(checkListener);
        chkVGA.addActionListener(checkListener);
        chkDVD.addActionListener(checkListener);
        chkLoa.addActionListener(checkListener);

        btnNhapMay.addActionListener(e -> xuLyNhapMay());
        btnNhapMoi.addActionListener(e -> xuLyNhapMoi());
        btnXoa.addActionListener(e -> xuLyXoa());
        btnThoat.addActionListener(e -> xuLyThoat());

        btnVeDau.addActionListener(e -> diChuyenDen(0));
        btnVeTruoc.addActionListener(e -> diChuyenDen(currentIndex - 1));
        btnVeSau.addActionListener(e -> diChuyenDen(currentIndex + 1));
        btnVeCuoi.addActionListener(e -> diChuyenDen(dsmt.size() - 1));

        radDo.addActionListener(e -> doiMauQuanTrong(Color.RED));
        radXanh.addActionListener(e -> doiMauQuanTrong(new Color(0, 102, 204)));
        radTim.addActionListener(e -> doiMauQuanTrong(new Color(153, 0, 153)));
    }

    /**
     * Câu 3b (1.0 đ): Khởi tạo màn hình:
     * - Con trỏ văn bản đặt vào ô Mã máy.
     * - Chỉ có các button Nhập mới và Thoát sáng lên, còn các nút khác bị mờ (enabled = false).
     */
    private void khoiTaoTrangThaiBanDau() {
        btnNhapMoi.setEnabled(true);
        btnThoat.setEnabled(true);
        btnNhapMay.setEnabled(false);
        btnXoa.setEnabled(false);
        capNhatTrangThaiDieuHuong();
        SwingUtilities.invokeLater(() -> txtMaMay.requestFocusInWindow());
    }

    private void capNhatTrangThaiNutNhap() {
        if (isProgrammaticUpdate) return;
        btnNhapMay.setEnabled(duLieuHopLe());
    }

    /**
     * Câu 3c (1.5 đ): Kiểm tra tính hợp lệ của các ô nhập thông tin Máy tính:
     * - Mã máy không được để trống, phải có đủ 5 ký tự, và không trùng lặp với máy tính đã có.
     * - Tên máy không được để trống.
     * - Đơn giá và Số lượng phải là số nguyên dương (> 0).
     * - Một loại máy được chọn.
     */
    public boolean duLieuHopLe() {
        String ma = txtMaMay.getText().trim();
        String ten = txtTenMay.getText().trim();
        String giaStr = txtDonGia.getText().trim();
        String slStr = txtSoLuong.getText().trim();

        if (ma.length() != 5 || ten.isEmpty() || giaStr.isEmpty() || slStr.isEmpty()) {
            return false;
        }
        if (!radLapRap.isSelected() && !radNhapKhau.isSelected()) {
            return false;
        }
        for (MayTinh mt : dsmt) {
            if (mt.getMaMay().equalsIgnoreCase(ma)) {
                return false;
            }
        }
        try {
            long donGia = Long.parseLong(giaStr);
            int soLuong = Integer.parseInt(slStr);
            return donGia > 0 && soLuong > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Câu 3d (1.0 đ): Nút Nhập máy:
     * Kiểm tra ràng buộc, lưu thông tin Máy tính hiện thời vào cuối DSMT,
     * sau đó cập nhật Giá trị, Tổng giá trị và Tổng lượng.
     */
    private void xuLyNhapMay() {
        if (!duLieuHopLe()) {
            if (!isTestMode) {
                JOptionPane.showMessageDialog(this,
                        "Thông tin máy tính chưa hợp lệ hoặc mã máy bị trùng!",
                        "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
            btnNhapMay.setEnabled(false);
            return;
        }

        MayTinh mt = new MayTinh(
                txtMaMay.getText().trim(),
                txtTenMay.getText().trim(),
                Long.parseLong(txtDonGia.getText().trim()),
                Integer.parseInt(txtSoLuong.getText().trim()),
                radNhapKhau.isSelected() ? "Nhập khẩu" : "Lắp ráp",
                chkVGA.isSelected(),
                chkDVD.isSelected(),
                chkLoa.isSelected()
        );

        dsmt.add(mt);
        currentIndex = dsmt.size() - 1;
        txtGiaTri.setText(MayTinh.dinhDangTien(mt.getGiaTriMay()));
        capNhatThongKe();
        capNhatTrangThaiDieuHuong();

        btnNhapMay.setEnabled(false);
        btnXoa.setEnabled(true);

        if (!isTestMode) {
            JOptionPane.showMessageDialog(this,
                    "Đã nhập máy tính vào danh sách DSMT thành công!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Câu 3a (1.0 đ): Tính và cập nhật Tổng Giá Trị = Σ Giá trị máy, Tổng lượng = Σ Số lượng.
     */
    private void capNhatThongKe() {
        double tongGiaTri = 0;
        long tongLuong = 0;
        for (MayTinh mt : dsmt) {
            tongGiaTri += mt.getGiaTriMay();
            tongLuong += mt.getSoLuong();
        }
        txtTongGiaTri.setText(dsmt.isEmpty() ? "" : MayTinh.dinhDangTien(tongGiaTri));
        txtTongLuong.setText(dsmt.isEmpty() ? "" : MayTinh.dinhDangTien(tongLuong));
    }

    /**
     * Câu 3f (0.5 đ): Nút Nhập mới: tất cả các thông tin của Máy tính trở về rỗng.
     */
    private void xuLyNhapMoi() {
        isProgrammaticUpdate = true;
        try {
            txtMaMay.setText("");
            txtTenMay.setText("");
            txtDonGia.setText("");
            txtSoLuong.setText("");
            txtGiaTri.setText("");
            radLapRap.setSelected(true);
            chkVGA.setSelected(false);
            chkDVD.setSelected(false);
            chkLoa.setSelected(false);
            btnNhapMay.setEnabled(false);
        } finally {
            isProgrammaticUpdate = false;
        }
        txtMaMay.requestFocusInWindow();
    }

    /**
     * Câu 3e (0.5 đ): Khi DSMT có ít nhất 1 phần tử thì Nút Xóa sáng lên và xóa Máy tính ở vị trí hiện thời.
     */
    private void xuLyXoa() {
        if (currentIndex < 0 || currentIndex >= dsmt.size()) return;
        if (!isTestMode) {
            int ans = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa máy tính tại vị trí hiện thời?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (ans != JOptionPane.YES_OPTION) return;
        }

        dsmt.remove(currentIndex);
        if (dsmt.isEmpty()) {
            currentIndex = -1;
            xuLyNhapMoi();
        } else {
            currentIndex = Math.min(currentIndex, dsmt.size() - 1);
            hienThiMayTinh(currentIndex);
        }

        capNhatThongKe();
        capNhatTrangThaiDieuHuong();
        btnXoa.setEnabled(!dsmt.isEmpty());
    }

    private void diChuyenDen(int index) {
        if (index >= 0 && index < dsmt.size()) {
            hienThiMayTinh(index);
        }
    }

    /**
     * Hiển thị thông tin máy tính tại vị trí index trong DSMT lên các ô điều khiển.
     */
    private void hienThiMayTinh(int index) {
        if (index < 0 || index >= dsmt.size()) return;
        MayTinh mt = dsmt.get(index);
        isProgrammaticUpdate = true;
        try {
            txtMaMay.setText(mt.getMaMay());
            txtTenMay.setText(mt.getTenMay());
            txtDonGia.setText(String.valueOf(mt.getDonGia()));
            txtSoLuong.setText(String.valueOf(mt.getSoLuong()));
            txtGiaTri.setText(MayTinh.dinhDangTien(mt.getGiaTriMay()));
            radLapRap.setSelected(!"Nhập khẩu".equalsIgnoreCase(mt.getLoaiMay()));
            radNhapKhau.setSelected("Nhập khẩu".equalsIgnoreCase(mt.getLoaiMay()));
            chkVGA.setSelected(mt.isCoVGA());
            chkDVD.setSelected(mt.isCoDVD());
            chkLoa.setSelected(mt.isCoLoa());
            btnNhapMay.setEnabled(false);
            currentIndex = index;
        } finally {
            isProgrammaticUpdate = false;
        }
        capNhatTrangThaiDieuHuong();
    }

    private void capNhatTrangThaiDieuHuong() {
        int n = dsmt.size();
        lblViTri.setText((n == 0 || currentIndex < 0) ? "0/0" : (currentIndex + 1) + "/" + n);
        btnVeDau.setEnabled(currentIndex > 0);
        btnVeTruoc.setEnabled(currentIndex > 0);
        btnVeSau.setEnabled(currentIndex >= 0 && currentIndex < n - 1);
        btnVeCuoi.setEnabled(currentIndex >= 0 && currentIndex < n - 1);
    }

    /**
     * Câu 3h (0.5 đ): Các check chọn ở phần Màu sắc khi được chọn sẽ có tác dụng
     * cho các phần tử quan trọng trên Cửa sổ.
     */
    private void doiMauQuanTrong(Color color) {
        if (nhanQuanTrong != null) {
            for (JLabel lbl : nhanQuanTrong) {
                lbl.setForeground(color);
            }
        }
    }

    /**
     * Câu 3g (0.5 đ): Nút Thoát: hỏi người dùng có chắc chắn thoát khỏi chương trình hay không?
     * Yes: thoát, No: không.
     */
    private void xuLyThoat() {
        if (isTestMode) {
            dispose();
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn thoát khỏi chương trình hay không?",
                "Xác nhận thoát", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new QuanLyMayTinhGUI().setVisible(true));
    }
}
