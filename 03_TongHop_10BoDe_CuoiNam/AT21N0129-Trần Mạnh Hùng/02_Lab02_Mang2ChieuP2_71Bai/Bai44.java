import java.util.Scanner;

/**
 * Bài 44: Phát sinh ngẫu nhiên ma trận vuông sắp xếp tăng/giảm
 */
public class Bai44 {

    public static int[][] taoMaTranSapXep(int n, boolean tangDan) {
        int total = n * n;
        int[] arr = new int[total];
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < total; i++) {
            arr[i] = rand.nextInt(100);
        }
        java.util.Arrays.sort(arr);
        if (!tangDan) {
            for (int i = 0; i < total / 2; i++) {
                int temp = arr[i];
                arr[i] = arr[total - 1 - i];
                arr[total - 1 - i] = temp;
            }
        }
        int[][] a = new int[n][n];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = arr[idx++];
            }
        }
        return a;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 44: Phát sinh ngẫu nhiên ma trận vuông sắp xếp tăng/giảm ===");
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
        System.out.println("Ma tran sap xep tang dan:");
        Mang2Chieu.xuat(taoMaTranSapXep(n, true));
        System.out.println("Ma tran sap xep giam dan (mo rong):");
        Mang2Chieu.xuat(taoMaTranSapXep(n, false));
        sc.close();
    }
}
