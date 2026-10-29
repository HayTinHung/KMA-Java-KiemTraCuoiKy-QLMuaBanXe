import java.util.Scanner;

/** Các hàm tiện ích dùng chung cho toàn bộ bài thực hành. */
public final class MatrixUtils {
    private MatrixUtils() {
    }

    /** Nhập một số nguyên trong đoạn [min, max]. */
    public static int inputInt(Scanner scanner, String message, int min, int max) {
        while (true) {
            System.out.printf("%s (%d..%d): ", message, min, max);
            if (!scanner.hasNextInt()) {
                System.out.println("Dữ liệu không hợp lệ. Vui lòng nhập một số nguyên.");
                scanner.next();
                continue;
            }

            int value = scanner.nextInt();
            if (value >= min && value <= max) {
                return value;
            }
            System.out.printf("Giá trị phải nằm trong đoạn [%d, %d]. Vui lòng nhập lại.%n", min, max);
        }
    }

    /** Nhập một số nguyên dương. */
    public static int inputPositiveInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message + ": ");
            if (!scanner.hasNextInt()) {
                System.out.println("Dữ liệu không hợp lệ. Vui lòng nhập một số nguyên.");
                scanner.next();
                continue;
            }

            int value = scanner.nextInt();
            if (value > 0) {
                return value;
            }
            System.out.println("Giá trị phải là số nguyên dương. Vui lòng nhập lại.");
        }
    }

    /** Nhập ma trận gồm các số nguyên bất kỳ. */
    public static int[][] inputMatrix(Scanner scanner, int rows, int columns) {
        int[][] matrix = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.printf("Nhập a[%d][%d]: ", i, j);
                while (!scanner.hasNextInt()) {
                    System.out.print("Dữ liệu không hợp lệ. Nhập lại a[" + i + "][" + j + "]: ");
                    scanner.next();
                }
                matrix[i][j] = scanner.nextInt();
            }
        }
        return matrix;
    }

    /** Xuất ma trận dưới dạng bảng. */
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.printf("%8d", value);
            }
            System.out.println();
        }
    }

    public static boolean isPrime(int value) {
        if (value < 2) {
            return false;
        }
        for (int divisor = 2; (long) divisor * divisor <= value; divisor++) {
            if (value % divisor == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPerfectNumber(int value) {
        if (value <= 1) {
            return false;
        }

        int divisorSum = 1;
        for (int divisor = 2; (long) divisor * divisor <= value; divisor++) {
            if (value % divisor == 0) {
                divisorSum += divisor;
                int pairedDivisor = value / divisor;
                if (pairedDivisor != divisor) {
                    divisorSum += pairedDivisor;
                }
            }
        }
        return divisorSum == value;
    }

    /** Số may mắn là số nguyên dương chỉ chứa chữ số 6 và 8. */
    public static boolean isLuckyNumber(int value) {
        if (value <= 0) {
            return false;
        }
        while (value > 0) {
            int digit = value % 10;
            if (digit != 6 && digit != 8) {
                return false;
            }
            value /= 10;
        }
        return true;
    }

    public static boolean isBorder(int row, int column, int rows, int columns) {
        return row == 0 || row == rows - 1 || column == 0 || column == columns - 1;
    }
}

