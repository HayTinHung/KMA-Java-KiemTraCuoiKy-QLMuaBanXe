import java.util.Scanner;

class ThoiGian {
    int gio;
    int phut;
    int giay;

    public ThoiGian(int gio, int phut, int giay) {
        this.gio = gio;
        this.phut = phut;
        this.giay = giay;
    }

    public long toTotalSeconds() {
        return (long) gio * 3600 + phut * 60 + giay;
    }

    public static ThoiGian fromTotalSeconds(long sec) {
        sec = Math.abs(sec);
        int g = (int) (sec / 3600);
        int p = (int) ((sec % 3600) / 60);
        int s = (int) (sec % 60);
        return new ThoiGian(g, p, s);
    }

    public void xuat() {
        System.out.printf("%02d:%02d:%02d", gio, phut, giay);
    }
}

public class Bai09_QuanLyThoiGian {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CHUONG TRINH TINH KHOANG CACH GIUA 2 MOC THOI GIAN ===");

        System.out.print("Nhap moc thoi gian 1 (gio phut giay): ");
        ThoiGian t1 = new ThoiGian(sc.nextInt(), sc.nextInt(), sc.nextInt());

        System.out.print("Nhap moc thoi gian 2 (gio phut giay): ");
        ThoiGian t2 = new ThoiGian(sc.nextInt(), sc.nextInt(), sc.nextInt());

        System.out.print("Moc 1: "); t1.xuat(); System.out.println();
        System.out.print("Moc 2: "); t2.xuat(); System.out.println();

        long diffSec = Math.abs(t1.toTotalSeconds() - t2.toTotalSeconds());
        ThoiGian diff = ThoiGian.fromTotalSeconds(diffSec);

        System.out.print("Khoang cach giua 2 moc thoi gian: ");
        diff.xuat();
        System.out.printf(" (%d gio, %d phut, %d giay - Tong cong %d giay)\n", diff.gio, diff.phut, diff.giay, diffSec);

        sc.close();
    }
}
