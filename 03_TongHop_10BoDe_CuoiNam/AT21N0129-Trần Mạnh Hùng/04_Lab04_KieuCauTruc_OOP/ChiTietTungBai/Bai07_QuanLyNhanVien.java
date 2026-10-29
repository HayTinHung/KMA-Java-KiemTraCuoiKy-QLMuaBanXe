import java.util.ArrayList;
import java.util.Scanner;

class NhanVien {
    String hoTen;
    String phai;
    String ngaySinh;
    String diaChi;
    double luongCoBan;
    double thuong;
    double bhxh;
    double phat;

    public void nhap(Scanner sc) {
        sc.nextLine();
        System.out.print("Nhap Ho va Ten: ");
        this.hoTen = sc.nextLine();
        System.out.print("Nhap Phai (Nam/Nu): ");
        this.phai = sc.nextLine();
        System.out.print("Nhap Ngay sinh (dd/mm/yyyy): ");
        this.ngaySinh = sc.nextLine();
        System.out.print("Nhap Dia chi: ");
        this.diaChi = sc.nextLine();
        System.out.print("Nhap Luong co ban: ");
        this.luongCoBan = sc.nextDouble();
        System.out.print("Nhap Thuong: ");
        this.thuong = sc.nextDouble();
        System.out.print("Nhap BHXH: ");
        this.bhxh = sc.nextDouble();
        System.out.print("Nhap Phat: ");
        this.phat = sc.nextDouble();
    }

    public double getLuongThucLinh() {
        return luongCoBan + thuong - bhxh - phat;
    }

    public void xuat() {
        System.out.printf("Ten: %-20s | Phai: %-4s | Ngay sinh: %-10s | LTL: %.2f (CB:%.0f, Th:%.0f, BH:%.0f, Ph:%.0f)\n",
                hoTen, phai, ngaySinh, getLuongThucLinh(), luongCoBan, thuong, bhxh, phat);
    }
}

public class Bai07_QuanLyNhanVien {

    // Cach 1: Cap phat vung nho tinh (dung Mang co dinh NhanVien[])
    public static void xuatGiamDanTinh(NhanVien[] ds) {
        System.out.println("\n--- [CACH 1: MANG TINH] DANH SACH NHAN VIEN GIAM DAN THEO LUONG THUC LINH ---");
        // Clone array de sap xep
        NhanVien[] arr = ds.clone();
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i].getLuongThucLinh() < arr[j].getLuongThucLinh()) {
                    NhanVien temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        for (NhanVien nv : arr) {
            nv.xuat();
        }
    }

    // Cach 2: Cap phat vung nho dong (dung ArrayList<NhanVien>)
    public static void xuatGiamDanDong(ArrayList<NhanVien> list) {
        System.out.println("\n--- [CACH 2: MANG DONG] DANH SACH NHAN VIEN GIAM DAN THEO LUONG THUC LINH ---");
        ArrayList<NhanVien> dsDong = new ArrayList<>(list);
        dsDong.sort((a, b) -> Double.compare(b.getLuongThucLinh(), a.getLuongThucLinh()));
        for (NhanVien nv : dsDong) {
            nv.xuat();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CHUONG TRINH QUAN LY HO SO NHAN VIEN ===");
        System.out.print("Nhap so luong nhan vien n: ");
        int n = sc.nextInt();

        NhanVien[] dsTinh = new NhanVien[n];
        ArrayList<NhanVien> dsDong = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Nhap ho so nhan vien thu " + (i + 1) + " ---");
            NhanVien nv = new NhanVien();
            nv.nhap(sc);
            dsTinh[i] = nv;
            dsDong.add(nv);
        }

        xuatGiamDanTinh(dsTinh);
        xuatGiamDanDong(dsDong);

        sc.close();
    }
}
