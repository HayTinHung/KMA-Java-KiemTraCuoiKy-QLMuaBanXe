import java.util.Scanner;

/**
 * Bài 61: Tìm giá trị X trong tam giác dưới của đường chéo phụ
 */
public class Bai61 {

    public static int[] timXTamGiacDuoiCheoPhu(int[][] a, int x) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            for (int j = n - i; j < n; j++) {
                if (a[i][j] == x) return new int[]{i, j};
            }
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 61: Tìm giá trị X trong tam giác dưới của đường chéo phụ ===");
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
        System.out.print("Nhap X: ");
        int x = sc.nextInt();
        int[] pos = timXTamGiacDuoiCheoPhu(a, x);
        if (pos[0] != -1) System.out.printf("Tim thay X = %d tai [%d, %d]\n", x, pos[0], pos[1]);
        else System.out.println("Khong tim thay X.");
        sc.close();
    }
}
