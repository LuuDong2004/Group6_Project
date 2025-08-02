# Thay đổi từ JavaScript sang Thymeleaf MVC

## Tóm tắt các thay đổi đã thực hiện

### 1. **Thay thế Navigation tháng trước/sau**
**Trước (JavaScript):**
```javascript
document.getElementById("prevMonth").addEventListener("click", () => {
  currentDate.setMonth(currentDate.getMonth() - 1);
  loadCalendarData();
});
```

**Sau (Thymeleaf + Form):**
```html
<!-- Form để chuyển tháng trước -->
<form th:action="@{/admin/schedules/calendar}" method="get" style="display: inline;">
  <input type="hidden" name="year" th:value="${currentMonth == 1 ? currentYear - 1 : currentYear}">
  <input type="hidden" name="month" th:value="${currentMonth == 1 ? 12 : currentMonth - 1}">
  <button type="submit" class="month-nav-btn">
    <i class="fas fa-chevron-left"></i>
  </button>
</form>
```

### 2. **Thay thế Calendar Days Generation**
**Trước (JavaScript):**
```javascript
function generateCalendarDays() {
  const calendarDays = document.getElementById("calendarDays");
  calendarDays.innerHTML = "";
  
  // Tạo 42 ô (6 tuần x 7 ngày)
  for (let i = 0; i < 42; i++) {
    const cellDate = new Date(startDate);
    cellDate.setDate(startDate.getDate() + i);
    const dayElement = createDayElement(cellDate, month);
    calendarDays.appendChild(dayElement);
  }
}
```

**Sau (Thymeleaf):**
```html
<div th:each="day : ${calendarDays}" 
     th:class="|calendar-day ${day.isCurrentMonth ? '' : 'other-month'} ${day.isToday ? 'today' : ''} ${day.isPastDate ? 'past-date' : ''}|"
     th:onclick="|window.location.href='/admin/schedules/list/date?date=' + '${day.date}'|"
     style="cursor: pointer;">
  
  <div class="day-number" th:text="${day.dayNumber}">1</div>
  
  <div th:if="${day.scheduleCount > 0}" 
       class="schedule-count" 
       th:text="${day.scheduleCount + ' suất chiếu'}">
    2 suất chiếu
  </div>
</div>
```

### 3. **Thêm method generateCalendarDays trong Controller**
```java
private List<Map<String, Object>> generateCalendarDays(int year, int month, List<ScreeningScheduleDto> schedules) {
    List<Map<String, Object>> calendarDays = new ArrayList<>();
    
    // Nhóm lịch chiếu theo ngày
    Map<String, List<ScreeningScheduleDto>> schedulesByDate = schedules.stream()
        .collect(Collectors.groupingBy(schedule -> schedule.getScreeningDate().toString()));
    
    // Tạo LocalDate cho ngày đầu tiên của tháng
    LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
    
    // Tìm ngày đầu tiên cần hiển thị (có thể là tháng trước)
    LocalDate startDate = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
    
    // Tạo 42 ngày (6 tuần x 7 ngày)
    for (int i = 0; i < 42; i++) {
        LocalDate currentDate = startDate.plusDays(i);
        Map<String, Object> dayData = new HashMap<>();
        
        dayData.put("date", currentDate);
        dayData.put("dayNumber", currentDate.getDayOfMonth());
        dayData.put("isCurrentMonth", currentDate.getMonth().getValue() == month);
        dayData.put("isToday", currentDate.equals(LocalDate.now()));
        dayData.put("isPastDate", currentDate.isBefore(LocalDate.now()));
        
        // Lấy lịch chiếu cho ngày này
        String dateKey = currentDate.toString();
        List<ScreeningScheduleDto> daySchedules = schedulesByDate.getOrDefault(dateKey, new ArrayList<>());
        dayData.put("schedules", daySchedules);
        dayData.put("scheduleCount", daySchedules.size());
        
        calendarDays.add(dayData);
    }
    
    return calendarDays;
}
```

### 4. **Loại bỏ JavaScript không cần thiết**
**Đã xóa:**
- `initializeCalendar()` - không cần vì dữ liệu đã render sẵn
- `loadCalendarData()` - không cần AJAX call
- `generateCalendarDays()` - đã chuyển sang server-side
- `createDayElement()` - đã chuyển sang Thymeleaf
- `updateCalendarDisplay()` - không cần vì render sẵn
- `updateMonthHeader()` - không cần vì render sẵn
- Các utility functions cho date formatting

**Giữ lại:**
- `initializeDashboardBehavior()` - cần thiết cho sidebar toggle
- Responsive behavior - cần thiết cho UX

## Lợi ích của việc thay đổi

### 1. **Hiệu suất tốt hơn**
- Không cần AJAX calls để load dữ liệu
- Dữ liệu được render sẵn từ server
- Giảm thời gian load trang

### 2. **SEO friendly**
- Nội dung được render server-side
- Search engines có thể index được

### 3. **Bảo mật tốt hơn**
- Giảm thiểu client-side logic
- Dữ liệu được xử lý an toàn ở server

### 4. **Dễ bảo trì**
- Logic tập trung ở server
- Ít JavaScript phức tạp
- Dễ debug và test

### 5. **Tương thích tốt hơn**
- Hoạt động ngay cả khi JavaScript bị disable
- Tương thích với nhiều browser hơn

## Các file đã thay đổi

1. **src/main/resources/templates/admin/admin_schedule_calendar.html**
   - Thay thế navigation buttons bằng forms
   - Thay thế JavaScript calendar generation bằng Thymeleaf
   - Loại bỏ phần lớn JavaScript code

2. **src/main/java/group6/cinema_project/controller/Admin/AdminScheduleController.java**
   - Thêm method `generateCalendarDays()`
   - Cập nhật calendar view method để cung cấp `calendarDays`

## Kết quả

Trang calendar giờ đây:
- Load nhanh hơn (không cần AJAX)
- Ít JavaScript hơn (từ ~280 dòng xuống ~30 dòng)
- Dữ liệu được render sẵn từ server
- Vẫn giữ được tất cả chức năng
- UX tốt hơn với navigation mượt mà
