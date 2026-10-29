import java.util.Scanner;

/**
 * Bài 18: Liệt kê các dòng toàn âm
 */
public class Bai18 {

    public static void lietKeDongToanAm(int[][] a) {
        System.out.print("Cac dong toan am: ");
        boolean found = false;
        for (int i = 0; i < a.length; i++) {
            boolean toanAm = true;
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] >= 0) {
                    toanAm = false;
                    break;
                }
            }
            if (toanAm) {
                System.out.print(i + " ");
                found = true;
            }
        }
        if (!found) System.out.print("Khong co");
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 18: Liệt kê các dòng toàn âm ===");
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
        lietKeDongToanAm(a);
        sc.close();
    }
}
