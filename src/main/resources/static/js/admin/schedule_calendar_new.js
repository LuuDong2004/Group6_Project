// Quản lý lịch chiếu phim - JavaScript cho giao diện lịch
// Updated: 2025-07-24 - Fixed logic to always navigate to detail page
document.addEventListener("DOMContentLoaded", function () {
  // Biến trạng thái ứng dụng
  let currentDate = new Date();
  let scheduleData = {};

  // Tên tháng tiếng Việt
  const monthNames = [
    "Tháng 1",
    "Tháng 2",
    "Tháng 3",
    "Tháng 4",
    "Tháng 5",
    "Tháng 6",
    "Tháng 7",
    "Tháng 8",
    "Tháng 9",
    "Tháng 10",
    "Tháng 11",
    "Tháng 12",
  ];

  // Khởi tạo ứng dụng
  function init() {
    console.log("Khởi tạo ứng dụng lịch chiếu");
    setupEventListeners();
    loadCalendarData(currentDate.getFullYear(), currentDate.getMonth() + 1);
  }

  // Thiết lập các event listener
  function setupEventListeners() {
    // Nút chuyển tháng
    document.getElementById("prev-month").addEventListener("click", () => {
      currentDate.setMonth(currentDate.getMonth() - 1);
      loadCalendarData(currentDate.getFullYear(), currentDate.getMonth() + 1);
    });

    document.getElementById("next-month").addEventListener("click", () => {
      currentDate.setMonth(currentDate.getMonth() + 1);
      loadCalendarData(currentDate.getFullYear(), currentDate.getMonth() + 1);
    });
  }

  // Tải dữ liệu lịch chiếu từ API
  async function loadCalendarData(year, month) {
    try {
      console.log(`Đang tải dữ liệu lịch chiếu cho tháng ${month}/${year}`);

      // Hiển thị loading spinner
      showLoading(true);

      const response = await fetch(
        `/admin/schedules/api/calendar-data?year=${year}&month=${month}`
      );
      const data = await response.json();

      if (data.success) {
        scheduleData = data.schedulesByDate || {};
        console.log("Dữ liệu lịch chiếu đã tải:", scheduleData);
        renderCalendar(year, month - 1); // month - 1 vì JavaScript month bắt đầu từ 0
      } else {
        console.error("Lỗi tải dữ liệu:", data.message);
        showError("Không thể tải dữ liệu lịch chiếu. Vui lòng thử lại.");
        scheduleData = {};
        renderCalendar(year, month - 1);
      }
    } catch (error) {
      console.error("Lỗi kết nối API:", error);
      showError("Lỗi kết nối. Vui lòng kiểm tra kết nối mạng và thử lại.");
      scheduleData = {};
      renderCalendar(year, month - 1);
    } finally {
      showLoading(false);
    }
  }

  // Hiển thị/ẩn loading spinner
  function showLoading(show) {
    const spinner = document.getElementById("loading-spinner");
    const content = document.getElementById("calendar-content");

    if (show) {
      spinner.style.display = "flex";
      content.style.display = "none";
    } else {
      spinner.style.display = "none";
      content.style.display = "block";
    }
  }

  // Hiển thị thông báo lỗi
  function showError(message) {
    // Tạo toast notification hoặc alert đơn giản
    alert(message);
  }

  // Render lịch
  function renderCalendar(year, month) {
    console.log(`Rendering calendar cho tháng ${month + 1}/${year}`);

    // Cập nhật tiêu đề tháng
    updateMonthTitle(year, month);

    // Tính toán ngày đầu tiên của tháng và số ngày trong tháng
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay(); // 0 = Chủ nhật

    // Lấy ngày hiện tại để so sánh
    const today = new Date();
    const todayStr = `${today.getFullYear()}-${String(
      today.getMonth() + 1
    ).padStart(2, "0")}-${String(today.getDate()).padStart(2, "0")}`;

    let calendarHTML = "";

    // Thêm các ô trống cho những ngày của tháng trước
    for (let i = 0; i < startingDayOfWeek; i++) {
      calendarHTML += `<div class="calendar-day"></div>`;
    }

    // Thêm các ngày trong tháng
    for (let day = 1; day <= daysInMonth; day++) {
      const dateStr = `${year}-${String(month + 1).padStart(2, "0")}-${String(
        day
      ).padStart(2, "0")}`;
      const schedules = scheduleData[dateStr] || [];
      const hasSchedules = schedules.length > 0;

      // Kiểm tra xem ngày này có phải là ngày quá khứ không (chỉ những ngày trước hôm nay)
      const isPastDate = dateStr < todayStr;

      let dayClass = "calendar-day";
      let dayContent = `<div class="calendar-day-number">${day}</div>`;

      // Thêm class cho ngày quá khứ
      if (isPastDate) {
        dayClass += " past-date";
      }

      if (hasSchedules) {
        dayClass += " has-schedules";
        dayContent += `<div class="calendar-day-schedules">${schedules.length} suất chiếu</div>`;
      }

      calendarHTML += `
                <div class="${dayClass}" onclick="handleDayClick('${dateStr}', ${hasSchedules}, ${isPastDate})">
                    ${dayContent}
                </div>
            `;
    }

    document.getElementById("calendar-grid").innerHTML = calendarHTML;
  }

  // Cập nhật tiêu đề tháng
  function updateMonthTitle(year, month) {
    const monthYearElement = document.getElementById("current-month-year");
    monthYearElement.textContent = `${monthNames[month]}, ${year}`;
  }

  // Xử lý click vào ngày
  window.handleDayClick = function (dateStr, hasSchedules, isPastDate) {
    console.log(
      "Ngày được click:",
      dateStr,
      "Có lịch chiếu:",
      hasSchedules,
      "Là ngày quá khứ:",
      isPastDate
    );

    // Luôn chuyển đến giao diện timeline cho ngày được chọn
    // Trang detail sẽ hiển thị timeline rỗng nếu không có lịch chiếu
    // Truyền thêm parameter isPastDate để trang detail biết cách xử lý
    console.log("Chuyển đến giao diện timeline cho ngày:", dateStr);
    const url = `/admin/schedules/detail?date=${dateStr}${
      isPastDate ? "&isPastDate=true" : ""
    }`;
    window.location.href = url;
  };

  // Khởi tạo ứng dụng
  init();
});
