import java.util.Scanner;

/**
 * Bài 48: Tính tổng các số lẻ trên đường chéo chính và mở rộng
 */
public class Bai48 {

    public static int tongLeCheoChinh(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) {
            if (a[i][i] % 2 != 0) tong += a[i][i];
        }
        return tong;
    }

    public static int tongDuongCheoChinh(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) {
            if (a[i][i] >= 0) tong += a[i][i];
        }
        return tong;
    }

    public static double tbcCheoChinh(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) tong += a[i][i];
        return (double) tong / n;
    }

    public static int tongNguyenToCheoChinh(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) {
            if (SoHoc.laSoNguyenTo(a[i][i])) tong += a[i][i];
        }
        return tong;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 48: Tính tổng các số lẻ trên đường chéo chính và mở rộng ===");
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
        System.out.println("Tong le cheo chinh: " + tongLeCheoChinh(a));
        System.out.println("Mo rong (i) Tong duong: " + tongDuongCheoChinh(a));
        System.out.println("Mo rong (ii) TBC cheo chinh: " + tbcCheoChinh(a));
        System.out.println("Mo rong (iii) Tong nguyen to: " + tongNguyenToCheoChinh(a));
        sc.close();
    }
}
