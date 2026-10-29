import java.util.Scanner;

/**
 * Bài 46: In ma trận vuông theo các quy ước đặc biệt (a, b, c)
 */
public class Bai46 {

    public static void inCauA(int[][] a) {
        System.out.println("a. So le in gia tri, so chan thay bang 'X':");
        for (int[] row : a) {
            for (int val : row) {
                if (val % 2 != 0) System.out.printf("%6d", val);
                else System.out.printf("%6s", "X");
            }
            System.out.println();
        }
    }

    public static void inCauB(int[][] a) {
        int n = a.length;
        System.out.println("b. Duong cheo chinh va phu in gia tri, con lai in khoang trang:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j || i + j == n - 1) System.out.printf("%6d", a[i][j]);
                else System.out.printf("%6s", "");
            }
            System.out.println();
        }
    }

    public static void inCauC(int[][] a) {
        int n = a.length;
        System.out.println("c. Khong nam tren duong cheo in gia tri, duong cheo in khoang trang:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && i + j != n - 1) System.out.printf("%6d", a[i][j]);
                else System.out.printf("%6s", "");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 46: In ma trận vuông theo các quy ước đặc biệt (a, b, c) ===");
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
        inCauA(a);
        inCauB(a);
        inCauC(a);
        sc.close();
    }
}
