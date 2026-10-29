import java.util.Scanner;

/**
 * Bài 40: Đếm số lượng giá trị chẵn nhỏ nhất trong mảng
 */
public class Bai40 {

    public static int demGiaTriChanMin(int[][] a) {
        int minChan = Integer.MAX_VALUE;
        for (int[] row : a) {
            for (int val : row) {
                if (val % 2 == 0 && val < minChan) {
                    minChan = val;
                }
            }
        }
        if (minChan == Integer.MAX_VALUE) return 0;
        int count = 0;
        for (int[] row : a) {
            for (int val : row) {
                if (val == minChan) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 40: Đếm số lượng giá trị chẵn nhỏ nhất trong mảng ===");
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
        System.out.println("So luong phan tu bang gia tri chan nho nhat: " + demGiaTriChanMin(a));
        sc.close();
    }
}
