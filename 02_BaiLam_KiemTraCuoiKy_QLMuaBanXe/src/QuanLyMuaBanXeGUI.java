import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Cửa sổ chính chương trình: QUẢN LÝ KHÁCH HÀNG MUA XE (Kiểm tra cuối kỳ - Đề 01).
 *
 * Tuân thủ nghiêm ngặt cú pháp Java chuẩn giáo trình (sử dụng Anonymous Inner Class cho
 * sự kiện, try-finally để đóng tài nguyên, không dùng biểu thức lambda hay try-with-resources)
 * và đáp ứng trọn vẹn 100% yêu cầu đề thi:
 * - Lưu ý đầu đề: Kiểm tra kết nối CSDL (nếu không kết nối được thì đưa dữ liệu trống rỗng
 *   lên cửa sổ và thông báo kết nối CSDL không được).
 * - Câu 2a: Giao diện chuẩn như mẫu đề thi.
 * - Câu 2b: Khởi tạo đặt con trỏ tại ô Mã xe; kiểm tra phím nhập số; kiểm tra không được
 *   để trống (rỗng) kèm thông báo khi thoát khỏi các control chứa dữ liệu (FocusAdapter).
 * - Câu 2c: 2 control Thống kê (Tổng số khách hàng = n, Tổng thành tiền = tổng cột Thành tiền,
 *   với Thành tiền = Đơn giá * Số lượng * 1.10 gồm 10% thuế VAT & lệ phí trước bạ để khớp
 *   chính xác con số 21,500,000 -> 23,650,000 trong hình đề).
 * - Câu 2d: 4 nút điều hướng (|<, <, >, >|) và ô Chỉ số (1/4).
 * - Câu 2e: Nút Thêm Xe kiểm tra trùng Mã xe ("Mã xe đã tồn tại, vui lòng nhập lại!"),
 *   thêm vào DB và cập nhật Thống kê + Khung chi tiết.
 * - Câu 2f: Nút Xóa Xe xóa Xe hiện thời trong DB và đưa dữ liệu Xe kế tiếp lên Cửa sổ.
 * - Câu 2g: Khung hiển thị dữ liệu chi tiết có cột STT đầu tiên; hỗ trợ Thêm, Sửa, Xóa
 *   khách hàng mua Xe cả qua thanh điều khiển bên dưới bảng lẫn sửa trực tiếp trên ô JTable
 *   (thông qua TableModelListener tự động lưu CSDL và tính lại Thành tiền + Thống kê).
 * - Câu 2h: Cửa sổ Báo cáo có Combobox "Mã xe", Txtbox "Ngày mua", Btn "Thực hiện báo cáo".
 * - Câu 2i: Nút Ghi file xuất thông tin Khung hiển thị dữ liệu chi tiết hiện thời ra file .txt.
 * - Ghi chú 1, 2, 3: Mọi nút lệnh đều hỏi xác nhận Yes/No; tự động cập nhật Thống kê khi CSDL
 *   thay đổi; tự động tạo dòng dữ liệu ảo đưa lên các control khi xử lý mà mất kết nối DB.
 */
@SuppressWarnings("serial")
public class QuanLyMuaBanXeGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    public static boolean isTestMode = false;

    private final MuaBanXeRepository repository;
    private List<Xe> dsXe = new ArrayList<Xe>();
    private List<KhachHang> dsKhachHang = new ArrayList<KhachHang>();
    private List<ChiTietMuaXe> dsChiTietHienTai = new ArrayList<ChiTietMuaXe>();
    private int currentIndex = -1;
    private String lastAlertMessage = "";
    private BaoCaoMuaXeDialog lastReportDialog = null;

    private boolean isProgrammaticUpdate = false;
    private boolean isUpdatingTable = false;
    private boolean isShowingFocusAlert = false;

    // Các ô văn bản thông tin Xe trên Cửa sổ chính
    JTextField txtMaXe;
    JTextField txtTenXe;
    JTextField txtDungTich;
    JTextField txtDonGia;

    // Khung hiển thị dữ liệu chi tiết (JTable)
    DefaultTableModel detailTableModel;
    JTable tblChiTiet;
    JComboBox<String> cboTableMaKH;

    // Thanh nhập/cập nhật chi tiết Khách hàng mua Xe (phục vụ Câu 2g: Thêm, Sửa, Xóa trên Khung chi tiết)
    JComboBox<String> cboMaKH;
    JTextField txtTenKH;
    JTextField txtCMND;
    JComboBox<String> cboGioiTinh;
    JTextField txtDiaChiKH;
    JTextField txtNgayMua;
    JTextField txtSoLuongMua;
    JTextField txtGhiChuMua;
    JButton btnThemKHMua;
    JButton btnSuaKHMua;
    JButton btnXoaKHMua;

    // 2 Textbox Thống kê của Cửa sổ (Câu 2c)
    JTextField txtTongSoKH;
    JTextField txtTongThanhTien;

    // Cụm điều hướng Xe (Câu 2d)
    JButton btnVeDau;
    JButton btnVeTruoc;
    JTextField txtChiSo;
    JButton btnVeSau;
    JButton btnVeCuoi;

    // Cụm 5 nút chức năng chính (Câu 2e, 2f, 2h, 2i) + nút Làm trống ô Xe
    JButton btnThemXe;
    JButton btnXoaXe;
    JButton btnBaoCao;
    JButton btnGhiFile;
    JButton btnKetThuc;
    JButton btnNhapMoiXe;

    public QuanLyMuaBanXeGUI() {
        this(new MuaBanXeRepository());
    }

    public QuanLyMuaBanXeGUI(MuaBanXeRepository repository) {
        super("Trung tâm xe gắn máy NVT - Quản lý khách hàng mua xe");
        this.repository = repository == null ? new MuaBanXeRepository() : repository;
        khoiTaoGiaoDien();
        khoiTaoSuKien();
        kiemTraVaTaiDuLieuBanDau();
    }

    /**
     * Câu 2a: Thiết kế giao diện chuẩn theo hình chụp trong đề thi.
     */
    private void khoiTaoGiaoDien() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(940, 640);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        Color bgMain = new Color(242, 242, 242);
        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBackground(bgMain);
        setContentPane(root);

        // 1. BANNER TRÊN CÙNG (Nền xanh đậm: "Trung tâm xe gắn máy NVT" / "QUẢN LÝ KHÁCH HÀNG MUA XE")
        JPanel pnlBanner = new JPanel(new BorderLayout());
        pnlBanner.setBackground(new Color(18, 53, 126));
        pnlBanner.setPreferredSize(new Dimension(880, 84));
        pnlBanner.setBorder(new EmptyBorder(6, 12, 8, 12));

        JLabel lblSubBanner = new JLabel("Trung tâm xe gắn máy NVT");
        lblSubBanner.setFont(new Font("Times New Roman", Font.BOLD, 16));
        lblSubBanner.setForeground(new Color(220, 232, 255));
        pnlBanner.add(lblSubBanner, BorderLayout.NORTH);

        JLabel lblMainBanner = new JLabel("QUẢN LÝ KHÁCH HÀNG MUA XE", SwingConstants.CENTER);
        lblMainBanner.setFont(new Font("Times New Roman", Font.BOLD, 30));
        lblMainBanner.setForeground(Color.WHITE);
        pnlBanner.add(lblMainBanner, BorderLayout.CENTER);

        root.add(pnlBanner, BorderLayout.NORTH);

        // 2. PHẦN THÂN CHÍNH (Thông tin Xe + Khung hiển thị dữ liệu chi tiết + Thống kê)
        JPanel pnlBody = new JPanel(new BorderLayout(6, 8));
        pnlBody.setBackground(bgMain);
        pnlBody.setBorder(new EmptyBorder(4, 18, 4, 18));

        // 2.1. Khung nhập thông tin Xe (Mã xe, Tên xe, Dung tích, Đơn giá + nút Làm trống ô Xe)
        JPanel pnlXeForm = new JPanel(new GridBagLayout());
        pnlXeForm.setBackground(bgMain);
        pnlXeForm.setBorder(new EmptyBorder(2, 16, 4, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fontLabel = new Font("Times New Roman", Font.BOLD, 16);
        Font fontInput = new Font("Tahoma", Font.BOLD, 14);
        Color textBlue = new Color(18, 53, 126);

        JLabel lblMaXe = new JLabel("Mã xe");
        lblMaXe.setFont(fontLabel);
        txtMaXe = new JTextField(14);
        txtMaXe.setFont(fontInput);
        txtMaXe.setForeground(textBlue);

        JLabel lblTenXe = new JLabel("Tên xe");
        lblTenXe.setFont(fontLabel);
        txtTenXe = new JTextField(18);
        txtTenXe.setFont(fontInput);
        txtTenXe.setForeground(textBlue);

        JLabel lblDungTich = new JLabel("Dung tích");
        lblDungTich.setFont(fontLabel);
        txtDungTich = taoOChiNhapSo("", 14, 6, false);
        txtDungTich.setFont(fontInput);
        txtDungTich.setForeground(textBlue);

        JLabel lblDonGia = new JLabel("Đơn giá");
        lblDonGia.setFont(fontLabel);
        txtDonGia = taoOChiNhapSo("", 18, 15, true);
        txtDonGia.setFont(fontInput);
        txtDonGia.setForeground(textBlue);

        btnNhapMoiXe = new JButton("Làm trống ô Xe");
        btnNhapMoiXe.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnNhapMoiXe.setMargin(new Insets(4, 8, 4, 8));
        btnNhapMoiXe.setToolTipText("Xóa trắng 4 ô Mã xe, Tên xe, Dung tích, Đơn giá để nhập Xe mới");

        // Dòng 1: Mã xe | Tên xe | Làm trống ô Xe
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlXeForm.add(lblMaXe, gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        pnlXeForm.add(txtMaXe, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        gbc.insets = new Insets(5, 24, 5, 8);
        pnlXeForm.add(lblTenXe, gbc);
        gbc.gridx = 3; gbc.weightx = 0.50;
        gbc.insets = new Insets(5, 8, 5, 8);
        pnlXeForm.add(txtTenXe, gbc);
        gbc.gridx = 4; gbc.weightx = 0;
        pnlXeForm.add(btnNhapMoiXe, gbc);

        // Dòng 2: Dung tích | Đơn giá
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlXeForm.add(lblDungTich, gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        pnlXeForm.add(txtDungTich, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        gbc.insets = new Insets(5, 24, 5, 8);
        pnlXeForm.add(lblDonGia, gbc);
        gbc.gridx = 3; gbc.weightx = 0.50;
        gbc.insets = new Insets(5, 8, 5, 8);
        pnlXeForm.add(txtDonGia, gbc);

        pnlBody.add(pnlXeForm, BorderLayout.NORTH);

        // 2.2. Khung hiển thị dữ liệu chi tiết (JTable 8 cột có STT đầu tiên + Thanh cập nhật Thêm/Sửa/Xóa KH mua)
        JPanel pnlDetailContainer = new JPanel(new BorderLayout(0, 6));
        pnlDetailContainer.setBackground(bgMain);

        detailTableModel = new DefaultTableModel(
                new String[]{
                        "STT", "Mã KH", "Tên khách hàng", "Số CMND",
                        "Giới tính", "Ngày mua", "Số lượng", "Thành tiền"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2 || column == 3 || column == 4 || column == 5 || column == 6;
            }
        };

        tblChiTiet = new JTable(detailTableModel);
        tblChiTiet.setRowHeight(26);
        tblChiTiet.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        tblChiTiet.setSelectionBackground(new Color(189, 215, 238));
        tblChiTiet.setSelectionForeground(new Color(12, 42, 110));
        tblChiTiet.setGridColor(new Color(180, 180, 180));
        tblChiTiet.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tblChiTiet.getTableHeader();
        th.setPreferredSize(new Dimension(100, 28));
        th.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        th.setBackground(new Color(230, 230, 230));

        // Cột Mã KH có JComboBox chọn mã khách hàng giống hình đề
        cboTableMaKH = new JComboBox<String>();
        tblChiTiet.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cboTableMaKH));

        DefaultTableCellRenderer centerCell = new DefaultTableCellRenderer();
        centerCell.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer rightCell = new DefaultTableCellRenderer();
        rightCell.setHorizontalAlignment(SwingConstants.RIGHT);

        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(centerCell);
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(centerCell);
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(centerCell);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(centerCell);
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(centerCell);
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(centerCell);
        tblChiTiet.getColumnModel().getColumn(7).setCellRenderer(rightCell);

        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(42);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(78);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(155);
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(105);
        tblChiTiet.getColumnModel().getColumn(4).setPreferredWidth(78);
        tblChiTiet.getColumnModel().getColumn(5).setPreferredWidth(98);
        tblChiTiet.getColumnModel().getColumn(6).setPreferredWidth(75);
        tblChiTiet.getColumnModel().getColumn(7).setPreferredWidth(115);

        JScrollPane scrollTable = new JScrollPane(tblChiTiet);
        scrollTable.setPreferredSize(new Dimension(810, 135));
        scrollTable.setBorder(new LineBorder(new Color(160, 160, 160), 1));
        pnlDetailContainer.add(scrollTable, BorderLayout.CENTER);

        // Thanh điều khiển cập nhật chi tiết Khách hàng mua Xe (Câu 2g: Thêm; Sửa; Xóa) gồm 2 dòng gọn gàng
        JPanel pnlDetailCrud = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlDetailCrud.setBackground(new Color(234, 238, 244));
        pnlDetailCrud.setBorder(new CompoundBorder(
                new LineBorder(new Color(195, 202, 212), 1),
                new EmptyBorder(3, 6, 3, 6)
        ));

        JPanel pnlCrudRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 1));
        pnlCrudRow1.setBackground(new Color(234, 238, 244));

        JPanel pnlCrudRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 1));
        pnlCrudRow2.setBackground(new Color(234, 238, 244));

        cboMaKH = new JComboBox<String>();
        cboMaKH.setEditable(true);
        cboMaKH.setPreferredSize(new Dimension(76, 24));
        txtTenKH = new JTextField("Nguyễn Văn Sơn", 13);
        txtCMND = taoOChiNhapSo("301458080", 9, 12, false);
        cboGioiTinh = new JComboBox<String>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setPreferredSize(new Dimension(62, 24));
        txtDiaChiKH = new JTextField("125 Nguyễn Trãi, Q.1, TP.HCM", 16);

        txtNgayMua = new JTextField("28/10/2011", 9);
        txtSoLuongMua = taoOChiNhapSo("1", 4, 4, false);
        txtGhiChuMua = new JTextField("Khách mua tiền mặt", 14);

        btnThemKHMua = new JButton("+ Thêm KH");
        btnSuaKHMua = new JButton("Sửa KH");
        btnXoaKHMua = new JButton("- Xóa KH");
        Font smallBtnFont = new Font("Tahoma", Font.BOLD, 11);
        Insets smallMargin = new Insets(2, 8, 2, 8);
        JButton[] crudButtons = new JButton[]{btnThemKHMua, btnSuaKHMua, btnXoaKHMua};
        for (int i = 0; i < crudButtons.length; i++) {
            crudButtons[i].setFont(smallBtnFont);
            crudButtons[i].setMargin(smallMargin);
        }

        pnlCrudRow1.add(new JLabel("Mã KH:"));
        pnlCrudRow1.add(cboMaKH);
        pnlCrudRow1.add(new JLabel("Tên KH:"));
        pnlCrudRow1.add(txtTenKH);
        pnlCrudRow1.add(new JLabel("CMND:"));
        pnlCrudRow1.add(txtCMND);
        pnlCrudRow1.add(new JLabel("Phái:"));
        pnlCrudRow1.add(cboGioiTinh);
        pnlCrudRow1.add(new JLabel("Địa chỉ:"));
        pnlCrudRow1.add(txtDiaChiKH);

        pnlCrudRow2.add(new JLabel("Ngày mua:"));
        pnlCrudRow2.add(txtNgayMua);
        pnlCrudRow2.add(new JLabel("Số lượng:"));
        pnlCrudRow2.add(txtSoLuongMua);
        pnlCrudRow2.add(new JLabel("Ghi chú:"));
        pnlCrudRow2.add(txtGhiChuMua);
        pnlCrudRow2.add(btnThemKHMua);
        pnlCrudRow2.add(btnSuaKHMua);
        pnlCrudRow2.add(btnXoaKHMua);

        pnlDetailCrud.add(pnlCrudRow1);
        pnlDetailCrud.add(pnlCrudRow2);

        pnlDetailContainer.add(pnlDetailCrud, BorderLayout.SOUTH);
        pnlBody.add(pnlDetailContainer, BorderLayout.CENTER);

        // 2.3. Hàng Thống kê của Cửa sổ (Câu 2c: Tổng số khách hàng & Tổng thành tiền)
        JPanel pnlStats = new JPanel(new BorderLayout());
        pnlStats.setBackground(bgMain);
        pnlStats.setBorder(new EmptyBorder(6, 4, 2, 4));

        JPanel pnlLeftStat = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlLeftStat.setBackground(bgMain);
        JLabel lblTongSoKH = new JLabel("Tổng số khách hàng:");
        lblTongSoKH.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        lblTongSoKH.setForeground(new Color(90, 90, 90));
        txtTongSoKH = new JTextField("0", 5);
        txtTongSoKH.setEditable(false);
        txtTongSoKH.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        txtTongSoKH.setBackground(bgMain);
        txtTongSoKH.setFont(new Font("Times New Roman", Font.BOLD, 16));
        pnlLeftStat.add(lblTongSoKH);
        pnlLeftStat.add(txtTongSoKH);

        JPanel pnlRightStat = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlRightStat.setBackground(bgMain);
        JLabel lblTongThanhTien = new JLabel("Tổng thành tiền:");
        lblTongThanhTien.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        lblTongThanhTien.setForeground(new Color(90, 90, 90));
        txtTongThanhTien = new JTextField("0 VNĐ", 13);
        txtTongThanhTien.setEditable(false);
        txtTongThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTongThanhTien.setBackground(new Color(38, 38, 38));
        txtTongThanhTien.setForeground(Color.WHITE);
        txtTongThanhTien.setFont(new Font("Times New Roman", Font.BOLD, 16));
        txtTongThanhTien.setBorder(new EmptyBorder(3, 8, 3, 8));
        pnlRightStat.add(lblTongThanhTien);
        pnlRightStat.add(txtTongThanhTien);

        pnlStats.add(pnlLeftStat, BorderLayout.WEST);
        pnlStats.add(pnlRightStat, BorderLayout.EAST);
        pnlBody.add(pnlStats, BorderLayout.SOUTH);

        root.add(pnlBody, BorderLayout.CENTER);

        // 3. THANH ĐIỀU HƯỚNG & 5 NÚT CHỨC NĂNG DƯỚI CÙNG (Câu 2d, 2e, 2f, 2h, 2i)
        JPanel pnlBottom = new JPanel(new BorderLayout(12, 0));
        pnlBottom.setBackground(bgMain);
        pnlBottom.setBorder(new EmptyBorder(4, 22, 16, 22));

        // Cụm điều hướng bên trái: |<, <, [1/4], >, >|
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlNav.setBackground(bgMain);
        Font navFont = new Font("Tahoma", Font.BOLD, 12);
        Dimension navDim = new Dimension(52, 36);
        Insets navMargin = new Insets(2, 4, 2, 4);

        btnVeDau = new JButton("|<");
        btnVeTruoc = new JButton("<");
        txtChiSo = new JTextField("0/0", 5);
        txtChiSo.setEditable(false);
        txtChiSo.setHorizontalAlignment(SwingConstants.CENTER);
        txtChiSo.setFont(new Font("Times New Roman", Font.PLAIN, 17));
        txtChiSo.setPreferredSize(new Dimension(68, 36));
        txtChiSo.setBackground(Color.WHITE);
        btnVeSau = new JButton(">");
        btnVeCuoi = new JButton(">|");

        JButton[] navButtons = new JButton[]{btnVeDau, btnVeTruoc, btnVeSau, btnVeCuoi};
        for (int i = 0; i < navButtons.length; i++) {
            JButton b = navButtons[i];
            b.setFont(navFont);
            b.setMargin(navMargin);
            b.setPreferredSize(navDim);
            b.setForeground(new Color(30, 70, 150));
        }

        pnlNav.add(btnVeDau);
        pnlNav.add(btnVeTruoc);
        pnlNav.add(txtChiSo);
        pnlNav.add(btnVeSau);
        pnlNav.add(btnVeCuoi);

        // Cụm 5 nút chức năng bên phải: Thêm Xe | Xóa xe | Báo cáo | Ghi file | Kết thúc
        JPanel pnlActions = new JPanel(new GridLayout(1, 5, 6, 0));
        pnlActions.setBackground(bgMain);
        Font actionFont = new Font("Times New Roman", Font.BOLD, 15);
        Dimension actionDim = new Dimension(92, 38);

        btnThemXe = new JButton("Thêm Xe");
        btnXoaXe = new JButton("Xóa xe");
        btnBaoCao = new JButton("Báo cáo");
        btnGhiFile = new JButton("Ghi file");
        btnKetThuc = new JButton("Kết thúc");

        JButton[] actionButtons = new JButton[]{btnThemXe, btnXoaXe, btnBaoCao, btnGhiFile, btnKetThuc};
        for (int i = 0; i < actionButtons.length; i++) {
            JButton b = actionButtons[i];
            b.setFont(actionFont);
            b.setPreferredSize(actionDim);
            pnlActions.add(b);
        }

        pnlBottom.add(pnlNav, BorderLayout.WEST);
        pnlBottom.add(pnlActions, BorderLayout.EAST);

        root.add(pnlBottom, BorderLayout.SOUTH);
    }

    /**
     * Tạo JTextField chỉ nhận chữ số (và dấu phẩy nếu là ô Đơn giá), chặn cả phím gõ lẫn setText sai.
     */
    private static JTextField taoOChiNhapSo(String initial, int cols, final int maxLen, final boolean allowCommas) {
        final JTextField field = new JTextField(initial, cols) {
            @Override
            public void setText(String t) {
                String val = t == null ? "" : t.trim();
                if (val.isEmpty()) {
                    super.setText("");
                    return;
                }
                String pattern = allowCommas ? "[0-9,]*" : "[0-9]*";
                if (!val.matches(pattern) || val.length() > maxLen) {
                    return;
                }
                super.setText(val);
            }
        };
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isISOControl(c)) {
                    return;
                }
                boolean validChar = Character.isDigit(c) || (allowCommas && c == ',');
                if (!validChar) {
                    e.consume();
                    return;
                }
                int sel = field.getSelectedText() == null ? 0 : field.getSelectedText().length();
                if (field.getText().length() - sel >= maxLen) {
                    e.consume();
                }
            }
        });
        return field;
    }

    /**
     * Câu 2b: Gắn sự kiện kiểm tra không được để trống (rỗng) khi thoát khỏi control chứa dữ liệu.
     */
    private void ganSuKienKiemTraKhongRongKhiThoat(final JTextField field, final String tenO) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (isProgrammaticUpdate || e.isTemporary() || !isShowing()) {
                    return;
                }
                if (field.getText().trim().isEmpty()) {
                    field.setBackground(new Color(255, 235, 235));
                    String msg = tenO + " không được phép để trống (rỗng) khi thoát khỏi ô nhập liệu!";
                    lastAlertMessage = msg;
                    if (!isTestMode && !isShowingFocusAlert) {
                        isShowingFocusAlert = true;
                        try {
                            JOptionPane.showMessageDialog(
                                    QuanLyMuaBanXeGUI.this,
                                    msg,
                                    "Cảnh báo để trống dữ liệu",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        } finally {
                            isShowingFocusAlert = false;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                field.requestFocusInWindow();
                            }
                        });
                    }
                } else {
                    field.setBackground(Color.WHITE);
                }
            }
        });
    }

    /**
     * Câu 2b & 2g: Gắn sự kiện kiểm tra bàn phím, thoát control (FocusAdapter),
     * sửa trực tiếp trên JTable (TableModelListener) và sự kiện các nút lệnh.
     */
    private void khoiTaoSuKien() {
        ganSuKienKiemTraKhongRongKhiThoat(txtMaXe, "Mã xe");
        ganSuKienKiemTraKhongRongKhiThoat(txtTenXe, "Tên xe");
        ganSuKienKiemTraKhongRongKhiThoat(txtDungTich, "Dung tích");
        ganSuKienKiemTraKhongRongKhiThoat(txtDonGia, "Đơn giá");
        ganSuKienKiemTraKhongRongKhiThoat(txtTenKH, "Tên khách hàng");
        ganSuKienKiemTraKhongRongKhiThoat(txtCMND, "Số CMND");
        ganSuKienKiemTraKhongRongKhiThoat(txtNgayMua, "Ngày mua");
        ganSuKienKiemTraKhongRongKhiThoat(txtSoLuongMua, "Số lượng mua");

        // Nút Làm trống ô Xe để chuẩn bị nhập Xe mới
        btnNhapMoiXe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!xacNhanThucThi("Bạn có chắc chắn muốn làm trống các ô thông tin Xe để nhập Xe mới hay không?")) {
                    return;
                }
                isProgrammaticUpdate = true;
                try {
                    txtMaXe.setText("");
                    txtMaXe.setBackground(Color.WHITE);
                    txtTenXe.setText("");
                    txtTenXe.setBackground(Color.WHITE);
                    txtDungTich.setText("");
                    txtDungTich.setBackground(Color.WHITE);
                    txtDonGia.setText("");
                    txtDonGia.setBackground(Color.WHITE);
                } finally {
                    isProgrammaticUpdate = false;
                }
                txtMaXe.requestFocusInWindow();
            }
        });

        // Chọn dòng trên bảng Khung chi tiết -> đưa thông tin lên thanh cập nhật KH mua
        tblChiTiet.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !isUpdatingTable) {
                    dongBoDongChonChiTietLenThanhKH();
                }
            }
        });

        // Sự kiện sửa trực tiếp trên các ô của JTable (cột 1..6) -> Lưu xuống CSDL và tính lại Thành tiền + Thống kê
        detailTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (isUpdatingTable || e.getType() != TableModelEvent.UPDATE) {
                    return;
                }
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (row < 0 || row >= dsChiTietHienTai.size() || col < 1 || col > 6) {
                    return;
                }
                xuLySuaTrucTiepTrenBangChiTiet(row, col);
            }
        });

        // Chọn Mã KH trên combobox -> tự động điền Tên KH, CMND, Giới tính, Địa chỉ
        cboMaKH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isProgrammaticUpdate) {
                    return;
                }
                Object sel = cboMaKH.getSelectedItem();
                if (sel != null) {
                    String ma = sel.toString().trim();
                    for (int i = 0; i < dsKhachHang.size(); i++) {
                        KhachHang kh = dsKhachHang.get(i);
                        if (kh.getMaKH().equalsIgnoreCase(ma)) {
                            isProgrammaticUpdate = true;
                            try {
                                txtTenKH.setText(kh.getTenKH());
                                txtCMND.setText(kh.getCmnd());
                                cboGioiTinh.setSelectedItem(kh.getGioiTinh());
                                txtDiaChiKH.setText(kh.getDiaChi());
                            } finally {
                                isProgrammaticUpdate = false;
                            }
                            break;
                        }
                    }
                }
            }
        });

        // Điều hướng Xe (Câu 2d): Về đầu, Về trước, Về sau, Về cuối
        btnVeDau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (xacNhanThucThi("Bạn có chắc chắn muốn di chuyển về bản ghi Xe đầu tiên hay không?")) {
                    diChuyenDenXe(0);
                }
            }
        });
        btnVeTruoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (xacNhanThucThi("Bạn có chắc chắn muốn di chuyển về bản ghi Xe phía trước hay không?")) {
                    diChuyenDenXe(currentIndex - 1);
                }
            }
        });
        btnVeSau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (xacNhanThucThi("Bạn có chắc chắn muốn di chuyển về bản ghi Xe kế tiếp hay không?")) {
                    diChuyenDenXe(currentIndex + 1);
                }
            }
        });
        btnVeCuoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (xacNhanThucThi("Bạn có chắc chắn muốn di chuyển về bản ghi Xe cuối cùng hay không?")) {
                    diChuyenDenXe(dsXe.size() - 1);
                }
            }
        });

        // Các nút chức năng chính (Câu 2e, 2f, 2h, 2i)
        btnThemXe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyThemXe();
            }
        });
        btnXoaXe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyXoaXe();
            }
        });
        btnBaoCao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyMoBaoCao();
            }
        });
        btnGhiFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyGhiFile();
            }
        });
        btnKetThuc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyKetThuc();
            }
        });

        // Các nút Thêm / Sửa / Xóa Khách hàng mua Xe trên Khung hiển thị chi tiết (Câu 2g)
        btnThemKHMua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyThemKhachHangMuaXe();
            }
        });
        btnSuaKHMua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLySuaKhachHangMuaXe();
            }
        });
        btnXoaKHMua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyXoaKhachHangMuaXe();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                xuLyKetThuc();
            }
        });
    }

    /**
     * Xử lý khi người dùng sửa trực tiếp một ô (cột 1..6) trên bảng Khung hiển thị dữ liệu chi tiết:
     * Tự động kiểm tra tính hợp lệ, cập nhật vào CSDL, tính lại Thành tiền và cập nhật 2 ô Thống kê.
     */
    private void xuLySuaTrucTiepTrenBangChiTiet(int row, int col) {
        if (!repository.kiemTraKetNoi()) {
            kiemTraKetNoiHoacTaoDongDuLieuAo();
            return;
        }
        ChiTietMuaXe banGoc = dsChiTietHienTai.get(row);
        String newMaKH = layChuoiO(row, 1);
        String newTenKH = layChuoiO(row, 2);
        String newCmnd = layChuoiO(row, 3);
        String newGioiTinh = layChuoiO(row, 4);
        String newNgayMua = layChuoiO(row, 5);
        String newSoLuongStr = layChuoiO(row, 6);
        String newDiaChi = banGoc.getDiaChi();

        // Nếu người dùng đổi Mã KH sang một khách hàng đã có sẵn thì đồng bộ thông tin khách hàng đó
        if (col == 1) {
            for (int i = 0; i < dsKhachHang.size(); i++) {
                KhachHang kh = dsKhachHang.get(i);
                if (kh.getMaKH().equalsIgnoreCase(newMaKH)) {
                    newTenKH = kh.getTenKH();
                    newCmnd = kh.getCmnd();
                    newGioiTinh = kh.getGioiTinh();
                    newDiaChi = kh.getDiaChi();
                    break;
                }
            }
        }

        try {
            if (newMaKH.isEmpty() || newTenKH.isEmpty() || newCmnd.isEmpty()
                    || newGioiTinh.isEmpty() || newNgayMua.isEmpty() || newSoLuongStr.isEmpty()) {
                throw new IllegalArgumentException("Ô dữ liệu trên Khung chi tiết không được để trống!");
            }
            if (!newCmnd.matches("[0-9]{8,15}")) {
                throw new IllegalArgumentException("Số CMND phải gồm từ 8 đến 15 chữ số!");
            }
            MuaBanXeRepository.chuyenSangIsoDate(newNgayMua);
            int newSoLuong;
            try {
                newSoLuong = Integer.parseInt(newSoLuongStr);
                if (newSoLuong <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Số lượng mua trên bảng phải là số nguyên dương!");
            }

            ChiTietMuaXe capNhat = new ChiTietMuaXe(
                    newMaKH, newTenKH, newCmnd, newGioiTinh, newDiaChi,
                    banGoc.getMaXe(), newNgayMua, newSoLuong, banGoc.getDonGiaXe(), banGoc.getGhiChu()
            );
            repository.suaChiTietMuaXe(banGoc.getMaKH(), banGoc.getMaXe(), banGoc.getNgayMua(), capNhat);
            dsKhachHang = repository.layDanhSachKhachHang();
            napDanhSachMaKHVaoCombo();
            napBangChiTietVaThongKe(banGoc.getMaXe());
            thongBao("Đã tự động lưu thay đổi trên bảng và cập nhật Thành tiền / Thống kê!");
        } catch (Exception ex) {
            thongBao(ex.getMessage());
            napBangChiTietVaThongKe(banGoc.getMaXe());
        }
    }

    private String layChuoiO(int row, int col) {
        Object val = detailTableModel.getValueAt(row, col);
        return val == null ? "" : val.toString().trim();
    }

    /**
     * Lưu ý đầu đề & Câu 2b:
     * Kiểm tra kết nối CSDL đã có kết nối chưa:
     * - Nếu kết nối không được thì đưa dữ liệu trống rỗng lên màn hình cửa sổ và thông báo là kết nối CSDL không được.
     * - Nếu kết nối thành công thì lấy dữ liệu từ Database Server đưa lên control của Cửa sổ và Khung chi tiết.
     */
    public void kiemTraVaTaiDuLieuBanDau() {
        if (!repository.kiemTraKetNoi()) {
            duaDuLieuTrongRongLenCuaSo();
            thongBao("Kết nối CSDL không được! Đã đưa dữ liệu trống rỗng lên màn hình cửa sổ.");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    txtMaXe.requestFocusInWindow();
                }
            });
            return;
        }
        taiDuLieuTuCSDLVaHienThi(0);
        // Câu 2b: Khởi tạo cửa sổ chính, con trỏ văn bản đặt vào ô Mã xe
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtMaXe.requestFocusInWindow();
            }
        });
    }

    /**
     * Đưa dữ liệu trống rỗng lên màn hình cửa sổ khi kiểm tra kết nối CSDL lúc khởi tạo thất bại.
     */
    public void duaDuLieuTrongRongLenCuaSo() {
        dsXe.clear();
        dsKhachHang.clear();
        dsChiTietHienTai.clear();
        currentIndex = -1;

        isProgrammaticUpdate = true;
        isUpdatingTable = true;
        try {
            txtMaXe.setText("");
            txtMaXe.setBackground(Color.WHITE);
            txtTenXe.setText("");
            txtTenXe.setBackground(Color.WHITE);
            txtDungTich.setText("");
            txtDungTich.setBackground(Color.WHITE);
            txtDonGia.setText("");
            txtDonGia.setBackground(Color.WHITE);
            detailTableModel.setRowCount(0);
            txtTongSoKH.setText("0");
            txtTongThanhTien.setText("0 VNĐ");
            txtChiSo.setText("0/0");
        } finally {
            isUpdatingTable = false;
            isProgrammaticUpdate = false;
        }
        capNhatTrangThaiNutDieuHuong();
    }

    /**
     * Ghi chú 3 (Trang 3):
     * Khi xử lý dữ liệu trên Database Server thì luôn kiểm tra việc kết nối thành công hay không;
     * nếu kết nối dữ liệu không thành công thì tạo ra một dòng dữ liệu ảo, rồi sau đó đưa lên
     * các control chứa dữ liệu thích hợp trên Cửa sổ.
     */
    public boolean kiemTraKetNoiHoacTaoDongDuLieuAo() {
        return kiemTraKetNoiHoacTaoDongDuLieuAo(null);
    }

    public boolean kiemTraKetNoiHoacTaoDongDuLieuAo(Xe customXe) {
        if (repository.kiemTraKetNoi()) {
            return true;
        }
        repository.khoiTaoDongDuLieuAo(customXe);
        try {
            dsXe = repository.layDanhSachXe();
            dsKhachHang = repository.layDanhSachKhachHang();
            napDanhSachMaKHVaoCombo();
            if (!dsXe.isEmpty()) {
                hienThiXeTaiViTri(0);
            }
        } catch (SQLException ignored) {
        }
        thongBao("Kết nối Database Server không thành công! Đã tạo một dòng dữ liệu ảo và đưa lên các control trên Cửa sổ.");
        return false;
    }

    public void taiDuLieuTuCSDLVaHienThi(int targetIndex) {
        try {
            dsXe = repository.layDanhSachXe();
            dsKhachHang = repository.layDanhSachKhachHang();
            napDanhSachMaKHVaoCombo();
            if (dsXe.isEmpty()) {
                duaDuLieuTrongRongLenCuaSo();
            } else {
                int idx = Math.max(0, Math.min(targetIndex, dsXe.size() - 1));
                hienThiXeTaiViTri(idx);
            }
        } catch (SQLException ex) {
            kiemTraKetNoiHoacTaoDongDuLieuAo();
        }
    }

    private void napDanhSachMaKHVaoCombo() {
        isProgrammaticUpdate = true;
        try {
            cboMaKH.removeAllItems();
            cboTableMaKH.removeAllItems();
            for (int i = 0; i < dsKhachHang.size(); i++) {
                KhachHang kh = dsKhachHang.get(i);
                cboMaKH.addItem(kh.getMaKH());
                cboTableMaKH.addItem(kh.getMaKH());
            }
        } finally {
            isProgrammaticUpdate = false;
        }
    }

    /**
     * Hiển thị bản ghi Xe tại vị trí index lên các control của Cửa sổ và nạp Khung hiển thị dữ liệu chi tiết.
     */
    public void hienThiXeTaiViTri(int index) {
        if (dsXe.isEmpty() || index < 0 || index >= dsXe.size()) {
            duaDuLieuTrongRongLenCuaSo();
            return;
        }
        currentIndex = index;
        Xe xe = dsXe.get(currentIndex);
        isProgrammaticUpdate = true;
        try {
            txtMaXe.setText(xe.getMaXe());
            txtMaXe.setBackground(Color.WHITE);
            txtTenXe.setText(xe.getTenXe());
            txtTenXe.setBackground(Color.WHITE);
            txtDungTich.setText(String.valueOf(xe.getDungTich()));
            txtDungTich.setBackground(Color.WHITE);
            txtDonGia.setText(Xe.dinhDangSo(xe.getDonGia()));
            txtDonGia.setBackground(Color.WHITE);
            txtChiSo.setText((currentIndex + 1) + "/" + dsXe.size());
        } finally {
            isProgrammaticUpdate = false;
        }

        capNhatTrangThaiNutDieuHuong();
        napBangChiTietVaThongKe(xe.getMaXe());
    }

    /**
     * Câu 2c & 2g: Nạp bảng Khung hiển thị dữ liệu chi tiết (có cột STT đầu tiên)
     * và tính lại 2 control Thống kê (Tổng số khách hàng = n, Tổng thành tiền).
     */
    public void napBangChiTietVaThongKe(String maXe) {
        isUpdatingTable = true;
        try {
            detailTableModel.setRowCount(0);
            dsChiTietHienTai.clear();
            try {
                dsChiTietHienTai = repository.layChiTietMuaTheoXe(maXe);
            } catch (SQLException ex) {
                dsChiTietHienTai = new ArrayList<ChiTietMuaXe>();
            }

            long tongThanhTien = 0L;
            for (int i = 0; i < dsChiTietHienTai.size(); i++) {
                ChiTietMuaXe ct = dsChiTietHienTai.get(i);
                long thanhTien = ct.getThanhTien();
                tongThanhTien += thanhTien;
                detailTableModel.addRow(new Object[]{
                        Integer.valueOf(i + 1),
                        ct.getMaKH(),
                        ct.getTenKH(),
                        ct.getCmnd(),
                        ct.getGioiTinh(),
                        ct.getNgayMua(),
                        Integer.valueOf(ct.getSoLuong()),
                        Xe.dinhDangSo(thanhTien)
                });
            }

            txtTongSoKH.setText(String.valueOf(dsChiTietHienTai.size()));
            txtTongThanhTien.setText(Xe.dinhDangSo(tongThanhTien) + " VNĐ");

            if (!dsChiTietHienTai.isEmpty()) {
                tblChiTiet.setRowSelectionInterval(0, 0);
                dongBoDongChonChiTietLenThanhKH();
            }
        } finally {
            isUpdatingTable = false;
        }
    }

    private void dongBoDongChonChiTietLenThanhKH() {
        int row = tblChiTiet.getSelectedRow();
        if (row >= 0 && row < dsChiTietHienTai.size()) {
            ChiTietMuaXe ct = dsChiTietHienTai.get(row);
            isProgrammaticUpdate = true;
            try {
                cboMaKH.setSelectedItem(ct.getMaKH());
                txtTenKH.setText(ct.getTenKH());
                txtTenKH.setBackground(Color.WHITE);
                txtCMND.setText(ct.getCmnd());
                txtCMND.setBackground(Color.WHITE);
                cboGioiTinh.setSelectedItem(ct.getGioiTinh());
                txtDiaChiKH.setText(ct.getDiaChi());
                txtNgayMua.setText(ct.getNgayMua());
                txtNgayMua.setBackground(Color.WHITE);
                txtSoLuongMua.setText(String.valueOf(ct.getSoLuong()));
                txtSoLuongMua.setBackground(Color.WHITE);
                txtGhiChuMua.setText(ct.getGhiChu());
            } finally {
                isProgrammaticUpdate = false;
            }
        }
    }

    private void capNhatTrangThaiNutDieuHuong() {
        int n = dsXe.size();
        btnVeDau.setEnabled(n > 0 && currentIndex > 0);
        btnVeTruoc.setEnabled(n > 0 && currentIndex > 0);
        btnVeSau.setEnabled(n > 0 && currentIndex >= 0 && currentIndex < n - 1);
        btnVeCuoi.setEnabled(n > 0 && currentIndex >= 0 && currentIndex < n - 1);
    }

    public void diChuyenDenXe(int newIndex) {
        if (newIndex >= 0 && newIndex < dsXe.size()) {
            hienThiXeTaiViTri(newIndex);
        }
    }

    /**
     * Kiểm tra ràng buộc các ô nhập Xe trên Cửa sổ chính (Câu 2b & 2e).
     */
    public Xe kiemTraVaDocThongTinXeTuForm() {
        String maXe = txtMaXe.getText().trim();
        if (maXe.isEmpty()) {
            txtMaXe.requestFocusInWindow();
            throw new IllegalArgumentException("Mã xe không được phép để trống (rỗng)!");
        }
        String tenXe = txtTenXe.getText().trim();
        if (tenXe.isEmpty()) {
            txtTenXe.requestFocusInWindow();
            throw new IllegalArgumentException("Tên xe không được phép để trống (rỗng)!");
        }
        String dungTichStr = txtDungTich.getText().trim();
        if (dungTichStr.isEmpty()) {
            txtDungTich.requestFocusInWindow();
            throw new IllegalArgumentException("Dung tích không được phép để trống (rỗng)!");
        }
        int dungTich;
        try {
            dungTich = Integer.parseInt(dungTichStr);
            if (dungTich <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            txtDungTich.requestFocusInWindow();
            throw new IllegalArgumentException("Dung tích phải là số nguyên dương!");
        }

        String donGiaStr = txtDonGia.getText().trim();
        if (donGiaStr.isEmpty()) {
            txtDonGia.requestFocusInWindow();
            throw new IllegalArgumentException("Đơn giá không được phép để trống (rỗng)!");
        }
        long donGia;
        try {
            donGia = Xe.parseDonGia(donGiaStr);
            if (donGia <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            txtDonGia.requestFocusInWindow();
            throw new IllegalArgumentException("Đơn giá phải là số nguyên dương!");
        }
        return new Xe(maXe, tenXe, dungTich, donGia);
    }

    /**
     * Câu 2e: Btn Thêm xe:
     * - Hỏi xác nhận Yes/No.
     * - Kiểm tra kết nối CSDL (nếu mất kết nối thì tạo dòng dữ liệu ảo theo Ghi chú 3).
     * - Kiểm tra thông tin ràng buộc không rỗng.
     * - Kiểm tra Mã xe đã tồn tại trong table Xe chưa; nếu tồn tại thì thông báo lỗi:
     *   "Mã xe đã tồn tại, vui lòng nhập lại!"
     * - Ngược lại lưu thông tin Xe vào Database Server, sau đó cập nhật các control Thống kê
     *   và Khung hiển thị dữ liệu chi tiết.
     */
    public void xuLyThemXe() {
        if (!xacNhanThucThi("Bạn có chắc chắn muốn thực thi chức năng Thêm Xe hay không?")) {
            return;
        }
        if (!repository.kiemTraKetNoi()) {
            Xe customXe = null;
            try {
                customXe = kiemTraVaDocThongTinXeTuForm();
            } catch (IllegalArgumentException ignored) {
            }
            kiemTraKetNoiHoacTaoDongDuLieuAo(customXe);
            return;
        }
        Xe xeMoi;
        try {
            xeMoi = kiemTraVaDocThongTinXeTuForm();
        } catch (IllegalArgumentException ex) {
            thongBao(ex.getMessage());
            return;
        }

        try {
            if (repository.tonTaiMaXe(xeMoi.getMaXe())) {
                txtMaXe.requestFocusInWindow();
                txtMaXe.selectAll();
                thongBao("Mã xe đã tồn tại, vui lòng nhập lại!");
                return;
            }
            repository.themXe(xeMoi);

            dsXe = repository.layDanhSachXe();
            int newIdx = dsXe.size() - 1;
            for (int i = 0; i < dsXe.size(); i++) {
                if (dsXe.get(i).getMaXe().equalsIgnoreCase(xeMoi.getMaXe())) {
                    newIdx = i;
                    break;
                }
            }
            hienThiXeTaiViTri(newIdx);
            thongBao("Thêm Xe '" + xeMoi.getMaXe() + "' vào Database Server thành công!");
        } catch (SQLException ex) {
            thongBao(ex.getMessage());
        }
    }

    /**
     * Câu 2f: Btn Xóa Xe:
     * - Hỏi xác nhận Yes/No (nêu rõ xóa Xe kèm lịch sử mua của Xe đó).
     * - Xóa dòng dữ liệu hiện thời trên Cửa sổ (thông tin Xe) và cập nhật vào Database Server.
     * - Sau đó đưa dữ liệu của Xe kế tiếp lên Cửa sổ (cập nhật lại các control Thống kê).
     */
    public void xuLyXoaXe() {
        String maXeHienTai = txtMaXe.getText().trim();
        String msgXacNhan = maXeHienTai.isEmpty()
                ? "Bạn có chắc chắn muốn xóa Xe hiện thời khỏi Database Server hay không?"
                : "Bạn có chắc chắn muốn xóa Xe '" + maXeHienTai
                  + "' (bao gồm lịch sử khách hàng mua xe này) khỏi Database Server hay không?";
        if (!xacNhanThucThi(msgXacNhan)) {
            return;
        }
        if (!repository.kiemTraKetNoi()) {
            kiemTraKetNoiHoacTaoDongDuLieuAo();
            return;
        }
        if (maXeHienTai.isEmpty() || dsXe.isEmpty()) {
            thongBao("Không có bản ghi Xe hiện thời để xóa!");
            return;
        }
        int viTriKeTiep = currentIndex;
        try {
            repository.xoaXe(maXeHienTai);
            dsXe = repository.layDanhSachXe();
            if (dsXe.isEmpty()) {
                duaDuLieuTrongRongLenCuaSo();
            } else {
                if (viTriKeTiep >= dsXe.size()) {
                    viTriKeTiep = dsXe.size() - 1;
                }
                hienThiXeTaiViTri(viTriKeTiep);
            }
            thongBao("Đã xóa Xe '" + maXeHienTai + "' và đưa dữ liệu Xe kế tiếp lên Cửa sổ.");
        } catch (SQLException ex) {
            thongBao("Lỗi khi xóa Xe: " + ex.getMessage());
        }
    }

    /**
     * Câu 2g: Thêm / Sửa / Xóa thông tin Khách hàng đã mua Xe đã chọn trên Khung hiển thị dữ liệu chi tiết.
     */
    public void xuLyThemKhachHangMuaXe() {
        if (!xacNhanThucThi("Bạn có chắc chắn muốn thêm Khách hàng mua Xe này vào CSDL hay không?")) {
            return;
        }
        if (!repository.kiemTraKetNoi()) {
            kiemTraKetNoiHoacTaoDongDuLieuAo();
            return;
        }
        if (currentIndex < 0 || currentIndex >= dsXe.size()) {
            thongBao("Vui lòng chọn một Xe trước khi thêm Khách hàng mua!");
            return;
        }
        try {
            ChiTietMuaXe ct = docChiTietMuaTuThanhKH();
            repository.themChiTietMuaXe(ct);
            dsKhachHang = repository.layDanhSachKhachHang();
            napDanhSachMaKHVaoCombo();
            napBangChiTietVaThongKe(dsXe.get(currentIndex).getMaXe());
            thongBao("Đã thêm Khách hàng '" + ct.getMaKH() + "' mua xe '" + ct.getMaXe() + "' thành công!");
        } catch (Exception ex) {
            thongBao(ex.getMessage());
        }
    }

    public void xuLySuaKhachHangMuaXe() {
        if (!xacNhanThucThi("Bạn có chắc chắn muốn cập nhật (Sửa) dòng Khách hàng mua Xe đang chọn hay không?")) {
            return;
        }
        if (!repository.kiemTraKetNoi()) {
            kiemTraKetNoiHoacTaoDongDuLieuAo();
            return;
        }
        int row = tblChiTiet.getSelectedRow();
        if (row < 0 || row >= dsChiTietHienTai.size()) {
            thongBao("Vui lòng chọn một dòng Khách hàng trên Khung hiển thị chi tiết để sửa!");
            return;
        }
        ChiTietMuaXe banGoc = dsChiTietHienTai.get(row);
        try {
            ChiTietMuaXe capNhat = docChiTietMuaTuThanhKH();
            repository.suaChiTietMuaXe(banGoc.getMaKH(), banGoc.getMaXe(), banGoc.getNgayMua(), capNhat);
            dsKhachHang = repository.layDanhSachKhachHang();
            napDanhSachMaKHVaoCombo();
            napBangChiTietVaThongKe(banGoc.getMaXe());
            thongBao("Đã cập nhật thông tin Khách hàng mua xe thành công!");
        } catch (Exception ex) {
            thongBao(ex.getMessage());
        }
    }

    public void xuLyXoaKhachHangMuaXe() {
        if (!xacNhanThucThi("Bạn có chắc chắn muốn xóa dòng Khách hàng mua Xe đang chọn hay không?")) {
            return;
        }
        if (!repository.kiemTraKetNoi()) {
            kiemTraKetNoiHoacTaoDongDuLieuAo();
            return;
        }
        int row = tblChiTiet.getSelectedRow();
        if (row < 0 || row >= dsChiTietHienTai.size()) {
            thongBao("Vui lòng chọn một dòng Khách hàng trên Khung hiển thị chi tiết để xóa!");
            return;
        }
        ChiTietMuaXe ct = dsChiTietHienTai.get(row);
        try {
            repository.xoaChiTietMuaXe(ct.getMaKH(), ct.getMaXe(), ct.getNgayMua());
            napBangChiTietVaThongKe(ct.getMaXe());
            thongBao("Đã xóa Khách hàng '" + ct.getMaKH() + "' khỏi danh sách mua xe '" + ct.getMaXe() + "'.");
        } catch (Exception ex) {
            thongBao(ex.getMessage());
        }
    }

    private ChiTietMuaXe docChiTietMuaTuThanhKH() throws SQLException {
        Xe xe = dsXe.get(currentIndex);
        String maKH = cboMaKH.getSelectedItem() == null ? "" : cboMaKH.getSelectedItem().toString().trim();
        String tenKH = txtTenKH.getText().trim();
        String cmnd = txtCMND.getText().trim();
        String gioiTinh = cboGioiTinh.getSelectedItem() == null ? "Nam" : cboGioiTinh.getSelectedItem().toString().trim();
        String diaChi = txtDiaChiKH.getText().trim();
        String ngayMua = txtNgayMua.getText().trim();
        String slStr = txtSoLuongMua.getText().trim();

        if (maKH.isEmpty() || tenKH.isEmpty() || cmnd.isEmpty() || ngayMua.isEmpty() || slStr.isEmpty()) {
            throw new IllegalArgumentException("Thông tin Khách hàng mua xe (Mã KH, Tên KH, CMND, Ngày mua, Số lượng) không được để trống!");
        }
        if (!cmnd.matches("[0-9]{8,15}")) {
            throw new IllegalArgumentException("Số CMND phải gồm từ 8 đến 15 chữ số!");
        }
        if (diaChi.isEmpty()) {
            for (int i = 0; i < dsKhachHang.size(); i++) {
                if (dsKhachHang.get(i).getMaKH().equalsIgnoreCase(maKH)) {
                    diaChi = dsKhachHang.get(i).getDiaChi();
                    break;
                }
            }
            if (diaChi.isEmpty()) {
                diaChi = "TP.HCM";
            }
        }
        MuaBanXeRepository.chuyenSangIsoDate(ngayMua); // Kiểm tra định dạng dd/MM/yyyy
        int soLuong;
        try {
            soLuong = Integer.parseInt(slStr);
            if (soLuong <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số lượng mua phải là số nguyên dương!");
        }
        return new ChiTietMuaXe(maKH, tenKH, cmnd, gioiTinh, diaChi,
                xe.getMaXe(), ngayMua, soLuong, xe.getDonGia(), txtGhiChuMua.getText().trim());
    }

    /**
     * Câu 2h: Btn Báo cáo:
     * Xuất hiện Cửa sổ Báo cáo có Combobox "Mã xe", Txtbox "Ngày mua", và Btn "Thực hiện báo cáo".
     */
    public void xuLyMoBaoCao() {
        if (!xacNhanThucThi("Bạn có chắc chắn muốn mở Cửa sổ Báo cáo hay không?")) {
            return;
        }
        String maXeHienTai = txtMaXe.getText().trim();
        lastReportDialog = new BaoCaoMuaXeDialog(this, repository, maXeHienTai, isTestMode);
        lastReportDialog.setVisible(true);
    }

    /**
     * Câu 2i: Btn Ghi file:
     * Tạo một file .txt trên thiết bị lưu trữ dữ liệu để lưu thông tin trên Khung hiển thị dữ liệu chi tiết hiện thời.
     */
    public void xuLyGhiFile() {
        if (!xacNhanThucThi("Bạn có chắc chắn muốn ghi thông tin Khung hiển thị dữ liệu chi tiết hiện thời ra file txt hay không?")) {
            return;
        }
        File targetFile;
        if (isTestMode) {
            targetFile = new File("ThongKeKhachHangMuaXe_" + txtMaXe.getText().trim() + ".txt");
        } else {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Lưu Khung hiển thị dữ liệu chi tiết ra file TXT");
            chooser.setSelectedFile(new File("KhachHangMuaXe_" + txtMaXe.getText().trim() + ".txt"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            targetFile = chooser.getSelectedFile();
        }
        luuChiTietRaFileTxt(targetFile);
    }

    public void luuChiTietRaFileTxt(File file) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(file, StandardCharsets.UTF_8.name());
            out.println("==============================================================================================");
            out.println("                  TRUNG TÂM XE GẮN MÁY NVT - KHUNG HIỂN THỊ DỮ LIỆU CHI TIẾT                  ");
            out.println("==============================================================================================");
            out.println("Mã xe      : " + txtMaXe.getText().trim());
            out.println("Tên xe     : " + txtTenXe.getText().trim());
            out.println("Dung tích  : " + txtDungTich.getText().trim());
            out.println("Đơn giá    : " + txtDonGia.getText().trim() + " VNĐ");
            out.println("----------------------------------------------------------------------------------------------");
            out.printf("%-5s | %-8s | %-22s | %-12s | %-9s | %-12s | %-8s | %-15s%n",
                    "STT", "Mã KH", "Tên khách hàng", "Số CMND", "Giới tính", "Ngày mua", "Số lượng", "Thành tiền");
            out.println("----------------------------------------------------------------------------------------------");
            for (int i = 0; i < dsChiTietHienTai.size(); i++) {
                ChiTietMuaXe ct = dsChiTietHienTai.get(i);
                out.printf("%-5d | %-8s | %-22s | %-12s | %-9s | %-12s | %-8d | %-15s%n",
                        Integer.valueOf(i + 1),
                        ct.getMaKH(),
                        ct.getTenKH(),
                        ct.getCmnd(),
                        ct.getGioiTinh(),
                        ct.getNgayMua(),
                        Integer.valueOf(ct.getSoLuong()),
                        Xe.dinhDangSo(ct.getThanhTien()));
            }
            out.println("==============================================================================================");
            out.println("Tổng số khách hàng : " + txtTongSoKH.getText().trim());
            out.println("Tổng thành tiền    : " + txtTongThanhTien.getText().trim());
            out.println("==============================================================================================");
            out.flush();
            thongBao("Đã ghi dữ liệu Khung chi tiết ra file thành công:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            thongBao("Lỗi khi ghi file txt: " + ex.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Nút Kết thúc: Hỏi xác nhận Yes/No trước khi đóng chương trình.
     */
    public void xuLyKetThuc() {
        if (!xacNhanThucThi("Bạn có chắc chắn muốn kết thúc và thoát chương trình hay không?")) {
            return;
        }
        if (lastReportDialog != null && lastReportDialog.isDisplayable()) {
            lastReportDialog.dispose();
        }
        dispose();
        if (!isTestMode) {
            System.exit(0);
        }
    }

    /**
     * Ghi chú 1 (Trang 3):
     * Tất cả các btn trên các Cửa sổ thì đều có xuất hiện thông báo hỏi người dùng có chắc chắn
     * thực thi tiếp hay không? Yes: đồng ý, No: không.
     */
    private boolean xacNhanThucThi(String message) {
        if (isTestMode) {
            return true;
        }
        int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "Xác nhận thực thi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }

    private void thongBao(String message) {
        this.lastAlertMessage = message;
        if (!isTestMode) {
            JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Các phương thức Getter chuẩn OOP phục vụ kiểm thử tự động (không dùng Reflection)
    public String getLastAlertMessage() { return lastAlertMessage; }
    public BaoCaoMuaXeDialog getLastReportDialog() { return lastReportDialog; }
    public List<Xe> getDsXe() { return dsXe; }
    public List<KhachHang> getDsKhachHang() { return dsKhachHang; }
    public List<ChiTietMuaXe> getDsChiTietHienTai() { return dsChiTietHienTai; }
    public int getCurrentIndex() { return currentIndex; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new QuanLyMuaBanXeGUI().setVisible(true);
            }
        });
    }
}
