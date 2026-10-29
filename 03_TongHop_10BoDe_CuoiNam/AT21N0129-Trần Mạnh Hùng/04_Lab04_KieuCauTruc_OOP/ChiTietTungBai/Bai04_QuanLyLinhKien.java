import java.util.Scanner;

class LinhKien {
    String tenLinhKien;
    String quyCach;
    char loai; // 'A' hoac 'B'
    int donGiaLoai1; // tot
    int donGiaLoai2; // thuong

    public void nhap(Scanner sc) {
        sc.nextLine();
        System.out.print("Nhap ten linh kien: ");
        this.tenLinhKien = sc.nextLine();
        System.out.print("Nhap quy cach: ");
        this.quyCach = sc.nextLine();
        System.out.print("Nhap loai linh kien (A/B): ");
        this.loai = Character.toUpperCase(sc.next().charAt(0));
        System.out.print("Nhap don gia loai 1 (chat luong tot): ");
        this.donGiaLoai1 = sc.nextInt();
        System.out.print("Nhap don gia loai 2 (chat luong thuong): ");
        this.donGiaLoai2 = sc.nextInt();
    }

    public void xuat() {
        System.out.printf("Loai: %c | Ten: %-20s | Quy cach: %-15s | Gia L1: %-8d | Gia L2: %-8d\n",
                loai, tenLinhKien, quyCach, donGiaLoai1, donGiaLoai2);
    }
}

public class Bai04_QuanLyLinhKien {

    public static LinhKien[] nhapDanhSach(Scanner sc) {
        System.out.print("Nhap so luong linh kien co tai cua hang n: ");
        int n = sc.nextInt();
        LinhKien[] ds = new LinhKien[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Nhap linh kien thu " + (i + 1) + " ---");
            ds[i] = new LinhKien();
            ds[i].nhap(sc);
        }
        return ds;
    }

    public static void sapXepTangDan(LinhKien[] ds) {
        for (int i = 0; i < ds.length - 1; i++) {
            for (int j = i + 1; j < ds.length; j++) {
                if (ds[i].loai > ds[j].loai ||
                   (ds[i].loai == ds[j].loai && ds[i].tenLinhKien.compareToIgnoreCase(ds[j].tenLinhKien) > 0)) {
                    LinhKien temp = ds[i];
                    ds[i] = ds[j];
                    ds[j] = temp;
                }
            }
        }
    }

    public static void xuatDanhSach(LinhKien[] ds) {
        System.out.println("\n================ DANH SACH LINH KIEN ================");
        for (int i = 0; i < ds.length; i++) {
            System.out.printf("[%d] ", i + 1);
            ds[i].xuat();
        }
    }

    public static boolean kiemTraDuLinhKienLoaiA(LinhKien[] ds) {
        int countA = 0;
        for (LinhKien lk : ds) {
            if (lk.loai == 'A') countA++;
        }
        return countA >= 10;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CHUONG TRINH QUAN LY LINH KIEN CUA HANG ===");

        LinhKien[] ds = nhapDanhSach(sc);

        System.out.println("\n--- DANH SACH SAU KHI SAP XEP TANG DAN THEO LOAI VA TEN ---");
        sapXepTangDan(ds);
        xuatDanhSach(ds);

        System.out.println("\n--- KIEM TRA LAP RAP MAY VI TINH ---");
        if (kiemTraDuLinhKienLoaiA(ds)) {
            System.out.println("=> DA DU TOI THIEU 10 LINH KIEN LOAI A DE LAP RAP MAY.");
        } else {
            System.out.println("=> CHUA DU 10 LINH KIEN LOAI A CAN THIET DE LAP RAP MAY!");
        }

        sc.close();
    }
}
