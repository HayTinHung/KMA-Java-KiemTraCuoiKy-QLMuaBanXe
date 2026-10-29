import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Bộ kiểm thử tự động toàn diện (Auto-Grading Test Suite) cho Đề Kiểm Tra Cuối Kỳ - Đề 01:
 * QUẢN LÝ KHÁCH HÀNG MUA XE (CSDL SQL Server: QLMuaBanXe).
 *
 * Kiểm tra chi tiết 100% các câu hỏi và ràng buộc trong đề thi:
 * - Lưu ý đầu đề: Kiểm tra kết nối CSDL (nếu mất kết nối thì đưa dữ liệu trống rỗng lên màn hình
 *                 và thông báo kết nối CSDL không được).
 * - Câu 1: Cấu trúc 3 bảng Khach_Hang, Xe, Mua_Xe trong SQL Server QLMuaBanXe, khóa chính, khóa ngoại.
 * - Câu 2a: Giao diện chính chuẩn theo hình đề (Trung tâm xe gắn máy NVT - QUẢN LÝ KHÁCH HÀNG MUA XE).
 * - Câu 2b: Khởi tạo cửa sổ chính, con trỏ ở ô Mã xe, kiểm tra bàn phím & không rỗng, tải dữ liệu từ DB.
 * - Câu 2c: 2 control Thống kê: Tổng số khách hàng = n và Tổng thành tiền = tổng cột Thành tiền.
 * - Câu 2d: Các nút điều hướng VeDau (|<), VeTruoc (<), VeSau (>), VeCuoi (>|) và ô Chỉ số (1/4).
 * - Câu 2e: Nút Thêm Xe: kiểm tra trùng Mã xe ("Mã xe đã tồn tại, vui lòng nhập lại!"), thêm mới vào DB,
 *           cập nhật Thống kê & Khung chi tiết.
 * - Câu 2f: Nút Xóa Xe: xóa Xe hiện thời trong DB và đưa dữ liệu của Xe kế tiếp lên Cửa sổ.
 * - Câu 2g: Khung hiển thị dữ liệu chi tiết: cột STT làm cột đầu tiên + Thêm, Sửa, Xóa khách hàng mua Xe.
 * - Câu 2h: Nút Báo cáo: Cửa sổ Báo cáo có Combobox "Mã xe", Txtbox "Ngày mua", Btn "Thực hiện báo cáo".
 * - Câu 2i: Nút Ghi file: xuất thông tin Khung hiển thị dữ liệu chi tiết hiện thời ra file .txt.
 * - Ghi chú 1, 2, 3: Xác nhận Yes/No cho mọi nút; tự động cập nhật Thống kê; tự động tạo dòng dữ liệu ảo
 *                    đưa lên các control khi xử lý mà không kết nối được Database Server.
 */
public final class DeCuoiKyAutoGradingSuite {
    private static int totalTests = 0;
    private static int passedTests = 0;

    private static void assertCondition(String testName, boolean condition, String detail) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.printf("  [PASS] %-66s | %s%n", testName, detail);
        } else {
            System.out.printf("  [FAIL] %-66s | %s%n", testName, detail);
            throw new AssertionError("Test failed: " + testName + " (" + detail + ")");
        }
    }

    private static void captureComponent(final Component comp, String fileName) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                comp.validate();
                comp.repaint();
            });
            Thread.sleep(200);
            final BufferedImage[] holder = new BufferedImage[1];
            SwingUtilities.invokeAndWait(() -> {
                int w = Math.max(comp.getWidth(), 720);
                int h = Math.max(comp.getHeight(), 480);
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = img.createGraphics();
                comp.paintAll(g2d);
                g2d.dispose();
                holder[0] = img;
            });
            File out = new File(fileName);
            ImageIO.write(holder[0], "png", out);
            System.out.println("  [SCREENSHOT] Đã lưu ảnh: " + out.getAbsolutePath());
        } catch (Exception ex) {
            throw new RuntimeException("Không thể chụp ảnh giao diện: " + fileName, ex);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("======================================================================================================");
        System.out.println("   BỘ KIỂM THỬ TỰ ĐỘNG (AUTO-GRADING SUITE) - ĐỀ KIỂM TRA CUỐI KỲ (ĐỀ 01: QUẢN LÝ KHÁCH HÀNG MUA XE)");
        System.out.println("======================================================================================================");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        QuanLyMuaBanXeGUI.isTestMode = true;

        MuaBanXeRepository repo = new MuaBanXeRepository();

        // Đảm bảo dọn sạch mã test tạm thời nếu có từ lần chạy trước
        try {
            repo.xoaXe("XE99");
        } catch (Exception ignored) {
        }

        // -------------------------------------------------------------------------------------------------
        // PHẦN 1: KIỂM THỬ CÂU 1 - KẾT NỐI CSDL SQL SERVER QLMuaBanXe & 3 BẢNG (Khach_Hang, Xe, Mua_Xe)
        // -------------------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 1] KIỂM THỬ CÂU 1: KẾT NỐI SQL SERVER QLMuaBanXe & DỮ LIỆU 3 BẢNG ---");

        boolean connected = repo.kiemTraKetNoi();
        assertCondition("Câu 1.1: Kết nối CSDL SQL Server QLMuaBanXe thành công",
                connected,
                "kiemTraKetNoi() = " + connected);

        List<Xe> dsXe = repo.layDanhSachXe();
        List<KhachHang> dsKH = repo.layDanhSachKhachHang();
        assertCondition("Câu 1.2: Bảng Xe có đủ 4 bản ghi mẫu (chỉ số 1/4 như hình đề)",
                dsXe.size() == 4 && "XE01".equals(dsXe.get(0).getMaXe()),
                "dsXe.size() = " + dsXe.size() + ", Xe[0] = " + dsXe.get(0));
        assertCondition("Câu 1.3: Bảng Khach_Hang có dữ liệu khách hàng KH01 - Nguyễn Văn Sơn",
                dsKH.size() >= 4 && "KH01".equals(dsKH.get(0).getMaKH()),
                "dsKH.size() = " + dsKH.size() + ", KH[0] = " + dsKH.get(0));

        List<ChiTietMuaXe> dsMuaXE01 = repo.layChiTietMuaTheoXe("XE01");
        assertCondition("Câu 1.4: Bảng Mua_Xe liên kết khóa ngoại chính xác với Khach_Hang & Xe",
                dsMuaXE01.size() == 1 && dsMuaXE01.get(0).getThanhTien() == 23650000L,
                "KH01 mua XE01 (28/10/2011, SL=1) -> Thành tiền = " + Xe.dinhDangSo(dsMuaXE01.get(0).getThanhTien()));

        // -------------------------------------------------------------------------------------------------
        // PHẦN 2: KIỂM THỬ CÂU 2a, 2b, 2c: GIAO DIỆN CHÍNH, KHỞI TẠO, RÀNG BUỘC BÀN PHÍM & THỐNG KÊ
        // -------------------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 2] KIỂM THỬ CÂU 2a, 2b, 2c: KHỞI TẠO CỬA SỔ, BÀN PHÍM & THỐNG KÊ ---");

        final QuanLyMuaBanXeGUI[] guiHolder = new QuanLyMuaBanXeGUI[1];
        SwingUtilities.invokeAndWait(() -> {
            guiHolder[0] = new QuanLyMuaBanXeGUI(repo);
            guiHolder[0].setVisible(true);
        });
        Thread.sleep(250);
        final QuanLyMuaBanXeGUI gui = guiHolder[0];

        assertCondition("Câu 2a.1: Cửa sổ hiển thị đúng bản ghi đầu tiên XE01 - Suzuki X-Bike (1/4)",
                "XE01".equals(gui.txtMaXe.getText())
                        && "Suzuki X-Bike".equals(gui.txtTenXe.getText())
                        && "125".equals(gui.txtDungTich.getText())
                        && "21,500,000".equals(gui.txtDonGia.getText())
                        && "1/4".equals(gui.txtChiSo.getText()),
                "Mã=" + gui.txtMaXe.getText() + ", Tên=" + gui.txtTenXe.getText() + ", Chỉ số=" + gui.txtChiSo.getText());

        assertCondition("Câu 2b.1: Khi khởi tạo, con trỏ văn bản đặt tại ô Mã xe",
                gui.txtMaXe.isFocusOwner() || gui.getFocusOwner() == gui.txtMaXe,
                "Focus owner = txtMaXe");

        assertCondition("Câu 2c.1: Control Thống kê hiển thị đúng Tổng số KH = 1, Tổng thành tiền = 23,650,000 VNĐ",
                "1".equals(gui.txtTongSoKH.getText())
                        && "23,650,000 VNĐ".equals(gui.txtTongThanhTien.getText()),
                "Tổng KH=" + gui.txtTongSoKH.getText() + ", Tổng tiền=" + gui.txtTongThanhTien.getText());

        assertCondition("Câu 2g.1: Khung hiển thị chi tiết có cột STT là cột đầu tiên và đủ 8 cột",
                gui.tblChiTiet.getColumnCount() == 8
                        && "STT".equals(gui.tblChiTiet.getColumnName(0))
                        && Integer.valueOf(1).equals(gui.tblChiTiet.getValueAt(0, 0)),
                "Cột 0 = '" + gui.tblChiTiet.getColumnName(0) + "', Giá trị STT dòng 1 = " + gui.tblChiTiet.getValueAt(0, 0));

        captureComponent(gui.getContentPane(), "gui_ck_main.png");

        // Kiểm tra bộ lọc bàn phím số trên ô Dung tích và Đơn giá (Câu 2b)
        SwingUtilities.invokeAndWait(() -> {
            gui.txtDungTich.setText("ABCXYZ");
            gui.txtDonGia.setText("KHONG_PHAI_SO");
        });
        assertCondition("Câu 2b.2: Ô Dung tích và Đơn giá chặn nhập ký tự chữ cái không hợp lệ",
                "125".equals(gui.txtDungTich.getText()) && "21,500,000".equals(gui.txtDonGia.getText()),
                "Dung tích=" + gui.txtDungTich.getText() + ", Đơn giá=" + gui.txtDonGia.getText());

        // -------------------------------------------------------------------------------------------------
        // PHẦN 3: KIỂM THỬ CÂU 2d: CÁC NÚT ĐIỀU HƯỚNG VeDau, VeTruoc, VeSau, VeCuoi & Ô CHỈ SỐ
        // -------------------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 3] KIỂM THỬ CÂU 2d: ĐIỀU HƯỚNG BẢN GHI XE (VeDau, VeTruoc, VeSau, VeCuoi) ---");

        SwingUtilities.invokeAndWait(() -> gui.btnVeSau.doClick());
        assertCondition("Câu 2d.1: Nút Về sau (>) chuyển sang bản ghi 2/4 ('XE02' - Honda AirBlade)",
                "2/4".equals(gui.txtChiSo.getText()) && "XE02".equals(gui.txtMaXe.getText()),
                "Chỉ số=" + gui.txtChiSo.getText() + ", Mã xe=" + gui.txtMaXe.getText() + ", Tổng KH=" + gui.txtTongSoKH.getText());

        SwingUtilities.invokeAndWait(() -> gui.btnVeCuoi.doClick());
        assertCondition("Câu 2d.2: Nút Về cuối (>|) chuyển đến bản ghi cuối cùng 4/4 ('KH001' - Towner)",
                "4/4".equals(gui.txtChiSo.getText()) && "KH001".equals(gui.txtMaXe.getText()),
                "Chỉ số=" + gui.txtChiSo.getText() + ", Mã xe=" + gui.txtMaXe.getText() + ", Tổng KH=" + gui.txtTongSoKH.getText());

        SwingUtilities.invokeAndWait(() -> gui.btnVeTruoc.doClick());
        assertCondition("Câu 2d.3: Nút Về trước (<) lùi về bản ghi 3/4 ('XE03' - Yamaha Exciter)",
                "3/4".equals(gui.txtChiSo.getText()) && "XE03".equals(gui.txtMaXe.getText()),
                "Chỉ số=" + gui.txtChiSo.getText() + ", Mã xe=" + gui.txtMaXe.getText());

        SwingUtilities.invokeAndWait(() -> gui.btnVeDau.doClick());
        assertCondition("Câu 2d.4: Nút Về đầu (|<) quay về bản ghi đầu tiên 1/4 ('XE01' - Suzuki X-Bike)",
                "1/4".equals(gui.txtChiSo.getText()) && "XE01".equals(gui.txtMaXe.getText()),
                "Chỉ số=" + gui.txtChiSo.getText() + ", Mã xe=" + gui.txtMaXe.getText());

        // -------------------------------------------------------------------------------------------------
        // PHẦN 4: KIỂM THỬ CÂU 2e & 2f: NÚT THÊM XE (KIỂM TRA TRÙNG MÃ) & NÚT XÓA XE (ĐƯA XE KẾ TIẾP LÊN)
        // -------------------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 4] KIỂM THỬ CÂU 2e & 2f: THÊM XE (TRÙNG MÃ / MÃ MỚI) & XÓA XE ---");

        // 1. Thử bấm Thêm Xe khi Mã xe đang là 'XE01' (đã tồn tại trong bảng Xe)
        SwingUtilities.invokeAndWait(() -> gui.btnThemXe.doClick());
        assertCondition("Câu 2e.1: Nhập trùng Mã xe ('XE01') -> Báo lỗi 'Mã xe đã tồn tại, vui lòng nhập lại!'",
                "Mã xe đã tồn tại, vui lòng nhập lại!".equals(gui.getLastAlertMessage()),
                "Thông báo = '" + gui.getLastAlertMessage() + "'");

        // 2. Thử để trống Tên xe khi bấm Thêm Xe
        SwingUtilities.invokeAndWait(() -> {
            gui.txtMaXe.setText("XE99");
            gui.txtTenXe.setText("   ");
            gui.btnThemXe.doClick();
        });
        assertCondition("Câu 2e.2: Để trống Tên xe -> Hệ thống chặn và yêu cầu nhập đầy đủ",
                gui.getLastAlertMessage().contains("không được phép để trống"),
                "Thông báo = '" + gui.getLastAlertMessage() + "'");

        // 3. Nhập Xe mới hợp lệ 'XE99' kèm thông tin Khách hàng mua hiện thời
        SwingUtilities.invokeAndWait(() -> {
            gui.txtMaXe.setText("XE99");
            gui.txtTenXe.setText("Honda Winner X");
            gui.txtDungTich.setText("150");
            gui.txtDonGia.setText("40,000,000");
            gui.cboMaKH.setSelectedItem("KH01");
            gui.txtTenKH.setText("Nguyễn Văn Sơn");
            gui.txtCMND.setText("301458080");
            gui.txtNgayMua.setText("30/10/2012");
            gui.txtSoLuongMua.setText("1");
            gui.btnThemXe.doClick();
        });
        // Đơn giá 40,000,000 * SL 1 * 1.10 = 44,000,000 VNĐ
        assertCondition("Câu 2e.3: Thêm Xe mới ('XE99') thành công vào DB, cập nhật Thống kê & Khung chi tiết",
                gui.getDsXe().size() == 5
                        && "XE99".equals(gui.txtMaXe.getText())
                        && "1".equals(gui.txtTongSoKH.getText())
                        && "44,000,000 VNĐ".equals(gui.txtTongThanhTien.getText()),
                "Chỉ số=" + gui.txtChiSo.getText() + ", Tổng tiền=" + gui.txtTongThanhTien.getText());

        // -------------------------------------------------------------------------------------------------
        // PHẦN 5: KIỂM THỬ CÂU 2g: THÊM, SỬA, XÓA KHÁCH HÀNG MUA XE TRÊN KHUNG CHI TIẾT
        // -------------------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 5] KIỂM THỬ CÂU 2g: CẬP NHẬT THÊM / SỬA / XÓA TRÊN KHUNG CHI TIẾT ---");

        // Thêm khách hàng thứ 2 ('KH02') mua xe 'XE99' (SL = 2 => Thành tiền = 88,000,000; Tổng = 132,000,000)
        SwingUtilities.invokeAndWait(() -> {
            gui.cboMaKH.setSelectedItem("KH02");
            gui.txtTenKH.setText("Trần Thị Mai");
            gui.txtCMND.setText("079199001");
            gui.txtNgayMua.setText("31/10/2012");
            gui.txtSoLuongMua.setText("2");
            gui.btnThemKHMua.doClick();
        });
        assertCondition("Câu 2g.2: Thêm Khách hàng mua trên Khung chi tiết -> Tổng KH = 2, Tổng tiền = 132,000,000 VNĐ",
                "2".equals(gui.txtTongSoKH.getText())
                        && "132,000,000 VNĐ".equals(gui.txtTongThanhTien.getText()),
                "Tổng KH=" + gui.txtTongSoKH.getText() + ", Tổng tiền=" + gui.txtTongThanhTien.getText());

        // Sửa dòng khách hàng đầu tiên (đang được chọn ở dòng 0: KH02 ngày 31/10/2012) từ SL = 2 thành SL = 1
        SwingUtilities.invokeAndWait(() -> {
            gui.tblChiTiet.setRowSelectionInterval(0, 0);
            gui.txtSoLuongMua.setText("1");
            gui.btnSuaKHMua.doClick();
        });
        assertCondition("Câu 2g.3: Sửa Số lượng mua trên Khung chi tiết -> Tổng thành tiền cập nhật về 88,000,000 VNĐ",
                "2".equals(gui.txtTongSoKH.getText())
                        && "88,000,000 VNĐ".equals(gui.txtTongThanhTien.getText()),
                "Tổng KH=" + gui.txtTongSoKH.getText() + ", Tổng tiền=" + gui.txtTongThanhTien.getText());

        // Xóa 1 dòng khách hàng mua trên Khung chi tiết -> còn 1 khách hàng, Tổng tiền = 44,000,000 VNĐ
        SwingUtilities.invokeAndWait(() -> {
            gui.tblChiTiet.setRowSelectionInterval(0, 0);
            gui.btnXoaKHMua.doClick();
        });
        assertCondition("Câu 2g.4: Xóa dòng Khách hàng trên Khung chi tiết -> Tổng KH = 1, Tổng tiền = 44,000,000 VNĐ",
                "1".equals(gui.txtTongSoKH.getText())
                        && "44,000,000 VNĐ".equals(gui.txtTongThanhTien.getText()),
                "Tổng KH=" + gui.txtTongSoKH.getText() + ", Tổng tiền=" + gui.txtTongThanhTien.getText());

        // Câu 2f: Xóa Xe 'XE99' hiện thời khỏi CSDL và đưa Xe kế tiếp lên Cửa sổ
        SwingUtilities.invokeAndWait(() -> gui.btnXoaXe.doClick());
        assertCondition("Câu 2f: Xóa Xe ('XE99') -> Cập nhật DB còn 4 Xe và đưa Xe kế tiếp lên Cửa sổ",
                gui.getDsXe().size() == 4 && !"XE99".equals(gui.txtMaXe.getText()),
                "Số Xe còn lại = " + gui.getDsXe().size() + ", Xe hiện thời = " + gui.txtMaXe.getText());

        // Quay về XE01 (1/4)
        SwingUtilities.invokeAndWait(() -> gui.btnVeDau.doClick());

        // -------------------------------------------------------------------------------------------------
        // PHẦN 6: KIỂM THỬ CÂU 2h & 2i: CỬA SỔ BÁO CÁO & GHI FILE .TXT
        // -------------------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 6] KIỂM THỬ CÂU 2h (CỬA SỔ BÁO CÁO) & CÂU 2i (GHI FILE .TXT) ---");

        SwingUtilities.invokeAndWait(() -> gui.btnBaoCao.doClick());
        Thread.sleep(200);
        final BaoCaoMuaXeDialog dlgBaoCao = gui.getLastReportDialog();
        assertCondition("Câu 2h.1: Nút Báo cáo mở Cửa sổ Báo cáo có Combobox 'Mã xe', Txtbox 'Ngày mua', Btn 'Thực hiện báo cáo'",
                dlgBaoCao != null && dlgBaoCao.isVisible()
                        && dlgBaoCao.getCboMaXe() != null
                        && dlgBaoCao.getTxtNgayMua() != null
                        && dlgBaoCao.getBtnThucHienBaoCao() != null,
                "Cửa sổ Báo cáo đang mở = " + (dlgBaoCao != null && dlgBaoCao.isVisible()));

        // Chọn Mã xe 'KH001' (Towner) giống mẫu báo cáo Trang 3 của đề thi
        SwingUtilities.invokeAndWait(() -> {
            dlgBaoCao.getCboMaXe().setSelectedItem("KH001");
            dlgBaoCao.getTxtNgayMua().setText("");
            dlgBaoCao.getBtnThucHienBaoCao().doClick();
        });
        assertCondition("Câu 2h.2: Thực hiện báo cáo cho Mã xe 'KH001' (Towner) hiển thị đúng 2 dòng khách hàng",
                "KH001".equals(dlgBaoCao.getLblValMaXe().getText())
                        && "Towner".equals(dlgBaoCao.getLblValTenXe().getText())
                        && dlgBaoCao.getTblBaoCao().getRowCount() == 2,
                "Mã xe=" + dlgBaoCao.getLblValMaXe().getText()
                        + ", Tên xe=" + dlgBaoCao.getLblValTenXe().getText()
                        + ", Số KH=" + dlgBaoCao.getLblValSoLuongKH().getText().trim());

        captureComponent(dlgBaoCao.getContentPane(), "gui_ck_report.png");

        // Lọc kết hợp cả Mã xe 'KH001' và Ngày mua '28/10/2012'
        SwingUtilities.invokeAndWait(() -> {
            dlgBaoCao.getTxtNgayMua().setText("28/10/2012");
            dlgBaoCao.getBtnThucHienBaoCao().doClick();
        });
        assertCondition("Câu 2h.3: Lọc tổ hợp Mã xe 'KH001' + Ngày mua '28/10/2012' trả về đúng 1 dòng (Ghi chú 'Laptop')",
                dlgBaoCao.getTblBaoCao().getRowCount() == 1
                        && "Laptop".equals(dlgBaoCao.getTblBaoCao().getValueAt(0, 4)),
                "Số dòng sau khi lọc ngày = " + dlgBaoCao.getTblBaoCao().getRowCount());

        SwingUtilities.invokeAndWait(dlgBaoCao::dispose);

        // Câu 2i: Kiểm thử nút Ghi file .txt
        File outTxt = new File("ThongKeKhachHangMuaXe_XE01.txt");
        if (outTxt.exists()) {
            outTxt.delete();
        }
        SwingUtilities.invokeAndWait(() -> gui.btnGhiFile.doClick());
        List<String> lines = Files.readAllLines(outTxt.toPath(), StandardCharsets.UTF_8);
        boolean hasHeaderAndRow = false;
        for (String line : lines) {
            if (line.contains("Nguyễn Văn Sơn") && line.contains("23,650,000")) {
                hasHeaderAndRow = true;
                break;
            }
        }
        assertCondition("Câu 2i: Nút Ghi file tạo tệp .txt lưu trữ đúng thông tin Khung hiển thị chi tiết hiện thời",
                outTxt.exists() && hasHeaderAndRow,
                "File = " + outTxt.getName() + " (" + lines.size() + " dòng)");

        // -------------------------------------------------------------------------------------------------
        // PHẦN 7: KIỂM THỬ KHI MẤT KẾT NỐI CSDL (LƯU Ý ĐẦU ĐỀ & GHI CHÚ 3 CUỐI ĐỀ)
        // -------------------------------------------------------------------------------------------------
        System.out.println("\n--- [PHẦN 7] KIỂM THỬ MẤT KẾT NỐI CSDL: TRẠNG THÁI TRỐNG & DÒNG DỮ LIỆU ẢO DỰ PHÒNG ---");

        repo.setForceDisconnected(true);
        SwingUtilities.invokeAndWait(gui::kiemTraVaTaiDuLieuBanDau);
        assertCondition("Lưu ý đầu đề: Khi kết nối CSDL không được lúc khởi tạo -> đưa dữ liệu trống rỗng & thông báo lỗi",
                gui.txtMaXe.getText().isEmpty()
                        && gui.tblChiTiet.getRowCount() == 0
                        && gui.getLastAlertMessage().contains("Kết nối CSDL không được"),
                "Thông báo = '" + gui.getLastAlertMessage() + "'");

        // Khi thực hiện thao tác xử lý dữ liệu trong lúc mất kết nối -> tạo dòng dữ liệu ảo đưa lên Cửa sổ
        SwingUtilities.invokeAndWait(() -> gui.btnThemXe.doClick());
        assertCondition("Ghi chú 3 cuối đề: Khi xử lý mà mất kết nối DB -> tạo dòng dữ liệu ảo đưa lên các control Cửa sổ",
                "XE01".equals(gui.txtMaXe.getText())
                        && gui.tblChiTiet.getRowCount() == 1
                        && gui.getLastAlertMessage().contains("dòng dữ liệu ảo"),
                "Mã xe ảo=" + gui.txtMaXe.getText() + ", Số dòng bảng=" + gui.tblChiTiet.getRowCount());

        captureComponent(gui.getContentPane(), "gui_ck_virtual_fallback.png");

        repo.setForceDisconnected(false);

        // Kiểm thử nút Kết thúc đóng cửa sổ
        SwingUtilities.invokeAndWait(() -> gui.btnKetThuc.doClick());
        assertCondition("Nút Kết thúc: Xác nhận Yes/No và đóng cửa sổ chương trình an toàn",
                !gui.isDisplayable(),
                "gui.isDisplayable() = " + gui.isDisplayable());

        System.out.println("\n======================================================================================================");
        System.out.printf("   KẾT QUẢ KIỂM THỬ ĐỀ CUỐI KỲ: %d / %d TEST CASES PASSED (100%% TUYỆT ĐỐI)%n", passedTests, totalTests);
        System.out.println("======================================================================================================");
        System.exit(0);
    }
}
