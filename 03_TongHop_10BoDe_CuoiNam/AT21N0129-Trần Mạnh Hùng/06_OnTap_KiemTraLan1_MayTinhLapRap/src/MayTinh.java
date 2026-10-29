import java.text.DecimalFormat;

/**
 * Dữ liệu của một máy tính trong danh sách DSMT (Đề Ôn Tập Lần 1).
 *
 * Hệ số tỷ lệ loại máy và phụ kiện được đóng gói dưới dạng hằng số tĩnh
 * trong lớp dữ liệu để phần giao diện chỉ đảm nhiệm nhập liệu và hiển thị.
 */
public final class MayTinh {
    public static final double TY_LE_LAP_RAP = 1.10;
    public static final double TY_LE_NHAP_KHAU = 1.20;
    public static final double TY_LE_VGA = 0.30;
    public static final double TY_LE_DVD = 0.05;
    public static final double TY_LE_LOA = 0.10;

    private String maMay;
    private String tenMay;
    private long donGia;
    private int soLuong;
    private String loaiMay;
    private boolean coVGA;
    private boolean coDVD;
    private boolean coLoa;

    public MayTinh() {
        this.loaiMay = "Lắp ráp";
    }

    public MayTinh(String maMay, String tenMay, long donGia, int soLuong,
                   String loaiMay, boolean coVGA, boolean coDVD, boolean coLoa) {
        this.maMay = maMay;
        this.tenMay = tenMay;
        this.donGia = donGia;
        this.soLuong = soLuong;
        this.loaiMay = loaiMay;
        this.coVGA = coVGA;
        this.coDVD = coDVD;
        this.coLoa = coLoa;
    }

    public String getMaMay() { return maMay; }
    public String getTenMay() { return tenMay; }
    public long getDonGia() { return donGia; }
    public int getSoLuong() { return soLuong; }
    public String getLoaiMay() { return loaiMay; }
    public boolean isCoVGA() { return coVGA; }
    public boolean isCoDVD() { return coDVD; }
    public boolean isCoLoa() { return coLoa; }

    public void setMaMay(String maMay) { this.maMay = maMay; }
    public void setTenMay(String tenMay) { this.tenMay = tenMay; }
    public void setDonGia(long donGia) { this.donGia = donGia; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void setLoaiMay(String loaiMay) { this.loaiMay = loaiMay; }
    public void setCoVGA(boolean coVGA) { this.coVGA = coVGA; }
    public void setCoDVD(boolean coDVD) { this.coDVD = coDVD; }
    public void setCoLoa(boolean coLoa) { this.coLoa = coLoa; }

    /**
     * Tỷ lệ loại máy:
     * - Lắp ráp: 110% (1.10)
     * - Nhập khẩu: 120% (1.20)
     */
    public double getTyLeLoaiMay() {
        return "Nhập khẩu".equalsIgnoreCase(loaiMay) ? TY_LE_NHAP_KHAU : TY_LE_LAP_RAP;
    }

    /**
     * Tổng tỷ lệ phụ kiện được chọn:
     * - VGA 2 GB: 30% (0.30)
     * - DVD: 5% (0.05)
     * - LOA: 10% (0.10)
     */
    public double getTyLePhuKien() {
        double tyLe = 0.0;
        if (coVGA) tyLe += TY_LE_VGA;
        if (coDVD) tyLe += TY_LE_DVD;
        if (coLoa) tyLe += TY_LE_LOA;
        return tyLe;
    }

    /**
     * Đơn giá bán = Đơn giá * Loại máy
     */
    public double getDonGiaBan() {
        return donGia * getTyLeLoaiMay();
    }

    /**
     * Tiền phụ kiện = Đơn giá * Phụ kiện
     */
    public double getTienPhuKien() {
        return donGia * getTyLePhuKien();
    }

    /**
     * Giá trị Máy = Số lượng * (Đơn giá bán + Tiền phụ kiện)
     */
    public double getGiaTriMay() {
        return soLuong * (getDonGiaBan() + getTienPhuKien());
    }

    /**
     * Định dạng tiền tệ chuẩn (ví dụ: 15,000,000)
     */
    public static String dinhDangTien(double value) {
        return new DecimalFormat("#,##0.##").format(value);
    }

    @Override
    public String toString() {
        return maMay + " - " + tenMay + " (" + dinhDangTien(getGiaTriMay()) + ")";
    }
}
