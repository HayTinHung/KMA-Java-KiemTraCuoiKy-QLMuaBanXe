import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Buổi 6 - Bài 2: Thiết kế Form "Đăng ký tài khoản" (Trang 8, đề bai tap giao dien 1.pdf).
 */
@SuppressWarnings("serial")
public final class Buoi06_Bai02_DangKyTaiKhoanGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    public static boolean isTestMode = false;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    JTextField txtTenDangNhap;
    JTextField txtEmail;
    JPasswordField txtMatKhau;
    JPasswordField txtXacNhanMatKhau;
    JButton btnDangKy;
    private String lastRegisteredSummary = "";

    public Buoi06_Bai02_DangKyTaiKhoanGUI() {
        super("Đăng ký tài khoản");
        initUI();
        initEvents();
    }

    private void initUI() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                xuLyDongForm();
            }
        });

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel lblHeader = new JLabel("Đăng ký tài khoản", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Times New Roman", Font.BOLD, 20));
        lblHeader.setForeground(new Color(0, 102, 204));
        main.add(lblHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTenDangNhap = new JTextField(18);
        txtEmail = new JTextField(18);
        txtMatKhau = new JPasswordField(18);
        txtXacNhanMatKhau = new JPasswordField(18);

        themDong(form, gbc, 0, "Tên đăng nhập", txtTenDangNhap, true);
        themDong(form, gbc, 1, "Địa chỉ email", txtEmail, true);
        themDong(form, gbc, 2, "Mật khẩu", txtMatKhau, true);
        themDong(form, gbc, 3, "Xác nhận mật khẩu", txtXacNhanMatKhau, false);

        main.add(form, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnDangKy = new JButton("Đăng ký");
        btnDangKy.setPreferredSize(new Dimension(160, 32));
        btnDangKy.setForeground(new Color(0, 102, 204));
        btnDangKy.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlBtn.add(btnDangKy);
        main.add(pnlBtn, BorderLayout.SOUTH);

        setContentPane(main);
        setSize(430, 265);
        setLocationRelativeTo(null);
    }

    private void themDong(JPanel panel, GridBagConstraints gbc, int row,
                          String labelText, JTextField field, boolean required) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.58;
        panel.add(field, gbc);

        gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.07;
        panel.add(new JLabel(required ? "(*)" : ""), gbc);
    }

    private void initEvents() {
        txtEmail.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String email = txtEmail.getText().trim();
                if (!email.isEmpty() && !laEmailHopLe(email) && !isTestMode) {
                    JOptionPane.showMessageDialog(Buoi06_Bai02_DangKyTaiKhoanGUI.this,
                            "Địa chỉ email không đúng định dạng (ví dụ: user@domain.com)!",
                            "Lỗi định dạng Email", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        txtXacNhanMatKhau.addActionListener(e -> xuLyDangKy());
        btnDangKy.addActionListener(e -> xuLyDangKy());
    }

    public static boolean laEmailHopLe(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public boolean xuLyDangKy() {
        String user = txtTenDangNhap.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = new String(txtMatKhau.getPassword());
        String confirm = new String(txtXacNhanMatKhau.getPassword());

        if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            if (!isTestMode) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ dữ liệu vào các trường bắt buộc có dấu (*)!",
                        "Thiếu thông tin bắt buộc", JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }
        if (!laEmailHopLe(email)) {
            if (!isTestMode) {
                JOptionPane.showMessageDialog(this,
                        "Địa chỉ email không hợp lệ!",
                        "Lỗi định dạng Email", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
        if (!pass.equals(confirm)) {
            if (!isTestMode) {
                JOptionPane.showMessageDialog(this,
                        "Xác nhận mật khẩu không khớp với mật khẩu đã nhập!",
                        "Lỗi mật khẩu", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }

        lastRegisteredSummary = String.format(
                "Tên đăng nhập: %s%nĐịa chỉ email: %s%nMật khẩu: %s%nXác nhận mật khẩu: %s",
                user, email, pass, confirm);
        if (!isTestMode) {
            JOptionPane.showMessageDialog(this, lastRegisteredSummary,
                    "Đăng ký thành công", JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }

    private void xuLyDongForm() {
        if (isTestMode) {
            dispose();
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đóng Form Đăng ký tài khoản không?",
                "Xác nhận thoát", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public String getLastRegisteredSummary() { return lastRegisteredSummary; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Buoi06_Bai02_DangKyTaiKhoanGUI().setVisible(true));
    }
}
