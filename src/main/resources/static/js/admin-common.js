/**
 * Common JavaScript utilities cho admin interface
 * Tách riêng để tái sử dụng cho nhiều trang admin
 */

/**
 * Khởi tạo sidebar toggle functionality
 */
function initializeSidebarToggle() {
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('sidebar');
    const mainContentWrapper = document.getElementById('mainContentWrapper');

    if (!sidebarToggle || !sidebar || !mainContentWrapper) return;

    function toggleSidebar() {
        sidebar.classList.toggle('collapsed');
        mainContentWrapper.classList.toggle('expanded');
    }

    sidebarToggle.addEventListener('click', toggleSidebar);

    // Responsive behavior
    function checkViewport() {
        if (window.innerWidth <= 992) {
            if (!sidebar.classList.contains('collapsed')) {
                toggleSidebar();
            }
        }
    }

    checkViewport();
    window.addEventListener('resize', checkViewport);
}

/**
 * Khởi tạo file upload preview functionality
 * @param {string} uploadAreaId - ID của upload area
 * @param {string} fileInputId - ID của file input
 * @param {string} imagePreviewId - ID của image preview container
 * @param {string} previewImgId - ID của preview image element
 * @param {string} removeImageBtnId - ID của remove image button
 */
function initializeFileUploadPreview(uploadAreaId, fileInputId, imagePreviewId, previewImgId, removeImageBtnId) {
    const uploadArea = document.getElementById(uploadAreaId);
    const fileInput = document.getElementById(fileInputId);
    const imagePreview = document.getElementById(imagePreviewId);
    const previewImg = document.getElementById(previewImgId);
    const removeImageBtn = document.getElementById(removeImageBtnId);

    if (!uploadArea || !fileInput || !imagePreview || !previewImg || !removeImageBtn) {
        console.warn('Một hoặc nhiều element cho file upload không tìm thấy');
        return;
    }

    // Click để chọn file
    uploadArea.addEventListener('click', () => fileInput.click());

    // Drag & drop functionality
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });

    uploadArea.addEventListener('dragleave', () => {
        uploadArea.classList.remove('dragover');
    });

    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFileSelect(files[0]);
        }
    });

    // File input change
    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            handleFileSelect(e.target.files[0]);
        }
    });

    // Remove image
    removeImageBtn.addEventListener('click', () => {
        fileInput.value = '';
        imagePreview.classList.add('d-none');
        removeImageBtn.classList.add('d-none');
        
        // Clear validation error nếu có
        const imageLabel = document.querySelector(`label[for="${fileInputId}"]`);
        if (imageLabel) imageLabel.classList.remove('required-error');
    });

    function handleFileSelect(file) {
        if (file && file.type.startsWith('image/')) {
            // Check file size (5MB max)
            if (file.size > 5 * 1024 * 1024) {
                alert('File quá lớn. Kích thước tối đa là 5MB.');
                fileInput.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = function (e) {
                previewImg.src = e.target.result;
                imagePreview.classList.remove('d-none');
                removeImageBtn.classList.remove('d-none');

                // Fade-in effect
                imagePreview.style.opacity = '0';
                setTimeout(() => {
                    imagePreview.style.transition = 'opacity 0.3s ease';
                    imagePreview.style.opacity = '1';
                }, 50);

                // Clear validation error nếu có
                const imageLabel = document.querySelector(`label[for="${fileInputId}"]`);
                if (imageLabel) imageLabel.classList.remove('required-error');
            };
            reader.readAsDataURL(file);
        } else {
            alert('File không hợp lệ. Vui lòng chọn file hình ảnh (PNG, JPG, GIF).');
            fileInput.value = '';
        }
    }
}

/**
 * Utility function để hiển thị thông báo
 * @param {string} message - Nội dung thông báo
 * @param {string} type - Loại thông báo: 'success', 'error', 'warning', 'info'
 * @param {number} duration - Thời gian hiển thị (ms), mặc định 5000ms
 */
function showNotification(message, type = 'info', duration = 5000) {
    // Tạo element thông báo
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    
    notification.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : type === 'warning' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
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

/**
 * Utility function để validate required fields
 * @param {string} formId - ID của form cần validate
 * @returns {boolean} - true nếu tất cả field hợp lệ
 */
function validateRequiredFields(formId) {
    const form = document.getElementById(formId);
    if (!form) return true;
    
    const requiredFields = form.querySelectorAll('input[required], textarea[required], select[required]');
    let isValid = true;
    let firstInvalidField = null;
    
    requiredFields.forEach(field => {
        const label = document.querySelector(`label[for="${field.id}"]`);
        const isEmpty = !field.value || field.value.trim() === '';
        
        if (isEmpty) {
            if (label) label.classList.add('required-error');
            isValid = false;
            if (!firstInvalidField) {
                firstInvalidField = field;
            }
        } else {
            if (label) label.classList.remove('required-error');
        }
    });
    
    if (firstInvalidField) {
        firstInvalidField.focus();
        firstInvalidField.scrollIntoView({
            behavior: 'smooth',
            block: 'center'
        });
    }
    
    return isValid;
}

/**
 * Utility function để format date
 * @param {Date|string} date - Date object hoặc date string
 * @param {string} format - Format pattern, mặc định 'dd/MM/yyyy'
 * @returns {string} - Formatted date string
 */
function formatDate(date, format = 'dd/MM/yyyy') {
    if (!date) return '';
    
    const d = new Date(date);
    if (isNaN(d.getTime())) return '';
    
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    
    return format
        .replace('dd', day)
        .replace('MM', month)
        .replace('yyyy', year);
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

// Export functions nếu sử dụng modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initializeSidebarToggle,
        initializeFileUploadPreview,
        showNotification,
        validateRequiredFields,
        formatDate,
        debounce
    };
}
