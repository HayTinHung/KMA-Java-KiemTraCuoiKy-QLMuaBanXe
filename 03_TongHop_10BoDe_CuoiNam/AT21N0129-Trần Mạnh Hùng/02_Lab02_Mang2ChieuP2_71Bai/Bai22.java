import java.util.Scanner;

/**
 * Bài 22: Liệt kê các cột có tổng cột nhỏ nhất
 */
public class Bai22 {

    public static void lietKeCotTongMin(int[][] a) {
        int m = a[0].length;
        int minTong = Mang2Chieu.tongCot(a, 0);
        for (int j = 1; j < m; j++) {
            int t = Mang2Chieu.tongCot(a, j);
            if (t < minTong) minTong = t;
        }
        System.out.printf("Tong nho nhat bang %d. Cac cot co tong bang %d: ", minTong, minTong);
        for (int j = 0; j < m; j++) {
            if (Mang2Chieu.tongCot(a, j) == minTong) {
                System.out.print(j + " ");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 22: Liệt kê các cột có tổng cột nhỏ nhất ===");
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
        lietKeCotTongMin(a);
        sc.close();
    }
}
