package ontap2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Đọc/ghi các bảng của QLHangHoa bằng JDBC. Mỗi thao tác dùng một kết nối ngắn,
 * nhờ vậy việc làm mới tự động trên giao diện không giữ khóa CSDL lâu.
 */
public final class HangHoaRepository {
    private static final String DEFAULT_URL =
            "jdbc:sqlserver://127.0.0.1:14333;databaseName=QLHangHoa;"
                    + "encrypt=true;trustServerCertificate=true;integratedSecurity=true;loginTimeout=3";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final boolean allowSqlcmdFallback;
    private final SqlcmdGateway sqlcmd;
    private volatile boolean backendSelected;
    private volatile boolean sqlcmdSelected;

    public HangHoaRepository(String jdbcUrl, String username, String password) {
        this(jdbcUrl, username, password, false);
    }

    private HangHoaRepository(String jdbcUrl, String username, String password,
                              boolean allowSqlcmdFallback) {
        if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("JDBC URL không được để trống.");
        }
        this.jdbcUrl = jdbcUrl.trim();
        this.username = username == null ? "" : username.trim();
        this.password = password == null ? "" : password;
        this.allowSqlcmdFallback = allowSqlcmdFallback && this.username.isEmpty();
        this.sqlcmd = new SqlcmdGateway(System.getenv("ONTAP2_SQLCMD_SERVER"));
    }

    /**
     * ONTAP2_DB_URL, ONTAP2_DB_USER, ONTAP2_DB_PASSWORD có thể cấu hình trước khi chạy.
     * Mặc định dùng Windows Authentication cho SQL Server cục bộ tại cổng 14333.
     */
    public static HangHoaRepository fromEnvironment() {
        String url = System.getenv("ONTAP2_DB_URL");
        boolean useDefaultUrl = url == null || url.trim().isEmpty();
        return new HangHoaRepository(useDefaultUrl ? DEFAULT_URL : url,
                System.getenv("ONTAP2_DB_USER"), System.getenv("ONTAP2_DB_PASSWORD"),
                useDefaultUrl);
    }

    private Connection openConnection() throws SQLException {
        if (username.isEmpty()) {
            return DriverManager.getConnection(jdbcUrl);
        }
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /** Select backend once; SQL commands are used only for this local default configuration. */
    private boolean shouldUseSqlcmd() throws SQLException {
        if (backendSelected) {
            return sqlcmdSelected;
        }
        synchronized (this) {
            if (backendSelected) {
                return sqlcmdSelected;
            }
            try (Connection ignored = openConnection()) {
                sqlcmdSelected = false;
                backendSelected = true;
                return false;
            } catch (SQLException jdbcError) {
                if (!allowSqlcmdFallback) {
                    throw jdbcError;
                }
                try {
                    sqlcmd.ping();
                    sqlcmdSelected = true;
                    backendSelected = true;
                    return true;
                } catch (SQLException sqlcmdError) {
                    sqlcmdError.addSuppressed(jdbcError);
                    throw sqlcmdError;
                }
            }
        }
    }

    public List<HangHoa> listHangHoa() throws SQLException {
        if (shouldUseSqlcmd()) {
            return sqlcmd.listHangHoa();
        }
        String sql = "SELECT MHH, THH, MaLoai, TinhChat FROM dbo.HangHoa ORDER BY MHH";
        List<HangHoa> result = new ArrayList<HangHoa>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                result.add(new HangHoa(rows.getInt("MHH"), rows.getString("THH"),
                        rows.getInt("MaLoai"), rows.getString("TinhChat")));
            }
        }
        return result;
    }

    public List<LoaiHang> listLoaiHang() throws SQLException {
        if (shouldUseSqlcmd()) {
            return sqlcmd.listLoaiHang();
        }
        String sql = "SELECT MaLoai, TLH, TyLe FROM dbo.LoaiHang ORDER BY MaLoai";
        List<LoaiHang> result = new ArrayList<LoaiHang>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                result.add(new LoaiHang(rows.getInt("MaLoai"), rows.getString("TLH"),
                        rows.getInt("TyLe")));
            }
        }
        return result;
    }

    public List<String> listTinhChat() throws SQLException {
        if (shouldUseSqlcmd()) {
            return sqlcmd.listTinhChat();
        }
        String sql = "SELECT TenTinhChat FROM dbo.TinhChat ORDER BY TenTinhChat";
        List<String> result = new ArrayList<String>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                result.add(rows.getString("TenTinhChat"));
            }
        }
        return result;
    }

    public void themHangHoa(HangHoa hangHoa) throws SQLException {
        validateHangHoa(hangHoa);
        if (shouldUseSqlcmd()) {
            try {
                sqlcmd.themHangHoa(hangHoa);
                return;
            } catch (SQLException ex) {
                throw readableConstraint(ex, "Không thể thêm hàng hóa. Kiểm tra mã hàng, loại hàng và tính chất.");
            }
        }
        String sql = "INSERT INTO dbo.HangHoa (MHH, THH, MaLoai, TinhChat) VALUES (?, ?, ?, ?)";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindHangHoa(statement, hangHoa);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw readableConstraint(ex, "Không thể thêm hàng hóa. Kiểm tra mã hàng, loại hàng và tính chất.");
        }
    }

    public void suaHangHoa(HangHoa hangHoa) throws SQLException {
        validateHangHoa(hangHoa);
        if (shouldUseSqlcmd()) {
            try {
                sqlcmd.suaHangHoa(hangHoa);
                return;
            } catch (SQLException ex) {
                throw readableConstraint(ex, "Không thể sửa hàng hóa. Kiểm tra loại hàng và tính chất.");
            }
        }
        String sql = "UPDATE dbo.HangHoa SET THH = ?, MaLoai = ?, TinhChat = ? WHERE MHH = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hangHoa.getTenHangHoa().trim());
            statement.setInt(2, hangHoa.getMaLoai());
            statement.setString(3, hangHoa.getTinhChat().trim());
            statement.setInt(4, hangHoa.getMaHangHoa());
            requireOneRow(statement.executeUpdate(), "Không tìm thấy hàng hóa cần sửa.");
        } catch (SQLException ex) {
            throw readableConstraint(ex, "Không thể sửa hàng hóa. Kiểm tra loại hàng và tính chất.");
        }
    }

    public void xoaHangHoa(int maHangHoa) throws SQLException {
        validateMaHangHoa(maHangHoa);
        if (shouldUseSqlcmd()) {
            sqlcmd.xoaHangHoa(maHangHoa);
            return;
        }
        String sql = "DELETE FROM dbo.HangHoa WHERE MHH = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maHangHoa);
            requireOneRow(statement.executeUpdate(), "Không tìm thấy hàng hóa cần xóa.");
        }
    }

    /** Mã loại do SQL Server tự sinh, trả về để giao diện chọn ngay loại vừa thêm. */
    public LoaiHang themLoaiHang(String tenLoaiHang, int tyLe) throws SQLException {
        String ten = requiredText(tenLoaiHang, "Tên loại hàng");
        if (tyLe < 0 || tyLe > 100) {
            throw new IllegalArgumentException("Tỷ lệ phải từ 0 đến 100%.");
        }
        if (shouldUseSqlcmd()) {
            try {
                return sqlcmd.themLoaiHang(ten, tyLe);
            } catch (SQLException ex) {
                throw readableConstraint(ex, "Không thể thêm loại hàng. Tên loại hàng có thể đã tồn tại.");
            }
        }
        String sql = "INSERT INTO dbo.LoaiHang (TLH, TyLe) OUTPUT INSERTED.MaLoai VALUES (?, ?)";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ten);
            statement.setInt(2, tyLe);
            try (ResultSet row = statement.executeQuery()) {
                if (!row.next()) {
                    throw new SQLException("SQL Server không trả về mã loại hàng mới.");
                }
                return new LoaiHang(row.getInt(1), ten, tyLe);
            }
        } catch (SQLException ex) {
            throw readableConstraint(ex, "Không thể thêm loại hàng. Tên loại hàng có thể đã tồn tại.");
        }
    }

    public void xoaLoaiHang(int maLoai) throws SQLException {
        if (maLoai <= 0) {
            throw new IllegalArgumentException("Mã loại hàng phải lớn hơn 0.");
        }
        if (shouldUseSqlcmd()) {
            try {
                sqlcmd.xoaLoaiHang(maLoai);
                return;
            } catch (SQLException ex) {
                throw readableConstraint(ex, "Loại hàng đang được sử dụng, hãy xử lý hàng hóa thuộc loại này trước.");
            }
        }
        String sql = "DELETE FROM dbo.LoaiHang WHERE MaLoai = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maLoai);
            requireOneRow(statement.executeUpdate(), "Không tìm thấy loại hàng cần xóa.");
        } catch (SQLException ex) {
            throw readableConstraint(ex, "Loại hàng đang được sử dụng, hãy xử lý hàng hóa thuộc loại này trước.");
        }
    }

    public void themTinhChat(String tinhChat) throws SQLException {
        String ten = requiredText(tinhChat, "Tính chất");
        if (shouldUseSqlcmd()) {
            try {
                sqlcmd.themTinhChat(ten);
                return;
            } catch (SQLException ex) {
                throw readableConstraint(ex, "Không thể thêm tính chất. Tên tính chất có thể đã tồn tại.");
            }
        }
        String sql = "INSERT INTO dbo.TinhChat (TenTinhChat) VALUES (?)";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ten);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw readableConstraint(ex, "Không thể thêm tính chất. Tên tính chất có thể đã tồn tại.");
        }
    }

    public void xoaTinhChat(String tinhChat) throws SQLException {
        String ten = requiredText(tinhChat, "Tính chất");
        if (shouldUseSqlcmd()) {
            try {
                sqlcmd.xoaTinhChat(ten);
                return;
            } catch (SQLException ex) {
                throw readableConstraint(ex, "Tính chất đang được sử dụng, hãy xử lý hàng hóa có tính chất này trước.");
            }
        }
        String sql = "DELETE FROM dbo.TinhChat WHERE TenTinhChat = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ten);
            requireOneRow(statement.executeUpdate(), "Không tìm thấy tính chất cần xóa.");
        } catch (SQLException ex) {
            throw readableConstraint(ex, "Tính chất đang được sử dụng, hãy xử lý hàng hóa có tính chất này trước.");
        }
    }

    /** Tổng = tổng của từng hàng: 1000 * (1 + TyLe/100). */
    public ThongKe getThongKe() throws SQLException {
        if (shouldUseSqlcmd()) {
            return sqlcmd.getThongKe();
        }
        String sql = "SELECT COUNT(*) AS SoHangHoa, "
                + "COALESCE(SUM(CAST(1000 AS DECIMAL(18,2)) * "
                + "(1 + CAST(L.TyLe AS DECIMAL(10,4)) / 100)), 0) AS TongGiaTri "
                + "FROM dbo.HangHoa H JOIN dbo.LoaiHang L ON H.MaLoai = L.MaLoai";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery()) {
            row.next();
            int count = row.getInt("SoHangHoa");
            BigDecimal total = row.getBigDecimal("TongGiaTri").setScale(2, RoundingMode.HALF_UP);
            BigDecimal average = count == 0 ? BigDecimal.ZERO.setScale(2)
                    : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            return new ThongKe(count, total, average);
        }
    }

    /** Xuất 4 cột của HangHoa dưới dạng UTF-8, phân tách bằng tab. */
    public void xuatHangHoa(Path outputFile) throws SQLException, IOException {
        if (outputFile == null) {
            throw new IllegalArgumentException("Chưa chọn tệp xuất.");
        }
        List<HangHoa> rows = listHangHoa();
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
            writer.write("MHH\tTHH\tMaLoai\tTinhChat");
            writer.newLine();
            for (HangHoa row : rows) {
                writer.write(Integer.toString(row.getMaHangHoa()));
                writer.write('\t');
                writer.write(plainCell(row.getTenHangHoa()));
                writer.write('\t');
                writer.write(Integer.toString(row.getMaLoai()));
                writer.write('\t');
                writer.write(plainCell(row.getTinhChat()));
                writer.newLine();
            }
        }
    }

    private static String plainCell(String value) {
        return value.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
    }

    private static void bindHangHoa(PreparedStatement statement, HangHoa hangHoa) throws SQLException {
        statement.setInt(1, hangHoa.getMaHangHoa());
        statement.setString(2, hangHoa.getTenHangHoa().trim());
        statement.setInt(3, hangHoa.getMaLoai());
        statement.setString(4, hangHoa.getTinhChat().trim());
    }

    private static void validateHangHoa(HangHoa hangHoa) {
        if (hangHoa == null) {
            throw new IllegalArgumentException("Hàng hóa không được để trống.");
        }
        validateMaHangHoa(hangHoa.getMaHangHoa());
        requiredText(hangHoa.getTenHangHoa(), "Tên hàng hóa");
        if (hangHoa.getMaLoai() <= 0) {
            throw new IllegalArgumentException("Phải chọn loại hàng.");
        }
        requiredText(hangHoa.getTinhChat(), "Tính chất");
    }

    private static void validateMaHangHoa(int maHangHoa) {
        if (maHangHoa < 10000 || maHangHoa > 99999) {
            throw new IllegalArgumentException("Mã hàng hóa phải là số nguyên gồm đúng 5 chữ số.");
        }
    }

    private static String requiredText(String text, String label) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " không được để trống.");
        }
        String value = text.trim();
        if (value.length() > 50) {
            throw new IllegalArgumentException(label + " không được vượt quá 50 ký tự.");
        }
        return value;
    }

    private static void requireOneRow(int affected, String message) throws SQLException {
        if (affected != 1) {
            throw new SQLException(message);
        }
    }

    private static SQLException readableConstraint(SQLException ex, String message) {
        int error = ex.getErrorCode();
        if (error == 2601 || error == 2627 || error == 547 || error == 2628 || error == 8152) {
            return new SQLException(message, ex.getSQLState(), error, ex);
        }
        return ex;
    }

    public static final class ThongKe {
        private final int soHangHoa;
        private final BigDecimal tongGiaTri;
        private final BigDecimal trungBinh;

        public ThongKe(int soHangHoa, BigDecimal tongGiaTri, BigDecimal trungBinh) {
            this.soHangHoa = soHangHoa;
            this.tongGiaTri = tongGiaTri;
            this.trungBinh = trungBinh;
        }

        public int getSoHangHoa() { return soHangHoa; }
        public BigDecimal getTongGiaTri() { return tongGiaTri; }
        public BigDecimal getTrungBinh() { return trungBinh; }
    }
}
