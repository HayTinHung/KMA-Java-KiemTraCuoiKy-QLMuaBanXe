/** Bài 4 đến Bài 9: Các phép tính trên toàn bộ ma trận. */
public final class Bai04Den09_ToanMang {
    private Bai04Den09_ToanMang() {
    }

    // Bài 4.
    public static long tongTatCa(int[][] matrix) {
        long sum = 0;
        for (int[] row : matrix) {
            for (int value : row) {
                sum += value;
            }
        }
        return sum;
    }

    // Bài 5.
    public static long tongSoChan(int[][] matrix) {
        long sum = 0;
        for (int[] row : matrix) {
            for (int value : row) {
                if (value % 2 == 0) {
                    sum += value;
                }
            }
        }
        return sum;
    }

    // Bài 6.
    public static double trungBinhCong(int[][] matrix) {
        return (double) tongTatCa(matrix) / (matrix.length * matrix[0].length);
    }

    /**
     * Bài 7. Trung bình nhân chỉ được xác định ở đây khi mọi phần tử đều dương.
     * Dùng tổng logarit để tránh tràn số khi ma trận lớn.
     */
    public static double trungBinhNhan(int[][] matrix) {
        double logarithmSum = 0.0;
        int count = matrix.length * matrix[0].length;
        for (int[] row : matrix) {
            for (int value : row) {
                if (value <= 0) {
                    throw new IllegalArgumentException(
                            "Trung bình nhân yêu cầu tất cả phần tử phải lớn hơn 0.");
                }
                logarithmSum += Math.log(value);
            }
        }
        return Math.exp(logarithmSum / count);
    }

    // Bài 8.
    public static long tongSoNguyenTo(int[][] matrix) {
        long sum = 0;
        for (int[] row : matrix) {
            for (int value : row) {
                if (MatrixUtils.isPrime(value)) {
                    sum += value;
                }
            }
        }
        return sum;
    }

    // Bài 9.
    public static long tongSoHoanThien(int[][] matrix) {
        long sum = 0;
        for (int[] row : matrix) {
            for (int value : row) {
                if (MatrixUtils.isPerfectNumber(value)) {
                    sum += value;
                }
            }
        }
        return sum;
    }

    public static void printAllResults(int[][] matrix) {
        System.out.println("Bài 4 - Tổng tất cả các số: " + tongTatCa(matrix));
        System.out.println("Bài 5 - Tổng các số chẵn: " + tongSoChan(matrix));
        System.out.printf("Bài 6 - Trung bình cộng: %.2f%n", trungBinhCong(matrix));
        try {
            System.out.printf("Bài 7 - Trung bình nhân: %.4f%n", trungBinhNhan(matrix));
        } catch (IllegalArgumentException exception) {
            System.out.println("Bài 7 - " + exception.getMessage());
        }
        System.out.println("Bài 8 - Tổng các số nguyên tố: " + tongSoNguyenTo(matrix));
        System.out.println("Bài 9 - Tổng các số hoàn thiện: " + tongSoHoanThien(matrix));
    }
}
