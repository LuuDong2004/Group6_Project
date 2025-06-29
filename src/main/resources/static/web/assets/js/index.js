// index.js

document.addEventListener('DOMContentLoaded', function () {
    // Load featured movies
    loadFeaturedMovies();
});

// Function to load featured movies
function loadFeaturedMovies() {
    // Call the dedicated endpoint for featured movies which are pre-sorted by rating
    fetch('/api/movies/featured')
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(movies => {
            // The movies are already sorted by the backend, just display them
            displayMovies(movies, '#featured-movies');
        })
        .catch(error => {
            console.error('Error loading featured movies:', error);
            const container = document.querySelector('#featured-movies');
            if (container) {
                container.innerHTML = "<p style='color: var(--theme-para);'>Error loading featured movies.</p>";
            }
        });
}

// Function to create a movie card
function createMovieCard(movie) {
    const imageUrl = 'assets/images/' + movie.image;
    const card = document.createElement('div');
    card.className = 'movie-card';
    card.style.cursor = 'pointer';
    card.style.borderRadius = '16px';
    card.style.overflow = 'hidden';
    card.style.background = '#181818';
    card.style.boxShadow = '0 2px 8px rgba(0,0,0,0.15)';
    card.style.display = 'flex';
    card.style.flexDirection = 'column';
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
    return card;
}

// Function to display movies in the grid
function displayMovies(movies, containerId) {
    const container = document.querySelector(containerId);
    container.innerHTML = '';
    movies.forEach(function(movie) {
        const card = createMovieCard(movie);
        container.appendChild(card);
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