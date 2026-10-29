import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Cửa sổ Báo cáo (Câu 2h):
 * - Chứa Combobox "Mã xe", Txtbox "Ngày mua", và Btn "Thực hiện báo cáo".
 * - Phát sinh báo cáo theo chuẩn giao diện Crystal Report trong đề thi:
 *   "Cửa hàng Mô tô PKL - THỐNG KÊ KHÁCH HÀNG ĐÃ MUA XE"
 *   với bảng 5 cột: STT | Mã khách hàng | Ngày mua | Số lượng | Ghi chú.
 */
@SuppressWarnings("serial")
public class BaoCaoMuaXeDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final MuaBanXeRepository repository;
    private final boolean autoConfirmMode;

    private JComboBox<String> cboMaXe;
    private JTextField txtNgayMua;
    private JButton btnThucHienBaoCao;
    private JButton btnDongBaoCao;

    private JLabel lblValMaXe;
    private JLabel lblValTenXe;
    private JLabel lblValDonGia;
    private JLabel lblValDungTich;
    private JLabel lblValSoLuongKH;
    private JLabel lblNgayLap;

    private DefaultTableModel reportTableModel;
    private JTable tblBaoCao;
    private List<Xe> danhSachXe = new ArrayList<Xe>();

    public BaoCaoMuaXeDialog(JFrame parent, MuaBanXeRepository repository, String initialMaXe, boolean autoConfirmMode) {
        super(parent, "Cửa sổ Báo cáo - Thống kê khách hàng đã mua xe", false);
        this.repository = repository;
        this.autoConfirmMode = autoConfirmMode;
        khoiTaoGiaoDien();
        napDanhSachMaXe(initialMaXe);
        thucHienPhatSinhBaoCao(false);
    }

    private void khoiTaoGiaoDien() {
        setSize(760, 560);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(8, 8));

        // 1. Thanh tiêu chí lọc ở phía Bắc (Combobox "Mã xe", Txtbox "Ngày mua", Btn "Thực hiện báo cáo")
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlFilter.setBackground(new Color(238, 242, 248));
        pnlFilter.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(140, 160, 190), 1),
                "Tiêu chí lọc báo cáo (Tổ hợp lựa chọn)"
        ));

        JLabel lblLocMaXe = new JLabel("Mã xe:");
        lblLocMaXe.setFont(new Font("Tahoma", Font.BOLD, 12));
        cboMaXe = new JComboBox<String>();
        cboMaXe.setPreferredSize(new Dimension(130, 26));
        cboMaXe.setFont(new Font("Tahoma", Font.PLAIN, 12));

        JLabel lblLocNgayMua = new JLabel("Ngày mua (dd/mm/yyyy):");
        lblLocNgayMua.setFont(new Font("Tahoma", Font.BOLD, 12));
        txtNgayMua = new JTextField(12);
        txtNgayMua.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtNgayMua.setToolTipText("Nhập ngày mua cụ thể (ví dụ 28/10/2011) hoặc để trống để xem tất cả ngày mua");

        btnThucHienBaoCao = new JButton("Thực hiện báo cáo");
        btnThucHienBaoCao.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnThucHienBaoCao.setForeground(new Color(18, 53, 126));

        btnDongBaoCao = new JButton("Đóng");
        btnDongBaoCao.setFont(new Font("Tahoma", Font.BOLD, 12));

        pnlFilter.add(lblLocMaXe);
        pnlFilter.add(cboMaXe);
        pnlFilter.add(lblLocNgayMua);
        pnlFilter.add(txtNgayMua);
        pnlFilter.add(btnThucHienBaoCao);
        pnlFilter.add(btnDongBaoCao);

        add(pnlFilter, BorderLayout.NORTH);

        // 2. Vùng hiển thị Báo cáo chuẩn Crystal Report ở Trung tâm
        JPanel pnlCrystalReport = new JPanel(new BorderLayout(8, 8));
        pnlCrystalReport.setBackground(Color.WHITE);
        pnlCrystalReport.setBorder(new CompoundBorder(
                new EmptyBorder(4, 12, 12, 12),
                new CompoundBorder(new LineBorder(new Color(180, 180, 180), 1), new EmptyBorder(14, 22, 14, 22))
        ));

        // 2.1. Phần đầu báo cáo (Header + Thông tin Xe)
        JPanel pnlReportHeader = new JPanel(new BorderLayout(4, 8));
        pnlReportHeader.setBackground(Color.WHITE);

        JPanel pnlTitles = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlTitles.setBackground(Color.WHITE);
        JLabel lblStore = new JLabel("Cửa hàng Mô tô PKL", SwingConstants.LEFT);
        lblStore.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
        JLabel lblMainReportTitle = new JLabel("THỐNG KÊ KHÁCH HÀNG ĐÃ MUA XE", SwingConstants.CENTER);
        lblMainReportTitle.setFont(new Font("Times New Roman", Font.BOLD, 24));
        pnlTitles.add(lblStore);
        pnlTitles.add(lblMainReportTitle);
        pnlReportHeader.add(pnlTitles, BorderLayout.NORTH);

        // Thông tin Xe được chọn: Mã xe, Tên xe, Đơn Giá, Dung tích
        JPanel pnlXeInfo = new JPanel(new GridBagLayout());
        pnlXeInfo.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font lblBold = new Font("Times New Roman", Font.BOLD, 15);
        Font valFont = new Font("Times New Roman", Font.BOLD, 15);
        Color valBlue = new Color(25, 55, 150);

        lblValMaXe = taoNhanGiaTriBaoCao(valFont, valBlue);
        lblValTenXe = taoNhanGiaTriBaoCao(valFont, valBlue);
        lblValDonGia = taoNhanGiaTriBaoCao(valFont, valBlue);
        lblValDungTich = taoNhanGiaTriBaoCao(valFont, valBlue);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlXeInfo.add(taoNhanTieuDe("Mã xe:", lblBold), gbc);
        gbc.gridx = 1; gbc.weightx = 0.45;
        pnlXeInfo.add(lblValMaXe, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlXeInfo.add(taoNhanTieuDe("Tên xe:", lblBold), gbc);
        gbc.gridx = 3; gbc.weightx = 0.55;
        pnlXeInfo.add(lblValTenXe, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlXeInfo.add(taoNhanTieuDe("Đơn Giá:", lblBold), gbc);
        gbc.gridx = 1; gbc.weightx = 0.45;
        pnlXeInfo.add(lblValDonGia, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlXeInfo.add(taoNhanTieuDe("Dung tích:", lblBold), gbc);
        gbc.gridx = 3; gbc.weightx = 0.55;
        pnlXeInfo.add(lblValDungTich, gbc);

        pnlReportHeader.add(pnlXeInfo, BorderLayout.CENTER);
        pnlCrystalReport.add(pnlReportHeader, BorderLayout.NORTH);

        // 2.2. Bảng báo cáo 5 cột: STT | Mã khách hàng | Ngày mua | Số lượng | Ghi chú
        reportTableModel = new DefaultTableModel(
                new String[]{"STT", "Mã khách hàng", "Ngày mua", "Số lượng", "Ghi chú"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBaoCao = new JTable(reportTableModel);
        tblBaoCao.setRowHeight(25);
        tblBaoCao.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        tblBaoCao.setGridColor(new Color(180, 120, 140));
        tblBaoCao.setShowGrid(true);

        JTableHeader header = tblBaoCao.getTableHeader();
        header.setPreferredSize(new Dimension(100, 28));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(new Color(82, 182, 72));
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new LineBorder(new Color(160, 110, 130), 1));
                lbl.setOpaque(true);
                return lbl;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblBaoCao.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblBaoCao.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblBaoCao.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tblBaoCao.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        tblBaoCao.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblBaoCao.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblBaoCao.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblBaoCao.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblBaoCao.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scrollReport = new JScrollPane(tblBaoCao);
        scrollReport.getViewport().setBackground(Color.WHITE);
        pnlCrystalReport.add(scrollReport, BorderLayout.CENTER);

        // 2.3. Chân trang Báo cáo (Số lượng khách hàng + Ngày tháng + Người lập)
        JPanel pnlFooter = new JPanel(new BorderLayout(4, 6));
        pnlFooter.setBackground(Color.WHITE);

        JPanel pnlCount = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        pnlCount.setBackground(Color.WHITE);
        JLabel lblCountTitle = new JLabel("Số lượng khách hàng:");
        lblCountTitle.setFont(lblBold);
        lblValSoLuongKH = new JLabel("0");
        lblValSoLuongKH.setFont(valFont);
        lblValSoLuongKH.setForeground(valBlue);
        lblValSoLuongKH.setBorder(new MatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        pnlCount.add(lblCountTitle);
        pnlCount.add(lblValSoLuongKH);
        pnlFooter.add(pnlCount, BorderLayout.NORTH);

        JPanel pnlSign = new JPanel(new GridLayout(4, 1, 0, 2));
        pnlSign.setBackground(Color.WHITE);
        pnlSign.setPreferredSize(new Dimension(280, 90));
        String ngayHienTai = new SimpleDateFormat("'TP.HCM, ngày' dd 'tháng' MM 'năm' yyyy").format(new Date());
        lblNgayLap = new JLabel(ngayHienTai, SwingConstants.CENTER);
        lblNgayLap.setFont(new Font("Times New Roman", Font.BOLD, 14));
        JLabel lblNguoiLap = new JLabel("Người lập", SwingConstants.CENTER);
        lblNguoiLap.setFont(new Font("Times New Roman", Font.BOLD, 14));
        JLabel lblBlank = new JLabel("", SwingConstants.CENTER);
        JLabel lblTenNguoiLap = new JLabel("Trần Mạnh Hùng (AT21N0129)", SwingConstants.CENTER);
        lblTenNguoiLap.setFont(new Font("Times New Roman", Font.BOLD, 14));
        pnlSign.add(lblNgayLap);
        pnlSign.add(lblNguoiLap);
        pnlSign.add(lblBlank);
        pnlSign.add(lblTenNguoiLap);

        JPanel pnlSignWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSignWrap.setBackground(Color.WHITE);
        pnlSignWrap.add(pnlSign);
        pnlFooter.add(pnlSignWrap, BorderLayout.CENTER);

        pnlCrystalReport.add(pnlFooter, BorderLayout.SOUTH);
        add(pnlCrystalReport, BorderLayout.CENTER);

        // Sự kiện các nút trên cửa sổ Báo cáo (đều hỏi Yes/No theo Ghi chú 1)
        btnThucHienBaoCao.addActionListener(e -> {
            if (xacNhan("Bạn có chắc chắn muốn thực hiện phát sinh báo cáo theo tiêu chí đã chọn hay không?")) {
                thucHienPhatSinhBaoCao(true);
            }
        });

        btnDongBaoCao.addActionListener(e -> {
            if (xacNhan("Bạn có chắc chắn muốn đóng Cửa sổ Báo cáo hay không?")) {
                dispose();
            }
        });
    }

    private static JLabel taoNhanTieuDe(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        return lbl;
    }

    private static JLabel taoNhanGiaTriBaoCao(Font font, Color color) {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        return lbl;
    }

    private boolean xacNhan(String message) {
        if (autoConfirmMode) {
            return true;
        }
        int ans = JOptionPane.showConfirmDialog(this, message, "Xác nhận thực thi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return ans == JOptionPane.YES_OPTION;
    }

    public void napDanhSachMaXe(String initialMaXe) {
        cboMaXe.removeAllItems();
        try {
            danhSachXe = repository.layDanhSachXe();
        } catch (SQLException e) {
            repository.khoiTaoDongDuLieuAo();
            try {
                danhSachXe = repository.layDanhSachXe();
            } catch (SQLException ignored) {
            }
        }
        for (Xe x : danhSachXe) {
            cboMaXe.addItem(x.getMaXe());
        }
        cboMaXe.addItem("-- Tất cả --");
        if (initialMaXe != null && !initialMaXe.trim().isEmpty()) {
            cboMaXe.setSelectedItem(initialMaXe.trim());
        } else if (cboMaXe.getItemCount() > 0) {
            cboMaXe.setSelectedIndex(0);
        }
    }

    public void thucHienPhatSinhBaoCao(boolean showCheckConnectionAlert) {
        if (!repository.kiemTraKetNoi()) {
            if (showCheckConnectionAlert && !autoConfirmMode) {
                JOptionPane.showMessageDialog(this,
                        "Kết nối Database Server không thành công!\nHệ thống sử dụng dòng dữ liệu ảo để hiển thị báo cáo.",
                        "Cảnh báo kết nối CSDL", JOptionPane.WARNING_MESSAGE);
            }
            repository.khoiTaoDongDuLieuAo();
        }
        String selectedMaXe = cboMaXe.getSelectedItem() == null ? "" : cboMaXe.getSelectedItem().toString().trim();
        String ngayMuaFilter = txtNgayMua.getText().trim();

        Xe xeInfo = null;
        for (Xe x : danhSachXe) {
            if (x.getMaXe().equalsIgnoreCase(selectedMaXe)) {
                xeInfo = x;
                break;
            }
        }
        if (xeInfo != null) {
            lblValMaXe.setText(xeInfo.getMaXe());
            lblValTenXe.setText(xeInfo.getTenXe());
            lblValDonGia.setText(Xe.dinhDangSo(xeInfo.getDonGia()) + " VNĐ");
            lblValDungTich.setText(String.valueOf(xeInfo.getDungTich()) + " cc");
        } else {
            lblValMaXe.setText("Tất cả mã xe");
            lblValTenXe.setText("Tất cả dòng xe");
            lblValDonGia.setText("---");
            lblValDungTich.setText("---");
        }

        reportTableModel.setRowCount(0);
        try {
            List<ChiTietMuaXe> rows = repository.locBaoCaoMuaXe(selectedMaXe, ngayMuaFilter);
            for (int i = 0; i < rows.size(); i++) {
                ChiTietMuaXe ct = rows.get(i);
                reportTableModel.addRow(new Object[]{
                        Integer.valueOf(i + 1),
                        ct.getMaKH(),
                        ct.getNgayMua(),
                        Integer.valueOf(ct.getSoLuong()),
                        ct.getGhiChu()
                });
            }
            lblValSoLuongKH.setText("  " + rows.size() + "  ");
        } catch (SQLException ex) {
            lblValSoLuongKH.setText("  0  ");
        }
    }

    // Các phương thức Getter chuẩn OOP phục vụ kiểm thử tự động
    public JComboBox<String> getCboMaXe() { return cboMaXe; }
    public JTextField getTxtNgayMua() { return txtNgayMua; }
    public JButton getBtnThucHienBaoCao() { return btnThucHienBaoCao; }
    public JTable getTblBaoCao() { return tblBaoCao; }
    public JLabel getLblValMaXe() { return lblValMaXe; }
    public JLabel getLblValTenXe() { return lblValTenXe; }
    public JLabel getLblValSoLuongKH() { return lblValSoLuongKH; }
}
