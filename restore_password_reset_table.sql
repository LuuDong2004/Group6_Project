-- Script khôi phục bảng password_reset_tokens từ bảng pwd_reset_tokens
-- Chạy script này để quay lại sử dụng bảng cũ

USE CinemaDB;

-- Bước 1: Tạo bảng password_reset_tokens mới
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'password_reset_tokens')
BEGIN
    CREATE TABLE password_reset_tokens (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        token VARCHAR(255) NOT NULL UNIQUE,
        user_id INT NOT NULL,
        expiry_date DATETIME2 NOT NULL,
        used BIT NOT NULL DEFAULT 0,
        FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
    );
    PRINT 'Đã tạo bảng password_reset_tokens';
END
ELSE
BEGIN
    PRINT 'Bảng password_reset_tokens đã tồn tại';
END

-- Bước 2: Tạo index cho bảng mới
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_password_reset_tokens_token' AND object_id = OBJECT_ID('password_reset_tokens'))
BEGIN
    CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens(token);
    PRINT 'Đã tạo index idx_password_reset_tokens_token';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_password_reset_tokens_user_id' AND object_id = OBJECT_ID('password_reset_tokens'))
BEGIN
    CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
    PRINT 'Đã tạo index idx_password_reset_tokens_user_id';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_password_reset_tokens_expiry_date' AND object_id = OBJECT_ID('password_reset_tokens'))
BEGIN
    CREATE INDEX idx_password_reset_tokens_expiry_date ON password_reset_tokens(expiry_date);
    PRINT 'Đã tạo index idx_password_reset_tokens_expiry_date';
END

-- Bước 3: Copy dữ liệu từ bảng pwd_reset_tokens sang password_reset_tokens
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'pwd_reset_tokens')
BEGIN
    INSERT INTO password_reset_tokens (token, user_id, expiry_date, used)
    SELECT token, user_id, expiry_date, used FROM pwd_reset_tokens;
    
    PRINT 'Đã copy dữ liệu từ pwd_reset_tokens sang password_reset_tokens';
    
    -- Hiển thị số lượng record đã copy
    SELECT COUNT(*) as copied_records FROM password_reset_tokens;
END
ELSE
BEGIN
    PRINT 'Bảng pwd_reset_tokens không tồn tại, bỏ qua copy dữ liệu';
END

-- Bước 4: Verify bảng mới
SELECT 'Bảng password_reset_tokens:' as info;
SELECT COUNT(*) as total_records FROM password_reset_tokens;

-- Bước 5: Hiển thị cấu trúc bảng
SELECT 'Cấu trúc bảng password_reset_tokens:' as info;
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'password_reset_tokens' 
ORDER BY ORDINAL_POSITION; 