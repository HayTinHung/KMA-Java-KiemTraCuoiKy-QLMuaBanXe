import java.util.Scanner;

/**
 * Bài 20: Liệt kê các dòng có giá trị giảm dần
 */
public class Bai20 {

    public static void lietKeDongGiamDan(int[][] a) {
        System.out.print("Cac dong co gia tri giam dan: ");
        boolean found = false;
        for (int i = 0; i < a.length; i++) {
            boolean giam = true;
            for (int j = 0; j < a[i].length - 1; j++) {
                if (a[i][j] < a[i][j + 1]) {
                    giam = false;
                    break;
                }
            }
            if (giam) {
                System.out.print(i + " ");
                found = true;
            }
        }
        if (!found) System.out.print("Khong co");
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 20: Liệt kê các dòng có giá trị giảm dần ===");
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
        lietKeDongGiamDan(a);
        sc.close();
    }
}
