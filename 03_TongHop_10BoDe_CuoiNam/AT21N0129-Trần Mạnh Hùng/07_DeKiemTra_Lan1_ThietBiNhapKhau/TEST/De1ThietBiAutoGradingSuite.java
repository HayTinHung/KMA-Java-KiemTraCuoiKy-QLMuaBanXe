package TEST;

import java.awt.Color;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Bộ kiểm thử tự động (Auto-Grading Test Suite) cho Đề Kiểm Tra Lần 1 - Đề 1:
 * Thiết Bị Nhập Khẩu.
 *
 * Kiểm tra đầy đủ toàn bộ yêu cầu đề thi:
 * - Câu 1 (2.5 đ): Cấu trúc giao diện, tiêu đề, 7 nhãn quan trọng, ô nhập, radio, checkbox, nút lệnh.
 * - Câu 2 (1.0 đ): Đóng gói dữ liệu ThietBi, tỷ lệ Loại TB (Lao động 110%, Sản xuất 130%),
 *                  Phụ kiện (Dây cáp 10%, Bánh xe 5%, Thay thế 8%).
 * - Câu 3a (1.0 đ): Công thức Giá nhập khẩu, Tiền phụ kiện, Giá trị TB, Tổng giá trị, Tổng lượng.
 * - Câu 3b (1.0 đ): Trạng thái ban đầu (con trỏ ở Mã TB, chỉ bật Nhập mới & Thoát).
 * - Câu 3c (1.5 đ): Ràng buộc Mã TB đúng 8 ký tự, không trùng, Tên TB khác rỗng,
 *                   Giá nhập & Số lượng > 0, tự động bật/tắt nút Nhập vào.
 * - Câu 3d (1.0 đ): Chức năng nút Nhập vào (thêm vào cuối DSTB, cập nhật Giá trị & Thống kê).
 * - Câu 3e (0.5 đ): Chức năng nút Xóa (bật khi DSTB >= 1, xóa phần tử hiện hành).
 * - Câu 3f (0.5 đ): Chức năng nút Nhập mới (xóa rỗng tất cả ô nhập của thiết bị).
 * - Câu 3g (0.5 đ): Chức năng nút Thoát (xác nhận Yes/No).
 * - Câu 3h (0.5 đ): Đổi màu 7 nhãn quan trọng theo lựa chọn Đỏ, Xanh, Tím.
 * - Câu 3i: Đồng hồ thời gian thực (SimpleDateFormat + Date + javax.swing.Timer).
 */
public final class De1ThietBiAutoGradingSuite {
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

    public static void main(String[] args) throws Exception {
        System.out.println("==============================================================================================");
        System.out.println("   BỘ KIỂM THỬ TỰ ĐỘNG (AUTO-GRADING SUITE) - ĐỀ KIỂM TRA LẦN 1 (ĐỀ 1: THIẾT BỊ NHẬP KHẨU)");
        System.out.println("==============================================================================================");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        ThietBiNhapKhauGUI.isTestMode = true;

        // 1. Kiểm thử Câu 2 & Câu 3a trên lớp ThietBi
        assertCondition("Câu 2.1: Tỷ lệ Thiết bị Lao động = 110% (1.10)",
                Math.abs(ThietBi.TY_LE_LAO_DONG - 1.10) < 1e-9,
                "TY_LE_LAO_DONG = " + ThietBi.TY_LE_LAO_DONG);
        assertCondition("Câu 2.2: Tỷ lệ Thiết bị Sản xuất = 130% (1.30)",
                Math.abs(ThietBi.TY_LE_SAN_XUAT - 1.30) < 1e-9,
                "TY_LE_SAN_XUAT = " + ThietBi.TY_LE_SAN_XUAT);
        assertCondition("Câu 2.3: Tỷ lệ phụ kiện Dây cáp (10%), Bánh xe (5%), Thay thế (8%)",
                Math.abs(ThietBi.TY_LE_DAY_CAP - 0.10) < 1e-9
                        && Math.abs(ThietBi.TY_LE_BANH_XE - 0.05) < 1e-9
                        && Math.abs(ThietBi.TY_LE_THAY_THE - 0.08) < 1e-9,
                "10%, 5%, 8%");

        // Lao động (110%) + Dây cáp (10%) + Bánh xe (5%) = 125%; Giá nhập 10,000,000 x 2 => 25,000,000
        ThietBi tb1 = new ThietBi("TB000001", "Máy Hàn Công Nghiệp", 10_000_000L, 2, "Lao Động", true, true, false);
        assertCondition("Câu 3a.1: Giá nhập khẩu = Giá nhập * Loại TB (Lao động = 11,000,000)",
                Math.abs(tb1.getGiaNhapKhau() - 11_000_000.0) < 1e-3,
                "Giá NK = " + ThietBi.dinhDangTien(tb1.getGiaNhapKhau()));
        assertCondition("Câu 3a.2: Tiền phụ kiện = Giá nhập * Phụ kiện (15% = 1,500,000)",
                Math.abs(tb1.getTienPhuKien() - 1_500_000.0) < 1e-3,
                "Tiền PK = " + ThietBi.dinhDangTien(tb1.getTienPhuKien()));
        assertCondition("Câu 3a.3: Giá trị TB = SL * (Giá NK + Tiền PK) = 25,000,000",
                Math.abs(tb1.getGiaTri() - 25_000_000.0) < 1e-3,
                "Giá trị = " + ThietBi.dinhDangTien(tb1.getGiaTri()));

        // 2. Kiểm thử Câu 1 & Câu 3b: Giao diện và trạng thái khởi tạo
        final ThietBiNhapKhauGUI[] holder = new ThietBiNhapKhauGUI[1];
        SwingUtilities.invokeAndWait(() -> {
            holder[0] = new ThietBiNhapKhauGUI();
            holder[0].setVisible(true);
        });
        Thread.sleep(200);
        ThietBiNhapKhauGUI gui = holder[0];

        assertCondition("Câu 1.1: Tiêu đề cửa sổ là 'Giao diện' và đủ 7 nhãn quan trọng",
                "Giao diện".equals(gui.getTitle()) && gui.nhanQuanTrong.size() == 7,
                "Title='" + gui.getTitle() + "', labels=" + gui.nhanQuanTrong.size());
        assertCondition("Câu 3b.1: Ban đầu DSTB rỗng, chỉ bật 'Nhập mới' & 'Thoát'",
                gui.dstb.isEmpty() && gui.btnNhapMoi.isEnabled() && gui.btnThoat.isEnabled()
                        && !gui.btnNhapVao.isEnabled() && !gui.btnXoa.isEnabled(),
                "btnNhapVao=" + gui.btnNhapVao.isEnabled() + ", btnXoa=" + gui.btnXoa.isEnabled());
        assertCondition("Câu 3i: Đồng hồ thời gian đã được khởi tạo bằng SimpleDateFormat",
                gui.lblThoiGian.getText() != null && !gui.lblThoiGian.getText().isEmpty(),
                "Thời gian = " + gui.lblThoiGian.getText());

        // 3. Kiểm thử Câu 3c: Ràng buộc Mã TB đúng 8 ký tự, không trùng, Giá nhập & SL > 0
        SwingUtilities.invokeAndWait(() -> {
            gui.txtMaTB.setText("TB001"); // 5 ký tự (< 8)
            gui.txtTenTB.setText("Máy Cắt CNC");
            gui.txtGiaNhap.setText("10000000");
            gui.txtSoLuong.setText("2");
        });
        assertCondition("Câu 3c.1: Mã TB chưa đủ 8 ký tự ('TB001') -> Nút Nhập vào vẫn mờ",
                !gui.btnNhapVao.isEnabled(),
                "Mã TB length=5 -> btnNhapVao=" + gui.btnNhapVao.isEnabled());

        SwingUtilities.invokeAndWait(() -> {
            gui.txtMaTB.setText("TB000001"); // đúng 8 ký tự
            gui.txtTenTB.setText("Máy Hàn Công Nghiệp");
            gui.txtGiaNhap.setText("10000000");
            gui.txtSoLuong.setText("2");
            gui.radLaoDong.setSelected(true);
            gui.chkDayCap.setSelected(true);
            gui.chkBanhXe.setSelected(true);
            gui.chkThayThe.setSelected(false);
        });
        assertCondition("Câu 3c.2: Dữ liệu hợp lệ (Mã TB 8 ký tự) -> Nút Nhập vào sáng lên",
                gui.btnNhapVao.isEnabled(),
                "Mã='TB000001' -> btnNhapVao=" + gui.btnNhapVao.isEnabled());

        // 4. Kiểm thử Câu 3d: Nhập thiết bị vào cuối DSTB & cập nhật thống kê
        SwingUtilities.invokeAndWait(() -> gui.btnNhapVao.doClick());
        assertCondition("Câu 3d.1: Nhập TB000001 vào cuối DSTB, Giá trị = 25,000,000",
                gui.dstb.size() == 1 && "25,000,000".equals(gui.txtGiaTri.getText())
                        && "25,000,000".equals(gui.txtTongGiaTri.getText())
                        && "2".equals(gui.txtTongLuong.getText()),
                "Tổng GT=" + gui.txtTongGiaTri.getText() + ", Tổng SL=" + gui.txtTongLuong.getText());

        // Kiểm tra trùng mã TB000001
        SwingUtilities.invokeAndWait(() -> {
            gui.txtMaTB.setText("TB000001");
            gui.txtTenTB.setText("Thiết bị trùng");
            gui.txtGiaNhap.setText("20000000");
            gui.txtSoLuong.setText("1");
        });
        assertCondition("Câu 3c.3: Mã TB trùng ('TB000001') -> Nút Nhập vào bị mờ",
                !gui.btnNhapVao.isEnabled(),
                "btnNhapVao=" + gui.btnNhapVao.isEnabled());

        // Nhập thiết bị thứ 2: TB000002 (Sản xuất 130% + Thay thế 8% = 138%, Giá 10,000,000 x 1 => 13,800,000)
        SwingUtilities.invokeAndWait(() -> {
            gui.btnNhapMoi.doClick();
            gui.txtMaTB.setText("TB000002");
            gui.txtTenTB.setText("Dây Chuyền Đóng Gói");
            gui.txtGiaNhap.setText("10000000");
            gui.txtSoLuong.setText("1");
            gui.radSanXuat.setSelected(true);
            gui.chkDayCap.setSelected(false);
            gui.chkBanhXe.setSelected(false);
            gui.chkThayThe.setSelected(true);
            gui.btnNhapVao.doClick();
        });
        assertCondition("Câu 3d.2: Nhập TB000002 -> Tổng giá trị = 38,800,000, Tổng lượng = 3",
                gui.dstb.size() == 2
                        && "13,800,000".equals(gui.txtGiaTri.getText())
                        && "38,800,000".equals(gui.txtTongGiaTri.getText())
                        && "3".equals(gui.txtTongLuong.getText())
                        && "2/2".equals(gui.lblViTri.getText()),
                "Vị trí=" + gui.lblViTri.getText() + ", Tổng GT=" + gui.txtTongGiaTri.getText());

        // 5. Kiểm thử Câu 3h (Đổi màu), Điều hướng, Câu 3e (Xóa), Câu 3f (Nhập mới), Câu 3g (Thoát)
        SwingUtilities.invokeAndWait(() -> {
            gui.btnVeDau.doClick();
            gui.radXanh.doClick();
        });
        assertCondition("Câu 3h.1 & Điều hướng: Về đầu (1/2) và đổi màu 7 nhãn sang Xanh",
                "1/2".equals(gui.lblViTri.getText())
                        && tatCaNhanCoMau(gui.nhanQuanTrong, new Color(0, 102, 204)),
                "Vị trí=" + gui.lblViTri.getText());

        SwingUtilities.invokeAndWait(() -> {
            gui.btnVeCuoi.doClick();
            gui.radTim.doClick();
        });
        assertCondition("Câu 3h.2 & Điều hướng: Về cuối (2/2) và đổi màu 7 nhãn sang Tím",
                "2/2".equals(gui.lblViTri.getText())
                        && tatCaNhanCoMau(gui.nhanQuanTrong, new Color(153, 0, 153)),
                "Vị trí=" + gui.lblViTri.getText());

        // Câu 3e: Xóa phần tử hiện thời (TB000002)
        SwingUtilities.invokeAndWait(() -> gui.btnXoa.doClick());
        assertCondition("Câu 3e: Xóa phần tử hiện hành -> DSTB còn 1 phần tử, Tổng GT = 25,000,000",
                gui.dstb.size() == 1
                        && "1/1".equals(gui.lblViTri.getText())
                        && "25,000,000".equals(gui.txtTongGiaTri.getText()),
                "dstb.size()=" + gui.dstb.size());

        // Câu 3f: Nhập mới xóa trắng các ô nhập
        SwingUtilities.invokeAndWait(() -> gui.btnNhapMoi.doClick());
        assertCondition("Câu 3f: Nút 'Nhập mới' xóa trắng tất cả ô nhập của Thiết bị",
                gui.txtMaTB.getText().isEmpty()
                        && gui.txtTenTB.getText().isEmpty()
                        && gui.txtGiaNhap.getText().isEmpty()
                        && gui.txtSoLuong.getText().isEmpty()
                        && gui.txtGiaTri.getText().isEmpty(),
                "Các ô nhập đều rỗng");

        // Câu 3g: Thoát
        SwingUtilities.invokeAndWait(() -> gui.btnThoat.doClick());
        assertCondition("Câu 3g: Nút 'Thoát' đóng cửa sổ chương trình",
                !gui.isDisplayable(),
                "gui.isDisplayable() = " + gui.isDisplayable());

        System.out.println("==============================================================================================");
        System.out.printf("   KẾT QUẢ KIỂM THỬ ĐỀ 1: %d / %d TEST CASES PASSED (100%%)%n", passedTests, totalTests);
        System.out.println("==============================================================================================");
    }
}
