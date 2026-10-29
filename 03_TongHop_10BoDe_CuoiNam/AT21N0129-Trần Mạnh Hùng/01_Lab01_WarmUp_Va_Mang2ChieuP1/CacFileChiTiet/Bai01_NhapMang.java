import java.util.Scanner;

/** Bài 1: Nhập mảng số nguyên dương có kích thước 5 < n, m < 200. */
public final class Bai01_NhapMang {
    private Bai01_NhapMang() {
    }

    public static int[][] nhapMangSoNguyenDuong(Scanner scanner) {
        int rows = MatrixUtils.inputInt(scanner, "Nhập số dòng n", 6, 199);
        int columns = MatrixUtils.inputInt(scanner, "Nhập số cột m", 6, 199);
        int[][] matrix = new int[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = MatrixUtils.inputPositiveInt(scanner,
                        String.format("Nhập a[%d][%d]", i, j));
            }
        }
        return matrix;
    }
}
