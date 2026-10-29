import java.util.Scanner;

/**
 * Bài 63: Tìm giá trị nhỏ nhất trong tam giác dưới của đường chéo phụ (dạng 2)
 */
public class Bai63 {

    public static int minTamGiacDuoiCheoPhu(int[][] a) {
        int n = a.length;
        int min = a[1][n - 1];
        for (int i = 0; i < n; i++) {
            for (int j = n - i; j < n; j++) {
                if (a[i][j] < min) min = a[i][j];
            }
        }
        return min;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 63: Tìm giá trị nhỏ nhất trong tam giác dưới của đường chéo phụ (dạng 2) ===");
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
        System.out.println("Min tam giac duoi cheo phu: " + minTamGiacDuoiCheoPhu(a));
        sc.close();
    }
}
