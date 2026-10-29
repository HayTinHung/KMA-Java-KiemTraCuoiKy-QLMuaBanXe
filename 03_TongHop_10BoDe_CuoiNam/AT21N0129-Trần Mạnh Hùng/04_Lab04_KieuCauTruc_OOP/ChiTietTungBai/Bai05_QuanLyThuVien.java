import java.util.Scanner;

class DauSach {
    String maSach;
    String tenSach;
    String tacGia;
    int soLuong;

    public void nhap(Scanner sc) {
        sc.nextLine();
        System.out.print("Nhap Ma Sach: ");
        this.maSach = sc.nextLine();
        System.out.print("Nhap Ten Sach: ");
        this.tenSach = sc.nextLine();
        System.out.print("Nhap Tac Gia: ");
        this.tacGia = sc.nextLine();
        System.out.print("Nhap So Luong cuon: ");
        this.soLuong = sc.nextInt();
    }

    public void xuat() {
        System.out.printf("Ma: %-8s | Ten: %-25s | Tac gia: %-20s | SL: %d\n",
                maSach, tenSach, tacGia, soLuong);
    }
}

public class Bai05_QuanLyThuVien {

    public static DauSach[] nhapDanhSach(Scanner sc) {
        System.out.print("Nhap so luong dau sach (toi da 100): ");
        int n = sc.nextInt();
        if (n > 100) n = 100;
        DauSach[] ds = new DauSach[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Nhap dau sach thu " + (i + 1) + " ---");
            ds[i] = new DauSach();
            ds[i].nhap(sc);
        }
        return ds;
    }

    public static void timKiemTenSach(DauSach[] ds, String ten) {
        System.out.println("\n--- KET QUA TIM KIEM SACH: \"" + ten + "\" ---");
        boolean found = false;
        for (DauSach s : ds) {
            if (s.tenSach.equalsIgnoreCase(ten)) {
                s.xuat();
                found = true;
            }
        }
        if (!found) {
            System.out.println("Khong tim thay");
        }
    }

    public static int tinhTongSoSach(DauSach[] ds) {
        int tong = 0;
        for (DauSach s : ds) {
            tong += s.soLuong;
        }
        return tong;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CHUONG TRINH QUAN LY THU VIEN ===");

        DauSach[] ds = nhapDanhSach(sc);

        sc.nextLine(); // clear buffer
        System.out.print("\nNhap ten sach can tim kiem: ");
        String tenTim = sc.nextLine();
        timKiemTenSach(ds, tenTim);

        System.out.println("\nTong so sach co trong thu vien: " + tinhTongSoSach(ds));

        sc.close();
    }
}
