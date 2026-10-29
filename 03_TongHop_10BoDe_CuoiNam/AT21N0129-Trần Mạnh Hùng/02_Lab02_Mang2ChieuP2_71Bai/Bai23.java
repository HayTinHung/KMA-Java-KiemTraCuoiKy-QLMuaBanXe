import java.util.Scanner;

/**
 * Bài 23: Liệt kê các dòng có nhiều số chẵn nhất
 */
public class Bai23 {

    public static void lietKeDongNhieuChanNhat(int[][] a) {
        int maxDem = -1;
        int[] dem = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int count = 0;
            for (int val : a[i]) {
                if (val % 2 == 0) count++;
            }
            dem[i] = count;
            if (count > maxDem) maxDem = count;
        }
        System.out.printf("So luong so chan nhieu nhat bang %d. Cac dong: ", maxDem);
        for (int i = 0; i < a.length; i++) {
            if (dem[i] == maxDem) System.out.print(i + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 23: Liệt kê các dòng có nhiều số chẵn nhất ===");
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
        lietKeDongNhieuChanNhat(a);
        sc.close();
    }
}
