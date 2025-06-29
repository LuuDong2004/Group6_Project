// movies.js
$(document).ready(function () {
    // Load tất cả phim khi trang load
    $.ajax({
        url: '/api/movies',
        method: 'GET',
        success: function(movies) {
            movies.sort((a, b) => a.id - b.id);
            renderMovieList(movies);
        }
    });

    // Sự kiện click vào thể loại
    $('.btn-choose-genre').on('click', function () {
        // Cập nhật class active
        $('.btn-choose-genre').removeClass('active');
        $(this).addClass('active');
        // Đổi tên nút dropdown
        $('#genre-dropdown-btn').text($(this).text());

        // Lấy thể loại
        var genre = $(this).data('genre');
        // Gọi API lấy phim theo thể loại
        if (genre === 'all') {
            $.ajax({
                url: '/api/movies',
                method: 'GET',
                success: function(movies) {
                    movies.sort((a, b) => a.id - b.id);
                    renderMovieList(movies);
                }
            });
        } else {
            $.get(`/api/movies/genre/${encodeURIComponent(genre)}`, function(movies) {
                renderMovieList(movies);
            });
        }
    });

    // Đóng modal khi click nút close
    document.querySelector('.close').addEventListener('click', () => {
        document.getElementById('movieModal').style.display = 'none';
    });

    // Đóng modal khi click ra ngoài
    window.addEventListener('click', (e) => {
        const modal = document.getElementById('movieModal');
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });
});

function renderMovieList(movies) {
    const movieList = document.getElementById('movie-list');
    movieList.innerHTML = '';
    if (!movies || movies.length === 0) {
        movieList.innerHTML = '<div style="color:#fff;text-align:center;margin:40px 0;">Không có phim nào phù hợp.</div>';
        return;
    }
    movies.forEach(function(movie) {
        const imageUrl = 'assets/images/' + movie.image;
        const card = document.createElement('div');
        card.className = 'movie-card';
        card.style.cursor = 'pointer';
        card.style.borderRadius = '16px';
        card.style.overflow = 'hidden';
        card.style.background = '#181818';
        card.style.margin = '16px';
        card.style.boxShadow = '0 2px 8px rgba(0,0,0,0.15)';
        card.style.display = 'inline-block';
        card.style.verticalAlign = 'top';
        card.style.width = '300px';
        card.addEventListener('click', function() {
            window.location.href = `movie_detail.html?id=${movie.id}`;
        });
        card.innerHTML = `
            <img src="${imageUrl}" alt="${movie.name}" class="movie-poster-img" style="width:100%;height:220px;object-fit:cover;">
            <div class="movie-info" style="padding: 16px 50px;">
                <div class="movie-title" style="font-weight:bold;font-size:1.1rem;color:#fff;">${movie.name}</div>
                <div class="movie-meta" style="margin-top:8px;color:#ccc;">
                    <span><i class="fa fa-clock-o"></i> ${movie.duration} phút</span>
                </div>
            </div>
        `;
        movieList.appendChild(card);
    });
}

// Function to fetch all movies from the API
async function fetchMovies() {
    try {
        const response = await fetch('/api/movies');
        const movies = await response.json();
        displayMovies(movies);
    } catch (error) {
        console.error('Error fetching movies:', error);
    }
}

// Function to display movies in the grid
function displayMovies(movies) {
    const movieList = document.getElementById('movie-list');
    movieList.innerHTML = ''; // Clear existing content

    movies.forEach(movie => {
        const movieCard = createMovieCard(movie);
        movieList.appendChild(movieCard);
    });
}

// Function to create a movie card
function createMovieCard(movie) {
    const movieGrid = document.createElement('div');
    movieGrid.className = 'w3l-populohny-grids';
    // Thêm đường dẫn nếu chỉ có tên file
    let imgSrc = movie.image && !movie.image.startsWith('assets/') ? 'assets/images/' + movie.image : movie.image;
    movieGrid.innerHTML = `
        <div class="item vhny-grid">
            <div class="box16 mb-0">
                <figure>
                    <img class="img-fluid movie-poster-img" src="${imgSrc}" alt="${movie.name}" data-id="${movie.id}">
                </figure>
                <a href="#" class="movie-link" data-movie-id="${movie.id}">
                    <div class="box-content">
                        <h3 class="title">${movie.name}</h3>
                        <h4>
                            <span class="post">
                                <span class="fa fa-clock-o"></span> ${movie.duration} phút
                            </span>
                            <span class="post fa fa-heart text-right"></span>
                        </h4>
                    </div>
                </a>
            </div>
        </div>
    `;

    // Click vào ảnh
    const posterImg = movieGrid.querySelector('.movie-poster-img');
    posterImg.addEventListener('click', () => {
        window.location.href = `movie_detail.html?id=${movie.id}`;
    });

    // Click vào overlay
    const movieLink = movieGrid.querySelector('.movie-link');
    movieLink.addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = `movie_detail.html?id=${movie.id}`;
    });

    return movieGrid;
}

// Function to show movie details in modal
async function showMovieDetails(movieId) {
    try {
        const response = await fetch(`/api/movies/${movieId}/detail`);
        const movie = await response.json();
        
        // Update modal content
        document.getElementById('moviePoster').src = movie.image;
        document.getElementById('movieTitle').textContent = movie.name;
        document.getElementById('movieGenre').textContent = movie.genre;
        document.getElementById('movieDirector').textContent = movie.directors.join(', ');
        document.getElementById('movieActors').textContent = movie.actors.join(', ');
        document.getElementById('movieDuration').textContent = movie.duration;
        document.getElementById('movieLanguage').textContent = movie.language;
        document.getElementById('movieReleaseDate').textContent = movie.release_date;
        document.getElementById('movieFormat').textContent = movie.format;
        document.getElementById('movieRating').textContent = movie.rating;
        document.getElementById('movieTrailer').src = movie.trailer;
        document.getElementById('movieSummary').textContent = movie.summary;

        // Show modal
        const modal = document.getElementById('movieModal');
        modal.style.display = 'block';
    } catch (error) {
        console.error('Error fetching movie details:', error);
    }
}

// Close modal when clicking the close button
document.querySelector('.close').addEventListener('click', () => {
    document.getElementById('movieModal').style.display = 'none';
});

// Close modal when clicking outside
window.addEventListener('click', (e) => {
    const modal = document.getElementById('movieModal');
    if (e.target === modal) {
        modal.style.display = 'none';
    }
});

// Initialize the page
document.addEventListener('DOMContentLoaded', () => {
    fetchMovies();
});

function getGenreFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get('genre');
}

function getSearchFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get('search');
} 