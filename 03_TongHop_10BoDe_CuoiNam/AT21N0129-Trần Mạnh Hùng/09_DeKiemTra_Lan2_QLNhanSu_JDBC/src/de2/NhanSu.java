package de2;

/** Một dòng nhân sự trong bảng NhanSu. */
public final class NhanSu {
    private final int maNhanSu;
    private final String tenNhanSu;
    private final int tienLuong;
    private final int maCap;
    private final String tenCap;
    private final int baoHiem;

    public NhanSu(int maNhanSu, String tenNhanSu, int tienLuong, int maCap,
                  String tenCap, int baoHiem) {
        this.maNhanSu = maNhanSu;
        this.tenNhanSu = tenNhanSu;
        this.tienLuong = tienLuong;
        this.maCap = maCap;
        this.tenCap = tenCap;
        this.baoHiem = baoHiem;
    }

    public int getMaNhanSu() { return maNhanSu; }
    public String getTenNhanSu() { return tenNhanSu; }
    public int getTienLuong() { return tienLuong; }
    public int getMaCap() { return maCap; }
    public String getTenCap() { return tenCap; }
    public int getBaoHiem() { return baoHiem; }
}
