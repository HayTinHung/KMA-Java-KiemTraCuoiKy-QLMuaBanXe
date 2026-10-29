import java.util.Scanner;

/**
 * Bài 43: Tạo giá trị ngẫu nhiên cho ma trận vuông
 */
public class Bai43 {

    public static int[][] taoMaTranVuong(int n, int min, int max) {
        return Mang2Chieu.taoNgauNhien(n, n, min, max);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 43: Tạo giá trị ngẫu nhiên cho ma trận vuông ===");
        System.out.print("Chon cach tao mang (1: Nhap tay, 2: Ngau nhien): ");
        int chon = sc.hasNextInt() ? sc.nextInt() : 2;
        int[][] a;
        if (chon == 1) {
            a = Mang2Chieu.nhap(sc);
        } else {
            System.out.print("Nhap so dong n: ");
            int n = sc.nextInt();
            System.out.print("Nhap so cot m: ");
            int m = sc.nextInt();
            a = Mang2Chieu.taoNgauNhien(n, m, -10, 20);
            Mang2Chieu.xuat(a);
        }
        System.out.print("Nhap kich thuoc n: ");
        int n = sc.nextInt();
        a = taoMaTranVuong(n, 1, 50);
        Mang2Chieu.xuat(a);
        sc.close();
    }
}
