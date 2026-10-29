import java.util.Scanner;

/**
 * Bài 24: Liệt kê các dòng có nhiều số nguyên tố nhất
 */
public class Bai24 {

    public static void lietKeDongNhieuNguyenToNhat(int[][] a) {
        int maxDem = -1;
        int[] dem = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int count = 0;
            for (int val : a[i]) {
                if (SoHoc.laSoNguyenTo(val)) count++;
            }
            dem[i] = count;
            if (count > maxDem) maxDem = count;
        }
        System.out.printf("So luong so nguyen to nhieu nhat bang %d. Cac dong: ", maxDem);
        for (int i = 0; i < a.length; i++) {
            if (dem[i] == maxDem) System.out.print(i + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 24: Liệt kê các dòng có nhiều số nguyên tố nhất ===");
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
        lietKeDongNhieuNguyenToNhat(a);
        sc.close();
    }
}
