import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Kiểm thử đầy đủ các yêu cầu trong Lab 01. */
public class Lab01Test {
    private static final int[][] SAMPLE = {
        {4, 2, 9, 7},
        {2, 2, 9, 4},
        {7, 8, 3, 4},
        {4, 3, 5, 6}
    };

    private static int passed;
    private static int groupsPassed;

    public static void main(String[] args) {
        testMatrixUtils();
        testBaiTapHuongDan();
        testBai01();
        testBai02();
        testBai03();
        testBai04Den09();
        testBai10Den14TheoDong();
        testBai10Den14TheoCot();
        testBai15();

        System.out.println("====================================================");
        System.out.printf("KẾT QUẢ: %d/%d nhóm đạt, %d kiểm tra thành công.%n",
                groupsPassed, groupsPassed, passed);
        System.out.println("TẤT CẢ BÀI TỪ 1 ĐẾN 15 ĐỀU ĐẠT.");
    }

    private static void testMatrixUtils() {
        assertTrue(MatrixUtils.isPrime(2), "2 là số nguyên tố");
        assertTrue(MatrixUtils.isPrime(97), "97 là số nguyên tố");
        assertTrue(!MatrixUtils.isPrime(-3), "Số âm không là số nguyên tố");
        assertTrue(!MatrixUtils.isPrime(1), "1 không là số nguyên tố");
        assertTrue(!MatrixUtils.isPrime(100), "100 không là số nguyên tố");
        assertTrue(MatrixUtils.isPerfectNumber(6), "6 là số hoàn thiện");
        assertTrue(MatrixUtils.isPerfectNumber(28), "28 là số hoàn thiện");
        assertTrue(!MatrixUtils.isPerfectNumber(1), "1 không là số hoàn thiện");
        assertTrue(MatrixUtils.isLuckyNumber(6868), "6868 là số may mắn");
        assertTrue(!MatrixUtils.isLuckyNumber(680), "680 không là số may mắn");
        assertTrue(!MatrixUtils.isLuckyNumber(-68), "Số âm không là số may mắn");
        assertTrue(MatrixUtils.isBorder(0, 1, 3, 3), "Vị trí trên là đường biên");
        assertTrue(!MatrixUtils.isBorder(1, 1, 3, 3), "Vị trí giữa không là đường biên");

        Scanner rangedInput = scannerOf("abc 0 3");
        int rangedValue = withoutOutput(() -> MatrixUtils.inputInt(rangedInput, "Nhập", 1, 5));
        assertEquals(3, rangedValue, "Nhập số nguyên có kiểm tra miền");

        Scanner positiveInput = scannerOf("x -1 4");
        int positiveValue = withoutOutput(() -> MatrixUtils.inputPositiveInt(positiveInput, "Nhập"));
        assertEquals(4, positiveValue, "Nhập số nguyên dương");

        Scanner matrixInput = scannerOf("x 1 2 3 4");
        int[][] inputMatrix = withoutOutput(() -> MatrixUtils.inputMatrix(matrixInput, 2, 2));
        assertMatrixEquals(new int[][] {{1, 2}, {3, 4}}, inputMatrix, "Nhập ma trận");
        assertContains(captureOutput(() -> MatrixUtils.printMatrix(inputMatrix)), "       1       2",
                "Xuất ma trận dạng bảng");
        passGroup("Hàm tiện ích và kiểm tra dữ liệu");
    }

    private static void testBaiTapHuongDan() {
        Scanner scanner = scannerOf("0 2 201 3 6 8 68 5 7 9");
        final int[][][] result = new int[1][][];
        String output = captureOutput(() -> result[0] = BaiTapHuongDan.run(scanner));

        assertMatrixEquals(new int[][] {{6, 8, 68}, {5, 7, 9}}, result[0],
                "Nhập mảng bài hướng dẫn");
        assertEquals(103, BaiTapHuongDan.tongMang(result[0]), "Tổng mảng bài hướng dẫn");
        assertEquals(103, BaiTapHuongDan.tongDuongBien(result[0]), "Tổng biên mảng 2x3");
        assertContains(output, "Các số may mắn trong mảng: 6 8 68", "Liệt kê số may mắn");
        passGroup("Bài tập có hướng dẫn");
    }

    private static void testBai01() {
        StringBuilder input = new StringBuilder("5 6 200 6 0 ");
        for (int value = 1; value <= 36; value++) {
            input.append(value).append(' ');
        }
        int[][] matrix = withoutOutput(() ->
                Bai01_NhapMang.nhapMangSoNguyenDuong(scannerOf(input.toString())));

        assertEquals(6, matrix.length, "Bài 1 - số dòng nhỏ nhất hợp lệ");
        assertEquals(6, matrix[0].length, "Bài 1 - số cột nhỏ nhất hợp lệ");
        assertEquals(1, matrix[0][0], "Bài 1 - nhập lại phần tử 0 không hợp lệ");
        assertEquals(36, matrix[5][5], "Bài 1 - phần tử cuối");
        for (int[] row : matrix) {
            for (int value : row) {
                assertTrue(value > 0, "Bài 1 - mọi phần tử là số nguyên dương");
            }
        }
        passGroup("Bài 1 - Nhập mảng số nguyên dương");
    }

    private static void testBai02() {
        int rows = 49;
        int columns = 49;
        int[][] a = Bai02_KhoiTaoMang.khoiTaoCauA(rows, columns);
        int[][] b = Bai02_KhoiTaoMang.khoiTaoCauB(rows, columns);
        int[][] c = Bai02_KhoiTaoMang.khoiTaoCauC(rows, columns);
        int[][] d = Bai02_KhoiTaoMang.khoiTaoCauD(rows, columns);
        int[][] e = Bai02_KhoiTaoMang.khoiTaoCauE(rows, columns);
        int[][] f = Bai02_KhoiTaoMang.khoiTaoCauF(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                assertTrue(a[i][j] >= -100 && a[i][j] <= 100, "Bài 2a");
                assertEquals(i * j, b[i][j], "Bài 2b");
                assertTrue(c[i][j] >= 0 && c[i][j] <= 99, "Bài 2c");
                assertTrue(d[i][j] > 0 && d[i][j] % 2 != 0, "Bài 2d");
                assertTrue(MatrixUtils.isPrime(e[i][j]), "Bài 2e");
                int expectedRemainder = (i + j) % 2 == 1 ? 0 : 1;
                assertEquals(expectedRemainder, f[i][j] % 2, "Bài 2f");
            }
        }
        passGroup("Bài 2a-2f - Sáu cách khởi tạo mảng");
    }

    private static void testBai03() {
        int[][] matrix = {{2, 4, 5}, {6, 7, 8}, {9, 10, 11}};
        String blank = "        ";
        String line = System.lineSeparator();

        String expectedA = blank + cell(4) + blank + line
                + cell(6) + blank + cell(8) + line
                + cell(9) + cell(10) + blank + line;
        assertEquals(expectedA, captureOutput(() -> Bai03_XuatMang.xuatAnSoNguyenTo(matrix)),
                "Bài 3a - ẩn đúng số nguyên tố");

        String expectedB = cell(2) + cell(4) + cell(5) + line
                + cell(6) + blank + cell(8) + line
                + cell(9) + cell(10) + cell(11) + line;
        assertEquals(expectedB, captureOutput(() -> Bai03_XuatMang.xuatChiDuongBien(matrix)),
                "Bài 3b - chỉ giữ đường biên");

        String expectedC = blank + cell(4) + blank + line
                + cell(6) + blank + cell(8) + line
                + blank + cell(10) + blank + line;
        assertEquals(expectedC, captureOutput(() -> Bai03_XuatMang.xuatAnViTriTongChiSoChan(matrix)),
                "Bài 3c - ẩn vị trí có tổng chỉ số chẵn");
        passGroup("Bài 3a-3c - Xuất ma trận đặc biệt");
    }

    private static void testBai04Den09() {
        assertEquals(79, Bai04Den09_ToanMang.tongTatCa(SAMPLE), "Bài 4");
        assertEquals(36, Bai04Den09_ToanMang.tongSoChan(SAMPLE), "Bài 5");
        assertDoubleEquals(4.9375, Bai04Den09_ToanMang.trungBinhCong(SAMPLE), "Bài 6");
        assertDoubleEquals(Math.pow(17_557_585_920.0, 1.0 / 16.0),
                Bai04Den09_ToanMang.trungBinhNhan(SAMPLE), "Bài 7");
        assertThrows(() -> Bai04Den09_ToanMang.trungBinhNhan(new int[][] {{1, 0, 2}}),
                "Bài 7 - từ chối ma trận có số 0");
        assertThrows(() -> Bai04Den09_ToanMang.trungBinhNhan(new int[][] {{1, -2, 3}}),
                "Bài 7 - từ chối ma trận có số âm");
        assertEquals(31, Bai04Den09_ToanMang.tongSoNguyenTo(SAMPLE), "Bài 8");
        assertEquals(34, Bai04Den09_ToanMang.tongSoHoanThien(new int[][] {{6, 28, 12}}), "Bài 9");
        String output = captureOutput(() -> Bai04Den09_ToanMang.printAllResults(SAMPLE));
        assertContains(output, "Bài 4 - Tổng tất cả các số: 79", "In kết quả Bài 4-9");
        passGroup("Bài 4-9 - Tính trên toàn ma trận");
    }

    private static void testBai10Den14TheoDong() {
        assertArrayEquals(new long[] {22, 17, 22, 18},
                Bai10Den14_TheoDong.tongTatCaMoiDong(SAMPLE), "Bài 10");
        assertArrayEquals(new long[] {6, 8, 12, 10},
                Bai10Den14_TheoDong.tongSoChanMoiDong(SAMPLE), "Bài 11");
        assertArrayEquals(new long[] {-3, -10},
                Bai10Den14_TheoDong.tongSoAmMoiDong(new int[][] {{-1, -2, 3}, {-4, 5, -6}}), "Bài 12");
        assertDoubleArrayEquals(new double[] {5.5, 4.25, 5.5, 4.5},
                Bai10Den14_TheoDong.trungBinhCongMoiDong(SAMPLE), "Bài 13");
        assertArrayEquals(new long[] {9, 4, 10, 8},
                Bai10Den14_TheoDong.tongSoNguyenToMoiDong(SAMPLE), "Bài 14");
        assertArrayEquals(new long[] {0},
                Bai10Den14_TheoDong.tongSoNguyenToMoiDong(new int[][] {{1, 4, 6, 8}}),
                "Bài 14 - dòng không có số nguyên tố bằng 0");

        assertContains(captureOutput(() -> Bai10Den14_TheoDong.printBai10(SAMPLE)),
                "dòng: [0, 2]", "Bài 10 - in đủ hai dòng đồng hạng");
        assertContains(captureOutput(() -> Bai10Den14_TheoDong.printBai11(SAMPLE)),
                "Giá trị lớn nhất: 12", "Bài 11 - in giá trị lớn nhất");
        assertContains(captureOutput(() -> Bai10Den14_TheoDong.printBai12(
                new int[][] {{-1, -2, 3}, {-4, 5, -6}})),
                "Giá trị lớn nhất: -3", "Bài 12 - in tổng âm lớn nhất");
        assertContains(captureOutput(() -> Bai10Den14_TheoDong.printBai13(SAMPLE)),
                "dòng: [0, 2]", "Bài 13 - in đủ dòng đồng hạng");
        assertContains(captureOutput(() -> Bai10Den14_TheoDong.printBai14(SAMPLE)),
                "Dòng 3: Tổng nguyên tố = 8", "Bài 14 - in tổng từng dòng");
        passGroup("Bài 10-14 - Tính theo dòng");
    }

    private static void testBai10Den14TheoCot() {
        assertArrayEquals(new long[] {17, 15, 26, 21},
                Bai10Den14_TheoCot.tongTatCaMoiCot(SAMPLE), "Theo cột - Bài 10");
        assertArrayEquals(new long[] {10, 12, 0, 14},
                Bai10Den14_TheoCot.tongSoChanMoiCot(SAMPLE), "Theo cột - Bài 11");
        assertArrayEquals(new long[] {-5, -2, -6},
                Bai10Den14_TheoCot.tongSoAmMoiCot(new int[][] {{-1, -2, 3}, {-4, 5, -6}}),
                "Theo cột - Bài 12");
        assertDoubleArrayEquals(new double[] {4.25, 3.75, 6.5, 5.25},
                Bai10Den14_TheoCot.trungBinhCongMoiCot(SAMPLE), "Theo cột - Bài 13");
        assertArrayEquals(new long[] {9, 7, 8, 7},
                Bai10Den14_TheoCot.tongSoNguyenToMoiCot(SAMPLE), "Theo cột - Bài 14");
        assertArrayEquals(new long[] {0, 0},
                Bai10Den14_TheoCot.tongSoNguyenToMoiCot(new int[][] {{1, 4}, {6, 8}}),
                "Theo cột - không có số nguyên tố bằng 0");

        assertContains(captureOutput(() -> Bai10Den14_TheoCot.printBai10(SAMPLE)),
                "cột: [2]", "Theo cột - in cột có tổng lớn nhất");
        assertContains(captureOutput(() -> Bai10Den14_TheoCot.printBai11(SAMPLE)),
                "Giá trị lớn nhất: 14", "Theo cột - in tổng chẵn lớn nhất");
        assertContains(captureOutput(() -> Bai10Den14_TheoCot.printBai12(
                new int[][] {{-1, -2, 3}, {-4, 5, -6}})),
                "Giá trị lớn nhất: -2", "Theo cột - in tổng âm lớn nhất");
        assertContains(captureOutput(() -> Bai10Den14_TheoCot.printBai13(SAMPLE)),
                "cột: [2]", "Theo cột - in trung bình lớn nhất");
        assertContains(captureOutput(() -> Bai10Den14_TheoCot.printBai14(SAMPLE)),
                "Cột 0: Tổng nguyên tố = 9", "Theo cột - in tổng nguyên tố");
        passGroup("Phần 4.3.3 - Tính theo cột");
    }

    private static void testBai15() {
        assertEquals(57, Bai15_DuongBien.tongTrenDuongBien(SAMPLE), "Bài 15a");
        assertEquals(22, Bai15_DuongBien.tongKhongThuocDuongBien(SAMPLE), "Bài 15b");
        assertEquals(79, Bai15_DuongBien.tongTrenDuongBien(SAMPLE)
                + Bai15_DuongBien.tongKhongThuocDuongBien(SAMPLE), "Bài 15 - đối chiếu tổng");
        assertEquals(6, Bai15_DuongBien.tongTrenDuongBien(new int[][] {{1, 2, 3}}),
                "Bài 15 - ma trận một dòng không cộng trùng");
        assertEquals(0, Bai15_DuongBien.tongKhongThuocDuongBien(new int[][] {{1, 2, 3}}),
                "Bài 15 - ma trận một dòng không có phần tử trong");
        assertEquals(6, Bai15_DuongBien.tongTrenDuongBien(new int[][] {{1}, {2}, {3}}),
                "Bài 15 - ma trận một cột không cộng trùng");
        passGroup("Bài 15 - Đường biên và phần bên trong");
    }

    private static Scanner scannerOf(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
    }

    private static String captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream replacement = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(replacement);
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static <T> T withoutOutput(ValueSupplier<T> action) {
        final Object[] value = new Object[1];
        captureOutput(() -> value[0] = action.get());
        @SuppressWarnings("unchecked")
        T result = (T) value[0];
        return result;
    }

    private static String cell(int value) {
        return String.format("%8d", value);
    }

    private static void passGroup(String name) {
        groupsPassed++;
        System.out.println("[ĐẠT] " + name);
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError("Sai kiểm tra: " + label);
        }
        passed++;
    }

    private static void assertEquals(long expected, long actual, String label) {
        assertTrue(expected == actual,
                label + " (mong đợi " + expected + ", nhận " + actual + ")");
    }

    private static void assertEquals(String expected, String actual, String label) {
        assertTrue(expected.equals(actual), label);
    }

    private static void assertDoubleEquals(double expected, double actual, String label) {
        assertTrue(Math.abs(expected - actual) < 1.0e-9,
                label + " (mong đợi " + expected + ", nhận " + actual + ")");
    }

    private static void assertArrayEquals(long[] expected, long[] actual, String label) {
        assertEquals(expected.length, actual.length, label + " - độ dài");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], label + " - vị trí " + i);
        }
    }

    private static void assertDoubleArrayEquals(double[] expected, double[] actual, String label) {
        assertEquals(expected.length, actual.length, label + " - độ dài");
        for (int i = 0; i < expected.length; i++) {
            assertDoubleEquals(expected[i], actual[i], label + " - vị trí " + i);
        }
    }

    private static void assertMatrixEquals(int[][] expected, int[][] actual, String label) {
        assertEquals(expected.length, actual.length, label + " - số dòng");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual[i].length, label + " - số cột dòng " + i);
            for (int j = 0; j < expected[i].length; j++) {
                assertEquals(expected[i][j], actual[i][j], label + " - phần tử [" + i + "][" + j + "]");
            }
        }
    }

    private static void assertContains(String actual, String expectedPart, String label) {
        assertTrue(actual.contains(expectedPart), label + " (thiếu: " + expectedPart + ")");
    }

    private static void assertThrows(Runnable action, String label) {
        try {
            action.run();
            throw new AssertionError("Sai kiểm tra: " + label + " (không phát sinh lỗi mong đợi)");
        } catch (IllegalArgumentException expected) {
            passed++;
        }
    }

    @FunctionalInterface
    private interface ValueSupplier<T> {
        T get();
    }
}

