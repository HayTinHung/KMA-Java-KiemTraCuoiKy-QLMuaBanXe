import java.util.Scanner;

class Ngay {
    int ngay;
    int thang;
    int nam;

    public Ngay(int ngay, int thang, int nam) {
        this.ngay = ngay;
        this.thang = thang;
        this.nam = nam;
    }

    public static boolean laNamNhuan(int y) {
        return (y % 400 == 0) || (y % 4 == 0 && y % 100 != 0);
    }

    public static int soNgayTrongThang(int m, int y) {
        switch (m) {
            case 4: case 6: case 9: case 11: return 30;
            case 2: return laNamNhuan(y) ? 29 : 28;
            default: return 31;
        }
    }

    // Chuyen doi ngay/thang/nam thanh tong so ngay tinh tu nam 1
    public long toTotalDays() {
        long days = ngay;
        for (int y = 1; y < nam; y++) {
            days += laNamNhuan(y) ? 366 : 365;
        }
        for (int m = 1; m < thang; m++) {
            days += soNgayTrongThang(m, nam);
        }
        return days;
    }

    public static void tinhKhoangCach(Ngay d1, Ngay d2) {
        Ngay start = d1.toTotalDays() <= d2.toTotalDays() ? d1 : d2;
        Ngay end = d1.toTotalDays() <= d2.toTotalDays() ? d2 : d1;

        int y1 = start.nam, m1 = start.thang, day1 = start.ngay;
        int y2 = end.nam, m2 = end.thang, day2 = end.ngay;

        int diffYear = y2 - y1;
        int diffMonth = m2 - m1;
        int diffDay = day2 - day1;

        if (diffDay < 0) {
            diffMonth--;
            int prevMonth = m2 - 1;
            int prevYear = y2;
            if (prevMonth == 0) {
                prevMonth = 12;
                prevYear--;
            }
            diffDay += soNgayTrongThang(prevMonth, prevYear);
        }

        if (diffMonth < 0) {
            diffYear--;
            diffMonth += 12;
        }

        long totalDays = Math.abs(end.toTotalDays() - start.toTotalDays());

        System.out.printf("in ra ngay %02d/%02d/%d va ngay %02d/%02d/%d cach nhau %d ngay %d thang va %d nam (Tong cong %d ngay)\n",
                start.ngay, start.thang, start.nam,
                end.ngay, end.thang, end.nam,
                diffDay, diffMonth, diffYear, totalDays);
    }
}

public class Bai08_TinhKhoangCachNgay {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CHUONG TRINH TINH KHOANG CACH GIUA 2 NGAY ===");

        System.out.println("Nhap ngay 1 (ngay thang nam):");
        Ngay d1 = new Ngay(sc.nextInt(), sc.nextInt(), sc.nextInt());

        System.out.println("Nhap ngay 2 (ngay thang nam):");
        Ngay d2 = new Ngay(sc.nextInt(), sc.nextInt(), sc.nextInt());

        Ngay.tinhKhoangCach(d1, d2);

        System.out.println("\n--- CHAY DE MO VOI VI DU DUOC GIOI THIEU TRONG DE ---");
        Ngay ex1 = new Ngay(15, 11, 2007);
        Ngay ex2 = new Ngay(11, 9, 2009);
        Ngay.tinhKhoangCach(ex1, ex2);

        sc.close();
    }
}
