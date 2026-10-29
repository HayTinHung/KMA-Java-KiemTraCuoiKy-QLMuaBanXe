import java.util.Scanner;

/**
 * Bài 66: Đếm giá trị chẵn trên đường chéo chính
 */
public class Bai66 {

    public static int demChanCheoChinh(int[][] a) {
        int n = a.length, count = 0;
        for (int i = 0; i < n; i++) {
            if (a[i][i] % 2 == 0) count++;
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 66: Đếm giá trị chẵn trên đường chéo chính ===");
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
        System.out.println("So luong gia tri chan tren duong cheo chinh: " + demChanCheoChinh(a));
        sc.close();
    }
}
