public class SoHoc {
    public static boolean laSoNguyenTo(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public static boolean laSoHoanThien(int n) {
        if (n <= 1) return false;
        int tong = 1;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                tong += i;
                if (i * i != n) tong += n / i;
            }
        }
        return tong == n;
    }

    public static boolean laSoChinhPhuong(int n) {
        if (n < 0) return false;
        int sq = (int) Math.sqrt(n);
        return sq * sq == n;
    }

    public static boolean laSoMayMan(int n) {
        if (n <= 0) return false;
        int temp = n;
        while (temp > 0) {
            int d = temp % 10;
            if (d != 4 && d != 7) return false;
            temp /= 10;
        }
        return true;
    }
}
