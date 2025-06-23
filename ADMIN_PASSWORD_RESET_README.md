# Hướng dẫn sử dụng tính năng Admin Reset Password

## Tổng quan
Tính năng Admin Reset Password cho phép admin có thể reset mật khẩu cho bất kỳ user nào trong hệ thống với nhiều tùy chọn linh hoạt.

## Các tính năng đã triển khai

### 1. DTOs
- **AdminPasswordResetDto**: DTO cho admin reset password với các tùy chọn:
  - `userId`: ID của user cần reset
  - `sendEmail`: Có gửi email thông báo không
  - `customPassword`: Mật khẩu tùy chỉnh
  - `generateRandomPassword`: Tạo mật khẩu ngẫu nhiên

### 2. Services
- **EmailService**: Thêm method `sendAdminPasswordResetEmail`
- **UserService**: Thêm method `adminResetPassword`

### 3. Controllers
- **AdminUserController**: Thêm các endpoint cho admin reset password

### 4. Templates
- **admin_user_view.html**: Cập nhật UI với modal reset password

## Các endpoint

### Web Endpoints
- `GET /admin/users/reset-password/{id}`: Reset password đơn giản (legacy)
- `POST /admin/users/reset-password/{id}`: Reset password với tùy chọn

### API Endpoints
- `POST /admin/users/api/reset-password`: API reset password

## Luồng hoạt động

### 1. Admin truy cập trang user
1. Admin vào `/admin/users/list`
2. Click vào user cần reset password
3. Trang hiển thị thông tin user với nút "Reset Mật khẩu"

### 2. Admin reset password
1. Click nút "Reset Mật khẩu"
2. Modal hiển thị với các tùy chọn:
   - Tạo mật khẩu ngẫu nhiên
   - Nhập mật khẩu tùy chỉnh
   - Gửi email thông báo
3. Admin chọn tùy chọn và submit
4. Hệ thống xử lý và hiển thị kết quả

### 3. Kết quả
- Nếu gửi email: User nhận email với mật khẩu mới
- Nếu không gửi email: Admin thấy mật khẩu mới trên màn hình
- Thông báo thành công/thất bại

## Các tùy chọn reset password

### 1. Tạo mật khẩu ngẫu nhiên
- Hệ thống tự động tạo mật khẩu 8 ký tự
- Bao gồm chữ hoa, chữ thường, số
- An toàn và khó đoán

### 2. Nhập mật khẩu tùy chỉnh
- Admin có thể nhập mật khẩu theo ý muốn
- Validation: ít nhất 6 ký tự
- Phù hợp cho trường hợp đặc biệt

### 3. Gửi email thông báo
- **Bật**: User nhận email với mật khẩu mới
- **Tắt**: Admin thấy mật khẩu mới trên màn hình
- Email chứa thông tin admin thực hiện

## Bảo mật

### 1. Admin Authentication
- Chỉ admin mới có quyền reset password
- Kiểm tra role ADMIN trong SecurityConfig

### 2. Password Security
- Mật khẩu được mã hóa bằng BCrypt
- Validation mật khẩu tùy chỉnh
- Không lưu mật khẩu plain text

### 3. Audit Trail
- Email chứa thông tin admin thực hiện
- Log các hoạt động reset password
- Theo dõi suspicious activities

## Cấu hình

### 1. Email Configuration
Đảm bảo email đã được cấu hình trong `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=staffcinemaa@gmail.com
spring.mail.password=staffcinema1
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 2. Security Configuration
Đảm bảo admin có quyền truy cập các endpoint:
```java
.antMatchers("/admin/**").hasRole("ADMIN")
```

## Sử dụng

### 1. Qua Web Interface
1. Đăng nhập admin
2. Vào "Quản lý người dùng"
3. Click vào user cần reset
4. Click "Reset Mật khẩu"
5. Chọn tùy chọn và submit

### 2. Qua API
```bash
POST /admin/users/api/reset-password
Content-Type: application/json

{
  "userId": 1,
  "sendEmail": true,
  "generateRandomPassword": true,
  "customPassword": null
}
```

## Response Format

### Success Response
```json
{
  "success": true,
  "message": "Reset mật khẩu thành công!",
  "user": {
    "id": 1,
    "userName": "testuser",
    "email": "test@example.com",
    ...
  },
  "newPassword": "abc123" // Chỉ khi không gửi email
}
```

### Error Response
```json
{
  "success": false,
  "message": "Không tìm thấy người dùng với ID: 999"
}
```

## Troubleshooting

### 1. Email không gửi được
- Kiểm tra cấu hình SMTP
- Kiểm tra username/password
- Kiểm tra firewall/antivirus

### 2. Permission denied
- Kiểm tra role ADMIN
- Kiểm tra SecurityConfig
- Kiểm tra session

### 3. User không tồn tại
- Kiểm tra user ID
- Kiểm tra database connection
- Kiểm tra user status

## Cải tiến có thể thực hiện

### 1. Bulk Reset
- Reset password cho nhiều user cùng lúc
- Import từ file Excel/CSV

### 2. Password Policy
- Kiểm tra mật khẩu theo policy
- Yêu cầu mật khẩu phức tạp hơn

### 3. Notification
- Gửi SMS thông báo
- Push notification
- Slack/Discord integration

### 4. Audit Log
- Log chi tiết các hoạt động
- Export audit report
- Alert suspicious activities

### 5. Rate Limiting
- Giới hạn số lần reset trong 1 giờ
- Cooldown period
- Admin approval cho reset nhiều user 