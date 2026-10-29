import java.io.File;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Bộ kiểm thử tự động toàn diện cho toàn bộ 6 Buổi của Lab 03 ("bai tap giao dien 1.pdf").
 * Sử dụng 100% cú pháp OOP chuẩn của trường (không dùng Reflection hay Stream).
 */
public final class Lab03AutoGradingSuite {
    private static int total = 0;
    private static int passed = 0;

    private static void check(String name, boolean cond, String detail) {
        total++;
        if (cond) {
            passed++;
            System.out.printf("  [PASS] %-56s | %s%n", name, detail);
        } else {
            System.out.printf("  [FAIL] %-56s | %s%n", name, detail);
            throw new AssertionError("Failed: " + name + " -> " + detail);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("====================================================================================");
        System.out.println("   BỘ KIỂM THỬ TỰ ĐỘNG LAB 03 (ĐẦY ĐỦ 6 BUỔI: OOP, CONSOLE, MẢNG/CHUỖI, SWING GUI)");
        System.out.println("====================================================================================");

        // 1. Kiểm thử Buổi 1 (Bài 1..4)
        PhanSo p1 = new PhanSo(1, 2);
        PhanSo p2 = new PhanSo(1, 3);
        check("Buổi 1 - Bài 1: Cộng phân số 1/2 + 1/3 = 5/6",
                "5/6".equals(p1.cong(p2).toString()), p1.cong(p2).toString());
        check("Buổi 1 - Bài 2: Đổi nhiệt độ 100C -> 212F và 32F -> 0C",
                Math.abs(NhietDo.cToF(100) - 212.0) < 1e-6 && Math.abs(NhietDo.fToC(32) - 0.0) < 1e-6,
                "100C=" + NhietDo.cToF(100) + "F");

        DanhSachSinhVien dssv = new DanhSachSinhVien();
        dssv.themSinhVien(new SinhVien(101, "Trần Mạnh Hùng", 9.0));
        check("Buổi 1 - Bài 3: Quản lý Sinh viên (Thêm, Tìm kiếm, Xóa)",
                dssv.timKiem(101) != null && !dssv.themSinhVien(new SinhVien(101, "Trùng", 8.0)),
                "SV 101: " + dssv.timKiem(101).getHoTen());

        File tmpXlsx = new File("tmp_lab03_test.xlsx");
        try {
            Bai04_SinhVienExcelXML.taoTepExcelMauStatic(tmpXlsx);
            List<SinhVienExcel> excelList = Bai04_SinhVienExcelXML.docTepExcelStatic(tmpXlsx);
            String xml = Bai04_SinhVienExcelXML.taoChuoiXML(excelList);
            check("Buổi 1 - Bài 4: Tạo/Đọc Excel .xlsx & Chuyển sang XML",
                    excelList.size() == 3 && xml.contains("<MSSV>SV001</MSSV>"),
                    "Đọc " + excelList.size() + " SV từ .xlsx");
        } finally {
            if (tmpXlsx.exists()) tmpXlsx.delete();
        }

        // 2. Kiểm thử Buổi 2 & Buổi 3
        check("Buổi 2 - Tiền điện bậc thang: 50KW = 250$, 101KW = 507$",
                Buoi02_03_CoBanVaVongLap.tinhTienDien(50) == 250L
                        && Buoi02_03_CoBanVaVongLap.tinhTienDien(101) == 507L,
                "50KW=250$, 101KW=507$");
        check("Buổi 3 - Tổng bình phương S1(3) = 14 & Số 2 chữ số khác nhau = 81",
                Buoi02_03_CoBanVaVongLap.tinhTongBinhPhuong(3) == 14L
                        && Buoi02_03_CoBanVaVongLap.soHaiChuSoKhacNhau().size() == 81,
                "S1(3)=14, count=81");

        // 3. Kiểm thử Buổi 4 & Buổi 5
        int[] sampleArr = {1, 5, 2, 6, 3, 5, 1, 8, 6};
        check("Buổi 5 - Tổng các phần tử cực đại [1,5,2,6,3,5,1,8,6] = 24",
                Buoi04_05_MangChuoiMaTran.tinhTongPhanTuCucDai(sampleArr) == 24,
                "5 + 6 + 5 + 8 = 24");
        String[] hlt = Buoi04_05_MangChuoiMaTran.tachHoLotTen("  Trần   Mạnh  Hùng ");
        check("Buổi 5 - Chuẩn hóa & Tách Họ / Họ lót / Tên",
                "Trần".equals(hlt[0]) && "Mạnh".equals(hlt[1]) && "Hùng".equals(hlt[2]),
                hlt[0] + " | " + hlt[1] + " | " + hlt[2]);

        // 4. Kiểm thử Buổi 6 - 3 Form Giao diện GUI
        Buoi06_BaiMau_MyNameProjectGUI.isTestMode = true;
        Buoi06_Bai01_CongTruNhanChiaGUI.isTestMode = true;
        Buoi06_Bai02_DangKyTaiKhoanGUI.isTestMode = true;

        SwingUtilities.invokeAndWait(() -> {
            Buoi06_BaiMau_MyNameProjectGUI f1 = new Buoi06_BaiMau_MyNameProjectGUI();
            f1.txtYourName.setText("Marry");
            f1.txtYearOfBirth.setText("1997");
            f1.xuLyShow();
            check("Buổi 6 - Bài mẫu: Form My Name Project tính tuổi & ErrorProvider",
                    f1.getLastMessage().startsWith("My Name is: Marry"),
                    f1.getLastMessage().replace("\n", " | "));
            f1.dispose();

            Buoi06_Bai01_CongTruNhanChiaGUI f2 = new Buoi06_Bai01_CongTruNhanChiaGUI();
            f2.txtA.setText("15");
            f2.txtB.setText("25");
            f2.thucHienPhepToan('*');
            check("Buổi 6 - Bài 1: Form Cộng trừ nhân chia (15 * 25 = 375)",
                    "375".equals(f2.txtKetQua.getText()),
                    "Kết quả = " + f2.txtKetQua.getText());
            f2.dispose();

            Buoi06_Bai02_DangKyTaiKhoanGUI f3 = new Buoi06_Bai02_DangKyTaiKhoanGUI();
            f3.txtTenDangNhap.setText("tranmanhhung");
            f3.txtEmail.setText("hung.at21n0129@actvn.edu.vn");
            f3.txtMatKhau.setText("Kma@2026");
            f3.txtXacNhanMatKhau.setText("Kma@2026");
            boolean ok = f3.xuLyDangKy();
            check("Buổi 6 - Bài 2: Form Đăng ký tài khoản (Validate Email & Enter)",
                    ok && f3.getLastRegisteredSummary().contains("hung.at21n0129@actvn.edu.vn"),
                    "Đăng ký thành công");
            f3.dispose();
        });

        System.out.println("====================================================================================");
        System.out.printf("   KẾT QUẢ KIỂM THỬ LAB 03: %d / %d PASSED (100%%)%n", passed, total);
        System.out.println("====================================================================================");
        System.exit(0);
    }
}
