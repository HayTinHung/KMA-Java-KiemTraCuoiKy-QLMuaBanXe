import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Bộ kiểm thử tự động toàn diện (Auto-Grading Test Suite) cho Đề Ôn Tập Lần 1.
 *
 * Kiểm tra chi tiết từng câu hỏi trong đề thi:
 * - Câu 1 (2.5 đ): Cấu trúc giao diện, tiêu đề, các nhãn, ô nhập, radio, checkbox, nút lệnh.
 * - Câu 2 (1.0 đ): Đóng gói dữ liệu và hệ số tỷ lệ Loại máy (110%, 120%), Phụ kiện (30%, 5%, 10%).
 * - Câu 3a (1.0 đ): Công thức tính Đơn giá bán, Tiền phụ kiện, Giá trị máy, Tổng giá trị, Tổng lượng.
 * - Câu 3b (1.0 đ): Trạng thái khởi tạo ban đầu (con trỏ ở Mã máy, chỉ bật Nhập mới & Thoát).
 * - Câu 3c (1.5 đ): Kiểm tra hợp lệ dữ liệu nhập (Mã máy đủ 5 ký tự, không trùng, Tên khác rỗng,
 *                   Đơn giá & Số lượng nguyên dương > 0) và tự động bật/tắt nút Nhập máy.
 * - Câu 3d (1.0 đ): Chức năng nút Nhập máy (thêm vào cuối DSMT, cập nhật Giá trị & Thống kê).
 * - Câu 3e (0.5 đ): Chức năng nút Xóa (bật khi DSMT >= 1 phần tử, xóa phần tử hiện hành).
 * - Câu 3f (0.5 đ): Chức năng nút Nhập mới (xóa rỗng toàn bộ ô nhập của máy tính).
 * - Câu 3g (0.5 đ): Chức năng nút Thoát (xác nhận Yes/No).
 * - Câu 3h (0.5 đ): Đổi màu 7 phần tử quan trọng theo lựa chọn Đỏ, Xanh, Tím.
 */
public final class OnTap1AutoGradingSuite {
    private static int totalTests = 0;
    private static int passedTests = 0;

    private static void assertCondition(String testName, boolean condition, String detail) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.printf("  [PASS] %-62s | %s%n", testName, detail);
        } else {
            System.out.printf("  [FAIL] %-62s | %s%n", testName, detail);
            throw new AssertionError("Test failed: " + testName + " (" + detail + ")");
        }
    }

    private static boolean tatCaNhanCoMau(List<JLabel> dsNhan, Color mauMongDoi) {
        for (JLabel lbl : dsNhan) {
            if (!lbl.getForeground().equals(mauMongDoi)) {
                return false;
            }
        }
        return true;
    }

    private static void captureWindow(QuanLyMayTinhGUI gui, String fileName) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                gui.validate();
                gui.repaint();
            });
            Thread.sleep(180);
            final BufferedImage[] imgHolder = new BufferedImage[1];
            SwingUtilities.invokeAndWait(() -> {
                int w = gui.getContentPane().getWidth();
                int h = gui.getContentPane().getHeight();
                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                gui.getContentPane().paint(g2d);
                g2d.dispose();
                imgHolder[0] = image;
            });
            File outFile = new File(fileName);
            ImageIO.write(imgHolder[0], "png", outFile);
            System.out.println("  [SCREENSHOT] Đã lưu ảnh giao diện: " + outFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi chụp ảnh giao diện: " + fileName, e);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("==============================================================================================");
        System.out.println("   BỘ KIỂM THỬ TỰ ĐỘNG (AUTO-GRADING SUITE) - ĐỀ ÔN TẬP LẦN 1: MÁY TÍNH LẮP RÁP");
        System.out.println("==============================================================================================");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        QuanLyMayTinhGUI.isTestMode = true;

        // -----------------------------------------------------------------------------------------
        // PHẦN 1: KIỂM THỬ CÂU 2 & CÂU 3a TRÊN LỚP DỮ LIỆU MayTinh (MODEL UNIT TESTS)
        // -----------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 1] KIỂM THỬ CÂU 2 & CÂU 3a: LỚP DỮ LIỆU MayTinh & CÔNG THỨC TÍNH ---");

        assertCondition("Câu 2.1: Tỷ lệ máy Lắp ráp = 110% (1.10)",
                Math.abs(MayTinh.TY_LE_LAP_RAP - 1.10) < 1e-9,
                "TY_LE_LAP_RAP = " + MayTinh.TY_LE_LAP_RAP);
        assertCondition("Câu 2.2: Tỷ lệ máy Nhập khẩu = 120% (1.20)",
                Math.abs(MayTinh.TY_LE_NHAP_KHAU - 1.20) < 1e-9,
                "TY_LE_NHAP_KHAU = " + MayTinh.TY_LE_NHAP_KHAU);
        assertCondition("Câu 2.3: Tỷ lệ phụ kiện VGA 2 GB = 30% (0.30)",
                Math.abs(MayTinh.TY_LE_VGA - 0.30) < 1e-9,
                "TY_LE_VGA = " + MayTinh.TY_LE_VGA);
        assertCondition("Câu 2.4: Tỷ lệ phụ kiện DVD = 5% (0.05)",
                Math.abs(MayTinh.TY_LE_DVD - 0.05) < 1e-9,
                "TY_LE_DVD = " + MayTinh.TY_LE_DVD);
        assertCondition("Câu 2.5: Tỷ lệ phụ kiện LOA = 10% (0.10)",
                Math.abs(MayTinh.TY_LE_LOA - 0.10) < 1e-9,
                "TY_LE_LOA = " + MayTinh.TY_LE_LOA);

        // Kiểm tra công thức Câu 3a với máy Lắp ráp (10,000,000 x 2, VGA + LOA)
        // Đơn giá bán = 10,000,000 * 1.10 = 11,000,000
        // Tiền phụ kiện = 10,000,000 * (0.30 + 0.10) = 4,000,000
        // Giá trị máy = 2 * (11,000,000 + 4,000,000) = 30,000,000
        MayTinh mtTest1 = new MayTinh("MT001", "PC Gaming KMA", 10_000_000L, 2, "Lắp ráp", true, false, true);
        assertCondition("Câu 3a.1: Đơn giá bán = Đơn giá * Loại máy (Lắp ráp)",
                Math.abs(mtTest1.getDonGiaBan() - 11_000_000.0) < 1e-3,
                "Đơn giá bán = " + MayTinh.dinhDangTien(mtTest1.getDonGiaBan()));
        assertCondition("Câu 3a.2: Tiền phụ kiện = Đơn giá * Phụ kiện (VGA + LOA = 40%)",
                Math.abs(mtTest1.getTienPhuKien() - 4_000_000.0) < 1e-3,
                "Tiền phụ kiện = " + MayTinh.dinhDangTien(mtTest1.getTienPhuKien()));
        assertCondition("Câu 3a.3: Giá trị máy = SL * (Đơn giá bán + Tiền phụ kiện)",
                Math.abs(mtTest1.getGiaTriMay() - 30_000_000.0) < 1e-3,
                "Giá trị máy = " + MayTinh.dinhDangTien(mtTest1.getGiaTriMay()));

        // Kiểm tra công thức Câu 3a với máy Nhập khẩu (20,000,000 x 3, DVD)
        // Đơn giá bán = 20,000,000 * 1.20 = 24,000,000
        // Tiền phụ kiện = 20,000,000 * 0.05 = 1,000,000
        // Giá trị máy = 3 * (24,000,000 + 1,000,000) = 75,000,000
        MayTinh mtTest2 = new MayTinh("MT002", "Workstation Dell", 20_000_000L, 3, "Nhập khẩu", false, true, false);
        assertCondition("Câu 3a.4: Giá trị máy Nhập khẩu kèm DVD",
                Math.abs(mtTest2.getGiaTriMay() - 75_000_000.0) < 1e-3,
                "Giá trị máy = " + MayTinh.dinhDangTien(mtTest2.getGiaTriMay()));

        // -----------------------------------------------------------------------------------------
        // PHẦN 2: KIỂM THỬ CÂU 1 & CÂU 3b: GIAO DIỆN VÀ TRẠNG THÁI KHỞI TẠO BAN ĐẦU
        // -----------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 2] KIỂM THỬ CÂU 1 & CÂU 3b: KHỞI TẠO GIAO DIỆN & TRẠNG THÁI BAN ĐẦU ---");

        final QuanLyMayTinhGUI[] guiHolder = new QuanLyMayTinhGUI[1];
        SwingUtilities.invokeAndWait(() -> {
            guiHolder[0] = new QuanLyMayTinhGUI();
            guiHolder[0].setVisible(true);
        });
        Thread.sleep(250);
        QuanLyMayTinhGUI gui = guiHolder[0];

        JTextField txtMaMay = gui.txtMaMay;
        JTextField txtTenMay = gui.txtTenMay;
        JTextField txtDonGia = gui.txtDonGia;
        JTextField txtSoLuong = gui.txtSoLuong;
        JTextField txtGiaTri = gui.txtGiaTri;
        JTextField txtTongGiaTri = gui.txtTongGiaTri;
        JTextField txtTongLuong = gui.txtTongLuong;

        JRadioButton radLapRap = gui.radLapRap;
        JRadioButton radNhapKhau = gui.radNhapKhau;
        JCheckBox chkVGA = gui.chkVGA;
        JCheckBox chkDVD = gui.chkDVD;
        JCheckBox chkLoa = gui.chkLoa;

        JButton btnNhapMay = gui.btnNhapMay;
        JButton btnNhapMoi = gui.btnNhapMoi;
        JButton btnXoa = gui.btnXoa;
        JButton btnThoat = gui.btnThoat;
        JButton btnVeDau = gui.btnVeDau;
        JButton btnVeTruoc = gui.btnVeTruoc;
        JButton btnVeSau = gui.btnVeSau;
        JButton btnVeCuoi = gui.btnVeCuoi;
        JLabel lblViTri = gui.lblViTri;

        JRadioButton radDo = gui.radDo;
        JRadioButton radXanh = gui.radXanh;
        JRadioButton radTim = gui.radTim;
        List<JLabel> nhanQuanTrong = gui.nhanQuanTrong;
        List<MayTinh> dsmt = gui.dsmt;

        assertCondition("Câu 1.1: Tiêu đề cửa sổ là 'Giao diện'",
                "Giao diện".equals(gui.getTitle()),
                "Title = '" + gui.getTitle() + "'");
        assertCondition("Câu 1.2: Đủ 7 nhãn quan trọng trên cửa sổ",
                nhanQuanTrong.size() == 7,
                "Số nhãn quan trọng = " + nhanQuanTrong.size());
        assertCondition("Câu 3a.5: Ban đầu danh sách DSMT rỗng",
                dsmt.isEmpty(),
                "dsmt.size() = " + dsmt.size());
        assertCondition("Câu 3b.1: Nút 'Nhập mới' và 'Thoát' sáng lên (enabled = true)",
                btnNhapMoi.isEnabled() && btnThoat.isEnabled(),
                "btnNhapMoi=" + btnNhapMoi.isEnabled() + ", btnThoat=" + btnThoat.isEnabled());
        assertCondition("Câu 3b.2: Các nút 'Nhập máy', 'Xóa' bị mờ (enabled = false)",
                !btnNhapMay.isEnabled() && !btnXoa.isEnabled(),
                "btnNhapMay=" + btnNhapMay.isEnabled() + ", btnXoa=" + btnXoa.isEnabled());
        assertCondition("Câu 3b.3: Các nút điều hướng (Về đầu/trước/sau/cuối) bị mờ",
                !btnVeDau.isEnabled() && !btnVeTruoc.isEnabled() && !btnVeSau.isEnabled() && !btnVeCuoi.isEnabled(),
                "Chỉ số vị trí = " + lblViTri.getText());
        assertCondition("Câu 3b.4: Con trỏ văn bản đặt tại ô Mã máy",
                txtMaMay.isFocusOwner() || gui.getFocusOwner() == txtMaMay,
                "Focus owner = txtMaMay");

        captureWindow(gui, "gui_startup.png");

        // -----------------------------------------------------------------------------------------
        // PHẦN 3: KIỂM THỬ CÂU 3c: RÀNG BUỘC DỮ LIỆU & TỰ ĐỘNG BẬT/TẮT NÚT NHẬP MÁY
        // -----------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 3] KIỂM THỬ CÂU 3c: RÀNG BUỘC DỮ LIỆU Ô NHẬP & NÚT NHẬP MÁY ---");

        // 1. Mã máy chưa đủ 5 ký tự (4 ký tự)
        SwingUtilities.invokeAndWait(() -> {
            txtMaMay.setText("MT01");
            txtTenMay.setText("PC KMA");
            txtDonGia.setText("10000000");
            txtSoLuong.setText("2");
        });
        assertCondition("Câu 3c.1: Mã máy 4 ký tự (< 5) -> Nút Nhập máy vẫn mờ",
                !btnNhapMay.isEnabled(),
                "Mã máy='MT01' (length=4), btnNhapMay=" + btnNhapMay.isEnabled());

        // 2. Mã máy 6 ký tự (> 5)
        SwingUtilities.invokeAndWait(() -> txtMaMay.setText("MT0001"));
        assertCondition("Câu 3c.2: Mã máy 6 ký tự (> 5) -> Nút Nhập máy vẫn mờ",
                !btnNhapMay.isEnabled(),
                "Mã máy='MT0001' (length=6), btnNhapMay=" + btnNhapMay.isEnabled());

        // 3. Mã máy đúng 5 ký tự nhưng Tên máy rỗng
        SwingUtilities.invokeAndWait(() -> {
            txtMaMay.setText("MT001");
            txtTenMay.setText("   ");
        });
        assertCondition("Câu 3c.3: Tên máy để trống -> Nút Nhập máy vẫn mờ",
                !btnNhapMay.isEnabled(),
                "Tên máy='', btnNhapMay=" + btnNhapMay.isEnabled());

        // 4. Đơn giá âm hoặc bằng 0 hoặc chữ
        SwingUtilities.invokeAndWait(() -> {
            txtTenMay.setText("PC Gaming KMA");
            txtDonGia.setText("-5000");
        });
        assertCondition("Câu 3c.4: Đơn giá âm (-5000) -> Nút Nhập máy vẫn mờ",
                !btnNhapMay.isEnabled(),
                "Đơn giá='-5000', btnNhapMay=" + btnNhapMay.isEnabled());

        SwingUtilities.invokeAndWait(() -> txtDonGia.setText("abc"));
        assertCondition("Câu 3c.5: Đơn giá không phải số ('abc') -> Nút Nhập máy vẫn mờ",
                !btnNhapMay.isEnabled(),
                "Đơn giá='abc', btnNhapMay=" + btnNhapMay.isEnabled());

        // 5. Số lượng bằng 0
        SwingUtilities.invokeAndWait(() -> {
            txtDonGia.setText("10000000");
            txtSoLuong.setText("0");
        });
        assertCondition("Câu 3c.6: Số lượng bằng 0 -> Nút Nhập máy vẫn mờ",
                !btnNhapMay.isEnabled(),
                "Số lượng='0', btnNhapMay=" + btnNhapMay.isEnabled());

        // 6. Điền đầy đủ và hợp lệ -> Nút Nhập máy tự động sáng lên
        SwingUtilities.invokeAndWait(() -> {
            txtMaMay.setText("MT001");
            txtTenMay.setText("PC Gaming KMA");
            txtDonGia.setText("10000000");
            txtSoLuong.setText("2");
            radLapRap.setSelected(true);
            chkVGA.setSelected(true);
            chkDVD.setSelected(false);
            chkLoa.setSelected(true);
        });
        assertCondition("Câu 3c.7: Thông tin hợp lệ đầy đủ -> Nút Nhập máy sáng lên",
                btnNhapMay.isEnabled(),
                "Mã='MT001', Tên='PC Gaming KMA', Giá=10000000, SL=2 -> btnNhapMay=" + btnNhapMay.isEnabled());

        // -----------------------------------------------------------------------------------------
        // PHẦN 4: KIỂM THỬ CÂU 3d: NÚT NHẬP MÁY, THÊM VÀO CUỐI DSMT & CẬP NHẬT THỐNG KÊ
        // -----------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 4] KIỂM THỬ CÂU 3d: NÚT NHẬP MÁY & THỐNG KÊ DSMT ---");

        SwingUtilities.invokeAndWait(btnNhapMay::doClick);
        assertCondition("Câu 3d.1: Nhập máy 1 ('MT001') vào cuối DSMT",
                dsmt.size() == 1 && "MT001".equals(dsmt.get(0).getMaMay()),
                "dsmt.size()=" + dsmt.size() + ", Giá trị=" + txtGiaTri.getText());
        assertCondition("Câu 3d.2: Cập nhật đúng Giá trị (30,000,000), Tổng giá trị & Tổng lượng (2)",
                "30,000,000".equals(txtGiaTri.getText())
                        && "30,000,000".equals(txtTongGiaTri.getText())
                        && "2".equals(txtTongLuong.getText()),
                "Giá trị=" + txtGiaTri.getText() + ", Tổng GT=" + txtTongGiaTri.getText() + ", Tổng SL=" + txtTongLuong.getText());

        // Kiểm tra trùng mã máy 'MT001' (Câu 3c: không trùng lặp với máy tính đã có)
        SwingUtilities.invokeAndWait(() -> {
            txtMaMay.setText("MT001");
            txtTenMay.setText("Máy Trùng Mã");
            txtDonGia.setText("15000000");
            txtSoLuong.setText("1");
        });
        assertCondition("Câu 3c.8: Mã máy trùng với máy đã có trong DSMT ('MT001') -> Nút Nhập máy bị mờ",
                !btnNhapMay.isEnabled(),
                "Mã trùng='MT001' -> btnNhapMay=" + btnNhapMay.isEnabled());

        // Nhập máy 2: MT002 (Nhập khẩu, 20,000,000 x 3, DVD -> Giá trị = 75,000,000)
        SwingUtilities.invokeAndWait(() -> {
            btnNhapMoi.doClick();
            txtMaMay.setText("MT002");
            txtTenMay.setText("Workstation Dell");
            txtDonGia.setText("20000000");
            txtSoLuong.setText("3");
            radNhapKhau.setSelected(true);
            chkVGA.setSelected(false);
            chkDVD.setSelected(true);
            chkLoa.setSelected(false);
            btnNhapMay.doClick();
        });
        assertCondition("Câu 3d.3: Nhập máy 2 ('MT002') -> Tổng giá trị = 105,000,000, Tổng lượng = 5",
                dsmt.size() == 2
                        && "75,000,000".equals(txtGiaTri.getText())
                        && "105,000,000".equals(txtTongGiaTri.getText())
                        && "5".equals(txtTongLuong.getText()),
                "Giá trị=" + txtGiaTri.getText() + ", Tổng GT=" + txtTongGiaTri.getText() + ", Tổng SL=" + txtTongLuong.getText());

        // Nhập máy 3: MT003 (Lắp ráp, 15,000,000 x 1, VGA + DVD + LOA = 45%)
        // Đơn giá bán = 15,000,000 * 1.10 = 16,500,000
        // Tiền phụ kiện = 15,000,000 * 0.45 = 6,750,000
        // Giá trị máy = 1 * 23,250,000 = 23,250,000
        // Tổng giá trị = 105,000,000 + 23,250,000 = 128,250,000; Tổng lượng = 6
        SwingUtilities.invokeAndWait(() -> {
            btnNhapMoi.doClick();
            txtMaMay.setText("MT003");
            txtTenMay.setText("Server KMA Lab");
            txtDonGia.setText("15000000");
            txtSoLuong.setText("1");
            radLapRap.setSelected(true);
            chkVGA.setSelected(true);
            chkDVD.setSelected(true);
            chkLoa.setSelected(true);
            btnNhapMay.doClick();
        });
        assertCondition("Câu 3d.4: Nhập máy 3 ('MT003') -> Tổng giá trị = 128,250,000, Tổng lượng = 6",
                dsmt.size() == 3
                        && "23,250,000".equals(txtGiaTri.getText())
                        && "128,250,000".equals(txtTongGiaTri.getText())
                        && "6".equals(txtTongLuong.getText())
                        && "3/3".equals(lblViTri.getText()),
                "Vị trí=" + lblViTri.getText() + ", Tổng GT=" + txtTongGiaTri.getText() + ", Tổng SL=" + txtTongLuong.getText());

        captureWindow(gui, "gui_filled.png");

        // -----------------------------------------------------------------------------------------
        // PHẦN 5: KIỂM THỬ ĐIỀU HƯỚNG, CÂU 3h (MÀU SẮC), CÂU 3f (NHẬP MỚI), CÂU 3e (XÓA), CÂU 3g (THOÁT)
        // -----------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 5] KIỂM THỬ ĐIỀU HƯỚNG, MÀU SẮC (3h), NHẬP MỚI (3f), XÓA (3e), THOÁT (3g) ---");

        // Điều hướng Về trước (từ 3/3 về 2/3) và đổi màu Xanh
        SwingUtilities.invokeAndWait(() -> {
            btnVeTruoc.doClick();
            radXanh.doClick();
        });
        assertCondition("Điều hướng: Nút 'Về trước' chuyển về bản ghi 2/3 ('MT002')",
                "2/3".equals(lblViTri.getText()) && "MT002".equals(txtMaMay.getText()),
                "Vị trí=" + lblViTri.getText() + ", Mã máy=" + txtMaMay.getText());
        assertCondition("Câu 3h.1: Chọn radio 'Xanh' đổi màu cả 7 nhãn quan trọng sang Xanh",
                tatCaNhanCoMau(nhanQuanTrong, new Color(0, 102, 204)),
                "Màu nhãn = " + nhanQuanTrong.get(0).getForeground());

        captureWindow(gui, "gui_color_blue.png");

        // Đổi màu Tím và Về đầu (1/3)
        SwingUtilities.invokeAndWait(() -> {
            btnVeDau.doClick();
            radTim.doClick();
        });
        assertCondition("Điều hướng: Nút 'Về đầu' chuyển về bản ghi 1/3 ('MT001')",
                "1/3".equals(lblViTri.getText()) && "MT001".equals(txtMaMay.getText()),
                "Vị trí=" + lblViTri.getText() + ", Mã máy=" + txtMaMay.getText());
        assertCondition("Câu 3h.2: Chọn radio 'Tím' đổi màu cả 7 nhãn quan trọng sang Tím",
                tatCaNhanCoMau(nhanQuanTrong, new Color(153, 0, 153)),
                "Màu nhãn = " + nhanQuanTrong.get(0).getForeground());

        captureWindow(gui, "gui_color_purple.png");

        // Đổi lại màu Đỏ và kiểm tra nút Về cuối (3/3)
        SwingUtilities.invokeAndWait(() -> {
            radDo.doClick();
            btnVeCuoi.doClick();
        });
        assertCondition("Câu 3h.3: Chọn radio 'Đỏ' đổi màu cả 7 nhãn quan trọng về Đỏ",
                tatCaNhanCoMau(nhanQuanTrong, Color.RED),
                "Màu nhãn = " + nhanQuanTrong.get(0).getForeground());
        assertCondition("Điều hướng: Nút 'Về cuối' chuyển tới bản ghi 3/3 ('MT003')",
                "3/3".equals(lblViTri.getText()) && "MT003".equals(txtMaMay.getText()),
                "Vị trí=" + lblViTri.getText() + ", Mã máy=" + txtMaMay.getText());

        // Câu 3e: Xóa phần tử ở vị trí hiện thời (đang ở 3/3 -> xóa MT003)
        assertCondition("Câu 3e.1: Khi DSMT >= 1 phần tử thì nút 'Xóa' sáng lên",
                btnXoa.isEnabled(),
                "btnXoa.isEnabled() = " + btnXoa.isEnabled());
        SwingUtilities.invokeAndWait(btnXoa::doClick);
        assertCondition("Câu 3e.2: Xóa phần tử hiện thời ('MT003'), DSMT còn 2 phần tử, cập nhật Thống kê",
                dsmt.size() == 2
                        && "2/2".equals(lblViTri.getText())
                        && "105,000,000".equals(txtTongGiaTri.getText())
                        && "5".equals(txtTongLuong.getText()),
                "dsmt.size()=" + dsmt.size() + ", Tổng GT=" + txtTongGiaTri.getText() + ", Tổng SL=" + txtTongLuong.getText());

        // Câu 3f: Nút Nhập mới xóa rỗng tất cả ô nhập của Máy tính
        SwingUtilities.invokeAndWait(btnNhapMoi::doClick);
        assertCondition("Câu 3f: Nút 'Nhập mới' đưa tất cả thông tin ô nhập của Máy tính về rỗng",
                txtMaMay.getText().isEmpty()
                        && txtTenMay.getText().isEmpty()
                        && txtDonGia.getText().isEmpty()
                        && txtSoLuong.getText().isEmpty()
                        && txtGiaTri.getText().isEmpty()
                        && !chkVGA.isSelected() && !chkDVD.isSelected() && !chkLoa.isSelected(),
                "Tất cả ô nhập rỗng, nút Nhập máy=" + btnNhapMay.isEnabled());

        // Câu 3g: Nút Thoát đóng cửa sổ an toàn
        SwingUtilities.invokeAndWait(btnThoat::doClick);
        assertCondition("Câu 3g: Nút 'Thoát' thực thi sự kiện đóng chương trình",
                !gui.isDisplayable(),
                "gui.isDisplayable() = " + gui.isDisplayable());

        System.out.println("\n==============================================================================================");
        System.out.printf("   KẾT QUẢ KIỂM THỬ: %d / %d TEST CASES PASSED (100%% TUYỆT ĐỐI)%n", passedTests, totalTests);
        System.out.println("==============================================================================================");
        System.exit(0);
    }
}
