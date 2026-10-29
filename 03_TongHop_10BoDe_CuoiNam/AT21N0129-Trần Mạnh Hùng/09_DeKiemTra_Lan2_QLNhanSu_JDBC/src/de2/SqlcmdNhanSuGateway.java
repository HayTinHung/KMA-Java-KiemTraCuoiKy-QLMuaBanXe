package de2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Cầu nối cho SQL Server cục bộ khi máy chưa bật TCP/IP. */
final class SqlcmdNhanSuGateway {
    private static final int TIMEOUT_SECONDS = 30;
    private static final int MAX_OUTPUT_BYTES = 16 * 1024 * 1024;
    private static final Pattern SQL_ERROR = Pattern.compile("(?m)\\bMsg\\s+(\\d+)\\b");
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();
    private final String server;

    SqlcmdNhanSuGateway(String server) {
        this.server = server == null || server.trim().isEmpty() ? ".\\SQL2025DEV" : server.trim();
    }

    void ping() throws SQLException {
        if (!"1".equals(singleValue("SELECT 1"))) {
            throw new SQLException("Không xác nhận được kết nối SQL Server.");
        }
    }

    List<NhanSu> listNhanSu() throws SQLException {
        String sql = "SELECT N.MNS, " + hex("N.TNS") + ", N.TienLuong, N.MaCap, "
                + hex("C.Ten") + ", C.BaoHiem FROM dbo.NhanSu N JOIN dbo.Cap C "
                + "ON N.MaCap = C.MaCap ORDER BY N.MNS";
        List<String[]> rows = query(sql, 6);
        List<NhanSu> result = new ArrayList<NhanSu>(rows.size());
        for (String[] row : rows) {
            result.add(new NhanSu(parseInt(row[0]), decode(row[1]), parseInt(row[2]),
                    parseInt(row[3]), decode(row[4]), parseInt(row[5])));
        }
        return result;
    }

    List<Cap> listCap() throws SQLException {
        List<String[]> rows = query("SELECT MaCap, " + hex("Ten") + ", BaoHiem FROM dbo.Cap ORDER BY MaCap", 3);
        List<Cap> result = new ArrayList<Cap>(rows.size());
        for (String[] row : rows) {
            result.add(new Cap(parseInt(row[0]), decode(row[1]), parseInt(row[2])));
        }
        return result;
    }

    void themNhanSu(NhanSu row) throws SQLException {
        expectOk("INSERT INTO dbo.NhanSu (MNS, TNS, TienLuong, MaCap) VALUES ("
                + row.getMaNhanSu() + ", " + unicode(row.getTenNhanSu()) + ", "
                + row.getTienLuong() + ", " + row.getMaCap() + "); SELECT 'OK'");
    }

    void suaNhanSu(NhanSu row) throws SQLException {
        int changed = parseInt(singleValue("UPDATE dbo.NhanSu SET TNS = " + unicode(row.getTenNhanSu())
                + ", TienLuong = " + row.getTienLuong() + ", MaCap = " + row.getMaCap()
                + " WHERE MNS = " + row.getMaNhanSu() + "; SELECT @@ROWCOUNT"));
        if (changed != 1) throw new SQLException("Không tìm thấy nhân sự cần sửa.");
    }

    void xoaNhanSu(int maNhanSu) throws SQLException {
        int changed = parseInt(singleValue("DELETE FROM dbo.NhanSu WHERE MNS = "
                + maNhanSu + "; SELECT @@ROWCOUNT"));
        if (changed != 1) throw new SQLException("Không tìm thấy nhân sự cần xóa.");
    }

    void themCap(Cap cap) throws SQLException {
        expectOk("INSERT INTO dbo.Cap (MaCap, Ten, BaoHiem) VALUES (" + cap.getMaCap() + ", "
                + unicode(cap.getTen()) + ", " + cap.getBaoHiem() + "); SELECT 'OK'");
    }

    void xoaCap(int maCap) throws SQLException {
        int changed = parseInt(singleValue("DELETE FROM dbo.Cap WHERE MaCap = " + maCap + "; SELECT @@ROWCOUNT"));
        if (changed != 1) throw new SQLException("Không tìm thấy cấp cần xóa.");
    }

    private String hex(String column) {
        return "CONVERT(varchar(400), CONVERT(varbinary(200), " + column + "), 2)";
    }

    private void expectOk(String sql) throws SQLException {
        if (!"OK".equals(singleValue(sql))) throw new SQLException("SQL Server không xác nhận thao tác.");
    }

    private String singleValue(String sql) throws SQLException {
        List<String[]> rows = query(sql, 1);
        if (rows.size() != 1) throw new SQLException("SQL Server trả về số dòng không mong đợi.");
        return rows.get(0)[0];
    }

    private List<String[]> query(String sql, int columns) throws SQLException {
        String output = run("SET NOCOUNT ON; SET XACT_ABORT ON; " + sql + ";");
        List<String[]> rows = new ArrayList<String[]>();
        for (String line : output.split("\\R")) {
            if (line.trim().isEmpty()) continue;
            String[] fields = line.split("\\|", -1);
            if (fields.length != columns) throw new SQLException("Định dạng dữ liệu sqlcmd không hợp lệ: " + line);
            for (int i = 0; i < fields.length; i++) fields[i] = fields[i].trim();
            rows.add(fields);
        }
        return rows;
    }

    private String run(String sql) throws SQLException {
        ProcessBuilder builder = new ProcessBuilder("sqlcmd", "-S", server, "-E", "-C", "-d", "QLNhanSu",
                "-b", "-V", "11", "-h", "-1", "-W", "-s", "|", "-w", "2000", "-Q", sql);
        builder.redirectErrorStream(true);
        final Process process;
        try {
            process = builder.start();
        } catch (IOException ex) {
            throw new SQLException("Không chạy được sqlcmd. Hãy kiểm tra sqlcmd trong PATH.", ex);
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        AtomicReference<IOException> readError = new AtomicReference<IOException>();
        Thread reader = new Thread(() -> {
            try (InputStream input = process.getInputStream()) {
                byte[] chunk = new byte[8192]; int count;
                while ((count = input.read(chunk)) != -1) {
                    if (buffer.size() + count > MAX_OUTPUT_BYTES) throw new IOException("Kết quả quá lớn.");
                    buffer.write(chunk, 0, count);
                }
            } catch (IOException ex) { readError.set(ex); }
        }, "de2-sqlcmd-reader");
        reader.setDaemon(true); reader.start();
        try {
            if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly(); reader.join(1000);
                throw new SQLException("sqlcmd quá thời gian chờ.");
            }
            reader.join(5000);
        } catch (InterruptedException ex) {
            process.destroyForcibly(); Thread.currentThread().interrupt();
            throw new SQLException("Đã ngắt khi chờ sqlcmd.", ex);
        }
        if (readError.get() != null) throw new SQLException("Không đọc được kết quả sqlcmd.", readError.get());
        String output = new String(buffer.toByteArray(), StandardCharsets.UTF_8).trim();
        if (process.exitValue() != 0) {
            Matcher matcher = SQL_ERROR.matcher(output);
            int code = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
            throw new SQLException("SQL Server: " + output, "S1000", code);
        }
        return output;
    }

    private static String unicode(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_16LE);
        StringBuilder result = new StringBuilder("CONVERT(nvarchar(50), 0x");
        for (byte b : bytes) result.append(HEX[(b >>> 4) & 0xF]).append(HEX[b & 0xF]);
        return result.append(')').toString();
    }

    private static String decode(String hex) throws SQLException {
        if ((hex.length() & 3) != 0) throw new SQLException("Chuỗi UTF-16 hex không hợp lệ.");
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int high = Character.digit(hex.charAt(2 * i), 16);
            int low = Character.digit(hex.charAt(2 * i + 1), 16);
            if (high < 0 || low < 0) throw new SQLException("Chuỗi hex không hợp lệ.");
            bytes[i] = (byte) ((high << 4) | low);
        }
        return new String(bytes, StandardCharsets.UTF_16LE);
    }

    private static int parseInt(String value) throws SQLException {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException ex) { throw new SQLException("Số nguyên SQL Server không hợp lệ: " + value, ex); }
    }
}
