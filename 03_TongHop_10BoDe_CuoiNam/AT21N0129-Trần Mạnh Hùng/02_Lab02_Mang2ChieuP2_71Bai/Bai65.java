import java.util.Scanner;

/**
 * Bài 65: Tìm giá trị lớn nhất trên đường chéo phụ
 */
public class Bai65 {

    public static int maxCheoPhu(int[][] a) {
        int n = a.length;
        int max = a[0][n - 1];
        for (int i = 1; i < n; i++) {
            if (a[i][n - 1 - i] > max) max = a[i][n - 1 - i];
        }
        return max;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 65: Tìm giá trị lớn nhất trên đường chéo phụ ===");
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
        System.out.println("Max duong cheo phu: " + maxCheoPhu(a));
        sc.close();
    }
}
