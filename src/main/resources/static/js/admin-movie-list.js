/**
 * JavaScript tối giản cho Admin Movie List
 * Chỉ giữ lại những chức năng thực sự cần thiết
 */

document.addEventListener('DOMContentLoaded', function () {
    // Xử lý modal xóa phim
    initializeDeleteModal();
    
    // Responsive sidebar cho mobile (fallback nếu CSS không đủ)
    initializeMobileResponsive();
});

/**
 * Khởi tạo modal xóa phim
 */
function initializeDeleteModal() {
    const deleteMovieModal = document.getElementById('deleteMovieModal');
    if (!deleteMovieModal) return;

    deleteMovieModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const movieId = button.getAttribute('data-movie-id');
        const movieName = button.getAttribute('data-movie-name');
        
        const movieNameElement = deleteMovieModal.querySelector('#movieNameToDelete');
        const confirmDeleteBtn = deleteMovieModal.querySelector('#confirmDeleteBtn');
        
        if (movieNameElement && confirmDeleteBtn) {
            movieNameElement.textContent = movieName || 'phim này';
            confirmDeleteBtn.href = '/admin/movies/delete/' + movieId;
        }
    });
}

/**
 * Xử lý responsive cho mobile
 */
function initializeMobileResponsive() {
    function checkMobileViewport() {
        const checkbox = document.getElementById('sidebarToggleCheckbox');
        if (window.innerWidth <= 768 && checkbox && !checkbox.checked) {
            checkbox.checked = true;
        }
    }
    
    checkMobileViewport();
    window.addEventListener('resize', debounce(checkMobileViewport, 250));
}

/**
 * Utility function để debounce function calls
 * @param {Function} func - Function cần debounce
 * @param {number} wait - Thời gian chờ (ms)
 * @returns {Function} - Debounced function
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Utility function để hiển thị thông báo (nếu cần)
 * @param {string} message - Nội dung thông báo
 * @param {string} type - Loại thông báo: 'success', 'error', 'warning', 'info'
 * @param {number} duration - Thời gian hiển thị (ms), mặc định 5000ms
 */
function showNotification(message, type = 'info', duration = 5000) {
    // Tạo element thông báo
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    
    const iconMap = {
        'success': 'check-circle',
        'error': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    
    notification.innerHTML = `
        <i class="fas fa-${iconMap[type] || 'info-circle'} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    document.body.appendChild(notification);
    
    // Tự động xóa sau duration
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, duration);
}

// Export functions nếu sử dụng modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initializeDeleteModal,
        initializeMobileResponsive,
        showNotification,
        debounce
    };
}
