import java.util.Scanner;

/**
 * Bài 25: Liệt kê các dòng có nhiều số hoàn thiện nhất
 */
public class Bai25 {

    public static void lietKeDongNhieuHoanThienNhat(int[][] a) {
        int maxDem = -1;
        int[] dem = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int count = 0;
            for (int val : a[i]) {
                if (SoHoc.laSoHoanThien(val)) count++;
            }
            dem[i] = count;
            if (count > maxDem) maxDem = count;
        }
        System.out.printf("So luong so hoan thien nhieu nhat bang %d. Cac dong: ", maxDem);
        for (int i = 0; i < a.length; i++) {
            if (dem[i] == maxDem) System.out.print(i + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 25: Liệt kê các dòng có nhiều số hoàn thiện nhất ===");
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
        lietKeDongNhieuHoanThienNhat(a);
        sc.close();
    }
}
