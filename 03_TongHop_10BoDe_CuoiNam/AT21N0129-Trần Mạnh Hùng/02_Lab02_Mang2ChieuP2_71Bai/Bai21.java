import java.util.Scanner;

/**
 * Bài 21: Liệt kê các dòng có tổng dòng lớn nhất
 */
public class Bai21 {

    public static void lietKeDongTongMax(int[][] a) {
        int maxTong = Mang2Chieu.tongDong(a, 0);
        for (int i = 1; i < a.length; i++) {
            int t = Mang2Chieu.tongDong(a, i);
            if (t > maxTong) maxTong = t;
        }
        System.out.printf("Tong lon nhat bang %d. Cac dong co tong bang %d: ", maxTong, maxTong);
        for (int i = 0; i < a.length; i++) {
            if (Mang2Chieu.tongDong(a, i) == maxTong) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Bài 21: Liệt kê các dòng có tổng dòng lớn nhất ===");
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
        lietKeDongTongMax(a);
        sc.close();
    }
}
