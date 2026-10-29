import java.util.ArrayList;
import java.util.Scanner;

/**
 * ============================================================================
 * TRƯỜNG ĐẠI HỌC QUỐC TẾ HỒNG BÀNG - KHOA CÔNG NGHỆ KỸ THUẬT - BỘ MÔN CNTT
 * BÀI THỰC HÀNH SỐ 04: KIỂU CẤU TRÚC / LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG (CLASS OOP)
 * ============================================================================
 * File gộp duy nhất chứa toàn bộ 9 bài tập trong giáo trình Lab 04.
 * Bao gồm Menu chọn bài chạy thử cực kỳ tiện lợi cho việc nộp bài.
 */
public class NopBaiLab {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int chon = -1;
        while (chon != 0) {
            System.out.println("\n=======================================================");
            System.out.println("  MENU THỰC HÀNH SỐ 04: KIỂU CẤU TRÚC / CLASS OOP JAVA ");
            System.out.println("=======================================================");
            System.out.println(" 1. Bài 1: Quản lý mảng Phân Số (Cộng, Trừ, Nhân, Chia, Max, Min, Sort)");
            System.out.println(" 2. Bài 2: Quản lý danh mục Bộ Phim (Lọc thể loại, Doanh thu VN, DT Trung bình)");
            System.out.println(" 3. Bài 3: Quản lý Máy Tính cửa hàng (Kiểm tra dữ liệu nhập, Đếm khoảng giá)");
            System.out.println(" 4. Bài 4: Quản lý Linh Kiện máy tính (Sắp xếp Loại+Tên, Kiểm tra đủ 10 loại A)");
            System.out.println(" 5. Bài 5: Quản lý thư viện Đầu Sách (Tìm kiếm tên sách, Tính tổng số sách)");
            System.out.println(" 6. Bài 6: Quản lý danh mục Mặt Hàng (Tìm tồn nhiều/ít nhất, Bảo hành > 12th)");
            System.out.println(" 7. Bài 7: Quản lý Hồ Sơ Nhân Viên (Lương thực lĩnh, Cấp phát Tĩnh vs Động)");
            System.out.println(" 8. Bài 8: Tính khoảng cách giữa 2 Ngày (Số ngày, Năm/Tháng/Ngày)");
            System.out.println(" 9. Bài 9: Hiển thị và tính khoảng cách Thời Gian (hh:mm:ss)");
            System.out.println(" 0. Thoát chương trình");
            System.out.println("=======================================================");
            System.out.print("Nhập lựa chọn của bạn (0-9): ");

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
                    System.out.println("Đã thoát chương trình. Cảm ơn!");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn từ 0 đến 9.");
            }
        }
        sc.close();
    }

    // =========================================================================
    // BÀI 1: QUẢN LÝ MẢNG PHÂN SỐ
    // =========================================================================
    static class PhanSo {
        int tu;
        int mau;

        public PhanSo(int tu, int mau) {
            this.tu = tu;
            this.mau = (mau == 0) ? 1 : mau;
            rutGon();
        }

        private int ucln(int a, int b) {
            a = Math.abs(a);
            b = Math.abs(b);
            while (b != 0) {
                int temp = b;
                b = a % b;
                a = temp;
            }
            return a == 0 ? 1 : a;
        }

        public void rutGon() {
            int u = ucln(tu, mau);
            tu /= u;
            mau /= u;
            if (mau < 0) {
                tu = -tu;
                mau = -mau;
            }
        }

        public PhanSo cong(PhanSo ps) {
            return new PhanSo(this.tu * ps.mau + ps.tu * this.mau, this.mau * ps.mau);
        }

        public PhanSo tru(PhanSo ps) {
            return new PhanSo(this.tu * ps.mau - ps.tu * this.mau, this.mau * ps.mau);
        }

        public PhanSo nhan(PhanSo ps) {
            return new PhanSo(this.tu * ps.tu, this.mau * ps.mau);
        }

        public PhanSo chia(PhanSo ps) {
            return new PhanSo(this.tu * ps.mau, this.mau * ps.tu);
        }

        public double giaTri() {
            return (double) tu / mau;
        }

        public void xuat() {
            if (mau == 1) System.out.print(tu);
            else System.out.print(tu + "/" + mau);
        }
    }

    private static void chayBai1(Scanner sc) {
        System.out.println("\n--- BÀI 1: QUẢN LÝ MẢNG PHÂN SỐ ---");
        System.out.print("Nhap so luong phan so n: ");
        int n = sc.nextInt();
        PhanSo[] A = new PhanSo[n];
        for (int i = 0; i < n; i++) {
            System.out.println("Nhap phan so thu " + (i + 1) + ":");
            System.out.print("  Nhap tu so: ");
            int tu = sc.nextInt();
            int mau = 0;
            do {
                System.out.print("  Nhap mau so (khac 0): ");
                mau = sc.nextInt();
            } while (mau == 0);
            A[i] = new PhanSo(tu, mau);
        }

        System.out.print("\nDanh sach phan so: ");
        for (PhanSo p : A) { p.xuat(); System.out.print("  "); }
        System.out.println();

        PhanSo max = A[0], min = A[0];
        for (int i = 1; i < n; i++) {
            if (A[i].giaTri() > max.giaTri()) max = A[i];
            if (A[i].giaTri() < min.giaTri()) min = A[i];
        }

        System.out.print("Phan so Max: "); max.xuat(); System.out.println();
        System.out.print("Phan so Min: "); min.xuat(); System.out.println();

        // Sap xep tang dan
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (A[i].giaTri() > A[j].giaTri()) {
                    PhanSo temp = A[i]; A[i] = A[j]; A[j] = temp;
                }
            }
        }
        System.out.print("Sau khi sap xep tang dan: ");
        for (PhanSo p : A) { p.xuat(); System.out.print("  "); }
        System.out.println();

        System.out.print("Tong (Max + Min) = "); max.cong(min).xuat(); System.out.println();
        System.out.print("Hieu (Max - Min) = "); max.tru(min).xuat(); System.out.println();
        System.out.print("Tich (Max * Min) = "); max.nhan(min).xuat(); System.out.println();
        System.out.print("Thuong (Max / Min) = "); max.chia(min).xuat(); System.out.println();
    }

    // =========================================================================
    // BÀI 2: QUẢN LÝ BỘ PHIM
    // =========================================================================
    static class Phim {
        String tenPhim;
        double doanhThu;
        int theLoai; // 0-Hinh su, 1-Tinh cam, 2-Hai
        int namSanXuat;
        boolean isPhimVietNam;

        public void nhap(Scanner sc) {
            sc.nextLine(); // clear buffer
            System.out.print("Nhap ten phim: "); tenPhim = sc.nextLine();
            System.out.print("Nhap doanh thu: "); doanhThu = sc.nextDouble();
            System.out.print("Nhap the loai (0-Hinh su, 1-Tinh cam, 2-Hai): "); theLoai = sc.nextInt();
            System.out.print("Nhap nam san xuat: "); namSanXuat = sc.nextInt();
            System.out.print("Phim Viet Nam san xuat? (true/false): "); isPhimVietNam = sc.nextBoolean();
        }

        public void xuat() {
            String tl = (theLoai == 0) ? "Hinh su" : (theLoai == 1) ? "Tinh cam" : "Hai";
            String xx = isPhimVietNam ? "Viet Nam" : "Nuoc ngoai";
            System.out.printf("%-20s | Doanh thu: %-10.2f | The loai: %-10s | Nam: %-4d | Xuat xu: %s\n",
                    tenPhim, doanhThu, tl, namSanXuat, xx);
        }
    }

    private static void chayBai2(Scanner sc) {
        System.out.println("\n--- BÀI 2: QUẢN LÝ BỘ PHIM ---");
        System.out.print("Nhap so luong phim n: ");
        int n = sc.nextInt();
        Phim[] ds = new Phim[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\nNhap phim thu " + (i + 1) + ":");
            ds[i] = new Phim();
            ds[i].nhap(sc);
        }

        System.out.println("\n================ DANH SACH PHIM ================");
        for (Phim p : ds) p.xuat();

        System.out.print("\nNhap the loai can in (0-Hinh su, 1-Tinh cam, 2-Hai): ");
        int tl = sc.nextInt();
        System.out.println("--- Phim thuộc the loai " + tl + " ---");
        for (Phim p : ds) { if (p.theLoai == tl) p.xuat(); }

        double tongVN = 0;
        for (Phim p : ds) { if (p.isPhimVietNam) tongVN += p.doanhThu; }
        System.out.printf("\nTong doanh thu phim Viet Nam = %.2f\n", tongVN);

        System.out.print("\nNhap fromYear va toYear: ");
        int fy = sc.nextInt(), ty = sc.nextInt();
        double sum = 0; int cnt = 0;
        for (Phim p : ds) {
            if (p.namSanXuat >= fy && p.namSanXuat <= ty) { sum += p.doanhThu; cnt++; }
        }
        System.out.printf("Doanh thu trung binh tu %d den %d = %.2f\n", fy, ty, (cnt == 0 ? 0 : sum / cnt));

        System.out.print("\nNhap LowerBound va UpperBound doanh thu: ");
        double lb = sc.nextDouble(), ub = sc.nextDouble();
        int countNgoai = 0;
        for (Phim p : ds) { if (p.doanhThu < lb || p.doanhThu > ub) countNgoai++; }
        System.out.printf("So phim co doanh thu < %.2f hoac > %.2f = %d\n", lb, ub, countNgoai);
    }

    // =========================================================================
    // BÀI 3: QUẢN LÝ MÁY TÍNH
    // =========================================================================
    static class MayTinh {
        String model;
        String hangSX;
        int baoHanh; // 1:3th, 2:6th, 3:12th, 4:24th, 5:36th
        float giaTien;
        boolean isSanXuatVN;

        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Nhap Model (toi da 10 ky tu): "); model = sc.nextLine();
            System.out.print("Nhap Hang san xuat (toi da 30 ky tu): "); hangSX = sc.nextLine();

            // Validate baoHanh 1..5
            do {
                System.out.print("Nhap thoi gian bao hanh (1:3th, 2:6th, 3:12th, 4:24th, 5:36th): ");
                baoHanh = sc.nextInt();
                if (baoHanh < 1 || baoHanh > 5) System.out.println("Sai quy dinh! Nhap lai (1..5).");
            } while (baoHanh < 1 || baoHanh > 5);

            System.out.print("Nhap gia tien: "); giaTien = sc.nextFloat();

            // Validate san xuat VN: Y/y hoac N/n
            char c;
            do {
                System.out.print("San xuat tai Viet Nam? (Y/N): ");
                c = sc.next().charAt(0);
            } while (c != 'Y' && c != 'y' && c != 'N' && c != 'n');
            isSanXuatVN = (c == 'Y' || c == 'y');
        }

        public int getSoThangBH() {
            int[] tb = {0, 3, 6, 12, 24, 36};
            return tb[baoHanh];
        }

        public void xuat() {
            String xx = isSanXuatVN ? "Sản xuất tại Việt Nam" : "Sản xuất tại nước ngoài";
            System.out.printf("Model: %-10s | Hang: %-15s | Bao hanh: %-2d thang | Gia: %-10.2f | %s\n",
                    model, hangSX, getSoThangBH(), giaTien, xx);
        }
    }

    private static void chayBai3(Scanner sc) {
        System.out.println("\n--- BÀI 3: QUẢN LÝ MÁY TÍNH ---");
        System.out.print("Nhap so luong may tinh n: ");
        int n = sc.nextInt();
        MayTinh[] ds = new MayTinh[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\nNhap may tinh thu " + (i + 1) + ":");
            ds[i] = new MayTinh();
            ds[i].nhap(sc);
        }

        System.out.println("\n================ DANH SACH MAY TINH ================");
        for (MayTinh m : ds) m.xuat();

        sc.nextLine();
        System.out.print("\nNhap hang san xuat can loc: ");
        String hang = sc.nextLine();
        System.out.println("--- May tinh cua hang " + hang + " ---");
        for (MayTinh m : ds) { if (m.hangSX.equalsIgnoreCase(hang)) m.xuat(); }

        System.out.print("\nNhap fromPrice va toPrice: ");
        float fp = sc.nextFloat(), tp = sc.nextFloat();
        int cntPrice = 0;
        for (MayTinh m : ds) { if (m.giaTien >= fp && m.giaTien <= tp) cntPrice++; }
        System.out.printf("So model co gia tu %.2f den %.2f = %d\n", fp, tp, cntPrice);

        System.out.print("\nNhap lowerPrice va upperPrice: ");
        float lp = sc.nextFloat(), up = sc.nextFloat();
        System.out.println("--- May tinh VN co gia < " + lp + " hoac > " + up + " ---");
        for (MayTinh m : ds) {
            if (m.isSanXuatVN && (m.giaTien < lp || m.giaTien > up)) m.xuat();
        }
    }

    // =========================================================================
    // BÀI 4: QUẢN LÝ LINH KIỆN
    // =========================================================================
    static class LinhKien {
        String tenLinhKien, quyCach; char loai; int donGia1, donGia2;
        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Nhap ten linh kien: "); tenLinhKien = sc.nextLine();
            System.out.print("Nhap quy cach: "); quyCach = sc.nextLine();
            System.out.print("Nhap loai linh kien (A/B): "); loai = Character.toUpperCase(sc.next().charAt(0));
            System.out.print("Don gia loai 1 (tot): "); donGia1 = sc.nextInt();
            System.out.print("Don gia loai 2 (thuong): "); donGia2 = sc.nextInt();
        }
        public void xuat() {
            System.out.printf("Loai: %c | Ten: %-20s | Quy cach: %-15s | Gia L1: %-8d | Gia L2: %-8d\n",
                    loai, tenLinhKien, quyCach, donGia1, donGia2);
        }
    }

    private static void chayBai4(Scanner sc) {
        System.out.println("\n--- BÀI 4: QUẢN LÝ LINH KIỆN MÁY TÍNH ---");
        System.out.print("Nhap so luong linh kien n: ");
        int n = sc.nextInt();
        LinhKien[] ds = new LinhKien[n];
        int countA = 0;
        for (int i = 0; i < n; i++) {
            System.out.println("\nNhap linh kien " + (i + 1) + ":");
            ds[i] = new LinhKien(); ds[i].nhap(sc);
            if (ds[i].loai == 'A') countA++;
        }

        // Sap xep tăng dan theo loai va ten
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (ds[i].loai > ds[j].loai ||
                   (ds[i].loai == ds[j].loai && ds[i].tenLinhKien.compareToIgnoreCase(ds[j].tenLinhKien) > 0)) {
                    LinhKien temp = ds[i]; ds[i] = ds[j]; ds[j] = temp;
                }
            }
        }

        System.out.println("\n--- DANH SÁCH LINH KIỆN SẮP XẾP TĂNG DẦN ---");
        for (LinhKien lk : ds) lk.xuat();

        System.out.println("\nKiem tra du 10 linh kien loai A de lap rap: " + (countA >= 10 ? "DA DU (" + countA + ")" : "CHUA DU (" + countA + "/10)"));
    }

    // =========================================================================
    // BÀI 5: QUẢN LÝ THƯ VIỆN ĐẦU SÁCH
    // =========================================================================
    static class DauSach {
        String maSach, tenSach, tacGia; int soLuong;
        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Ma sach: "); maSach = sc.nextLine();
            System.out.print("Ten sach: "); tenSach = sc.nextLine();
            System.out.print("Tac gia: "); tacGia = sc.nextLine();
            System.out.print("So luong: "); soLuong = sc.nextInt();
        }
        public void xuat() {
            System.out.printf("Ma: %-8s | Ten: %-25s | Tac gia: %-20s | SL: %d\n", maSach, tenSach, tacGia, soLuong);
        }
    }

    private static void chayBai5(Scanner sc) {
        System.out.println("\n--- BÀI 5: QUẢN LÝ THƯ VIỆN ĐẦU SÁCH ---");
        System.out.print("Nhap so dau sach (n <= 100): ");
        int n = sc.nextInt(); if (n > 100) n = 100;
        DauSach[] ds = new DauSach[n];
        int tongSach = 0;
        for (int i = 0; i < n; i++) {
            System.out.println("\nNhap dau sach " + (i + 1) + ":");
            ds[i] = new DauSach(); ds[i].nhap(sc);
            tongSach += ds[i].soLuong;
        }

        sc.nextLine();
        System.out.print("\nNhap ten sach can tim: ");
        String tenTim = sc.nextLine();
        boolean found = false;
        for (DauSach s : ds) {
            if (s.tenSach.equalsIgnoreCase(tenTim)) { s.xuat(); found = true; }
        }
        if (!found) System.out.println("Khong tim thay");

        System.out.println("Tong so sach trong thu vien = " + tongSach);
    }

    // =========================================================================
    // BÀI 6: QUẢN LÝ MẶT HÀNG
    // =========================================================================
    static class MatHang {
        String maHang, tenHang; int soLuong; double donGia; int soLuongTon, baoHanhThang;
        public void nhap(Scanner sc) {
            sc.nextLine();
            System.out.print("Ma hang: "); maHang = sc.nextLine();
            System.out.print("Ten mat hang: "); tenHang = sc.nextLine();
            System.out.print("So luong: "); soLuong = sc.nextInt();
            System.out.print("Don gia: "); donGia = sc.nextDouble();
            System.out.print("So luong ton: "); soLuongTon = sc.nextInt();
            System.out.print("Bao hanh (thang): "); baoHanhThang = sc.nextInt();
        }
        public void xuat() {
            System.out.printf("Ma: %-8s | Ten: %-20s | SL: %-4d | Gia: %-10.2f | Ton: %-4d | BH: %d thang\n",
                    maHang, tenHang, soLuong, donGia, soLuongTon, baoHanhThang);
        }
    }

    private static void chayBai6(Scanner sc) {
        System.out.println("\n--- BÀI 6: QUẢN LÝ MẶT HÀNG ---");
        System.out.print("Nhap so luong mat hang n: ");
        int n = sc.nextInt();
        MatHang[] ds = new MatHang[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\nNhap mat hang thu " + (i + 1) + ":");
            ds[i] = new MatHang(); ds[i].nhap(sc);
        }

        int maxTon = ds[0].soLuongTon, minTon = ds[0].soLuongTon;
        double maxGia = ds[0].donGia;
        for (MatHang m : ds) {
            if (m.soLuongTon > maxTon) maxTon = m.soLuongTon;
            if (m.soLuongTon < minTon) minTon = m.soLuongTon;
            if (m.donGia > maxGia) maxGia = m.donGia;
        }

        System.out.println("\n--- MAT HANG TON NHIEU NHAT ---");
        for (MatHang m : ds) if (m.soLuongTon == maxTon) m.xuat();

        System.out.println("--- MAT HANG TON IT NHAT ---");
        for (MatHang m : ds) if (m.soLuongTon == minTon) m.xuat();

        System.out.println("--- MAT HANG GIA CAO NHAT ---");
        for (MatHang m : ds) if (m.donGia == maxGia) m.xuat();

        System.out.println("--- MAT HANG BAO HANH > 12 THANG ---");
        for (MatHang m : ds) if (m.baoHanhThang > 12) m.xuat();

        // Sap xep tang dan theo ton
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (ds[i].soLuongTon > ds[j].soLuongTon) {
                    MatHang temp = ds[i]; ds[i] = ds[j]; ds[j] = temp;
                }
            }
        }
        System.out.println("\n--- SAU KHI SAP XEP TANG DAN THEO SO LUONG TON ---");
        for (MatHang m : ds) m.xuat();
    }

    // =========================================================================
    // BÀI 7: QUẢN LÝ HỒ SƠ NHÂN VIÊN
    // =========================================================================
    static class NhanVien {
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
            System.out.printf("Ten: %-20s | Phai: %-4s | Ngay sinh: %-10s | LTL: %.2f\n", hoTen, phai, ngaySinh, getThucLinh());
        }
    }

    private static void chayBai7(Scanner sc) {
        System.out.println("\n--- BÀI 7: QUẢN LÝ HỒ SƠ NHÂN VIÊN ---");
        System.out.print("Nhap so luong nhan vien n: ");
        int n = sc.nextInt();
        NhanVien[] dsTinh = new NhanVien[n];
        ArrayList<NhanVien> dsDong = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.println("\nNhap nhan vien thu " + (i + 1) + ":");
            NhanVien nv = new NhanVien(); nv.nhap(sc);
            dsTinh[i] = nv;
            dsDong.add(nv);
        }

        // Cach 1: Cap phat tinh (Mang NhanVien[])
        System.out.println("\n--- [CACH 1: MANG TINH] DANH SACH GIAM DAN LUONG THUC LINH ---");
        NhanVien[] arrTinh = dsTinh.clone();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (arrTinh[i].getThucLinh() < arrTinh[j].getThucLinh()) {
                    NhanVien temp = arrTinh[i]; arrTinh[i] = arrTinh[j]; arrTinh[j] = temp;
                }
            }
        }
        for (NhanVien nv : arrTinh) nv.xuat();

        // Cach 2: Cap phat dong (ArrayList<NhanVien>)
        System.out.println("\n--- [CACH 2: MANG DONG] DANH SACH GIAM DAN LUONG THUC LINH ---");
        dsDong.sort((a, b) -> Double.compare(b.getThucLinh(), a.getThucLinh()));
        for (NhanVien nv : dsDong) nv.xuat();
    }

    // =========================================================================
    // BÀI 8: TÍNH KHOẢNG CÁCH NGÀY
    // =========================================================================
    static class Ngay {
        int d, m, y;
        public Ngay(int d, int m, int y) { this.d = d; this.m = m; this.y = y; }
        public static boolean isLeap(int y) { return (y % 400 == 0) || (y % 4 == 0 && y % 100 != 0); }
        public static int daysInMonth(int m, int y) {
            if (m == 4 || m == 6 || m == 9 || m == 11) return 30;
            if (m == 2) return isLeap(y) ? 29 : 28;
            return 31;
        }
        public long toTotalDays() {
            long days = d;
            for (int i = 1; i < y; i++) days += isLeap(i) ? 366 : 365;
            for (int i = 1; i < m; i++) days += daysInMonth(i, y);
            return days;
        }
        public static void tinhKhoangCach(Ngay d1, Ngay d2) {
            Ngay s = d1.toTotalDays() <= d2.toTotalDays() ? d1 : d2;
            Ngay e = d1.toTotalDays() <= d2.toTotalDays() ? d2 : d1;

            int dy = e.y - s.y;
            int dm = e.m - s.m;
            int dd = e.d - s.d;

            if (dd < 0) {
                dm--;
                int pm = e.m - 1, py = e.y;
                if (pm == 0) { pm = 12; py--; }
                dd += daysInMonth(pm, py);
            }
            if (dm < 0) { dy--; dm += 12; }

            long totalDays = Math.abs(e.toTotalDays() - s.toTotalDays());
            System.out.printf("in ra ngay %02d/%02d/%d va ngay %02d/%02d/%d cach nhau %d ngay %d thang va %d nam (Tong cong %d ngay)\n",
                    s.d, s.m, s.y, e.d, e.m, e.y, dd, dm, dy, totalDays);
        }
    }

    private static void chayBai8(Scanner sc) {
        System.out.println("\n--- BÀI 8: TÍNH KHOẢNG CÁCH GIỮA 2 NGÀY ---");
        System.out.print("Nhap ngay 1 (ngay thang nam): ");
        Ngay d1 = new Ngay(sc.nextInt(), sc.nextInt(), sc.nextInt());
        System.out.print("Nhap ngay 2 (ngay thang nam): ");
        Ngay d2 = new Ngay(sc.nextInt(), sc.nextInt(), sc.nextInt());

        Ngay.tinhKhoangCach(d1, d2);
    }

    // =========================================================================
    // BÀI 9: TÍNH KHOẢNG CÁCH THỜI GIAN
    // =========================================================================
    static class ThoiGian {
        int g, p, s;
        public ThoiGian(int g, int p, int s) { this.g = g; this.p = p; this.s = s; }
        public long toSec() { return (long) g * 3600 + p * 60 + s; }
        public void xuat() { System.out.printf("%02d:%02d:%02d", g, p, s); }
    }

    private static void chayBai9(Scanner sc) {
        System.out.println("\n--- BÀI 9: KHOẢNG CÁCH THỜI GIAN ---");
        System.out.print("Nhap thoi gian 1 (gio phut giay): ");
        ThoiGian t1 = new ThoiGian(sc.nextInt(), sc.nextInt(), sc.nextInt());
        System.out.print("Nhap thoi gian 2 (gio phut giay): ");
        ThoiGian t2 = new ThoiGian(sc.nextInt(), sc.nextInt(), sc.nextInt());

        System.out.print("Moc 1: "); t1.xuat(); System.out.println();
        System.out.print("Moc 2: "); t2.xuat(); System.out.println();

        long diffSec = Math.abs(t1.toSec() - t2.toSec());
        long h = diffSec / 3600;
        long m = (diffSec % 3600) / 60;
        long s = diffSec % 60;

        System.out.printf("Khoang cach giua 2 moc thoi gian: %02d:%02d:%02d (%d gio, %d phut, %d giay - Tong cong %d giay)\n",
                h, m, s, h, m, s, diffSec);
    }
}
