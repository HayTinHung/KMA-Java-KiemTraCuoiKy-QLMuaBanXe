import java.util.Scanner;

class Phim {
    String tenPhim;
    double doanhThu;
    int theLoai; // 0-Hinh su, 1-Tinh cam, 2-Hai
    int namSanXuat;
    boolean isPhimVietNam;

    public void nhap(Scanner sc) {
        sc.nextLine(); // clear buffer
        System.out.print("Nhap ten phim: ");
        this.tenPhim = sc.nextLine();
        System.out.print("Nhap doanh thu (trieu VND): ");
        this.doanhThu = sc.nextDouble();
        System.out.print("Nhap the loai (0-Hinh su, 1-Tinh cam, 2-Hai): ");
        this.theLoai = sc.nextInt();
        System.out.print("Nhap nam san xuat: ");
        this.namSanXuat = sc.nextInt();
        System.out.print("Phim Viet Nam san xuat (true/false): ");
        this.isPhimVietNam = sc.nextBoolean();
    }

    public void xuat() {
        String tlStr = (theLoai == 0) ? "Hinh su" : (theLoai == 1) ? "Tinh cam" : "Hai";
        String xuatXu = isPhimVietNam ? "Viet Nam" : "Nuoc ngoai";
        System.out.printf("%-25s | Doanh thu: %-10.2f | The loai: %-10s | Nam: %-4d | Xuat xu: %s\n",
                tenPhim, doanhThu, tlStr, namSanXuat, xuatXu);
    }
}

public class Bai02_QuanLyPhim {

    public static Phim[] nhapDanhSachPhim(Scanner sc) {
        System.out.print("Nhap so luong phim n: ");
        int n = sc.nextInt();
        Phim[] ds = new Phim[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Nhap phim thu " + (i + 1) + " ---");
            ds[i] = new Phim();
            ds[i].nhap(sc);
        }
        return ds;
    }

    public static void xuatDanhSachPhim(Phim[] ds) {
        System.out.println("\n================ DANH SACH BOPHIM ================");
        for (int i = 0; i < ds.length; i++) {
            System.out.printf("[%d] ", i + 1);
            ds[i].xuat();
        }
    }

    public static void xuatTheoTheLoai(Phim[] ds, int theLoai) {
        System.out.println("\n--- DANH SACH PHIM THEO THE LOAI " + theLoai + " ---");
        boolean count = false;
        for (Phim p : ds) {
            if (p.theLoai == theLoai) {
                p.xuat();
                count = true;
            }
        }
        if (!count) System.out.println("Khong co phim nao thuộc the loai nay.");
    }

    public static double tongDoanhThuPhimVN(Phim[] ds) {
        double tong = 0;
        for (Phim p : ds) {
            if (p.isPhimVietNam) {
                tong += p.doanhThu;
            }
        }
        return tong;
    }

    public static double doanhThuTrungBinhNam(Phim[] ds, int fromYear, int toYear) {
        double tong = 0;
        int count = 0;
        for (Phim p : ds) {
            if (p.namSanXuat >= fromYear && p.namSanXuat <= toYear) {
                tong += p.doanhThu;
                count++;
            }
        }
        return count == 0 ? 0 : tong / count;
    }

    public static int demPhimDoanhThuNgoaiKhoang(Phim[] ds, double lower, double upper) {
        int count = 0;
        for (Phim p : ds) {
            if (p.doanhThu < lower || p.doanhThu > upper) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CHUONG TRINH QUAN LY PHIM ===");

        Phim[] ds = nhapDanhSachPhim(sc);
        xuatDanhSachPhim(ds);

        System.out.print("\nNhap the loai can loc (0-Hinh su, 1-Tinh cam, 2-Hai): ");
        int tl = sc.nextInt();
        xuatTheoTheLoai(ds, tl);

        System.out.printf("\nTong doanh thu phim Viet Nam = %.2f\n", tongDoanhThuPhimVN(ds));

        System.out.print("\nNhap fromYear va toYear: ");
        int fy = sc.nextInt();
        int ty = sc.nextInt();
        System.out.printf("Doanh thu trung binh phim tu %d den %d = %.2f\n", fy, ty, doanhThuTrungBinhNam(ds, fy, ty));

        System.out.print("\nNhap LowerBound va UpperBound doanh thu: ");
        double lb = sc.nextDouble();
        double ub = sc.nextDouble();
        System.out.printf("So luong phim co doanh thu < %.2f hoa c > %.2f = %d\n", lb, ub, demPhimDoanhThuNgoaiKhoang(ds, lb, ub));

        sc.close();
    }
}
