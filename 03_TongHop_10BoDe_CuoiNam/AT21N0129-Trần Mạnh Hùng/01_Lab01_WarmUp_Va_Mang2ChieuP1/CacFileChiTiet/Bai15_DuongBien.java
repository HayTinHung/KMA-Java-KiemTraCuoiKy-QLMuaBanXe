/** Bài 15: Tính tổng trên đường biên và không thuộc đường biên. */
public final class Bai15_DuongBien {
    private Bai15_DuongBien() {
    }

    // Bài 15a.
    public static long tongTrenDuongBien(int[][] matrix) {
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

    // Bài 15b.
    public static long tongKhongThuocDuongBien(int[][] matrix) {
        long sum = 0;
        int rows = matrix.length;
        int columns = matrix[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (!MatrixUtils.isBorder(i, j, rows, columns)) {
                    sum += matrix[i][j];
                }
            }
        }
        return sum;
    }
}
