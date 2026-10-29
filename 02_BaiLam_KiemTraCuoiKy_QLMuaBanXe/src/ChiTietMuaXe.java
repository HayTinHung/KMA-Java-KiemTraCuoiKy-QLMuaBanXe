/**
 * Lớp thực thể ChiTietMuaXe kết hợp thông tin bảng Mua_Xe với Khach_Hang và Xe
 * để hiển thị lên Khung hiển thị dữ liệu chi tiết và Cửa sổ Báo cáo.
 */
public class ChiTietMuaXe {
    private String maKH;
    private String tenKH;
    private String cmnd;
    private String gioiTinh;
    private String diaChi;
    private String maXe;
    private String ngayMua; // Định dạng dd/MM/yyyy
    private int soLuong;
    private long donGiaXe;
    private String ghiChu;

    public ChiTietMuaXe() {
        this.maKH = "";
        this.tenKH = "";
        this.cmnd = "";
        this.gioiTinh = "Nam";
        this.diaChi = "";
        this.maXe = "";
        this.ngayMua = "28/10/2011";
        this.soLuong = 1;
        this.donGiaXe = 0L;
        this.ghiChu = "";
    }

    public ChiTietMuaXe(String maKH, String tenKH, String cmnd, String gioiTinh, String diaChi,
                        String maXe, String ngayMua, int soLuong, long donGiaXe, String ghiChu) {
        this.maKH = maKH == null ? "" : maKH.trim();
        this.tenKH = tenKH == null ? "" : tenKH.trim();
        this.cmnd = cmnd == null ? "" : cmnd.trim();
        this.gioiTinh = gioiTinh == null ? "Nam" : gioiTinh.trim();
        this.diaChi = diaChi == null ? "" : diaChi.trim();
        this.maXe = maXe == null ? "" : maXe.trim();
        this.ngayMua = ngayMua == null ? "" : ngayMua.trim();
        this.soLuong = soLuong;
        this.donGiaXe = donGiaXe;
        this.ghiChu = ghiChu == null ? "" : ghiChu.trim();
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

    public String getMaXe() {
        return maXe;
    }

    public void setMaXe(String maXe) {
        this.maXe = maXe == null ? "" : maXe.trim();
    }

    public String getNgayMua() {
        return ngayMua;
    }

    public void setNgayMua(String ngayMua) {
        this.ngayMua = ngayMua == null ? "" : ngayMua.trim();
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public long getDonGiaXe() {
        return donGiaXe;
    }

    public void setDonGiaXe(long donGiaXe) {
        this.donGiaXe = donGiaXe;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu == null ? "" : ghiChu.trim();
    }

    /**
     * Tính Thành tiền của dòng mua xe:
     * Thành tiền = Số lượng * Đơn giá * 1.10 (gồm 10% thuế/phí trước bạ).
     * Ví dụ trên đề: Đơn giá = 21,500,000, Số lượng = 1 => Thành tiền = 23,650,000.
     */
    public long getThanhTien() {
        return Math.round(soLuong * (double) donGiaXe * Xe.TY_LE_THANH_TIEN);
    }
}
