import java.util.Scanner;

/**
 * Bài 57: Tìm giá trị X trên đường chéo phụ
 */
public class Bai57 {

    public static int[] timXCheoPhu(int[][] a, int x) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            if (a[i][n - 1 - i] == x) return new int[]{i, n - 1 - i};
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 57: Tìm giá trị X trên đường chéo phụ ===");
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
        System.out.print("Nhap X can tim: ");
        int x = sc.nextInt();
        int[] pos = timXCheoPhu(a, x);
        if (pos[0] != -1) System.out.printf("Tim thay X = %d tai vi tri [%d, %d]\n", x, pos[0], pos[1]);
        else System.out.println("Khong tim thay X tren duong cheo phu.");
        sc.close();
    }
}
