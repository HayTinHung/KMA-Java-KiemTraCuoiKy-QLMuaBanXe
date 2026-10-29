package de2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Tầng dữ liệu cho QLNhanSu, có JDBC và cầu nối sqlcmd dự phòng cục bộ. */
public final class NhanSuRepository {
    private static final String DEFAULT_URL =
            "jdbc:sqlserver://127.0.0.1:14333;databaseName=QLNhanSu;"
                    + "encrypt=true;trustServerCertificate=true;integratedSecurity=true;loginTimeout=3";
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final boolean allowFallback;
    private final SqlcmdNhanSuGateway sqlcmd;
    private volatile boolean backendSelected;
    private volatile boolean useSqlcmd;

    private NhanSuRepository(String jdbcUrl, String username, String password, boolean allowFallback) {
        this.jdbcUrl = jdbcUrl;
        this.username = username == null ? "" : username.trim();
        this.password = password == null ? "" : password;
        this.allowFallback = allowFallback && this.username.isEmpty();
        this.sqlcmd = new SqlcmdNhanSuGateway(System.getenv("DE2_SQLCMD_SERVER"));
    }

    public static NhanSuRepository fromEnvironment() {
        String url = System.getenv("DE2_DB_URL");
        boolean defaultUrl = url == null || url.trim().isEmpty();
        return new NhanSuRepository(defaultUrl ? DEFAULT_URL : url.trim(),
                System.getenv("DE2_DB_USER"), System.getenv("DE2_DB_PASSWORD"), defaultUrl);
    }

    private Connection open() throws SQLException {
        return username.isEmpty() ? DriverManager.getConnection(jdbcUrl)
                : DriverManager.getConnection(jdbcUrl, username, password);
    }

    private boolean useSqlcmd() throws SQLException {
        if (backendSelected) return useSqlcmd;
        synchronized (this) {
            if (backendSelected) return useSqlcmd;
            try (Connection ignored = open()) {
                useSqlcmd = false;
                backendSelected = true;
                return false;
            } catch (SQLException jdbcError) {
                if (!allowFallback) throw jdbcError;
                try {
                    sqlcmd.ping();
                    useSqlcmd = true;
                    backendSelected = true;
                    return true;
                } catch (SQLException fallbackError) {
                    fallbackError.addSuppressed(jdbcError);
                    throw fallbackError;
                }
            }
        }
    }

    public List<NhanSu> listNhanSu() throws SQLException {
        if (useSqlcmd()) return sqlcmd.listNhanSu();
        String sql = "SELECT N.MNS,N.TNS,N.TienLuong,N.MaCap,C.Ten,C.BaoHiem "
                + "FROM dbo.NhanSu N JOIN dbo.Cap C ON N.MaCap=C.MaCap ORDER BY N.MNS";
        List<NhanSu> result = new ArrayList<NhanSu>();
        try (Connection c = open(); PreparedStatement s = c.prepareStatement(sql); ResultSet r = s.executeQuery()) {
            while (r.next()) result.add(new NhanSu(r.getInt(1), r.getString(2), r.getInt(3),
                    r.getInt(4), r.getString(5), r.getInt(6)));
        }
        return result;
    }

    public List<Cap> listCap() throws SQLException {
        if (useSqlcmd()) return sqlcmd.listCap();
        List<Cap> result = new ArrayList<Cap>();
        try (Connection c = open(); PreparedStatement s = c.prepareStatement(
                "SELECT MaCap,Ten,BaoHiem FROM dbo.Cap ORDER BY MaCap"); ResultSet r = s.executeQuery()) {
            while (r.next()) result.add(new Cap(r.getInt(1), r.getString(2), r.getInt(3)));
        }
        return result;
    }

    public void themNhanSu(NhanSu row) throws SQLException {
        validate(row);
        if (useSqlcmd()) { sqlcmd.themNhanSu(row); return; }
        try (Connection c = open(); PreparedStatement s = c.prepareStatement(
                "INSERT INTO dbo.NhanSu(MNS,TNS,TienLuong,MaCap) VALUES(?,?,?,?)")) {
            bind(s, row); s.executeUpdate();
        } catch (SQLException ex) { throw readable(ex, "Không thể thêm nhân sự. Kiểm tra mã, cấp và dữ liệu trùng."); }
    }

    public void suaNhanSu(NhanSu row) throws SQLException {
        validate(row);
        if (useSqlcmd()) { sqlcmd.suaNhanSu(row); return; }
        try (Connection c = open(); PreparedStatement s = c.prepareStatement(
                "UPDATE dbo.NhanSu SET TNS=?,TienLuong=?,MaCap=? WHERE MNS=?")) {
            s.setString(1, row.getTenNhanSu()); s.setInt(2, row.getTienLuong());
            s.setInt(3, row.getMaCap()); s.setInt(4, row.getMaNhanSu());
            if (s.executeUpdate() != 1) throw new SQLException("Không tìm thấy nhân sự cần sửa.");
        } catch (SQLException ex) { throw readable(ex, "Không thể sửa nhân sự. Kiểm tra cấp và dữ liệu."); }
    }

    public void xoaNhanSu(int maNhanSu) throws SQLException {
        if (maNhanSu <= 0 || maNhanSu > 99999) throw new IllegalArgumentException("Mã nhân sự phải từ 1 đến 99999 (tối đa 5 chữ số).");
        if (useSqlcmd()) { sqlcmd.xoaNhanSu(maNhanSu); return; }
        try (Connection c = open(); PreparedStatement s = c.prepareStatement(
                "DELETE FROM dbo.NhanSu WHERE MNS=?")) {
            s.setInt(1, maNhanSu); if (s.executeUpdate() != 1) throw new SQLException("Không tìm thấy nhân sự cần xóa.");
        }
    }

    public void themCap(Cap cap) throws SQLException {
        if (cap == null || cap.getMaCap() <= 0 || cap.getTen() == null || cap.getTen().trim().isEmpty())
            throw new IllegalArgumentException("Mã cấp và tên cấp không được để trống.");
        if (cap.getBaoHiem() < 0 || cap.getBaoHiem() > 100) throw new IllegalArgumentException("Bảo hiểm phải từ 0 đến 100%.");
        if (useSqlcmd()) { sqlcmd.themCap(cap); return; }
        try (Connection c = open(); PreparedStatement s = c.prepareStatement(
                "INSERT INTO dbo.Cap(MaCap,Ten,BaoHiem) VALUES(?,?,?)")) {
            s.setInt(1, cap.getMaCap()); s.setString(2, cap.getTen().trim()); s.setInt(3, cap.getBaoHiem()); s.executeUpdate();
        } catch (SQLException ex) { throw readable(ex, "Không thể thêm cấp. Mã hoặc tên cấp có thể đã tồn tại."); }
    }

    public void xoaCap(int maCap) throws SQLException {
        if (maCap <= 0) throw new IllegalArgumentException("Mã cấp không hợp lệ.");
        if (useSqlcmd()) { sqlcmd.xoaCap(maCap); return; }
        try (Connection c = open(); PreparedStatement s = c.prepareStatement("DELETE FROM dbo.Cap WHERE MaCap=?")) {
            s.setInt(1, maCap); if (s.executeUpdate() != 1) throw new SQLException("Không tìm thấy cấp cần xóa.");
        } catch (SQLException ex) { throw readable(ex, "Không thể xóa cấp đang được nhân sự sử dụng."); }
    }

    public static BigDecimal tienBaoHiem(NhanSu row) {
        return BigDecimal.valueOf(row.getTienLuong() + 100L)
                .multiply(BigDecimal.valueOf(row.getBaoHiem()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public static ThongKe thongKe(List<NhanSu> rows) {
        BigDecimal total = BigDecimal.ZERO;
        for (NhanSu row : rows) total = total.add(BigDecimal.valueOf(row.getTienLuong()).add(tienBaoHiem(row)));
        total = total.setScale(2, RoundingMode.HALF_UP);
        BigDecimal average = rows.isEmpty() ? BigDecimal.ZERO.setScale(2)
                : total.divide(BigDecimal.valueOf(rows.size()), 2, RoundingMode.HALF_UP);
        return new ThongKe(total, average);
    }

    private static void validate(NhanSu row) {
        if (row == null || row.getMaNhanSu() <= 0 || row.getMaNhanSu() > 99999)
            throw new IllegalArgumentException("Mã nhân sự phải từ 1 đến 99999 (tối đa 5 chữ số).");
        if (row.getTenNhanSu() == null || row.getTenNhanSu().trim().isEmpty()) throw new IllegalArgumentException("Tên nhân sự không được để trống.");
        if (row.getTienLuong() <= 0) throw new IllegalArgumentException("Tiền lương phải lớn hơn 0.");
        if (row.getMaCap() <= 0) throw new IllegalArgumentException("Hãy chọn một cấp.");
    }

    private static void bind(PreparedStatement s, NhanSu row) throws SQLException {
        s.setInt(1, row.getMaNhanSu()); s.setString(2, row.getTenNhanSu()); s.setInt(3, row.getTienLuong()); s.setInt(4, row.getMaCap());
    }

    private static SQLException readable(SQLException ex, String message) {
        ex.addSuppressed(new SQLException(message));
        return new SQLException(message, ex);
    }

    public static final class ThongKe {
        private final BigDecimal total;
        private final BigDecimal average;
        ThongKe(BigDecimal total, BigDecimal average) { this.total = total; this.average = average; }
        public BigDecimal getTotal() { return total; }
        public BigDecimal getAverage() { return average; }
    }
}
