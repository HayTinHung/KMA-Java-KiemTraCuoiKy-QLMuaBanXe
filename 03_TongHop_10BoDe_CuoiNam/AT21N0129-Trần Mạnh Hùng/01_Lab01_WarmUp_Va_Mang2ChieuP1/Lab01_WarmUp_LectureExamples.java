import java.util.Arrays;
import java.util.Scanner;

/**
 * Lab 01 - Warm up (Instructor: MSc. Hoang Ngoc Long - Java_Lab01.pdf).
 *
 * Chương trình tổng hợp các ví dụ trong bài giảng (Lecture Examples) phục vụ
 * thực hành biên dịch bằng lệnh javac và chạy bằng lệnh java:
 * 1. Kiểm tra môi trường JDK, PATH, CLASSPATH.
 * 2. Xuất văn bản với System.out.print, println, printf.
 * 3. Nhập số nguyên từ bàn phím với Scanner, tính tổng/hiệu/tích/thương và so sánh.
 * 4. Cấu trúc điều khiển if/else, switch/case, vòng lặp for/while và mảng cơ bản.
 */
public final class Lab01_WarmUp_LectureExamples {

    public static void kiemTraMoiTruongJDK() {
        System.out.println("=== 1. KIỂM TRA MÔI TRƯỜNG JDK & BIẾN MÔI TRƯỜNG ===");
        System.out.println("Java Version : " + System.getProperty("java.version"));
        System.out.println("Java Home    : " + System.getProperty("java.home"));
        System.out.println("OS Name      : " + System.getProperty("os.name"));
        System.out.println("User Dir     : " + System.getProperty("user.dir"));
    }

    public static void viDuXuatVanBan() {
        System.out.println("\n=== 2. VÍ DỤ XUẤT VĂN BẢN (Welcome1, Welcome2, Welcome3, Welcome4) ===");
        System.out.println("Welcome to Java Programming!");
        System.out.print("Welcome to ");
        System.out.println("Java Programming (print + println)!");
        System.out.println("Welcome\nto\nJava\nProgramming!");
        System.out.printf("%s%n%s%n", "Welcome to", "Java Programming (printf)!");
    }

    public static void viDuTinhToanVaSoSanh(int number1, int number2) {
        System.out.println("\n=== 3. VÍ DỤ ADDITION & COMPARISON (Hai số: " + number1 + " và " + number2 + ") ===");
        int sum = number1 + number2;
        int diff = number1 - number2;
        long prod = (long) number1 * number2;
        System.out.printf("Sum        : %d + %d = %d%n", number1, number2, sum);
        System.out.printf("Difference : %d - %d = %d%n", number1, number2, diff);
        System.out.printf("Product    : %d * %d = %d%n", number1, number2, prod);
        if (number2 != 0) {
            System.out.printf("Quotient   : %d / %d = %.2f%n", number1, number2, (double) number1 / number2);
        }

        if (number1 == number2) {
            System.out.printf("Comparison : %d == %d%n", number1, number2);
        }
        if (number1 != number2) {
            System.out.printf("Comparison : %d != %d%n", number1, number2);
        }
        if (number1 < number2) {
            System.out.printf("Comparison : %d < %d%n", number1, number2);
        }
        if (number1 > number2) {
            System.out.printf("Comparison : %d > %d%n", number1, number2);
        }
    }

    public static void viDuVongLapVaMang() {
        System.out.println("\n=== 4. VÍ DỤ VÒNG LẶP & MẢNG TRONG BÀI GIẢNG ===");
        int[] scores = {85, 92, 78, 95, 88};
        int total = 0;
        for (int s : scores) {
            total += s;
        }
        double avg = (double) total / scores.length;
        System.out.println("Danh sách điểm : " + Arrays.toString(scores));
        System.out.printf("Tổng điểm = %d, Điểm trung bình = %.2f%n", total, avg);
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            kiemTraMoiTruongJDK();
            viDuXuatVanBan();
            viDuTinhToanVaSoSanh(45, 15);
            viDuVongLapVaMang();
            return;
        }

        kiemTraMoiTruongJDK();
        viDuXuatVanBan();

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("\nNhập số nguyên thứ nhất (hoặc nhấn Enter để dùng mặc định 45): ");
            String line1 = sc.hasNextLine() ? sc.nextLine().trim() : "";
            int a = line1.isEmpty() ? 45 : Integer.parseInt(line1);

            System.out.print("Nhập số nguyên thứ hai (hoặc nhấn Enter để dùng mặc định 15): ");
            String line2 = sc.hasNextLine() ? sc.nextLine().trim() : "";
            int b = line2.isEmpty() ? 15 : Integer.parseInt(line2);

            viDuTinhToanVaSoSanh(a, b);
        } catch (Exception e) {
            viDuTinhToanVaSoSanh(45, 15);
        }
        viDuVongLapVaMang();
    }
}
