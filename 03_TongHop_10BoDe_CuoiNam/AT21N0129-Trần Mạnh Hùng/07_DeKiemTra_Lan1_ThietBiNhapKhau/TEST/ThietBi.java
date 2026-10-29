package TEST;

import java.text.DecimalFormat;

/**
 * Dữ liệu của một thiết bị nhập khẩu trong đề 1.
 *
 * Tỷ lệ được giữ trong lớp dữ liệu để phần giao diện chỉ lo nhập liệu
 * và hiển thị, còn công thức được gom ở một chỗ.
 */
public class ThietBi {
    public static final double TY_LE_LAO_DONG = 1.10;
    public static final double TY_LE_SAN_XUAT = 1.30;
    public static final double TY_LE_DAY_CAP = 0.10;
    public static final double TY_LE_BANH_XE = 0.05;
    public static final double TY_LE_THAY_THE = 0.08;

    private String maTB;
    private String tenTB;
    private long giaNhap;
    private int soLuong;
    private String loaiTB;
    private boolean dayCap;
    private boolean banhXe;
    private boolean thayThe;

    public ThietBi() {
        loaiTB = "Lao Động";
    }

    public ThietBi(String maTB, String tenTB, long giaNhap, int soLuong,
                   String loaiTB, boolean dayCap, boolean banhXe, boolean thayThe) {
        this.maTB = maTB;
        this.tenTB = tenTB;
        this.giaNhap = giaNhap;
        this.soLuong = soLuong;
        this.loaiTB = loaiTB;
        this.dayCap = dayCap;
        this.banhXe = banhXe;
        this.thayThe = thayThe;
    }

    public String getMaTB() { return maTB; }
    public String getTenTB() { return tenTB; }
    public long getGiaNhap() { return giaNhap; }
    public int getSoLuong() { return soLuong; }
    public String getLoaiTB() { return loaiTB; }
    public boolean isDayCap() { return dayCap; }
    public boolean isBanhXe() { return banhXe; }
    public boolean isThayThe() { return thayThe; }

    public void setMaTB(String maTB) { this.maTB = maTB; }
    public void setTenTB(String tenTB) { this.tenTB = tenTB; }
    public void setGiaNhap(long giaNhap) { this.giaNhap = giaNhap; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void setLoaiTB(String loaiTB) { this.loaiTB = loaiTB; }
    public void setDayCap(boolean dayCap) { this.dayCap = dayCap; }
    public void setBanhXe(boolean banhXe) { this.banhXe = banhXe; }
    public void setThayThe(boolean thayThe) { this.thayThe = thayThe; }

    public double getTyLeLoaiTB() {
        return "Sản Xuất".equalsIgnoreCase(loaiTB) ? TY_LE_SAN_XUAT : TY_LE_LAO_DONG;
    }

    public double getTyLePhuKien() {
        double tyLe = 0;
        if (dayCap) tyLe += TY_LE_DAY_CAP;
        if (banhXe) tyLe += TY_LE_BANH_XE;
        if (thayThe) tyLe += TY_LE_THAY_THE;
        return tyLe;
    }

    public double getGiaNhapKhau() {
        return giaNhap * getTyLeLoaiTB();
    }

    public double getTienPhuKien() {
        return giaNhap * getTyLePhuKien();
    }

    public double getGiaTri() {
        return soLuong * (getGiaNhapKhau() + getTienPhuKien());
    }

    public static String dinhDangTien(double value) {
        return new DecimalFormat("#,##0.##").format(value);
    }

    @Override
    public String toString() {
        return maTB + " - " + tenTB + " (" + dinhDangTien(getGiaTri()) + ")";
    }
}
