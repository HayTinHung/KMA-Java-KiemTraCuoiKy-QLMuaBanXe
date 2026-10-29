import java.util.Scanner;

/**
 * Bài 15: Liệt kê các dòng có chứa số nguyên tố
 */
public class Bai15 {

    public static void lietKeDongChuaNguyenTo(int[][] a) {
        System.out.print("Cac dong chua so nguyen to: ");
        boolean found = false;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (SoHoc.laSoNguyenTo(a[i][j])) {
                    System.out.print(i + " ");
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
        System.out.println("=== Bài 15: Liệt kê các dòng có chứa số nguyên tố ===");
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
        lietKeDongChuaNguyenTo(a);
        sc.close();
    }
}
