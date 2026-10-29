import java.util.Scanner;

/**
 * BÀI TẬP 3 - LỚP COMPLEXTEST
 * Menu thực hiện:
 * 1. Nhập 2 số phức Z1 và Z2
 * 2. Cộng 2 số phức (Z1 + Z2)
 * 3. Trừ 2 số phức (Z1 - Z2)
 * 4. In ra số phức dạng (realPart, imaginaryPart)
 */
public class ComplexTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, "UTF-8");
        Complex z1 = new Complex(3.5, 4.5);
        Complex z2 = new Complex(1.5, -2.0);

        while (true) {
            System.out.println("\n================ MENU SỐ PHỨC (COMPLEX TEST) ================");
            System.out.println("Số phức hiện tại: Z1 = " + z1.toTupleString() + " [" + z1.toAlgebraicString() + "]"
                    + " | Z2 = " + z2.toTupleString() + " [" + z2.toAlgebraicString() + "]");
            System.out.println("1. Nhập 2 số phức Z1 và Z2");
            System.out.println("2. Cộng 2 số phức (Z1 + Z2)");
            System.out.println("3. Trừ 2 số phức (Z1 - Z2)");
            System.out.println("4. In ra số phức dạng (realPart, imaginaryPart)");
            System.out.println("0. Thoát chương trình");
            System.out.print("Chọn chức năng (0-4): ");

            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;

            switch (choice) {
                case "1":
                    System.out.print("Nhập phần thực của Z1: ");
                    double r1 = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Nhập phần ảo của Z1: ");
                    double i1 = Double.parseDouble(sc.nextLine().trim());
                    z1 = new Complex(r1, i1);

                    System.out.print("Nhập phần thực của Z2: ");
                    double r2 = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Nhập phần ảo của Z2: ");
                    double i2 = Double.parseDouble(sc.nextLine().trim());
                    z2 = new Complex(r2, i2);
                    System.out.println("Đã cập nhật Z1 = " + z1.toTupleString() + ", Z2 = " + z2.toTupleString());
                    break;

                case "2":
                    Complex sum = z1.add(z2);
                    System.out.println("Kết quả Cộng: " + z1.toTupleString() + " + " + z2.toTupleString()
                            + " = " + sum.toTupleString() + "  [" + sum.toAlgebraicString() + "]");
                    break;

                case "3":
                    Complex diff = z1.subtract(z2);
                    System.out.println("Kết quả Trừ : " + z1.toTupleString() + " - " + z2.toTupleString()
                            + " = " + diff.toTupleString() + "  [" + diff.toAlgebraicString() + "]");
                    break;

                case "4":
                    System.out.println("Số phức Z1 = " + z1.toTupleString());
                    System.out.println("Số phức Z2 = " + z2.toTupleString());
                    System.out.println("Tổng Z1+Z2 = " + z1.add(z2).toTupleString());
                    System.out.println("Hiệu Z1-Z2 = " + z1.subtract(z2).toTupleString());
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }
}
