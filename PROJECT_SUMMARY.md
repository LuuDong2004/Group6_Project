# Tóm Tắt Dự Án Cinema Project - Group 6

## Thông Tin Chung
- **Tên dự án**: Cinema Project
- **Nhóm**: Group 6
- **Công nghệ**: Spring Boot 3.5.0, Java 21
- **Database**: Microsoft SQL Server
- **Framework**: Spring MVC, Spring Security, Spring Data JPA

## Mô Tả Dự Án
Hệ thống quản lý rạp chiếu phim toàn diện với các chức năng đặt vé online, quản lý lịch chiếu, và hệ thống phân quyền người dùng.

## Kiến Trúc Hệ Thống

### 1. Công Nghệ Sử Dụng
- **Backend**: Spring Boot 3.5.0
- **Security**: Spring Security 6 với OAuth2
- **Database**: SQL Server với Spring Data JPA
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Email**: Spring Mail
- **Validation**: Spring Validation

### 2. Cấu Trúc Dự Án
```
src/main/java/group6/cinema_project/
├── config/          # Cấu hình hệ thống
├── controller/      # Controllers xử lý request
├── dto/            # Data Transfer Objects
├── entity/         # JPA Entities
├── exception/      # Exception handling
├── repository/     # Data access layer
├── security/       # Security configuration
└── service/        # Business logic layer
```

## Các Entity Chính

### 1. User (Người Dùng)
- **Thuộc tính**: id, userName, phone, email, password, dateOfBirth, address
- **Role**: USER, ADMIN, STAFF
- **Provider**: LOCAL, GOOGLE (OAuth2)
- **Validation**: Email format, username length, password strength

### 2. Movie (Phim)
- **Thuộc tính**: id, name, image, duration, releaseDate, rating, genre, language, trailer, description
- **Quan hệ**: Many-to-Many với Actor và Director
- **Chức năng**: Tìm kiếm theo thể loại, rating, phân trang

### 3. Schedule (Lịch Chiếu)
- **Thuộc tính**: id, screeningDate, startTime, endTime
- **Quan hệ**: Many-to-One với Movie, ScreeningRoom, Branch
- **Chức năng**: Lọc theo phim, rạp, ngày chiếu

### 4. Seat (Ghế Ngồi)
- **Thuộc tính**: id, name, row
- **Quan hệ**: Many-to-One với ScreeningRoom
- **Chức năng**: Hiển thị trạng thái ghế (trống/đã đặt)

### 5. Ticket (Vé)
- **Thuộc tính**: id, qrCode, description, price
- **Quan hệ**: Many-to-One với Seat, Schedule, Invoice
- **Chức năng**: Tạo QR code, quản lý vé đã mua

### 6. Branch (Chi Nhánh)
- **Thuộc tính**: id, name, description, address
- **Quan hệ**: One-to-Many với ScreeningRoom

### 7. ScreeningRoom (Phòng Chiếu)
- **Thuộc tính**: id, name, capacity, description
- **Quan hệ**: Many-to-One với Branch

### 8. Invoice (Hóa Đơn)
- **Thuộc tính**: id, paymentDateTime
- **Quan hệ**: Many-to-One với User, Employee

### 9. Food (Đồ Ăn)
- **Thuộc tính**: id, name, price, size, description, image
- **Chức năng**: Quản lý combo đồ ăn

### 10. Actor & Director (Diễn Viên & Đạo Diễn)
- **Thuộc tính**: id, name, imageUrl, description
- **Quan hệ**: Many-to-Many với Movie

## Chức Năng Chính

### 1. Hệ Thống Người Dùng
- **Đăng ký/Đăng nhập**: Local và OAuth2 (Google)
- **Quản lý profile**: Cập nhật thông tin cá nhân
- **Phân quyền**: USER, ADMIN, STAFF với các quyền khác nhau

### 2. Quản Lý Phim
- **CRUD phim**: Thêm, sửa, xóa, xem phim
- **Upload hình ảnh**: Poster và trailer
- **Tìm kiếm**: Theo thể loại, rating, tên phim
- **Phân trang**: Hiển thị danh sách phim

### 3. Hệ Thống Đặt Vé
- **Chọn phim**: Xem thông tin chi tiết phim
- **Chọn lịch chiếu**: Theo rạp và thời gian
- **Chọn ghế**: Giao diện trực quan với trạng thái ghế
- **Thanh toán**: Tạo hóa đơn và vé điện tử

### 4. Quản Lý Admin
- **Dashboard**: Thống kê tổng quan
- **Quản lý người dùng**: CRUD users, reset password
- **Quản lý phim**: CRUD movies với upload file
- **Quản lý lịch chiếu**: Tạo và quản lý schedule
- **Quản lý đồ ăn**: CRUD food items

### 5. Hệ Thống Staff
- **Dashboard riêng**: Giao diện dành cho nhân viên
- **Quản lý vé**: Xem và xử lý vé
- **Quản lý lịch chiếu**: Xem lịch chiếu
- **Quyền hạn hạn chế**: Chỉ xem, không chỉnh sửa

## Bảo Mật

### 1. Spring Security
- **Authentication**: Form login và OAuth2
- **Authorization**: Role-based access control
- **Password**: BCrypt encoding
- **Session**: Management với max sessions

### 2. Validation
- **Input validation**: Bean Validation
- **XSS protection**: Thymeleaf escaping
- **CSRF protection**: Spring Security CSRF

### 3. Email Security
- **Password reset**: Secure email với random password
- **Admin notifications**: Email thông báo admin actions

## Tính Năng Đặc Biệt

### 1. OAuth2 Integration
- **Google Login**: Đăng nhập bằng tài khoản Google
- **Custom OAuth2 Service**: Xử lý user info từ Google

### 2. Email System
- **SMTP Configuration**: Gmail SMTP
- **Password Reset**: Admin có thể reset password user
- **Notifications**: Email thông báo các hoạt động

### 3. File Upload
- **Movie Images**: Upload poster phim
- **Food Images**: Upload hình ảnh đồ ăn
- **Secure Storage**: Lưu trữ file an toàn

### 4. Advanced Search
- **Movie Search**: Theo genre, rating, tên
- **Schedule Filter**: Theo phim, rạp, ngày
- **Pagination**: Phân trang cho danh sách lớn

## Cấu Hình Hệ Thống

### 1. Database
- **SQL Server**: Primary database
- **Connection Pool**: HikariCP
- **JPA**: Hibernate ORM

### 2. Email
- **SMTP**: Gmail configuration
- **Credentials**: staffcinemaa@gmail.com

### 3. Security
- **Password Policy**: Minimum 6 characters
- **Session Timeout**: Configurable
- **Role Hierarchy**: ADMIN > STAFF > USER

## Hướng Dẫn Sử Dụng

### 1. Admin
- **URL**: `/admin/login`
- **Chức năng**: Quản lý toàn bộ hệ thống

### 2. Staff
- **URL**: `/staff/login`
- **Credentials**: staff@cinema.com / 123456
- **Chức năng**: Hỗ trợ khách hàng, quản lý vé

### 3. User
- **URL**: `/login`
- **Chức năng**: Đặt vé, quản lý profile

## Tài Liệu Kỹ Thuật
- `ADMIN_PASSWORD_RESET_README.md`: Hướng dẫn reset password
- `STAFF_LOGIN_GUIDE.md`: Hướng dẫn đăng nhập staff
- `create_staff_account.sql`: Script tạo tài khoản staff

## Kết Luận
Cinema Project là một hệ thống quản lý rạp chiếu phim hoàn chỉnh với:
- **Kiến trúc**: MVC pattern với Spring Boot
- **Bảo mật**: Multi-layer security với Spring Security
- **Tính năng**: Đầy đủ từ đặt vé đến quản lý admin
- **Mở rộng**: Dễ dàng thêm tính năng mới
- **Bảo trì**: Code structure rõ ràng, dễ maintain
