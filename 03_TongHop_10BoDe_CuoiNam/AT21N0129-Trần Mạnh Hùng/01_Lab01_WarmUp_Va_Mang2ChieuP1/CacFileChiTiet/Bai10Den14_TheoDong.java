import java.util.ArrayList;
import java.util.List;

/** Bài 10 đến Bài 14: Các phép tính trên từng dòng. */
public final class Bai10Den14_TheoDong {
    private Bai10Den14_TheoDong() {
    }

    // Bài 10.
    public static long[] tongTatCaMoiDong(int[][] matrix) {
        long[] results = new long[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int value : matrix[i]) {
                results[i] += value;
            }
        }
        return results;
    }

    // Bài 11.
    public static long[] tongSoChanMoiDong(int[][] matrix) {
        long[] results = new long[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int value : matrix[i]) {
                if (value % 2 == 0) {
                    results[i] += value;
                }
            }
        }
        return results;
    }

    // Bài 12.
    public static long[] tongSoAmMoiDong(int[][] matrix) {
        long[] results = new long[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int value : matrix[i]) {
                if (value < 0) {
                    results[i] += value;
                }
            }
        }
        return results;
    }

    // Bài 13.
    public static double[] trungBinhCongMoiDong(int[][] matrix) {
        long[] sums = tongTatCaMoiDong(matrix);
        double[] results = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            results[i] = (double) sums[i] / matrix[i].length;
        }
        return results;
    }

    // Bài 14.
    public static long[] tongSoNguyenToMoiDong(int[][] matrix) {
        long[] results = new long[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int value : matrix[i]) {
                if (MatrixUtils.isPrime(value)) {
                    results[i] += value;
                }
            }
        }
        return results;
    }

    public static void printBai10(int[][] matrix) {
        printLongResults("Bài 10 - Tổng tất cả các số trên mỗi dòng", "Tổng dòng", tongTatCaMoiDong(matrix), true);
    }

    public static void printBai11(int[][] matrix) {
        printLongResults("Bài 11 - Tổng các số chẵn trên mỗi dòng", "Tổng chẵn", tongSoChanMoiDong(matrix), true);
    }

    public static void printBai12(int[][] matrix) {
        printLongResults("Bài 12 - Tổng các số âm trên mỗi dòng", "Tổng âm", tongSoAmMoiDong(matrix), true);
    }

    public static void printBai13(int[][] matrix) {
        double[] results = trungBinhCongMoiDong(matrix);
        System.out.println("\nBài 13 - Trung bình cộng trên mỗi dòng");
        for (int i = 0; i < results.length; i++) {
            System.out.printf("Dòng %d: %.2f%n", i, results[i]);
        }
        double max = results[0];
        for (double result : results) {
            max = Math.max(max, result);
        }
        System.out.printf("Trung bình lớn nhất: %.2f; dòng: %s%n", max, equalIndexes(results, max));
    }

    public static void printBai14(int[][] matrix) {
        printLongResults("Bài 14 - Tổng các số nguyên tố trên mỗi dòng", "Tổng nguyên tố",
                tongSoNguyenToMoiDong(matrix), false);
    }

    private static void printLongResults(String title, String label, long[] results, boolean printMaximum) {
        System.out.println("\n" + title);
        for (int i = 0; i < results.length; i++) {
            System.out.printf("Dòng %d: %s = %d%n", i, label, results[i]);
        }
        if (printMaximum) {
            long max = results[0];
            for (long result : results) {
                max = Math.max(max, result);
            }
            System.out.printf("Giá trị lớn nhất: %d; dòng: %s%n", max, equalIndexes(results, max));
        }
    }

    private static String equalIndexes(long[] values, long expected) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == expected) {
                indexes.add(i);
            }
        }
        return indexes.toString();
    }

    private static String equalIndexes(double[] values, double expected) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (Double.compare(values[i], expected) == 0) {
                indexes.add(i);
            }
        }
        return indexes.toString();
    }
}
