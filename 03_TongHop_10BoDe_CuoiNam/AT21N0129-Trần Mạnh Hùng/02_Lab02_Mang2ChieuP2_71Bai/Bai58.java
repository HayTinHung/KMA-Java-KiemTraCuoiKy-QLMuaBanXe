import java.util.Scanner;

/**
 * Bài 58: Tìm giá trị X trong tam giác trên của đường chéo chính
 */
public class Bai58 {

    public static int[] timXTamGiacTrenCheoChinh(int[][] a, int x) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (a[i][j] == x) return new int[]{i, j};
            }
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 58: Tìm giá trị X trong tam giác trên của đường chéo chính ===");
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
        int[] pos = timXTamGiacTrenCheoChinh(a, x);
        if (pos[0] != -1) System.out.printf("Tim thay X = %d tai [%d, %d]\n", x, pos[0], pos[1]);
        else System.out.println("Khong tim thay X.");
        sc.close();
    }
}
