import java.util.Scanner;

/**
 * Bài 17: Liệt kê các cột có chứa số chính phương
 */
public class Bai17 {

    public static void lietKeCotChuaChinhPhuong(int[][] a) {
        System.out.print("Cac cot chua so chinh phuong: ");
        boolean found = false;
        int m = a[0].length;
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < a.length; i++) {
                if (SoHoc.laSoChinhPhuong(a[i][j])) {
                    System.out.print(j + " ");
                    found = true;
                    break;
                }
            }
        }
        if (!found) System.out.print("Khong co");
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 17: Liệt kê các cột có chứa số chính phương ===");
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
        lietKeCotChuaChinhPhuong(a);
        sc.close();
    }
}
