import java.util.Scanner;

/**
 * Bài 8: Tìm vị trí số nguyên tố lớn nhất trong mảng
 */
public class Bai08 {

    public static int[] timNguyenToMax(int[][] a) {
        int n = a.length, m = a[0].length;
        int maxR = -1, maxC = -1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (SoHoc.laSoNguyenTo(a[i][j])) {
                    if (maxR == -1 || a[i][j] > a[maxR][maxC]) {
                        maxR = i;
                        maxC = j;
                    }
                }
            }
        }
        return new int[]{maxR, maxC};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 8: Tìm vị trí số nguyên tố lớn nhất trong mảng ===");
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
        int[] pos = timNguyenToMax(a);
        if (pos[0] != -1) {
            System.out.printf("So nguyen to lon nhat: %d tai vi tri [%d, %d]\n", a[pos[0]][pos[1]], pos[0], pos[1]);
        } else {
            System.out.println("Khong co so nguyen to trong mang.");
        }
        sc.close();
    }
}
