import java.util.Scanner;

/**
 * BÀI TẬP 4 - LỚP POLYNOMIALTEST
 * Thực hiện menu:
 * 1. Nhập và xuất đa thức trong trường số hữu tỷ Q (bậc 0 <= n <= 100)
 * 2. Tính giá trị của đa thức tại điểm x0 thuộc Q
 * 3. Tính Tổng P(x) + Q(x) và Tích P(x) * Q(x) của 2 đa thức
 */
public class PolynomialTest {

    public static Polynomial createSampleP() {
        // P(x) = 3/2 * x^2 - 1/4 * x + 2
        return new Polynomial(new Rational[]{
                new Rational(2, 1),
                new Rational(-1, 4),
                new Rational(3, 2)
        });
    }

    public static Polynomial createSampleQ() {
        // Q(x) = 2/3 * x + 1/2
        return new Polynomial(new Rational[]{
                new Rational(1, 2),
                new Rational(2, 3)
        });
    }

    private static Polynomial inputPolynomial(Scanner sc, String name) {
        System.out.print("Nhập bậc n (0 <= n <= 100) của đa thức " + name + ": ");
        int n = Integer.parseInt(sc.nextLine().trim());
        Rational[] coeffs = new Rational[n + 1];
        for (int i = 0; i <= n; i++) {
            System.out.print("  Nhập hệ số a_" + i + " (dạng p/q hoặc số nguyên): ");
            coeffs[i] = Rational.parse(sc.nextLine().trim());
        }
        return new Polynomial(coeffs);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, "UTF-8");
        Polynomial p = createSampleP();
        Polynomial q = createSampleQ();

        while (true) {
            System.out.println("\n================ MENU ĐA THỨC HỮU TỶ Q (POLYNOMIAL TEST) ================");
            System.out.println("Đa thức P(x) hiện tại (bậc " + p.getDegree() + "): P(x) = " + p);
            System.out.println("Đa thức Q(x) hiện tại (bậc " + q.getDegree() + "): Q(x) = " + q);
            System.out.println("1. Nhập và xuất đa thức P(x), Q(x)");
            System.out.println("2. Tính giá trị của đa thức P(x0) và Q(x0) tại điểm x0 thuộc Q");
            System.out.println("3. Tính Tổng P(x) + Q(x) và Tích P(x) * Q(x) của 2 đa thức");
            System.out.println("0. Thoát chương trình");
            System.out.print("Chọn chức năng (0-3): ");

            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;

            switch (choice) {
                case "1":
                    p = inputPolynomial(sc, "P(x)");
                    q = inputPolynomial(sc, "Q(x)");
                    System.out.println("Đã nhập thành công:");
                    System.out.println("  P(x) = " + p);
                    System.out.println("  Q(x) = " + q);
                    break;

                case "2":
                    System.out.print("Nhập điểm x0 thuộc Q (dạng p/q hoặc số nguyên, ví dụ 2/3): ");
                    Rational x0 = Rational.parse(sc.nextLine().trim());
                    Rational valP = p.evaluate(x0);
                    Rational valQ = q.evaluate(x0);
                    System.out.printf("Tại x0 = %s:%n", x0);
                    System.out.printf("  P(%s) = %s (xấp xỉ %.4f)%n", x0, valP, valP.toDouble());
                    System.out.printf("  Q(%s) = %s (xấp xỉ %.4f)%n", x0, valQ, valQ.toDouble());
                    break;

                case "3":
                    Polynomial sum = p.add(q);
                    Polynomial prod = p.multiply(q);
                    System.out.println("Tổng P(x) + Q(x) = " + sum);
                    System.out.println("Tích P(x) * Q(x) = " + prod);
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }
}
