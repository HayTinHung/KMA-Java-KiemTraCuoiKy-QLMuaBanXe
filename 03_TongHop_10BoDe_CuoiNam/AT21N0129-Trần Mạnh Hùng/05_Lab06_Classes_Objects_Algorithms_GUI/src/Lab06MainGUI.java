import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 * BẢNG ĐIỀU KHIỂN GIAO DIỆN TỔNG HỢP LAB 06 (JAVA SWING GUI)
 * Tích hợp đầy đủ:
 * - Tab 1: Bài 1 - Quản lý Hóa đơn (Invoice & InvoiceTest)
 * - Tab 2: Bài 2 - Quản lý Nhân viên (Employee & EmployeeTest)
 * - Tab 3: Bài 3 - Số phức (Complex & ComplexTest)
 * - Tab 4: Bài 4 - Đa thức hữu tỷ Q (Polynomial & PolynomialTest)
 * - Thanh trạng thái thời gian thực (Live Clock Timer) & Xác nhận Thoát (Yes/No)
 */
@SuppressWarnings("serial")
public final class Lab06MainGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";
    private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 13);

    // Dữ liệu Bài 1 & Bài 2
    private List<Invoice> invoices = InvoiceTest.createMockInvoices();
    private List<Employee> employees = EmployeeTest.createMockEmployees();

    // Thành phần Bài 1 (Invoice)
    private DefaultTableModel invoiceTableModel;
    private JTable invoiceTable;
    private JTextField txtPartNum = new JTextField(12);
    private JTextField txtPartDesc = new JTextField(18);
    private JTextField txtQty = new JTextField(8);
    private JTextField txtPrice = new JTextField(10);
    private JTextField txtTotalInvoices = new JTextField(16);
    private JComboBox<String> cboInvSortAlg = new JComboBox<>(new String[]{
            "Bubble Sort", "Interchange Sort", "Insertion Sort", "Selection Sort", "Quick Sort"
    });
    private JComboBox<String> cboInvSortBy = new JComboBox<>(new String[]{
            "Theo Mã mặt hàng", "Theo Số lượng mua"
    });
    private JComboBox<String> cboInvSearchAlg = new JComboBox<>(new String[]{
            "Linear Search", "Binary Search", "Library Search"
    });

    // Thành phần Bài 2 (Employee)
    private DefaultTableModel employeeTableModel;
    private JTable employeeTable;
    private JTextField txtEmpId = new JTextField(10);
    private JTextField txtLastName = new JTextField(10);
    private JTextField txtFirstName = new JTextField(14);
    private JTextField txtSalary = new JTextField(10);
    private JTextField txtHireDate = new JTextField("15/03/2023", 10);
    private JComboBox<String> cboEmpSortAlg = new JComboBox<>(new String[]{
            "Bubble Sort", "Interchange Sort", "Insertion Sort", "Selection Sort", "Quick Sort"
    });
    private JComboBox<String> cboEmpSortBy = new JComboBox<>(new String[]{
            "Theo Mã nhân viên", "Theo Tên nhân viên"
    });
    private JComboBox<String> cboEmpSearchAlg = new JComboBox<>(new String[]{
            "Linear Search", "Binary Search", "Library Search"
    });

    // Thành phần Bài 3 & Bài 4
    private JTextField txtZ1Real = new JTextField("3.5", 8);
    private JTextField txtZ1Imag = new JTextField("4.5", 8);
    private JTextField txtZ2Real = new JTextField("1.5", 8);
    private JTextField txtZ2Imag = new JTextField("-2.0", 8);
    private JTextArea txtComplexResult = new JTextArea(8, 50);

    private JTextField txtPolyP = new JTextField("2, -1/4, 3/2", 25);
    private JTextField txtPolyQ = new JTextField("1/2, 2/3", 25);
    private JTextField txtPolyX0 = new JTextField("2/3", 10);
    private JTextArea txtPolyResult = new JTextArea(9, 50);

    // Đồng hồ thời gian thực
    private JTextField txtLiveClock = new JTextField(18);
    private JTabbedPane tabbedPane;

    public Lab06MainGUI() {
        String startTime = new SimpleDateFormat(TIME_PATTERN).format(new Date());
        setTitle("Lab 06 - Lập trình Hướng đối tượng Java (Bắt đầu lúc: " + startTime + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(buildHeaderBar(startTime), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(BOLD_FONT);
        tabbedPane.addTab("Bài 1: Quản lý Hóa đơn (Invoice)", buildInvoiceTab());
        tabbedPane.addTab("Bài 2: Quản lý Nhân viên (Employee)", buildEmployeeTab());
        tabbedPane.addTab("Bài 3 & 4: Số phức (Complex) & Đa thức Q (Polynomial)", buildMathTab());

        add(tabbedPane, BorderLayout.CENTER);

        initTimer();
        refreshInvoiceTable();
        refreshEmployeeTable();
        computeComplexAndPolynomial();
    }

    private JPanel buildHeaderBar(String startTime) {
        JPanel top = new JPanel(new BorderLayout(10, 5));
        top.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 12));
        top.setBackground(new Color(240, 244, 248));

        JLabel lblTitle = new JLabel("HỌC VIỆN KỸ THUẬT MẬT MÃ (KMA) - BÀI THỰC HÀNH SỐ 6: CLASSES & OBJECTS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(0, 51, 102));

        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBox.setOpaque(false);
        rightBox.add(new JLabel("Giờ hiện hành:"));
        txtLiveClock.setEditable(false);
        txtLiveClock.setFont(BOLD_FONT);
        txtLiveClock.setHorizontalAlignment(JTextField.CENTER);
        txtLiveClock.setText(startTime);
        rightBox.add(txtLiveClock);

        JButton btnExit = new JButton("Thoát chương trình");
        btnExit.setBackground(new Color(190, 40, 40));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFont(BOLD_FONT);
        btnExit.addActionListener(e -> confirmExit());
        rightBox.add(btnExit);

        top.add(lblTitle, BorderLayout.WEST);
        top.add(rightBox, BorderLayout.EAST);
        return top;
    }

    private JPanel buildInvoiceTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Form nhập bên trên
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin Hóa đơn phần cứng (Invoice)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Mã mặt hàng:"), gbc);
        gbc.gridx = 1; form.add(txtPartNum, gbc);
        gbc.gridx = 2; form.add(new JLabel("Mô tả mặt hàng:"), gbc);
        gbc.gridx = 3; form.add(txtPartDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Số lượng (>0):"), gbc);
        gbc.gridx = 1; form.add(txtQty, gbc);
        gbc.gridx = 2; form.add(new JLabel("Đơn giá (>0):"), gbc);
        gbc.gridx = 3; form.add(txtPrice, gbc);

        gbc.gridx = 4; gbc.gridy = 0; form.add(new JLabel("Tổng tiền HĐ:"), gbc);
        txtTotalInvoices.setEditable(false);
        txtTotalInvoices.setFont(BOLD_FONT);
        txtTotalInvoices.setForeground(new Color(180, 20, 20));
        gbc.gridx = 5; form.add(txtTotalInvoices, gbc);

        // Hàng nút chức năng
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAdd = new JButton("Thêm Invoice");
        JButton btnDel = new JButton("Xóa theo Mã");
        JButton btnMock = new JButton("Nạp 10 mẫu (Mock)");
        JButton btnSort = new JButton("Thực hiện Sắp xếp");
        JButton btnSearch = new JButton("Tìm theo Mã");

        actions.add(btnAdd);
        actions.add(btnDel);
        actions.add(btnMock);
        actions.add(new JLabel("| Sắp xếp:"));
        actions.add(cboInvSortBy);
        actions.add(cboInvSortAlg);
        actions.add(btnSort);
        actions.add(new JLabel("| Tìm:"));
        actions.add(cboInvSearchAlg);
        actions.add(btnSearch);

        JPanel northBox = new JPanel(new BorderLayout());
        northBox.add(form, BorderLayout.CENTER);
        northBox.add(actions, BorderLayout.SOUTH);
        panel.add(northBox, BorderLayout.NORTH);

        // Bảng hiển thị
        invoiceTableModel = new DefaultTableModel(
                new String[]{"STT", "Mã Mặt Hàng", "Mô Tả Mặt Hàng", "Số Lượng", "Đơn Giá (VNĐ)", "Thành Tiền (VNĐ)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        invoiceTable = new JTable(invoiceTableModel);
        invoiceTable.setRowHeight(24);
        invoiceTable.setFont(UI_FONT);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoiceTable.getSelectedRow() != -1) {
                int r = invoiceTable.getSelectedRow();
                Invoice inv = invoices.get(r);
                txtPartNum.setText(inv.getPartNumber());
                txtPartDesc.setText(inv.getPartDescription());
                txtQty.setText(String.valueOf(inv.getQuantity()));
                txtPrice.setText(String.format("%.0f", inv.getPricePerItem()));
            }
        });

        panel.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        // Sự kiện nút bấm Bài 1
        btnAdd.addActionListener(e -> {
            try {
                String code = txtPartNum.getText().trim();
                String desc = txtPartDesc.getText().trim();
                int qty = Integer.parseInt(txtQty.getText().trim());
                double price = Double.parseDouble(txtPrice.getText().trim());
                if (code.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Mã và mô tả không được để trống!");
                    return;
                }
                if (!InvoiceTest.addInvoice(invoices, new Invoice(code, desc, qty, price))) {
                    JOptionPane.showMessageDialog(this, "Trùng mã mặt hàng: " + code, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                refreshInvoiceTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số lượng và đơn giá phải là số hợp lệ!");
            }
        });

        btnDel.addActionListener(e -> {
            String code = txtPartNum.getText().trim();
            if (InvoiceTest.deleteByPartNumber(invoices, code)) {
                refreshInvoiceTable();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy mã mặt hàng: " + code);
            }
        });

        btnMock.addActionListener(e -> {
            invoices = InvoiceTest.createMockInvoices();
            refreshInvoiceTable();
        });

        btnSort.addActionListener(e -> {
            Comparator<Invoice> cmp = cboInvSortBy.getSelectedIndex() == 1
                    ? InvoiceTest.BY_QUANTITY : InvoiceTest.BY_PART_NUMBER;
            switch (cboInvSortAlg.getSelectedIndex()) {
                case 0: InvoiceTest.bubbleSort(invoices, cmp); break;
                case 1: InvoiceTest.interchangeSort(invoices, cmp); break;
                case 2: InvoiceTest.insertionSort(invoices, cmp); break;
                case 3: InvoiceTest.selectionSort(invoices, cmp); break;
                default: InvoiceTest.quickSort(invoices, cmp); break;
            }
            refreshInvoiceTable();
        });

        btnSearch.addActionListener(e -> {
            String code = txtPartNum.getText().trim();
            Invoice found;
            int idx = cboInvSearchAlg.getSelectedIndex();
            if (idx == 1) found = InvoiceTest.binarySearch(invoices, code);
            else if (idx == 2) found = InvoiceTest.librarySearch(invoices, code);
            else found = InvoiceTest.linearSearch(invoices, code);

            if (found != null) {
                JOptionPane.showMessageDialog(this, "Tìm thấy: " + found.getPartNumber()
                        + " - " + found.getPartDescription()
                        + " | Thành tiền: " + String.format("%,.2f VNĐ", found.getInvoiceAmount()));
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn mã: " + code);
            }
        });

        return panel;
    }

    private JPanel buildEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin Nhân viên (Employee)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1; form.add(txtEmpId, gbc);
        gbc.gridx = 2; form.add(new JLabel("Họ:"), gbc);
        gbc.gridx = 3; form.add(txtLastName, gbc);
        gbc.gridx = 4; form.add(new JLabel("Tên & Đệm:"), gbc);
        gbc.gridx = 5; form.add(txtFirstName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Lương tháng:"), gbc);
        gbc.gridx = 1; form.add(txtSalary, gbc);
        gbc.gridx = 2; form.add(new JLabel("Ngày thuê (dd/MM/yyyy):"), gbc);
        gbc.gridx = 3; form.add(txtHireDate, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAdd = new JButton("Thêm NV");
        JButton btnDel = new JButton("Xóa theo Mã");
        JButton btnMock = new JButton("Nạp 10 mẫu (Mock)");
        JButton btnSort = new JButton("Thực hiện Sắp xếp");
        JButton btnSearch = new JButton("Tìm theo Mã");

        actions.add(btnAdd);
        actions.add(btnDel);
        actions.add(btnMock);
        actions.add(new JLabel("| Sắp xếp:"));
        actions.add(cboEmpSortBy);
        actions.add(cboEmpSortAlg);
        actions.add(btnSort);
        actions.add(new JLabel("| Tìm:"));
        actions.add(cboEmpSearchAlg);
        actions.add(btnSearch);

        JPanel northBox = new JPanel(new BorderLayout());
        northBox.add(form, BorderLayout.CENTER);
        northBox.add(actions, BorderLayout.SOUTH);
        panel.add(northBox, BorderLayout.NORTH);

        employeeTableModel = new DefaultTableModel(
                new String[]{"STT", "Mã NV", "Họ", "Tên & Tên Đệm", "Lương Tháng (VNĐ)", "Ngày Thuê"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        employeeTable = new JTable(employeeTableModel);
        employeeTable.setRowHeight(24);
        employeeTable.setFont(UI_FONT);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && employeeTable.getSelectedRow() != -1) {
                int r = employeeTable.getSelectedRow();
                Employee emp = employees.get(r);
                txtEmpId.setText(emp.getEmployeeId());
                txtLastName.setText(emp.getLastName());
                txtFirstName.setText(emp.getFirstName());
                txtSalary.setText(String.format("%.0f", emp.getMonthlySalary()));
                txtHireDate.setText(emp.getHireDate().toString());
            }
        });

        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            try {
                String id = txtEmpId.getText().trim();
                String last = txtLastName.getText().trim();
                String first = txtFirstName.getText().trim();
                double sal = Double.parseDouble(txtSalary.getText().trim());
                String[] dp = txtHireDate.getText().trim().split("/");
                HireDate hd = new HireDate(Integer.parseInt(dp[0]), Integer.parseInt(dp[1]), Integer.parseInt(dp[2]));
                if (!EmployeeTest.addEmployee(employees, new Employee(id, first, last, sal, hd))) {
                    JOptionPane.showMessageDialog(this, "Mã nhân viên bị trùng hoặc rỗng!");
                    return;
                }
                refreshEmployeeTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ: " + ex.getMessage());
            }
        });

        btnDel.addActionListener(e -> {
            if (EmployeeTest.deleteByEmployeeId(employees, txtEmpId.getText().trim())) {
                refreshEmployeeTable();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên cần xóa!");
            }
        });

        btnMock.addActionListener(e -> {
            employees = EmployeeTest.createMockEmployees();
            refreshEmployeeTable();
        });

        btnSort.addActionListener(e -> {
            Comparator<Employee> cmp = cboEmpSortBy.getSelectedIndex() == 1
                    ? EmployeeTest.BY_FIRST_NAME : EmployeeTest.BY_EMPLOYEE_ID;
            switch (cboEmpSortAlg.getSelectedIndex()) {
                case 0: EmployeeTest.bubbleSort(employees, cmp); break;
                case 1: EmployeeTest.interchangeSort(employees, cmp); break;
                case 2: EmployeeTest.insertionSort(employees, cmp); break;
                case 3: EmployeeTest.selectionSort(employees, cmp); break;
                default: EmployeeTest.quickSort(employees, cmp); break;
            }
            refreshEmployeeTable();
        });

        btnSearch.addActionListener(e -> {
            String id = txtEmpId.getText().trim();
            Employee found;
            int idx = cboEmpSearchAlg.getSelectedIndex();
            if (idx == 1) found = EmployeeTest.binarySearch(employees, id);
            else if (idx == 2) found = EmployeeTest.librarySearch(employees, id);
            else found = EmployeeTest.linearSearch(employees, id);

            if (found != null) {
                JOptionPane.showMessageDialog(this, "Tìm thấy NV: " + found.getFullName()
                        + " | Lương: " + String.format("%,.0f VNĐ", found.getMonthlySalary()));
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy mã NV: " + id);
            }
        });

        return panel;
    }

    private JPanel buildMathTab() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Khung Bài 3: Complex
        JPanel pComplex = new JPanel(new BorderLayout(6, 6));
        pComplex.setBorder(BorderFactory.createTitledBorder("Bài 3: Phép toán Số phức (Complex: a + b*i)"));
        JPanel cTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        cTop.add(new JLabel("Z1 (Thực, Ảo):"));
        cTop.add(txtZ1Real);
        cTop.add(txtZ1Imag);
        cTop.add(new JLabel("Z2 (Thực, Ảo):"));
        cTop.add(txtZ2Real);
        cTop.add(txtZ2Imag);
        JButton btnCalcComplex = new JButton("Tính Cộng / Trừ & In (real, imag)");
        btnCalcComplex.addActionListener(e -> computeComplexAndPolynomial());
        cTop.add(btnCalcComplex);
        txtComplexResult.setEditable(false);
        txtComplexResult.setFont(new Font("Consolas", Font.PLAIN, 13));
        pComplex.add(cTop, BorderLayout.NORTH);
        pComplex.add(new JScrollPane(txtComplexResult), BorderLayout.CENTER);

        // Khung Bài 4: Polynomial Q
        JPanel pPoly = new JPanel(new BorderLayout(6, 6));
        pPoly.setBorder(BorderFactory.createTitledBorder("Bài 4: Đa thức trên trường số hữu tỷ Q (Polynomial 0 <= n <= 100)"));
        JPanel qTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        qTop.add(new JLabel("Hệ số P(x) [a0, a1...]:"));
        qTop.add(txtPolyP);
        qTop.add(new JLabel("Hệ số Q(x):"));
        qTop.add(txtPolyQ);
        qTop.add(new JLabel("Điểm x0:"));
        qTop.add(txtPolyX0);
        JButton btnCalcPoly = new JButton("Tính P(x0), Tổng & Tích");
        btnCalcPoly.addActionListener(e -> computeComplexAndPolynomial());
        qTop.add(btnCalcPoly);
        txtPolyResult.setEditable(false);
        txtPolyResult.setFont(new Font("Consolas", Font.PLAIN, 13));
        pPoly.add(qTop, BorderLayout.NORTH);
        pPoly.add(new JScrollPane(txtPolyResult), BorderLayout.CENTER);

        panel.add(pComplex);
        panel.add(pPoly);
        return panel;
    }

    public void computeComplexAndPolynomial() {
        try {
            Complex z1 = new Complex(Double.parseDouble(txtZ1Real.getText().trim()),
                    Double.parseDouble(txtZ1Imag.getText().trim()));
            Complex z2 = new Complex(Double.parseDouble(txtZ2Real.getText().trim()),
                    Double.parseDouble(txtZ2Imag.getText().trim()));
            Complex sum = z1.add(z2);
            Complex diff = z1.subtract(z2);

            txtComplexResult.setText(
                    "Số phức Z1 = " + z1.toTupleString() + "   [" + z1.toAlgebraicString() + "]\n" +
                    "Số phức Z2 = " + z2.toTupleString() + "   [" + z2.toAlgebraicString() + "]\n" +
                    "------------------------------------------------------------------\n" +
                    "• Cộng 2 số phức (Z1 + Z2) = " + sum.toTupleString() + "   [" + sum.toAlgebraicString() + "]\n" +
                    "• Trừ 2 số phức  (Z1 - Z2) = " + diff.toTupleString() + "   [" + diff.toAlgebraicString() + "]"
            );
        } catch (Exception ex) {
            txtComplexResult.setText("Lỗi dữ liệu số phức: " + ex.getMessage());
        }

        try {
            String[] pTokens = txtPolyP.getText().split(",");
            Rational[] pCoeffs = new Rational[pTokens.length];
            for (int i = 0; i < pTokens.length; i++) pCoeffs[i] = Rational.parse(pTokens[i]);
            Polynomial P = new Polynomial(pCoeffs);

            String[] qTokens = txtPolyQ.getText().split(",");
            Rational[] qCoeffs = new Rational[qTokens.length];
            for (int i = 0; i < qTokens.length; i++) qCoeffs[i] = Rational.parse(qTokens[i]);
            Polynomial Q = new Polynomial(qCoeffs);

            Rational x0 = Rational.parse(txtPolyX0.getText());
            Rational valP = P.evaluate(x0);
            Polynomial sumPQ = P.add(Q);
            Polynomial prodPQ = P.multiply(Q);

            txtPolyResult.setText(
                    "Đa thức P(x) (bậc " + P.getDegree() + ") = " + P + "\n" +
                    "Đa thức Q(x) (bậc " + Q.getDegree() + ") = " + Q + "\n" +
                    "------------------------------------------------------------------\n" +
                    "• Giá trị P(" + x0 + ")     = " + valP + " (xấp xỉ " + String.format("%.4f", valP.toDouble()) + ")\n" +
                    "• Tổng P(x) + Q(x) = " + sumPQ + "\n" +
                    "• Tích P(x) * Q(x) = " + prodPQ
            );
        } catch (Exception ex) {
            txtPolyResult.setText("Lỗi dữ liệu đa thức: " + ex.getMessage());
        }
    }

    public void refreshInvoiceTable() {
        invoiceTableModel.setRowCount(0);
        double total = 0;
        for (int i = 0; i < invoices.size(); i++) {
            Invoice inv = invoices.get(i);
            total += inv.getInvoiceAmount();
            invoiceTableModel.addRow(new Object[]{
                    i + 1,
                    inv.getPartNumber(),
                    inv.getPartDescription(),
                    inv.getQuantity(),
                    String.format("%,.0f", inv.getPricePerItem()),
                    String.format("%,.0f", inv.getInvoiceAmount())
            });
        }
        txtTotalInvoices.setText(String.format("%,.0f VNĐ (n=%d)", total, invoices.size()));
    }

    public void refreshEmployeeTable() {
        employeeTableModel.setRowCount(0);
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            employeeTableModel.addRow(new Object[]{
                    i + 1,
                    emp.getEmployeeId(),
                    emp.getLastName(),
                    emp.getFirstName(),
                    String.format("%,.0f", emp.getMonthlySalary()),
                    emp.getHireDate().toString()
            });
        }
    }

    private void initTimer() {
        Timer timer = new Timer(1000, e -> txtLiveClock.setText(new SimpleDateFormat(TIME_PATTERN).format(new Date())));
        timer.start();
    }

    private void confirmExit() {
        int res = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn thoát khỏi chương trình Lab 06 hay không?",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION
        );
        if (res == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new Lab06MainGUI().setVisible(true));
    }
}
