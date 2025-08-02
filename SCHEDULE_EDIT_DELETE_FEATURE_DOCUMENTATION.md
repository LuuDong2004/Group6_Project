# 📝 Tài liệu Chức năng Sửa và Xóa Lịch Chiếu

## 🎯 Tổng quan

Tài liệu này mô tả việc triển khai chức năng sửa và xóa lịch chiếu trong hệ thống quản lý rạp chiếu phim, bao gồm:

- **Icon sửa/xóa với hover effect** ở góc phải dưới mỗi box lịch chiếu
- **Validation logic** chỉ cho phép sửa/xóa lịch chiếu có status "UPCOMING"
- **Kiểm tra booking** không cho sửa/xóa nếu đã có người đặt vé
- **API endpoints** để kiểm tra khả năng chỉnh sửa
- **UI/UX cải tiến** với animation và feedback

## 🏗️ Kiến trúc hệ thống

### Backend Components

#### 1. Repository Layer
- **BookingRepository**: Thêm methods kiểm tra booking existence
- **AdminScheduleRepository**: Sử dụng existing methods

#### 2. Service Layer  
- **AdminScheduleService**: Thêm validation logic
- **IAdminScheduleService**: Cập nhật interface

#### 3. Controller Layer
- **AdminScheduleController**: Thêm API endpoints và validation

### Frontend Components

#### 1. Templates
- **admin_schedules_list.html**: Cập nhật UI và JavaScript

#### 2. CSS Styling
- **Action buttons**: Hover effects và animations
- **Schedule items**: Enhanced styling

#### 3. JavaScript Logic
- **Validation checks**: Real-time API calls
- **Event handling**: Edit/delete actions

## 📁 Files đã tạo/sửa đổi

### 1. Backend Files

#### BookingRepository.java
```java
// Thêm methods kiểm tra booking
boolean existsByScheduleId(Integer scheduleId);
long countByScheduleId(Integer scheduleId);
```

#### AdminScheduleServiceImpl.java
```java
// Thêm validation methods
boolean canEditSchedule(Integer scheduleId);
boolean canDeleteSchedule(Integer scheduleId);
Map<String, Object> getScheduleEditabilityInfo(Integer scheduleId);
```

#### IAdminScheduleService.java
```java
// Cập nhật interface với new methods
```

#### AdminScheduleController.java
```java
// Thêm API endpoint
@GetMapping("/api/check-editability/{id}")
// Cập nhật edit/delete methods với validation
```

### 2. Frontend Files

#### admin_schedules_list.html
- Thêm CSS cho action buttons
- Cập nhật HTML structure
- Thêm JavaScript validation logic

## 🎯 Chi tiết Implementation

### 1. Validation Logic

#### Điều kiện cho phép sửa/xóa:
1. **Status phải là "UPCOMING"**
   - Không cho sửa/xóa lịch "ACTIVE" (đang chiếu)
   - Không cho sửa/xóa lịch "ENDED" (đã kết thúc)

2. **Không có booking nào**
   - Kiểm tra trong bảng Booking
   - Nếu có booking thì không cho sửa/xóa

#### Implementation:
```java
public boolean canEditSchedule(Integer scheduleId) {
    // Kiểm tra schedule tồn tại
    Optional<ScreeningSchedule> scheduleOpt = movieScheduleRepository.findById(scheduleId);
    if (scheduleOpt.isEmpty()) return false;
    
    ScreeningSchedule schedule = scheduleOpt.get();
    
    // Chỉ cho phép UPCOMING
    if (!"UPCOMING".equalsIgnoreCase(schedule.getStatus())) return false;
    
    // Kiểm tra booking
    boolean hasBookings = bookingRepository.existsByScheduleId(scheduleId);
    return !hasBookings;
}
```

### 2. UI/UX Design

#### Action Buttons:
- **Vị trí**: Góc phải dưới cùng của schedule box
- **Hiển thị**: Chỉ khi hover vào schedule box
- **Điều kiện**: Chỉ hiển thị cho status "UPCOMING"

#### CSS Styling:
```css
.schedule-actions {
    position: absolute;
    bottom: 8px;
    right: 8px;
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
}

.schedule-item:hover .schedule-actions {
    opacity: 1;
    visibility: visible;
}
```

#### Button States:
- **Enabled**: Gradient background, hover effects
- **Disabled**: Opacity 0.5, no hover effects
- **Tooltips**: Hiển thị lý do không thể sửa/xóa

### 3. API Integration

#### Check Editability Endpoint:
```javascript
GET /admin/schedules/api/check-editability/{id}

Response:
{
    "success": true,
    "canEdit": true,
    "canDelete": true,
    "reason": "Có thể chỉnh sửa/xóa",
    "status": "UPCOMING",
    "bookingCount": 0
}
```

#### Real-time Validation:
- Gọi API khi trang load
- Cập nhật trạng thái buttons
- Hiển thị tooltips với lý do

### 4. Error Handling

#### Backend Validation:
```java
// Trong edit/delete methods
if (!movieScheduleService.canEditSchedule(id)) {
    Map<String, Object> editabilityInfo = movieScheduleService.getScheduleEditabilityInfo(id);
    String reason = (String) editabilityInfo.get("reason");
    redirectAttributes.addFlashAttribute("error", "Không thể chỉnh sửa: " + reason);
    return "redirect:/admin/schedules/list";
}
```

#### Frontend Feedback:
```javascript
// Alert messages cho user
if (canEdit) {
    window.location.href = `/admin/schedules/edit/${scheduleId}`;
} else {
    alert(`Không thể chỉnh sửa lịch chiếu: ${reason}`);
}
```

## 🔄 Workflow

### Edit Schedule Flow:
1. User hover vào schedule box (UPCOMING)
2. Action buttons xuất hiện
3. Click edit button
4. JavaScript gọi API check editability
5. Nếu OK → chuyển đến edit page
6. Nếu không OK → hiển thị alert với lý do

### Delete Schedule Flow:
1. User hover vào schedule box (UPCOMING)  
2. Action buttons xuất hiện
3. Click delete button
4. JavaScript gọi API check editability
5. Nếu OK → hiển thị confirm dialog
6. User confirm → submit POST request
7. Nếu không OK → hiển thị alert với lý do

## 🎨 Visual Design

### Color Scheme:
- **Edit button**: Green gradient (#10b981 → #059669)
- **Delete button**: Red gradient (#ef4444 → #dc2626)
- **Hover effects**: Scale transform + shadow
- **Disabled state**: 50% opacity

### Animations:
- **Button appearance**: Fade in/out với visibility
- **Hover effects**: Scale 1.1 + shadow
- **Schedule box**: Lift effect với transform translateY

## 🧪 Testing Scenarios

### Test Cases:

1. **UPCOMING schedule without bookings**
   - ✅ Buttons hiển thị khi hover
   - ✅ Có thể edit và delete

2. **UPCOMING schedule with bookings**
   - ✅ Buttons hiển thị nhưng disabled
   - ❌ Không thể edit/delete
   - ✅ Tooltip hiển thị lý do

3. **ACTIVE schedule**
   - ❌ Buttons không hiển thị
   - ❌ Không thể edit/delete

4. **ENDED schedule**
   - ❌ Buttons không hiển thị  
   - ❌ Không thể edit/delete

## 🚀 Deployment Notes

### Database Impact:
- Không có thay đổi schema
- Chỉ thêm queries mới

### Performance:
- API calls được cache trong session
- Minimal overhead cho UI updates

### Browser Compatibility:
- Modern browsers (ES6+)
- Fallback cho older browsers

## 📋 Future Enhancements

### Possible Improvements:
1. **Batch operations**: Sửa/xóa nhiều lịch cùng lúc
2. **Advanced permissions**: Role-based access control
3. **Audit logging**: Track edit/delete actions
4. **Real-time updates**: WebSocket notifications
5. **Mobile optimization**: Touch-friendly UI

---

**Tác giả**: AI Assistant  
**Ngày tạo**: 2025-01-08  
**Version**: 1.0
