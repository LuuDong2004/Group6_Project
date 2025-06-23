# Hướng dẫn sử dụng tính năng Reset Password

## Tổng quan
Tính năng reset password cho phép user quên mật khẩu có thể đặt lại mật khẩu thông qua email một cách an toàn.

## Các tính năng đã triển khai

### 1. Entity và Repository
- **PasswordResetToken**: Entity lưu trữ token reset password
- **PasswordResetTokenRepository**: Repository để thao tác với database

### 2. DTOs
- **PasswordResetRequestDto**: DTO cho request reset password (chỉ cần email)
- **PasswordResetConfirmDto**: DTO cho confirm reset password (token + mật khẩu mới)

### 3. Services
- **EmailService**: Interface và implementation để gửi email
- **UserService**: Thêm các method reset password

### 4. Controllers
- **AuthController**: Thêm các endpoint cho reset password

### 5. Templates
- **forgot_password.html**: Trang nhập email để request reset
- **reset_password.html**: Trang nhập mật khẩu mới

## Cấu hình cần thiết

### 1. Database
Chạy script SQL `create_password_reset_table.sql` để tạo bảng `password_reset_tokens`.

### 2. Email Configuration
Cập nhật file `application.properties` với thông tin email thực tế:

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Application Configuration
app.base-url=http://localhost:8080
```

**Lưu ý**: Để sử dụng Gmail, bạn cần:
1. Bật 2-factor authentication
2. Tạo App Password
3. Sử dụng App Password thay vì mật khẩu thông thường

### 3. Base URL
Cập nhật `app.base-url` trong `application.properties` theo domain thực tế của ứng dụng.

## Luồng hoạt động

### 1. User quên mật khẩu
1. User truy cập `/forgot-password`
2. Nhập email
3. Hệ thống kiểm tra email có tồn tại không
4. Nếu tồn tại, tạo token và gửi email
5. Nếu không tồn tại, vẫn hiển thị thông báo thành công (bảo mật)

### 2. User nhận email và reset password
1. User click link trong email
2. Link dẫn đến `/reset-password/confirm?token=xxx`
3. Hệ thống validate token
4. User nhập mật khẩu mới
5. Hệ thống cập nhật mật khẩu và gửi email xác nhận

## Các endpoint

### Web Endpoints
- `GET /forgot-password`: Trang nhập email
- `POST /forgot-password`: Xử lý request reset password
- `GET /reset-password/confirm`: Trang nhập mật khẩu mới
- `POST /reset-password/confirm`: Xử lý confirm reset password

### API Endpoints
- `POST /api/forgot-password`: API request reset password
- `POST /api/reset-password/confirm`: API confirm reset password

## Bảo mật

### 1. Token Security
- Token được tạo ngẫu nhiên bằng UUID
- Token có thời gian hết hạn 24 giờ
- Token chỉ sử dụng được 1 lần
- Token được xóa sau khi sử dụng

### 2. Email Security
- Không tiết lộ thông tin user tồn tại hay không
- Email chứa link an toàn với token
- Gửi email xác nhận sau khi reset thành công

### 3. Password Security
- Mật khẩu được mã hóa bằng BCrypt
- Validation mật khẩu mới
- Yêu cầu xác nhận mật khẩu

## Testing

### 1. Test với email thật
1. Cấu hình email trong `application.properties`
2. Đăng ký user với email thật
3. Test flow reset password

### 2. Test với email giả
1. Sử dụng service như Mailtrap hoặc Ethereal
2. Cấu hình trong `application.properties`
3. Test flow mà không gửi email thật

## Troubleshooting

### 1. Email không gửi được
- Kiểm tra cấu hình SMTP
- Kiểm tra username/password
- Kiểm tra firewall/antivirus

### 2. Token không hợp lệ
- Kiểm tra thời gian hết hạn
- Kiểm tra token đã được sử dụng chưa
- Kiểm tra database connection

### 3. Database errors
- Chạy script tạo bảng
- Kiểm tra foreign key constraints
- Kiểm tra database permissions

## Cải tiến có thể thực hiện

### 1. Rate Limiting
- Giới hạn số lần request reset password
- Giới hạn số email gửi trong 1 giờ

### 2. Captcha
- Thêm captcha cho form forgot password
- Ngăn chặn spam

### 3. Audit Log
- Log các hoạt động reset password
- Theo dõi suspicious activities

### 4. Multiple Email Providers
- Hỗ trợ nhiều email provider
- Fallback khi provider chính lỗi

### 5. SMS Reset
- Thêm option reset qua SMS
- 2FA cho reset password 