import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Buổi 6 - Bài 1: Form "Cộng trừ nhân chia" (Trang 7, đề bai tap giao dien 1.pdf).
 *
 * Sử dụng 100% kiến thức chuẩn trong tài liệu trường (giao dien Java 2-66.pdf & 66-87.pdf):
 * - Kiểm tra tính hợp lệ theo 2 mức độ:
 *   + Mức 1: Dùng ErrorProvider (!) thông báo lỗi khi dữ liệu a, b không hợp lệ.
 *   + Mức 2: Dùng KeyAdapter (keyTyped -> e.consume()) chặn người dùng gõ ký tự khác số vào a, b.
 * - Thông báo lỗi bằng MessageBox (JOptionPane) nếu dữ liệu không phù hợp (ví dụ chia cho 0).
 * - Hỏi xác nhận Yes/No trước khi đóng Form.
 */
@SuppressWarnings("serial")
public final class Buoi06_Bai01_CongTruNhanChiaGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    public static boolean isTestMode = false;

    JTextField txtA;
    JTextField txtB;
    JTextField txtKetQua;
    JLabel errA;
    JLabel errB;
    JButton btnCong;
    JButton btnTru;
    JButton btnNhan;
    JButton btnChia;

    public Buoi06_Bai01_CongTruNhanChiaGUI() {
        super("Cộng trừ nhân chia");
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

        JPanel main = new JPanel(new BorderLayout(8, 10));
        main.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtA = new JTextField(10);
        txtB = new JTextField(10);
        txtKetQua = new JTextField(24);
        txtKetQua.setEditable(false);
        txtKetQua.setBackground(Color.WHITE);

        errA = taoErrorBadge();
        errB = taoErrorBadge();

        // Cài đặt Mức 2 bằng KeyAdapter (chuẩn tài liệu giao dien Java 66-87.pdf)
        caiDatChanKyTuKhacSo(txtA);
        caiDatChanKyTuKhacSo(txtB);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.08;
        form.add(new JLabel("a ="), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.38;
        form.add(txtA, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.04;
        form.add(errA, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.08;
        form.add(new JLabel("   b="), gbc);
        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0.38;
        form.add(txtB, gbc);
        gbc.gridx = 5; gbc.gridy = 0; gbc.weightx = 0.04;
        form.add(errB, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        form.add(new JLabel("Kết quả"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 5;
        form.add(txtKetQua, gbc);

        main.add(form, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        btnCong = new JButton("+");
        btnTru = new JButton("-");
        btnNhan = new JButton("x");
        btnChia = new JButton("/");
        for (JButton b : new JButton[]{btnCong, btnTru, btnNhan, btnChia}) {
            b.setPreferredSize(new Dimension(68, 30));
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            pnlBtns.add(b);
        }
        main.add(pnlBtns, BorderLayout.SOUTH);

        setContentPane(main);
        setSize(420, 180);
        setLocationRelativeTo(null);
    }

    private JLabel taoErrorBadge() {
        JLabel lbl = new JLabel("!");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(new Color(210, 30, 30));
        lbl.setOpaque(true);
        lbl.setHorizontalAlignment(JLabel.CENTER);
        lbl.setPreferredSize(new Dimension(16, 16));
        lbl.setVisible(false);
        return lbl;
    }

    /**
     * Mức 2: Dùng KeyAdapter.keyTyped chặn ký tự không phải chữ số, dấu '-' đầu hoặc dấu '.'.
     */
    private void caiDatChanKyTuKhacSo(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isISOControl(c)) return;
                String current = field.getText();
                if (Character.isDigit(c)) return;
                if (c == '-' && current.isEmpty()) return;
                if (c == '.' && !current.contains(".")) return;
                e.consume();
            }
        });
    }

    private void initEvents() {
        btnCong.addActionListener(e -> thucHienPhepToan('+'));
        btnTru.addActionListener(e -> thucHienPhepToan('-'));
        btnNhan.addActionListener(e -> thucHienPhepToan('*'));
        btnChia.addActionListener(e -> thucHienPhepToan('/'));
    }

    public void thucHienPhepToan(char op) {
        String strA = txtA.getText().trim();
        String strB = txtB.getText().trim();

        boolean okA = true, okB = true;
        double a = 0, b = 0;
        try {
            a = Double.parseDouble(strA);
        } catch (NumberFormatException ex) {
            okA = false;
        }
        try {
            b = Double.parseDouble(strB);
        } catch (NumberFormatException ex) {
            okB = false;
        }

        errA.setVisible(!okA);
        errB.setVisible(!okB);

        if (!okA || !okB) {
            txtKetQua.setText("");
            if (!isTestMode) {
                JOptionPane.showMessageDialog(this,
                        "Dữ liệu nhập vào ô a hoặc b chưa hợp lệ!",
                        "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        if (op == '/' && Math.abs(b) < 1e-12) {
            errB.setVisible(true);
            txtKetQua.setText("");
            if (!isTestMode) {
                JOptionPane.showMessageDialog(this,
                        "Không thể thực hiện phép chia cho 0!",
                        "Lỗi toán học", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        double res;
        switch (op) {
            case '+': res = a + b; break;
            case '-': res = a - b; break;
            case '*': res = a * b; break;
            case '/': res = a / b; break;
            default: res = 0;
        }
        txtKetQua.setText(new DecimalFormat("#,##0.####").format(res));
    }

    private void xuLyDongForm() {
        if (isTestMode) {
            dispose();
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đóng Form Cộng trừ nhân chia không?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Buoi06_Bai01_CongTruNhanChiaGUI().setVisible(true));
    }
}
