import java.util.Scanner;

/**
 * Bài 62: Tìm giá trị lớn nhất trong tam giác trên của đường chéo chính (dạng 1)
 */
public class Bai62 {

    public static int maxTamGiacTrenCheoChinh(int[][] a) {
        int n = a.length;
        int max = a[0][1];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (a[i][j] > max) max = a[i][j];
            }
        }
        return max;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 62: Tìm giá trị lớn nhất trong tam giác trên của đường chéo chính (dạng 1) ===");
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
        System.out.println("Max tam giac tren cheo chinh: " + maxTamGiacTrenCheoChinh(a));
        sc.close();
    }
}
