import java.util.Scanner;

/**
 * Bài 31: Số lượng số x xuất hiện trong mảng hai chiều
 */
public class Bai31 {

    public static int demXuatHienX(int[][] a, int x) {
        int count = 0;
        for (int[] row : a) {
            for (int val : row) {
                if (val == x) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 31: Số lượng số x xuất hiện trong mảng hai chiều ===");
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
        System.out.print("Nhap gia tri x can dem: ");
        int x = sc.nextInt();
        System.out.printf("So luong phan tu bang %d: %d\n", x, demXuatHienX(a, x));
        sc.close();
    }
}
