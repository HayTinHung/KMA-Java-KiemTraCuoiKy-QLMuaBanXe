import java.util.Scanner;

/**
 * Bài 54: Tính TBC các số trong tam giác
 */
public class Bai54 {

    public static double tbcTamGiacTrenCheoChinh(int[][] a) {
        int n = a.length, tong = 0, count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                tong += a[i][j];
                count++;
            }
        }
        return count == 0 ? 0 : (double) tong / count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 54: Tính TBC các số trong tam giác ===");
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
        System.out.println("TBC cac so tam giac tren cheo chinh: " + tbcTamGiacTrenCheoChinh(a));
        sc.close();
    }
}
