import java.util.Scanner;

/**
 * Bài 11: Tìm vị trí số hoàn thiện đầu tiên trong mảng
 */
public class Bai11 {

    public static int[] timHoanThienDauTien(int[][] a) {
        int n = a.length, m = a[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (SoHoc.laSoHoanThien(a[i][j])) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 11: Tìm vị trí số hoàn thiện đầu tiên trong mảng ===");
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
        int[] pos = timHoanThienDauTien(a);
        if (pos[0] != -1) {
            System.out.printf("So hoan thien dau tien: %d tai vi tri [%d, %d]\n", a[pos[0]][pos[1]], pos[0], pos[1]);
        } else {
            System.out.println("Khong co so hoan thien trong mang.");
        }
        sc.close();
    }
}
