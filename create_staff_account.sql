-- Script tạo tài khoản staff cho Cinema Project
-- Chạy script này trong database để tạo tài khoản staff

-- Tạo tài khoản staff
INSERT INTO Users (user_name, phone, email, password, date_of_birth, address, role, provider) 
VALUES (
    'Staff User', 
    '0123456789', 
    'staff@cinema.com', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- password: 123456
    '1990-01-01', 
    'Hà Nội', 
    'STAFF', 
    'LOCAL'
);

-- Tạo tài khoản admin (nếu chưa có)
INSERT INTO Users (user_name, phone, email, password, date_of_birth, address, role, provider) 
VALUES (
    'Admin User', 
    '0987654321', 
    'admin@cinema.com', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- password: 123456
    '1985-01-01', 
    'Hà Nội', 
    'ADMIN', 
    'LOCAL'
);

-- Kiểm tra kết quả
SELECT id, user_name, email, role, provider FROM Users WHERE role IN ('STAFF', 'ADMIN'); 