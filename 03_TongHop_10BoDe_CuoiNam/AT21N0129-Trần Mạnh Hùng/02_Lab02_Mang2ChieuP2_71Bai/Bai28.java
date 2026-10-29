import java.util.Scanner;

/**
 * Bài 28: Số lượng số chẵn và dương
 */
public class Bai28 {

    public static int demChanVaDuong(int[][] a) {
        int count = 0;
        for (int[] row : a) {
            for (int val : row) {
                if (val > 0 && val % 2 == 0) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 28: Số lượng số chẵn và dương ===");
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
        System.out.println("So luong so chẵn và dương: " + demChanVaDuong(a));
        sc.close();
    }
}
