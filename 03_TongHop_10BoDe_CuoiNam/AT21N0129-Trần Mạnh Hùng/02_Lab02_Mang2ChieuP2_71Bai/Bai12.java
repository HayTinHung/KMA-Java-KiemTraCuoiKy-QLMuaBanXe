import java.util.Scanner;

/**
 * Bài 12: Tìm cột có tổng nhỏ nhất trong mảng
 */
public class Bai12 {

    public static int timCotTongMin(int[][] a) {
        int m = a[0].length;
        int minCot = 0;
        int minTong = Mang2Chieu.tongCot(a, 0);
        for (int j = 1; j < m; j++) {
            int tong = Mang2Chieu.tongCot(a, j);
            if (tong < minTong) {
                minTong = tong;
                minCot = j;
            }
        }
        return minCot;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 12: Tìm cột có tổng nhỏ nhất trong mảng ===");
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
        int cot = timCotTongMin(a);
        System.out.printf("Cot %d co tong nho nhat = %d\n", cot, Mang2Chieu.tongCot(a, cot));
        sc.close();
    }
}
