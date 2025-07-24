// Quản lý lịch chiếu phim - JavaScript cho giao diện timeline
document.addEventListener("DOMContentLoaded", function () {
  // Biến trạng thái ứng dụng
  let selectedDate = null;
  let selectedBranchId = null;
  let scheduleData = [];
  let roomData = [];
  let branchData = [];
  let filteredSchedules = [];
  let isPastDate = false;
  let isToday = false;
  let isFutureDate = false;

  // Lấy ngày từ URL parameters
  const urlParams = new URLSearchParams(window.location.search);
  selectedDate =
    urlParams.get("date") || new Date().toISOString().split("T")[0];

  // Tính toán loại ngày
  const today = new Date().toISOString().split("T")[0];
  isPastDate = selectedDate < today;
  isToday = selectedDate === today;
  isFutureDate = selectedDate > today;

  // Khởi tạo ứng dụng
  function init() {
    console.log(
      "Khởi tạo giao diện timeline cho ngày:",
      selectedDate,
      "| Quá khứ:",
      isPastDate,
      "| Hôm nay:",
      isToday,
      "| Tương lai:",
      isFutureDate
    );
    setupEventListeners();
    updateTimelineTitle();
    setupStatusFilter();
    loadTimelineData();
  }

  // Thiết lập các event listener
  function setupEventListeners() {
    // Nút quay lại lịch
    document
      .getElementById("back-to-calendar")
      .addEventListener("click", (e) => {
        e.preventDefault();
        window.location.href = "/admin/schedules/list";
      });

    // Ô tìm kiếm phim
    document.getElementById("movie-search").addEventListener("input", (e) => {
      filterSchedules();
    });

    // Dropdown lọc trạng thái
    document.getElementById("status-filter").addEventListener("change", (e) => {
      filterSchedules();
    });

    // Dropdown lọc chi nhánh
    document.getElementById("branch-filter").addEventListener("change", (e) => {
      selectedBranchId = e.target.value || null;
      loadTimelineData(); // Tải lại dữ liệu khi thay đổi chi nhánh
    });

    // Button cập nhật lịch chiếu
    document
      .getElementById("update-schedules-btn")
      .addEventListener("click", (e) => {
        e.preventDefault();
        updateScheduleStatuses();
      });
  }

  // Thiết lập dropdown lọc trạng thái dựa trên ngày
  function setupStatusFilter() {
    const statusFilter = document.getElementById("status-filter");

    if (isPastDate) {
      // Ngày quá khứ (trước hôm nay): Mặc định "Đã chiếu" và read-only
      statusFilter.innerHTML = `
        <option value="ALL">Tất cả trạng thái</option>
        <option value="ACTIVE">Đang chiếu</option>
        <option value="UPCOMING">Sắp chiếu</option>
        <option value="ENDED" selected>Đã chiếu</option>
      `;
      statusFilter.disabled = true;
      statusFilter.setAttribute("readonly", true);
    } else if (isToday) {
      // Ngày hiện tại (hôm nay): Mặc định "Tất cả" và có thể lọc tất cả trạng thái (bao gồm "Đã chiếu")
      statusFilter.innerHTML = `
        <option value="ALL" selected>Tất cả trạng thái</option>
        <option value="ACTIVE">Đang chiếu</option>
        <option value="UPCOMING">Sắp chiếu</option>
        <option value="ENDED">Đã chiếu</option>
      `;
      statusFilter.disabled = false;
      statusFilter.removeAttribute("readonly");
    } else if (isFutureDate) {
      // Ngày tương lai: Mặc định "Tất cả" và có thể lọc (không có "Đã chiếu")
      statusFilter.innerHTML = `
        <option value="ALL" selected>Tất cả trạng thái</option>
        <option value="ACTIVE">Đang chiếu</option>
        <option value="UPCOMING">Sắp chiếu</option>
      `;
      statusFilter.disabled = false;
      statusFilter.removeAttribute("readonly");
    }
  }

  // Thiết lập dropdown lọc chi nhánh
  function setupBranchFilter() {
    const branchFilter = document.getElementById("branch-filter");

    if (branchData && branchData.length > 0) {
      let optionsHTML = "";

      branchData.forEach((branch, index) => {
        // Nếu chưa có selectedBranchId, chọn branch đầu tiên làm mặc định
        const isSelected = selectedBranchId
          ? selectedBranchId == branch.id
          : index === 0;

        const selected = isSelected ? "selected" : "";
        optionsHTML += `<option value="${branch.id}" ${selected}>${branch.name}</option>`;

        // Set selectedBranchId nếu đây là branch đầu tiên và chưa có selection
        if (index === 0 && !selectedBranchId) {
          selectedBranchId = branch.id;
        }
      });

      branchFilter.innerHTML = optionsHTML;
    } else {
      branchFilter.innerHTML = '<option value="">Không có chi nhánh</option>';
    }
  }

  // Cập nhật tiêu đề timeline
  function updateTimelineTitle() {
    const titleElement = document.getElementById("timeline-title");
    const dateObj = new Date(selectedDate + "T00:00:00");

    const dayNames = [
      "Chủ nhật",
      "Thứ hai",
      "Thứ ba",
      "Thứ tư",
      "Thứ năm",
      "Thứ sáu",
      "Thứ bảy",
    ];
    const dayName = dayNames[dateObj.getDay()];

    const formattedDate = dateObj.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });

    titleElement.textContent = `Lịch chiếu ngày ${dayName}, ${formattedDate}`;
  }

  // Tải dữ liệu timeline từ API
  async function loadTimelineData() {
    try {
      console.log(
        "Đang tải dữ liệu timeline cho ngày:",
        selectedDate,
        "chi nhánh:",
        selectedBranchId
      );

      // Hiển thị loading spinner
      showLoading(true);

      // Tạo URL với branch parameter nếu có
      let apiUrl = `/admin/schedules/api/timeline-data?date=${selectedDate}`;
      if (selectedBranchId) {
        apiUrl += `&branchId=${selectedBranchId}`;
      }

      const response = await fetch(apiUrl);
      const data = await response.json();

      if (data.success) {
        scheduleData = data.schedules || [];
        roomData = data.rooms || [];
        branchData = data.branches || [];
        filteredSchedules = [...scheduleData];

        console.log("Dữ liệu timeline đã tải:", {
          scheduleData,
          roomData,
          branchData,
        });

        // Thiết lập branch filter nếu chưa có
        if (!selectedBranchId && branchData.length > 0) {
          setupBranchFilter();
          // Reload data với branch đầu tiên được chọn
          loadTimelineData();
          return;
        }

        renderTimeline();
      } else {
        console.error("Lỗi tải dữ liệu timeline:", data.message);
        showError("Không thể tải dữ liệu timeline. Vui lòng thử lại.");
      }
    } catch (error) {
      console.error("Lỗi kết nối API timeline:", error);
      showError("Lỗi kết nối. Vui lòng kiểm tra kết nối mạng và thử lại.");
    } finally {
      showLoading(false);
    }
  }

  // Hiển thị/ẩn loading spinner
  function showLoading(show) {
    const spinner = document.getElementById("timeline-loading");
    const content = document.getElementById("timeline-content");

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
    alert(message);
  }

  // Render timeline
  function renderTimeline() {
    console.log("Rendering timeline với dữ liệu:", filteredSchedules);
    renderTimeMarkers();
    renderRoomRows();
  }

  // Render các mốc thời gian (hiển thị từ 9:00 - 24:00)
  function renderTimeMarkers() {
    const timeMarkersContainer = document.getElementById(
      "time-markers-container"
    );
    let markersHTML = "";
    const totalHours = 16; // Timeline từ 8:00 đến 24:00 là 16 tiếng

    // Bắt đầu hiển thị các mốc thời gian từ 9:00
    for (let hour = 9; hour <= 24; hour++) {
      // Vị trí được tính dựa trên mốc 8:00 là 0%
      const position = ((hour - 8) / totalHours) * 100;
      markersHTML += `
        <div class="time-marker" style="left: ${position}%;">
          ${hour}:00
        </div>
      `;
    }
    timeMarkersContainer.innerHTML = markersHTML;
  }

  // Render các hàng phòng chiếu
  function renderRoomRows() {
    let rowsHTML = "";

    console.log("Rendering room rows với rooms:", roomData);
    console.log("Filtered schedules:", filteredSchedules);

    roomData.forEach((room) => {
      const roomSchedules = filteredSchedules.filter(
        (schedule) => schedule.screeningRoomId === room.id
      );

      console.log(`Phòng ${room.name} có ${roomSchedules.length} lịch chiếu`);

      // Chỉ hiển thị add button nếu không phải ngày quá khứ (ngày hôm nay và tương lai vẫn cho phép thêm)
      const addButtonHTML = isPastDate
        ? ""
        : `
            <button class="add-schedule-btn"
                    onclick="addScheduleForRoom(${room.id}, '${room.name}')"
                    title="Thêm lịch chiếu cho ${room.name}">
              <i class="fas fa-plus"></i>
            </button>`;

      rowsHTML += `
        <div class="room-row">
          <div class="room-label">
            <strong>${room.name}</strong>
            ${addButtonHTML}
          </div>
          <div class="room-timeline">
            ${renderScheduleBlocks(roomSchedules)}
          </div>
        </div>
      `;
    });

    document.getElementById("room-rows-container").innerHTML = rowsHTML;
  }

  // Render các khối suất chiếu
  function renderScheduleBlocks(schedules) {
    let blocksHTML = "";

    schedules.forEach((schedule) => {
      const startTime = formatTimeDisplay(schedule.startTime || "09:00");
      const endTime = formatTimeDisplay(schedule.endTime || "11:00");
      const movieName = schedule.movieName || "Phim không xác định";
      const status = schedule.status || "ACTIVE";
      const scheduleId = schedule.id;

      const startHour = timeToDecimal(schedule.startTime || "09:00");
      const endHour = timeToDecimal(schedule.endTime || "11:00");

      const left = ((startHour - 8) / 16) * 100;
      const width = ((endHour - startHour) / 16) * 100;

      const statusClass = getStatusClass(status);

      // Chỉ hiển thị button delete cho lịch chiếu có trạng thái UPCOMING
      const deleteButtonHTML =
        status === "UPCOMING"
          ? `
        <button class="schedule-delete-btn"
                onclick="deleteSchedule(${scheduleId}, '${movieName}')"
                title="Xóa lịch chiếu">
          <i class="fas fa-times"></i>
        </button>
      `
          : "";

      // Chỉ hiển thị button edit cho lịch chiếu có trạng thái UPCOMING
      const editButtonHTML =
        status === "UPCOMING"
          ? `
        <button class="schedule-edit-btn"
                onclick="editSchedule(${scheduleId})"
                title="Sửa lịch chiếu">
          <i class="fas fa-edit"></i>
        </button>
      `
          : "";

      blocksHTML += `
        <div class="schedule-block ${statusClass}"
             style="left: ${left}%; width: ${width}%;"
             data-movie-name="${movieName.toLowerCase()}"
             data-status="${status}"
             data-schedule-id="${scheduleId}"
             title="${movieName} | ${startTime} - ${endTime} | ${getStatusText(
        status
      )}">
          <span>
            ${movieName} | ${startTime} - ${endTime}
          </span>
          ${editButtonHTML}
          ${deleteButtonHTML}
        </div>
      `;
    });

    return blocksHTML;
  }

  // Chuyển đổi thời gian từ "HH:MM" thành số thập phân
  function timeToDecimal(timeStr) {
    const [hours, minutes] = timeStr.split(":").map(Number);
    return hours + minutes / 60;
  }

  // Format thời gian hiển thị từ "HH:MM:SS" thành "HH:MM"
  function formatTimeDisplay(timeStr) {
    if (!timeStr) return "";

    // Nếu thời gian có format HH:MM:SS, chỉ lấy HH:MM
    if (timeStr.includes(":")) {
      const parts = timeStr.split(":");
      if (parts.length >= 2) {
        return `${parts[0]}:${parts[1]}`;
      }
    }

    return timeStr;
  }

  // Lấy class CSS cho trạng thái
  function getStatusClass(status) {
    switch (status) {
      case "ACTIVE":
        return "status-active";
      case "UPCOMING":
        return "status-upcoming";
      case "ENDED":
        return "status-ended";
      default:
        return "status-active";
    }
  }

  // Lấy text hiển thị cho trạng thái
  function getStatusText(status) {
    switch (status) {
      case "ACTIVE":
        return "Đang chiếu";
      case "UPCOMING":
        return "Sắp chiếu";
      case "ENDED":
        return "Đã chiếu";
      default:
        return "Đang chiếu";
    }
  }

  // Lọc lịch chiếu theo tìm kiếm và trạng thái
  function filterSchedules() {
    const searchTerm = document
      .getElementById("movie-search")
      .value.toLowerCase()
      .trim();
    const statusFilter = document.getElementById("status-filter").value;

    console.log("Lọc lịch chiếu với:", { searchTerm, statusFilter });

    filteredSchedules = scheduleData.filter((schedule) => {
      const movieName = (schedule.movieName || "").toLowerCase();
      const matchesSearch = searchTerm === "" || movieName.includes(searchTerm);

      const matchesStatus =
        statusFilter === "ALL" || schedule.status === statusFilter;

      return matchesSearch && matchesStatus;
    });

    console.log("Kết quả lọc:", filteredSchedules);
    renderTimeline();
  }

  // Hàm xử lý khi bấm nút thêm lịch chiếu cho phòng
  window.addScheduleForRoom = function (roomId, roomName) {
    console.log(`Thêm lịch chiếu cho phòng ${roomName} (ID: ${roomId})`);

    // Kiểm tra nếu là ngày quá khứ thì không cho phép thêm
    if (isPastDate) {
      alert("Không thể thêm lịch chiếu cho ngày quá khứ!");
      return;
    }

    // Tìm thông tin chi nhánh của phòng này
    const room = roomData.find((r) => r.id === roomId);
    const branchId = room ? room.branch?.id : null;
    const branchName = room ? room.branch?.name : "";

    // Chuyển hướng đến trang thêm lịch chiếu với thông tin phòng, ngày và chi nhánh
    let url = `/admin/schedules/add?roomId=${roomId}&roomName=${encodeURIComponent(
      roomName
    )}&date=${selectedDate}`;

    if (branchId) {
      url += `&branchId=${branchId}&branchName=${encodeURIComponent(
        branchName
      )}`;
    }

    window.location.href = url;
  };

  // Hàm xử lý khi bấm nút sửa lịch chiếu
  window.editSchedule = function (scheduleId) {
    console.log(`Sửa lịch chiếu ID: ${scheduleId}`);
    // Chuyển hướng đến trang sửa lịch chiếu
    window.location.href = `/admin/schedules/edit/${scheduleId}`;
  };

  // Hàm xử lý khi bấm nút xóa lịch chiếu
  window.deleteSchedule = async function (scheduleId, movieName) {
    console.log(`Yêu cầu xóa lịch chiếu ID: ${scheduleId}, Phim: ${movieName}`);

    // Hiển thị dialog xác nhận
    const confirmMessage = `Bạn có chắc chắn muốn xóa lịch chiếu phim "${movieName}"?\n\nHành động này không thể hoàn tác.`;
    if (!confirm(confirmMessage)) {
      return;
    }

    try {
      // Gửi request DELETE đến API
      const response = await fetch(
        `/admin/schedules/api/delete/${scheduleId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      const result = await response.json();

      if (result.success) {
        console.log("Xóa lịch chiếu thành công:", result.message);

        // Hiển thị thông báo thành công
        alert("Xóa lịch chiếu thành công!");

        // Tải lại dữ liệu timeline để cập nhật giao diện
        await loadTimelineData();
      } else {
        console.error("Lỗi khi xóa lịch chiếu:", result.message);
        alert("Lỗi: " + result.message);
      }
    } catch (error) {
      console.error("Lỗi kết nối khi xóa lịch chiếu:", error);
      alert("Có lỗi xảy ra khi xóa lịch chiếu. Vui lòng thử lại.");
    }
  };

  // Hàm cập nhật trạng thái lịch chiếu
  async function updateScheduleStatuses() {
    const updateBtn = document.getElementById("update-schedules-btn");
    const originalText = updateBtn.innerHTML;

    try {
      // Hiển thị trạng thái đang tải
      updateBtn.disabled = true;
      updateBtn.innerHTML =
        '<i class="fas fa-spinner fa-spin"></i> <span>Đang cập nhật...</span>';

      console.log("Bắt đầu cập nhật trạng thái lịch chiếu");

      const response = await fetch("/admin/schedules/update-status", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      });

      const result = await response.json();
      console.log("Kết quả cập nhật:", result);

      if (result.success) {
        // Hiển thị thông báo thành công
        if (result.totalUpdated > 0) {
          alert(result.message);
          console.log(`Đã cập nhật ${result.totalUpdated} lịch chiếu:`, {
            upcomingToActive: result.upcomingToActiveCount,
            activeToEnded: result.activeToEndedCount,
          });
        } else {
          alert("Không có lịch chiếu nào cần cập nhật");
        }

        // Tải lại dữ liệu timeline để cập nhật giao diện
        await loadTimelineData();
      } else {
        console.error("Lỗi khi cập nhật lịch chiếu:", result.message);
        alert("Lỗi: " + result.message);
      }
    } catch (error) {
      console.error("Lỗi kết nối khi cập nhật lịch chiếu:", error);
      alert("Có lỗi xảy ra khi cập nhật lịch chiếu. Vui lòng thử lại.");
    } finally {
      // Khôi phục trạng thái button
      updateBtn.disabled = false;
      updateBtn.innerHTML = originalText;
    }
  }

  // Khởi tạo ứng dụng
  init();
});
