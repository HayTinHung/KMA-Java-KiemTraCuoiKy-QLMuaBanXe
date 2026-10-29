import java.util.Random;

/** Bài 2: Các cách khởi tạo mảng có kích thước 2 <= n, m < 50. */
public final class Bai02_KhoiTaoMang {
    private static final Random RANDOM = new Random();

    private Bai02_KhoiTaoMang() {
    }

    // Bài 2a: Giá trị ngẫu nhiên thuộc [-100, 100].
    public static int[][] khoiTaoCauA(int rows, int columns) {
        int[][] matrix = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = RANDOM.nextInt(201) - 100;
            }
        }
        return matrix;
    }

    // Bài 2b: a[i][j] = i * j.
    public static int[][] khoiTaoCauB(int rows, int columns) {
        int[][] matrix = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = i * j;
            }
        }
        return matrix;
    }

    // Bài 2c: Giá trị ngẫu nhiên thuộc [0, 99].
    public static int[][] khoiTaoCauC(int rows, int columns) {
        int[][] matrix = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = RANDOM.nextInt(100);
            }
        }
        return matrix;
    }

    // Bài 2d: Tất cả phần tử là số lẻ thuộc [1, 99].
    public static int[][] khoiTaoCauD(int rows, int columns) {
        int[][] matrix = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = 2 * RANDOM.nextInt(50) + 1;
            }
        }
        return matrix;
    }

    // Bài 2e: Tất cả phần tử là số nguyên tố nhỏ hơn 100.
    public static int[][] khoiTaoCauE(int rows, int columns) {
        int[] primesBelow100 = {
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
            43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97
        };
        int[][] matrix = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = primesBelow100[RANDOM.nextInt(primesBelow100.length)];
            }
        }
        return matrix;
    }

    // Bài 2f: (i+j) lẻ nhận số chẵn; (i+j) chẵn nhận số lẻ.
    public static int[][] khoiTaoCauF(int rows, int columns) {
        int[][] matrix = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i + j) % 2 == 1) {
                    matrix[i][j] = 2 * RANDOM.nextInt(50);
                } else {
                    matrix[i][j] = 2 * RANDOM.nextInt(50) + 1;
                }
            }
        }
        return matrix;
    }
}
