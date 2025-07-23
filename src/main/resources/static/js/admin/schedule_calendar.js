// Quản lý lịch chiếu phim - JavaScript
document.addEventListener("DOMContentLoaded", function () {
  // Biến trạng thái ứng dụng
  let currentView = "calendar"; // 'calendar' hoặc 'timeline'
  let currentDate = new Date();
  let selectedDate = null;

  // Màu sắc cho các phim (Tailwind CSS classes) - Loại bỏ màu trắng và các màu nhạt
  const movieColors = [
    "bg-blue-600",
    "bg-teal-600",
    "bg-purple-600",
    "bg-orange-600",
    "bg-red-600",
    "bg-green-600",
    "bg-indigo-600",
    "bg-pink-600",
    "bg-amber-600",
    "bg-slate-600",
    "bg-cyan-600",
    "bg-emerald-600",
    "bg-violet-600",
    "bg-rose-600",
    "bg-lime-600",
    "bg-sky-600",
  ];

  // Map để lưu màu đã gán cho mỗi phim
  const movieColorMap = new Map();

  // Dữ liệu lịch chiếu và phòng chiếu từ API
  let scheduleData = {};
  let rooms = [];

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

  // Tên ngày trong tuần tiếng Việt
  const dayNames = [
    "Chủ nhật",
    "Thứ hai",
    "Thứ ba",
    "Thứ tư",
    "Thứ năm",
    "Thứ sáu",
    "Thứ bảy",
  ];

  // Khởi tạo ứng dụng
  function init() {
    loadCalendarData(currentDate.getFullYear(), currentDate.getMonth() + 1);
    setupEventListeners();
  }

  // Tải dữ liệu lịch chiếu từ API
  async function loadCalendarData(year, month) {
    try {
      const response = await fetch(
        `/admin/schedules/api/calendar-data?year=${year}&month=${month}`
      );
      const data = await response.json();

      if (data.success) {
        scheduleData = data.schedulesByDate || {};
        rooms = data.rooms || [];
        renderCalendar(year, month - 1); // month - 1 vì JavaScript month bắt đầu từ 0
      } else {
        console.error("Lỗi tải dữ liệu:", data.message);
        scheduleData = {};
        rooms = [];
        renderCalendar(year, month - 1);
      }
    } catch (error) {
      console.error("Lỗi kết nối API:", error);
      scheduleData = {};
      rooms = [];
      renderCalendar(year, month - 1);
    }
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

    // Nút quay lại lịch
    document
      .getElementById("back-to-calendar")
      .addEventListener("click", () => {
        showCalendarView();
      });

    // Ô tìm kiếm phim
    document.getElementById("movie-search").addEventListener("input", (e) => {
      filterTimelineByMovie(e.target.value);
    });
  }

  // Hiển thị giao diện lịch
  function showCalendarView() {
    currentView = "calendar";
    document.getElementById("calendar-view").classList.remove("d-none");
    document.getElementById("timeline-view").classList.add("d-none");
  }

  // Hiển thị giao diện timeline
  function showTimelineView(date) {
    currentView = "timeline";
    selectedDate = date;
    document.getElementById("calendar-view").classList.add("d-none");
    document.getElementById("timeline-view").classList.remove("d-none");

    // Cập nhật tiêu đề ngày
    updateTimelineTitle(date);

    // Render timeline
    renderTimeline(date);

    // Reset ô tìm kiếm
    document.getElementById("movie-search").value = "";
  }

  // Cập nhật tiêu đề timeline
  function updateTimelineTitle(date) {
    const dateObj = new Date(date);
    const dayName = dayNames[dateObj.getDay()];
    const day = dateObj.getDate();
    const month = dateObj.getMonth() + 1;
    const year = dateObj.getFullYear();

    const title = `Lịch chiếu ngày ${dayName}, ngày ${day} tháng ${month} năm ${year}`;
    document.getElementById("timeline-date-title").textContent = title;
  }

  // Render lịch theo tháng
  function renderCalendar(year, month) {
    // Cập nhật tiêu đề tháng/năm
    document.getElementById(
      "current-month-year"
    ).textContent = `${monthNames[month]}, ${year}`;

    // Tính toán các ngày trong tháng
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();

    // Tạo HTML cho lưới lịch
    let calendarHTML = "";

    // Thêm các ô trống cho những ngày của tháng trước
    for (let i = 0; i < startingDayOfWeek; i++) {
      calendarHTML +=
        '<div class="calendar-day" style="background-color: #f8f9fa; opacity: 0.5;"></div>';
    }

    // Thêm các ngày trong tháng
    for (let day = 1; day <= daysInMonth; day++) {
      const dateStr = `${year}-${String(month + 1).padStart(2, "0")}-${String(
        day
      ).padStart(2, "0")}`;
      const schedules = scheduleData[dateStr] || [];
      const hasSchedules = schedules.length > 0;

      let dayClass = "calendar-day";
      let dayContent = `<div class="calendar-day-number">${day}</div>`;

      if (hasSchedules) {
        dayClass += " has-schedules";
        dayContent += `<div class="calendar-day-schedules">${schedules.length} suất chiếu</div>`;
      }

      calendarHTML += `
                <div class="${dayClass}" onclick="handleDayClick('${dateStr}', ${hasSchedules})">
                    ${dayContent}
                </div>
            `;
    }

    document.getElementById("calendar-grid").innerHTML = calendarHTML;
  }

  // Xử lý click vào ngày
  window.handleDayClick = function (dateStr, hasSchedules) {
    console.log("Day clicked:", dateStr, "Has schedules:", hasSchedules);
    if (hasSchedules) {
      console.log("Switching to timeline view for date:", dateStr);
      showTimelineView(dateStr);
    } else {
      console.log("No schedules for this date");
    }
  };

  // Gán màu cho phim
  function getMovieColor(movieId) {
    if (!movieColorMap.has(movieId)) {
      const colorIndex = movieColorMap.size % movieColors.length;
      movieColorMap.set(movieId, movieColors[colorIndex]);
    }
    return movieColorMap.get(movieId);
  }

  // Render timeline cho một ngày
  function renderTimeline(date) {
    const schedules = scheduleData[date] || [];

    // Render time markers (8:00 - 24:00)
    renderTimeMarkers();

    // Render room rows với schedules
    renderRoomRows(schedules);
  }

  // Render các mốc thời gian (8:00 - 24:00)
  function renderTimeMarkers() {
    let markersHTML = "";
    for (let hour = 8; hour <= 24; hour++) {
      const position = ((hour - 8) / 16) * 100; // 16 hours total (8-24)
      markersHTML += `
                <div class="absolute text-xs text-gray-600 font-medium" style="left: ${position}%; padding: 0 4px;">
                    ${hour}:00
                </div>
            `;
    }
    document.getElementById("time-markers").innerHTML = markersHTML;
  }

  // Render các hàng phòng chiếu
  function renderRoomRows(schedules) {
    let rowsHTML = "";

    console.log("Rendering room rows with schedules:", schedules);
    console.log("Available rooms:", rooms);

    rooms.forEach((room) => {
      const roomSchedules = schedules.filter(
        (s) => s.screeningRoomId === room.id || s.screeningRoom?.id === room.id
      );

      console.log(`Room ${room.name} has ${roomSchedules.length} schedules`);

      rowsHTML += `
                <div class="room-row d-flex">
                    <div class="room-label d-flex align-items-center" style="width: 200px; padding: 1rem;">
                        <strong>${room.name}</strong>
                    </div>
                    <div class="flex-fill position-relative" style="min-width: 1200px;">
                        ${renderScheduleBlocks(roomSchedules)}
                    </div>
                </div>
            `;
    });

    document.getElementById("timeline-content").innerHTML = rowsHTML;
  }

  // Render các khối suất chiếu
  function renderScheduleBlocks(schedules) {
    let blocksHTML = "";

    schedules.forEach((schedule) => {
      // Xử lý thời gian từ DTO
      const startTime = schedule.startTime || "09:00";
      const endTime = schedule.endTime || "11:00";
      const movieName =
        schedule.movieName || schedule.movie?.name || "Phim không xác định";
      const movieId = schedule.movieId || schedule.movie?.id || 1;
      const roomId =
        schedule.screeningRoomId || schedule.screeningRoom?.id || 1;

      const startHour = parseFloat(startTime.replace(":", "."));
      const endHour = parseFloat(endTime.replace(":", "."));

      // Tính toán vị trí và chiều rộng (8:00 = 0%, 24:00 = 100%)
      const left = ((startHour - 8) / 16) * 100;
      const width = ((endHour - startHour) / 16) * 100;

      const color = getMovieColor(movieId);

      blocksHTML += `
                <div class="schedule-block ${color}"
                     style="left: ${left}%; width: ${width}%;"
                     data-movie-name="${movieName.toLowerCase()}">
                    <span class="text-center px-2">
                        ${movieName} | ${startTime} - ${endTime}
                    </span>
                </div>
            `;
    });

    return blocksHTML;
  }

  // Lọc timeline theo tên phim
  function filterTimelineByMovie(searchTerm) {
    const blocks = document.querySelectorAll(".schedule-block");
    const term = searchTerm.toLowerCase().trim();

    blocks.forEach((block) => {
      const movieName = block.getAttribute("data-movie-name");
      if (term === "" || movieName.includes(term)) {
        block.style.display = "flex";
      } else {
        block.style.display = "none";
      }
    });
  }

  // Khởi tạo ứng dụng
  init();
});
