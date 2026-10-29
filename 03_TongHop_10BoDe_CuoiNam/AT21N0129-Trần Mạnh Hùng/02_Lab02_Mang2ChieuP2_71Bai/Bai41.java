import java.util.Scanner;

/**
 * Bài 41: Hoán vị hai dòng r1, r2
 */
public class Bai41 {

    public static boolean hoanViDong(int[][] a, int r1, int r2) {
        int n = a.length;
        if (r1 < 0 || r1 >= n || r2 < 0 || r2 >= n) return false;
        int[] temp = a[r1];
        a[r1] = a[r2];
        a[r2] = temp;
        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 41: Hoán vị hai dòng r1, r2 ===");
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
        System.out.print("Nhap r1 va r2: ");
        int r1 = sc.nextInt();
        int r2 = sc.nextInt();
        if (hoanViDong(a, r1, r2)) {
            System.out.printf("Sau khi hoan vi dong %d va %d:\n", r1, r2);
            Mang2Chieu.xuat(a);
        } else {
            System.out.println("Dong nhap vao khong hop le!");
        }
        sc.close();
    }
}
