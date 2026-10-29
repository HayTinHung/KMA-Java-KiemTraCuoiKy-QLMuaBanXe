import java.util.Scanner;

/**
 * Bài 47: Tính tổng các số trên đường chéo chính
 */
public class Bai47 {

    public static int tongCheoChinh(int[][] a) {
        int n = a.length;
        int tong = 0;
        for (int i = 0; i < n; i++) {
            tong += a[i][i];
        }
        return tong;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 47: Tính tổng các số trên đường chéo chính ===");
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
        System.out.println("Tong duong cheo chinh: " + tongCheoChinh(a));
        sc.close();
    }
}
