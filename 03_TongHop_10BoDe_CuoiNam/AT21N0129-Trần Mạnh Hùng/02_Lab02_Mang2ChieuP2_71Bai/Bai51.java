import java.util.Scanner;

/**
 * Bài 51: Tính tổng tất cả các số có trong tam giác trên đường chéo chính
 */
public class Bai51 {

    public static int tongTamGiacTrenCheoChinh(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                tong += a[i][j];
            }
        }
        return tong;
    }

    public static int tongTamGiacDuoiCheoChinh(int[][] a) {
        int n = a.length, tong = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                tong += a[i][j];
            }
        }
        return tong;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 51: Tính tổng tất cả các số có trong tam giác trên đường chéo chính ===");
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
        System.out.println("Tong tam giac tren cheo chinh: " + tongTamGiacTrenCheoChinh(a));
        System.out.println("Tong tam giac duoi cheo chinh: " + tongTamGiacDuoiCheoChinh(a));
        sc.close();
    }
}
