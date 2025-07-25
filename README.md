🎬 Cinema_Project
Dự án quản lý rạp chiếu phim, hỗ trợ đặt vé, quản lý lịch chiếu, blog, voucher, quản trị viên và người dùng.
🚀 Tính năng chính
Đặt vé xem phim trực tuyến
Quản lý lịch chiếu, phòng chiếu, ghế ngồi
Quản lý phim, diễn viên, đạo diễn, suất chiếu
Quản lý blog, voucher, thực phẩm
Hệ thống phân quyền: Admin, User
Xác thực OAuth2, bảo mật Spring Security
Giao diện web hiện đại, responsive
🗂️ Cấu trúc thư mục
Apply to PaymentContr...
⚙️ Hướng dẫn cài đặt & chạy dự án
1. Yêu cầu
Java 11 trở lên
Maven 3.6+
SQL Server (Microsoft SQL Server)
2. Cài đặt
Apply to PaymentContr...
Run
3. Cấu hình kết nối SQL Server
Mở file src/main/resources/application.properties (hoặc application.yml) và cấu hình như sau:
Apply to PaymentContr...
> Lưu ý:
> - Thay YOUR_SQLSERVER_USERNAME và YOUR_SQLSERVER_PASSWORD bằng thông tin của bạn.
> - Đảm bảo SQL Server đã tạo database cinema_db hoặc đổi tên cho phù hợp.
4. Chạy ứng dụng
Apply to PaymentContr...
Run
Hoặc chạy file jar:
Apply to PaymentContr...
Run
5. Truy cập giao diện web
Người dùng: http://localhost:8080/web/index.html
Admin: http://localhost:8080/admin
📝 Đóng góp
Fork dự án, tạo branch mới, commit và gửi pull request.
Mọi đóng góp đều được hoan nghênh!
📄 Bản quyền
Dự án thuộc nhóm 6 - Đồ án Java Spring Boot 2025
