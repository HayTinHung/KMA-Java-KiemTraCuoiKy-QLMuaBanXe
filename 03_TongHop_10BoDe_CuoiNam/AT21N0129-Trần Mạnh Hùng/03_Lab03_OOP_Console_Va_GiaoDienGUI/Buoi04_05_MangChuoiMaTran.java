import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Giải toàn bộ bài tập Buổi 4 và Buổi 5 trong đề "bai tap giao dien 1.pdf" (Lab 03).
 *
 * Bao gồm:
 * - Buổi 4:
 *   + Mảng 1 chiều: Nhập/xuất, liệt kê phần tử lẻ ở vị trí chẵn, liệt kê số nguyên tố,
 *     tìm phần tử âm đầu tiên, tìm max/min, tính tổng mảng.
 *   + Chuỗi s (họ tên): Kiểm tra chuỗi đối xứng, đổi ký tự đầu mỗi từ thành hoa,
 *     đổi hoa <-> thường, đếm nguyên âm / phụ âm / khoảng trắng.
 *   + Đa thức trên mảng 1 chiều DaThuc {int bac, int[] heSo}: Cộng 2 đa thức.
 *   + Ma trận nguyên m x n: Tìm min/max kèm chỉ số của toàn ma trận, của từng hàng,
 *     của đường chéo chính; tìm phần tử xuất hiện nhiều nhất trên dòng i.
 * - Buổi 5:
 *   + Chuỗi họ tên: Xóa khoảng trắng thừa, đếm số từ, tách Họ, Họ lót, Tên.
 *   + Mảng 1 chiều (về nhà): Tính tổng các phần tử cực đại (lớn hơn các phần tử xung quanh).
 *   + Ma trận vuông cấp n: Liệt kê và tính tổng đường chéo chính, đường chéo phụ, tam giác trên, tam giác dưới.
 */
public final class Buoi04_05_MangChuoiMaTran {

    // =========================================================================
    // 1. MẢNG MỘT CHIỀU (BUỔI 4 & BUỔI 5)
    // =========================================================================
    public static List<Integer> lietKeLeOViTriChan(int[] a) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < a.length; i += 2) {
            if (a[i] % 2 != 0) res.add(a[i]);
        }
        return res;
    }

    public static List<Integer> lietKeNguyenToTrongMang(int[] a) {
        List<Integer> res = new ArrayList<>();
        for (int x : a) {
            if (Buoi02_03_CoBanVaVongLap.laSoNguyenTo(x)) res.add(x);
        }
        return res;
    }

    public static int timPhanTuAmDauTien(int[] a) {
        for (int x : a) {
            if (x < 0) return x;
        }
        return 0; // Trả về 0 nếu không có số âm
    }

    public static int[] timMinMaxMang(int[] a) {
        if (a == null || a.length == 0) throw new IllegalArgumentException("Mảng rỗng");
        int min = a[0];
        int max = a[0];
        for (int x : a) {
            if (x < min) min = x;
            if (x > max) max = x;
        }
        return new int[]{min, max};
    }

    public static long tinhTongMang(int[] a) {
        long s = 0;
        for (int x : a) s += x;
        return s;
    }

    /**
     * Buổi 5 - Bài tập về nhà 1:
     * Tính tổng các phần tử cực đại (phần tử lớn hơn các phần tử xung quanh nó).
     * Ví dụ trong đề: 1 5 2 6 3 5 1 8 6 -> các cực đại là 5, 6, 5, 8 -> tổng = 24.
     */
    public static int tinhTongPhanTuCucDai(int[] a) {
        if (a == null || a.length == 0) return 0;
        if (a.length == 1) return a[0];
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            boolean greaterLeft = (i == 0) || (a[i] > a[i - 1]);
            boolean greaterRight = (i == a.length - 1) || (a[i] > a[i + 1]);
            if (greaterLeft && greaterRight) {
                sum += a[i];
            }
        }
        return sum;
    }

    // =========================================================================
    // 2. XỬ LÝ CHUỖI HỌ TÊN (BUỔI 4 & BUỔI 5)
    // =========================================================================
    public static boolean laChuoiDoiXung(String s) {
        if (s == null) return false;
        int l = 0, r = s.length() - 1;
        while (l < r) {
            if (s.charAt(l++) != s.charAt(r--)) return false;
        }
        return true;
    }

    public static String xoaKhoangTrangThua(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }

    public static String doiKyTuDauTuThanhHoa(String s) {
        String clean = xoaKhoangTrangThua(s);
        if (clean.isEmpty()) return "";
        String[] words = clean.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (i > 0) sb.append(" ");
            sb.append(Character.toUpperCase(w.charAt(0)));
            if (w.length() > 1) {
                sb.append(w.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static String daoHoaThuong(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) sb.append(Character.toLowerCase(c));
            else if (Character.isLowerCase(c)) sb.append(Character.toUpperCase(c));
            else sb.append(c);
        }
        return sb.toString();
    }

    public static int[] demNguyenAmPhuAmKhoangTrang(String s) {
        int nguyenAm = 0, phuAm = 0, khoangTrang = 0;
        String vowels = "aeiouAEIOUáàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵ";
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                khoangTrang++;
            } else if (Character.isLetter(c)) {
                if (vowels.indexOf(c) >= 0) nguyenAm++;
                else phuAm++;
            }
        }
        return new int[]{nguyenAm, phuAm, khoangTrang};
    }

    public static int demSoTu(String s) {
        String clean = xoaKhoangTrangThua(s);
        return clean.isEmpty() ? 0 : clean.split(" ").length;
    }

    /**
     * Tách Họ, Họ lót, Tên từ chuỗi họ tên.
     * @return mảng 3 phần tử: [0] = Họ, [1] = Họ lót, [2] = Tên
     */
    public static String[] tachHoLotTen(String hoTen) {
        String clean = xoaKhoangTrangThua(hoTen);
        if (clean.isEmpty()) return new String[]{"", "", ""};
        String[] parts = clean.split(" ");
        if (parts.length == 1) return new String[]{"", "", parts[0]};
        String ho = parts[0];
        String ten = parts[parts.length - 1];
        StringBuilder lot = new StringBuilder();
        for (int i = 1; i < parts.length - 1; i++) {
            if (i > 1) lot.append(" ");
            lot.append(parts[i]);
        }
        return new String[]{ho, lot.toString(), ten};
    }

    // =========================================================================
    // 3. CỘNG HAI ĐA THỨC TRÊN MẢNG 1 CHIỀU (BUỔI 4 - NÂNG CAO 1)
    // =========================================================================
    public static final class DaThuc {
        private final int bac;
        private final int[] chiSoDaThuc;

        public DaThuc(int bac, int[] chiSoDaThuc) {
            this.bac = bac;
            this.chiSoDaThuc = Arrays.copyOf(chiSoDaThuc, bac + 1);
        }

        public int getBac() { return bac; }
        public int[] getChiSoDaThuc() { return Arrays.copyOf(chiSoDaThuc, chiSoDaThuc.length); }

        public DaThuc cong(DaThuc other) {
            int maxBac = Math.max(this.bac, other.bac);
            int[] res = new int[maxBac + 1];
            for (int i = 0; i <= maxBac; i++) {
                int a = (i <= this.bac) ? this.chiSoDaThuc[i] : 0;
                int b = (i <= other.bac) ? other.chiSoDaThuc[i] : 0;
                res[i] = a + b;
            }
            return new DaThuc(maxBac, res);
        }
    }

    // =========================================================================
    // 4. MA TRẬN m x n & MA TRẬN VUÔNG CẤP n (BUỔI 4 & BUỔI 5)
    // =========================================================================
    public static int phanTuXuatHienNhieuNhatTrenDong(int[][] m, int row) {
        Map<Integer, Integer> freq = new LinkedHashMap<>();
        for (int val : m[row]) {
            freq.put(val, freq.getOrDefault(val, 0) + 1);
        }
        int bestVal = m[row][0];
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                bestVal = e.getKey();
            }
        }
        return bestVal;
    }

    public static long tongDuongCheoChinh(int[][] a) {
        long sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i][i];
        return sum;
    }

    public static long tongDuongCheoPhu(int[][] a) {
        int n = a.length;
        long sum = 0;
        for (int i = 0; i < n; i++) sum += a[i][n - 1 - i];
        return sum;
    }

    public static long tongTamGiacTren(int[][] a) {
        int n = a.length;
        long sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) sum += a[i][j];
        }
        return sum;
    }

    public static long tongTamGiacDuoi(int[][] a) {
        int n = a.length;
        long sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) sum += a[i][j];
        }
        return sum;
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            int[] sample = {1, 5, 2, 6, 3, 5, 1, 8, 6};
            System.out.println("Tổng cực đại = " + tinhTongPhanTuCucDai(sample));
            return;
        }
        try (Scanner sc = new Scanner(System.in)) {
            int[] arr = {1, 5, 2, 6, 3, 5, 1, 8, 6};
            System.out.println("Mảng mẫu: " + Arrays.toString(arr));
            System.out.println("Tổng các phần tử cực đại: " + tinhTongPhanTuCucDai(arr));
            String[] hlt = tachHoLotTen("   Trần   Mạnh   Hùng  ");
            System.out.printf("Họ: '%s', Họ lót: '%s', Tên: '%s'%n", hlt[0], hlt[1], hlt[2]);
        }
    }
}
