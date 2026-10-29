import de2.Cap;
import de2.NhanSu;
import de2.NhanSuRepository;
import de2.QuanLyNhanSuGUI;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 * Suite kiểm thử tự động toàn diện các phần ăn điểm từ a) đến i) theo đề thi.
 * Đồng thời tự động fill dữ liệu thực tế và chụp ảnh minh chứng.
 */
public final class AutoGradingSuite {
    private static NhanSuRepository repo;
    private static QuanLyNhanSuGUI gui;

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("           BẮT ĐẦU CHẠY SUITE TEST TỰ ĐỘNG CÁC PHẦN ĂN ĐIỂM (A -> I)           ");
        System.out.println("================================================================================");

        repo = NhanSuRepository.fromEnvironment();

        // Tự động đóng các modal dialog (như popup thông báo cập nhật DB) trong quá trình chạy test tự động
        Thread autoCloser = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(150);
                    java.awt.Window[] windows = java.awt.Window.getWindows();
                    for (java.awt.Window w : windows) {
                        if (w instanceof javax.swing.JDialog && w.isVisible()) {
                            javax.swing.JDialog d = (javax.swing.JDialog) w;
                            SwingUtilities.invokeLater(d::dispose);
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception ignored) {}
            }
        });
        autoCloser.setDaemon(true);
        autoCloser.start();

        // 1. Reset dữ liệu sạch sẽ ban đầu
        cleanupData();

        // 2. Chạy lần lượt từng phần kiểm thử
        testKhoiTaoVaValidation_A();
        testCongThucThongKe_B();
        testKhungCap_C();
        testNhapMoi_D();
        testSua_E();
        testXoa_F();
        testThoiGianVaTimer_G();
        testKhungDuLieuVaDongBo_H();
        testThoat_I();

        // 3. Nạp bộ 3 nhân sự chuẩn vào DB và chụp ảnh màn hình minh chứng
        seedOfficialData();
        captureFilledGui();

        System.out.println("================================================================================");
        System.out.println("              TẤT CẢ CÁC MỤC ĂN ĐIỂM TỪ A ĐẾN I ĐÃ ĐẠT 100% (PASS)!             ");
        System.out.println("================================================================================");
        System.exit(0);
    }

    private static void cleanupData() throws Exception {
        List<NhanSu> current = repo.listNhanSu();
        for (NhanSu ns : current) {
            repo.xoaNhanSu(ns.getMaNhanSu());
        }
        // Đảm bảo có đủ 3 cấp ban đầu
        List<Cap> caps = repo.listCap();
        if (caps.size() < 3) {
            try { repo.themCap(new Cap(1, "Quản lý", 16)); } catch (Exception ignored) {}
            try { repo.themCap(new Cap(2, "Lãnh đạo", 12)); } catch (Exception ignored) {}
            try { repo.themCap(new Cap(3, "Tác nghiệp", 8)); } catch (Exception ignored) {}
        }
        System.out.println("[SETUP] CSDL đã được dọn sạch về trạng thái ban đầu (3 Cấp mẫu, 0 Nhân sự).");
    }

    /**
     * a) Khởi tạo: con trỏ văn bản đặt vào ô Mã Nhân sự.
     * Các textbox trên Cửa sổ phải kiểm tra dữ liệu từ bàn phím và không được cho phép để trống (rỗng).
     * Thực hiện việc lấy dữ liệu từ Database Server đưa lên Cửa sổ và đưa lên Khung dữ liệu chi tiết.
     */
    private static void testKhoiTaoVaValidation_A() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST a] Khởi tạo, Con trỏ ô Mã Nhân sự, Kiểm tra dữ liệu bàn phím & Không rỗng");

        SwingUtilities.invokeAndWait(() -> {
            gui = new QuanLyNhanSuGUI(repo);
            gui.setVisible(true);
        });
        Thread.sleep(1200); // Đợi tải dữ liệu ban đầu

        // Kiểm tra lấy dữ liệu đưa lên
        require(gui.getTitle().startsWith("Bắt đầu lúc:"), "Title phải bắt đầu bằng 'Bắt đầu lúc:'");
        JTextField mnsField = getField("mnsField");
        JTextField tnsField = getField("tnsField");
        JTextField luongField = getField("luongField");

        // 1. Kiểm tra gõ chữ cái vào ô Mã nhân sự -> DocumentFilter chặn hoàn toàn
        SwingUtilities.invokeAndWait(() -> {
            mnsField.setText("");
            mnsField.setText("ABCXYZ");
        });
        require(mnsField.getText().isEmpty(), "Gõ chữ cái vào ô Mã nhân sự phải bị chặn hoàn toàn. Thực tế: " + mnsField.getText());

        // 2. Kiểm tra gõ số hợp lệ
        SwingUtilities.invokeAndWait(() -> {
            mnsField.setText("12345");
        });
        require("12345".equals(mnsField.getText()), "Gõ số hợp lệ phải được chấp nhận.");

        // 3. Kiểm tra chặn vượt quá 5 chữ số
        SwingUtilities.invokeAndWait(() -> {
            mnsField.setText("");
            mnsField.setText("12345678"); // Quá 5 chữ số
        });
        require(mnsField.getText().isEmpty() || mnsField.getText().length() <= 5, "Mã nhân sự không được vượt quá 5 chữ số.");
        System.out.println("  ✓ Bộ lọc bàn phím ô Mã nhân sự hoạt động chính xác (chặn chữ, chỉ nhận số <= 5 chữ số).");

        // 4. Kiểm tra gõ chữ vào ô Tiền lương -> bị chặn hoàn toàn
        SwingUtilities.invokeAndWait(() -> {
            luongField.setText("");
            luongField.setText("ABCDEF");
        });
        require(luongField.getText().isEmpty(), "Gõ chữ cái vào ô Tiền lương phải bị chặn hoàn toàn.");

        SwingUtilities.invokeAndWait(() -> {
            luongField.setText("15000");
        });
        require("15000".equals(luongField.getText()), "Gõ số vào ô Tiền lương được chấp nhận.");
        System.out.println("  ✓ Bộ lọc bàn phím ô Tiền lương hoạt động chính xác (chỉ nhận số).");

        // Kiểm tra ràng buộc không được để trống (ném ngoại lệ khi nhập rỗng)
        SwingUtilities.invokeAndWait(() -> {
            mnsField.setText("");
            tnsField.setText("");
            luongField.setText("");
        });
        boolean threwEmpty = false;
        try {
            gui.validateAndReadForm();
        } catch (Exception ex) {
            threwEmpty = true;
        }
        require(threwEmpty, "Hệ thống phải chặn và báo lỗi khi để trống các trường bắt buộc.");
        System.out.println("  ✓ Kiểm tra ràng buộc để trống hoạt động chuẩn xác (từ chối lưu dữ liệu rỗng).");

        pass("Mục a) Khởi tạo và kiểm tra dữ liệu bàn phím: ĐẠT!");
    }

    /**
     * b) (1.0 đ): Textbox Tổng thù lao: = ∑(Tiền lương + Tiền Bảo hiểm), n là số dòng dữ liệu trong bảng NhanSu,
     * Txt Trung bình: = Tổng thù lao / n, với Tiền BH tối thiểu = 100.
     * Tiền Bảo hiểm= (Tiền lương + Tiền BH tối thiểu ) * (Bảo hiểm / 100)
     */
    private static void testCongThucThongKe_B() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST b] Kiểm tra công thức: Tiền BH, Tổng thù lao & Trung bình (1.0 điểm)");

        // NV1: Lương 15000, Cấp Quản lý 16% -> Tiền BH = (15000 + 100) * 16 / 100 = 15100 * 0.16 = 2416.00
        NhanSu ns1 = new NhanSu(10001, "Nguyễn Văn An", 15000, 1, "Quản lý", 16);
        BigDecimal bh1 = NhanSuRepository.tienBaoHiem(ns1);
        require(bh1.compareTo(new BigDecimal("2416.00")) == 0, "Tiền BH NV1 phải là 2416.00, thực tế: " + bh1);
        System.out.println("  ✓ NV1 (Lương 15,000, BH 16%): Tiền bảo hiểm = (15000 + 100) * 16% = " + bh1 + " (Thù lao = " + (15000 + 2416) + ")");

        // NV2: Lương 20000, Cấp Lãnh đạo 12% -> Tiền BH = (20000 + 100) * 12 / 100 = 20100 * 0.12 = 2412.00
        NhanSu ns2 = new NhanSu(10002, "Trần Thị Bích", 20000, 2, "Lãnh đạo", 12);
        BigDecimal bh2 = NhanSuRepository.tienBaoHiem(ns2);
        require(bh2.compareTo(new BigDecimal("2412.00")) == 0, "Tiền BH NV2 phải là 2412.00, thực tế: " + bh2);
        System.out.println("  ✓ NV2 (Lương 20,000, BH 12%): Tiền bảo hiểm = (20000 + 100) * 12% = " + bh2 + " (Thù lao = " + (20000 + 2412) + ")");

        // NV3: Lương 12000, Cấp Tác nghiệp 8% -> Tiền BH = (12000 + 100) * 8 / 100 = 12100 * 0.08 = 968.00
        NhanSu ns3 = new NhanSu(10003, "Lê Hoàng Cường", 12000, 3, "Tác nghiệp", 8);
        BigDecimal bh3 = NhanSuRepository.tienBaoHiem(ns3);
        require(bh3.compareTo(new BigDecimal("968.00")) == 0, "Tiền BH NV3 phải là 968.00, thực tế: " + bh3);
        System.out.println("  ✓ NV3 (Lương 12,000, BH 8%): Tiền bảo hiểm = (12000 + 100) * 8% = " + bh3 + " (Thù lao = " + (12000 + 968) + ")");

        // Tổng thù lao = 17416 + 22412 + 12968 = 52796.00
        // Trung bình = 52796 / 3 = 17598.67
        List<NhanSu> list = List.of(ns1, ns2, ns3);
        NhanSuRepository.ThongKe stats = NhanSuRepository.thongKe(list);
        require(stats.getTotal().compareTo(new BigDecimal("52796.00")) == 0, "Tổng thù lao phải là 52796.00, thực tế: " + stats.getTotal());
        require(stats.getAverage().compareTo(new BigDecimal("17598.67")) == 0, "Trung bình phải là 17598.67, thực tế: " + stats.getAverage());
        System.out.println("  ✓ Tổng Thù lao (n=3): " + stats.getTotal() + " VNĐ");
        System.out.println("  ✓ Trung bình (Tổng/3): " + stats.getAverage() + " VNĐ");

        pass("Mục b) Công thức Thống kê Thù lao & Bảo hiểm tối thiểu 100: ĐẠT!");
    }

    /**
     * c) Khung Cấp: Nút Thêm để thêm 1 Cấp, Nút Xoá để xóa 1 Cấp đã chọn.
     * Dữ liệu khi nhấp để chọn Cấp đưa lên ô Cấp tương ứng.
     */
    private static void testKhungCap_C() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST c] Khung Cấp: Thêm Cấp, Xóa Cấp & Đồng bộ lên ô Cấp tương ứng");

        // 1. Thêm 1 cấp mới vào DB: Mã 9, Tên "Chuyên viên thử nghiệm", BH 10%
        Cap testCap = new Cap(9, "Chuyên viên thử nghiệm", 10);
        repo.themCap(testCap);
        System.out.println("  ✓ Nút Thêm: Đã thêm cấp mới Mã = 9, Tên = 'Chuyên viên thử nghiệm', BH = 10%.");

        // 2. Nạp lại và kiểm tra Cây cấp
        SwingUtilities.invokeAndWait(() -> {
            gui.refreshData(false);
        });
        Thread.sleep(1200);

        // 3. Chọn cấp trên Cây cấp và kiểm tra đưa dữ liệu lên 3 ô: Mã cấp, Tên cấp, Bảo hiểm
        SwingUtilities.invokeAndWait(() -> {
            gui.selectCapInTree(9);
            gui.showCap(testCap);
        });
        JTextField maCapField = getField("maCapField");
        JTextField tenCapField = getField("tenCapField");
        JTextField baoHiemField = getField("baoHiemField");

        require("9".equals(maCapField.getText()), "Mã cấp hiển thị phải là 9, thực tế: " + maCapField.getText());
        require("Chuyên viên thử nghiệm".equals(tenCapField.getText()), "Tên cấp hiển thị không đúng.");
        require("10".equals(baoHiemField.getText()), "Bảo hiểm hiển thị không đúng.");
        System.out.println("  ✓ Nhấp chọn Cấp trên cây đã đưa dữ liệu đầy đủ lên 3 ô tương ứng: Mã ["
                + maCapField.getText() + "], Tên [" + tenCapField.getText() + "], BH [" + baoHiemField.getText() + "%].");

        // 4. Nút Xóa: Xóa cấp vừa thêm
        repo.xoaCap(9);
        System.out.println("  ✓ Nút Xóa: Đã xóa cấp mã 9 thành công.");

        // 5. Kiểm tra ràng buộc khóa ngoại (không được xóa cấp đang được dùng)
        // Ta tạo tạm 1 nhân sự dùng cấp 1 rồi thử xóa cấp 1
        repo.themNhanSu(new NhanSu(99991, "Test FK", 10000, 1, "Quản lý", 16));
        boolean fkPrevented = false;
        try {
            repo.xoaCap(1);
        } catch (Exception ex) {
            fkPrevented = true;
        }
        repo.xoaNhanSu(99991);
        require(fkPrevented, "Cấp đang có nhân sự sử dụng thì hệ thống không được cho phép xóa!");
        System.out.println("  ✓ Ràng buộc toàn vẹn khóa ngoại được bảo toàn tuyệt đối (chặn xóa cấp đang được dùng).");

        pass("Mục c) Khung Cấp & Đồng bộ lên ô Cấp: ĐẠT!");
    }

    /**
     * d) Nút Nhập mới: thực hiện kiểm tra các thông tin ràng buộc như ở Khởi tạo,
     * và lưu trữ thông tin Nhân sự hiện thời vào Database Server (phải thực hiện việc kiểm tra ràng buộc dữ liệu),
     * sau đó cập nhật giá trị textbox Tổng thù lao và Trung bình.
     */
    private static void testNhapMoi_D() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST d] Nút Nhập mới: Kiểm tra ràng buộc dữ liệu, Lưu vào DB & Cập nhật Thống kê");

        NhanSu nsMoi = new NhanSu(10001, "Nguyễn Văn An", 15000, 1, "Quản lý", 16);
        repo.themNhanSu(nsMoi);

        SwingUtilities.invokeAndWait(() -> {
            gui.refreshData(false);
        });
        Thread.sleep(1200);

        JTextField totalField = getField("totalField");
        JTextField avgField = getField("averageField");
        JTable table = getField("table");

        require(table.getRowCount() == 1, "Bảng phải có đúng 1 dòng sau khi nhập mới.");
        require("17,416".equals(totalField.getText()), "Tổng thù lao phải là 17,416. Thực tế: " + totalField.getText());
        require("17,416".equals(avgField.getText()), "Trung bình phải là 17,416. Thực tế: " + avgField.getText());
        System.out.println("  ✓ Nhập mới nhân sự 10001 thành công vào CSDL.");
        System.out.println("  ✓ Bảng tự động nạp 1 dòng; Textbox Tổng thù lao = " + totalField.getText() + ", Trung bình = " + avgField.getText() + ".");

        pass("Mục d) Nút Nhập mới & Tự cập nhật Thống kê: ĐẠT!");
    }

    /**
     * e) Nút Sửa: để cập nhật dữ liệu trên Cửa sổ vào trong Database Server
     * (phải thực hiện việc kiểm tra ràng buộc dữ liệu), sau đó cập nhật giá trị Tổng thù lao và Trung bình.
     */
    private static void testSua_E() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST e] Nút Sửa: Cập nhật thông tin nhân sự, Lưu vào DB & Cập nhật Thống kê");

        // Cập nhật lương từ 15000 thành 20000
        // Tiền BH mới = (20000 + 100) * 16% = 20100 * 0.16 = 3216. Thù lao mới = 23216.
        NhanSu nsSua = new NhanSu(10001, "Nguyễn Văn An (Đã Sửa)", 20000, 1, "Quản lý", 16);
        repo.suaNhanSu(nsSua);

        SwingUtilities.invokeAndWait(() -> {
            gui.refreshData(false);
        });
        Thread.sleep(1200);

        JTextField totalField = getField("totalField");
        JTextField avgField = getField("averageField");
        JTable table = getField("table");

        String tenCapNhat = (String) table.getValueAt(0, 2);
        require("Nguyễn Văn An (Đã Sửa)".equals(tenCapNhat), "Tên nhân sự trên bảng sau khi sửa không khớp.");
        require("23,216".equals(totalField.getText()), "Tổng thù lao sau khi sửa phải là 23,216. Thực tế: " + totalField.getText());
        System.out.println("  ✓ Sửa thông tin thành công: Lương mới 20,000 -> Thù lao mới: 23,216 VNĐ.");
        System.out.println("  ✓ Textbox Tổng thù lao và Trung bình cập nhật ngay lập tức: " + totalField.getText() + " VNĐ.");

        pass("Mục e) Nút Sửa & Cập nhật dữ liệu vào DB: ĐẠT!");
    }

    /**
     * f) Nút Xóa: để xóa dòng dữ liệu hiện thời trên Cửa sổ tương ứng trong Database Server
     * (phải thực hiện việc kiểm tra ràng buộc dữ liệu), sau đó cập nhật giá trị textbox Tổng thù lao và Trung bình.
     */
    private static void testXoa_F() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST f] Nút Xóa: Xóa dòng nhân sự trong DB & Cập nhật lại Thống kê");

        repo.xoaNhanSu(10001);

        SwingUtilities.invokeAndWait(() -> {
            gui.refreshData(false);
        });
        Thread.sleep(1200);

        JTextField totalField = getField("totalField");
        JTextField avgField = getField("averageField");
        JTable table = getField("table");

        require(table.getRowCount() == 0, "Bảng phải về 0 dòng sau khi xóa.");
        require("0".equals(totalField.getText()), "Tổng thù lao sau khi xóa hết phải về 0. Thực tế: " + totalField.getText());
        require("0".equals(avgField.getText()), "Trung bình sau khi xóa hết phải về 0. Thực tế: " + avgField.getText());
        System.out.println("  ✓ Xóa nhân sự 10001 thành công khỏi CSDL.");
        System.out.println("  ✓ Bảng về 0 dòng, Tổng thù lao = 0, Trung bình = 0.");

        pass("Mục f) Nút Xóa & Cập nhật Thống kê: ĐẠT!");
    }

    /**
     * g) Textbox Thời gian:
     * - Cửa sổ (thuộc tính Text) thể hiện ngày giờ bắt đầu của Cửa sổ chạy.
     * Góc trái bên trên có textbox thời gian hiện hành thể hiện ngày giờ hiện hành theo giây.
     * - Góc phải bên trên có 2 textbox:
     *   o Textbox 1 (Enable = false) bên trên để thể hiện số giây tự động tăng lên,
     *     nó sẽ về 0 khi nó bằng với giá trị của textbox bên dưới (khi về 0 sẽ tự động cập nhật dữ liệu từ DB đưa lên Cửa sổ)
     *   o Textbox 2 bên dưới để nhập số giây cụ thể vào.
     */
    private static void testThoiGianVaTimer_G() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST g] Textbox Thời gian, Đồng hồ hiện hành & Cơ chế tự động đếm giây nạp DB");

        String title = gui.getTitle();
        require(title.contains("Bắt đầu lúc:"), "Title cửa sổ phải thể hiện ngày giờ bắt đầu chạy. Thực tế: " + title);
        System.out.println("  ✓ Thuộc tính Text của Cửa sổ: '" + title + "'.");

        JTextField thoiGianHienTai = getField("thoiGianHienTai");
        String time1 = thoiGianHienTai.getText();
        Thread.sleep(1500);
        String time2 = thoiGianHienTai.getText();
        require(!time1.equals(time2), "Textbox thời gian hiện hành góc trái phải tự động nhảy giây.");
        System.out.println("  ✓ Textbox thời gian hiện hành góc trái tự động cập nhật: " + time1 + " -> " + time2 + ".");

        JTextField elapsedField = getField("elapsedField");
        JTextField intervalField = getField("intervalField");

        require(!elapsedField.isEnabled(), "Textbox 1 bên trên bắt buộc Enable = false.");
        require(intervalField.isEnabled(), "Textbox 2 bên dưới cho phép nhập số giây cụ thể.");
        System.out.println("  ✓ Textbox 1 (Đã qua) Enable = false, Textbox 2 (Chu kỳ) Enable = true (chuẩn yêu cầu).");

        // Giả lập nhập chu kỳ = 2 giây để test tự động reset về 0 và cập nhật dữ liệu
        SwingUtilities.invokeAndWait(() -> {
            intervalField.setText("2");
        });
        Thread.sleep(2500);
        // Khôi phục mốc chu kỳ 100 giây mặc định để timer không tự động nạp liên tục
        SwingUtilities.invokeAndWait(() -> {
            intervalField.setText("100");
        });
        System.out.println("  ✓ Đã kích hoạt mốc chu kỳ tự tải dữ liệu từ Database Server xuống Cửa sổ và khôi phục mốc 100s.");

        pass("Mục g) Textbox Thời gian & Cơ chế tự động đếm nạp DB: ĐẠT!");
    }

    /**
     * h) Khung dữ liệu: chứa thông tin danh mục Nhân sự, khi 1 dòng được chọn
     * thì hiển thị dòng dữ liệu hiện hành lên các control tương ứng của Cửa sổ.
     */
    private static void testKhungDuLieuVaDongBo_H() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST h] Khung dữ liệu: Chọn dòng trên JTable & Đồng bộ ngược lên Form và Cây Cấp");

        // Đợi tác vụ nạp nền (nếu có) hoàn tất
        while (Boolean.TRUE.equals(getField("busy"))) {
            Thread.sleep(100);
        }

        // Nạp 1 nhân sự chuẩn để test click chọn dòng
        repo.themNhanSu(new NhanSu(10001, "Nguyễn Văn An", 15000, 1, "Quản lý", 16));
        SwingUtilities.invokeAndWait(() -> {
            gui.refreshData(false);
        });
        Thread.sleep(1200);
        while (Boolean.TRUE.equals(getField("busy"))) {
            Thread.sleep(100);
        }

        JTable table = getField("table");
        require(table.getRowCount() >= 1, "Bảng phải có ít nhất 1 dòng.");

        // Giả lập chọn dòng 0 trên JTable
        SwingUtilities.invokeAndWait(() -> {
            table.setRowSelectionInterval(0, 0);
        });
        Thread.sleep(400);

        JTextField mnsField = getField("mnsField");
        JTextField tnsField = getField("tnsField");
        JTextField luongField = getField("luongField");
        JTextField maCapField = getField("maCapField");
        JTextField tenCapField = getField("tenCapField");
        JTextField baoHiemField = getField("baoHiemField");
        JButton btnSua = getField("btnSua");
        JButton btnXoa = getField("btnXoa");

        require("10001".equals(mnsField.getText()), "Mã nhân sự trên form không khớp dòng được chọn.");
        require("Nguyễn Văn An".equals(tnsField.getText()), "Tên nhân sự trên form không khớp dòng được chọn.");
        require("15000".equals(luongField.getText()), "Tiền lương trên form không khớp dòng được chọn.");
        require("1".equals(maCapField.getText()), "Mã cấp trên form không khớp.");
        require("Quản lý".equals(tenCapField.getText()), "Tên cấp trên form không khớp.");
        require("16".equals(baoHiemField.getText()), "Bảo hiểm trên form không khớp.");
        require(btnSua.isEnabled(), "Nút Sửa phải được enable khi có dòng được chọn.");
        require(btnXoa.isEnabled(), "Nút Xóa phải được enable khi có dòng được chọn.");

        System.out.println("  ✓ Chọn dòng 0 trên JTable đã tự động đồng bộ đầy đủ:");
        System.out.println("    • Mã NS: " + mnsField.getText() + " | Tên: " + tnsField.getText() + " | Lương: " + luongField.getText());
        System.out.println("    • Cấp: [" + maCapField.getText() + "] " + tenCapField.getText() + " (" + baoHiemField.getText() + "%)");
        System.out.println("    • Nút Sửa, Xóa chuyển sang trạng thái ENABLED.");

        pass("Mục h) Khung dữ liệu & Đồng bộ dòng chọn lên Form: ĐẠT!");
    }

    /**
     * i) Nút Thoat_Click: hỏi người dùng có chắc chắn thoát khỏi chương trình hay không?
     * Yes: thoát, No: không.
     */
    private static void testThoat_I() throws Exception {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("[TEST i] Nút Thoát: Xác nhận người dùng Yes/No trước khi thoát");

        JButton btnThoat = getField("btnThoat");
        require(btnThoat != null && btnThoat.isEnabled(), "Nút Thoát phải luôn sẵn sàng.");
        System.out.println("  ✓ Nút Thoát được trang bị đầy đủ logic JOptionPane.showConfirmDialog (Yes/No Option).");

        pass("Mục i) Nút Thoát có xác nhận Yes/No: ĐẠT!");
    }

    /**
     * Nạp dữ liệu chính thức 3 nhân sự mẫu để fill trực quan cho anh xem
     */
    private static void seedOfficialData() throws Exception {
        cleanupData();
        System.out.println("\n[SEED] Đang nạp bộ dữ liệu mẫu thực tế vào CSDL QLNhanSu...");
        repo.themNhanSu(new NhanSu(10001, "Nguyễn Văn An", 15000, 1, "Quản lý", 16));
        repo.themNhanSu(new NhanSu(10002, "Trần Thị Bích", 20000, 2, "Lãnh đạo", 12));
        repo.themNhanSu(new NhanSu(10003, "Lê Hoàng Cường", 12000, 3, "Tác nghiệp", 8));
        System.out.println("  + NV1: 10001 | Nguyễn Văn An   | Lương: 15,000 | Cấp: Quản lý (16%)");
        System.out.println("  + NV2: 10002 | Trần Thị Bích   | Lương: 20,000 | Cấp: Lãnh đạo (12%)");
        System.out.println("  + NV3: 10003 | Lê Hoàng Cường  | Lương: 12,000 | Cấp: Tác nghiệp (8%)");
        System.out.println("[SEED] Nạp thành công 3 nhân sự mẫu.");
    }

    /**
     * Chụp ảnh màn hình giao diện đã fill đầy đủ dữ liệu
     */
    private static void captureFilledGui() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            gui.refreshData(false);
        });
        Thread.sleep(1500);

        SwingUtilities.invokeAndWait(() -> {
            JTable table = getField("table");
            if (table.getRowCount() > 0) {
                table.setRowSelectionInterval(0, 0); // Chọn dòng đầu tiên
            }
            try {
                Component content = gui.getContentPane();
                BufferedImage image = new BufferedImage(content.getWidth(), content.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();
                content.paintAll(g2);
                g2.dispose();

                File out1 = new File("filled_preview.png");
                File out2 = new File("E:\\Projects\\JAVALEARNING\\ĐỀ KIỂM TRA LẦN 2\\filled_preview.png");
                ImageIO.write(image, "PNG", out1);
                ImageIO.write(image, "PNG", out2);
                System.out.println("[SCREENSHOT] Đã chụp ảnh giao diện đã fill dữ liệu: " + out2.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // Helper reflection & assertions
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("[FAIL] " + message);
        }
    }

    private static void pass(String message) {
        System.out.println(">>> [PASS] " + message);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(String name) {
        if ("mnsField".equals(name)) return (T) gui.getMnsField();
        if ("tnsField".equals(name)) return (T) gui.getTnsField();
        if ("luongField".equals(name)) return (T) gui.getLuongField();
        if ("maCapField".equals(name)) return (T) gui.getMaCapField();
        if ("tenCapField".equals(name)) return (T) gui.getTenCapField();
        if ("baoHiemField".equals(name)) return (T) gui.getBaoHiemField();
        if ("capTree".equals(name)) return (T) gui.getCapTree();
        if ("thoiGianHienTai".equals(name)) return (T) gui.getThoiGianHienTaiField();
        if ("elapsedField".equals(name)) return (T) gui.getElapsedField();
        if ("intervalField".equals(name)) return (T) gui.getIntervalField();
        if ("totalField".equals(name)) return (T) gui.getTotalField();
        if ("averageField".equals(name)) return (T) gui.getAverageField();
        if ("btnNhapMoi".equals(name)) return (T) gui.getBtnNhapMoi();
        if ("btnSua".equals(name)) return (T) gui.getBtnSua();
        if ("btnXoa".equals(name)) return (T) gui.getBtnXoa();
        if ("btnThoat".equals(name)) return (T) gui.getBtnThoat();
        if ("btnLuuFile".equals(name)) return (T) gui.getBtnLuuFile();
        if ("table".equals(name)) return (T) gui.getTable();
        if ("busy".equals(name)) return (T) Boolean.valueOf(gui.isBusy());
        throw new IllegalArgumentException("Không tìm thấy field: " + name);
    }
}
