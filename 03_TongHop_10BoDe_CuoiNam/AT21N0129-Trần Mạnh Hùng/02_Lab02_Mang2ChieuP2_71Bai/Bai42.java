import java.util.Scanner;

/**
 * Bài 42: Hoán vị hai cột c1, c2
 */
public class Bai42 {

    public static boolean hoanViCot(int[][] a, int c1, int c2) {
        int m = a[0].length;
        if (c1 < 0 || c1 >= m || c2 < 0 || c2 >= m) return false;
        for (int i = 0; i < a.length; i++) {
            int temp = a[i][c1];
            a[i][c1] = a[i][c2];
            a[i][c2] = temp;
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 42: Hoán vị hai cột c1, c2 ===");
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
        System.out.print("Nhap c1 va c2: ");
        int c1 = sc.nextInt();
        int c2 = sc.nextInt();
        if (hoanViCot(a, c1, c2)) {
            System.out.printf("Sau khi hoan vi cot %d va %d:\n", c1, c2);
            Mang2Chieu.xuat(a);
        } else {
            System.out.println("Cot nhap vao khong hop le!");
        }
        sc.close();
    }
}
