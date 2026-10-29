import java.util.Scanner;

class MatHang {
    String maHang;
    String tenHang;
    int soLuong;
    double donGia;
    int soLuongTon;
    int baoHanhThang;

    public void nhap(Scanner sc) {
        sc.nextLine();
        System.out.print("Nhap Ma Hang: ");
        this.maHang = sc.nextLine();
        System.out.print("Nhap Ten Mat Hang: ");
        this.tenHang = sc.nextLine();
        System.out.print("Nhap So Luong: ");
        this.soLuong = sc.nextInt();
        System.out.print("Nhap Don Gia: ");
        this.donGia = sc.nextDouble();
        System.out.print("Nhap So Luong Ton: ");
        this.soLuongTon = sc.nextInt();
        System.out.print("Nhap Thoi Gian Bao Hanh (thang): ");
        this.baoHanhThang = sc.nextInt();
    }

    public void xuat() {
        System.out.printf("Ma: %-8s | Ten: %-20s | SL: %-4d | Gia: %-10.2f | Ton: %-4d | BH: %d thang\n",
                maHang, tenHang, soLuong, donGia, soLuongTon, baoHanhThang);
    }
}

public class Bai06_QuanLyMatHang {

    public static MatHang[] nhapDanhSach(Scanner sc) {
        System.out.print("Nhap so luong mat hang n: ");
        int n = sc.nextInt();
        MatHang[] ds = new MatHang[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Nhap mat hang thu " + (i + 1) + " ---");
            ds[i] = new MatHang();
            ds[i].nhap(sc);
        }
        return ds;
    }

    public static void timTonNhieuNhat(MatHang[] ds) {
        System.out.println("\n--- MAT HANG SO LUONG TON NHIEU NHAT ---");
        int maxTon = ds[0].soLuongTon;
        for (MatHang m : ds) {
            if (m.soLuongTon > maxTon) maxTon = m.soLuongTon;
        }
        for (MatHang m : ds) {
            if (m.soLuongTon == maxTon) m.xuat();
        }
    }

    public static void timTonItNhat(MatHang[] ds) {
        System.out.println("\n--- MAT HANG SO LUONG TON IT NHAT ---");
        int minTon = ds[0].soLuongTon;
        for (MatHang m : ds) {
            if (m.soLuongTon < minTon) minTon = m.soLuongTon;
        }
        for (MatHang m : ds) {
            if (m.soLuongTon == minTon) m.xuat();
        }
    }

    public static void timGiaCaoNhat(MatHang[] ds) {
        System.out.println("\n--- MAT HANG CO GIA TIEN CAO NHAT ---");
        double maxGia = ds[0].donGia;
        for (MatHang m : ds) {
            if (m.donGia > maxGia) maxGia = m.donGia;
        }
        for (MatHang m : ds) {
            if (m.donGia == maxGia) m.xuat();
        }
    }

    public static void inBaoHanhLonHon12Thang(MatHang[] ds) {
        System.out.println("\n--- MAT HANG BAO HANH > 12 THANG ---");
        boolean found = false;
        for (MatHang m : ds) {
            if (m.baoHanhThang > 12) {
                m.xuat();
                found = true;
            }
        }
        if (!found) System.out.println("Khong co mat hang nào bao hanh > 12 thang.");
    }

    public static void sapXepTangDanTheoTon(MatHang[] ds) {
        for (int i = 0; i < ds.length - 1; i++) {
            for (int j = i + 1; j < ds.length; j++) {
                if (ds[i].soLuongTon > ds[j].soLuongTon) {
                    MatHang temp = ds[i];
                    ds[i] = ds[j];
                    ds[j] = temp;
                }
            }
        }
    }

    public static void xuatDanhSach(MatHang[] ds) {
        for (int i = 0; i < ds.length; i++) {
            System.out.printf("[%d] ", i + 1);
            ds[i].xuat();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CHUONG TRINH QUAN LY MAT HANG ===");

        MatHang[] ds = nhapDanhSach(sc);

        timTonNhieuNhat(ds);
        timTonItNhat(ds);
        timGiaCaoNhat(ds);
        inBaoHanhLonHon12Thang(ds);

        System.out.println("\n--- DANH SACH SAP XEP TANG DAN THEO SO LUONG TON ---");
        sapXepTangDanTheoTon(ds);
        xuatDanhSach(ds);

        sc.close();
    }
}
