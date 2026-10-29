import java.util.ArrayList;
import java.util.Scanner;

/**
 * FILE TONG HOP DE NOP BAI: BÀI THỰC HÀNH SỐ 04 (KIỂU CẤU TRÚC / CLASS OOP JAVA)
 * Gom toan bo 9 bai tap vao 1 file duy nhat co Menu chon bai.
 */
public class NopBai_CauTrucLab1 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int chon = -1;
        while (chon != 0) {
            System.out.println("\n=======================================================");
            System.out.println("  MENU CHON BAI THUC HANH SO 04 (CAU TRUC / CLASS OOP)");
            System.out.println("=======================================================");
            System.out.println(" 1. Bai 1: Quan ly Mang Phan So");
            System.out.println(" 2. Bai 2: Quan ly Danh muc Bo Phim");
            System.out.println(" 3. Bai 3: Quan ly Danh muc May Tinh");
            System.out.println(" 4. Bai 4: Quan ly Linh Kien May Tinh");
            System.out.println(" 5. Bai 5: Quan ly Thu Vien Dau Sach");
            System.out.println(" 6. Bai 6: Quan ly Danh muc Mat Hang");
            System.out.println(" 7. Bai 7: Quan ly Ho so Nhan Vien");
            System.out.println(" 8. Bai 8: Tinh Khoang cach giua 2 Ngay");
            System.out.println(" 9. Bai 9: Hien thi va Tinh khoang cach Thoi Gian");
            System.out.println(" 0. Thoat chuong trinh");
            System.out.println("=======================================================");
            System.out.print("Nhap lua chon cua ban (0-9): ");

            if (!sc.hasNextInt()) {
                break;
            }
            chon = sc.nextInt();

            switch (chon) {
                case 1:
                    chayBai1(sc);
                    break;
                case 2:
                    chayBai2(sc);
                    break;
                case 3:
                    chayBai3(sc);
                    break;
                case 4:
                    chayBai4(sc);
                    break;
                case 5:
                    chayBai5(sc);
                    break;
                case 6:
                    chayBai6(sc);
                    break;
                case 7:
                    chayBai7(sc);
                    break;
                case 8:
                    chayBai8(sc);
                    break;
                case 9:
                    chayBai9(sc);
                    break;
                case 0:
                    System.out.println("Tam biet!");
                    break;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
        sc.close();
    }

    // =========================================================================
    // BÀI 1: PHÂN SỐ
    // =========================================================================
    static class PhanSoModel {
        int tu, mau;
        public PhanSoModel(int tu, int mau) {
            this.tu = tu;
            this.mau = (mau == 0) ? 1 : mau;
            rutGon();
        }
        private int ucln(int a, int b) {
            a = Math.abs(a); b = Math.abs(b);
            while (b != 0) { int t = b; b = a % b; a = t; }
            return a == 0 ? 1 : a;
        }
        public void rutGon() {
            int u = ucln(tu, mau);
            tu /= u; mau /= u;
            if (mau < 0) { tu = -tu; mau = -mau; }
        }
        public PhanSoModel cong(PhanSoModel p) { return new PhanSoModel(tu * p.mau + p.tu * mau, mau * p.mau); }
        public PhanSoModel tru(PhanSoModel p) { return new PhanSoModel(tu * p.mau - p.tu * mau, mau * p.mau); }
        public PhanSoModel nhan(PhanSoModel p) { return new PhanSoModel(tu * p.tu, mau * p.mau); }
        public PhanSoModel chia(PhanSoModel p) { return new PhanSoModel(tu * p.mau, mau * p.tu); }
        public double giaTri() { return (double) tu / mau; }
        public void xuat() { System.out.print(mau == 1 ? tu : (tu + "/" + mau)); }
    }

    private static void chayBai1(Scanner sc) {
        System.out.println("\n--- BÀI 1: QUẢN LÝ MẢNG PHÂN SỐ ---");
        System.out.print("Nhap n: ");
        int n = sc.nextInt();
        PhanSoModel[] A = new PhanSoModel[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Nhap tu va mau PS " + (i + 1) + ": ");
            int tu = sc.nextInt();
            int mau = sc.nextInt();
            A[i] = new PhanSoModel(tu, mau);
        }
        System.out.print("Danh sach PS: ");
        for (PhanSoModel p : A) { p.xuat(); System.out.print("  "); }
        System.out.println();

        PhanSoModel max = A[0], min = A[0];
        for (int i = 1; i < n; i++) {
            if (A[i].giaTri() > max.giaTri()) max = A[i];
            if (A[i].giaTri() < min.giaTri()) min = A[i];
        }
        System.out.print("Max = "); max.xuat(); System.out.println();
        System.out.print("Min = "); min.xuat(); System.out.println();
        System.out.print("Max + Min = "); max.cong(min).xuat(); System.out.println();
        System.out.print("Max - Min = "); max.tru(min).xuat(); System.out.println();
        System.out.print("Max * Min = "); max.nhan(min).xuat(); System.out.println();
        System.out.print("Max / Min = "); max.chia(min).xuat(); System.out.println();
    }

    // =========================================================================
    // BÀI 2: BỘ PHIM
    // =========================================================================
    static class PhimModel {
        String tenPhim; double doanhThu; int theLoai, namSanXuat; boolean isPhimVN;
        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Ten phim: "); tenPhim = sc.nextLine();
            System.out.print("Doanh thu: "); doanhThu = sc.nextDouble();
            System.out.print("The loai (0-Hinh su, 1-Tinh cam, 2-Hai): "); theLoai = sc.nextInt();
            System.out.print("Nam SX: "); namSanXuat = sc.nextInt();
            System.out.print("Is Phim Viet Nam (true/false): "); isPhimVN = sc.nextBoolean();
        }
        public void xuat() {
            String tl = (theLoai == 0) ? "Hinh su" : (theLoai == 1) ? "Tinh cam" : "Hai";
            System.out.printf("%-20s | DT: %-8.2f | TL: %-10s | Nam: %-4d | VN: %b\n", tenPhim, doanhThu, tl, namSanXuat, isPhimVN);
        }
    }

    private static void chayBai2(Scanner sc) {
        System.out.println("\n--- BÀI 2: QUẢN LÝ BỘ PHIM ---");
        System.out.print("Nhap n phim: ");
        int n = sc.nextInt();
        PhimModel[] ds = new PhimModel[n];
        for (int i = 0; i < n; i++) {
            System.out.println("Nhap phim " + (i + 1) + ":");
            ds[i] = new PhimModel(); ds[i].nhap(sc);
        }
        System.out.println("\nDanh sach phim:");
        for (PhimModel p : ds) p.xuat();

        double tongVN = 0;
        for (PhimModel p : ds) if (p.isPhimVN) tongVN += p.doanhThu;
        System.out.printf("Tong doanh thu phim VN: %.2f\n", tongVN);
    }

    // =========================================================================
    // BÀI 3: MÁY TÍNH
    // =========================================================================
    static class MayTinhModel {
        String model, hangSX; int baoHanh; float giaTien; boolean isVN;
        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Model: "); model = sc.nextLine();
            System.out.print("Hang SX: "); hangSX = sc.nextLine();
            do {
                System.out.print("Bao hanh (1:3th, 2:6th, 3:12th, 4:24th, 5:36th): ");
                baoHanh = sc.nextInt();
            } while (baoHanh < 1 || baoHanh > 5);
            System.out.print("Gia tien: "); giaTien = sc.nextFloat();
            char c;
            do {
                System.out.print("SX tai VN (Y/N): "); c = sc.next().charAt(0);
            } while (c != 'Y' && c != 'y' && c != 'N' && c != 'n');
            isVN = (c == 'Y' || c == 'y');
        }
        public int getThangBH() {
            int[] tb = {0, 3, 6, 12, 24, 36}; return tb[baoHanh];
        }
        public void xuat() {
            System.out.printf("Model: %-10s | Hang: %-10s | BH: %-2d thang | Gia: %-8.2f | VN: %b\n", model, hangSX, getThangBH(), giaTien, isVN);
        }
    }

    private static void chayBai3(Scanner sc) {
        System.out.println("\n--- BÀI 3: QUẢN LÝ MÁY TÍNH ---");
        System.out.print("Nhap n may tinh: ");
        int n = sc.nextInt();
        MayTinhModel[] ds = new MayTinhModel[n];
        for (int i = 0; i < n; i++) {
            System.out.println("Nhap may tinh " + (i + 1) + ":");
            ds[i] = new MayTinhModel(); ds[i].nhap(sc);
        }
        System.out.println("\nDanh sach may tinh:");
        for (MayTinhModel m : ds) m.xuat();
    }

    // =========================================================================
    // BÀI 4: LINH KIỆN
    // =========================================================================
    static class LinhKienModel {
        String ten, quyCach; char loai; int donGia1, donGia2;
        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Ten linh kien: "); ten = sc.nextLine();
            System.out.print("Quy cach: "); quyCach = sc.nextLine();
            System.out.print("Loai (A/B): "); loai = Character.toUpperCase(sc.next().charAt(0));
            System.out.print("Don gia loại 1: "); donGia1 = sc.nextInt();
            System.out.print("Don gia loại 2: "); donGia2 = sc.nextInt();
        }
        public void xuat() {
            System.out.printf("Loai: %c | Ten: %-15s | Quy cach: %-10s | Gia1: %d | Gia2: %d\n", loai, ten, quyCach, donGia1, donGia2);
        }
    }

    private static void chayBai4(Scanner sc) {
        System.out.println("\n--- BÀI 4: QUẢN LÝ LINH KIỆN ---");
        System.out.print("Nhap n linh kien: ");
        int n = sc.nextInt();
        LinhKienModel[] ds = new LinhKienModel[n];
        int countA = 0;
        for (int i = 0; i < n; i++) {
            ds[i] = new LinhKienModel(); ds[i].nhap(sc);
            if (ds[i].loai == 'A') countA++;
        }
        System.out.println("Du 10 linh kien loai A chua: " + (countA >= 10 ? "ROI" : "CHUA"));
    }

    // =========================================================================
    // BÀI 5: ĐẦU SÁCH THƯ VIỆN
    // =========================================================================
    static class DauSachModel {
        String maSach, tenSach, tacGia; int soLuong;
        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Ma sach: "); maSach = sc.nextLine();
            System.out.print("Ten sach: "); tenSach = sc.nextLine();
            System.out.print("Tac gia: "); tacGia = sc.nextLine();
            System.out.print("So luong: "); soLuong = sc.nextInt();
        }
        public void xuat() {
            System.out.printf("Ma: %-8s | Ten: %-20s | TG: %-15s | SL: %d\n", maSach, tenSach, tacGia, soLuong);
        }
    }

    private static void chayBai5(Scanner sc) {
        System.out.println("\n--- BÀI 5: QUẢN LÝ THƯ VIỆN ĐẦU SÁCH ---");
        System.out.print("Nhap n dau sach: ");
        int n = sc.nextInt();
        DauSachModel[] ds = new DauSachModel[n];
        int tong = 0;
        for (int i = 0; i < n; i++) {
            ds[i] = new DauSachModel(); ds[i].nhap(sc);
            tong += ds[i].soLuong;
        }
        System.out.println("Tong so sach trong thu vien: " + tong);
    }

    // =========================================================================
    // BÀI 6: MẶT HÀNG
    // =========================================================================
    static class MatHangModel {
        String ma, ten; int soLuong; double donGia; int ton, baoHanh;
        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Ma hang: "); ma = sc.nextLine();
            System.out.print("Ten hang: "); ten = sc.nextLine();
            System.out.print("So luong: "); soLuong = sc.nextInt();
            System.out.print("Don gia: "); donGia = sc.nextDouble();
            System.out.print("Ton kho: "); ton = sc.nextInt();
            System.out.print("Bao hanh (thang): "); baoHanh = sc.nextInt();
        }
        public void xuat() {
            System.out.printf("Ma: %-6s | Ten: %-15s | SL: %-3d | Gia: %-8.2f | Ton: %-3d | BH: %d th\n", ma, ten, soLuong, donGia, ton, baoHanh);
        }
    }

    private static void chayBai6(Scanner sc) {
        System.out.println("\n--- BÀI 6: QUẢN LÝ MẶT HÀNG ---");
        System.out.print("Nhap n mat hang: ");
        int n = sc.nextInt();
        MatHangModel[] ds = new MatHangModel[n];
        for (int i = 0; i < n; i++) { ds[i] = new MatHangModel(); ds[i].nhap(sc); }
        System.out.println("\nDanh sach mat hang:");
        for (MatHangModel m : ds) m.xuat();
    }

    // =========================================================================
    // BÀI 7: NHÂN VIÊN
    // =========================================================================
    static class NhanVienModel {
        String hoTen, phai, ngaySinh, diaChi; double luongCB, thuong, bhxh, phat;
        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Ho va ten: "); hoTen = sc.nextLine();
            System.out.print("Phai: "); phai = sc.nextLine();
            System.out.print("Ngay sinh: "); ngaySinh = sc.nextLine();
            System.out.print("Dia chi: "); diaChi = sc.nextLine();
            System.out.print("Luong CB: "); luongCB = sc.nextDouble();
            System.out.print("Thuong: "); thuong = sc.nextDouble();
            System.out.print("BHXH: "); bhxh = sc.nextDouble();
            System.out.print("Phat: "); phat = sc.nextDouble();
        }
        public double getThucLinh() { return luongCB + thuong - bhxh - phat; }
        public void xuat() {
            System.out.printf("Ten: %-15s | Phai: %-4s | Luong thuc linh: %.2f\n", hoTen, phai, getThucLinh());
        }
    }

    private static void chayBai7(Scanner sc) {
        System.out.println("\n--- BÀI 7: QUẢN LÝ NHÂN VIÊN ---");
        System.out.print("Nhap n nhan vien: ");
        int n = sc.nextInt();
        ArrayList<NhanVienModel> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            NhanVienModel nv = new NhanVienModel(); nv.nhap(sc); list.add(nv);
        }
        list.sort((a, b) -> Double.compare(b.getThucLinh(), a.getThucLinh()));
        System.out.println("\nDanh sach giam dan theo luong thực linh:");
        for (NhanVienModel nv : list) nv.xuat();
    }

    // =========================================================================
    // BÀI 8: KHOẢNG CÁCH NGÀY
    // =========================================================================
    static class NgayModel {
        int d, m, y;
        public NgayModel(int d, int m, int y) { this.d = d; this.m = m; this.y = y; }
        public static boolean isLeap(int y) { return (y % 400 == 0) || (y % 4 == 0 && y % 100 != 0); }
        public static int daysInMonth(int m, int y) {
            if (m == 4 || m == 6 || m == 9 || m == 11) return 30;
            if (m == 2) return isLeap(y) ? 29 : 28;
            return 31;
        }
        public long toDays() {
            long total = d;
            for (int i = 1; i < y; i++) total += isLeap(i) ? 366 : 365;
            for (int i = 1; i < m; i++) total += daysInMonth(i, y);
            return total;
        }
    }

    private static void chayBai8(Scanner sc) {
        System.out.println("\n--- BÀI 8: TÍNH KHOẢNG CÁCH NGÀY ---");
        System.out.print("Nhap ngay 1 (d m y): "); NgayModel d1 = new NgayModel(sc.nextInt(), sc.nextInt(), sc.nextInt());
        System.out.print("Nhap ngay 2 (d m y): "); NgayModel d2 = new NgayModel(sc.nextInt(), sc.nextInt(), sc.nextInt());
        long diff = Math.abs(d1.toDays() - d2.toDays());
        System.out.printf("Ngay %02d/%02d/%d va %02d/%02d/%d cach nhau %d ngay\n", d1.d, d1.m, d1.y, d2.d, d2.m, d2.y, diff);
    }

    // =========================================================================
    // BÀI 9: KHOẢNG CÁCH THỜI GIAN
    // =========================================================================
    static class ThoiGianModel {
        int g, p, s;
        public ThoiGianModel(int g, int p, int s) { this.g = g; this.p = p; this.s = s; }
        public long toSec() { return (long) g * 3600 + p * 60 + s; }
        public void xuat() { System.out.printf("%02d:%02d:%02d", g, p, s); }
    }

    private static void chayBai9(Scanner sc) {
        System.out.println("\n--- BÀI 9: THỜI GIAN ---");
        System.out.print("Nhap thoi gian 1 (h m s): "); ThoiGianModel t1 = new ThoiGianModel(sc.nextInt(), sc.nextInt(), sc.nextInt());
        System.out.print("Nhap thoi gian 2 (h m s): "); ThoiGianModel t2 = new ThoiGianModel(sc.nextInt(), sc.nextInt(), sc.nextInt());
        long diffSec = Math.abs(t1.toSec() - t2.toSec());
        long h = diffSec / 3600;
        long m = (diffSec % 3600) / 60;
        long s = diffSec % 60;
        System.out.printf("Khoang cach: %02d:%02d:%02d (%d gio, %d phut, %d giay)\n", h, m, s, h, m, s);
    }
}
