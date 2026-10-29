package ontap2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL Server Shared Memory bridge for local installations with TCP/IP disabled.
 * All user text becomes UTF-16LE hex data, never SQL syntax or shell arguments.
 */
final class SqlcmdGateway {
    private static final int TIMEOUT_SECONDS = 30;
    private static final int MAX_OUTPUT_BYTES = 32 * 1024 * 1024;
    private static final Pattern SQL_ERROR = Pattern.compile("(?m)\\bMsg\\s+(\\d+)\\b");
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();
    private static final String HEX_THH = "CONVERT(varchar(200), CONVERT(varbinary(100), THH), 2)";
    private static final String HEX_TINH_CHAT =
            "CONVERT(varchar(200), CONVERT(varbinary(100), TinhChat), 2)";
    private static final String HEX_TLH = "CONVERT(varchar(200), CONVERT(varbinary(100), TLH), 2)";
    private static final String HEX_TEN_TINH_CHAT =
            "CONVERT(varchar(200), CONVERT(varbinary(100), TenTinhChat), 2)";

    private final String server;

    SqlcmdGateway(String server) {
        this.server = server == null || server.trim().isEmpty() ? ".\\SQL2025DEV" : server.trim();
    }

    void ping() throws SQLException {
        if (!"1".equals(singleValue("SELECT 1"))) {
            throw new SQLException("Không xác nhận được kết nối Shared Memory với SQL Server.");
        }
    }

    List<HangHoa> listHangHoa() throws SQLException {
        List<String[]> rows = query("SELECT MHH, " + HEX_THH + ", MaLoai, " + HEX_TINH_CHAT
                + " FROM dbo.HangHoa ORDER BY MHH", 4);
        List<HangHoa> result = new ArrayList<HangHoa>(rows.size());
        for (String[] row : rows) {
            result.add(new HangHoa(parseInt(row[0]), decode(row[1]), parseInt(row[2]), decode(row[3])));
        }
        return result;
    }

    List<LoaiHang> listLoaiHang() throws SQLException {
        List<String[]> rows = query("SELECT MaLoai, " + HEX_TLH
                + ", TyLe FROM dbo.LoaiHang ORDER BY MaLoai", 3);
        List<LoaiHang> result = new ArrayList<LoaiHang>(rows.size());
        for (String[] row : rows) {
            result.add(new LoaiHang(parseInt(row[0]), decode(row[1]), parseInt(row[2])));
        }
        return result;
    }

    List<String> listTinhChat() throws SQLException {
        List<String[]> rows = query("SELECT " + HEX_TEN_TINH_CHAT
                + " FROM dbo.TinhChat ORDER BY TenTinhChat", 1);
        List<String> result = new ArrayList<String>(rows.size());
        for (String[] row : rows) {
            result.add(decode(row[0]));
        }
        return result;
    }

    void themHangHoa(HangHoa row) throws SQLException {
        expectOk("INSERT INTO dbo.HangHoa (MHH, THH, MaLoai, TinhChat) VALUES ("
                + row.getMaHangHoa() + ", " + unicode(row.getTenHangHoa().trim()) + ", "
                + row.getMaLoai() + ", " + unicode(row.getTinhChat().trim()) + "); SELECT 'OK'");
    }

    void suaHangHoa(HangHoa row) throws SQLException {
        int changed = parseInt(singleValue("UPDATE dbo.HangHoa SET THH = "
                + unicode(row.getTenHangHoa().trim()) + ", MaLoai = " + row.getMaLoai()
                + ", TinhChat = " + unicode(row.getTinhChat().trim())
                + " WHERE MHH = " + row.getMaHangHoa() + "; SELECT @@ROWCOUNT"));
        if (changed != 1) {
            throw new SQLException("Không tìm thấy hàng hóa cần sửa.");
        }
    }

    void xoaHangHoa(int maHangHoa) throws SQLException {
        int changed = parseInt(singleValue("DELETE FROM dbo.HangHoa WHERE MHH = "
                + maHangHoa + "; SELECT @@ROWCOUNT"));
        if (changed != 1) {
            throw new SQLException("Không tìm thấy hàng hóa cần xóa.");
        }
    }

    LoaiHang themLoaiHang(String ten, int tyLe) throws SQLException {
        int maLoai = parseInt(singleValue("INSERT INTO dbo.LoaiHang (TLH, TyLe) VALUES ("
                + unicode(ten) + ", " + tyLe + "); SELECT CAST(SCOPE_IDENTITY() AS int)"));
        return new LoaiHang(maLoai, ten, tyLe);
    }

    void xoaLoaiHang(int maLoai) throws SQLException {
        int changed = parseInt(singleValue("DELETE FROM dbo.LoaiHang WHERE MaLoai = "
                + maLoai + "; SELECT @@ROWCOUNT"));
        if (changed != 1) {
            throw new SQLException("Không tìm thấy loại hàng cần xóa.");
        }
    }

    void themTinhChat(String ten) throws SQLException {
        expectOk("INSERT INTO dbo.TinhChat (TenTinhChat) VALUES (" + unicode(ten)
                + "); SELECT 'OK'");
    }

    void xoaTinhChat(String ten) throws SQLException {
        int changed = parseInt(singleValue("DELETE FROM dbo.TinhChat WHERE TenTinhChat = "
                + unicode(ten) + "; SELECT @@ROWCOUNT"));
        if (changed != 1) {
            throw new SQLException("Không tìm thấy tính chất cần xóa.");
        }
    }

    HangHoaRepository.ThongKe getThongKe() throws SQLException {
        String sql = "SELECT COUNT(*), COALESCE(SUM(CAST(1000 AS DECIMAL(18,2)) * "
                + "(1 + CAST(L.TyLe AS DECIMAL(10,4)) / 100)), 0) "
                + "FROM dbo.HangHoa H JOIN dbo.LoaiHang L ON H.MaLoai = L.MaLoai";
        List<String[]> rows = query(sql, 2);
        if (rows.size() != 1) {
            throw new SQLException("Không đọc được thống kê hàng hóa.");
        }
        try {
            int count = parseInt(rows.get(0)[0]);
            BigDecimal total = new BigDecimal(rows.get(0)[1]).setScale(2, RoundingMode.HALF_UP);
            BigDecimal average = count == 0 ? BigDecimal.ZERO.setScale(2)
                    : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            return new HangHoaRepository.ThongKe(count, total, average);
        } catch (NumberFormatException ex) {
            throw new SQLException("Giá trị thống kê từ SQL Server không hợp lệ.", ex);
        }
    }

    private void expectOk(String sql) throws SQLException {
        if (!"OK".equals(singleValue(sql))) {
            throw new SQLException("SQL Server không xác nhận thao tác lưu dữ liệu.");
        }
    }

    private String singleValue(String sql) throws SQLException {
        List<String[]> rows = query(sql, 1);
        if (rows.size() != 1) {
            throw new SQLException("SQL Server trả về số dòng không mong đợi: " + rows.size());
        }
        return rows.get(0)[0];
    }

    private List<String[]> query(String sql, int expectedColumns) throws SQLException {
        String output = run("SET NOCOUNT ON; SET XACT_ABORT ON; " + sql + ";");
        List<String[]> rows = new ArrayList<String[]>();
        for (String line : output.split("\\R")) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] fields = line.split("\\|", -1);
            if (fields.length != expectedColumns) {
                throw new SQLException("Định dạng dữ liệu từ sqlcmd không hợp lệ: " + line);
            }
            for (int i = 0; i < fields.length; i++) {
                fields[i] = fields[i].trim();
            }
            rows.add(fields);
        }
        return rows;
    }

    private String run(String sql) throws SQLException {
        ProcessBuilder builder = new ProcessBuilder("sqlcmd", "-S", server, "-E", "-C",
                "-d", "QLHangHoa", "-b", "-V", "11", "-h", "-1", "-W",
                "-s", "|", "-w", "2000", "-Q", sql);
        builder.redirectErrorStream(true);
        final Process process;
        try {
            process = builder.start();
        } catch (IOException ex) {
            throw new SQLException("Không chạy được sqlcmd. Kiểm tra sqlcmd đã có trong PATH.", ex);
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        AtomicReference<IOException> readError = new AtomicReference<IOException>();
        Thread reader = new Thread(() -> {
            try (InputStream input = process.getInputStream()) {
                byte[] chunk = new byte[8192];
                int count;
                while ((count = input.read(chunk)) != -1) {
                    if (buffer.size() + count > MAX_OUTPUT_BYTES) {
                        throw new IOException("Kết quả sqlcmd vượt quá 32 MiB.");
                    }
                    buffer.write(chunk, 0, count);
                }
            } catch (IOException ex) {
                readError.set(ex);
            }
        }, "ontap2-sqlcmd-reader");
        reader.setDaemon(true);
        reader.start();

        try {
            if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                reader.join(1000);
                throw new SQLException("sqlcmd quá thời gian chờ " + TIMEOUT_SECONDS + " giây.");
            }
            reader.join(5000);
        } catch (InterruptedException ex) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
            throw new SQLException("Đã ngắt khi chờ sqlcmd.", ex);
        }
        if (readError.get() != null) {
            throw new SQLException("Không đọc được kết quả từ sqlcmd.", readError.get());
        }
        String output = new String(buffer.toByteArray(), StandardCharsets.UTF_8).trim();
        if (process.exitValue() != 0) {
            Matcher matcher = SQL_ERROR.matcher(output);
            int code = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
            throw new SQLException("SQL Server: " + output, "S1000", code);
        }
        return output;
    }

    /** Unicode SQL literal expressed only as hex digits. */
    private static String unicode(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_16LE);
        StringBuilder result = new StringBuilder(30 + bytes.length * 2);
        result.append("CONVERT(nvarchar(50), 0x");
        for (byte b : bytes) {
            result.append(HEX[(b >>> 4) & 0xF]).append(HEX[b & 0xF]);
        }
        return result.append(')').toString();
    }

    private static String decode(String hex) throws SQLException {
        if ((hex.length() & 3) != 0) {
            throw new SQLException("Chuỗi UTF-16 hex từ SQL Server có độ dài sai.");
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            if (hi < 0 || lo < 0) {
                throw new SQLException("Chuỗi UTF-16 hex từ SQL Server không hợp lệ.");
            }
            bytes[i] = (byte) ((hi << 4) | lo);
        }
        return new String(bytes, StandardCharsets.UTF_16LE);
    }

    private static int parseInt(String value) throws SQLException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new SQLException("Số nguyên từ SQL Server không hợp lệ: " + value, ex);
        }
    }
}
