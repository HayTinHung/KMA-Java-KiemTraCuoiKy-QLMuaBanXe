package ontap2;

import java.util.Objects;

/** Một dòng của bảng LoaiHang; TyLe được lưu dưới dạng phần trăm. */
public final class LoaiHang {
    private final int maLoai;
    private final String tenLoaiHang;
    private final int tyLe;

    public LoaiHang(int maLoai, String tenLoaiHang, int tyLe) {
        this.maLoai = maLoai;
        this.tenLoaiHang = Objects.requireNonNull(tenLoaiHang, "tenLoaiHang");
        this.tyLe = tyLe;
    }

    public int getMaLoai() { return maLoai; }
    public String getTenLoaiHang() { return tenLoaiHang; }
    public int getTyLe() { return tyLe; }

    @Override
    public String toString() { return tenLoaiHang + " (" + tyLe + "%)"; }
}
