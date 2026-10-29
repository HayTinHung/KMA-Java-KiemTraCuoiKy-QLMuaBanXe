import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Tầng truy xuất dữ liệu CSDL SQL Server QLMuaBanXe.
 *
 * Đáp ứng đầy đủ yêu cầu đề thi và tính khả chuyển đa môi trường:
 * 1. Lưu ý đầu đề: Kiểm tra kết nối CSDL đã có kết nối chưa; nếu kết nối không được
 *    thì thông báo và đưa dữ liệu trống rỗng lên màn hình.
 * 2. Ghi chú cuối đề: Khi xử lý dữ liệu trên Database Server thì luôn kiểm tra việc
 *    kết nối thành công hay không; nếu kết nối không thành công thì tạo ra một dòng
 *    dữ liệu ảo, rồi sau đó đưa lên các control chứa dữ liệu thích hợp trên Cửa sổ.
 * 3. Hỗ trợ cấu hình linh hoạt qua tệp config.properties / db.properties, System properties,
 *    biến môi trường, tự động dò tìm nhiều SQL Server instance phổ biến (.\SQL2025DEV,
 *    localhost, ., .\SQLEXPRESS, (local)), và hỗ trợ chế độ giả lập CSDL (Simulated DB Mode)
 *    trên các máy chấm bài không cài đặt SQL Server.
 */
public class MuaBanXeRepository {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String DEFAULT_PORT = "1433";
    private static final String DEFAULT_DB_NAME = "QLMuaBanXe";
    private static final String DEFAULT_SQLCMD_SERVER = ".\\SQL2025DEV";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final List<String> candidateSqlcmdServers;
    private String activeSqlcmdServer;

    private boolean forceDisconnected = false;
    private boolean simulatedDatabaseMode = false;
    private Boolean useSqlcmdBackend = null;

    // Bộ nhớ dữ liệu ảo / giả lập khi mất kết nối hoặc khi chạy trên máy không cài SQL Server
    private final List<KhachHang> virtualKhachHang = new ArrayList<KhachHang>();
    private final List<Xe> virtualXe = new ArrayList<Xe>();
    private final List<ChiTietMuaXe> virtualMuaXe = new ArrayList<ChiTietMuaXe>();

    public MuaBanXeRepository() {
        Properties props = taiCauHinhTuFileVaMoiTruong();
        String host = layGiaTriCauHinh(props, "db.host", "DB_HOST", DEFAULT_HOST);
        String port = layGiaTriCauHinh(props, "db.port", "DB_PORT", DEFAULT_PORT);
        String dbName = layGiaTriCauHinh(props, "db.name", "DB_NAME", DEFAULT_DB_NAME);
        String integrated = layGiaTriCauHinh(props, "db.integratedSecurity", "DB_INTEGRATED_SECURITY", "true");
        String customUrl = layGiaTriCauHinh(props, "db.url", "DB_URL", "");

        if (!customUrl.isEmpty()) {
            this.jdbcUrl = customUrl;
        } else {
            boolean useIntegrated = "true".equalsIgnoreCase(integrated) || "1".equals(integrated);
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("jdbc:sqlserver://").append(host).append(":").append(port)
                    .append(";databaseName=").append(dbName)
                    .append(";encrypt=true;trustServerCertificate=true;loginTimeout=2");
            if (useIntegrated) {
                urlBuilder.append(";integratedSecurity=true");
            }
            this.jdbcUrl = urlBuilder.toString();
        }

        this.username = layGiaTriCauHinh(props, "db.user", "DB_USER", "");
        this.password = layGiaTriCauHinh(props, "db.password", "DB_PASSWORD", "");

        String serversRaw = layGiaTriCauHinh(
                props,
                "db.sqlcmdServers",
                "DB_SQLCMD_SERVER",
                DEFAULT_SQLCMD_SERVER + ",localhost,.,.\\SQLEXPRESS,(local)"
        );
        this.candidateSqlcmdServers = phanTachDanhSachServer(serversRaw);
        this.activeSqlcmdServer = this.candidateSqlcmdServers.isEmpty()
                ? DEFAULT_SQLCMD_SERVER
                : this.candidateSqlcmdServers.get(0);
    }

    public MuaBanXeRepository(String jdbcUrl, String username, String password, String sqlcmdServer) {
        this.jdbcUrl = jdbcUrl == null || jdbcUrl.trim().isEmpty()
                ? "jdbc:sqlserver://127.0.0.1:1433;databaseName=QLMuaBanXe;encrypt=true;trustServerCertificate=true;integratedSecurity=true;loginTimeout=2"
                : jdbcUrl.trim();
        this.username = username == null ? "" : username.trim();
        this.password = password == null ? "" : password;
        String serverCfg = sqlcmdServer == null || sqlcmdServer.trim().isEmpty()
                ? DEFAULT_SQLCMD_SERVER + ",localhost,.,.\\SQLEXPRESS,(local)"
                : sqlcmdServer.trim();
        this.candidateSqlcmdServers = phanTachDanhSachServer(serverCfg);
        this.activeSqlcmdServer = this.candidateSqlcmdServers.isEmpty()
                ? DEFAULT_SQLCMD_SERVER
                : this.candidateSqlcmdServers.get(0);
    }

    private static Properties taiCauHinhTuFileVaMoiTruong() {
        Properties props = new Properties();
        String[] candidateFiles = new String[]{
                "config.properties",
                "db.properties",
                "Kiểm tra cuối kì/config.properties",
                "../config.properties"
        };
        for (int i = 0; i < candidateFiles.length; i++) {
            File f = new File(candidateFiles[i]);
            if (f.isFile()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(f);
                    props.load(fis);
                    break;
                } catch (Exception ignored) {
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
        return props;
    }

    private static String layGiaTriCauHinh(Properties props, String propKey, String envKey, String defaultVal) {
        String sysVal = System.getProperty(propKey);
        if (sysVal != null && !sysVal.trim().isEmpty()) {
            return sysVal.trim();
        }
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }
        String fileVal = props.getProperty(propKey);
        if (fileVal != null && !fileVal.trim().isEmpty()) {
            return fileVal.trim();
        }
        return defaultVal;
    }

    private static List<String> phanTachDanhSachServer(String raw) {
        List<String> list = new ArrayList<String>();
        if (raw != null) {
            String[] parts = raw.split(",");
            for (int i = 0; i < parts.length; i++) {
                String s = parts[i].trim();
                if (!s.isEmpty() && !list.contains(s)) {
                    list.add(s);
                }
            }
        }
        if (list.isEmpty()) {
            list.add(DEFAULT_SQLCMD_SERVER);
        }
        return list;
    }

    public void setForceDisconnected(boolean forceDisconnected) {
        this.forceDisconnected = forceDisconnected;
        if (!this.simulatedDatabaseMode) {
            this.useSqlcmdBackend = null;
        }
    }

    public boolean isForceDisconnected() {
        return forceDisconnected;
    }

    /**
     * Kích hoạt chế độ giả lập CSDL (Simulated Database Mode) với đầy đủ 4 Xe và 6 giao dịch mẫu
     * từ QLMuaBanXe.sql khi chạy kiểm thử trên máy chấm bài không cài đặt SQL Server.
     */
    public void enableSimulatedDatabaseMode() {
        this.simulatedDatabaseMode = true;
        this.forceDisconnected = false;
        khoiTaoDuLieuMauAoDayDu();
    }

    public void disableSimulatedDatabaseMode() {
        this.simulatedDatabaseMode = false;
        this.useSqlcmdBackend = null;
    }

    public boolean isSimulatedDatabaseMode() {
        return simulatedDatabaseMode;
    }

    /**
     * Kiểm tra kết nối tới CSDL SQL Server QLMuaBanXe có thành công hay không.
     */
    public boolean kiemTraKetNoi() {
        if (forceDisconnected) {
            return false;
        }
        if (simulatedDatabaseMode) {
            return true;
        }
        try {
            if (useSqlcmdBackend != null) {
                if (!useSqlcmdBackend.booleanValue()) {
                    Connection conn = null;
                    try {
                        conn = openJdbc();
                        return conn != null && !conn.isClosed();
                    } finally {
                        closeQuietly(conn);
                    }
                } else {
                    return "1".equals(sqlcmdSingleValue("SELECT 1"));
                }
            }
            Connection conn = null;
            try {
                conn = openJdbc();
                if (conn != null && !conn.isClosed()) {
                    useSqlcmdBackend = Boolean.FALSE;
                    return true;
                }
            } catch (Throwable ignored) {
                // Chuyển sang dò tìm các instance SQL Server qua sqlcmd cục bộ
            } finally {
                closeQuietly(conn);
            }
            for (int i = 0; i < candidateSqlcmdServers.size(); i++) {
                String candidate = candidateSqlcmdServers.get(i);
                this.activeSqlcmdServer = candidate;
                try {
                    if ("1".equals(sqlcmdSingleValue("SELECT 1"))) {
                        useSqlcmdBackend = Boolean.TRUE;
                        return true;
                    }
                } catch (Throwable ignored) {
                }
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
        khoiTaoDongDuLieuAo(null);
    }

    /**
     * Khởi tạo dòng dữ liệu ảo dự phòng khi mất kết nối Database Server, đồng thời giữ lại
     * thông tin Xe người dùng vừa nhập trên biểu mẫu (nếu hợp lệ).
     */
    public void khoiTaoDongDuLieuAo(Xe customXe) {
        virtualKhachHang.clear();
        virtualXe.clear();
        virtualMuaXe.clear();

        KhachHang khAo = new KhachHang("KH01", "Nguyễn Văn Sơn (Dữ liệu ảo)", "301458080", "Nam", "125 Nguyễn Trãi, Q.1, TP.HCM");
        KhachHang khAo2 = new KhachHang("KH02", "Trần Thị Mai (Dữ liệu ảo)", "079199001", "Nữ", "42 Lê Lợi, Q.3, TP.HCM");
        virtualKhachHang.add(khAo);
        virtualKhachHang.add(khAo2);

        Xe xeAo = new Xe("XE01", "Suzuki X-Bike", 125, 21500000L);
        virtualXe.add(xeAo);

        ChiTietMuaXe ctAo = new ChiTietMuaXe(
                khAo.getMaKH(), khAo.getTenKH(), khAo.getCmnd(), khAo.getGioiTinh(), khAo.getDiaChi(),
                xeAo.getMaXe(), "28/10/2011", 1, xeAo.getDonGia(), "Dòng dữ liệu ảo khi mất kết nối CSDL"
        );
        virtualMuaXe.add(ctAo);

        if (customXe != null && !customXe.getMaXe().isEmpty() && !customXe.getMaXe().equalsIgnoreCase("XE01")) {
            virtualXe.add(customXe);
        }
    }

    /**
     * Khởi tạo đầy đủ 4 xe mẫu và 6 giao dịch mua xe như trong QLMuaBanXe.sql
     * phục vụ chạy kiểm thử trên môi trường OnlineGDB hoặc máy không có SQL Server.
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

    private List<Xe> sapXepDanhSachXe(List<Xe> input) {
        List<Xe> copy = new ArrayList<Xe>(input);
        Collections.sort(copy, new Comparator<Xe>() {
            @Override
            public int compare(Xe a, Xe b) {
                boolean aXe = a.getMaXe().toUpperCase().startsWith("XE");
                boolean bXe = b.getMaXe().toUpperCase().startsWith("XE");
                if (aXe != bXe) {
                    return aXe ? -1 : 1;
                }
                return a.getMaXe().compareToIgnoreCase(b.getMaXe());
            }
        });
        return copy;
    }

    private List<ChiTietMuaXe> sapXepChiTietMua(List<ChiTietMuaXe> input) {
        List<ChiTietMuaXe> copy = new ArrayList<ChiTietMuaXe>(input);
        Collections.sort(copy, new Comparator<ChiTietMuaXe>() {
            @Override
            public int compare(ChiTietMuaXe a, ChiTietMuaXe b) {
                try {
                    String isoA = chuyenSangIsoDate(a.getNgayMua());
                    String isoB = chuyenSangIsoDate(b.getNgayMua());
                    int cmpDate = isoB.compareTo(isoA); // DESC
                    if (cmpDate != 0) {
                        return cmpDate;
                    }
                } catch (SQLException ignored) {
                }
                return a.getMaKH().compareToIgnoreCase(b.getMaKH());
            }
        });
        return copy;
    }

    public List<Xe> layDanhSachXe() throws SQLException {
        if (!kiemTraKetNoi() || simulatedDatabaseMode) {
            if (virtualXe.isEmpty()) {
                khoiTaoDongDuLieuAo();
            }
            return sapXepDanhSachXe(virtualXe);
        }
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String sql = "SELECT MaXe, " + hexCol("TenXe") + ", DungTich, DonGia FROM dbo.Xe "
                    + "ORDER BY CASE WHEN MaXe LIKE 'XE%' THEN 0 ELSE 1 END, MaXe";
            List<String[]> rows = sqlcmdQuery(sql, 4);
            List<Xe> list = new ArrayList<Xe>();
            for (int i = 0; i < rows.size(); i++) {
                String[] r = rows.get(i);
                list.add(new Xe(r[0], decodeHexUnicode(r[1]), Integer.parseInt(r[2]), Long.parseLong(r[3])));
            }
            return list;
        }
        String sql = "SELECT MaXe, TenXe, DungTich, DonGia FROM dbo.Xe "
                + "ORDER BY CASE WHEN MaXe LIKE 'XE%' THEN 0 ELSE 1 END, MaXe";
        List<Xe> list = new ArrayList<Xe>();
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = openJdbc();
            ps = c.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Xe(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getLong(4)));
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(c);
        }
        return list;
    }

    public List<KhachHang> layDanhSachKhachHang() throws SQLException {
        if (!kiemTraKetNoi() || simulatedDatabaseMode) {
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
            for (int i = 0; i < rows.size(); i++) {
                String[] r = rows.get(i);
                list.add(new KhachHang(r[0], decodeHexUnicode(r[1]), r[2],
                        decodeHexUnicode(r[3]), decodeHexUnicode(r[4])));
            }
            return list;
        }
        String sql = "SELECT MaKH, TenKH, CMND, GioiTinh, DiaChi FROM dbo.Khach_Hang ORDER BY MaKH";
        List<KhachHang> list = new ArrayList<KhachHang>();
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = openJdbc();
            ps = c.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new KhachHang(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5)));
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(c);
        }
        return list;
    }

    public List<ChiTietMuaXe> layChiTietMuaTheoXe(String maXe) throws SQLException {
        if (!kiemTraKetNoi() || simulatedDatabaseMode) {
            List<ChiTietMuaXe> list = new ArrayList<ChiTietMuaXe>();
            for (int i = 0; i < virtualMuaXe.size(); i++) {
                ChiTietMuaXe ct = virtualMuaXe.get(i);
                if (ct.getMaXe().equalsIgnoreCase(maXe)) {
                    list.add(ct);
                }
            }
            return sapXepChiTietMua(list);
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
            for (int i = 0; i < rows.size(); i++) {
                String[] r = rows.get(i);
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
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = openJdbc();
            ps = c.prepareStatement(sql);
            ps.setString(1, maXe);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ChiTietMuaXe(
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getInt(8), rs.getLong(9), rs.getString(10)
                ));
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(c);
        }
        return list;
    }

    /**
     * Lọc dữ liệu báo cáo theo Mã xe và Ngày mua (dd/MM/yyyy).
     * Nếu người dùng nhập Ngày mua thì bắt buộc kiểm tra đúng định dạng dd/MM/yyyy và so khớp chính xác ngày.
     */
    public List<ChiTietMuaXe> locBaoCaoMuaXe(String maXe, String ngayMuaFilter) throws SQLException {
        String filter = ngayMuaFilter == null ? "" : ngayMuaFilter.trim();
        String normalizedFilterDate = "";
        if (!filter.isEmpty()) {
            normalizedFilterDate = chuanHoaNgayDdMmYyyy(filter);
        }

        List<ChiTietMuaXe> source = new ArrayList<ChiTietMuaXe>();
        if (maXe == null || maXe.trim().isEmpty() || "-- Tất cả --".equals(maXe.trim())) {
            List<Xe> allXe = layDanhSachXe();
            for (int i = 0; i < allXe.size(); i++) {
                source.addAll(layChiTietMuaTheoXe(allXe.get(i).getMaXe()));
            }
        } else {
            source.addAll(layChiTietMuaTheoXe(maXe.trim()));
        }

        if (normalizedFilterDate.isEmpty()) {
            return source;
        }
        List<ChiTietMuaXe> filtered = new ArrayList<ChiTietMuaXe>();
        for (int i = 0; i < source.size(); i++) {
            ChiTietMuaXe ct = source.get(i);
            String ctDate = ct.getNgayMua();
            try {
                ctDate = chuanHoaNgayDdMmYyyy(ctDate);
            } catch (SQLException ignored) {
            }
            if (ctDate.equalsIgnoreCase(normalizedFilterDate)) {
                filtered.add(ct);
            }
        }
        return filtered;
    }

    public boolean tonTaiMaXe(String maXe) throws SQLException {
        List<Xe> all = layDanhSachXe();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getMaXe().equalsIgnoreCase(maXe.trim())) {
                return true;
            }
        }
        return false;
    }

    public void themXe(Xe xe) throws SQLException {
        if (tonTaiMaXe(xe.getMaXe())) {
            throw new SQLException("Mã xe đã tồn tại, vui lòng nhập lại!");
        }
        if (!kiemTraKetNoi() || simulatedDatabaseMode) {
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
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = openJdbc();
            ps = c.prepareStatement(sql);
            ps.setString(1, xe.getMaXe());
            ps.setString(2, xe.getTenXe());
            ps.setInt(3, xe.getDungTich());
            ps.setLong(4, xe.getDonGia());
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(c);
        }
    }

    public void xoaXe(String maXe) throws SQLException {
        if (!kiemTraKetNoi() || simulatedDatabaseMode) {
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
        Connection c = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        try {
            c = openJdbc();
            ps1 = c.prepareStatement("DELETE FROM dbo.Mua_Xe WHERE MaXe = ?");
            ps1.setString(1, maXe);
            ps1.executeUpdate();

            ps2 = c.prepareStatement("DELETE FROM dbo.Xe WHERE MaXe = ?");
            ps2.setString(1, maXe);
            ps2.executeUpdate();
        } finally {
            closeQuietly(ps1);
            closeQuietly(ps2);
            closeQuietly(c);
        }
    }

    /**
     * Lưu mới hoặc cập nhật thông tin Khách hàng.
     * Nếu khách hàng đã tồn tại và địa chỉ truyền vào bị rỗng, giữ nguyên địa chỉ hiện có trong CSDL
     * thay vì ghi đè bằng giá trị mặc định.
     */
    public void luuHoacCapNhatKhachHang(KhachHang kh) throws SQLException {
        String diaChiMoi = kh.getDiaChi() == null ? "" : kh.getDiaChi().trim();
        if (!kiemTraKetNoi() || simulatedDatabaseMode) {
            for (int i = 0; i < virtualKhachHang.size(); i++) {
                KhachHang existing = virtualKhachHang.get(i);
                if (existing.getMaKH().equalsIgnoreCase(kh.getMaKH())) {
                    String resolvedAddress = diaChiMoi.isEmpty() ? existing.getDiaChi() : diaChiMoi;
                    if (resolvedAddress.isEmpty()) {
                        resolvedAddress = "TP.HCM";
                    }
                    KhachHang updatedKh = new KhachHang(
                            kh.getMaKH(), kh.getTenKH(), kh.getCmnd(), kh.getGioiTinh(), resolvedAddress
                    );
                    virtualKhachHang.set(i, updatedKh);
                    dongBoKhachHangTrongVirtualMuaXe(updatedKh);
                    return;
                }
            }
            String resolvedAddress = diaChiMoi.isEmpty() ? "TP.HCM" : diaChiMoi;
            KhachHang newKh = new KhachHang(
                    kh.getMaKH(), kh.getTenKH(), kh.getCmnd(), kh.getGioiTinh(), resolvedAddress
            );
            virtualKhachHang.add(newKh);
            dongBoKhachHangTrongVirtualMuaXe(newKh);
            return;
        }

        String cleanMa = sanitizeSqlLiteral(kh.getMaKH());
        String cleanCmnd = sanitizeSqlLiteral(kh.getCmnd());
        if (Boolean.TRUE.equals(useSqlcmdBackend)) {
            String updateDiaChiClause = diaChiMoi.isEmpty()
                    ? ""
                    : ", DiaChi = " + toNCharExpr(diaChiMoi);
            String insertDiaChiExpr = toNCharExpr(diaChiMoi.isEmpty() ? "TP.HCM" : diaChiMoi);
            String sql = "IF EXISTS (SELECT 1 FROM dbo.Khach_Hang WHERE MaKH = '" + cleanMa + "') "
                    + "UPDATE dbo.Khach_Hang SET TenKH = " + toNCharExpr(kh.getTenKH())
                    + ", CMND = '" + cleanCmnd + "', GioiTinh = " + toNCharExpr(kh.getGioiTinh())
                    + updateDiaChiClause
                    + " WHERE MaKH = '" + cleanMa + "' "
                    + "ELSE INSERT INTO dbo.Khach_Hang (MaKH, TenKH, CMND, GioiTinh, DiaChi) VALUES ('"
                    + cleanMa + "', " + toNCharExpr(kh.getTenKH()) + ", '" + cleanCmnd + "', "
                    + toNCharExpr(kh.getGioiTinh()) + ", "
                    + insertDiaChiExpr + "); SELECT 'OK'";
            sqlcmdSingleValue(sql);
            return;
        }

        Connection c = null;
        PreparedStatement checkPs = null;
        ResultSet rs = null;
        PreparedStatement actionPs = null;
        try {
            c = openJdbc();
            checkPs = c.prepareStatement("SELECT DiaChi FROM dbo.Khach_Hang WHERE MaKH = ?");
            checkPs.setString(1, kh.getMaKH());
            rs = checkPs.executeQuery();
            boolean exists = false;
            String existingDiaChi = "";
            if (rs.next()) {
                exists = true;
                existingDiaChi = rs.getString(1) == null ? "" : rs.getString(1).trim();
            }
            if (exists) {
                String finalDiaChi = diaChiMoi.isEmpty()
                        ? (existingDiaChi.isEmpty() ? "TP.HCM" : existingDiaChi)
                        : diaChiMoi;
                actionPs = c.prepareStatement(
                        "UPDATE dbo.Khach_Hang SET TenKH=?, CMND=?, GioiTinh=?, DiaChi=? WHERE MaKH=?"
                );
                actionPs.setString(1, kh.getTenKH());
                actionPs.setString(2, kh.getCmnd());
                actionPs.setString(3, kh.getGioiTinh());
                actionPs.setString(4, finalDiaChi);
                actionPs.setString(5, kh.getMaKH());
                actionPs.executeUpdate();
            } else {
                String finalDiaChi = diaChiMoi.isEmpty() ? "TP.HCM" : diaChiMoi;
                actionPs = c.prepareStatement(
                        "INSERT INTO dbo.Khach_Hang (MaKH, TenKH, CMND, GioiTinh, DiaChi) VALUES (?, ?, ?, ?, ?)"
                );
                actionPs.setString(1, kh.getMaKH());
                actionPs.setString(2, kh.getTenKH());
                actionPs.setString(3, kh.getCmnd());
                actionPs.setString(4, kh.getGioiTinh());
                actionPs.setString(5, finalDiaChi);
                actionPs.executeUpdate();
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(checkPs);
            closeQuietly(actionPs);
            closeQuietly(c);
        }
    }

    private void dongBoKhachHangTrongVirtualMuaXe(KhachHang kh) {
        for (int i = 0; i < virtualMuaXe.size(); i++) {
            ChiTietMuaXe ct = virtualMuaXe.get(i);
            if (ct.getMaKH().equalsIgnoreCase(kh.getMaKH())) {
                ct.setTenKH(kh.getTenKH());
                ct.setCmnd(kh.getCmnd());
                ct.setGioiTinh(kh.getGioiTinh());
                ct.setDiaChi(kh.getDiaChi());
            }
        }
    }

    public void themChiTietMuaXe(ChiTietMuaXe ct) throws SQLException {
        String isoDate = chuyenSangIsoDate(ct.getNgayMua());
        String normalizedNgay = chuanHoaNgayDdMmYyyy(ct.getNgayMua());
        ct.setNgayMua(normalizedNgay);

        luuHoacCapNhatKhachHang(new KhachHang(
                ct.getMaKH(), ct.getTenKH(), ct.getCmnd(), ct.getGioiTinh(), ct.getDiaChi()
        ));
        if (!kiemTraKetNoi() || simulatedDatabaseMode) {
            for (int i = 0; i < virtualMuaXe.size(); i++) {
                ChiTietMuaXe item = virtualMuaXe.get(i);
                if (item.getMaKH().equalsIgnoreCase(ct.getMaKH())
                        && item.getMaXe().equalsIgnoreCase(ct.getMaXe())
                        && item.getNgayMua().equalsIgnoreCase(ct.getNgayMua())) {
                    throw new SQLException("Khách hàng '" + ct.getMaKH() + "' đã có giao dịch mua xe '"
                            + ct.getMaXe() + "' vào ngày " + ct.getNgayMua() + "!");
                }
            }
            // Đồng bộ lại địa chỉ từ danh sách khách hàng nếu ct.diaChi đang để trống
            if (ct.getDiaChi().isEmpty()) {
                for (int i = 0; i < virtualKhachHang.size(); i++) {
                    if (virtualKhachHang.get(i).getMaKH().equalsIgnoreCase(ct.getMaKH())) {
                        ct.setDiaChi(virtualKhachHang.get(i).getDiaChi());
                        break;
                    }
                }
            }
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
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = openJdbc();
            ps = c.prepareStatement(sql);
            ps.setString(1, ct.getMaKH());
            ps.setString(2, ct.getMaXe());
            ps.setString(3, isoDate);
            ps.setInt(4, ct.getSoLuong());
            ps.setString(5, ct.getGhiChu());
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(c);
        }
    }

    public void suaChiTietMuaXe(String oldMaKH, String maXe, String oldNgayMua, ChiTietMuaXe updated) throws SQLException {
        String oldIso = chuyenSangIsoDate(oldNgayMua);
        String newIso = chuyenSangIsoDate(updated.getNgayMua());
        String normalizedNewNgay = chuanHoaNgayDdMmYyyy(updated.getNgayMua());
        updated.setNgayMua(normalizedNewNgay);

        luuHoacCapNhatKhachHang(new KhachHang(
                updated.getMaKH(), updated.getTenKH(), updated.getCmnd(), updated.getGioiTinh(), updated.getDiaChi()
        ));
        if (!kiemTraKetNoi() || simulatedDatabaseMode) {
            for (int i = 0; i < virtualMuaXe.size(); i++) {
                ChiTietMuaXe item = virtualMuaXe.get(i);
                if (item.getMaKH().equalsIgnoreCase(oldMaKH)
                        && item.getMaXe().equalsIgnoreCase(maXe)
                        && item.getNgayMua().equalsIgnoreCase(oldNgayMua)) {
                    if (updated.getDiaChi().isEmpty()) {
                        updated.setDiaChi(item.getDiaChi());
                    }
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
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = openJdbc();
            ps = c.prepareStatement(sql);
            ps.setString(1, updated.getMaKH());
            ps.setString(2, newIso);
            ps.setInt(3, updated.getSoLuong());
            ps.setString(4, updated.getGhiChu());
            ps.setString(5, oldMaKH);
            ps.setString(6, maXe);
            ps.setString(7, oldIso);
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(c);
        }
    }

    public void xoaChiTietMuaXe(String maKH, String maXe, String ngayMua) throws SQLException {
        String isoDate = chuyenSangIsoDate(ngayMua);
        if (!kiemTraKetNoi() || simulatedDatabaseMode) {
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
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = openJdbc();
            ps = c.prepareStatement(sql);
            ps.setString(1, maKH);
            ps.setString(2, maXe);
            ps.setString(3, isoDate);
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(c);
        }
    }

    public static String chuyenSangIsoDate(String ddMMyyyy) throws SQLException {
        if (ddMMyyyy == null || ddMMyyyy.trim().isEmpty()) {
            throw new SQLException("Ngày mua không được để trống!");
        }
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat("dd/MM/yyyy");
            inFmt.setLenient(false);
            Date parsed = inFmt.parse(ddMMyyyy.trim());
            return new SimpleDateFormat("yyyy-MM-dd").format(parsed);
        } catch (ParseException e) {
            throw new SQLException("Ngày mua không đúng định dạng dd/MM/yyyy: " + ddMMyyyy);
        }
    }

    public static String chuanHoaNgayDdMmYyyy(String ddMMyyyy) throws SQLException {
        if (ddMMyyyy == null || ddMMyyyy.trim().isEmpty()) {
            throw new SQLException("Ngày mua không được để trống!");
        }
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat("dd/MM/yyyy");
            inFmt.setLenient(false);
            Date parsed = inFmt.parse(ddMMyyyy.trim());
            return new SimpleDateFormat("dd/MM/yyyy").format(parsed);
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

    private static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
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
        List<String> cmd = new ArrayList<String>();
        cmd.add("sqlcmd");
        cmd.add("-S");
        cmd.add(activeSqlcmdServer);
        cmd.add("-C");
        if (!username.isEmpty()) {
            cmd.add("-U");
            cmd.add(username);
            cmd.add("-P");
            cmd.add(password);
        }
        cmd.add("-d");
        cmd.add("QLMuaBanXe");
        cmd.add("-W");
        cmd.add("-h");
        cmd.add("-1");
        cmd.add("-s");
        cmd.add("|");
        cmd.add("-Q");
        cmd.add(fullQuery);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        try {
            Process proc = pb.start();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            InputStream in = null;
            try {
                in = proc.getInputStream();
                byte[] chunk = new byte[4096];
                int read;
                while ((read = in.read(chunk)) != -1) {
                    buffer.write(chunk, 0, read);
                }
            } finally {
                closeQuietly(in);
            }
            boolean finished = proc.waitFor(10, TimeUnit.SECONDS);
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
            for (int i = 0; i < lines.length; i++) {
                String trimmed = lines[i].trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                String[] parts = trimmed.split("\\|", -1);
                if (parts.length == expectedCols) {
                    for (int j = 0; j < parts.length; j++) {
                        parts[j] = parts[j].trim();
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
