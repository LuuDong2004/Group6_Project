/**
 * Utility functions for the cinema booking system
 */

// Utility function to format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Utility function to format date
function formatDate(date) {
    return new Date(date).toLocaleDateString('vi-VN');
}

// Utility function to format time
function formatTime(time) {
    return new Date('1970-01-01T' + time).toLocaleTimeString('vi-VN', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Utility function to show loading spinner
function showLoading(element) {
    if (element) {
        element.innerHTML = '<div class="spinner-border spinner-border-sm" role="status"><span class="sr-only">Loading...</span></div>';
    }
}

// Utility function to hide loading spinner
function hideLoading(element, originalContent) {
    if (element) {
        element.innerHTML = originalContent;
    }
}

// Utility function to show alert messages
function showAlert(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    // Insert at the top of the page
    const container = document.querySelector('.container') || document.body;
    container.insertBefore(alertDiv, container.firstChild);
    
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

// Utility function to validate email
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Utility function to validate phone number (Vietnamese format)
function isValidPhone(phone) {
    const phoneRegex = /^(0[3|5|7|8|9])+([0-9]{8})$/;
    return phoneRegex.test(phone);
}

// Utility function to debounce function calls
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

// Utility function to get CSRF token
function getCSRFToken() {
    const csrfMeta = document.querySelector('meta[name="_csrf"]');
    return csrfMeta ? csrfMeta.getAttribute('content') : null;
}

// Utility function to get CSRF header name
function getCSRFHeader() {
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    return csrfHeaderMeta ? csrfHeaderMeta.getAttribute('content') : null;
}

// Utility function to make AJAX requests with CSRF token
function makeAjaxRequest(url, options = {}) {
    const csrfToken = getCSRFToken();
    const csrfHeader = getCSRFHeader();
    
    const defaultOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include'
    };
    
    // Add CSRF token to headers if available
    if (csrfToken && csrfHeader) {
        defaultOptions.headers[csrfHeader] = csrfToken;
    }
    
    // Merge with provided options
    const finalOptions = { ...defaultOptions, ...options };
    if (options.headers) {
        finalOptions.headers = { ...defaultOptions.headers, ...options.headers };
    }
    
    return fetch(url, finalOptions);
}

// Utility function to scroll to element smoothly
function scrollToElement(element, offset = 0) {
    if (element) {
        const elementPosition = element.offsetTop - offset;
        window.scrollTo({
            top: elementPosition,
            behavior: 'smooth'
        });
    }
}

// Utility function to check if element is in viewport
function isInViewport(element) {
    const rect = element.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
}

// Export functions if using modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        formatCurrency,
        formatDate,
        formatTime,
        showLoading,
        hideLoading,
        showAlert,
        isValidEmail,
        isValidPhone,
        debounce,
        getCSRFToken,
        getCSRFHeader,
        makeAjaxRequest,
        scrollToElement,
        isInViewport
    };
}
