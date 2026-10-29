import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp đối tượng Sinh Viên.
 */
class SinhVien implements Serializable {
    private static final long serialVersionUID = 1L;

    private int mssv;
    private String hoTen;
    private double diemTB;

    public SinhVien() {}

    public SinhVien(int mssv, String hoTen, double diemTB) {
        this.mssv = mssv;
        this.hoTen = hoTen;
        this.diemTB = diemTB;
    }

    public int getMssv() {
        return mssv;
    }

    public void setMssv(int mssv) {
        this.mssv = mssv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public double getDiemTB() {
        return diemTB;
    }

    public void setDiemTB(double diemTB) {
        this.diemTB = diemTB;
    }
}

/**
 * Lớp quản lý Danh sách Sinh viên.
 */
class DanhSachSinhVien {
    private List<SinhVien> danhSach;

    public DanhSachSinhVien() {
        this.danhSach = new ArrayList<>();
    }

    public List<SinhVien> getDanhSach() {
        return danhSach;
    }

    public boolean themSinhVien(SinhVien sv) {
        if (timKiem(sv.getMssv()) != null) {
            return false; // Trùng MSSV
        }
        danhSach.add(sv);
        return true;
    }

    public boolean capNhatSinhVien(SinhVien svMoi) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMssv() == svMoi.getMssv()) {
                danhSach.set(i, svMoi);
                return true;
            }
        }
        return false;
    }

    public boolean xoaSinhVien(int mssv) {
        return danhSach.removeIf(sv -> sv.getMssv() == mssv);
    }

    public SinhVien timKiem(int mssv) {
        for (SinhVien sv : danhSach) {
            if (sv.getMssv() == mssv) {
                return sv;
            }
        }
        return null;
    }

    public void xoaTatCa() {
        danhSach.clear();
    }

    public void luuFile(String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"))) {
            for (SinhVien sv : danhSach) {
                bw.write(sv.getMssv() + "," + sv.getHoTen() + "," + sv.getDiemTB());
                bw.newLine();
            }
        }
    }

    public void docFile(String filePath) throws IOException {
        danhSach.clear();
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int mssv = Integer.parseInt(parts[0].trim());
                    String hoTen = parts[1].trim();
                    double diemTB = Double.parseDouble(parts[2].trim());
                    danhSach.add(new SinhVien(mssv, hoTen, diemTB));
                }
            }
        }
    }
}

/**
 * Giao diện Quản Lý Sinh Viên (Bài 3 - Lab 03).
 */
public class Bai03_QuanLySinhVien extends JFrame implements ActionListener {
    private JTextField txtMSSV, txtHoTen, txtDiemTB;
    private JButton btnThem, btnCapNhat, btnXoa, btnTaoMoi, btnLuuFile, btnDocFile, btnThoat;
    private JTable tableSinhVien;
    private DefaultTableModel tableModel;

    private DanhSachSinhVien dsSV;
    private final String FILE_PATH = "C:\\Users\\tmh23\\Desktop\\JAVALEARNING\\Lab_03\\sinhvien.txt";

    public Bai03_QuanLySinhVien() {
        setTitle("Bài 3: Chương Trình Quản Lý Sinh Viên");
        setSize(650, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dsSV = new DanhSachSinhVien();

        initUI();
        napDuLieuMau();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ DANH SÁCH SINH VIÊN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 102));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Form nhập thông tin
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Sinh Viên"));

        formPanel.add(new JLabel("Mã Số Sinh Viên (MSSV):"));
        txtMSSV = new JTextField();
        formPanel.add(txtMSSV);

        formPanel.add(new JLabel("Họ và Tên Sinh Viên:"));
        txtHoTen = new JTextField();
        formPanel.add(txtHoTen);

        formPanel.add(new JLabel("Điểm Trung Bình (0.0 - 10.0):"));
        txtDiemTB = new JTextField();
        formPanel.add(txtDiemTB);

        // Bảng danh sách JTable
        String[] columnNames = {"MSSV", "Họ và Tên", "Điểm Trung Bình"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSinhVien = new JTable(tableModel);
        tableSinhVien.setRowHeight(24);
        tableSinhVien.setFont(new Font("Arial", Font.PLAIN, 13));

        // Bắt sự kiện chọn dòng trong bảng
        tableSinhVien.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tableSinhVien.getSelectedRow();
            if (selectedRow >= 0) {
                txtMSSV.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtHoTen.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtDiemTB.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableSinhVien);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh Sách Sinh Viên"));

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel các nút thao tác
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));

        btnThem = new JButton("Thêm Mới");
        btnCapNhat = new JButton("Cập Nhật");
        btnXoa = new JButton("Xóa SV");
        btnTaoMoi = new JButton("Tạo Mới DS");
        btnLuuFile = new JButton("Lưu Tệp");
        btnDocFile = new JButton("Tải Tệp");
        btnThoat = new JButton("Thoát");

        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnTaoMoi.addActionListener(this);
        btnLuuFile.addActionListener(this);
        btnDocFile.addActionListener(this);
        btnThoat.addActionListener(this);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnTaoMoi);
        buttonPanel.add(btnLuuFile);
        buttonPanel.add(btnDocFile);
        buttonPanel.add(btnThoat);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void napDuLieuMau() {
        dsSV.themSinhVien(new SinhVien(101, "Nguyễn Văn An", 8.5));
        dsSV.themSinhVien(new SinhVien(102, "Trần Thị Bình", 7.8));
        dsSV.themSinhVien(new SinhVien(103, "Lê Hoàng Cường", 9.2));
        capNhatBang();
    }

    private void capNhatBang() {
        tableModel.setRowCount(0);
        for (SinhVien sv : dsSV.getDanhSach()) {
            tableModel.addRow(new Object[]{sv.getMssv(), sv.getHoTen(), sv.getDiemTB()});
        }
    }

    private void xoaForm() {
        txtMSSV.setText("");
        txtHoTen.setText("");
        txtDiemTB.setText("");
        txtMSSV.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnThoat) {
            System.exit(0);
            return;
        }

        if (src == btnTaoMoi) {
            dsSV.xoaTatCa();
            capNhatBang();
            xoaForm();
            JOptionPane.showMessageDialog(this, "Đã tạo mới danh sách sinh viên rỗng!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (src == btnLuuFile) {
            try {
                dsSV.luuFile(FILE_PATH);
                JOptionPane.showMessageDialog(this, "Đã lưu danh sách sinh viên vào tệp:\n" + FILE_PATH, "Lưu Tệp Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu tệp: " + ex.getMessage(), "Lỗi Ghi File", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        if (src == btnDocFile) {
            try {
                dsSV.docFile(FILE_PATH);
                capNhatBang();
                JOptionPane.showMessageDialog(this, "Đã tải danh sách sinh viên từ tệp thành công!", "Tải Tệp Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải tệp: " + ex.getMessage(), "Lỗi Đọc File", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        // Xử lý các thao tác Thêm, Cập nhật, Xóa
        String strMSSV = txtMSSV.getText().trim();
        if (strMSSV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập MSSV!", "Thiếu Thông Tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mssv;
        try {
            mssv = Integer.parseInt(strMSSV);
            if (mssv <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "MSSV phải là một số nguyên dương!", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (src == btnXoa) {
            if (dsSV.xoaSinhVien(mssv)) {
                capNhatBang();
                xoaForm();
                JOptionPane.showMessageDialog(this, "Đã xóa sinh viên có MSSV: " + mssv, "Xóa Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên có MSSV: " + mssv, "Lỗi Xóa SV", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        String hoTen = txtHoTen.getText().trim();
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ và Tên sinh viên!", "Thiếu Thông Tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String strDiem = txtDiemTB.getText().trim();
        double diemTB;
        try {
            diemTB = Double.parseDouble(strDiem);
            if (diemTB < 0.0 || diemTB > 10.0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Điểm trung bình phải từ 0.0 đến 10.0!", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SinhVien sv = new SinhVien(mssv, hoTen, diemTB);

        if (src == btnThem) {
            if (dsSV.themSinhVien(sv)) {
                capNhatBang();
                xoaForm();
                JOptionPane.showMessageDialog(this, "Thêm sinh viên mới thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "MSSV " + mssv + " đã tồn tại trong danh sách!", "Lỗi Trùng MSSV", JOptionPane.ERROR_MESSAGE);
            }
        } else if (src == btnCapNhat) {
            if (dsSV.capNhatSinhVien(sv)) {
                capNhatBang();
                xoaForm();
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin sinh viên thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên có MSSV " + mssv + " để cập nhật!", "Lỗi Cập Nhật", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void runAutomatedTests() {
        System.out.println("=================================================");
        System.out.println("  KIỂM THỬ TỰ ĐỘNG BÀI 3: QUẢN LÝ SINH VIÊN");
        System.out.println("=================================================");

        DanhSachSinhVien ds = new DanhSachSinhVien();

        // Test Thêm SV
        boolean them1 = ds.themSinhVien(new SinhVien(1, "Nguyễn Văn A", 8.0));
        System.out.printf("[Test 1] Thêm sinh viên MSSV 1 -> %s%n", them1 ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Trùng MSSV
        boolean themTrung = ds.themSinhVien(new SinhVien(1, "Trùng MSSV", 7.0));
        System.out.printf("[Test 2] Bắt lỗi trùng MSSV 1 -> %s%n", !themTrung ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Cập nhật SV
        boolean capNhat = ds.capNhatSinhVien(new SinhVien(1, "Nguyễn Văn A (Đã Sửa)", 9.5));
        SinhVien svDaSua = ds.timKiem(1);
        boolean checkSua = capNhat && svDaSua != null && svDaSua.getDiemTB() == 9.5;
        System.out.printf("[Test 3] Cập nhật sinh viên MSSV 1 -> %s%n", checkSua ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Ghi/Đọc tệp
        String testFile = "C:\\Users\\tmh23\\Desktop\\JAVALEARNING\\Lab_03\\test_sv.txt";
        boolean filePass = false;
        try {
            ds.themSinhVien(new SinhVien(2, "Trần Thị B", 6.5));
            ds.luuFile(testFile);

            DanhSachSinhVien dsDoc = new DanhSachSinhVien();
            dsDoc.docFile(testFile);

            filePass = dsDoc.getDanhSach().size() == 2 && dsDoc.timKiem(2) != null;
            new File(testFile).delete();
        } catch (Exception e) {
            filePass = false;
        }
        System.out.printf("[Test 4] Lưu và Tải tệp dữ liệu -> %s%n", filePass ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Xóa SV
        boolean xoa = ds.xoaSinhVien(1);
        System.out.printf("[Test 5] Xóa sinh viên MSSV 1 -> %s%n", xoa && ds.timKiem(1) == null ? "ĐẠT (PASS)" : "THẤT BẠI");

        System.out.println("=================================================");
        System.out.println("  HOÀN THÀNH KIỂM THỬ BÀI 3!");
        System.out.println("=================================================");
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            runAutomatedTests();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            new Bai03_QuanLySinhVien().setVisible(true);
        });
    }
}
