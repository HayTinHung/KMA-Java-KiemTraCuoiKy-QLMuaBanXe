package ontap2;

import java.util.Objects;

/** Một dòng của bảng HangHoa. */
public final class HangHoa {
    private final int maHangHoa;
    private final String tenHangHoa;
    private final int maLoai;
    private final String tinhChat;

    public HangHoa(int maHangHoa, String tenHangHoa, int maLoai, String tinhChat) {
        this.maHangHoa = maHangHoa;
        this.tenHangHoa = Objects.requireNonNull(tenHangHoa, "tenHangHoa");
        this.maLoai = maLoai;
        this.tinhChat = Objects.requireNonNull(tinhChat, "tinhChat");
    }

    public int getMaHangHoa() { return maHangHoa; }
    public String getTenHangHoa() { return tenHangHoa; }
    public int getMaLoai() { return maLoai; }
    public String getTinhChat() { return tinhChat; }
}
