// Movie Detail Page JavaScript
document.addEventListener('DOMContentLoaded', function() {

    // Show comment form button
    const showCommentFormBtn = document.getElementById('showCommentFormBtn');
    const addCommentSection = document.getElementById('addCommentSection');

    if (showCommentFormBtn && addCommentSection) {
        showCommentFormBtn.addEventListener('click', function() {
            addCommentSection.style.display = 'block';
            showCommentFormBtn.style.display = 'none';
        });
    }

    // Submit comment functionality
    const submitCommentBtn = document.getElementById('submitComment');
    const userComment = document.getElementById('userComment');
    const userRating = document.getElementById('userRating');
    const commentMsg = document.getElementById('commentMsg');
    const commentWarnMsg = document.getElementById('commentWarnMsg');

    if (submitCommentBtn) {
        submitCommentBtn.addEventListener('click', function() {
            const comment = userComment.value.trim();
            const rating = userRating.value;

            if (!comment) {
                commentWarnMsg.textContent = 'Vui lòng nhập nội dung đánh giá!';
                return;
            }

            // Lấy movie ID từ URL
            const urlParams = new URLSearchParams(window.location.search);
            const movieId = window.location.pathname.split('/').pop(); // Lấy ID từ URL /movie/view/{id}

            // Gửi review đến server
            fetch('/review/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `movieId=${movieId}&comment=${encodeURIComponent(comment)}&rating=${rating}`
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        commentMsg.textContent = data.message;
                        commentWarnMsg.textContent = '';
                        userComment.value = '';

                        // Reload trang để hiển thị review mới
                        setTimeout(() => {
                            window.location.reload();
                        }, 1500);
                    } else {
                        commentWarnMsg.textContent = data.message || 'Có lỗi xảy ra khi gửi đánh giá';
                        commentMsg.textContent = '';
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    commentWarnMsg.textContent = 'Có lỗi xảy ra khi gửi đánh giá';
                    commentMsg.textContent = '';
                });
        });
    }

    // Book ticket button - now it's a link, so we don't need click handler
    // The link will handle the navigation automatically

    // Trailer iframe error handling
    const movieTrailer = document.getElementById('movieTrailer');
    if (movieTrailer) {
        movieTrailer.addEventListener('error', function() {
            this.style.display = 'none';
            const trailerContainer = this.parentElement;
            if (trailerContainer) {
                trailerContainer.innerHTML = '<p style="color: #ccc; text-align: center; padding: 20px;">Trailer không khả dụng</p>';
            }
        });
    }
}); 