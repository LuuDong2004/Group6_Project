// index.js

document.addEventListener('DOMContentLoaded', function () {
    // Load featured movies
    loadFeaturedMovies();
});

// Function to load featured movies
function loadFeaturedMovies() {
    fetch('/api/movies')
        .then(res => res.json())
        .then(movies => {
            // Sắp xếp giảm dần theo rating, lấy 10 phim đầu
            movies.sort(function(a, b) {
                const ratingA = (a.rating && !isNaN(a.rating)) ? Number(a.rating) : 0;
                const ratingB = (b.rating && !isNaN(b.rating)) ? Number(b.rating) : 0;
                return ratingB - ratingA;
            });
            const top8 = movies.slice(0, 8);
            displayMovies(top8, '#featured-movies');
        })
        .catch(error => {
            console.error('Error loading featured movies:', error);
        });
}

// Function to create a movie card
function createMovieCard(movie) {
    const imageUrl = 'assets/images/' + movie.image;
    const card = document.createElement('div');
    card.className = 'movie-card';
    card.innerHTML = `
        <div class="box16">
            <img src="${imageUrl}" alt="${movie.name}" class="movie-poster-img" data-id="${movie.id}">
            <div class="box-content">
                <h3 class="title">${movie.name}</h3>
                <h4>
                    <span class="post"><span class="fa fa-clock-o"></span> ${movie.duration} phút</span>
                    <span class="post fa fa-heart text-right"></span>
                </h4>
            </div>
        </div>
    `;
    // Click vào ảnh
    card.querySelector('.movie-poster-img').onclick = function() {
        window.location.href = `movie_detail.html?id=${movie.id}`;
    };
    // Click vào overlay
    card.querySelector('.box-content').onclick = function() {
        window.location.href = `movie_detail.html?id=${movie.id}`;
    };
    return card;
}

// Function to display movies in the grid
function displayMovies(movies, containerId) {
    console.log('Rendering movies:', movies, 'to', containerId);
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