/* Ôn tập lần 2 - SQL Server. Chạy bằng SSMS hoặc sqlcmd (hỗ trợ lệnh GO). */
IF DB_ID(N'QLHangHoa') IS NULL
    CREATE DATABASE QLHangHoa;
GO

USE QLHangHoa;
GO

IF OBJECT_ID(N'dbo.LoaiHang', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LoaiHang (
        MaLoai INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_LoaiHang PRIMARY KEY,
        TLH NVARCHAR(50) NOT NULL,
        TyLe INT NOT NULL,
        CONSTRAINT UQ_LoaiHang_TLH UNIQUE (TLH),
        CONSTRAINT CK_LoaiHang_TLH CHECK (LEN(LTRIM(RTRIM(TLH))) > 0),
        CONSTRAINT CK_LoaiHang_TyLe CHECK (TyLe BETWEEN 0 AND 100)
    );
END;
GO

/* Danh mục riêng để nhóm Tính chất có thể Thêm/Xóa độc lập. */
IF OBJECT_ID(N'dbo.TinhChat', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.TinhChat (
        TenTinhChat NVARCHAR(50) NOT NULL CONSTRAINT PK_TinhChat PRIMARY KEY,
        CONSTRAINT CK_TinhChat_Ten CHECK (LEN(LTRIM(RTRIM(TenTinhChat))) > 0)
    );
END;
GO

IF OBJECT_ID(N'dbo.HangHoa', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HangHoa (
        MHH INT NOT NULL CONSTRAINT PK_HangHoa PRIMARY KEY,
        THH NVARCHAR(50) NOT NULL,
        MaLoai INT NOT NULL,
        TinhChat NVARCHAR(50) NOT NULL,
        CONSTRAINT CK_HangHoa_MHH CHECK (MHH BETWEEN 10000 AND 99999),
        CONSTRAINT CK_HangHoa_THH CHECK (LEN(LTRIM(RTRIM(THH))) > 0),
        CONSTRAINT FK_HangHoa_LoaiHang FOREIGN KEY (MaLoai)
            REFERENCES dbo.LoaiHang(MaLoai),
        CONSTRAINT FK_HangHoa_TinhChat FOREIGN KEY (TinhChat)
            REFERENCES dbo.TinhChat(TenTinhChat)
    );
END;
GO

/* Nếu bảng HangHoa đã được restore, bổ sung tính chất hiện có vào danh mục. */
INSERT INTO dbo.TinhChat (TenTinhChat)
SELECT DISTINCT LTRIM(RTRIM(H.TinhChat))
FROM dbo.HangHoa AS H
WHERE H.TinhChat IS NOT NULL
  AND LEN(LTRIM(RTRIM(H.TinhChat))) > 0
  AND NOT EXISTS (
      SELECT 1 FROM dbo.TinhChat AS T
      WHERE T.TenTinhChat = LTRIM(RTRIM(H.TinhChat))
  );
GO

/* Dữ liệu mẫu được nạp chỉ khi danh mục đang rỗng. */
IF NOT EXISTS (SELECT 1 FROM dbo.LoaiHang)
BEGIN
    INSERT INTO dbo.LoaiHang (TLH, TyLe) VALUES
        (N'Điện tử', 15),
        (N'Gia dụng', 10),
        (N'Văn phòng', 5);
END;

IF NOT EXISTS (SELECT 1 FROM dbo.TinhChat)
BEGIN
    INSERT INTO dbo.TinhChat (TenTinhChat) VALUES
        (N'Mới'),
        (N'Đã qua sử dụng'),
        (N'Cần bảo trì');
END;
GO

/* Nếu restore bảng cũ, báo lỗi rõ ràng khi mã loại/tính chất không hợp lệ. */
IF EXISTS (
    SELECT 1 FROM dbo.HangHoa AS H
    LEFT JOIN dbo.LoaiHang AS L ON H.MaLoai = L.MaLoai
    WHERE L.MaLoai IS NULL
)
    THROW 51001, N'HangHoa có MaLoai không tồn tại trong LoaiHang; hãy sửa dữ liệu trước khi tạo khóa ngoại.', 1;

IF EXISTS (
    SELECT 1 FROM dbo.HangHoa AS H
    LEFT JOIN dbo.TinhChat AS T ON H.TinhChat = T.TenTinhChat
    WHERE T.TenTinhChat IS NULL
)
    THROW 51002, N'HangHoa có TinhChat không tồn tại trong danh mục; hãy sửa dữ liệu trước khi tạo khóa ngoại.', 1;
GO

/* Nếu bảng cũ đã tồn tại và hợp lệ, bổ sung các quan hệ còn thiếu. */
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_HangHoa_LoaiHang'
               AND parent_object_id = OBJECT_ID(N'dbo.HangHoa'))
BEGIN
    ALTER TABLE dbo.HangHoa WITH CHECK
        ADD CONSTRAINT FK_HangHoa_LoaiHang
        FOREIGN KEY (MaLoai) REFERENCES dbo.LoaiHang(MaLoai);
END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_HangHoa_TinhChat'
               AND parent_object_id = OBJECT_ID(N'dbo.HangHoa'))
BEGIN
    ALTER TABLE dbo.HangHoa WITH CHECK
        ADD CONSTRAINT FK_HangHoa_TinhChat
        FOREIGN KEY (TinhChat) REFERENCES dbo.TinhChat(TenTinhChat);
END;
GO
