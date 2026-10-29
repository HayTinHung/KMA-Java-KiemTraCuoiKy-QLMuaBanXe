-- =================================================================================
-- KỊCH BẢN KHỞI TẠO CƠ SỞ DỮ LIỆU SQL SERVER: QLMuaBanXe
-- ĐỀ KIỂM TRA CUỐI KỲ - ĐỀ 01 (MÔN: LẬP TRÌNH JAVA - THỜI GIAN: 90 PHÚT)
-- Sinh viên thực hiện : Trần Mạnh Hùng - MSSV: AT21N0129
-- =================================================================================

USE master;
GO

IF DB_ID(N'QLMuaBanXe') IS NULL
BEGIN
    CREATE DATABASE QLMuaBanXe;
END
GO

USE QLMuaBanXe;
GO

-- Xóa các bảng cũ theo thứ tự khóa ngoại nếu đã tồn tại để chạy lại sạch sẽ
IF OBJECT_ID(N'dbo.Mua_Xe', N'U') IS NOT NULL DROP TABLE dbo.Mua_Xe;
IF OBJECT_ID(N'dbo.Xe', N'U') IS NOT NULL DROP TABLE dbo.Xe;
IF OBJECT_ID(N'dbo.Khach_Hang', N'U') IS NOT NULL DROP TABLE dbo.Khach_Hang;
GO

-- 1. Bảng Khach_Hang: Lưu thông tin Khách hàng
-- Khach_Hang [MaKH; TenKH (Text, 50); CMND (Số); GioiTinh (Text, 10); DiaChi (Text, 50)]
CREATE TABLE dbo.Khach_Hang (
    MaKH     VARCHAR(20)  NOT NULL,
    TenKH    NVARCHAR(50) NOT NULL,
    CMND     VARCHAR(20)  NOT NULL,
    GioiTinh NVARCHAR(10) NOT NULL,
    DiaChi   NVARCHAR(50) NOT NULL,
    CONSTRAINT PK_Khach_Hang PRIMARY KEY (MaKH),
    CONSTRAINT CK_Khach_Hang_TenKH_NotEmpty CHECK (LEN(LTRIM(RTRIM(TenKH))) > 0),
    CONSTRAINT CK_Khach_Hang_CMND_Digits CHECK (CMND NOT LIKE '%[^0-9]%' AND LEN(CMND) >= 8),
    CONSTRAINT CK_Khach_Hang_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ'))
);
GO

-- 2. Bảng Xe: Lưu thông tin Xe gắn máy
-- Xe [MaXe; TenXe (Text, 50); DungTich (số nguyên); DonGia (số nguyên)]
CREATE TABLE dbo.Xe (
    MaXe     VARCHAR(20)  NOT NULL,
    TenXe    NVARCHAR(50) NOT NULL,
    DungTich INT          NOT NULL,
    DonGia   BIGINT       NOT NULL,
    CONSTRAINT PK_Xe PRIMARY KEY (MaXe),
    CONSTRAINT CK_Xe_TenXe_NotEmpty CHECK (LEN(LTRIM(RTRIM(TenXe))) > 0),
    CONSTRAINT CK_Xe_DungTich_Positive CHECK (DungTich > 0),
    CONSTRAINT CK_Xe_DonGia_Positive CHECK (DonGia > 0)
);
GO

-- 3. Bảng Mua_Xe: Lưu thông tin Khách hàng mua Xe
-- Mua_Xe [MaKH; MaXe; NgayMua (Date/Time, dd/mm/yyyy); SoLuong (số nguyên); GhiChu]
CREATE TABLE dbo.Mua_Xe (
    MaKH    VARCHAR(20)   NOT NULL,
    MaXe    VARCHAR(20)   NOT NULL,
    NgayMua DATE          NOT NULL,
    SoLuong INT           NOT NULL,
    GhiChu  NVARCHAR(100) NOT NULL CONSTRAINT DF_Mua_Xe_GhiChu DEFAULT (N''),
    CONSTRAINT PK_Mua_Xe PRIMARY KEY (MaKH, MaXe, NgayMua),
    CONSTRAINT FK_Mua_Xe_Khach_Hang FOREIGN KEY (MaKH)
        REFERENCES dbo.Khach_Hang (MaKH) ON UPDATE CASCADE,
    CONSTRAINT FK_Mua_Xe_Xe FOREIGN KEY (MaXe)
        REFERENCES dbo.Xe (MaXe) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CK_Mua_Xe_SoLuong_Positive CHECK (SoLuong > 0)
);
GO

-- =================================================================================
-- NẠP DỮ LIỆU MẪU CHUẨN THEO HÌNH CHỤP TRONG ĐỀ THI (4 XE -> CHỈ SỐ 1/4)
-- =================================================================================

INSERT INTO dbo.Khach_Hang (MaKH, TenKH, CMND, GioiTinh, DiaChi) VALUES
('KH01', N'Nguyễn Văn Sơn',  '301458080', N'Nam', N'125 Nguyễn Trãi, Q.1, TP.HCM'),
('KH02', N'Trần Thị Mai',    '079199001', N'Nữ',  N'42 Lê Lợi, Q.3, TP.HCM'),
('KH03', N'Lê Hoàng Nam',    '079198234', N'Nam', N'88 Cộng Hòa, Q.Tân Bình, TP.HCM'),
('KH04', N'Phạm Minh Tuấn',  '079200567', N'Nam', N'19 Võ Văn Ngân, TP.Thủ Đức');
GO

INSERT INTO dbo.Xe (MaXe, TenXe, DungTich, DonGia) VALUES
('XE01',  N'Suzuki X-Bike',  125, 21500000),
('XE02',  N'Honda AirBlade', 150, 42000000),
('XE03',  N'Yamaha Exciter', 155, 47500000),
('KH001', N'Towner',         110, 18500000);
GO

INSERT INTO dbo.Mua_Xe (MaKH, MaXe, NgayMua, SoLuong, GhiChu) VALUES
('KH01', 'XE01',  '2011-10-28', 1, N'Khách mua tiền mặt'),
('KH02', 'XE02',  '2012-05-15', 1, N'Tặng mũ bảo hiểm'),
('KH03', 'XE02',  '2012-06-20', 2, N'Mua theo lô công ty'),
('KH04', 'XE03',  '2012-08-12', 1, N'Bảo hành 3 năm'),
('KH01', 'KH001', '2012-10-29', 3, N''),
('KH02', 'KH001', '2012-10-28', 1, N'Laptop');
GO
