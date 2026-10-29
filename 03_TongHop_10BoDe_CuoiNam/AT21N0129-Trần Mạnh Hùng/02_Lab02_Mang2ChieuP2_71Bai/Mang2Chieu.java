import java.util.Random;
import java.util.Scanner;

public class Mang2Chieu {

    public static int[][] nhap(Scanner sc) {
        System.out.print("Nhap so dong n: ");
        int n = sc.nextInt();
        System.out.print("Nhap so cot m: ");
        int m = sc.nextInt();
        int[][] a = new int[n][m];
        System.out.println("Nhap cac phan tu cua mang:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.printf("a[%d][%d] = ", i, j);
                a[i][j] = sc.nextInt();
            }
        }
        return a;
    }

    public static int[][] taoNgauNhien(int n, int m, int min, int max) {
        Random rand = new Random();
        int[][] a = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                a[i][j] = rand.nextInt(max - min + 1) + min;
            }
        }
        return a;
    }

    public static void xuat(int[][] a) {
        int n = a.length;
        int m = a[0].length;
        System.out.println("Mang 2 chieu (" + n + "x" + m + "):");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.printf("%6d", a[i][j]);
            }
            System.out.println();
        }
    }

    public static int tongDong(int[][] a, int r) {
        int tong = 0;
        for (int j = 0; j < a[r].length; j++) {
            tong += a[r][j];
        }
        return tong;
    }

    public static int tongCot(int[][] a, int c) {
        int tong = 0;
        for (int i = 0; i < a.length; i++) {
            tong += a[i][c];
        }
        return tong;
    }

    public static int maxMang(int[][] a) {
        int max = a[0][0];
        for (int[] row : a) {
            for (int val : row) {
                if (val > max) max = val;
            }
        }
        return max;
    }

    public static int minMang(int[][] a) {
        int min = a[0][0];
        for (int[] row : a) {
            for (int val : row) {
                if (val < min) min = val;
            }
        }
        return min;
    }
}
