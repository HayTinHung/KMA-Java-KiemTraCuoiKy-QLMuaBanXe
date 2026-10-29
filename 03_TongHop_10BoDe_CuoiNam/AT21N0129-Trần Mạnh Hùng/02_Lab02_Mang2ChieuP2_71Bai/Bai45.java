import java.util.Scanner;

/**
 * Bài 45: Phát sinh ngẫu nhiên ma trận vuông theo tính chẵn lẻ của (i+j)
 */
public class Bai45 {

    public static int[][] taoMaTranChanLeViTri(int n) {
        int[][] a = new int[n][n];
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int val = rand.nextInt(50) + 1;
                if ((i + j) % 2 == 0) {
                    if (val % 2 != 0) val++;
                } else {
                    if (val % 2 == 0) val++;
                }
                a[i][j] = val;
            }
        }
        return a;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 45: Phát sinh ngẫu nhiên ma trận vuông theo tính chẵn lẻ của (i+j) ===");
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
        System.out.print("Nhap n: ");
        int n = sc.nextInt();
        a = taoMaTranChanLeViTri(n);
        Mang2Chieu.xuat(a);
        sc.close();
    }
}
