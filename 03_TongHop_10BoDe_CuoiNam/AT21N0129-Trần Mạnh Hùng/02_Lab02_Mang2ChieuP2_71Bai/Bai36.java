import java.util.Scanner;

/**
 * Bài 36: Đếm giá trị là số nguyên tố có trên từng cột
 */
public class Bai36 {

    public static void demNguyenToTrenTungCot(int[][] a) {
        int m = a[0].length;
        for (int j = 0; j < m; j++) {
            int count = 0;
            for (int i = 0; i < a.length; i++) {
                if (SoHoc.laSoNguyenTo(a[i][j])) count++;
            }
            System.out.printf("Cot %d: %d so nguyen to\n", j, count);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 36: Đếm giá trị là số nguyên tố có trên từng cột ===");
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
        demNguyenToTrenTungCot(a);
        sc.close();
    }
}
