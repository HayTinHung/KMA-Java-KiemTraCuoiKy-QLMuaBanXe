import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Lớp thực thể Xe biểu diễn dữ liệu bảng Xe trong CSDL QLMuaBanXe.
 * Cấu trúc: Xe [MaXe; TenXe (Text, 50); DungTich (số nguyên); DonGia (số nguyên)].
 */
public class Xe {
    /** Hệ số thuế VAT & phí trước bạ (110% = Đơn giá + 10% thuế): 21,500,000 * 1.10 = 23,650,000 VNĐ. */
    public static final double TY_LE_THANH_TIEN = 1.10;

    private String maXe;
    private String tenXe;
    private int dungTich;
    private long donGia;

    public Xe() {
        this.maXe = "";
        this.tenXe = "";
        this.dungTich = 110;
        this.donGia = 0L;
    }

    public Xe(String maXe, String tenXe, int dungTich, long donGia) {
        this.maXe = maXe == null ? "" : maXe.trim();
        this.tenXe = tenXe == null ? "" : tenXe.trim();
        this.dungTich = dungTich;
        this.donGia = donGia;
    }

    public String getMaXe() {
        return maXe;
    }

    public void setMaXe(String maXe) {
        this.maXe = maXe == null ? "" : maXe.trim();
    }

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe == null ? "" : tenXe.trim();
    }

    public int getDungTich() {
        return dungTich;
    }

    public void setDungTich(int dungTich) {
        this.dungTich = dungTich;
    }

    public long getDonGia() {
        return donGia;
    }

    public void setDonGia(long donGia) {
        this.donGia = donGia;
    }

    public static String dinhDangSo(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(value);
    }

    public static long parseDonGia(String text) {
        if (text == null) {
            throw new NumberFormatException("Đơn giá rỗng");
        }
        String cleaned = text.trim().replace(",", "").replace(".", "").replace("VNĐ", "").replace("VND", "").trim();
        return Long.parseLong(cleaned);
    }

    @Override
    public String toString() {
        return maXe + " - " + tenXe;
    }
}
