// Xử lý chọn ngày
$(document).on('click', '.date-selector', function() {
  const date = $(this).data('date');
  const movieId = $(this).data('movie-id');
  if (date && movieId) {
    window.location.href = `/schedule/movie/${movieId}/date?date=${date}`;
  }
});

// Xử lý chọn rạp
$(document).on('click', '.branch-selector', function() {
  const branchId = $(this).data('branch-id');
  const movieId = $(this).data('movie-id');
  const date = $(this).data('date');
  if (branchId && movieId && date) {
    window.location.href = `/schedule/movie/${movieId}/date?date=${date}&branchId=${branchId}`;
  }
});

// Xử lý chọn lịch chiếu
$(document).on('click', '.schedule-selector', function() {
  const scheduleId = $(this).data('schedule-id');
  const movieId = $(this).data('movie-id');
  const date = $(this).data('date');
  const branchId = $(this).data('branch-id');
  if (scheduleId && movieId && date && branchId) {
    window.location.href = `/schedule/movie/${movieId}/date?date=${date}&branchId=${branchId}&scheduleId=${scheduleId}`;
  }
});