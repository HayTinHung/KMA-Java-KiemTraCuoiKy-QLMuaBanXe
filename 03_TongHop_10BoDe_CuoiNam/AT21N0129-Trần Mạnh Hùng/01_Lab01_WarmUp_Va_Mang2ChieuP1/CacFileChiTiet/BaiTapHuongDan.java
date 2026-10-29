import java.util.Scanner;

/** Bài tập có hướng dẫn ở trang 3 đến trang 5 của tài liệu. */
public final class BaiTapHuongDan {
    public static final int ROWS = 100;
    public static final int COLUMNS = 200;

    private BaiTapHuongDan() {
    }

    public static int[][] nhapMang(Scanner scanner) {
        int rows = MatrixUtils.inputInt(scanner, "Nhập số dòng", 1, ROWS);
        int columns = MatrixUtils.inputInt(scanner, "Nhập số cột", 1, COLUMNS);
        return MatrixUtils.inputMatrix(scanner, rows, columns);
    }

    // Yêu cầu (iv): Tính tổng tất cả phần tử trong mảng.
    public static long tongMang(int[][] matrix) {
        long sum = 0;
        for (int[] row : matrix) {
            for (int value : row) {
                sum += value;
            }
        }
        return sum;
    }

    // Yêu cầu (v): Tính tổng các phần tử trên đường biên.
    public static long tongDuongBien(int[][] matrix) {
        long sum = 0;
        int rows = matrix.length;
        int columns = matrix[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (MatrixUtils.isBorder(i, j, rows, columns)) {
                    sum += matrix[i][j];
                }
            }
        }
        return sum;
    }

    // Yêu cầu (vi): Liệt kê các số chỉ chứa chữ số 6 và 8.
    public static void lietKeSoMayMan(int[][] matrix) {
        boolean found = false;
        System.out.print("Các số may mắn trong mảng: ");
        for (int[] row : matrix) {
            for (int value : row) {
                if (MatrixUtils.isLuckyNumber(value)) {
                    System.out.print(value + " ");
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.print("Không có");
        }
        System.out.println();
    }

    public static int[][] run(Scanner scanner) {
        System.out.println("\n=== BÀI TẬP CÓ HƯỚNG DẪN ===");
        int[][] matrix = nhapMang(scanner);
        System.out.println("Ma trận vừa nhập:");
        MatrixUtils.printMatrix(matrix);
        System.out.println("Tổng các phần tử: " + tongMang(matrix));
        System.out.println("Tổng các phần tử trên đường biên: " + tongDuongBien(matrix));
        lietKeSoMayMan(matrix);
        return matrix;
    }
}
