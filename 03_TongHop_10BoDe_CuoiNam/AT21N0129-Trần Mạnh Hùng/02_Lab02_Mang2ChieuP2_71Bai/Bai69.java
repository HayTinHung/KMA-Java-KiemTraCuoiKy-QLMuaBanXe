import java.util.Scanner;

/**
 * Bài 69: Đếm giá trị là số hoàn thiện trong tam giác dưới của đường chéo chính
 */
public class Bai69 {

    public static int demHoanThienTamGiacDuoiCheoChinh(int[][] a) {
        int n = a.length, count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (SoHoc.laSoHoanThien(a[i][j])) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 69: Đếm giá trị là số hoàn thiện trong tam giác dưới của đường chéo chính ===");
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
        System.out.println("So hoan thien tam giac duoi cheo chinh: " + demHoanThienTamGiacDuoiCheoChinh(a));
        sc.close();
    }
}
