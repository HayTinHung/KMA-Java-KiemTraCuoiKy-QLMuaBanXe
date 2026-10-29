import java.util.Scanner;

class PhanSo {
    int tu;
    int mau;

    public PhanSo() {
        this.tu = 0;
        this.mau = 1;
    }

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
        int tuMoi = this.tu * ps.mau + ps.tu * this.mau;
        int mauMoi = this.mau * ps.mau;
        return new PhanSo(tuMoi, mauMoi);
    }

    public PhanSo tru(PhanSo ps) {
        int tuMoi = this.tu * ps.mau - ps.tu * this.mau;
        int mauMoi = this.mau * ps.mau;
        return new PhanSo(tuMoi, mauMoi);
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

public class Bai01_PhanSo {

    public static PhanSo nhap1PS(Scanner sc) {
        System.out.print("Nhap tu so: ");
        int tu = sc.nextInt();
        int mau = 0;
        do {
            System.out.print("Nhap mau so (khac 0): ");
            mau = sc.nextInt();
        } while (mau == 0);
        return new PhanSo(tu, mau);
    }

    // a. Nhap mang phan so
    public static void nhapMangPS(PhanSo[] A, int n, Scanner sc) {
        for (int i = 0; i < n; i++) {
            System.out.println("Nhap phan so thu " + (i + 1) + ":");
            A[i] = nhap1PS(sc);
        }
    }

    // b. Xuat mang phan so
    public static void xuatMangPS(PhanSo[] A, int n) {
        System.out.print("Danh sach phan so: ");
        for (int i = 0; i < n; i++) {
            A[i].xuat();
            System.out.print("  ");
        }
        System.out.println();
    }

    // c. Tim phan so lon nhat, nho nhat
    public static PhanSo timPSMax(PhanSo[] A, int n) {
        PhanSo max = A[0];
        for (int i = 1; i < n; i++) {
            if (A[i].giaTri() > max.giaTri()) {
                max = A[i];
            }
        }
        return max;
    }

    public static PhanSo timPSMin(PhanSo[] A, int n) {
        PhanSo min = A[0];
        for (int i = 1; i < n; i++) {
            if (A[i].giaTri() < min.giaTri()) {
                min = A[i];
            }
        }
        return min;
    }

    // d. Sap xep mang phan so tang dan
    public static void sortMangPS(PhanSo[] A, int n) {
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (A[i].giaTri() > A[j].giaTri()) {
                    PhanSo temp = A[i];
                    A[i] = A[j];
                    A[j] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== QUAN LY MANG PHAN SO ===");
        System.out.print("Nhap so luong phan so n: ");
        int n = sc.nextInt();
        PhanSo[] A = new PhanSo[n];

        nhapMangPS(A, n, sc);
        xuatMangPS(A, n);

        PhanSo max = timPSMax(A, n);
        PhanSo min = timPSMin(A, n);
        System.out.print("Phan so lon nhat (Max): "); max.xuat(); System.out.println();
        System.out.print("Phan so nho nhat (Min): "); min.xuat(); System.out.println();

        System.out.println("\n--- SAP XEP TANG DAN ---");
        sortMangPS(A, n);
        xuatMangPS(A, n);

        System.out.println("\n--- PHEP TINH GIUA MAX VA MIN ---");
        System.out.print("Tong (Max + Min) = "); max.cong(min).xuat(); System.out.println();
        System.out.print("Hieu (Max - Min) = "); max.tru(min).xuat(); System.out.println();
        System.out.print("Tich (Max * Min) = "); max.nhan(min).xuat(); System.out.println();
        System.out.print("Thuong (Max / Min) = "); max.chia(min).xuat(); System.out.println();

        sc.close();
    }
}
