package de2;

/** Một cấp nhân sự và phần trăm bảo hiểm của cấp đó. */
public final class Cap {
    private final int maCap;
    private final String ten;
    private final int baoHiem;

    public Cap(int maCap, String ten, int baoHiem) {
        this.maCap = maCap;
        this.ten = ten;
        this.baoHiem = baoHiem;
    }

    public int getMaCap() { return maCap; }
    public String getTen() { return ten; }
    public int getBaoHiem() { return baoHiem; }

    @Override
    public String toString() {
        return ten;
    }
}
