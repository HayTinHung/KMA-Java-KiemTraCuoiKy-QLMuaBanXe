import java.util.Scanner;

/**
 * Bài 52: Tính tổng các số lẻ có trong tam giác trên/dưới
 */
public class Bai52 {

    public static int tongLeTamGiacTrenCheoChinh(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (a[i][j] % 2 != 0) tong += a[i][j];
            }
        }
        return tong;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 52: Tính tổng các số lẻ có trong tam giác trên/dưới ===");
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
        System.out.println("Tong so le tam giac tren cheo chinh: " + tongLeTamGiacTrenCheoChinh(a));
        sc.close();
    }
}
