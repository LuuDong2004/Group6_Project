// movie_detail.js

function getMovieIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

document.addEventListener('DOMContentLoaded', async () => {
    const movieId = getMovieIdFromUrl();
    if (!movieId) return;
    const response = await fetch(`/api/movies/${movieId}/detail`);
    const movie = await response.json();
    // Render thông tin phim
    document.getElementById('moviePoster').src = movie.image && !movie.image.startsWith('assets/') ? 'assets/images/' + movie.image : (movie.image || '');
    document.getElementById('movieTitle').textContent = movie.name || '';
    // Cập nhật breadcrumbs với tên phim
    var breadcrumbMovieName = document.getElementById('breadcrumbMovieName');
    if (breadcrumbMovieName) breadcrumbMovieName.textContent = movie.name || 'Chi tiết phim';
    document.getElementById('movieGenre').textContent = movie.genre || '';
    // Render đạo diễn (ưu tiên directorsData, fallback directors)
    if (movie.directorsData && movie.directorsData.length > 0) {
        const directorLinks = movie.directorsData.map(d =>
            `<a href="person_detail.html?type=director&id=${d.id}" class="person-link">${d.name}</a>`
        );
        document.getElementById('movieDirector').innerHTML = directorLinks.join(', ');
    } else if (movie.directors && movie.directors.length > 0) {
        document.getElementById('movieDirector').textContent = movie.directors.join(', ');
    } else {
        document.getElementById('movieDirector').textContent = 'Không có thông tin';
    }
    // Hiển thị diễn viên (nếu có)
    const uniqueActors = movie.actors ? [...new Set(movie.actors)] : [];
    // Render diễn viên (nếu có dữ liệu id)
    if (movie.actorsData) {
        const actorLinks = movie.actorsData.map(a =>
            `<a href="person_detail.html?type=actor&id=${a.id}" class="person-link">${a.name}</a>`
        );
        document.getElementById('movieActors').innerHTML = actorLinks.join(', ');
    } else {
        document.getElementById('movieActors').textContent = uniqueActors.length > 0 ? uniqueActors.join(', ') : 'Không có thông tin';
    }
    document.getElementById('movieDuration').textContent = movie.duration ? movie.duration : '';
    document.getElementById('movieLanguage').textContent = movie.language || '';
    // Định dạng lại ngày khởi chiếu
    if (movie.release_date) {
        const date = new Date(movie.release_date);
        let formatted = '';
        if (!isNaN(date.getTime())) {
            formatted = date.toLocaleDateString('vi-VN');
        } else {
            formatted = movie.release_date.substring(0, 10);
        }
        document.getElementById('movieReleaseDate').textContent = formatted;
    } else {
        document.getElementById('movieReleaseDate').textContent = '';
    }
    // Hiển thị rating
    const ratingValue = movie.rating && !isNaN(movie.rating) && Number(movie.rating) > 0 ? Number(movie.rating).toFixed(1) + '/10' : 'Chưa có';
    document.getElementById('movieRating').textContent = ratingValue;
    document.getElementById('movieTrailer').src = movie.trailer || '';
    // Hiển thị bình luận từ database
    const commentsDiv = document.getElementById('movieComments');
    if (movie.reviews && movie.reviews.length > 0) {
        commentsDiv.innerHTML = movie.reviews.map(r =>
            `<div class="comment-item" style="margin-bottom:15px; padding:10px; border-bottom:1px solid #333;">
                <div><strong>${r.user}</strong> <span class="rating">(${r.rating}/10)</span></div>
                <div style="margin:5px 0; color:#ccc;">${r.comment}</div>
                <div style="color:#888; font-size:0.9em;">${r.date ? new Date(r.date).toLocaleDateString('vi-VN') : ''}</div>
            </div>`
        ).join('');
    } else {
        commentsDiv.innerHTML = '<div class="no-comments">Chưa có bình luận</div>';
    }

    // Nút Viết đánh giá
    const showBtn = document.getElementById('showCommentFormBtn');
    if (showBtn) {
        showBtn.onclick = async function() {
            const userId = localStorage.getItem('userId');
            const warnMsg = document.getElementById('commentWarnMsg');
            warnMsg.textContent = '';
            if (!userId) {
                warnMsg.textContent = 'Bạn cần đăng nhập để đánh giá phim!';
                return;
            }
            try {
                const checkRes = await fetch(`/api/booking/check?userId=${userId}&movieId=${movieId}`);
                const canComment = await checkRes.json();
                if (canComment === true) {
                    document.getElementById('addCommentSection').style.display = 'block';
                    showBtn.style.display = 'none';
                } else {
                    warnMsg.textContent = 'Bạn cần xem phim này mới được đánh giá!';
                }
            } catch (e) {
                warnMsg.textContent = 'Có lỗi xảy ra, vui lòng thử lại!';
            }
        };
    }

    // Xử lý gửi bình luận
    const submitBtn = document.getElementById('submitComment');
    if (submitBtn) {
        submitBtn.onclick = async function() {
            const userId = localStorage.getItem('userId');
            const comment = document.getElementById('userComment').value;
            const rating = document.getElementById('userRating').value;
            if (!comment) return;
            const res = await fetch('/api/review', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId, movieId, comment, rating })
            });
            if (res.ok) {
                document.getElementById('commentMsg').innerText = 'Gửi đánh giá thành công!';
                // Có thể reload lại bình luận nếu muốn
            } else {
                document.getElementById('commentMsg').innerText = 'Bạn không đủ điều kiện đánh giá!';
            }
        };
    }
});

async function showMovieDetails(movieId) {
    try {
        // Fetch movie details (gọi đúng API detail)
        const movieRes = await fetch(`/api/movies/${movieId}/detail`);
        if (!movieRes.ok) {
            throw new Error('Failed to fetch movie details');
        }
        const movie = await movieRes.json();

        // Update movie details
        document.getElementById('moviePoster').src = movie.image || 'assets/images/default-movie.jpg';
        document.getElementById('movieTitle').innerText = movie.name;
        document.getElementById('movieGenre').innerText = movie.genre;
        document.getElementById('movieDirector').innerText = movie.directors ? movie.directors.join(', ') : '';
        document.getElementById('movieActors').innerText = movie.actors ? movie.actors.join(', ') : '';
        document.getElementById('movieDuration').innerText = movie.duration;
        document.getElementById('movieLanguage').innerText = movie.language;
        document.getElementById('movieReleaseDate').innerText = movie.release_date;
        document.getElementById('movieFormat').innerText = movie.format;
        // Hiển thị rating
        const ratingValue = movie.rating && !isNaN(movie.rating) && Number(movie.rating) > 0 ? Number(movie.rating).toFixed(1) + '/10' : 'Chưa có';
        document.getElementById('movieRating').innerText = ratingValue;
        
        // Update trailer if available
        const trailerFrame = document.getElementById('movieTrailer');
        if (movie.trailer) {
            trailerFrame.src = movie.trailer;
            trailerFrame.parentElement.style.display = 'block';
        } else {
            trailerFrame.parentElement.style.display = 'none';
        }

        // Update summary
        document.getElementById('movieSummary').innerText = movie.summary || 'Chưa có tóm tắt phim';

        // Update comments
        const commentsDiv = document.getElementById('movieComments');
        if (movie.reviews && movie.reviews.length > 0) {
            commentsDiv.innerHTML = movie.reviews.map(r =>
                `<div class="comment-item" style="margin-bottom:15px; padding:10px; border-bottom:1px solid #eee;">
                    <div><strong>${r.user}</strong> <span class="rating">(${r.rating}/10)</span></div>
                    <div style="margin:5px 0;">${r.comment}</div>
                    <div style="color:#666; font-size:0.9em;">${r.date ? new Date(r.date).toLocaleDateString('vi-VN') : ''}</div>
                </div>`
            ).join('');
        } else {
            commentsDiv.innerHTML = '<div class="no-comments">Chưa có bình luận</div>';
        }

        // Update book ticket button
        const bookBtn = document.getElementById('bookTicketBtn');
        bookBtn.onclick = function() {
            window.location.href = `/ticket-booking.html?movieId=${movieId}`;
        };

        // Show modal
        modal.style.display = "block";
    } catch (error) {
        console.error('Error loading movie details:', error);
        alert('Có lỗi xảy ra khi tải thông tin phim. Vui lòng thử lại sau.');
    }
}

// Đã xóa đoạn gán onclick cho closePopupBtn vì không còn nút đóng popup 

document.getElementById('user-name').textContent = user.userName; 