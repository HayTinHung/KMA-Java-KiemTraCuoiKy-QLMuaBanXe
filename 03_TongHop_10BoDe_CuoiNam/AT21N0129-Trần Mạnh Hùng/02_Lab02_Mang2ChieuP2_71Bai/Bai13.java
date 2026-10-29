import java.util.Scanner;

/**
 * Bài 13: Tìm dòng có tổng lớn nhất trong mảng
 */
public class Bai13 {

    public static int timDongTongMax(int[][] a) {
        int n = a.length;
        int maxDong = 0;
        int maxTong = Mang2Chieu.tongDong(a, 0);
        for (int i = 1; i < n; i++) {
            int tong = Mang2Chieu.tongDong(a, i);
            if (tong > maxTong) {
                maxTong = tong;
                maxDong = i;
            }
        }
        return maxDong;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 13: Tìm dòng có tổng lớn nhất trong mảng ===");
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
        int dong = timDongTongMax(a);
        System.out.printf("Dong %d co tong lon nhat = %d\n", dong, Mang2Chieu.tongDong(a, dong));
        sc.close();
    }
}
