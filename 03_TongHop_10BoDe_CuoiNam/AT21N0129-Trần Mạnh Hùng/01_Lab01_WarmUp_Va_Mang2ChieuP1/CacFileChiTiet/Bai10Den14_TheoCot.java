import java.util.ArrayList;
import java.util.List;

/** Phần 4.3.3: Thực hiện Bài 10 đến Bài 14 theo từng cột. */
public final class Bai10Den14_TheoCot {
    private Bai10Den14_TheoCot() {
    }

    public static long[] tongTatCaMoiCot(int[][] matrix) {
        long[] results = new long[matrix[0].length];
        for (int j = 0; j < matrix[0].length; j++) {
            for (int[] row : matrix) {
                results[j] += row[j];
            }
        }
        return results;
    }

    public static long[] tongSoChanMoiCot(int[][] matrix) {
        long[] results = new long[matrix[0].length];
        for (int j = 0; j < matrix[0].length; j++) {
            for (int[] row : matrix) {
                if (row[j] % 2 == 0) {
                    results[j] += row[j];
                }
            }
        }
        return results;
    }

    public static long[] tongSoAmMoiCot(int[][] matrix) {
        long[] results = new long[matrix[0].length];
        for (int j = 0; j < matrix[0].length; j++) {
            for (int[] row : matrix) {
                if (row[j] < 0) {
                    results[j] += row[j];
                }
            }
        }
        return results;
    }

    public static double[] trungBinhCongMoiCot(int[][] matrix) {
        long[] sums = tongTatCaMoiCot(matrix);
        double[] results = new double[matrix[0].length];
        for (int j = 0; j < results.length; j++) {
            results[j] = (double) sums[j] / matrix.length;
        }
        return results;
    }

    public static long[] tongSoNguyenToMoiCot(int[][] matrix) {
        long[] results = new long[matrix[0].length];
        for (int j = 0; j < matrix[0].length; j++) {
            for (int[] row : matrix) {
                if (MatrixUtils.isPrime(row[j])) {
                    results[j] += row[j];
                }
            }
        }
        return results;
    }

    public static void printBai10(int[][] matrix) {
        printLongResults("Theo cột - Tổng tất cả các số", "Tổng cột", tongTatCaMoiCot(matrix), true);
    }

    public static void printBai11(int[][] matrix) {
        printLongResults("Theo cột - Tổng các số chẵn", "Tổng chẵn", tongSoChanMoiCot(matrix), true);
    }

    public static void printBai12(int[][] matrix) {
        printLongResults("Theo cột - Tổng các số âm", "Tổng âm", tongSoAmMoiCot(matrix), true);
    }

    public static void printBai13(int[][] matrix) {
        double[] results = trungBinhCongMoiCot(matrix);
        System.out.println("\nTheo cột - Trung bình cộng");
        for (int j = 0; j < results.length; j++) {
            System.out.printf("Cột %d: %.2f%n", j, results[j]);
        }
        double max = results[0];
        for (double result : results) {
            max = Math.max(max, result);
        }
        System.out.printf("Trung bình lớn nhất: %.2f; cột: %s%n", max, equalIndexes(results, max));
    }

    public static void printBai14(int[][] matrix) {
        printLongResults("Theo cột - Tổng các số nguyên tố", "Tổng nguyên tố",
                tongSoNguyenToMoiCot(matrix), false);
    }

    private static void printLongResults(String title, String label, long[] results, boolean printMaximum) {
        System.out.println("\n" + title);
        for (int j = 0; j < results.length; j++) {
            System.out.printf("Cột %d: %s = %d%n", j, label, results[j]);
        }
        if (printMaximum) {
            long max = results[0];
            for (long result : results) {
                max = Math.max(max, result);
            }
            System.out.printf("Giá trị lớn nhất: %d; cột: %s%n", max, equalIndexes(results, max));
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
