import java.util.Scanner;

/**
 * Bài 30: Số lần xuất hiện của số nhỏ nhất trong mảng hai chiều
 */
public class Bai30 {

    public static int demSoNhoNhat(int[][] a) {
        int min = Mang2Chieu.minMang(a);
        int count = 0;
        for (int[] row : a) {
            for (int val : row) {
                if (val == min) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 30: Số lần xuất hiện của số nhỏ nhất trong mảng hai chiều ===");
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
        int min = Mang2Chieu.minMang(a);
        System.out.printf("Gia tri nho nhat = %d, so lan xuat hien = %d\n", min, demSoNhoNhat(a));
        sc.close();
    }
}
