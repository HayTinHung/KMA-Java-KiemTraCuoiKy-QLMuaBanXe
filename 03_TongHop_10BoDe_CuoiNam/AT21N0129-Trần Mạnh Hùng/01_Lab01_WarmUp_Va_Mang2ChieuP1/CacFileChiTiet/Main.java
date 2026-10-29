import java.util.Scanner;

/**
 * BÀI THỰC HÀNH SỐ 01 - MẢNG HAI CHIỀU
 * Họ và tên: ........................................
 * MSSV: .............................................
 * Lớp: ..............................................
 */
public class Main {
    private static final int EXIT = 0;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int[][] matrix = null;
            int choice;

            printHeader();
            do {
                printMenu();
                choice = MatrixUtils.inputInt(scanner, "Nhập lựa chọn", 0, 11);

                switch (choice) {
                    case 1 -> matrix = BaiTapHuongDan.run(scanner);
                    case 2 -> matrix = runBai01(scanner);
                    case 3 -> matrix = runBai02(scanner);
                    case 4 -> printCurrentMatrix(matrix);
                    case 5 -> runBai03(scanner, matrix);
                    case 6 -> runBai04Den09(matrix);
                    case 7 -> runTheoDong(scanner, matrix);
                    case 8 -> runTheoCot(scanner, matrix);
                    case 9 -> runBai15(matrix);
                    case 10 -> matrix = inputGeneralMatrix(scanner);
                    case 11 -> matrix = sampleMatrix();
                    case EXIT -> System.out.println("Chương trình kết thúc.");
                    default -> throw new IllegalStateException("Lựa chọn ngoài phạm vi.");
                }
            } while (choice != EXIT);
        }
    }

    private static void printHeader() {
        System.out.println("====================================================");
        System.out.println("   BÀI THỰC HÀNH 01 - MẢNG HAI CHIỀU (JAVA)");
        System.out.println("====================================================");
    }

    private static void printMenu() {
        System.out.println("\n---------------------- MENU ------------------------");
        System.out.println(" 1. Bài tập có hướng dẫn (trang 3-5)");
        System.out.println(" 2. Bài 1 - Nhập mảng số nguyên dương");
        System.out.println(" 3. Bài 2 - Khởi tạo mảng (câu a-f)");
        System.out.println(" 4. Xuất ma trận hiện tại");
        System.out.println(" 5. Bài 3 - Các cách xuất đặc biệt (câu a-c)");
        System.out.println(" 6. Bài 4 đến 9 - Tính trên toàn ma trận");
        System.out.println(" 7. Bài 10 đến 14 - Tính theo dòng");
        System.out.println(" 8. Phần 4.3.3 - Tính theo cột");
        System.out.println(" 9. Bài 15 - Tính tổng đường biên");
        System.out.println("10. Nhập một ma trận nguyên để kiểm tra các bài");
        System.out.println("11. Dùng ma trận mẫu 4 x 4 trong tài liệu");
        System.out.println(" 0. Thoát");
    }

    private static int[][] runBai01(Scanner scanner) {
        System.out.println("\n=== BÀI 1: NHẬP MẢNG SỐ NGUYÊN DƯƠNG ===");
        int[][] matrix = Bai01_NhapMang.nhapMangSoNguyenDuong(scanner);
        System.out.println("Ma trận vừa nhập:");
        MatrixUtils.printMatrix(matrix);
        return matrix;
    }

    private static int[][] runBai02(Scanner scanner) {
        System.out.println("\n=== BÀI 2: KHỞI TẠO MẢNG ===");
        int rows = MatrixUtils.inputInt(scanner, "Nhập số dòng n", 2, 49);
        int columns = MatrixUtils.inputInt(scanner, "Nhập số cột m", 2, 49);
        System.out.println("1. Câu a - Ngẫu nhiên trong [-100, 100]");
        System.out.println("2. Câu b - a[i][j] = i * j");
        System.out.println("3. Câu c - Ngẫu nhiên trong [0, 99]");
        System.out.println("4. Câu d - Toàn số lẻ");
        System.out.println("5. Câu e - Toàn số nguyên tố");
        System.out.println("6. Câu f - Chẵn/lẻ theo tổng chỉ số");
        int subChoice = MatrixUtils.inputInt(scanner, "Chọn câu", 1, 6);

        int[][] matrix = switch (subChoice) {
            case 1 -> Bai02_KhoiTaoMang.khoiTaoCauA(rows, columns);
            case 2 -> Bai02_KhoiTaoMang.khoiTaoCauB(rows, columns);
            case 3 -> Bai02_KhoiTaoMang.khoiTaoCauC(rows, columns);
            case 4 -> Bai02_KhoiTaoMang.khoiTaoCauD(rows, columns);
            case 5 -> Bai02_KhoiTaoMang.khoiTaoCauE(rows, columns);
            case 6 -> Bai02_KhoiTaoMang.khoiTaoCauF(rows, columns);
            default -> throw new IllegalStateException("Lựa chọn ngoài phạm vi.");
        };
        System.out.println("Ma trận vừa khởi tạo:");
        MatrixUtils.printMatrix(matrix);
        return matrix;
    }

    private static void runBai03(Scanner scanner, int[][] matrix) {
        if (!hasMatrix(matrix)) {
            return;
        }
        System.out.println("1. Câu a - Ẩn số nguyên tố");
        System.out.println("2. Câu b - Chỉ xuất đường biên");
        System.out.println("3. Câu c - Ẩn vị trí có (i + j) chẵn");
        int subChoice = MatrixUtils.inputInt(scanner, "Chọn câu", 1, 3);
        switch (subChoice) {
            case 1 -> Bai03_XuatMang.xuatAnSoNguyenTo(matrix);
            case 2 -> Bai03_XuatMang.xuatChiDuongBien(matrix);
            case 3 -> Bai03_XuatMang.xuatAnViTriTongChiSoChan(matrix);
            default -> throw new IllegalStateException("Lựa chọn ngoài phạm vi.");
        }
    }

    private static void runBai04Den09(int[][] matrix) {
        if (hasMatrix(matrix)) {
            System.out.println("\n=== BÀI 4 ĐẾN BÀI 9 ===");
            Bai04Den09_ToanMang.printAllResults(matrix);
        }
    }

    private static void runTheoDong(Scanner scanner, int[][] matrix) {
        if (!hasMatrix(matrix)) {
            return;
        }
        int exercise = MatrixUtils.inputInt(scanner, "Chọn bài theo dòng", 10, 14);
        switch (exercise) {
            case 10 -> Bai10Den14_TheoDong.printBai10(matrix);
            case 11 -> Bai10Den14_TheoDong.printBai11(matrix);
            case 12 -> Bai10Den14_TheoDong.printBai12(matrix);
            case 13 -> Bai10Den14_TheoDong.printBai13(matrix);
            case 14 -> Bai10Den14_TheoDong.printBai14(matrix);
            default -> throw new IllegalStateException("Lựa chọn ngoài phạm vi.");
        }
    }

    private static void runTheoCot(Scanner scanner, int[][] matrix) {
        if (!hasMatrix(matrix)) {
            return;
        }
        int exercise = MatrixUtils.inputInt(scanner, "Chọn yêu cầu tương ứng Bài 10..14", 10, 14);
        switch (exercise) {
            case 10 -> Bai10Den14_TheoCot.printBai10(matrix);
            case 11 -> Bai10Den14_TheoCot.printBai11(matrix);
            case 12 -> Bai10Den14_TheoCot.printBai12(matrix);
            case 13 -> Bai10Den14_TheoCot.printBai13(matrix);
            case 14 -> Bai10Den14_TheoCot.printBai14(matrix);
            default -> throw new IllegalStateException("Lựa chọn ngoài phạm vi.");
        }
    }

    private static void runBai15(int[][] matrix) {
        if (hasMatrix(matrix)) {
            System.out.println("Bài 15a - Tổng trên đường biên: "
                    + Bai15_DuongBien.tongTrenDuongBien(matrix));
            System.out.println("Bài 15b - Tổng không thuộc đường biên: "
                    + Bai15_DuongBien.tongKhongThuocDuongBien(matrix));
        }
    }

    private static int[][] inputGeneralMatrix(Scanner scanner) {
        int rows = MatrixUtils.inputInt(scanner, "Nhập số dòng", 1, 100);
        int columns = MatrixUtils.inputInt(scanner, "Nhập số cột", 1, 200);
        return MatrixUtils.inputMatrix(scanner, rows, columns);
    }

    private static void printCurrentMatrix(int[][] matrix) {
        if (hasMatrix(matrix)) {
            System.out.println("Ma trận hiện tại:");
            MatrixUtils.printMatrix(matrix);
        }
    }

    private static boolean hasMatrix(int[][] matrix) {
        if (matrix == null) {
            System.out.println("Chưa có ma trận. Hãy chọn mục 1, 2, 3, 10 hoặc 11 trước.");
            return false;
        }
        return true;
    }

    private static int[][] sampleMatrix() {
        int[][] matrix = {
            {4, 2, 9, 7},
            {2, 2, 9, 4},
            {7, 8, 3, 4},
            {4, 3, 5, 6}
        };
        System.out.println("Đã chọn ma trận mẫu:");
        MatrixUtils.printMatrix(matrix);
        return matrix;
    }
}

