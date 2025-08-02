# Test Kịch Bản Xung Đột Lịch Chiếu

## Mô tả cải thiện

Đã cải thiện xử lý lỗi khi thêm lịch chiếu bị trùng với các thay đổi sau:

### 1. Cải thiện ScheduleConflictException

- Thêm emoji và format đẹp hơn cho thông báo lỗi
- Hiển thị thông tin chi tiết về phòng chiếu, ngày, thời gian
- Liệt kê rõ ràng các lịch chiếu bị xung đột
- Thêm gợi ý giải pháp cho người dùng

### 2. Cải thiện Frontend Template

- Thêm CSS styling đặc biệt cho thông báo xung đột lịch chiếu
- Hiển thị thông báo lỗi với animation và màu sắc nổi bật
- Phân biệt rõ ràng giữa lỗi xung đột và lỗi validation thường

## Cách test

### Bước 1: Tạo lịch chiếu đầu tiên

1. Truy cập `/admin/schedules/add`
2. Chọn phim: "Avengers"
3. Chọn ngày: 2024-12-25
4. Chọn phòng: "Phòng 1"
5. Thời gian: 14:00 - 16:30
6. Lưu thành công

### Bước 2: Tạo lịch chiếu xung đột

1. Truy cập `/admin/schedules/add` lần nữa
2. Chọn phim: "Spider-Man"
3. Chọn ngày: 2024-12-25 (cùng ngày)
4. Chọn phòng: "Phòng 1" (cùng phòng)
5. Thời gian: 15:00 - 17:30 (bị xung đột với lịch trước)
6. Nhấn "Thêm lịch chiếu"

### Kết quả mong đợi

Sẽ hiển thị thông báo lỗi đơn giản với format:

```
⚠️ Phát hiện xung đột với các lịch chiếu: "Avengers" (14:00 - 16:30)
```

Nếu có nhiều lịch chiếu bị xung đột:

```
⚠️ Phát hiện xung đột với các lịch chiếu: "Avengers" (14:00 - 16:30), "Spider-Man" (16:00 - 18:00)
```

### Các trường hợp test khác

1. **Xung đột một phần**: Thời gian bắt đầu hoặc kết thúc trùng một phần
2. **Xung đột hoàn toàn**: Thời gian nằm hoàn toàn trong lịch chiếu khác
3. **Nhiều xung đột**: Thời gian trùng với nhiều lịch chiếu cùng lúc
4. **Edit lịch chiếu**: Test xung đột khi chỉnh sửa lịch chiếu hiện có

## Lợi ích của cải thiện

1. **Thông báo rõ ràng**: Người dùng biết chính xác lịch chiếu nào bị xung đột
2. **Giao diện đơn giản**: Thông báo lỗi dễ đọc và không phức tạp
3. **Thông tin cần thiết**: Hiển thị tên phim và thời gian xung đột
4. **Dễ hiểu**: Thông báo ngắn gọn, dễ đọc
5. **Trải nghiệm tốt**: Giảm confusion cho admin với thông báo đơn giản
