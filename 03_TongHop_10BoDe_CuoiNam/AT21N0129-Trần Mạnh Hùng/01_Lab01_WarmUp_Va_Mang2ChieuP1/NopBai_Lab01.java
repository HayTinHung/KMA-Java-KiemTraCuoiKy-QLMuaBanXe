import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * ============================================================================
 * TRUONG DAI HOC QUOC TE HONG BANG - KHOA CONG NGHE KY THUAT - BO MON CNTT
 * BAI THUC HANH SO 01: MANG HAI CHIEU (PHAN 1)
 * ============================================================================
 * File gop duy nhat chua toan bo cac bai tap tu Bai huong dan den Bai 1 - 15.
 * Tich hop Menu chon bai truc tiep tren Console.
 */
public class NopBai_Lab01 {

    static class Utils {
        public static int inputInt(Scanner scanner, String message, int min, int max) {
            while (true) {
                System.out.printf("%s (%d..%d): ", message, min, max);
                if (!scanner.hasNextInt()) {
                    System.out.println("Du lieu khong hop le. Vui long nhap so nguyen.");
                    scanner.next();
                    continue;
                }
                int val = scanner.nextInt();
                if (val >= min && val <= max) return val;
                System.out.printf("Gia tri phai trong khoang [%d, %d]. Nhap lai.\n", min, max);
            }
        }

        public static int inputPositiveInt(Scanner scanner, String message) {
            while (true) {
                System.out.print(message + ": ");
                if (!scanner.hasNextInt()) {
                    System.out.println("Du lieu khong hop le. Vui long nhap so nguyen.");
                    scanner.next();
                    continue;
                }
                int val = scanner.nextInt();
                if (val > 0) return val;
                System.out.println("Gia tri phai la so nguyen duong (> 0). Nhap lai.");
            }
        }

        public static int[][] inputMatrix(Scanner scanner, int rows, int columns) {
            int[][] a = new int[rows][columns];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    System.out.printf("Nhap a[%d][%d]: ", i, j);
                    while (!scanner.hasNextInt()) {
                        System.out.printf("Nhap lai a[%d][%d]: ", i, j);
                        scanner.next();
                    }
                    a[i][j] = scanner.nextInt();
                }
            }
            return a;
        }

        public static void printMatrix(int[][] matrix) {
            if (matrix == null) return;
            for (int[] row : matrix) {
                for (int val : row) {
                    System.out.printf("%8d", val);
                }
                System.out.println();
            }
        }

        public static boolean isPrime(int n) {
            if (n < 2) return false;
            for (int i = 2; i * i <= n; i++) {
                if (n % i == 0) return false;
            }
            return true;
        }

        public static boolean isPerfectNumber(int n) {
            if (n <= 1) return false;
            int sum = 1;
            for (int i = 2; i * i <= n; i++) {
                if (n % i == 0) {
                    sum += i;
                    if (i * i != n) sum += n / i;
                }
            }
            return sum == n;
        }

        public static boolean isBorder(int r, int c, int rows, int cols) {
            return r == 0 || r == rows - 1 || c == 0 || c == cols - 1;
        }

        public static boolean isLuckyNumber(int n) {
            if (n <= 0) return false;
            int t = n;
            while (t > 0) {
                int d = t % 10;
                if (d != 6 && d != 8) return false;
                t /= 10;
            }
            return true;
        }
    }

    static class BaiHuongDan {
        public static long tongMang(int[][] m) {
            long s = 0;
            for (int[] r : m) for (int v : r) s += v;
            return s;
        }

        public static long tongDuongBien(int[][] m) {
            long s = 0;
            int rows = m.length, cols = m[0].length;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (Utils.isBorder(i, j, rows, cols)) s += m[i][j];
                }
            }
            return s;
        }

        public static void lietKeSoMayMan(int[][] m) {
            boolean found = false;
            System.out.print("Cac so may man (chi gom chu so 6 va 8): ");
            for (int[] r : m) {
                for (int v : r) {
                    if (Utils.isLuckyNumber(v)) {
                        System.out.print(v + " ");
                        found = true;
                    }
                }
            }
            if (!found) System.out.print("Khong co");
            System.out.println();
        }

        public static int[][] run(Scanner sc) {
            System.out.println("\n=== BAI TAP CO HUONG DAN ===");
            int rows = Utils.inputInt(sc, "Nhap so dong", 1, 100);
            int cols = Utils.inputInt(sc, "Nhap so cot", 1, 200);
            int[][] m = Utils.inputMatrix(sc, rows, cols);
            System.out.println("Ma tran vua nhap:");
            Utils.printMatrix(m);
            System.out.println("Tong cac phan tu: " + tongMang(m));
            System.out.println("Tong duong bien: " + tongDuongBien(m));
            lietKeSoMayMan(m);
            return m;
        }
    }

    static int[][] runBai01(Scanner sc) {
        System.out.println("\n=== BAI 1: NHAP MANG SO NGUYEN DUONG (5 < n, m < 200) ===");
        int rows = Utils.inputInt(sc, "Nhap so dong n", 6, 199);
        int cols = Utils.inputInt(sc, "Nhap so cot m", 6, 199);
        int[][] m = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m[i][j] = Utils.inputPositiveInt(sc, String.format("Nhap a[%d][%d] (> 0)", i, j));
            }
        }
        System.out.println("Ma tran vua nhap:");
        Utils.printMatrix(m);
        return m;
    }

    static int[][] runBai02(Scanner sc) {
        Random rand = new Random();
        System.out.println("\n=== BAI 2: KHOI TAO MANG NGAU NHIEN (2 <= n, m < 50) ===");
        int rows = Utils.inputInt(sc, "Nhap so dong n", 2, 49);
        int cols = Utils.inputInt(sc, "Nhap so cot m", 2, 49);
        System.out.println("1. Cau a: Ngau nhien trong [-100, 100]");
        System.out.println("2. Cau b: a[i][j] = i * j");
        System.out.println("3. Cau c: Ngau nhien trong [0, 99]");
        System.out.println("4. Cau d: Toan so le");
        System.out.println("5. Cau e: Toan so nguyen to nho hon 100");
        System.out.println("6. Cau f: (i+j) le la so chan, (i+j) chan la so le");
        int sub = Utils.inputInt(sc, "Chon cau (1-6)", 1, 6);

        int[][] m = new int[rows][cols];
        switch (sub) {
            case 1:
                for (int i = 0; i < rows; i++)
                    for (int j = 0; j < cols; j++) m[i][j] = rand.nextInt(201) - 100;
                break;
            case 2:
                for (int i = 0; i < rows; i++)
                    for (int j = 0; j < cols; j++) m[i][j] = i * j;
                break;
            case 3:
                for (int i = 0; i < rows; i++)
                    for (int j = 0; j < cols; j++) m[i][j] = rand.nextInt(100);
                break;
            case 4:
                for (int i = 0; i < rows; i++)
                    for (int j = 0; j < cols; j++) m[i][j] = 2 * rand.nextInt(50) + 1;
                break;
            case 5:
                int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};
                for (int i = 0; i < rows; i++)
                    for (int j = 0; j < cols; j++) m[i][j] = primes[rand.nextInt(primes.length)];
                break;
            case 6:
                for (int i = 0; i < rows; i++)
                    for (int j = 0; j < cols; j++) {
                        if ((i + j) % 2 == 1) m[i][j] = 2 * rand.nextInt(50);
                        else m[i][j] = 2 * rand.nextInt(50) + 1;
                    }
                break;
        }
        System.out.println("Ma tran khoi tao thanh cong:");
        Utils.printMatrix(m);
        return m;
    }

    static void runBai03(Scanner sc, int[][] m) {
        if (m == null) {
            System.out.println("Chua co ma tran! Hay chon khoi tao ma tran truoc.");
            return;
        }
        System.out.println("\n=== BAI 3: CAC CACH XUAT DAC BIET ===");
        System.out.println("1. Cau a: Thay so nguyen to bang khoang trang");
        System.out.println("2. Cau b: Thay phan tu khong thuoc duong bien bang khoang trang");
        System.out.println("3. Cau c: Thay phan tu co (i + j) chan bang khoang trang");
        int sub = Utils.inputInt(sc, "Chon cau (1-3)", 1, 3);
        int rows = m.length, cols = m[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean hide = false;
                if (sub == 1) hide = Utils.isPrime(m[i][j]);
                else if (sub == 2) hide = !Utils.isBorder(i, j, rows, cols);
                else if (sub == 3) hide = ((i + j) % 2 == 0);

                if (hide) System.out.print("        ");
                else System.out.printf("%8d", m[i][j]);
            }
            System.out.println();
        }
    }

    static void runBai04Den09(int[][] m) {
        if (m == null) {
            System.out.println("Chua co ma tran! Hay chon khoi tao ma tran truoc.");
            return;
        }
        System.out.println("\n=== BAI 4 DEN BAI 9: TINH TOAN TOAN MA TRAN ===");
        long tongTatCa = 0, tongChan = 0, tongSNT = 0, tongHoanThien = 0;
        int totalCells = m.length * m[0].length;
        double logSum = 0.0;
        boolean allPositive = true;

        for (int[] row : m) {
            for (int v : row) {
                tongTatCa += v;
                if (v % 2 == 0) tongChan += v;
                if (Utils.isPrime(v)) tongSNT += v;
                if (Utils.isPerfectNumber(v)) tongHoanThien += v;
                if (v <= 0) allPositive = false;
                else if (allPositive) logSum += Math.log(v);
            }
        }
        System.out.println("Bai 4 - Tong tat ca cac so: " + tongTatCa);
        System.out.println("Bai 5 - Tong cac so chan: " + tongChan);
        System.out.printf("Bai 6 - Trung binh cong: %.2f\n", (double) tongTatCa / totalCells);
        if (allPositive) {
            System.out.printf("Bai 7 - Trung binh nhan: %.4f\n", Math.exp(logSum / totalCells));
        } else {
            System.out.println("Bai 7 - Trung binh nhan: Chi xac dinh khi tat ca phan tu deu > 0");
        }
        System.out.println("Bai 8 - Tong cac so nguyen to: " + tongSNT);
        System.out.println("Bai 9 - Tong cac so hoan thien: " + tongHoanThien);
    }

    static void runTheoDong(Scanner sc, int[][] m) {
        if (m == null) {
            System.out.println("Chua co ma tran! Hay chon khoi tao ma tran truoc.");
            return;
        }
        System.out.println("\n=== BAI 10 - 14: THAO TAC THEO TUNG DONG ===");
        System.out.println("10. Tong tat ca so tren moi dong & dong lon nhat");
        System.out.println("11. Tong so chan tren moi dong & dong lon nhat");
        System.out.println("12. Tong so am tren moi dong & dong lon nhat");
        System.out.println("13. Trung binh cong moi dong & dong lon nhat");
        System.out.println("14. Tong so nguyen to tren moi dong");
        int sub = Utils.inputInt(sc, "Chon bai (10-14)", 10, 14);

        int rows = m.length;
        if (sub == 13) {
            double[] res = new double[rows];
            double maxVal = -Double.MAX_VALUE;
            for (int i = 0; i < rows; i++) {
                long s = 0;
                for (int v : m[i]) s += v;
                res[i] = (double) s / m[i].length;
                if (res[i] > maxVal) maxVal = res[i];
                System.out.printf("Dong %d: TBC = %.2f\n", i, res[i]);
            }
            List<Integer> maxIdx = new ArrayList<>();
            for (int i = 0; i < rows; i++) if (Double.compare(res[i], maxVal) == 0) maxIdx.add(i);
            System.out.printf("TBC lon nhat: %.2f tai dong: %s\n", maxVal, maxIdx);
            return;
        }

        long[] res = new long[rows];
        long maxVal = -Long.MAX_VALUE;
        for (int i = 0; i < rows; i++) {
            for (int v : m[i]) {
                if (sub == 10) res[i] += v;
                else if (sub == 11 && v % 2 == 0) res[i] += v;
                else if (sub == 12 && v < 0) res[i] += v;
                else if (sub == 14 && Utils.isPrime(v)) res[i] += v;
            }
            if (res[i] > maxVal) maxVal = res[i];
            System.out.printf("Dong %d: Tong = %d\n", i, res[i]);
        }
        if (sub != 14) {
            List<Integer> maxIdx = new ArrayList<>();
            for (int i = 0; i < rows; i++) if (res[i] == maxVal) maxIdx.add(i);
            System.out.printf("Gia tri lon nhat: %d tai dong: %s\n", maxVal, maxIdx);
        }
    }

    static void runTheoCot(Scanner sc, int[][] m) {
        if (m == null) {
            System.out.println("Chua co ma tran! Hay chon khoi tao ma tran truoc.");
            return;
        }
        System.out.println("\n=== PHAN 4.3.3: THAO TAC THEO TUNG COT ===");
        System.out.println("10. Tong tat ca so tren moi cot & cot lon nhat");
        System.out.println("11. Tong so chan tren moi cot & cot lon nhat");
        System.out.println("12. Tong so am tren moi cot & cot lon nhat");
        System.out.println("13. Trung binh cong moi cot & cot lon nhat");
        System.out.println("14. Tong so nguyen to tren moi cot");
        int sub = Utils.inputInt(sc, "Chon yeu cau (10-14)", 10, 14);

        int rows = m.length, cols = m[0].length;
        if (sub == 13) {
            double[] res = new double[cols];
            double maxVal = -Double.MAX_VALUE;
            for (int j = 0; j < cols; j++) {
                long s = 0;
                for (int i = 0; i < rows; i++) s += m[i][j];
                res[j] = (double) s / rows;
                if (res[j] > maxVal) maxVal = res[j];
                System.out.printf("Cot %d: TBC = %.2f\n", j, res[j]);
            }
            List<Integer> maxIdx = new ArrayList<>();
            for (int j = 0; j < cols; j++) if (Double.compare(res[j], maxVal) == 0) maxIdx.add(j);
            System.out.printf("TBC lon nhat: %.2f tai cot: %s\n", maxVal, maxIdx);
            return;
        }

        long[] res = new long[cols];
        long maxVal = -Long.MAX_VALUE;
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                int v = m[i][j];
                if (sub == 10) res[j] += v;
                else if (sub == 11 && v % 2 == 0) res[j] += v;
                else if (sub == 12 && v < 0) res[j] += v;
                else if (sub == 14 && Utils.isPrime(v)) res[j] += v;
            }
            if (res[j] > maxVal) maxVal = res[j];
            System.out.printf("Cot %d: Tong = %d\n", j, res[j]);
        }
        if (sub != 14) {
            List<Integer> maxIdx = new ArrayList<>();
            for (int j = 0; j < cols; j++) if (res[j] == maxVal) maxIdx.add(j);
            System.out.printf("Gia tri lon nhat: %d tai cot: %s\n", maxVal, maxIdx);
        }
    }

    static void runBai15(int[][] m) {
        if (m == null) {
            System.out.println("Chua co ma tran! Hay chon khoi tao ma tran truoc.");
            return;
        }
        System.out.println("\n=== BAI 15: TONG DUONG BIEN & PHAN BEN TRONG ===");
        long bien = 0, khongBien = 0;
        int rows = m.length, cols = m[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (Utils.isBorder(i, j, rows, cols)) bien += m[i][j];
                else khongBien += m[i][j];
            }
        }
        System.out.println("Bai 15a - Tong tat ca cac so tren duong bien: " + bien);
        System.out.println("Bai 15b - Tong cac so KHONG thuoc duong bien: " + khongBien);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[][] matrix = {
            {4, 2, 9, 7},
            {2, 2, 9, 4},
            {7, 8, 3, 4},
            {4, 3, 5, 6}
        };
        int chon = -1;
        while (chon != 0) {
            System.out.println("\n========================================================");
            System.out.println("  MENU THUC HANH SO 01: MANG HAI CHIEU (JAVA - HIU)    ");
            System.out.println("========================================================");
            System.out.println(" 1. Bai tap co huong dan (trang 3 - 5)");
            System.out.println(" 2. Bai 1: Nhap mang so nguyen duong (5 < n, m < 200)");
            System.out.println(" 3. Bai 2: Khoi tao mang ngau nhien (cau a - f)");
            System.out.println(" 4. Xuat ma tran hien tai");
            System.out.println(" 5. Bai 3: Cac cach xuat dac biet (cau a - c)");
            System.out.println(" 6. Bai 4 den 9: Tinh toan tren toan bo ma tran");
            System.out.println(" 7. Bai 10 den 14: Thao tac tren tung dong");
            System.out.println(" 8. Phan 4.3.3: Thao tac tren tung cot");
            System.out.println(" 9. Bai 15: Tong tren duong bien & phan ben trong");
            System.out.println("10. Nhap ma tran tuy y de kiem tra");
            System.out.println("11. Nap lai ma tran mau 4x4 (trang 7 giao trinh)");
            System.out.println(" 0. Thoat chuong trinh");
            System.out.println("========================================================");
            System.out.print("Nhap lua chon cua ban (0 - 11): ");

            if (!sc.hasNextInt()) break;
            chon = sc.nextInt();

            switch (chon) {
                case 1: matrix = BaiHuongDan.run(sc); break;
                case 2: matrix = runBai01(sc); break;
                case 3: matrix = runBai02(sc); break;
                case 4:
                    System.out.println("Ma tran hien tai:");
                    Utils.printMatrix(matrix);
                    break;
                case 5: runBai03(sc, matrix); break;
                case 6: runBai04Den09(matrix); break;
                case 7: runTheoDong(sc, matrix); break;
                case 8: runTheoCot(sc, matrix); break;
                case 9: runBai15(matrix); break;
                case 10:
                    int r = Utils.inputInt(sc, "Nhap so dong", 1, 100);
                    int c = Utils.inputInt(sc, "Nhap so cot", 1, 200);
                    matrix = Utils.inputMatrix(sc, r, c);
                    System.out.println("Ma tran vua nhap:");
                    Utils.printMatrix(matrix);
                    break;
                case 11:
                    matrix = new int[][]{
                        {4, 2, 9, 7},
                        {2, 2, 9, 4},
                        {7, 8, 3, 4},
                        {4, 3, 5, 6}
                    };
                    System.out.println("Da nap ma tran mau 4x4 thanh cong:");
                    Utils.printMatrix(matrix);
                    break;
                case 0:
                    System.out.println("Da thoat chuong trinh. Cam on!");
                    break;
                default:
                    System.out.println("Lua chon khong hop le! Vui long nhap tu 0 den 11.");
            }
        }
        sc.close();
    }
}
