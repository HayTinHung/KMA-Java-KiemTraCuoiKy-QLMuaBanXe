import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Giải toàn bộ bài tập Buổi 2 và Buổi 3 trong đề "bai tap giao dien 1.pdf" (Lab 03).
 *
 * Bao gồm:
 * - Buổi 2:
 *   + Kiểm tra số âm / số dương / bằng 0.
 *   + Tính tích giai thừa 1 * 2 * 3 * ... * n.
 *   + Tìm Ước chung lớn nhất (ƯCLN) của 2 số nguyên a, b.
 *   + Bài 1: Tính tiền khách phải trả (giảm 3% nếu Thành tiền > 100).
 *   + Bài 2: Tính tiền điện bậc thang (1-100: 5$, 101-150: 7$, 151-200: 10$, 201-300: 15$, >=301: 20$).
 *   + Bài 3 & 4: In bảng cửu chương từ 1 đến 9 theo hàng dọc và theo hàng ngang.
 *   + Bài 5: Vẽ hình chữ nhật đặc kích thước d x r bằng dấu *.
 * - Buổi 3:
 *   + Bài 6: Vẽ hình chữ nhật rỗng kích thước d x r bằng dấu *.
 *   + Bài 7: Tính tổng bình phương S1 = 1^2 + 2^2 + ... + n^2.
 *   + Bài 8 & 9: Kiểm tra số nguyên tố và liệt kê các số nguyên tố < n.
 *   + Bài 10: Nhập dãy số nguyên (dừng khi nhập 0), tính tổng dãy số vừa nhập.
 *   + Bài tập nâng cao 1 & 2: Giải phương trình bậc 2 (ax^2 + bx + c = 0) & In tam giác cân rỗng.
 *   + Bài tập về nhà 1a, 1b, 2, 3, 4: Tính hàm phân nhánh f(x), số 2 chữ số khác nhau đôi một,
 *     số lẻ < n, đếm số chẵn trong đoạn [n, m].
 */
public final class Buoi02_03_CoBanVaVongLap {

    // 1. Kiểm tra số âm hay số dương
    public static String kiemTraAmDuong(int n) {
        if (n > 0) return "Số dương";
        if (n < 0) return "Số âm";
        return "Số 0";
    }

    // 2. Tính tích 1*2*3*...*n
    public static long tinhTich1DenN(int n) {
        if (n < 1) throw new IllegalArgumentException("n phải >= 1");
        long prod = 1L;
        for (int i = 2; i <= n; i++) {
            prod *= i;
        }
        return prod;
    }

    // 3. Tìm ƯCLN của hai số nguyên a và b
    public static int ucln(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int r = a % b;
            a = b;
            b = r;
        }
        return a;
    }

    // 4. Tính tiền khách phải trả (Thành tiền = đơn giá * số lượng; nếu > 100 giảm 3%)
    public static double tinhTienMuaHang(double donGia, int soLuong) {
        double thanhTien = donGia * soLuong;
        double giamGia = (thanhTien > 100.0) ? (thanhTien * 0.03) : 0.0;
        return thanhTien - giamGia;
    }

    // 5. Tính tiền điện bậc thang lũy tiến (Ví dụ: 50KW -> 250$, 101KW -> 507$)
    public static long tinhTienDien(int kw) {
        if (kw <= 0) return 0L;
        long tien = 0L;
        int bac1 = Math.min(kw, 100);
        tien += bac1 * 5L;
        if (kw > 100) {
            int bac2 = Math.min(kw - 100, 50);
            tien += bac2 * 7L;
        }
        if (kw > 150) {
            int bac3 = Math.min(kw - 150, 50);
            tien += bac3 * 10L;
        }
        if (kw > 200) {
            int bac4 = Math.min(kw - 200, 100);
            tien += bac4 * 15L;
        }
        if (kw > 300) {
            int bac5 = kw - 300;
            tien += bac5 * 20L;
        }
        return tien;
    }

    // 6. Bảng cửu chương dọc & ngang
    public static String bangCuuChuongDoc() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 9; i++) {
            sb.append("--- Bảng nhân ").append(i).append(" ---\n");
            for (int j = 1; j <= 10; j++) {
                sb.append(String.format("%d x %2d = %2d%n", i, j, i * j));
            }
        }
        return sb.toString();
    }

    public static String bangCuuChuongNgang() {
        StringBuilder sb = new StringBuilder();
        for (int j = 1; j <= 10; j++) {
            for (int i = 1; i <= 9; i++) {
                sb.append(String.format("%d x %2d = %2d   ", i, j, i * j));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // 7. Vẽ hình chữ nhật đặc và rỗng kích thước d (chiều dài/cột) x r (chiều rộng/dòng)
    public static String veHinhChuNhatDac(int d, int r) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < d; j++) {
                sb.append(j == d - 1 ? "*" : "*   ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String veHinhChuNhatRong(int d, int r) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < d; j++) {
                boolean isBorder = (i == 0 || i == r - 1 || j == 0 || j == d - 1);
                sb.append(isBorder ? "*" : " ");
                if (j < d - 1) sb.append("   ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // 8. Tính tổng bình phương S1 = 1^2 + 2^2 + ... + n^2
    public static long tinhTongBinhPhuong(int n) {
        long sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += (long) i * i;
        }
        return sum;
    }

    // 9. Kiểm tra số nguyên tố & liệt kê các số nguyên tố < n
    public static boolean laSoNguyenTo(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public static List<Integer> lietKeNguyenToNhoHon(int n) {
        List<Integer> res = new ArrayList<>();
        for (int i = 2; i < n; i++) {
            if (laSoNguyenTo(i)) res.add(i);
        }
        return res;
    }

    // 10. Giải phương trình bậc 2: ax^2 + bx + c = 0
    public static String giaiPhuongTrinhBac2(double a, double b, double c) {
        if (Math.abs(a) < 1e-12) {
            if (Math.abs(b) < 1e-12) {
                return Math.abs(c) < 1e-12 ? "Vô số nghiệm" : "Vô nghiệm";
            }
            return String.format("Phương trình bậc nhất có nghiệm x = %.4f", -c / b);
        }
        double delta = b * b - 4 * a * c;
        if (delta < 0) return "Vô nghiệm thực";
        if (Math.abs(delta) < 1e-12) {
            return String.format("Nghiệm kép x1 = x2 = %.4f", -b / (2 * a));
        }
        double x1 = (-b - Math.sqrt(delta)) / (2 * a);
        double x2 = (-b + Math.sqrt(delta)) / (2 * a);
        return String.format("Hai nghiệm phân biệt: x1 = %.4f, x2 = %.4f", x1, x2);
    }

    // 11. In tam giác cân rỗng chiều cao h
    public static String veTamGiacCanRong(int h) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= h; i++) {
            for (int j = 1; j <= 2 * h - 1; j++) {
                if (i == h || j == h - i + 1 || j == h + i - 1) {
                    sb.append("*");
                } else {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // 12. Bài tập về nhà: Tính hàm phân nhánh f(x) câu a và câu b
    public static double tinhHamFa(double x) {
        if (x > 0) {
            return 3.0 * x + Math.sqrt(x);
        }
        return Math.exp(x) + 4.0;
    }

    public static double tinhHamFb(double x) {
        if (x >= 1.0) {
            return Math.sqrt(x * x + 1.0);
        }
        if (x > -1.0) {
            return 3.0 * x + 5.0;
        }
        return x * x + 2.0 * x - 1.0;
    }

    // 13. Xuất các số có 2 chữ số sao cho các chữ số khác nhau đôi một (10, 12, ..., 98)
    public static List<Integer> soHaiChuSoKhacNhau() {
        List<Integer> list = new ArrayList<>();
        for (int n = 10; n <= 99; n++) {
            int chuc = n / 10;
            int donVi = n % 10;
            if (chuc != donVi) {
                list.add(n);
            }
        }
        return list;
    }

    // 14. Đếm số lượng số chẵn trong đoạn [n, m]
    public static int demSoChanTrongDoan(int n, int m) {
        int low = Math.min(n, m);
        int high = Math.max(n, m);
        int count = 0;
        for (int x = low; x <= high; x++) {
            if (x % 2 == 0) count++;
        }
        return count;
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            System.out.println("Kiểm thử Buổi 2 & 3: 50KW=" + tinhTienDien(50) + "$, 101KW=" + tinhTienDien(101) + "$");
            return;
        }

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n================ MENU LAB 03 - BUỔI 2 & BUỔI 3 ================");
                System.out.println("1. Kiểm tra số âm/dương, giai thừa 1..n và ƯCLN(a, b)");
                System.out.println("2. Tính tiền mua hàng (giảm 3% nếu > 100) & Tiền điện bậc thang");
                System.out.println("3. In bảng cửu chương 1..9 (dọc và ngang)");
                System.out.println("4. Vẽ hình chữ nhật đặc, rỗng (d x r) và tam giác cân rỗng");
                System.out.println("5. Tổng bình phương S1, Số nguyên tố < n, Giải PT bậc 2");
                System.out.println("6. Bài tập về nhà: Hàm f(x), Số 2 chữ số khác nhau, Đếm số chẵn [n, m]");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");
                String line = sc.hasNextLine() ? sc.nextLine().trim() : "0";
                if ("0".equals(line) || line.isEmpty()) break;

                switch (line) {
                    case "1":
                        System.out.println("-15 là: " + kiemTraAmDuong(-15) + "; 5! = " + tinhTich1DenN(5) + "; ƯCLN(36, 48) = " + ucln(36, 48));
                        break;
                    case "2":
                        System.out.println("Mua hàng (đơn giá 60, SL 2): " + tinhTienMuaHang(60, 2));
                        System.out.println("Tiền điện 50KW = " + tinhTienDien(50) + "$; 101KW = " + tinhTienDien(101) + "$");
                        break;
                    case "3":
                        System.out.println(bangCuuChuongNgang());
                        break;
                    case "4":
                        System.out.println("Hình chữ nhật rỗng 5x3:\n" + veHinhChuNhatRong(5, 3));
                        System.out.println("Tam giác cân rỗng h=4:\n" + veTamGiacCanRong(4));
                        break;
                    case "5":
                        System.out.println("S1(3) = " + tinhTongBinhPhuong(3));
                        System.out.println("Các số nguyên tố < 20: " + lietKeNguyenToNhoHon(20));
                        System.out.println("PT x^2 - 5x + 6 = 0: " + giaiPhuongTrinhBac2(1, -5, 6));
                        break;
                    case "6":
                        System.out.printf("fa(4) = %.4f, fb(2) = %.4f%n", tinhHamFa(4), tinhHamFb(2));
                        System.out.println("Số lượng số 2 chữ số khác nhau đôi một: " + soHaiChuSoKhacNhau().size());
                        System.out.println("Số chẵn trong đoạn [1, 10]: " + demSoChanTrongDoan(1, 10));
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
            }
        }
    }
}
