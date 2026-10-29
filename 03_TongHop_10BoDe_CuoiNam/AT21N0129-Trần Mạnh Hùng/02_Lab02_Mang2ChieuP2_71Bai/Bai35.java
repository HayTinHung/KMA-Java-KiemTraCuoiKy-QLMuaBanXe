import java.util.Scanner;

/**
 * Bài 35: Đếm giá trị âm có trên từng dòng
 */
public class Bai35 {

    public static void demAmTrenTungDong(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            int count = 0;
            for (int val : a[i]) {
                if (val < 0) count++;
            }
            System.out.printf("Dong %d: %d so am\n", i, count);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 35: Đếm giá trị âm có trên từng dòng ===");
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
        demAmTrenTungDong(a);
        sc.close();
    }
}
