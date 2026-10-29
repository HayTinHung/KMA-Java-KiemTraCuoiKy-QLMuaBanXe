package TEST;

import javax.swing.BorderFactory;
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
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Giao diện Thiết Bị Nhập Khẩu - Đề 1.
 * Bố cục được dựng theo ảnh đề: phần nhập liệu bên trái, xử lý và thống kê
 * bên phải.
 */
public class ThietBiNhapKhauGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    public static boolean isTestMode = false;

    final List<ThietBi> dstb = new ArrayList<>();
    private int currentIndex = -1;
    private boolean isProgrammaticUpdate;

    JLabel lblMaTB;
    JLabel lblTenTB;
    JLabel lblGiaNhap;
    JLabel lblSoLuong;
    JLabel lblGiaTri;
    JLabel lblTongGiaTri;
    JLabel lblTongLuong;
    JLabel lblThongKe;
    JLabel lblViTri;
    JLabel lblThoiGian;
    List<JLabel> nhanQuanTrong;

    JTextField txtMaTB;
    JTextField txtTenTB;
    JTextField txtGiaNhap;
    JTextField txtSoLuong;
    JTextField txtGiaTri;
    JTextField txtTongGiaTri;
    JTextField txtTongLuong;

    JRadioButton radLaoDong;
    JRadioButton radSanXuat;
    JCheckBox chkDayCap;
    JCheckBox chkBanhXe;
    JCheckBox chkThayThe;

    JButton btnNhapVao;
    JButton btnNhapMoi;
    JButton btnXoa;
    JButton btnThoat;
    JButton btnVeDau;
    JButton btnVeTruoc;
    JButton btnVeSau;
    JButton btnVeCuoi;

    JRadioButton radDo;
    JRadioButton radXanh;
    JRadioButton radTim;

    public ThietBiNhapKhauGUI() {
        super("Giao diện");
        khoiTaoGiaoDien();
        khoiTaoSuKien();
        khoiTaoTrangThaiBanDau();
    }

    private void khoiTaoGiaoDien() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                xuLyThoat();
            }
        });

        Font fontNhan = new Font("Segoe UI", Font.BOLD, 14);
        Font fontThuong = new Font("Segoe UI", Font.PLAIN, 13);
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 25);
        Color nen = new Color(240, 240, 240);

        JPanel mainPanel = new JPanel(new BorderLayout(6, 6));
        mainPanel.setBorder(new EmptyBorder(2, 2, 3, 2));
        mainPanel.setBackground(nen);

        JPanel pnlTitle = new JPanel(new BorderLayout());
        pnlTitle.setPreferredSize(new Dimension(575, 67));
        pnlTitle.setBackground(nen);
        pnlTitle.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel lblTitle = new JLabel("Thiết Bị Nhập Khẩu", SwingConstants.CENTER);
        lblTitle.setFont(fontTieuDe);
        lblTitle.setForeground(Color.RED);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);
        lblThoiGian = new JLabel();
        lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblThoiGian.setBorder(new EmptyBorder(2, 4, 0, 0));
        // Giữ đồng hồ cho yêu cầu xử lý, nhưng ẩn khỏi giao diện Câu 1.
        lblThoiGian.setVisible(false);
        mainPanel.add(pnlTitle, BorderLayout.NORTH);

        // Hai cột chính luôn bằng nhau như ảnh đề.
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 5, 0));
        pnlCenter.setBackground(nen);
        pnlCenter.add(taoPanelBenTrai(fontNhan, fontThuong, nen));
        pnlCenter.add(taoPanelBenPhai(fontNhan, fontThuong, nen));
        mainPanel.add(pnlCenter, BorderLayout.CENTER);

        nhanQuanTrong = new ArrayList<>();
        nhanQuanTrong.add(lblMaTB);
        nhanQuanTrong.add(lblTenTB);
        nhanQuanTrong.add(lblGiaNhap);
        nhanQuanTrong.add(lblSoLuong);
        nhanQuanTrong.add(lblGiaTri);
        nhanQuanTrong.add(lblTongGiaTri);
        nhanQuanTrong.add(lblTongLuong);
        doiMauQuanTrong(Color.RED);

        setContentPane(mainPanel);
        setPreferredSize(new Dimension(590, 340));
        pack();
        setMinimumSize(new Dimension(590, 340));
        setResizable(true);
        setLocationRelativeTo(null);
    }

    private JPanel taoPanelBenTrai(Font fontNhan, Font fontThuong, Color nen) {
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(nen);
        pnlLeft.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));

        JPanel pnlInputs = new JPanel(new GridBagLayout());
        pnlInputs.setBackground(nen);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 3, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblMaTB = new JLabel("Mã TB:");
        lblTenTB = new JLabel("Tên TB:");
        lblGiaNhap = new JLabel("Giá Nhập:");
        lblSoLuong = new JLabel("Số Lượng:");
        JLabel[] labels = {lblMaTB, lblTenTB, lblGiaNhap, lblSoLuong};
        txtMaTB = new JTextField();
        txtTenTB = new JTextField();
        txtGiaNhap = new JTextField();
        txtSoLuong = new JTextField();
        JTextField[] fields = {txtMaTB, txtTenTB, txtGiaNhap, txtSoLuong};
        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(fontNhan);
            fields[i].setFont(fontThuong);
            fields[i].setPreferredSize(new Dimension(180, 21));
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(2, 3, 2, 15);
            pnlInputs.add(labels[i], gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(2, 3, 2, 13);
            pnlInputs.add(fields[i], gbc);
        }
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 1;
        leftGbc.weighty = 0;
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        pnlLeft.add(pnlInputs, leftGbc);

        JPanel pnlLuaChon = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlLuaChon.setBackground(nen);
        pnlLuaChon.add(taoPanelLoaiTB(fontNhan, fontThuong, nen));
        pnlLuaChon.add(taoPanelPhuKien(fontNhan, fontThuong, nen));
        pnlLuaChon.setPreferredSize(new Dimension(280, 140));
        pnlLuaChon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        leftGbc.gridy = 1;
        leftGbc.insets = new Insets(12, 0, 0, 0);
        pnlLeft.add(pnlLuaChon, leftGbc);
        return pnlLeft;
    }

    private JPanel taoPanelLoaiTB(Font fontNhan, Font fontThuong, Color nen) {
        JPanel panel = taoPanelCoTieuDe("Loại Thiết Bị", fontNhan, nen);
        panel.setLayout(new BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        radLaoDong = new JRadioButton("Lao Động", true);
        radSanXuat = new JRadioButton("Sản Xuất");
        radLaoDong.setFont(fontThuong);
        radSanXuat.setFont(fontThuong);
        radLaoDong.setBackground(nen);
        radSanXuat.setBackground(nen);
        ButtonGroup group = new ButtonGroup();
        group.add(radLaoDong);
        group.add(radSanXuat);
        panel.add(radLaoDong);
        panel.add(javax.swing.Box.createVerticalStrut(7));
        panel.add(radSanXuat);
        return panel;
    }

    private JPanel taoPanelPhuKien(Font fontNhan, Font fontThuong, Color nen) {
        JPanel panel = taoPanelCoTieuDe("Phụ Kiện", fontNhan, nen);
        panel.setLayout(new BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        chkDayCap = new JCheckBox("Dây cáp");
        chkBanhXe = new JCheckBox("Bánh xe");
        chkThayThe = new JCheckBox("Thay thế");
        JCheckBox[] checks = {chkDayCap, chkBanhXe, chkThayThe};
        for (JCheckBox check : checks) {
            check.setFont(fontThuong);
            check.setBackground(nen);
            panel.add(check);
            panel.add(javax.swing.Box.createVerticalStrut(3));
        }
        return panel;
    }

    private JPanel taoPanelCoTieuDe(String title, Font fontNhan, Color nen) {
        JPanel panel = new JPanel();
        panel.setBackground(nen);
        TitledBorder titleBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), title);
        titleBorder.setTitleFont(fontNhan);
        panel.setBorder(new CompoundBorder(titleBorder, new EmptyBorder(3, 7, 5, 7)));
        return panel;
    }

    private JPanel taoPanelBenPhai(Font fontNhan, Font fontThuong, Color nen) {
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBackground(nen);

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTop.setBackground(nen);
        pnlTop.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));
        btnNhapVao = new JButton("Nhập vào");
        btnNhapMoi = new JButton("Nhập mới");
        pnlTop.add(btnNhapVao);
        pnlTop.add(btnNhapMoi);

        JPanel pnlThongKe = new JPanel(new BorderLayout(0, 2));
        pnlThongKe.setBackground(nen);
        pnlThongKe.setPreferredSize(new Dimension(285, 122));
        lblGiaTri = new JLabel("Giá trị:");
        lblTongGiaTri = new JLabel("Tổng Giá trị:");
        lblTongLuong = new JLabel("Tổng lượng:");
        lblGiaTri.setFont(fontNhan);
        lblTongGiaTri.setFont(fontNhan);
        lblTongLuong.setFont(fontNhan);
        txtGiaTri = new JTextField();
        txtTongGiaTri = new JTextField();
        txtTongLuong = new JTextField();
        txtGiaTri.setPreferredSize(new Dimension(180, 21));
        txtTongGiaTri.setPreferredSize(new Dimension(180, 21));
        txtTongLuong.setPreferredSize(new Dimension(180, 21));
        txtGiaTri.setEditable(false);
        txtTongGiaTri.setEditable(false);
        txtTongLuong.setEditable(false);
        lblThongKe = new JLabel("Thống kê");
        lblThongKe.setFont(fontThuong);
        // Căn cùng mép trái với nhãn Giá trị (pnlGiaTri có inset trái 4px).
        lblThongKe.setBorder(new EmptyBorder(0, 4, 0, 0));

        // Hàng Giá trị có ô rộng hơn và bắt đầu ngay sau nhãn.
        JPanel pnlGiaTri = new JPanel(new GridBagLayout());
        pnlGiaTri.setBackground(nen);
        GridBagConstraints gbcGiaTri = new GridBagConstraints();
        gbcGiaTri.insets = new Insets(0, 4, 0, 4);
        gbcGiaTri.fill = GridBagConstraints.HORIZONTAL;
        gbcGiaTri.gridx = 0;
        gbcGiaTri.weightx = 0;
        pnlGiaTri.add(lblGiaTri, gbcGiaTri);
        gbcGiaTri.gridx = 1;
        gbcGiaTri.weightx = 1;
        pnlGiaTri.add(txtGiaTri, gbcGiaTri);

        // Hai dòng thống kê dùng cùng một cột nhãn, nên ô bắt đầu thẳng hàng.
        JPanel pnlTong = new JPanel(new GridBagLayout());
        pnlTong.setBackground(nen);
        GridBagConstraints gbcTong = new GridBagConstraints();
        gbcTong.insets = new Insets(0, 4, 2, 4);
        gbcTong.fill = GridBagConstraints.HORIZONTAL;
        gbcTong.gridx = 0;
        gbcTong.weightx = 0;
        pnlTong.add(lblTongGiaTri, gbcTong);
        gbcTong.gridx = 1;
        gbcTong.weightx = 1;
        pnlTong.add(txtTongGiaTri, gbcTong);
        gbcTong.gridx = 0;
        gbcTong.gridy = 1;
        gbcTong.weightx = 0;
        pnlTong.add(lblTongLuong, gbcTong);
        gbcTong.gridx = 1;
        gbcTong.weightx = 1;
        pnlTong.add(txtTongLuong, gbcTong);

        JPanel pnlThongKeNoiDung = new JPanel(new BorderLayout(0, 1));
        pnlThongKeNoiDung.setBackground(nen);
        pnlThongKeNoiDung.add(lblThongKe, BorderLayout.NORTH);
        pnlThongKeNoiDung.add(pnlTong, BorderLayout.CENTER);
        pnlThongKe.add(pnlGiaTri, BorderLayout.NORTH);
        pnlThongKe.add(pnlThongKeNoiDung, BorderLayout.CENTER);
        pnlThongKe.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlThongKe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 122));

        JPanel pnlNavigation = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        pnlNavigation.setBackground(nen);
        pnlNavigation.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlNavigation.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));
        btnVeDau = new JButton("Về đầu");
        btnVeTruoc = new JButton("Về trước");
        btnVeSau = new JButton("Về sau");
        btnVeCuoi = new JButton("Về cuối");
        Font fontNavigation = new Font("Segoe UI", Font.PLAIN, 11);
        Insets marginNavigation = new Insets(1, 2, 1, 2);
        for (JButton button : new JButton[]{btnVeDau, btnVeTruoc, btnVeSau, btnVeCuoi}) {
            button.setFont(fontNavigation);
            button.setMargin(marginNavigation);
        }
        btnVeDau.setPreferredSize(new Dimension(53, 21));
        btnVeTruoc.setPreferredSize(new Dimension(62, 21));
        btnVeSau.setPreferredSize(new Dimension(53, 21));
        btnVeCuoi.setPreferredSize(new Dimension(62, 21));
        lblViTri = new JLabel("0/0", SwingConstants.CENTER);
        lblViTri.setOpaque(true);
        lblViTri.setBackground(Color.WHITE);
        lblViTri.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblViTri.setPreferredSize(new Dimension(35, 21));
        pnlNavigation.add(btnVeDau);
        pnlNavigation.add(btnVeTruoc);
        pnlNavigation.add(lblViTri);
        pnlNavigation.add(btnVeSau);
        pnlNavigation.add(btnVeCuoi);

        JPanel pnlBottom = new JPanel(new BorderLayout(10, 0));
        pnlBottom.setBackground(nen);
        pnlBottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBottom.setPreferredSize(new Dimension(285, 77));
        pnlBottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 77));
        JPanel pnlMauSac = taoPanelCoTieuDe("Màu sắc", fontThuong, nen);
        pnlMauSac.setLayout(new GridLayout(3, 1));
        pnlMauSac.setPreferredSize(new Dimension(102, 77));
        pnlMauSac.setMinimumSize(new Dimension(102, 77));
        radDo = new JRadioButton("Đỏ", true);
        radXanh = new JRadioButton("Xanh");
        radTim = new JRadioButton("Tím");
        ButtonGroup colorGroup = new ButtonGroup();
        colorGroup.add(radDo);
        colorGroup.add(radXanh);
        colorGroup.add(radTim);
        for (JRadioButton radio : new JRadioButton[]{radDo, radXanh, radTim}) {
            radio.setBackground(nen);
            radio.setFont(fontThuong);
            pnlMauSac.add(radio);
        }
        pnlBottom.add(pnlMauSac, BorderLayout.WEST);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 28));
        pnlActions.setBackground(nen);
        btnXoa = new JButton("Xóa");
        btnThoat = new JButton("Thoát");
        pnlActions.add(btnXoa);
        pnlActions.add(btnThoat);
        pnlBottom.add(pnlActions, BorderLayout.CENTER);

        pnlRight.add(pnlTop);
        pnlRight.add(javax.swing.Box.createVerticalStrut(8));
        pnlRight.add(pnlThongKe);
        pnlRight.add(javax.swing.Box.createVerticalGlue());
        pnlRight.add(pnlNavigation);
        pnlRight.add(javax.swing.Box.createVerticalStrut(8));
        pnlRight.add(pnlBottom);
        return pnlRight;
    }

    private void khoiTaoSuKien() {
        DocumentListener inputListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { capNhatTrangThaiNhap(); }
            public void removeUpdate(DocumentEvent e) { capNhatTrangThaiNhap(); }
            public void changedUpdate(DocumentEvent e) { capNhatTrangThaiNhap(); }
        };
        txtMaTB.getDocument().addDocumentListener(inputListener);
        txtTenTB.getDocument().addDocumentListener(inputListener);
        txtGiaNhap.getDocument().addDocumentListener(inputListener);
        txtSoLuong.getDocument().addDocumentListener(inputListener);

        btnNhapVao.addActionListener(e -> xuLyNhapVao());
        btnNhapMoi.addActionListener(e -> xuLyNhapMoi());
        btnXoa.addActionListener(e -> xuLyXoa());
        btnThoat.addActionListener(e -> xuLyThoat());

        btnVeDau.addActionListener(e -> diChuyenDen(0));
        btnVeTruoc.addActionListener(e -> diChuyenDen(currentIndex - 1));
        btnVeSau.addActionListener(e -> diChuyenDen(currentIndex + 1));
        btnVeCuoi.addActionListener(e -> diChuyenDen(dstb.size() - 1));

        ActionListener colorListener = e -> {
            if (radDo.isSelected()) doiMauQuanTrong(Color.RED);
            else if (radXanh.isSelected()) doiMauQuanTrong(new Color(0, 102, 204));
            else if (radTim.isSelected()) doiMauQuanTrong(new Color(153, 0, 153));
        };
        radDo.addActionListener(colorListener);
        radXanh.addActionListener(colorListener);
        radTim.addActionListener(colorListener);

        javax.swing.Timer clock = new javax.swing.Timer(60_000, e -> capNhatThoiGian());
        clock.setInitialDelay(0);
        clock.start();
    }

    private void khoiTaoTrangThaiBanDau() {
        btnNhapVao.setEnabled(false);
        btnNhapMoi.setEnabled(true);
        btnXoa.setEnabled(false);
        btnThoat.setEnabled(true);
        capNhatTrangThaiDieuHuong();
        capNhatThoiGian();
        SwingUtilities.invokeLater(() -> txtMaTB.requestFocusInWindow());
    }

    private void capNhatThoiGian() {
        lblThoiGian.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
    }

    private boolean duLieuHopLe() {
        String ma = txtMaTB.getText().trim();
        String ten = txtTenTB.getText().trim();
        if (ma.length() != 8 || ten.isEmpty()) return false;
        for (ThietBi tb : dstb) {
            if (tb.getMaTB().equalsIgnoreCase(ma)) return false;
        }
        try {
            if (Long.parseLong(txtGiaNhap.getText().trim()) <= 0) return false;
            if (Integer.parseInt(txtSoLuong.getText().trim()) <= 0) return false;
        } catch (NumberFormatException ex) {
            return false;
        }
        return radLaoDong.isSelected() || radSanXuat.isSelected();
    }

    private void capNhatTrangThaiNhap() {
        if (!isProgrammaticUpdate) btnNhapVao.setEnabled(duLieuHopLe());
    }

    private void xuLyNhapVao() {
        if (!duLieuHopLe()) return;
        ThietBi tb = new ThietBi(
                txtMaTB.getText().trim(),
                txtTenTB.getText().trim(),
                Long.parseLong(txtGiaNhap.getText().trim()),
                Integer.parseInt(txtSoLuong.getText().trim()),
                radSanXuat.isSelected() ? "Sản Xuất" : "Lao Động",
                chkDayCap.isSelected(), chkBanhXe.isSelected(), chkThayThe.isSelected());
        dstb.add(tb);
        currentIndex = dstb.size() - 1;
        txtGiaTri.setText(ThietBi.dinhDangTien(tb.getGiaTri()));
        capNhatThongKe();
        capNhatTrangThaiDieuHuong();
        btnNhapVao.setEnabled(false);
        btnXoa.setEnabled(true);
        if (!isTestMode) {
            JOptionPane.showMessageDialog(this, "Nhập thiết bị thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void capNhatThongKe() {
        double tongGiaTri = 0;
        long tongLuong = 0;
        for (ThietBi tb : dstb) {
            tongGiaTri += tb.getGiaTri();
            tongLuong += tb.getSoLuong();
        }
        txtTongGiaTri.setText(ThietBi.dinhDangTien(tongGiaTri));
        txtTongLuong.setText(ThietBi.dinhDangTien(tongLuong));
    }

    private void xuLyNhapMoi() {
        isProgrammaticUpdate = true;
        try {
            currentIndex = -1;
            txtMaTB.setText("");
            txtTenTB.setText("");
            txtGiaNhap.setText("");
            txtSoLuong.setText("");
            txtGiaTri.setText("");
            radLaoDong.setSelected(true);
            chkDayCap.setSelected(false);
            chkBanhXe.setSelected(false);
            chkThayThe.setSelected(false);
            btnNhapVao.setEnabled(false);
            btnXoa.setEnabled(false);
        } finally {
            isProgrammaticUpdate = false;
        }
        capNhatTrangThaiDieuHuong();
        txtMaTB.requestFocusInWindow();
    }

    private void xuLyXoa() {
        if (currentIndex < 0 || currentIndex >= dstb.size()) return;
        if (!isTestMode) {
            int answer = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa thiết bị hiện tại?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) return;
        }
        dstb.remove(currentIndex);
        if (dstb.isEmpty()) {
            currentIndex = -1;
            xuLyNhapMoi();
        } else {
            currentIndex = Math.min(currentIndex, dstb.size() - 1);
            hienThiThietBi(currentIndex);
        }
        capNhatThongKe();
        capNhatTrangThaiDieuHuong();
        btnXoa.setEnabled(!dstb.isEmpty());
    }

    private void diChuyenDen(int index) {
        if (index >= 0 && index < dstb.size()) hienThiThietBi(index);
    }

    private void hienThiThietBi(int index) {
        if (index < 0 || index >= dstb.size()) return;
        ThietBi tb = dstb.get(index);
        isProgrammaticUpdate = true;
        try {
            txtMaTB.setText(tb.getMaTB());
            txtTenTB.setText(tb.getTenTB());
            txtGiaNhap.setText(String.valueOf(tb.getGiaNhap()));
            txtSoLuong.setText(String.valueOf(tb.getSoLuong()));
            txtGiaTri.setText(ThietBi.dinhDangTien(tb.getGiaTri()));
            radLaoDong.setSelected(!"Sản Xuất".equalsIgnoreCase(tb.getLoaiTB()));
            radSanXuat.setSelected("Sản Xuất".equalsIgnoreCase(tb.getLoaiTB()));
            chkDayCap.setSelected(tb.isDayCap());
            chkBanhXe.setSelected(tb.isBanhXe());
            chkThayThe.setSelected(tb.isThayThe());
            btnNhapVao.setEnabled(false);
            currentIndex = index;
        } finally {
            isProgrammaticUpdate = false;
        }
        capNhatTrangThaiDieuHuong();
    }

    private void capNhatTrangThaiDieuHuong() {
        int n = dstb.size();
        lblViTri.setText(n == 0 ? "0/0" : (currentIndex + 1) + "/" + n);
        btnVeDau.setEnabled(currentIndex > 0);
        btnVeTruoc.setEnabled(currentIndex > 0);
        btnVeSau.setEnabled(currentIndex >= 0 && currentIndex < n - 1);
        btnVeCuoi.setEnabled(currentIndex >= 0 && currentIndex < n - 1);
    }

    private void doiMauQuanTrong(Color color) {
        if (nhanQuanTrong != null) {
            for (JLabel label : nhanQuanTrong) label.setForeground(color);
        }
    }

    private void xuLyThoat() {
        if (isTestMode) {
            dispose();
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn thoát khỏi chương trình hay không?",
                "Xác nhận thoát", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) System.exit(0);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new ThietBiNhapKhauGUI().setVisible(true));
    }
}
