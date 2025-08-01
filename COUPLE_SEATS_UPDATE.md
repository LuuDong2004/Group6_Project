/ừừ# Cập nhật Hiển thị Ghế Couple

## Mô tả vấn đề
Trước đây, các ghế couple được hiển thị như những ghế riêng biệt thay vì ghế đôi (2 ghế ghép thành 1). Điều này làm cho người dùng khó phân biệt được đâu là ghế couple thực sự.

## Các thay đổi đã thực hiện

### 1. Cập nhật file `screening_room_edit.html`

#### Thay đổi CSS:
- Thêm class `.couple-seat` với kích thước gấp đôi ghế thường (64px x 32px)
- Thêm đường kẻ phân chia ở giữa ghế couple để thể hiện đây là ghế đôi
- Cập nhật border-radius cho ghế couple

#### Thay đổi JavaScript:
- Cập nhật hàm `renderSeatMap()` để hiển thị ghế couple như ghế đôi
- Thay đổi từ CSS Grid sang Flexbox để dễ dàng xử lý ghế couple
- Cập nhật logic tính toán số ghế thực tế bao gồm cả ghế couple
- Cập nhật thông tin hiển thị để hiển thị số ghế couple thực tế

#### Thay đổi mô tả:
- Cập nhật mô tả để giải thích rõ ghế couple là ghế đôi (2 ghế ghép thành 1)

### 2. Cập nhật file `ticket-booking-seat.html`

#### Thay đổi CSS:
- Thêm CSS đặc biệt cho ghế couple với kích thước gấp đôi
- Thêm đường kẻ phân chia ở giữa ghế couple

#### Thay đổi JavaScript:
- Cập nhật logic xử lý loại ghế để bao gồm ghế couple
- Thêm xử lý đặc biệt cho ghế couple khi hiển thị

## Kết quả

### Trước khi cập nhật:
- Ghế couple hiển thị như 2 ghế riêng biệt
- Khó phân biệt với ghế thường
- Không thể hiện được đặc điểm ghế đôi

### Sau khi cập nhật:
- Ghế couple hiển thị như 1 ghế đôi (kích thước gấp đôi)
- Có đường kẻ phân chia ở giữa để thể hiện đây là ghế đôi
- Dễ dàng phân biệt với các loại ghế khác
- Số ghế được tính toán chính xác (ghế couple = 1 ghế đôi thay vì 2 ghế riêng)

## Cách hoạt động

1. **Trong trang chỉnh sửa phòng chiếu:**
   - Ghế couple được hiển thị với kích thước 64px x 32px (gấp đôi ghế thường 32px x 32px)
   - Có đường kẻ phân chia ở giữa để thể hiện đây là ghế đôi
   - Số ghế couple thực tế = số ghế mỗi hàng / 2

2. **Trong trang đặt vé:**
   - Ghế couple được xử lý đặc biệt với kích thước lớn hơn
   - Vẫn giữ nguyên logic đặt vé nhưng hiển thị rõ ràng hơn

## Lưu ý

- Các thay đổi này chỉ ảnh hưởng đến giao diện hiển thị
- Logic backend vẫn tạo ghế couple như những ghế riêng biệt (để đảm bảo tương thích)
- Có thể cần cập nhật thêm logic backend trong tương lai để tối ưu hóa việc lưu trữ ghế couple

## Files đã thay đổi

1. `src/main/resources/templates/admin2/screening_room_edit.html`
2. `src/main/resources/templates/ticket-booking-seat.html` 