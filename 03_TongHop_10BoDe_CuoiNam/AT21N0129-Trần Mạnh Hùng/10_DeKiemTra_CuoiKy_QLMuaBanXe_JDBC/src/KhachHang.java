/**
 * Lớp thực thể KhachHang biểu diễn dữ liệu bảng Khach_Hang trong CSDL QLMuaBanXe.
 * Cấu trúc: Khach_Hang [MaKH; TenKH (Text, 50); CMND (Số); GioiTinh (Text, 10); DiaChi (Text, 50)].
 */
public class KhachHang {
    private String maKH;
    private String tenKH;
    private String cmnd;
    private String gioiTinh;
    private String diaChi;

    public KhachHang() {
        this.maKH = "";
        this.tenKH = "";
        this.cmnd = "";
        this.gioiTinh = "Nam";
        this.diaChi = "";
    }

    public KhachHang(String maKH, String tenKH, String cmnd, String gioiTinh, String diaChi) {
        this.maKH = maKH == null ? "" : maKH.trim();
        this.tenKH = tenKH == null ? "" : tenKH.trim();
        this.cmnd = cmnd == null ? "" : cmnd.trim();
        this.gioiTinh = gioiTinh == null ? "Nam" : gioiTinh.trim();
        this.diaChi = diaChi == null ? "" : diaChi.trim();
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH == null ? "" : maKH.trim();
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH == null ? "" : tenKH.trim();
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd == null ? "" : cmnd.trim();
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh == null ? "Nam" : gioiTinh.trim();
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi == null ? "" : diaChi.trim();
    }

    @Override
    public String toString() {
        return maKH + " - " + tenKH;
    }
}
