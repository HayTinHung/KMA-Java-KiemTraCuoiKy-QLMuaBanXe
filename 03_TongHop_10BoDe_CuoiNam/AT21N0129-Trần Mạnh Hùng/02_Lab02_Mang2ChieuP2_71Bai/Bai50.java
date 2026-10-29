import java.util.Scanner;

/**
 * Bài 50: Tính tổng các số nguyên tố trên đường chéo phụ và mở rộng
 */
public class Bai50 {

    public static int tongNguyenToCheoPhu(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) {
            int val = a[i][n - 1 - i];
            if (SoHoc.laSoNguyenTo(val)) tong += val;
        }
        return tong;
    }

    public static int tongDuongCheoPhu(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) {
            int val = a[i][n - 1 - i];
            if (val >= 0) tong += val;
        }
        return tong;
    }

    public static double tbcCheoPhu(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) tong += a[i][n - 1 - i];
        return (double) tong / n;
    }

    public static int tongLeCheoPhu(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) {
            int val = a[i][n - 1 - i];
            if (val % 2 != 0) tong += val;
        }
        return tong;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 50: Tính tổng các số nguyên tố trên đường chéo phụ và mở rộng ===");
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
        System.out.println("Tong nguyen to cheo phu: " + tongNguyenToCheoPhu(a));
        System.out.println("Mo rong (i) Tong duong: " + tongDuongCheoPhu(a));
        System.out.println("Mo rong (ii) TBC cheo phu: " + tbcCheoPhu(a));
        System.out.println("Mo rong (iii) Tong le cheo phu: " + tongLeCheoPhu(a));
        sc.close();
    }
}
