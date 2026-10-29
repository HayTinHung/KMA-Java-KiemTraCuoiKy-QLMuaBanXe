import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tầng truy xuất dữ liệu CSDL SQL Server QLMuaBanXe.
 *
 * Đáp ứng đầy đủ:
 * 1. Lưu ý đầu đề: Kiểm tra kết nối CSDL đã có kết nối chưa; nếu kết nối không được
 *    thì thông báo và đưa dữ liệu trống rỗng lên màn hình.
 * 2. Ghi chú cuối đề: Khi xử lý dữ liệu trên Database Server thì luôn kiểm tra việc
 *    kết nối thành công hay không; nếu kết nối không thành công thì tạo ra một dòng
 *    dữ liệu ảo, rồi sau đó đưa lên các control chứa dữ liệu thích hợp trên Cửa sổ.
 */
public class MuaBanXeRepository {
    private static final String DEFAULT_JDBC_URL =
            "jdbc:sqlserver://127.0.0.1:1433;databaseName=QLMuaBanXe;"
                    + "encrypt=true;trustServerCertificate=true;integratedSecurity=true;loginTimeout=2";
    private static final String DEFAULT_SQLCMD_SERVER = ".\\SQL2025DEV";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String sqlcmdServer;
    private boolean forceDisconnected = false;
    private Boolean useSqlcmdBackend = null;

    // Bộ nhớ dữ liệu ảo dự phòng khi mất kết nối Database Server
    private final List<KhachHang> virtualKhachHang = new ArrayList<KhachHang>();
    private final List<Xe> virtualXe = new ArrayList<Xe>();
    private final List<ChiTietMuaXe> virtualMuaXe = new ArrayList<ChiTietMuaXe>();

    public MuaBanXeRepository() {
        this(DEFAULT_JDBC_URL, "", "", DEFAULT_SQLCMD_SERVER);
    }

    public MuaBanXeRepository(String jdbcUrl, String username, String password, String sqlcmdServer) {
        this.jdbcUrl = jdbcUrl == null || jdbcUrl.trim().isEmpty() ? DEFAULT_JDBC_URL : jdbcUrl.trim();
        this.username = username == null ? "" : username.trim();
        this.password = password == null ? "" : password;
        this.sqlcmdServer = sqlcmdServer == null || sqlcmdServer.trim().isEmpty()
                ? DEFAULT_SQLCMD_SERVER : sqlcmdServer.trim();
    }

    public void setForceDisconnected(boolean forceDisconnected) {
        this.forceDisconnected = forceDisconnected;
        this.useSqlcmdBackend = null;
    }

    public boolean isForceDisconnected() {
        return forceDisconnected;
    }

    /**
     * Kiểm tra kết nối tới CSDL SQL Server QLMuaBanXe có thành công hay không.
     */
    public boolean kiemTraKetNoi() {
        if (forceDisconnected) {
            return false;
        }
        try {
            if (useSqlcmdBackend != null) {
                if (!useSqlcmdBackend.booleanValue()) {
                    try (Connection conn = openJdbc()) {
                        return conn != null && !conn.isClosed();
                    }
                } else {
                    return "1".equals(sqlcmdSingleValue("SELECT 1"));
                }
            }
            try (Connection conn = openJdbc()) {
                if (conn != null && !conn.isClosed()) {
                    useSqlcmdBackend = Boolean.FALSE;
                    return true;
                }
            } catch ( Throwable ignored ) {
                // Chuyển sang kiểm tra cầu nối sqlcmd cục bộ
            }
            if ("1".equals(sqlcmdSingleValue("SELECT 1"))) {
                useSqlcmdBackend = Boolean.TRUE;
                return true;
            }
        } catch (Throwable ignored) {
            // Kết nối thất bại
        }
        return false;
    }

    /**
     * Khởi tạo dòng dữ liệu ảo dự phòng (Virtual Row) khi không kết nối được Database Server
     * theo đúng yêu cầu tại phần Ghi chú trang 3 của đề thi.
     */
    public void khoiTaoDongDuLieuAo() {
        virtualKhachHang.clear();
        virtualXe.clear();
        virtualMuaXe.clear();

        KhachHang khAo = new KhachHang("KH01", "Nguyễn Văn Sơn (Dữ liệu ảo)", "301458080", "Nam", "TP.HCM");
        KhachHang khAo2 = new KhachHang("KH02", "Trần Thị Mai (Dữ liệu ảo)", "079199001", "Nữ", "TP.HCM");
        virtualKhachHang.add(khAo);
        virtualKhachHang.add(khAo2);

        Xe xeAo = new Xe("XE01", "Suzuki X-Bike", 125, 21500000L);
        virtualXe.add(xeAo);

        ChiTietMuaXe ctAo = new ChiTietMuaXe(
                khAo.getMaKH(), khAo.getTenKH(), khAo.getCmnd(), khAo.getGioiTinh(), khAo.getDiaChi(),
                xeAo.getMaXe(), "28/10/2011", 1, xeAo.getDonGia(), "Dòng dữ liệu ảo khi mất kết nối CSDL"
        );
        virtualMuaXe.add(ctAo);
    }

    /**
     * Khởi tạo đầy đủ 4 xe mẫu như trong đề khi chạy trên môi trường OnlineGDB không có SQL Server.
     */
    public void khoiTaoDuLieuMauAoDayDu() {
        virtualKhachHang.clear();
        virtualXe.clear();
        virtualMuaXe.clear();

        virtualKhachHang.add(new KhachHang("KH01", "Nguyễn Văn Sơn", "301458080", "Nam", "125 Nguyễn Trãi, Q.1, TP.HCM"));
        virtualKhachHang.add(new KhachHang("KH02", "Trần Thị Mai", "079199001", "Nữ", "42 Lê Lợi, Q.3, TP.HCM"));
        virtualKhachHang.add(new KhachHang("KH03", "Lê Hoàng Nam", "079198234", "Nam", "88 Cộng Hòa, Q.Tân Bình, TP.HCM"));
        virtualKhachHang.add(new KhachHang("KH04", "Phạm Minh Tuấn", "079200567", "Nam", "19 Võ Văn Ngân, TP.Thủ Đức"));

        virtualXe.add(new Xe("XE01", "Suzuki X-Bike", 125, 21500000L));
        virtualXe.add(new Xe("XE02", "Honda AirBlade", 150, 42000000L));
        virtualXe.add(new Xe("XE03", "Yamaha Exciter", 155, 47500000L));
        virtualXe.add(new Xe("KH001", "Towner", 110, 18500000L));

        virtualMuaXe.add(new ChiTietMuaXe("KH01", "Nguyễn Văn Sơn", "301458080", "Nam", "125 Nguyễn Trãi, Q.1, TP.HCM",
                "XE01", "28/10/2011", 1, 21500000L, "Khách mua tiền mặt"));
        virtualMuaXe.add(new ChiTietMuaXe("KH02", "Trần Thị Mai", "079199001", "Nữ", "42 Lê Lợi, Q.3, TP.HCM",
                "XE02", "15/05/2012", 1, 42000000L, "Tặng mũ bảo hiểm"));
        virtualMuaXe.add(new ChiTietMuaXe("KH03", "Lê Hoàng Nam", "079198234", "Nam", "88 Cộng Hòa, Q.Tân Bình, TP.HCM",
                "XE02", "20/06/2012", 2, 42000000L, "Mua theo lô công ty"));
        virtualMuaXe.add(new ChiTietMuaXe("KH04", "Phạm Minh Tuấn", "079200567", "Nam", "19 Võ Văn Ngân, TP.Thủ Đức",
                "XE03", "12/08/2012", 1, 47500000L, "Bảo hành 3 năm"));
        virtualMuaXe.add(new ChiTietMuaXe("KH01", "Nguyễn Văn Sơn", "301458080", "Nam", "125 Nguyễn Trãi, Q.1, TP.HCM",
                "KH001", "29/10/2012", 3, 18500000L, ""));
        virtualMuaXe.add(new ChiTietMuaXe("KH02", "Trần Thị Mai", "079199001", "Nữ", "42 Lê Lợi, Q.3, TP.HCM",
                "KH001", "28/10/2012", 1, 18500000L, "Laptop"));
    }

    public List<Xe> layDanhSachXe() throws SQLException {
        if (!kiemTraKetNoi()) {
            if (virtualXe.isEmpty()) {
                khoiTaoDongDuLieuAo();
            }
            return new ArrayList<Xe>(virtualXe);
        }
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String sql = "SELECT MaXe, " + hexCol("TenXe") + ", DungTich, DonGia FROM dbo.Xe "
                    + "ORDER BY CASE WHEN MaXe LIKE 'XE%' THEN 0 ELSE 1 END, MaXe";
            List<String[]> rows = sqlcmdQuery(sql, 4);
            List<Xe> list = new ArrayList<Xe>();
            for (String[] r : rows) {
                list.add(new Xe(r[0], decodeHexUnicode(r[1]), Integer.parseInt(r[2]), Long.parseLong(r[3])));
            }
            return list;
        }
        String sql = "SELECT MaXe, TenXe, DungTich, DonGia FROM dbo.Xe "
                + "ORDER BY CASE WHEN MaXe LIKE 'XE%' THEN 0 ELSE 1 END, MaXe";
        List<Xe> list = new ArrayList<Xe>();
        try (Connection c = openJdbc();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Xe(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getLong(4)));
            }
        }
        return list;
    }

    public List<KhachHang> layDanhSachKhachHang() throws SQLException {
        if (!kiemTraKetNoi()) {
            if (virtualKhachHang.isEmpty()) {
                khoiTaoDongDuLieuAo();
            }
            return new ArrayList<KhachHang>(virtualKhachHang);
        }
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String sql = "SELECT MaKH, " + hexCol("TenKH") + ", CMND, " + hexCol("GioiTinh") + ", "
                    + hexCol("DiaChi") + " FROM dbo.Khach_Hang ORDER BY MaKH";
            List<String[]> rows = sqlcmdQuery(sql, 5);
            List<KhachHang> list = new ArrayList<KhachHang>();
            for (String[] r : rows) {
                list.add(new KhachHang(r[0], decodeHexUnicode(r[1]), r[2],
                        decodeHexUnicode(r[3]), decodeHexUnicode(r[4])));
            }
            return list;
        }
        String sql = "SELECT MaKH, TenKH, CMND, GioiTinh, DiaChi FROM dbo.Khach_Hang ORDER BY MaKH";
        List<KhachHang> list = new ArrayList<KhachHang>();
        try (Connection c = openJdbc();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new KhachHang(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5)));
            }
        }
        return list;
    }

    public List<ChiTietMuaXe> layChiTietMuaTheoXe(String maXe) throws SQLException {
        if (!kiemTraKetNoi()) {
            List<ChiTietMuaXe> list = new ArrayList<ChiTietMuaXe>();
            for (ChiTietMuaXe ct : virtualMuaXe) {
                if (ct.getMaXe().equalsIgnoreCase(maXe)) {
                    list.add(ct);
                }
            }
            return list;
        }
        String cleanMaXe = sanitizeSqlLiteral(maXe);
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String sql = "SELECT M.MaKH, " + hexCol("K.TenKH") + ", K.CMND, " + hexCol("K.GioiTinh") + ", "
                    + hexCol("K.DiaChi") + ", M.MaXe, CONVERT(varchar(10), M.NgayMua, 103), M.SoLuong, "
                    + "X.DonGia, " + hexCol("M.GhiChu") + " "
                    + "FROM dbo.Mua_Xe M "
                    + "JOIN dbo.Khach_Hang K ON M.MaKH = K.MaKH "
                    + "JOIN dbo.Xe X ON M.MaXe = X.MaXe "
                    + "WHERE M.MaXe = '" + cleanMaXe + "' "
                    + "ORDER BY M.NgayMua DESC, M.MaKH ASC";
            List<String[]> rows = sqlcmdQuery(sql, 10);
            List<ChiTietMuaXe> list = new ArrayList<ChiTietMuaXe>();
            for (String[] r : rows) {
                list.add(new ChiTietMuaXe(
                        r[0], decodeHexUnicode(r[1]), r[2], decodeHexUnicode(r[3]), decodeHexUnicode(r[4]),
                        r[5], r[6], Integer.parseInt(r[7]), Long.parseLong(r[8]), decodeHexUnicode(r[9])
                ));
            }
            return list;
        }
        String sql = "SELECT M.MaKH, K.TenKH, K.CMND, K.GioiTinh, K.DiaChi, M.MaXe, "
                + "CONVERT(varchar(10), M.NgayMua, 103), M.SoLuong, X.DonGia, M.GhiChu "
                + "FROM dbo.Mua_Xe M "
                + "JOIN dbo.Khach_Hang K ON M.MaKH = K.MaKH "
                + "JOIN dbo.Xe X ON M.MaXe = X.MaXe "
                + "WHERE M.MaXe = ? ORDER BY M.NgayMua DESC, M.MaKH ASC";
        List<ChiTietMuaXe> list = new ArrayList<ChiTietMuaXe>();
        try (Connection c = openJdbc();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ChiTietMuaXe(
                            rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                            rs.getString(6), rs.getString(7), rs.getInt(8), rs.getLong(9), rs.getString(10)
                    ));
                }
            }
        }
        return list;
    }

    public List<ChiTietMuaXe> locBaoCaoMuaXe(String maXe, String ngayMuaFilter) throws SQLException {
        List<ChiTietMuaXe> source = new ArrayList<ChiTietMuaXe>();
        if (maXe == null || maXe.trim().isEmpty() || "-- Tất cả --".equals(maXe.trim())) {
            for (Xe x : layDanhSachXe()) {
                source.addAll(layChiTietMuaTheoXe(x.getMaXe()));
            }
        } else {
            source.addAll(layChiTietMuaTheoXe(maXe.trim()));
        }
        String filter = ngayMuaFilter == null ? "" : ngayMuaFilter.trim();
        if (filter.isEmpty()) {
            return source;
        }
        List<ChiTietMuaXe> filtered = new ArrayList<ChiTietMuaXe>();
        for (ChiTietMuaXe ct : source) {
            if (ct.getNgayMua().equalsIgnoreCase(filter) || ct.getNgayMua().contains(filter)) {
                filtered.add(ct);
            }
        }
        return filtered;
    }

    public boolean tonTaiMaXe(String maXe) throws SQLException {
        for (Xe x : layDanhSachXe()) {
            if (x.getMaXe().equalsIgnoreCase(maXe.trim())) {
                return true;
            }
        }
        return false;
    }

    public void themXe(Xe xe) throws SQLException {
        if (tonTaiMaXe(xe.getMaXe())) {
            throw new SQLException("Mã xe đã tồn tại, vui lòng nhập lại!");
        }
        if (!kiemTraKetNoi()) {
            virtualXe.add(xe);
            return;
        }
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String sql = "INSERT INTO dbo.Xe (MaXe, TenXe, DungTich, DonGia) VALUES ('"
                    + sanitizeSqlLiteral(xe.getMaXe()) + "', "
                    + toNCharExpr(xe.getTenXe()) + ", "
                    + xe.getDungTich() + ", "
                    + xe.getDonGia() + "); SELECT 'OK'";
            if (!"OK".equals(sqlcmdSingleValue(sql))) {
                throw new SQLException("Không thể thêm Xe vào CSDL.");
            }
            return;
        }
        String sql = "INSERT INTO dbo.Xe (MaXe, TenXe, DungTich, DonGia) VALUES (?, ?, ?, ?)";
        try (Connection c = openJdbc();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, xe.getMaXe());
            ps.setString(2, xe.getTenXe());
            ps.setInt(3, xe.getDungTich());
            ps.setLong(4, xe.getDonGia());
            ps.executeUpdate();
        }
    }

    public void xoaXe(String maXe) throws SQLException {
        if (!kiemTraKetNoi()) {
            for (int i = virtualXe.size() - 1; i >= 0; i--) {
                if (virtualXe.get(i).getMaXe().equalsIgnoreCase(maXe)) {
                    virtualXe.remove(i);
                }
            }
            for (int i = virtualMuaXe.size() - 1; i >= 0; i--) {
                if (virtualMuaXe.get(i).getMaXe().equalsIgnoreCase(maXe)) {
                    virtualMuaXe.remove(i);
                }
            }
            return;
        }
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String clean = sanitizeSqlLiteral(maXe);
            String sql = "DELETE FROM dbo.Mua_Xe WHERE MaXe = '" + clean + "'; "
                    + "DELETE FROM dbo.Xe WHERE MaXe = '" + clean + "'; SELECT 'OK'";
            sqlcmdSingleValue(sql);
            return;
        }
        try (Connection c = openJdbc()) {
            try (PreparedStatement ps1 = c.prepareStatement("DELETE FROM dbo.Mua_Xe WHERE MaXe = ?")) {
                ps1.setString(1, maXe);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = c.prepareStatement("DELETE FROM dbo.Xe WHERE MaXe = ?")) {
                ps2.setString(1, maXe);
                ps2.executeUpdate();
            }
        }
    }

    public void luuHoacCapNhatKhachHang(KhachHang kh) throws SQLException {
        if (!kiemTraKetNoi()) {
            for (int i = 0; i < virtualKhachHang.size(); i++) {
                if (virtualKhachHang.get(i).getMaKH().equalsIgnoreCase(kh.getMaKH())) {
                    virtualKhachHang.set(i, kh);
                    return;
                }
            }
            virtualKhachHang.add(kh);
            return;
        }
        String cleanMa = sanitizeSqlLiteral(kh.getMaKH());
        String cleanCmnd = sanitizeSqlLiteral(kh.getCmnd());
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String sql = "IF EXISTS (SELECT 1 FROM dbo.Khach_Hang WHERE MaKH = '" + cleanMa + "') "
                    + "UPDATE dbo.Khach_Hang SET TenKH = " + toNCharExpr(kh.getTenKH())
                    + ", CMND = '" + cleanCmnd + "', GioiTinh = " + toNCharExpr(kh.getGioiTinh())
                    + ", DiaChi = " + toNCharExpr(kh.getDiaChi().isEmpty() ? "TP.HCM" : kh.getDiaChi())
                    + " WHERE MaKH = '" + cleanMa + "' "
                    + "ELSE INSERT INTO dbo.Khach_Hang (MaKH, TenKH, CMND, GioiTinh, DiaChi) VALUES ('"
                    + cleanMa + "', " + toNCharExpr(kh.getTenKH()) + ", '" + cleanCmnd + "', "
                    + toNCharExpr(kh.getGioiTinh()) + ", "
                    + toNCharExpr(kh.getDiaChi().isEmpty() ? "TP.HCM" : kh.getDiaChi()) + "); SELECT 'OK'";
            sqlcmdSingleValue(sql);
            return;
        }
        String checkSql = "SELECT COUNT(*) FROM dbo.Khach_Hang WHERE MaKH = ?";
        try (Connection c = openJdbc();
             PreparedStatement checkPs = c.prepareStatement(checkSql)) {
            checkPs.setString(1, kh.getMaKH());
            boolean exists = false;
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    exists = true;
                }
            }
            if (exists) {
                try (PreparedStatement up = c.prepareStatement(
                        "UPDATE dbo.Khach_Hang SET TenKH=?, CMND=?, GioiTinh=?, DiaChi=? WHERE MaKH=?")) {
                    up.setString(1, kh.getTenKH());
                    up.setString(2, kh.getCmnd());
                    up.setString(3, kh.getGioiTinh());
                    up.setString(4, kh.getDiaChi().isEmpty() ? "TP.HCM" : kh.getDiaChi());
                    up.setString(5, kh.getMaKH());
                    up.executeUpdate();
                }
            } else {
                try (PreparedStatement ins = c.prepareStatement(
                        "INSERT INTO dbo.Khach_Hang (MaKH, TenKH, CMND, GioiTinh, DiaChi) VALUES (?, ?, ?, ?, ?)")) {
                    ins.setString(1, kh.getMaKH());
                    ins.setString(2, kh.getTenKH());
                    ins.setString(3, kh.getCmnd());
                    ins.setString(4, kh.getGioiTinh());
                    ins.setString(5, kh.getDiaChi().isEmpty() ? "TP.HCM" : kh.getDiaChi());
                    ins.executeUpdate();
                }
            }
        }
    }

    public void themChiTietMuaXe(ChiTietMuaXe ct) throws SQLException {
        String isoDate = chuyenSangIsoDate(ct.getNgayMua());
        luuHoacCapNhatKhachHang(new KhachHang(
                ct.getMaKH(), ct.getTenKH(), ct.getCmnd(), ct.getGioiTinh(),
                ct.getDiaChi().isEmpty() ? "TP.HCM" : ct.getDiaChi()
        ));
        if (!kiemTraKetNoi()) {
            virtualMuaXe.add(ct);
            return;
        }
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String sql = "INSERT INTO dbo.Mua_Xe (MaKH, MaXe, NgayMua, SoLuong, GhiChu) VALUES ('"
                    + sanitizeSqlLiteral(ct.getMaKH()) + "', '"
                    + sanitizeSqlLiteral(ct.getMaXe()) + "', '"
                    + isoDate + "', "
                    + ct.getSoLuong() + ", "
                    + toNCharExpr(ct.getGhiChu()) + "); SELECT 'OK'";
            sqlcmdSingleValue(sql);
            return;
        }
        String sql = "INSERT INTO dbo.Mua_Xe (MaKH, MaXe, NgayMua, SoLuong, GhiChu) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = openJdbc();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ct.getMaKH());
            ps.setString(2, ct.getMaXe());
            ps.setString(3, isoDate);
            ps.setInt(4, ct.getSoLuong());
            ps.setString(5, ct.getGhiChu());
            ps.executeUpdate();
        }
    }

    public void suaChiTietMuaXe(String oldMaKH, String maXe, String oldNgayMua, ChiTietMuaXe updated) throws SQLException {
        String oldIso = chuyenSangIsoDate(oldNgayMua);
        String newIso = chuyenSangIsoDate(updated.getNgayMua());
        luuHoacCapNhatKhachHang(new KhachHang(
                updated.getMaKH(), updated.getTenKH(), updated.getCmnd(), updated.getGioiTinh(),
                updated.getDiaChi().isEmpty() ? "TP.HCM" : updated.getDiaChi()
        ));
        if (!kiemTraKetNoi()) {
            for (int i = 0; i < virtualMuaXe.size(); i++) {
                ChiTietMuaXe item = virtualMuaXe.get(i);
                if (item.getMaKH().equalsIgnoreCase(oldMaKH)
                        && item.getMaXe().equalsIgnoreCase(maXe)
                        && item.getNgayMua().equalsIgnoreCase(oldNgayMua)) {
                    virtualMuaXe.set(i, updated);
                    return;
                }
            }
            return;
        }
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String sql = "UPDATE dbo.Mua_Xe SET MaKH = '" + sanitizeSqlLiteral(updated.getMaKH())
                    + "', NgayMua = '" + newIso
                    + "', SoLuong = " + updated.getSoLuong()
                    + ", GhiChu = " + toNCharExpr(updated.getGhiChu())
                    + " WHERE MaKH = '" + sanitizeSqlLiteral(oldMaKH)
                    + "' AND MaXe = '" + sanitizeSqlLiteral(maXe)
                    + "' AND NgayMua = '" + oldIso + "'; SELECT 'OK'";
            sqlcmdSingleValue(sql);
            return;
        }
        String sql = "UPDATE dbo.Mua_Xe SET MaKH=?, NgayMua=?, SoLuong=?, GhiChu=? WHERE MaKH=? AND MaXe=? AND NgayMua=?";
        try (Connection c = openJdbc();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, updated.getMaKH());
            ps.setString(2, newIso);
            ps.setInt(3, updated.getSoLuong());
            ps.setString(4, updated.getGhiChu());
            ps.setString(5, oldMaKH);
            ps.setString(6, maXe);
            ps.setString(7, oldIso);
            ps.executeUpdate();
        }
    }

    public void xoaChiTietMuaXe(String maKH, String maXe, String ngayMua) throws SQLException {
        String isoDate = chuyenSangIsoDate(ngayMua);
        if (!kiemTraKetNoi()) {
            for (int i = virtualMuaXe.size() - 1; i >= 0; i--) {
                ChiTietMuaXe item = virtualMuaXe.get(i);
                if (item.getMaKH().equalsIgnoreCase(maKH)
                        && item.getMaXe().equalsIgnoreCase(maXe)
                        && item.getNgayMua().equalsIgnoreCase(ngayMua)) {
                    virtualMuaXe.remove(i);
                    return;
                }
            }
            return;
        }
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String sql = "DELETE FROM dbo.Mua_Xe WHERE MaKH = '" + sanitizeSqlLiteral(maKH)
                    + "' AND MaXe = '" + sanitizeSqlLiteral(maXe)
                    + "' AND NgayMua = '" + isoDate + "'; SELECT 'OK'";
            sqlcmdSingleValue(sql);
            return;
        }
        String sql = "DELETE FROM dbo.Mua_Xe WHERE MaKH=? AND MaXe=? AND NgayMua=?";
        try (Connection c = openJdbc();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maKH);
            ps.setString(2, maXe);
            ps.setString(3, isoDate);
            ps.executeUpdate();
        }
    }

    public static String chuyenSangIsoDate(String ddMMyyyy) throws SQLException {
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat("dd/MM/yyyy");
            inFmt.setLenient(false);
            Date parsed = inFmt.parse(ddMMyyyy.trim());
            return new SimpleDateFormat("yyyy-MM-dd").format(parsed);
        } catch (ParseException e) {
            throw new SQLException("Ngày mua không đúng định dạng dd/MM/yyyy: " + ddMMyyyy);
        }
    }

    private Connection openJdbc() throws SQLException {
        if (username.isEmpty()) {
            return DriverManager.getConnection(jdbcUrl);
        }
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    private static String hexCol(String colName) {
        return "CONVERT(varchar(400), CONVERT(varbinary(200), " + colName + "), 2)";
    }

    private static String sanitizeSqlLiteral(String val) {
        return val == null ? "" : val.trim().replace("'", "''");
    }

    private static String toNCharExpr(String value) {
        if (value == null || value.isEmpty()) {
            return "N''";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            if (i > 0) {
                sb.append('+');
            }
            sb.append("NCHAR(").append((int) value.charAt(i)).append(')');
        }
        return sb.toString();
    }

    private static String decodeHexUnicode(String hex) {
        if (hex == null || hex.isEmpty()) {
            return "";
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            bytes[i] = (byte) ((hi << 4) | lo);
        }
        return new String(bytes, StandardCharsets.UTF_16LE);
    }

    private String sqlcmdSingleValue(String sql) throws SQLException {
        List<String[]> rows = sqlcmdQuery(sql, 1);
        if (rows.isEmpty()) {
            throw new SQLException("SQL Server không trả về dữ liệu.");
        }
        return rows.get(0)[0];
    }

    private List<String[]> sqlcmdQuery(String sql, int expectedCols) throws SQLException {
        String fullQuery = "SET NOCOUNT ON; SET XACT_ABORT ON; " + sql;
        ProcessBuilder pb = new ProcessBuilder(
                "sqlcmd", "-S", sqlcmdServer, "-C", "-d", "QLMuaBanXe",
                "-W", "-h", "-1", "-s", "|", "-Q", fullQuery
        );
        pb.redirectErrorStream(true);
        try {
            Process proc = pb.start();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (InputStream in = proc.getInputStream()) {
                byte[] chunk = new byte[4096];
                int read;
                while ((read = in.read(chunk)) != -1) {
                    buffer.write(chunk, 0, read);
                }
            }
            boolean finished = proc.waitFor(15, TimeUnit.SECONDS);
            if (!finished) {
                proc.destroyForcibly();
                throw new SQLException("Quá thời gian chờ kết nối SQL Server.");
            }
            String out = new String(buffer.toByteArray(), StandardCharsets.UTF_8).trim();
            if (proc.exitValue() != 0 || out.contains("Msg ")) {
                throw new SQLException("Lỗi SQL Server: " + out);
            }
            List<String[]> rows = new ArrayList<String[]>();
            if (out.isEmpty()) {
                return rows;
            }
            String[] lines = out.split("\\R");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                String[] parts = trimmed.split("\\|", -1);
                if (parts.length == expectedCols) {
                    for (int i = 0; i < parts.length; i++) {
                        parts[i] = parts[i].trim();
                    }
                    rows.add(parts);
                }
            }
            return rows;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Không thể thực thi sqlcmd: " + e.getMessage(), e);
        }
    }
}
