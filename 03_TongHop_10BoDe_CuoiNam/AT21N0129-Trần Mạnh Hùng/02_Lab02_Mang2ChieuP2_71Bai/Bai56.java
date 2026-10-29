import java.util.Scanner;

/**
 * Bài 56: Tìm giá trị X trên đường chéo chính
 */
public class Bai56 {

    public static int timXCheoChinh(int[][] a, int x) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            if (a[i][i] == x) return i;
        }
        return -1;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 56: Tìm giá trị X trên đường chéo chính ===");
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
        System.out.print("Nhap X can tim: ");
        int x = sc.nextInt();
        int idx = timXCheoChinh(a, x);
        if (idx != -1) System.out.printf("Tim thay X = %d tai vi tri [%d, %d]\n", x, idx, idx);
        else System.out.println("Khong tim thay X tren duong cheo chinh.");
        sc.close();
    }
}
