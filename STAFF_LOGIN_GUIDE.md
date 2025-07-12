# Hướng Dẫn Đăng Nhập Staff

## Vấn đề đã được sửa:

1. ✅ **Tạo StaffController riêng biệt** với endpoint `/staff/login`
2. ✅ **Cập nhật SecurityConfig** để cho phép staff truy cập
3. ✅ **Tạo template staff_login.html** với giao diện riêng
4. ✅ **Tạo staff dashboard** và các trang quản lý

## Cách tạo tài khoản staff:

### Phương pháp 1: Sử dụng Admin Panel
1. Đăng nhập với tài khoản admin
2. Vào `/admin/users/management`
3. Thêm user mới hoặc thay đổi role của user hiện có thành STAFF

### Phương pháp 2: Sử dụng SQL Script
Chạy file `create_staff_account.sql` trong database:

```sql
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
```

### Phương pháp 3: Sử dụng Admin Controller (Test)
Truy cập `/admin/create-staff` để tạo tài khoản staff tự động.

## Thông tin đăng nhập staff:

- **Email:** staff@cinema.com
- **Password:** 123456
- **URL đăng nhập:** `/staff/login`

## Các trang staff có thể truy cập:

- `/staff/dashboard` - Dashboard chính
- `/staff/tickets` - Quản lý vé
- `/staff/schedules` - Quản lý lịch chiếu
- `/admin/movies/list` - Xem danh sách phim
- `/admin/foods/list` - Xem danh sách combo food

## Lưu ý:

- Staff có thể truy cập các trang admin nhưng không thể thêm/sửa/xóa
- Chỉ admin mới có quyền quản lý user và phân quyền
- Staff có thể xem thông tin nhưng không thể thay đổi cấu hình hệ thống 