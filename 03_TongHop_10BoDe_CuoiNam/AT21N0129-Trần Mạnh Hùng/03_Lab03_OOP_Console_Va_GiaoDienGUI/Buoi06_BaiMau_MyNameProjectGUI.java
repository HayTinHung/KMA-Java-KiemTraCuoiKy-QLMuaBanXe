import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Buổi 6 - Bài tập mẫu: Form "My Name Project" (Trang 6-7, đề bai tap giao dien 1.pdf).
 *
 * Sử dụng 100% kiến thức chuẩn trong tài liệu trường:
 * - JFrame, JPanel, JLabel, JTextField, JButton, GridBagLayout, FlowLayout, BorderLayout.
 * - DocumentListener, ActionListener, WindowAdapter, JOptionPane, Calendar.
 */
@SuppressWarnings("serial")
public final class Buoi06_BaiMau_MyNameProjectGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    public static boolean isTestMode = false;

    JTextField txtYourName;
    JTextField txtYearOfBirth;
    JLabel errYourName;
    JLabel errYearOfBirth;
    JButton btnShow;
    JButton btnClear;
    JButton btnExit;
    private String lastMessage = "";

    public Buoi06_BaiMau_MyNameProjectGUI() {
        super("My Name Project");
        initUI();
        initEvents();
    }

    private void initUI() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                xuLyExit();
            }
        });

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblName = new JLabel("Your Name:");
        JLabel lblYear = new JLabel("Year of birth:");
        txtYourName = new JTextField(18);
        txtYearOfBirth = new JTextField(18);

        errYourName = taoErrorBadge();
        errYearOfBirth = taoErrorBadge();

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        form.add(lblName, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.70;
        form.add(txtYourName, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.05;
        form.add(errYourName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        form.add(lblYear, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.70;
        form.add(txtYearOfBirth, gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.05;
        form.add(errYearOfBirth, gbc);

        main.add(form, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        btnShow = new JButton("Show");
        btnClear = new JButton("Clear");
        btnExit = new JButton("Exit");
        for (JButton b : new JButton[]{btnShow, btnClear, btnExit}) {
            b.setPreferredSize(new Dimension(80, 28));
            pnlBtns.add(b);
        }
        main.add(pnlBtns, BorderLayout.SOUTH);

        setContentPane(main);
        setSize(410, 185);
        setLocationRelativeTo(null);
    }

    private JLabel taoErrorBadge() {
        JLabel lbl = new JLabel("!");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(new Color(210, 30, 30));
        lbl.setOpaque(true);
        lbl.setHorizontalAlignment(JLabel.CENTER);
        lbl.setPreferredSize(new Dimension(18, 18));
        lbl.setVisible(false);
        return lbl;
    }

    private void initEvents() {
        DocumentListener dl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { kiemTraErrorProvider(); }
            @Override
            public void removeUpdate(DocumentEvent e) { kiemTraErrorProvider(); }
            @Override
            public void changedUpdate(DocumentEvent e) { kiemTraErrorProvider(); }
        };
        txtYourName.getDocument().addDocumentListener(dl);
        txtYearOfBirth.getDocument().addDocumentListener(dl);

        btnShow.addActionListener(e -> xuLyShow());
        btnClear.addActionListener(e -> xuLyClear());
        btnExit.addActionListener(e -> xuLyExit());
    }

    public boolean kiemTraErrorProvider() {
        String name = txtYourName.getText().trim();
        String yearStr = txtYearOfBirth.getText().trim();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        boolean validName = !name.isEmpty();
        errYourName.setVisible(!validName);
        errYourName.setToolTipText(validName ? null : "Your Name không được để trống!");

        boolean validYear = false;
        if (!yearStr.isEmpty()) {
            try {
                int y = Integer.parseInt(yearStr);
                validYear = (y > 1900 && y <= currentYear);
            } catch (NumberFormatException ignored) {
                validYear = false;
            }
        }
        errYearOfBirth.setVisible(!validYear);
        errYearOfBirth.setToolTipText(validYear ? null : "Year of birth phải là số nguyên hợp lệ!");

        return validName && validYear;
    }

    public void xuLyShow() {
        if (!kiemTraErrorProvider()) {
            if (!isTestMode) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ Your Name và Year of birth là số hợp lệ!",
                        "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }
        String name = txtYourName.getText().trim();
        int year = Integer.parseInt(txtYearOfBirth.getText().trim());
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = currentYear - year;
        lastMessage = "My Name is: " + name + "\nAge: " + age;
        if (!isTestMode) {
            JOptionPane.showMessageDialog(this, lastMessage, "Thông tin", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void xuLyClear() {
        txtYourName.setText("");
        txtYearOfBirth.setText("");
        errYourName.setVisible(false);
        errYearOfBirth.setVisible(false);
        txtYourName.requestFocusInWindow();
    }

    public void xuLyExit() {
        if (isTestMode) {
            dispose();
            return;
        }
        int ans = JOptionPane.showConfirmDialog(this,
                "Bạn có thực sự muốn thoát khỏi chương trình không?",
                "Xác nhận thoát", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public String getLastMessage() { return lastMessage; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Buoi06_BaiMau_MyNameProjectGUI().setVisible(true));
    }
}
