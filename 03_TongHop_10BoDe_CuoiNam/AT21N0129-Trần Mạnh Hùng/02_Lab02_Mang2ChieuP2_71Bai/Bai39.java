import java.util.Scanner;

/**
 * Bài 39: Đếm số lượng giá trị nhỏ nhất trong mảng
 */
public class Bai39 {

    public static int demGiaTriMin(int[][] a) {
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
        System.out.println("=== Bài 39: Đếm số lượng giá trị nhỏ nhất trong mảng ===");
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
        System.out.printf("Min = %d, So luong = %d\n", min, demGiaTriMin(a));
        sc.close();
    }
}
