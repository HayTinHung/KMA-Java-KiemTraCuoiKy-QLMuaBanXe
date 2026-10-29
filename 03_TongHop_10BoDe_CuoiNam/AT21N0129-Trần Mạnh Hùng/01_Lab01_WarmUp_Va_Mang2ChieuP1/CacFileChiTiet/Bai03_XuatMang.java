/** Bài 3: Ba cách xuất ma trận có thay một số vị trí bằng khoảng trắng. */
public final class Bai03_XuatMang {
    private static final String BLANK_CELL = "        ";

    private Bai03_XuatMang() {
    }

    // Bài 3a: Ẩn các số nguyên tố.
    public static void xuatAnSoNguyenTo(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                printCell(value, MatrixUtils.isPrime(value));
            }
            System.out.println();
        }
    }

    // Bài 3b: Chỉ giữ lại các phần tử trên đường biên.
    public static void xuatChiDuongBien(int[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                printCell(matrix[i][j], !MatrixUtils.isBorder(i, j, rows, columns));
            }
            System.out.println();
        }
    }

    // Bài 3c: Ẩn phần tử có tổng chỉ số dòng và cột là số chẵn.
    public static void xuatAnViTriTongChiSoChan(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                printCell(matrix[i][j], (i + j) % 2 == 0);
            }
            System.out.println();
        }
    }

    private static void printCell(int value, boolean hidden) {
        if (hidden) {
            System.out.print(BLANK_CELL);
        } else {
            System.out.printf("%8d", value);
        }
    }
}
