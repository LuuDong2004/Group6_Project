# Cinema Project - Payment Flow Documentation

## Tổng quan luồng thanh toán

Luồng thanh toán đã được cải thiện với các tính năng sau:

1. **Tạo QR Code Sepay**: Người dùng bấm thanh toán → gọi API tạo QR Sepay
2. **Quét QR và thanh toán**: Người dùng quét QR bằng app ngân hàng
3. **Webhook xử lý**: Sepay gọi webhook khi thanh toán hoàn tất
4. **Cập nhật trạng thái**: Hệ thống cập nhật trạng thái booking và ghế
5. **Gửi email**: Tự động gửi vé điện tử qua email
6. **Chuyển trang thành công**: Chuyển đến trang xác nhận thanh toán

## Các API Endpoints

### 1. Tạo QR Code Sepay
```
POST /payment/sepay/create
Content-Type: application/json

{
    "orderId": 123,
    "amount": 150000,
    "description": "Thanh toán vé xem phim",
    "merchantCode": "CINEMA_PROJECT_001",
    "callbackUrl": "https://your-domain.com/payment/sepay/webhook"
}
```

### 2. Kiểm tra trạng thái thanh toán
```
GET /payment/sepay/check-status?transactionId=MOCK_TXN_123
```

### 3. Webhook nhận thông báo từ Sepay
```
POST /payment/sepay/webhook
Content-Type: application/json

{
    "transactionId": "MOCK_TXN_123",
    "status": "COMPLETED"
}
```

### 4. Hủy thanh toán
```
POST /payment/sepay/cancel
Content-Type: application/x-www-form-urlencoded

transactionId=MOCK_TXN_123
```

## Testing Endpoints (Development Only)

### 1. Test Complete Payment
```
POST /payment/sepay/test-complete
Content-Type: application/x-www-form-urlencoded

transactionId=MOCK_TXN_123
```

### 2. Test Webhook
```
POST /payment/sepay/test-webhook
Content-Type: application/x-www-form-urlencoded

transactionId=MOCK_TXN_123&status=COMPLETED
```

### 3. Debug Transactions
```
GET /payment/sepay/debug
```

## Luồng hoạt động chi tiết

### Bước 1: Người dùng chọn ghế và tạo booking
- Người dùng chọn ghế trên trang seat selection
- Hệ thống tạo booking với trạng thái "PENDING"
- Chuyển đến trang thanh toán

### Bước 2: Tạo QR Code thanh toán
- Người dùng bấm nút "Thanh toán"
- Hệ thống gọi API `/payment/sepay/create`
- Tạo QR code và transaction ID
- Hiển thị QR code cho người dùng quét

### Bước 3: Quét QR và thanh toán
- Người dùng quét QR bằng app ngân hàng
- Thực hiện thanh toán trong app ngân hàng
- Sepay xử lý thanh toán

### Bước 4: Webhook xử lý
- Sepay gọi webhook `/payment/sepay/webhook` khi thanh toán hoàn tất
- Hệ thống nhận thông báo và cập nhật trạng thái
- Gọi `bookingService.confirmBookingPaid()` để:
  - Cập nhật trạng thái booking thành "PAID"
  - Cập nhật trạng thái ghế thành "RESERVED"
  - Gửi email vé điện tử

### Bước 5: Chuyển trang thành công
- Frontend tự động kiểm tra trạng thái mỗi 3 giây
- Khi phát hiện thanh toán thành công, chuyển đến `/payment/success`
- Hiển thị thông tin vé và xác nhận thanh toán

## Cách test luồng thanh toán

### 1. Tạo booking test
```
1. Truy cập trang seat selection
2. Chọn ghế và tạo booking
3. Chuyển đến trang thanh toán
```

### 2. Test thanh toán thành công
```
1. Trên trang sepay-payment, sử dụng nút "Test Complete Payment"
2. Hệ thống sẽ simulate thanh toán thành công
3. Kiểm tra:
   - Trạng thái booking được cập nhật thành "PAID"
   - Trạng thái ghế được cập nhật thành "RESERVED"
   - Email vé điện tử được gửi
   - Chuyển đến trang success
```

### 3. Test hủy thanh toán
```
1. Sử dụng nút "Test Cancel Payment"
2. Kiểm tra trạng thái booking được cập nhật thành "CANCELLED"
3. Ghế được giải phóng
```

### 4. Test webhook trực tiếp
```
POST /payment/sepay/test-webhook
Content-Type: application/x-www-form-urlencoded

transactionId=MOCK_TXN_123&status=COMPLETED
```

## Cấu hình

### Environment Variables
```properties
# Domain cho webhook callback
domain=https://your-domain.com

# Sepay webhook API key (nếu cần)
SEPAY_WEBHOOK_APIKEY=your_api_key
```

### Database
Đảm bảo các bảng sau có đủ dữ liệu:
- `booking`: Lưu thông tin đặt vé
- `seat_reservation`: Lưu trạng thái ghế
- `user`: Thông tin người dùng
- `schedule`: Lịch chiếu phim

## Troubleshooting

### 1. QR Code không hiển thị
- Kiểm tra API Sepay có hoạt động không
- Kiểm tra console log để xem lỗi
- Sử dụng mock QR code để test

### 2. Webhook không nhận được
- Kiểm tra URL webhook có đúng không
- Kiểm tra network connectivity
- Sử dụng test endpoint để simulate

### 3. Email không được gửi
- Kiểm tra cấu hình email service
- Kiểm tra SendGrid API key
- Kiểm tra template email có tồn tại không

### 4. Trạng thái không được cập nhật
- Kiểm tra database connection
- Kiểm tra transaction mapping
- Sử dụng debug endpoint để xem trạng thái

## Lưu ý quan trọng

1. **Production**: Thay thế mock API bằng Sepay API thật
2. **Security**: Thêm validation và authentication cho webhook
3. **Error Handling**: Xử lý các trường hợp lỗi network, timeout
4. **Logging**: Thêm logging chi tiết để debug
5. **Monitoring**: Monitor webhook calls và payment status
6. **Database**: Sử dụng database thay vì in-memory maps cho production 