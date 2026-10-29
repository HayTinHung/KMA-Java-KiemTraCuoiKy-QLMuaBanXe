import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * Lớp hỗ trợ quy đổi giữa Độ C và Độ F.
 */
class NhietDo {
    public static double cToF(double c) {
        return c * 9.0 / 5.0 + 32.0;
    }

    public static double fToC(double f) {
        return (f - 32.0) * 5.0 / 9.0;
    }
}

/**
 * Giao diện GUI Chuyển đổi Nhiệt Độ C <-> F (Bài 2 - Lab 03).
 */
public class Bai02_NhietDo extends JFrame implements ActionListener {
    private JTextField txtDoC, txtDoF;
    private JButton btnCtoF, btnFtoC, btnXoa, btnThoat;
    private DecimalFormat df = new DecimalFormat("#,##0.00");

    public Bai02_NhietDo() {
        setTitle("Bài 2: Chuyển Đổi Nhiệt Độ (Độ C <-> Độ F)");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tiêu đề
        JLabel lblTitle = new JLabel("CHƯƠNG TRÌNH CHUYỂN ĐỔI NHIỆT ĐỘ", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(new Color(204, 102, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Grid nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 15));

        JLabel lblC = new JLabel("Nhiệt độ (Độ C):");
        lblC.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDoC = new JTextField();
        txtDoC.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblF = new JLabel("Nhiệt độ (Độ F):");
        lblF.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDoF = new JTextField();
        txtDoF.setFont(new Font("Arial", Font.BOLD, 14));

        inputPanel.add(lblC);
        inputPanel.add(txtDoC);
        inputPanel.add(lblF);
        inputPanel.add(txtDoF);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));

        btnCtoF = new JButton("Độ C -> Độ F");
        btnFtoC = new JButton("Độ F -> Độ C");
        btnXoa = new JButton("Xóa Form");
        btnThoat = new JButton("Thoát");

        btnCtoF.addActionListener(this);
        btnFtoC.addActionListener(this);
        btnXoa.addActionListener(this);
        btnThoat.addActionListener(this);

        buttonPanel.add(btnCtoF);
        buttonPanel.add(btnFtoC);
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
            txtDoC.setText("");
            txtDoF.setText("");
            txtDoC.requestFocus();
            return;
        }

        if (source == btnCtoF) {
            String valC = txtDoC.getText().trim();
            if (valC.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị Độ C!", "Lỗi Trống Dữ Liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double c = Double.parseDouble(valC);
                double f = NhietDo.cToF(c);
                txtDoF.setText(df.format(f));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Giá trị Độ C phải là một số thực hợp lệ!", "Lỗi Định Dạng Số", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == btnFtoC) {
            String valF = txtDoF.getText().trim();
            if (valF.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị Độ F!", "Lỗi Trống Dữ Liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double f = Double.parseDouble(valF);
                double c = NhietDo.fToC(f);
                txtDoC.setText(df.format(c));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Giá trị Độ F phải là một số thực hợp lệ!", "Lỗi Định Dạng Số", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void runAutomatedTests() {
        System.out.println("=================================================");
        System.out.println("  KIỂM THỬ TỰ ĐỘNG BÀI 2: CHUYỂN ĐỔI NHIỆT ĐỘ");
        System.out.println("=================================================");

        // Test C -> F (0 C = 32 F)
        double f0 = NhietDo.cToF(0);
        System.out.printf("[Test 1] 0°C -> °F = %.2f | Kỳ vọng: 32.00 -> %s%n",
                f0, Math.abs(f0 - 32.0) < 0.001 ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test C -> F (100 C = 212 F)
        double f100 = NhietDo.cToF(100);
        System.out.printf("[Test 2] 100°C -> °F = %.2f | Kỳ vọng: 212.00 -> %s%n",
                f100, Math.abs(f100 - 212.0) < 0.001 ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test F -> C (32 F = 0 C)
        double c32 = NhietDo.fToC(32);
        System.out.printf("[Test 3] 32°F -> °C = %.2f | Kỳ vọng: 0.00 -> %s%n",
                c32, Math.abs(c32 - 0.0) < 0.001 ? "ĐẠT (PASS)" : "THẤT BẠI");

        // Test F -> C (98.6 F = 37 C)
        double cBody = NhietDo.fToC(98.6);
        System.out.printf("[Test 4] 98.6°F -> °C = %.2f | Kỳ vọng: 37.00 -> %s%n",
                cBody, Math.abs(cBody - 37.0) < 0.01 ? "ĐẠT (PASS)" : "THẤT BẠI");

        System.out.println("=================================================");
        System.out.println("  HOÀN THÀNH KIỂM THỬ BÀI 2!");
        System.out.println("=================================================");
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            runAutomatedTests();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            new Bai02_NhietDo().setVisible(true);
        });
    }
}
