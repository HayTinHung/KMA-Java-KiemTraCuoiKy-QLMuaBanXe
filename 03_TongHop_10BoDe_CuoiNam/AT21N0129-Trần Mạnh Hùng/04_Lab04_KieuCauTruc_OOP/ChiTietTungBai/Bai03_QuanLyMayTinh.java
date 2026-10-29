import java.util.Scanner;

class MayTinh {
    String model;
    String hangSX;
    int baoHanh; // 1: 3 thang, 2: 6 thang, 3: 12 thang, 4: 24 thang, 5: 36 thang
    float giaTien;
    boolean isSanXuatVietNam;

    public void nhap(Scanner sc) {
        sc.nextLine(); // clear buffer
        System.out.print("Nhap Model (toi da 10 ky tu): ");
        this.model = sc.nextLine();
        System.out.print("Nhap Hang san xuat (toi da 30 ky tu): ");
        this.hangSX = sc.nextLine();

        // Validate baoHanh tu 1..5
        do {
            System.out.print("Nhap thoi gian bao hanh (1:3th, 2:6th, 3:12th, 4:24th, 5:36th): ");
            this.baoHanh = sc.nextInt();
            if (this.baoHanh < 1 || this.baoHanh > 5) {
                System.out.println("Gia tri khong hop le! Vui long nhap lai (1..5).");
            }
        } while (this.baoHanh < 1 || this.baoHanh > 5);

        System.out.print("Nhap gia tien (USD/VND): ");
        this.giaTien = sc.nextFloat();

        // Validate san xuat VN: Y/y hoac N/n
        char c;
        do {
            System.out.print("San xuat tai Viet Nam? (Y/N): ");
            c = sc.next().charAt(0);
            if (c != 'Y' && c != 'y' && c != 'N' && c != 'n') {
                System.out.println("Vui long chi nhap Y, y, N, hoac n!");
            }
        } while (c != 'Y' && c != 'y' && c != 'N' && c != 'n');
        this.isSanXuatVietNam = (c == 'Y' || c == 'y');
    }

    public int getSoThangBaoHanh() {
        switch (baoHanh) {
            case 1: return 3;
            case 2: return 6;
            case 3: return 12;
            case 4: return 24;
            case 5: return 36;
            default: return 0;
        }
    }

    public void xuat() {
        String xx = isSanXuatVietNam ? "San xuat tai Viet Nam" : "San xuat tai nuoc ngoai";
        System.out.printf("Model: %-10s | Hang: %-15s | Bao hanh: %-2d thang | Gia: %-10.2f | %s\n",
                model, hangSX, getSoThangBaoHanh(), giaTien, xx);
    }
}

public class Bai03_QuanLyMayTinh {

    public static MayTinh[] nhapDanhSach(Scanner sc) {
        System.out.print("Nhap so luong may tinh n: ");
        int n = sc.nextInt();
        MayTinh[] ds = new MayTinh[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Nhap may tinh thu " + (i + 1) + " ---");
            ds[i] = new MayTinh();
            ds[i].nhap(sc);
        }
        return ds;
    }

    public static void xuatDanhSach(MayTinh[] ds) {
        System.out.println("\n================ DANH SACH MAY TINH ================");
        for (int i = 0; i < ds.length; i++) {
            System.out.printf("[%d] ", i + 1);
            ds[i].xuat();
        }
    }

    public static void inTheoHang(MayTinh[] ds, String hang) {
        System.out.println("\n--- DANH SACH MAY TINH CUA HANG " + hang + " ---");
        boolean found = false;
        for (MayTinh m : ds) {
            if (m.hangSX.equalsIgnoreCase(hang)) {
                m.xuat();
                found = true;
            }
        }
        if (!found) System.out.println("Khong tim thay may tinh cua hang nay.");
    }

    public static int demTheoKhoangGia(MayTinh[] ds, float fromPrice, float toPrice) {
        int count = 0;
        for (MayTinh m : ds) {
            if (m.giaTien >= fromPrice && m.giaTien <= toPrice) {
                count++;
            }
        }
        return count;
    }

    public static void inMayVNTrongKhoangGia(MayTinh[] ds, float lowerPrice, float upperPrice) {
        System.out.printf("\n--- MAY TINH SX TAI VN CO GIA < %.2f HOAC > %.2f ---\n", lowerPrice, upperPrice);
        boolean found = false;
        for (MayTinh m : ds) {
            if (m.isSanXuatVietNam && (m.giaTien < lowerPrice || m.giaTien > upperPrice)) {
                m.xuat();
                found = true;
            }
        }
        if (!found) System.out.println("Khong co may tinh nao thoa man.");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CHUONG TRINH QUAN LY MAY TINH ===");

        MayTinh[] ds = nhapDanhSach(sc);
        xuatDanhSach(ds);

        sc.nextLine(); // clear buffer
        System.out.print("\nNhap hang san xuat can loc: ");
        String hang = sc.nextLine();
        inTheoHang(ds, hang);

        System.out.print("\nNhap fromPrice va toPrice: ");
        float fp = sc.nextFloat();
        float tp = sc.nextFloat();
        System.out.printf("So luong model may tinh co gia tu %.2f den %.2f = %d\n", fp, tp, demTheoKhoangGia(ds, fp, tp));

        System.out.print("\nNhap lowerPrice va upperPrice: ");
        float lp = sc.nextFloat();
        float up = sc.nextFloat();
        inMayVNTrongKhoangGia(ds, lp, up);

        sc.close();
    }
}
