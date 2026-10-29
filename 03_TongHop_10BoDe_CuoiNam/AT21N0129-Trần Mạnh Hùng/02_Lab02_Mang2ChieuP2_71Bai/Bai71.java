import java.util.Scanner;

/**
 * Bài 71: Đếm giá trị là số chính phương trong tam giác dưới của đường chéo phụ
 */
public class Bai71 {

    public static int demChinhPhuongTamGiacDuoiCheoPhu(int[][] a) {
        int n = a.length, count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = n - i; j < n; j++) {
                if (SoHoc.laSoChinhPhuong(a[i][j])) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 71: Đếm giá trị là số chính phương trong tam giác dưới của đường chéo phụ ===");
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
        System.out.println("So chinh phuong tam giac duoi cheo phu: " + demChinhPhuongTamGiacDuoiCheoPhu(a));
        sc.close();
    }
}
