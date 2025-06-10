// movies.js
$(document).ready(function () {
    $.ajax({
        url: '/api/movies', // API trả về danh sách phim
        method: 'GET',
        success: function(movies) {
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
            $('#movie-list').html(html);

            // Sự kiện click vào poster hoặc nút chi tiết
            $('.movie-poster-img, .btn-detail').on('click', function() {
                const movieId = $(this).data('id');
                window.location.href = `movie_detail.html?id=${movieId}`;
            });
        }
    });
});

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
                    <img class="img-fluid" src="${imgSrc}" alt="${movie.name}">
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

    // Add click event listener to show movie details
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