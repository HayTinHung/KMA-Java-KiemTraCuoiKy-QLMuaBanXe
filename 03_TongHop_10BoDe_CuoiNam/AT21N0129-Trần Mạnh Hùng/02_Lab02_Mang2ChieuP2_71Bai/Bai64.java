import java.util.Scanner;

/**
 * Bài 64: Tìm giá trị lớn nhất trên đường chéo chính
 */
public class Bai64 {

    public static int maxCheoChinh(int[][] a) {
        int n = a.length;
        int max = a[0][0];
        for (int i = 1; i < n; i++) {
            if (a[i][i] > max) max = a[i][i];
        }
        return max;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 64: Tìm giá trị lớn nhất trên đường chéo chính ===");
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
        System.out.println("Max duong cheo chinh: " + maxCheoChinh(a));
        sc.close();
    }
}
