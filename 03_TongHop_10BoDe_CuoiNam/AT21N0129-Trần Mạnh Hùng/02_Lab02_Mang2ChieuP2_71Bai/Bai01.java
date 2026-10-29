import java.util.Scanner;

/**
 * Bài 1: Tìm vị trí phần tử lớn nhất trong mảng
 */
public class Bai01 {

    public static int[] timViTriMax(int[][] a) {
        int n = a.length, m = a[0].length;
        int maxR = 0, maxC = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (a[i][j] > a[maxR][maxC]) {
                    maxR = i;
                    maxC = j;
                }
            }
        }
        return new int[]{maxR, maxC};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 1: Tìm vị trí phần tử lớn nhất trong mảng ===");
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
        int[] pos = timViTriMax(a);
        System.out.printf("Phan tu lon nhat: %d tai vi tri [%d, %d]\n", a[pos[0]][pos[1]], pos[0], pos[1]);
        sc.close();
    }
}
