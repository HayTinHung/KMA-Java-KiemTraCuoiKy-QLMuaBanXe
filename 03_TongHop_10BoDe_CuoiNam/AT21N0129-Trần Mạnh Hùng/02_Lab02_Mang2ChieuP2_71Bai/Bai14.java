import java.util.Scanner;

/**
 * Bài 14: Liệt kê các dòng có chứa giá trị âm
 */
public class Bai14 {

    public static void lietKeDongChuaAm(int[][] a) {
        System.out.print("Cac dong chua gia tri am: ");
        boolean found = false;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] < 0) {
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
        System.out.println("=== Bài 14: Liệt kê các dòng có chứa giá trị âm ===");
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
        lietKeDongChuaAm(a);
        sc.close();
    }
}
