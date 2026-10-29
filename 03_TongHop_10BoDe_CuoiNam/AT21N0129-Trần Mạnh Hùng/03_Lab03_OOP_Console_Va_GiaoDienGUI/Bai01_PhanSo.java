import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lớp đại diện cho Phân số và các phép toán cơ bản.
 */
class PhanSo {
    private long tuSo;
    private long mauSo;

    public PhanSo() {
        this.tuSo = 0;
        this.mauSo = 1;
    }

    public PhanSo(long tuSo, long mauSo) {
        if (mauSo == 0) {
            throw new IllegalArgumentException("Mẫu số không thể bằng 0!");
        }
        this.tuSo = tuSo;
        this.mauSo = mauSo;
        rutGon();
    }

    public long getTuSo() {
        return tuSo;
    }

    public long getMauSo() {
        return mauSo;
    }

    private static long ucln(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a == 0 ? 1 : a;
    }

    public final void rutGon() {
        if (mauSo < 0) {
            tuSo = -tuSo;
            mauSo = -mauSo;
        }
        long u = ucln(tuSo, mauSo);
        tuSo /= u;
        mauSo /= u;
    }

    public PhanSo cong(PhanSo ps) {
        long tu = this.tuSo * ps.mauSo + ps.tuSo * this.mauSo;
        long mau = this.mauSo * ps.mauSo;
        return new PhanSo(tu, mau);
    }

    public PhanSo tru(PhanSo ps) {
        long tu = this.tuSo * ps.mauSo - ps.tuSo * this.mauSo;
        long mau = this.mauSo * ps.mauSo;
        return new PhanSo(tu, mau);
    }

    public PhanSo nhan(PhanSo ps) {
        long tu = this.tuSo * ps.tuSo;
        long mau = this.mauSo * ps.mauSo;
        return new PhanSo(tu, mau);
    }

    public PhanSo chia(PhanSo ps) {
        if (ps.tuSo == 0) {
            throw new ArithmeticException("Không thể chia cho phân số 0!");
        }
        long tu = this.tuSo * ps.mauSo;
        long mau = this.mauSo * ps.tuSo;
        return new PhanSo(tu, mau);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PhanSo)) return false;
        PhanSo other = (PhanSo) obj;
        return this.tuSo == other.tuSo && this.mauSo == other.mauSo;
    }

    @Override
    public String toString() {
        if (mauSo == 1) {
            return String.valueOf(tuSo);
        }
        return tuSo + "/" + mauSo;
    }
}

/**
 * Giao diện GUI tính toán Phân số (Bài 1 - Lab 03).
 */
public class Bai01_PhanSo extends JFrame implements ActionListener {
    private JTextField txtTu1, txtMau1, txtTu2, txtMau2, txtKetQua;
    private JButton btnCong, btnTru, btnNhan, btnChia, btnXoa, btnThoat;

    public Bai01_PhanSo() {
        setTitle("Bài 1: Phép Tính Phân Số (AWT / Swing GUI)");
        setSize(480, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tiêu đề
        JLabel lblTitle = new JLabel("CHƯƠNG TRÌNH TÍNH TOÁN PHÂN SỐ", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(0, 102, 204));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Grid nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));

        inputPanel.add(new JLabel("Tử số 1:"));
        txtTu1 = new JTextField();
        inputPanel.add(txtTu1);

        inputPanel.add(new JLabel("Mẫu số 1:"));
        txtMau1 = new JTextField();
        inputPanel.add(txtMau1);

        inputPanel.add(new JLabel("Tử số 2:"));
        txtTu2 = new JTextField();
        inputPanel.add(txtTu2);

        inputPanel.add(new JLabel("Mẫu số 2:"));
        txtMau2 = new JTextField();
        inputPanel.add(txtMau2);

        inputPanel.add(new JLabel("KẾT QUẢ:"));
        txtKetQua = new JTextField();
        txtKetQua.setEditable(false);
        txtKetQua.setFont(new Font("Arial", Font.BOLD, 14));
        txtKetQua.setForeground(Color.RED);
        inputPanel.add(txtKetQua);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnCong = new JButton("Cộng (+)");
        btnTru = new JButton("Trừ (-)");
        btnNhan = new JButton("Nhân (*)");
        btnChia = new JButton("Chia (/)");
        btnXoa = new JButton("Xóa Form");
        btnThoat = new JButton("Thoát");

        btnCong.addActionListener(this);
        btnTru.addActionListener(this);
        btnNhan.addActionListener(this);
        btnChia.addActionListener(this);
        btnXoa.addActionListener(this);
        btnThoat.addActionListener(this);

        buttonPanel.add(btnCong);
        buttonPanel.add(btnTru);
        buttonPanel.add(btnNhan);
        buttonPanel.add(btnChia);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnThoat);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnThoat) {
            System.exit(0);
            return;
        }

        if (source == btnXoa) {
            txtTu1.setText("");
            txtMau1.setText("");
            txtTu2.setText("");
            txtMau2.setText("");
            txtKetQua.setText("");
            txtTu1.requestFocus();
            return;
        }

        try {
            long tu1 = Long.parseLong(txtTu1.getText().trim());
            long mau1 = Long.parseLong(txtMau1.getText().trim());
            long tu2 = Long.parseLong(txtTu2.getText().trim());
            long mau2 = Long.parseLong(txtMau2.getText().trim());

            PhanSo ps1 = new PhanSo(tu1, mau1);
            PhanSo ps2 = new PhanSo(tu2, mau2);
            PhanSo ketQua = null;

            if (source == btnCong) {
                ketQua = ps1.cong(ps2);
            } else if (source == btnTru) {
                ketQua = ps1.tru(ps2);
            } else if (source == btnNhan) {
                ketQua = ps1.nhan(ps2);
            } else if (source == btnChia) {
                ketQua = ps1.chia(ps2);
            }

            if (ketQua != null) {
                txtKetQua.setText(ketQua.toString());
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ cho tử và mẫu số!", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | ArithmeticException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Tính Toán", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void runAutomatedTests() {
        System.out.println("=================================================");
        System.out.println("  KIỂM THỬ TỰ ĐỘNG BÀI 1: PHÉP TÍNH PHÂN SỐ");
        System.out.println("=================================================");

        PhanSo ps1 = new PhanSo(1, 2);
        PhanSo ps2 = new PhanSo(1, 3);

        // Test Cộng
        PhanSo cong = ps1.cong(ps2);
        System.out.printf("[Test 1] 1/2 + 1/3 = %s | Kỳ vọng: 5/6 -> %s%n",
                cong, cong.toString().equals("5/6") ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Trừ
        PhanSo tru = ps1.tru(ps2);
        System.out.printf("[Test 2] 1/2 - 1/3 = %s | Kỳ vọng: 1/6 -> %s%n",
                tru, tru.toString().equals("1/6") ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Nhân
        PhanSo nhan = ps1.nhan(ps2);
        System.out.printf("[Test 3] 1/2 * 1/3 = %s | Kỳ vọng: 1/6 -> %s%n",
                nhan, nhan.toString().equals("1/6") ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Chia
        PhanSo chia = ps1.chia(ps2);
        System.out.printf("[Test 4] 1/2 / 1/3 = %s | Kỳ vọng: 3/2 -> %s%n",
                chia, chia.toString().equals("3/2") ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Rút gọn
        PhanSo ps3 = new PhanSo(2, 4);
        PhanSo ps4 = new PhanSo(2, 4);
        PhanSo rutGon = ps3.cong(ps4);
        System.out.printf("[Test 5] 2/4 + 2/4 = %s | Kỳ vọng: 1 -> %s%n",
                rutGon, rutGon.toString().equals("1") ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Mẫu = 0
        boolean caughtZeroDenominator = false;
        try {
            new PhanSo(5, 0);
        } catch (IllegalArgumentException e) {
            caughtZeroDenominator = true;
        }
        System.out.printf("[Test 6] Bắt lỗi mẫu số 0 -> %s%n",
                caughtZeroDenominator ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test Chia cho 0
        boolean caughtDivideByZero = false;
        try {
            ps1.chia(new PhanSo(0, 5));
        } catch (ArithmeticException e) {
            caughtDivideByZero = true;
        }
        System.out.printf("[Test 7] Bắt lỗi chia cho phân số 0 -> %s%n",
                caughtDivideByZero ? "ĐẠT (PASS)" : "THẤT BẠI");

        System.out.println("=================================================");
        System.out.println("  HOÀN THÀNH KIỂM THỬ BÀI 1!");
        System.out.println("=================================================");
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            runAutomatedTests();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            new Bai01_PhanSo().setVisible(true);
        });
    }
}
