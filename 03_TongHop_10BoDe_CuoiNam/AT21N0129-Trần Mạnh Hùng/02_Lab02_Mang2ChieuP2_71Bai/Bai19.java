import java.util.Scanner;

/**
 * Bài 19: Liệt kê chỉ số các dòng chứa toàn giá trị chẵn
 */
public class Bai19 {

    public static void lietKeDongToanChan(int[][] a) {
        System.out.print("Chi so cac dong toan chan: ");
        boolean found = false;
        for (int i = 0; i < a.length; i++) {
            boolean toanChan = true;
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] % 2 != 0) {
                    toanChan = false;
                    break;
                }
            }
            if (toanChan) {
                System.out.print(i + " ");
                found = true;
            }
        }
        if (!found) System.out.print("Khong co");
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 19: Liệt kê chỉ số các dòng chứa toàn giá trị chẵn ===");
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
        lietKeDongToanChan(a);
        sc.close();
    }
}
