import java.util.Scanner;

/**
 * Bài 34: Số lượng số nguyên tố nhỏ hơn x
 */
public class Bai34 {

    public static int demNguyenToNhoHonX(int[][] a, int x) {
        int count = 0;
        for (int[] row : a) {
            for (int val : row) {
                if (val < x && SoHoc.laSoNguyenTo(val)) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 34: Số lượng số nguyên tố nhỏ hơn x ===");
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
        System.out.print("Nhap gia tri x: ");
        int x = sc.nextInt();
        System.out.printf("So luong so nguyen to nho hon %d: %d\n", x, demNguyenToNhoHonX(a, x));
        sc.close();
    }
}
