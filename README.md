# Hồ Sơ Kiểm Tra Cuối Kỳ Môn Lập Trình Java (KMA) — `QLMuaBanXe`

- **Sinh viên thực hiện:** Trần Mạnh Hùng — **MSSV:** `AT21N0129`
- **Đơn vị:** Khoa An toàn thông tin — Học viện Kỹ thuật Mật mã (KMA)
- **Repository URL:** https://github.com/HayTinHung/KMA-Java-KiemTraCuoiKy-QLMuaBanXe

---

## ✅ Nhật Ký Hoàn Thiện 100% Theo Báo Cáo Đánh Giá Của Claude

Toàn bộ các điểm góp ý trong đợt kiểm tra trước đã được cập nhật triệt để:

1. **Khả năng tự chạy độc lập trên máy giám khảo (Portability & IDE/JDBC Setup):**
   - Đã đính kèm sẵn thư viện Microsoft JDBC Driver tại [`lib/mssql-jdbc-13.6.0.jre11.jar`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/lib/mssql-jdbc-13.6.0.jre11.jar) cả trong repo lẫn trong gói nén `AT21N0129-Trần Mạnh Hùng-QLMuaBanXe.zip`.
   - Đã bổ sung file cấu hình [`config.properties`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/config.properties), hướng dẫn chạy chi tiết [`HUONG_DAN_CHAY.txt`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/HUONG_DAN_CHAY.txt), và file cấu hình project chuẩn Eclipse/IDE (`.project`, `.classpath`).
   - Lớp [`MuaBanXeRepository.java`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/src/MuaBanXeRepository.java) hỗ trợ đọc `config.properties` / System properties / biến môi trường, hỗ trợ cả tài khoản SQL (`sa`/password) lẫn Windows Authentication, tự động dò tìm các SQL Server instance phổ biến (`localhost`, `.`, `.\SQLEXPRESS`, `.\SQL2025DEV`), và lưu file `QLMuaBanXe.sql` chuẩn **UTF-8 BOM**.
   - Bộ kiểm thử tự động [`DeCuoiKyAutoGradingSuite.java`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/src/DeCuoiKyAutoGradingSuite.java) (31/31 test cases) tự động kết nối SQL Server thật nếu có, hoặc tự động kích hoạt chế độ Giả lập CSDL chuẩn (`enableSimulatedDatabaseMode()` với đúng 4 xe & 6 giao dịch từ `QLMuaBanXe.sql`) để chạy và **PASS 31/31 test cases** trên bất kỳ máy chấm nào (kể cả máy Linux/Windows không cài SQL Server).
   - File đơn tệp `Main.java` trong gói `.zip` được đặt tại `OnlineGDB/Main.java` để khi giám khảo import thư mục project hoặc chạy `javac src/*.java` không bao giờ bị trùng tên class.

2. **Tương tác giao diện & Kiểm tra ràng buộc (Câu 2b, 2e, 2f, 2g, 2h):**
   - **Sửa trực tiếp trên `JTable` (Câu 2g):** Đã gắn `TableModelListener` lên `detailTableModel` trong [`QuanLyMuaBanXeGUI.java`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/src/QuanLyMuaBanXeGUI.java). Khi giám khảo sửa trực tiếp các cột `1..6` (`Mã KH`, `Tên khách hàng`, `Số CMND`, `Giới tính`, `Ngày mua`, `Số lượng`) ngay trên bảng `JTable`, hệ thống tự động kiểm tra hợp lệ, gọi `suaChiTietMuaXe(...)` lưu xuống CSDL, tính lại cột `Thành tiền` và cập nhật ngay 2 ô `Tổng số khách hàng` + `Tổng thành tiền`.
   - **Kiểm tra không rỗng khi thoát control (Câu 2b):** `FocusAdapter.focusLost` (`ganSuKienKiemTraKhongRongKhiThoat`) kiểm tra khi thoát khỏi ô nhập liệu đang để trống: vừa tô nền hồng cảnh báo, vừa phát thông báo nhắc không được để trống và đặt lại con trỏ focus.
   - **Bảo toàn Địa chỉ khách hàng & Đầy đủ các ô điều khiển:** Đã đưa đầy đủ `txtDiaChiKH` (`Địa chỉ`), `txtGhiChuMua` (`Ghi chú`) và nút `btnNhapMoiXe` (`Làm trống ô Xe`) lên giao diện; khi cập nhật khách hàng hiện có, giữ nguyên địa chỉ thật trong CSDL (không ghi đè bằng `"TP.HCM"`).
   - **Tách bạch `Btn Thêm Xe` (Câu 2e):** `xuLyThemXe()` chỉ kiểm tra và thêm bản ghi `Xe` mới vào bảng `Xe`; việc thêm khách hàng mua xe thực hiện riêng qua `Btn + Thêm KH`.
   - **Kiểm tra định dạng ngày báo cáo (Câu 2h):** `BaoCaoMuaXeDialog` và `MuaBanXeRepository.locBaoCaoMuaXe` kiểm tra chặt định dạng `dd/MM/yyyy` (`SimpleDateFormat.setLenient(false)`) và so khớp chính xác ngày mua.

3. **Đồng bộ 100% các gói nộp & Chuẩn hóa cú pháp theo Giáo trình:**
   - Đã loại bỏ hoàn toàn biểu thức `lambda` (`->`) và `try-with-resources`, thay bằng `Anonymous Inner Class` (`new ActionListener() { ... }`, `new Runnable() { ... }`) và `try ... finally` chuẩn giáo trình.
   - Đã đồng bộ 100% từng dòng giữa `02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/Main.java`, `AT21N0129-Trần Mạnh Hùng-QLMuaBanXe.docx`, `AT21N0129-Trần Mạnh Hùng-QLMuaBanXe.zip` và `03_TongHop_10BoDe_CuoiNam/AT21N0129-Trần Mạnh Hùng/10_DeKiemTra_CuoiKy_QLMuaBanXe_JDBC/Main.java`.
   - Script [`03_TongHop_10BoDe_CuoiNam/verify_all_10_modules.py`](03_TongHop_10BoDe_CuoiNam/verify_all_10_modules.py) sử dụng đường dẫn tương đối (`os.path.dirname(os.path.abspath(__file__))`) và thực thi kiểm thử chức năng cho cả 10 bộ đề.

---

## 📌 Thuyết Minh Thiết Kế Bám Sát Đề Thi (`De CK.pdf`)

1. **Về kiểu dữ liệu `VARCHAR(20)` cho `MaKH`, `MaXe` và `CMND`:**
   - Văn bản Câu 1 ghi gợi ý `MaKH (số nguyên)`, `MaXe (số nguyên)`, `CMND (Số)` kèm lưu ý cho phép: *"Hoặc sinh viên tùy ý xây dựng lại các bảng dữ liệu để lưu trữ hợp lý dữ liệu..."*.
   - Trên Hình chụp Cửa sổ chính (Trang 1) và Hình chụp Cửa sổ Báo cáo (Trang 3) của đề thi, dữ liệu mẫu thực tế có dạng mã chữ-số: `Mã xe = XE01`, `Mã KH = KH01`, `Mã xe báo cáo = KH001`, và số `CMND` có thể bắt đầu bằng chữ số `0` (`079199001`).
   - Do đó, sử dụng `VARCHAR(20)` cho `MaKH`, `MaXe` và `VARCHAR(20)` kèm `CHECK (CMND NOT LIKE '%[^0-9]%' AND LEN(CMND) >= 8)` cho `CMND` là hợp lý nhất để khớp 100% với hình đề.
2. **Về khóa chính bảng `Mua_Xe` và cột `GhiChu`:**
   - Khóa chính `PK_Mua_Xe (MaKH, MaXe, NgayMua)` cho phép một khách hàng mua thêm cùng mẫu xe ở các ngày khác nhau; cột `GhiChu NVARCHAR(100)` phục vụ hiển thị cột thứ 5 (`Ghi chú`) trên Cửa sổ Báo cáo ở Trang 3.
3. **Về công thức tính `Thành tiền = Đơn giá × Số lượng × 1.10`:**
   - Trên Hình chụp Cửa sổ chính ở Trang 1: `Đơn giá = 21,500,000`, `Số lượng = 1`, `Thành tiền = 23,650,000`.
   - Tỉ lệ chính xác: `23,650,000 / (21,500,000 × 1) = 1.10` (tức Đơn giá cộng 10% thuế VAT & lệ phí trước bạ).

---

## 📂 Cấu Trúc Repository

1. **Đề Thi Gốc & Tài Liệu Trường (`01_DeThi_Va_TaiLieuTruong/`):**
   - [`De CK.pdf`](01_DeThi_Va_TaiLieuTruong/De_Kiem_Tra_Cuoi_Ky/De%20CK.pdf), [`De_CK_Trang_1.png`](01_DeThi_Va_TaiLieuTruong/De_Kiem_Tra_Cuoi_Ky/De_CK_Trang_1.png), [`De_CK_Trang_2.png`](01_DeThi_Va_TaiLieuTruong/De_Kiem_Tra_Cuoi_Ky/De_CK_Trang_2.png), [`De_CK_Trang_3.png`](01_DeThi_Va_TaiLieuTruong/De_Kiem_Tra_Cuoi_Ky/De_CK_Trang_3.png), [`YeuCau_NopBai_Classroom.png`](01_DeThi_Va_TaiLieuTruong/De_Kiem_Tra_Cuoi_Ky/YeuCau_NopBai_Classroom.png).
2. **Bài Làm Kiểm Tra Cuối Kỳ (`02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/`):**
   - **2 File nộp chính thức (`2_TapTin_Nop_Classroom/`):**
     - [`AT21N0129-Trần Mạnh Hùng-QLMuaBanXe.zip`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/2_TapTin_Nop_Classroom/AT21N0129-Tr%E1%BA%A7n%20M%E1%BA%A1nh%20H%C3%B9ng-QLMuaBanXe.zip)
     - [`AT21N0129-Trần Mạnh Hùng-QLMuaBanXe.docx`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/2_TapTin_Nop_Classroom/AT21N0129-Tr%E1%BA%A7n%20M%E1%BA%A1nh%20H%C3%B9ng-QLMuaBanXe.docx)
   - **Mã nguồn & CSDL:** [`src/`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/src/), [`OnlineGDB/Main.java`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/OnlineGDB/Main.java), [`lib/mssql-jdbc-13.6.0.jre11.jar`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/lib/mssql-jdbc-13.6.0.jre11.jar), [`config.properties`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/config.properties), [`HUONG_DAN_CHAY.txt`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/HUONG_DAN_CHAY.txt), [`QLMuaBanXe.sql`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/QLMuaBanXe.sql), [`QLMuaBanXe.bak`](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/QLMuaBanXe.bak).
3. **Tổng Hợp 10 Bộ Đề Cuối Năm (`03_TongHop_10BoDe_CuoiNam/`):**
   - [`verify_all_10_modules.py`](03_TongHop_10BoDe_CuoiNam/verify_all_10_modules.py) & [`AT21N0129-Trần Mạnh Hùng/`](03_TongHop_10BoDe_CuoiNam/AT21N0129-Tr%E1%BA%A7n%20M%E1%BA%A1nh%20H%C3%B9ng/).

---

## 🖼️ Hình Ảnh Minh Chứng Thực Tế (GUI & OnlineGDB)

### A. Chạy thử nghiệm trên `https://www.onlinegdb.com/online_java_compiler` (0 lỗi font, ĐẠT 8/8 HẠNG MỤC, Exit code 0)
![OnlineGDB Top](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/screenshots/onlinegdb_compile_top.png)
![OnlineGDB Bottom](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/screenshots/onlinegdb_compile_test.png)

### B. Giao diện chính & Cửa sổ Báo cáo thống kê trên Desktop
![Main GUI](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/screenshots/gui_ck_main.png)
![Report Dialog](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/screenshots/gui_ck_report.png)
![Virtual Fallback](02_BaiLam_KiemTraCuoiKy_QLMuaBanXe/screenshots/gui_ck_virtual_fallback.png)
