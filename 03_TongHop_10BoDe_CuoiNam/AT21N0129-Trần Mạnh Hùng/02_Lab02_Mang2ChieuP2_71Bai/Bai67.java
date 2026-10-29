import java.util.Scanner;

/**
 * Bài 67: Đếm giá trị là số nguyên tố trên đường chéo phụ
 */
public class Bai67 {

    public static int demNguyenToCheoPhu(int[][] a) {
        int n = a.length, count = 0;
        for (int i = 0; i < n; i++) {
            if (SoHoc.laSoNguyenTo(a[i][n - 1 - i])) count++;
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 67: Đếm giá trị là số nguyên tố trên đường chéo phụ ===");
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
        System.out.println("So luong so nguyen to tren duong cheo phu: " + demNguyenToCheoPhu(a));
        sc.close();
    }
}
