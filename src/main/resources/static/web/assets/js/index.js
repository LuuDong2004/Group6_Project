// index.js
$(document).ready(function () {
    // Load featured movies
    loadFeaturedMovies();
    // Load upcoming movies
    loadUpcomingMovies();
});

// Function to load featured movies
function loadFeaturedMovies() {
    $.ajax({
        url: '/api/movies/featured', // API endpoint for featured movies
        method: 'GET',
        success: function(movies) {
            displayMovies(movies, '#featured-movies');
        },
        error: function(error) {
            console.error('Error loading featured movies:', error);
        }
    });
}

// Function to load upcoming movies
function loadUpcomingMovies() {
    $.ajax({
        url: '/api/movies/upcoming', // API endpoint for upcoming movies
        method: 'GET',
        success: function(movies) {
            displayMovies(movies, '#upcoming-movies');
        },
        error: function(error) {
            console.error('Error loading upcoming movies:', error);
        }
    });
}

// Function to display movies in the grid
function displayMovies(movies, containerId) {
    let html = '';
    movies.forEach(function(movie) {
        const imageUrl = 'assets/images/' + movie.image;
        html += `
        <div class="movie-card">
            <img src="${imageUrl}" alt="${movie.name}" class="movie-poster-img" data-id="${movie.id}">
            <div class="movie-info">
                <div class="movie-title">${movie.name}</div>
                <div class="movie-meta">
                    <span><b>Thể loại:</b> ${movie.genre}</span><br>
                    <span><b>Thời lượng:</b> ${movie.duration} phút</span><br>
                    <span><b>Khởi chiếu:</b> ${movie.releaseDate || movie.release_date}</span>
                </div>
                <button class="btn btn-detail" data-id="${movie.id}">Xem chi tiết</button>
            </div>
        </div>
        `;
    });
    $(containerId).html(html);

    // Add click event listeners
    $(containerId + ' .movie-poster-img, ' + containerId + ' .btn-detail').on('click', function() {
        const movieId = $(this).data('id');
        window.location.href = `movie_detail.html?id=${movieId}`;
    });
}

// Scroll to top function
function topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}

// Show/hide scroll to top button based on scroll position
window.onscroll = function() {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        document.getElementById("movetop").style.display = "block";
    } else {
        document.getElementById("movetop").style.display = "none";
    }
}; 