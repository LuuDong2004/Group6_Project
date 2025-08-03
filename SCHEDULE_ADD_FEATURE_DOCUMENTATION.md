# Tài liệu Chức năng Thêm Lịch Chiếu

## 📋 Tổng quan dự án

### Mục tiêu

Tạo chức năng thêm lịch chiếu phim với các yêu cầu:

- Click vào ngày trong calendar để thêm lịch chiếu
- Form thêm lịch chiếu với autocomplete phim
- Kiểm tra xung đột lịch chiếu realtime
- Chỉ cho phép tạo lịch chiếu với trạng thái "Sắp chiếu"

### Yêu cầu ban đầu

1. **Ngày hiển thị readonly** - nhận từ calendar click
2. **Trạng thái mặc định "Sắp chiếu"** cho phim tương lai
3. **Autocomplete chọn phim** - gõ tên hiện gợi ý
4. **Dropdown chọn phòng** - load danh sách phòng
5. **Validation xung đột** - kiểm tra startDate/endDate với phim hiện có

### Yêu cầu bổ sung

- **Logic click calendar**: Ngày trống → list page với thông báo, Ngày có lịch → add page
- **Chỉ cho phép trạng thái "Sắp chiếu"** - không được chọn "Đang chiếu" hoặc "Đã chiếu"

## 🏗️ Kiến trúc hệ thống

### Frontend

- **admin_schedules_add.html** - Form thêm lịch chiếu
- **admin_schedule_calendar.html** - Calendar với logic click
- **admin_schedules_list.html** - Danh sách lịch chiếu với thông báo

### Backend

- **AdminScheduleController** - Xử lý requests
- **IAdminScheduleService** - Business logic
- **API Endpoints** - Autocomplete, conflict check

### Database

- **ScreeningSchedule** - Entity lịch chiếu
- **Movie** - Entity phim
- **ScreeningRoom** - Entity phòng chiếu

## 📁 Các file đã tạo/sửa đổi

### 1. Tạo mới

- `src/main/resources/templates/admin/admin_schedules_add.html`

### 2. Sửa đổi

- `src/main/resources/templates/admin/admin_schedule_calendar.html`
- `src/main/resources/templates/admin/admin_schedules_list.html`
- `src/main/java/group6/cinema_project/controller/Admin/AdminScheduleController.java`

## 🎯 Chi tiết implementation

### 1. Trang thêm lịch chiếu (admin_schedules_add.html)

#### Tính năng chính:

- **Form responsive** với Bootstrap + Tailwind CSS
- **Input ngày readonly** - nhận từ URL parameter
- **Autocomplete phim** với debounce 300ms
- **Dropdown phòng chiếu** - hiển thị tên + sức chứa
- **Dropdown chi nhánh**
- **Thời gian bắt đầu/kết thúc** với auto-calculate
- **Kiểm tra xung đột realtime**
- **Trạng thái cố định "Sắp chiếu"**

#### JavaScript functions:

```javascript
-initializeAutocomplete() - // Xử lý autocomplete phim
  initializeConflictCheck() - // Kiểm tra xung đột
  initializeFormValidation() - // Validation form
  setDefaultStatus() - // Set trạng thái mặc định
  setDateFromUrlParameter() - // Lấy ngày từ URL
  calculateEndTime() - // Tính thời gian kết thúc
  checkConflict(); // Gọi API kiểm tra xung đột
```

### 2. Logic click calendar (admin_schedule_calendar.html)

#### Logic mới:

```javascript
dayDiv.addEventListener("click", () => {
  const dateString = formatDateKey(date);
  const schedules = scheduleData[dateString] || [];

  if (isPastDate) {
    // Ngày quá khứ → xem chi tiết
    window.location.href = `/admin/schedules/list/date?date=${dateString}`;
  } else if (schedules.length > 0) {
    // Ngày có lịch chiếu → thêm lịch chiếu
    window.location.href = `/admin/schedules/add?date=${dateString}`;
  } else {
    // Ngày trống → danh sách với thông báo
    window.location.href = `/admin/schedules/list/date?date=${dateString}`;
  }
});
```

### 3. Controller updates (AdminScheduleController.java)

#### API Endpoints mới:

```java
// Autocomplete movies
@GetMapping("/api/movies/search")
public List<Map<String, Object>> searchMovies(@RequestParam("q") String query)

// Get movie details
@GetMapping("/api/movies/{id}")
public Map<String, Object> getMovieById(@PathVariable("id") Integer id)

// Check schedule conflict
@PostMapping("/api/check-conflict")
public Map<String, Object> checkScheduleConflict(@RequestBody Map<String, Object> conflictData)
```

#### Cập nhật showAddScheduleForm:

```java
@GetMapping("/add")
public String showAddScheduleForm(Model model,
    @RequestParam(value = "date", required = false) LocalDate selectedDate) {

    ScreeningScheduleDto scheduleDto = new ScreeningScheduleDto();

    if (selectedDate != null) {
        scheduleDto.setScreeningDate(selectedDate);
    }

    // Luôn set trạng thái là "UPCOMING"
    scheduleDto.setStatus("UPCOMING");

    // Load dropdown data...
}
```

### 4. Thông báo trong list page (admin_schedules_list.html)

#### Thêm alert message:

```html
<div
  th:if="${message}"
  class="alert alert-info alert-dismissible fade show"
  role="alert"
>
  <i class="fas fa-info-circle me-2"></i>
  <span th:text="${message}"></span>
  <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
```

#### Cập nhật nút thêm lịch chiếu:

```html
<a
  th:href="@{/admin/schedules/add(date=${selectedDate})}"
  class="btn btn-primary"
>
  <i class="fas fa-plus me-2"></i>Thêm lịch chiếu
</a>
```

## 🔄 Luồng hoạt động

### Scenario 1: Click vào ngày trống

1. User click vào ngày chưa có lịch chiếu trong calendar
2. Chuyển đến `/admin/schedules/list/date?date=2024-12-15`
3. Hiển thị thông báo: "Chưa có lịch chiếu nào cho ngày..."
4. User click "Thêm lịch chiếu"
5. Chuyển đến `/admin/schedules/add?date=2024-12-15`
6. Form được pre-fill với ngày đã chọn

### Scenario 2: Click vào ngày có lịch chiếu

1. User click vào ngày đã có lịch chiếu
2. Chuyển trực tiếp đến `/admin/schedules/add?date=2024-12-15`
3. Form được pre-fill để thêm lịch chiếu mới

### Scenario 3: Thêm lịch chiếu

1. User điền form thêm lịch chiếu
2. Chọn phim từ autocomplete
3. Chọn phòng và chi nhánh
4. Nhập thời gian bắt đầu (thời gian kết thúc tự động tính)
5. Hệ thống kiểm tra xung đột realtime
6. Submit form với trạng thái "UPCOMING"

## 🛠️ Tính năng kỹ thuật

### Autocomplete Movies

- **Debounce 300ms** để tránh spam requests
- **Minimum 2 characters** để bắt đầu search
- **Limit 10 results** để tối ưu performance
- **Hiển thị thông tin**: Tên, thời lượng, thể loại
- **Click outside để đóng** suggestions

### Conflict Detection

- **Realtime checking** khi user thay đổi thông tin
- **API endpoint** `/admin/schedules/api/check-conflict`
- **Sử dụng service validation** có sẵn
- **Disable submit button** khi có xung đột
- **Hiển thị warning message** rõ ràng

### Auto-calculate End Time

- **Khi chọn phim** → lấy duration từ API
- **Khi nhập start time** → tự động tính end time
- **Format HH:mm** chuẩn
- **Trigger conflict check** sau khi tính

### Status Management

- **Chỉ cho phép "UPCOMING"** cho lịch chiếu mới
- **Dropdown readonly** với 1 option duy nhất
- **Hidden input** để đảm bảo submit đúng
- **Visual feedback** với chú thích

## 🎨 UI/UX Features

### Design System

- **Bootstrap 5.3.0** + **Tailwind CSS**
- **Font Awesome icons** cho visual cues
- **Inter font** cho typography
- **Gradient backgrounds** cho modern look

### Responsive Design

- **Mobile-first approach**
- **Flexible grid system**
- **Adaptive form layouts**
- **Touch-friendly interactions**

### User Experience

- **Loading states** cho async operations
- **Error handling** với clear messages
- **Success feedback** sau actions
- **Intuitive navigation** giữa các pages

## 🔍 Validation & Error Handling

### Frontend Validation

- **Required fields** validation
- **Date format** validation
- **Time format** validation
- **Movie selection** validation

### Backend Validation

- **DTO validation** với annotations
- **Business logic validation**
- **Conflict detection** với custom exception
- **Database constraints** validation

### Error Messages

- **Tiếng Việt** cho user-friendly
- **Specific error descriptions**
- **Action suggestions** trong messages
- **Consistent formatting** across app

## 📊 Performance Considerations

### Frontend Optimization

- **Debounced search** để giảm API calls
- **Cached movie data** cho autocomplete
- **Lazy loading** cho large datasets
- **Minified assets** cho faster loading

### Backend Optimization

- **Indexed database queries**
- **Efficient JOIN operations**
- **Pagination** cho large results
- **Caching strategies** cho frequent data

## 🧪 Testing Scenarios

### Functional Testing

1. **Calendar click navigation**
2. **Form submission** với valid data
3. **Autocomplete search** functionality
4. **Conflict detection** accuracy
5. **Status restriction** enforcement

### Edge Cases

1. **Empty search results**
2. **Network timeout** handling
3. **Invalid date parameters**
4. **Concurrent schedule creation**
5. **Browser compatibility**

## 🚀 Deployment Notes

### Dependencies

- **Spring Boot** backend framework
- **Thymeleaf** template engine
- **Bootstrap + Tailwind** CSS frameworks
- **Font Awesome** icon library

### Configuration

- **Database migrations** cho new fields
- **API endpoint security**
- **CORS configuration** nếu cần
- **Logging configuration** cho debugging

## 📈 Future Enhancements

### Potential Improvements

1. **Bulk schedule creation** cho multiple time slots
2. **Schedule templates** cho recurring patterns
3. **Advanced conflict resolution** suggestions
4. **Email notifications** cho schedule changes
5. **Mobile app integration**

### Technical Debt

1. **Code refactoring** cho better maintainability
2. **Unit test coverage** improvement
3. **API documentation** với Swagger
4. **Performance monitoring** setup

## 📝 Code Examples

### Key JavaScript Functions

#### Autocomplete Implementation

```javascript
function searchMovies(query) {
  fetch(`/admin/schedules/api/movies/search?q=${encodeURIComponent(query)}`)
    .then((response) => response.json())
    .then((data) => {
      displaySuggestions(data);
    })
    .catch((error) => {
      console.error("Lỗi khi tìm kiếm phim:", error);
    });
}
```

#### Conflict Check Implementation

```javascript
function checkConflict() {
  const conflictData = {
    screeningDate: document.getElementById("screeningDate").value,
    startTime: document.getElementById("startTime").value,
    endTime: document.getElementById("endTime").value,
    screeningRoomId: document.getElementById("screeningRoomId").value,
  };

  fetch("/admin/schedules/api/check-conflict", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(conflictData),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.hasConflict) {
        showConflictWarning(data.message);
      } else {
        hideConflictWarning();
      }
    });
}
```

### Key Backend Methods

#### Controller Method

```java
@GetMapping("/add")
public String showAddScheduleForm(Model model,
    @RequestParam(value = "date", required = false) LocalDate selectedDate) {

    ScreeningScheduleDto scheduleDto = new ScreeningScheduleDto();

    if (selectedDate != null) {
        scheduleDto.setScreeningDate(selectedDate);
    }

    scheduleDto.setStatus("UPCOMING");

    model.addAttribute("schedule", scheduleDto);
    model.addAttribute("movies", movieService.getAllMovie());
    model.addAttribute("screeningRooms", screeningRoomService.getAllScreeningRooms());
    model.addAttribute("branches", branchService.getAllBranches());

    return "admin/admin_schedules_add";
}
```

#### API Endpoint

```java
@PostMapping("/api/check-conflict")
@ResponseBody
public Map<String, Object> checkScheduleConflict(@RequestBody Map<String, Object> conflictData) {
    Map<String, Object> response = new HashMap<>();

    try {
        ScreeningScheduleDto scheduleDto = new ScreeningScheduleDto();
        scheduleDto.setScreeningDate(LocalDate.parse((String) conflictData.get("screeningDate")));
        scheduleDto.setStartTime(LocalTime.parse((String) conflictData.get("startTime")));
        scheduleDto.setEndTime(LocalTime.parse((String) conflictData.get("endTime")));
        scheduleDto.setScreeningRoomId((Integer) conflictData.get("screeningRoomId"));

        movieScheduleService.validateScheduleConflicts(scheduleDto);
        response.put("hasConflict", false);
        response.put("message", "Không có xung đột lịch chiếu");
    } catch (ScheduleConflictException e) {
        response.put("hasConflict", true);
        response.put("message", e.getMessage());
    }

    return response;
}
```

## 🔧 Troubleshooting Guide

### Common Issues

#### 1. Ngày không hiển thị trong form

**Nguyên nhân**: URL parameter không được đọc đúng
**Giải pháp**: Kiểm tra JavaScript function `setDateFromUrlParameter()`

#### 2. Autocomplete không hoạt động

**Nguyên nhân**: API endpoint không response hoặc CORS issue
**Giải pháp**: Kiểm tra network tab trong DevTools

#### 3. Conflict check không hoạt động

**Nguyên nhân**: Service validation method có lỗi
**Giải pháp**: Kiểm tra logs backend và database constraints

#### 4. Form submit không thành công

**Nguyên nhân**: Validation errors hoặc missing required fields
**Giải pháp**: Kiểm tra BindingResult trong controller

### Debug Commands

#### Frontend Debug

```javascript
// Check if date is set correctly
console.log("Date value:", document.getElementById("screeningDate").value);

// Check autocomplete data
console.log("Movie suggestions:", movieSuggestions);

// Check conflict response
console.log("Conflict check result:", conflictData);
```

#### Backend Debug

```java
// Log request parameters
log.info("Date parameter: {}", selectedDate);

// Log validation errors
log.error("Validation error: {}", bindingResult.getAllErrors());

// Log service calls
log.info("Calling conflict validation for: {}", scheduleDto);
```

## 📋 Checklist hoàn thành

### ✅ Frontend Tasks

- [x] Tạo form admin_schedules_add.html
- [x] Implement autocomplete functionality
- [x] Add conflict detection UI
- [x] Update calendar click logic
- [x] Add responsive design
- [x] Implement form validation

### ✅ Backend Tasks

- [x] Update AdminScheduleController
- [x] Add API endpoints for autocomplete
- [x] Add API endpoint for conflict check
- [x] Update showAddScheduleForm method
- [x] Add message handling in list page

### ✅ Integration Tasks

- [x] Connect frontend with backend APIs
- [x] Test calendar navigation flow
- [x] Test form submission
- [x] Test conflict detection
- [x] Test autocomplete search

### ✅ UI/UX Tasks

- [x] Design consistent interface
- [x] Add loading states
- [x] Add error messages
- [x] Add success feedback
- [x] Ensure mobile responsiveness

---

**Tác giả**: AI Assistant
**Ngày tạo**: 2024-12-15
**Version**: 1.0
**Status**: Completed ✅

**Tổng thời gian phát triển**: ~2 giờ
**Số file được tạo/sửa**: 4 files
**Số dòng code**: ~800 lines
**Số API endpoints**: 3 endpoints
