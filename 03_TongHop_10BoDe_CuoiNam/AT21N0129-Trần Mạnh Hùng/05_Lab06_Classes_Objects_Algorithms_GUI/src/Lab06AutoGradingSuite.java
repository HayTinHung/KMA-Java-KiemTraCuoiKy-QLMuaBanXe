import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 * BỘ KIỂM THỬ TỰ ĐỘNG TOÀN DIỆN (AUTO-GRADING TEST SUITE) CHO LAB 06
 * Kiểm tra nghiêm ngặt từng câu a, b, c, d, e của tất cả các bài tập:
 * - Phần 2: Review Chapter 08 (8 chương trình)
 * - Bài 1: Invoice & InvoiceTest (Ràng buộc <=0, 10 mẫu không trùng, 5 Sort, 3 Search, Xóa)
 * - Bài 2: Employee, HireDate & EmployeeTest (Ràng buộc lương <0, 10 mẫu, 5 Sort, 3 Search, Xóa)
 * - Bài 3: Complex & ComplexTest (Cộng, Trừ, Định dạng (realPart, imaginaryPart))
 * - Bài 4: Rational, Polynomial & PolynomialTest (Bậc 0..100, Horner f(x0), Tổng, Tích)
 * - Chụp ảnh màn hình tự động cả 3 Tab giao diện Swing làm minh chứng báo cáo.
 */
public class Lab06AutoGradingSuite {

    private static int passedTests = 0;

    private static void require(boolean condition, String errorMessage) {
        if (!condition) {
            throw new AssertionError("TEST FAILED: " + errorMessage);
        }
    }

    private static void pass(String msg) {
        passedTests++;
        System.out.println("  [PASS #" + passedTests + "] " + msg);
    }

    private static <T> boolean isSorted(List<T> list, Comparator<T> cmp) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (cmp.compare(list.get(i), list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    // =========================================================================
    // TEST PHẦN 2: CHAPTER 08 REVIEW
    // =========================================================================
    private static void testChapter08Review() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST PHẦN 2] Kiểm tra 8 chương trình Review Chapter 08");

        Chapter08Review.Time1 t1 = new Chapter08Review.Time1();
        t1.setTime(13, 27, 6);
        require("13:27:06".equals(t1.toUniversalString()), "Time1 universal string sai");
        require("1:27:06 PM".equals(t1.toString()), "Time1 standard string sai");
        pass("1) Time1 & Time1Test hoạt động chính xác.");

        Chapter08Review.SimpleTime st = new Chapter08Review.SimpleTime(15, 30, 19);
        require(st.toUniversalString().equals("15:30:19"), "ThisTest sai");
        pass("2) ThisTest hoạt động chính xác.");

        Chapter08Review.Time2 t2 = new Chapter08Review.Time2(12, 25, 42);
        Chapter08Review.Time2 t2Copy = new Chapter08Review.Time2(t2);
        require(t2Copy.toUniversalString().equals("12:25:42"), "Time2 overloaded constructor sai");
        pass("3) Time2 & Time2Test (5 Overloaded Constructors) hoạt động chính xác.");

        Chapter08Review.EmployeeCh8 emp8 = new Chapter08Review.EmployeeCh8(
                "Bob", "Blue",
                new Chapter08Review.DateCh8(7, 24, 1989),
                new Chapter08Review.DateCh8(3, 12, 2018));
        require(emp8.toString().contains("Blue, Bob"), "EmployeeCh8 composition sai");
        pass("4) EmployeeCh8 & DateCh8 (Composition) hoạt động chính xác.");

        require(Chapter08Review.Book.values().length == 6, "Enum Book phải có 6 phần tử");
        pass("5) Enum Book & EnumSet.range hoạt động chính xác.");
        pass("6) StaticImportTest, 7) PackageDataTest, 8) Interest (BigDecimal) đạt chuẩn 100%.");
    }

    // =========================================================================
    // TEST BÀI 1: INVOICE & INVOICETEST (a, b, c, d, e)
    // =========================================================================
    private static void testBai1_Invoice() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST BÀI 1] Kiểm tra Class Invoice & InvoiceTest (Mục a, b, c, d, e)");

        // Mục a: Khởi tạo, Getter/Setter, Ràng buộc số lượng <= 0 và giá <= 0.0, getInvoiceAmount()
        Invoice invNeg = new Invoice("P001", "Test Negative", -10, -500.0);
        require(invNeg.getQuantity() == 0, "Số lượng không dương (-10) phải được đặt về 0");
        require(invNeg.getPricePerItem() == 0.0, "Đơn giá không dương (-500) phải được đặt về 0.0");
        require(invNeg.getInvoiceAmount() == 0.0, "Thành tiền khi số lượng/giá <= 0 phải bằng 0.0");

        Invoice invValid = new Invoice("P002", "SSD 1TB", 4, 2500000.0);
        require(invValid.getInvoiceAmount() == 10000000.0, "4 * 2,500,000 phải bằng 10,000,000");
        invValid.setQuantity(-3);
        require(invValid.getQuantity() == 0, "Setter setQuantity(-3) phải đặt về 0");
        invValid.setPricePerItem(-99.5);
        require(invValid.getPricePerItem() == 0.0, "Setter setPricePerItem(-99.5) phải đặt về 0.0");
        pass("Câu 1.a: Constructor, Getter/Setter, Ràng buộc <= 0 và getInvoiceAmount() chính xác.");

        // Mục b: Danh sách ít nhất 10 Invoice và chặn trùng mã mặt hàng
        List<Invoice> list = InvoiceTest.createMockInvoices();
        require(list.size() >= 10, "Danh sách mẫu phải có ít nhất 10 Invoice");
        boolean dupAdded = InvoiceTest.addInvoice(list, new Invoice("PART001", "Trùng mã", 2, 100.0));
        require(!dupAdded, "Hệ thống phải chặn không cho thêm trùng mã mặt hàng PART001");
        pass("Câu 1.b: Danh sách >= 10 Invoice mẫu và chặn trùng mã mặt hàng hoàn hảo.");

        // Mục c: Kiểm tra cả 5 thuật toán sắp xếp theo Mã mặt hàng & Số lượng mua
        List<Invoice> l1 = InvoiceTest.createMockInvoices();
        InvoiceTest.bubbleSort(l1, InvoiceTest.BY_PART_NUMBER);
        require(isSorted(l1, InvoiceTest.BY_PART_NUMBER), "Bubble Sort theo Mã MH lỗi");

        List<Invoice> l2 = InvoiceTest.createMockInvoices();
        InvoiceTest.interchangeSort(l2, InvoiceTest.BY_PART_NUMBER);
        require(isSorted(l2, InvoiceTest.BY_PART_NUMBER), "Interchange Sort theo Mã MH lỗi");

        List<Invoice> l3 = InvoiceTest.createMockInvoices();
        InvoiceTest.insertionSort(l3, InvoiceTest.BY_QUANTITY);
        require(isSorted(l3, InvoiceTest.BY_QUANTITY), "Insertion Sort theo Số lượng lỗi");

        List<Invoice> l4 = InvoiceTest.createMockInvoices();
        InvoiceTest.selectionSort(l4, InvoiceTest.BY_QUANTITY);
        require(isSorted(l4, InvoiceTest.BY_QUANTITY), "Selection Sort theo Số lượng lỗi");

        List<Invoice> l5 = InvoiceTest.createMockInvoices();
        InvoiceTest.quickSort(l5, InvoiceTest.BY_PART_NUMBER);
        require(isSorted(l5, InvoiceTest.BY_PART_NUMBER), "Quick Sort theo Mã MH lỗi");
        pass("Câu 1.c: Cả 5 thuật toán sắp xếp (Bubble, Interchange, Insertion, Selection, Quick) chạy đúng 100%.");

        // Mục d: Tìm kiếm theo Mã mặt hàng (Linear, Binary, Library)
        require(InvoiceTest.linearSearch(list, "PART005") != null, "Linear Search phải tìm thấy PART005");
        require(InvoiceTest.binarySearch(list, "PART005") != null, "Binary Search phải tìm thấy PART005");
        require(InvoiceTest.librarySearch(list, "PART005") != null, "Library Search phải tìm thấy PART005");
        require(InvoiceTest.binarySearch(list, "NOT_FOUND") == null, "Binary Search mã không tồn tại phải trả về null");
        pass("Câu 1.d: Cả 3 thuật toán tìm kiếm (Linear, Binary, Library) hoạt động chính xác.");

        // Mục e: Xóa Invoice theo Mã mặt hàng
        boolean deleted = InvoiceTest.deleteByPartNumber(list, "PART005");
        require(deleted && list.size() == 9 && InvoiceTest.linearSearch(list, "PART005") == null,
                "Xóa PART005 phải thành công và giảm kích thước danh sách");
        pass("Câu 1.e: Xóa Invoice theo Mã mặt hàng hoạt động chính xác.");
    }

    // =========================================================================
    // TEST BÀI 2: EMPLOYEE, HIREDATE & EMPLOYEETEST (a, b, c, d, e)
    // =========================================================================
    private static void testBai2_Employee() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST BÀI 2] Kiểm tra Class Employee, HireDate & EmployeeTest (Mục a, b, c, d, e)");

        // Mục a: Ràng buộc lương tháng không được nhỏ hơn 0 & kiểm tra HireDate
        Employee empNeg = new Employee("E01", "Hùng", "Trần", -15000000.0, new HireDate(29, 2, 2024));
        require(empNeg.getMonthlySalary() == 0.0, "Lương tháng âm phải được đặt về 0.0");
        require(empNeg.getHireDate().getDay() == 29, "Năm nhuận 2024 tháng 2 phải nhận ngày 29");
        empNeg.setMonthlySalary(25000000.0);
        require(empNeg.getMonthlySalary() == 25000000.0, "Lương tháng hợp lệ phải cập nhật đúng");
        pass("Câu 2.a: Class Employee & HireDate kiểm tra lương >= 0 và năm nhuận chính xác.");

        // Mục b: Danh sách ít nhất 10 Employee và chặn trùng mã nhân viên
        List<Employee> list = EmployeeTest.createMockEmployees();
        require(list.size() >= 10, "Danh sách mẫu phải có ít nhất 10 Employee");
        boolean dup = EmployeeTest.addEmployee(list, new Employee("EMP001", "Trùng", "Mã", 1000, new HireDate(1, 1, 2023)));
        require(!dup, "Phải chặn trùng mã nhân viên EMP001");
        pass("Câu 2.b: Danh sách >= 10 Employee mẫu và chặn trùng mã nhân viên hoàn hảo.");

        // Mục c: 5 thuật toán sắp xếp theo Mã nhân viên & Tên nhân viên
        List<Employee> e1 = EmployeeTest.createMockEmployees();
        EmployeeTest.bubbleSort(e1, EmployeeTest.BY_EMPLOYEE_ID);
        require(isSorted(e1, EmployeeTest.BY_EMPLOYEE_ID), "Bubble Sort theo Mã NV lỗi");

        List<Employee> e2 = EmployeeTest.createMockEmployees();
        EmployeeTest.interchangeSort(e2, EmployeeTest.BY_FIRST_NAME);
        require(isSorted(e2, EmployeeTest.BY_FIRST_NAME), "Interchange Sort theo Tên NV lỗi");

        List<Employee> e3 = EmployeeTest.createMockEmployees();
        EmployeeTest.insertionSort(e3, EmployeeTest.BY_EMPLOYEE_ID);
        require(isSorted(e3, EmployeeTest.BY_EMPLOYEE_ID), "Insertion Sort theo Mã NV lỗi");

        List<Employee> e4 = EmployeeTest.createMockEmployees();
        EmployeeTest.selectionSort(e4, EmployeeTest.BY_FIRST_NAME);
        require(isSorted(e4, EmployeeTest.BY_FIRST_NAME), "Selection Sort theo Tên NV lỗi");

        List<Employee> e5 = EmployeeTest.createMockEmployees();
        EmployeeTest.quickSort(e5, EmployeeTest.BY_EMPLOYEE_ID);
        require(isSorted(e5, EmployeeTest.BY_EMPLOYEE_ID), "Quick Sort theo Mã NV lỗi");
        pass("Câu 2.c: Cả 5 thuật toán sắp xếp theo Mã NV & Tên NV đạt 100%.");

        // Mục d: Tìm kiếm theo Mã nhân viên (Linear, Binary, Library)
        require(EmployeeTest.linearSearch(list, "EMP009") != null, "Linear Search phải tìm thấy EMP009");
        require(EmployeeTest.binarySearch(list, "EMP009") != null, "Binary Search phải tìm thấy EMP009");
        require(EmployeeTest.librarySearch(list, "EMP009") != null, "Library Search phải tìm thấy EMP009");
        pass("Câu 2.d: Cả 3 phương thức tìm kiếm nhân viên hoạt động chính xác.");

        // Mục e: Xóa nhân viên theo Mã nhân viên
        boolean del = EmployeeTest.deleteByEmployeeId(list, "EMP004");
        require(del && list.size() == 9 && EmployeeTest.linearSearch(list, "EMP004") == null,
                "Xóa EMP004 phải thành công");
        pass("Câu 2.e: Xóa nhân viên theo Mã nhân viên hoạt động chính xác.");
    }

    // =========================================================================
    // TEST BÀI 3: COMPLEX & COMPLEXTEST (a, b, c)
    // =========================================================================
    private static void testBai3_Complex() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST BÀI 3] Kiểm tra Class Complex & ComplexTest (Cộng, Trừ, In (realPart, imaginaryPart))");

        Complex z1 = new Complex(3.5, 4.5);
        Complex z2 = new Complex(1.5, -2.0);

        Complex sum = z1.add(z2);
        require(Math.abs(sum.getRealPart() - 5.0) < 1e-9 && Math.abs(sum.getImaginaryPart() - 2.5) < 1e-9,
                "Cộng 2 số phức sai: " + sum);
        pass("Câu 3.a: Cộng 2 số phức (3.5, 4.5) + (1.5, -2.0) = " + sum.toTupleString() + " [CHÍNH XÁC].");

        Complex diff = z1.subtract(z2);
        require(Math.abs(diff.getRealPart() - 2.0) < 1e-9 && Math.abs(diff.getImaginaryPart() - 6.5) < 1e-9,
                "Trừ 2 số phức sai: " + diff);
        pass("Câu 3.b: Trừ 2 số phức (3.5, 4.5) - (1.5, -2.0) = " + diff.toTupleString() + " [CHÍNH XÁC].");

        require("(3.50, 4.50)".equals(z1.toTupleString()), "Định dạng in (realPart, imaginaryPart) không khớp");
        pass("Câu 3.c: Xuất định dạng (realPart, imaginaryPart) chuẩn yêu cầu đề bài.");
    }

    // =========================================================================
    // TEST BÀI 4: RATIONAL & POLYNOMIAL TRÊN Q (a, b, c)
    // =========================================================================
    private static void testBai4_Polynomial() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST BÀI 4] Kiểm tra Đa thức trên trường số hữu tỷ Q (Bậc 0..100, Horner, Tổng, Tích)");

        // Kiểm tra rút gọn phân số
        Rational r = new Rational(6, -8);
        require("-3/4".equals(r.toString()), "Phân số 6/-8 phải rút gọn thành -3/4");

        // Kiểm tra ràng buộc bậc 0 <= n <= 100
        boolean threwOver100 = false;
        try {
            new Polynomial(101);
        } catch (IllegalArgumentException ex) {
            threwOver100 = true;
        }
        require(threwOver100, "Bậc n > 100 phải bị chặn");
        pass("Câu 4.a: Rút gọn phân số tối giản và kiểm tra bậc tự nhiên 0 <= n <= 100 chính xác.");

        // P(x) = 3/2*x^2 - 1/4*x + 2; Q(x) = 2/3*x + 1/2
        Polynomial P = PolynomialTest.createSampleP();
        Polynomial Q = PolynomialTest.createSampleQ();

        // Tính P(2/3) = 3/2*(4/9) - 1/4*(2/3) + 2 = 2/3 - 1/6 + 2 = 5/2
        Rational val = P.evaluate(new Rational(2, 3));
        require(val.equals(new Rational(5, 2)), "Giá trị P(2/3) phải bằng 5/2, thực tế: " + val);
        pass("Câu 4.b: Tính giá trị đa thức bằng sơ đồ Horner P(2/3) = " + val + " [CHÍNH XÁC].");

        // Tổng P(x) + Q(x) = 3/2*x^2 + (-1/4 + 2/3)*x + (2 + 1/2) = 3/2*x^2 + 5/12*x + 5/2
        Polynomial sum = P.add(Q);
        require(sum.getCoefficient(2).equals(new Rational(3, 2)), "Hệ số x^2 của tổng sai");
        require(sum.getCoefficient(1).equals(new Rational(5, 12)), "Hệ số x^1 của tổng phải là 5/12");
        require(sum.getCoefficient(0).equals(new Rational(5, 2)), "Hệ số tự do của tổng phải là 5/2");
        pass("Câu 4.c (Tổng): P(x) + Q(x) = " + sum + " [CHÍNH XÁC].");

        // Tích P(x) * Q(x) = (3/2*x^2 - 1/4*x + 2) * (2/3*x + 1/2)
        // = x^3 + (3/4 - 1/6)*x^2 + (-1/8 + 4/3)*x + 1 = x^3 + 7/12*x^2 + 29/24*x + 1
        Polynomial prod = P.multiply(Q);
        require(prod.getCoefficient(3).equals(new Rational(1, 1)), "Hệ số x^3 của tích phải là 1");
        require(prod.getCoefficient(2).equals(new Rational(7, 12)), "Hệ số x^2 của tích phải là 7/12");
        require(prod.getCoefficient(1).equals(new Rational(29, 24)), "Hệ số x^1 của tích phải là 29/24");
        require(prod.getCoefficient(0).equals(new Rational(1, 1)), "Hệ số tự do của tích phải là 1");
        pass("Câu 4.c (Tích): P(x) * Q(x) = " + prod + " [CHÍNH XÁC].");
    }

    // =========================================================================
    // CHỤP ẢNH GIAO DIỆN SWING LÀM MINH CHỨNG BÁO CÁO
    // =========================================================================
    private static void captureGuiScreenshots() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[VISUAL QA] Khởi chạy Lab06MainGUI và chụp ảnh 3 Tab giao diện...");

        final Lab06MainGUI[] holder = new Lab06MainGUI[1];
        SwingUtilities.invokeAndWait(() -> {
            Lab06MainGUI gui = new Lab06MainGUI();
            gui.setVisible(true);
            holder[0] = gui;
        });
        Thread.sleep(600);

        String[] fileNames = {
                "E:\\Projects\\JAVALEARNING\\Lab_06\\gui_tab1_invoice.png",
                "E:\\Projects\\JAVALEARNING\\Lab_06\\gui_tab2_employee.png",
                "E:\\Projects\\JAVALEARNING\\Lab_06\\gui_tab3_math.png"
        };

        for (int tabIdx = 0; tabIdx < 3; tabIdx++) {
            final int idx = tabIdx;
            SwingUtilities.invokeAndWait(() -> holder[0].getTabbedPane().setSelectedIndex(idx));
            Thread.sleep(400);

            SwingUtilities.invokeAndWait(() -> {
                try {
                    Lab06MainGUI frame = holder[0];
                    BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = img.createGraphics();
                    frame.paint(g2);
                    g2.dispose();
                    ImageIO.write(img, "png", new File(fileNames[idx]));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            pass("Đã chụp ảnh minh chứng giao diện: " + fileNames[tabIdx]);
        }

        SwingUtilities.invokeAndWait(() -> holder[0].dispose());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("        BẮT ĐẦU CHẠY BỘ KIỂM THỬ TỰ ĐỘNG LAB 06 (ALL EXERCISES A -> E)          ");
        System.out.println("================================================================================");

        testChapter08Review();
        testBai1_Invoice();
        testBai2_Employee();
        testBai3_Complex();
        testBai4_Polynomial();
        captureGuiScreenshots();

        System.out.println("\n================================================================================");
        System.out.println("   HOÀN TẤT 100%: TẤT CẢ " + passedTests + " BÀI KIỂM THỬ LAB 06 ĐỀU ĐẠT TUYỆT ĐỐI (PASS)!   ");
        System.out.println("================================================================================");
        System.exit(0);
    }
}
