package group6.cinema_project.service.User.Impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.PersonSimpleDto;
import group6.cinema_project.dto.ReviewDto;
import group6.cinema_project.entity.Genre;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.service.User.IMovieService;

@Service
public class MovieService implements IMovieService {
    @Autowired
    private MovieRepository movieReponsitory;

    // public MovieService() {
    // }
    //
    // @Autowired
    // public MovieService(MovieRepository movieReponsitory) {
    //
    // this.movieReponsitory = movieReponsitory;
    // }

    @Override
    public List<MovieDto> getAllMovie() {
        List<Movie> movies = movieReponsitory.findAll();
        return movies.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    // @Override
    // public List<MovieDto> getMoviesByGenre(String gener){
    // List<Movie> movies = movieReponsitory.getMoviesByGenre(gener);
    //
    // return movies.stream()
    // .map(movie -> modelMapper.map(movie, MovieDto.class))
    // .collect(Collectors.toList());
    // }
    // @Override
    // public List<MovieDto> getMoviesByTop3Rating(){
    // Pageable top3 = PageRequest.of(0, 3);
    // List<Movie> movies = movieReponsitory.getMoviesByTop3Rating(top3);
    // return movies.stream()
    // .map(movie -> modelMapper.map(movie, MovieDto.class))
    // .collect(Collectors.toList());
    // }
    @Override
    public List<MovieDto> getMoviesWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Movie> movies = movieReponsitory.findAll(pageable).getContent();
        return movies.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> findMovieById(Integer movieId) {
        List<Movie> movies = movieReponsitory.findMovieById(movieId);
        return movies.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    public MovieDto getMovieById(Integer movieId) {
        return movieReponsitory.findById(movieId)
                .map(this::convertToBasicDto)
                .orElse(null);
    }

    @Override
    public MovieDto getMovieDetail(Integer movieId) {
        return movieReponsitory.findByIdWithAllRelations(movieId)
                .map(this::convertToDetailDto)
                .orElse(null);
    }

    public List<MovieDto> getTopMoviesToday() {
        int topN = 3;
        List<Movie> movies = movieReponsitory.findTopMovies7Days(topN);
        return movies.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    public List<MovieDto> getTopMovies7Days() {
        int topN = 3;
        List<Movie> movies = movieReponsitory.findTopMovies7Days(topN);
        return movies.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> filterMovies(String genre, Integer year, String sort, String search) {
        List<Movie> movies = movieReponsitory.findAll();
        return movies.stream()
                .filter(m -> genre == null || genre.isEmpty() || (m.getGenres() != null &&
                        m.getGenres().stream().anyMatch(g -> g.getName().toLowerCase().contains(genre.toLowerCase()))))
                .filter(m -> search == null || search.isEmpty()
                        || (m.getName() != null && m.getName().toLowerCase().contains(search.toLowerCase())))
                .sorted((m1, m2) -> {
                    if (sort == null || sort.equals("rating")) {
                        // Sắp xếp theo rating giảm dần (sử dụng rating code)
                        String rating1 = m1.getRating() != null ? m1.getRating().getCode() : "";
                        String rating2 = m2.getRating() != null ? m2.getRating().getCode() : "";
                        return rating2.compareTo(rating1);
                    } else if (sort.equals("az")) {
                        // Sắp xếp theo tên A-Z
                        return m1.getName().compareToIgnoreCase(m2.getName());
                    }
                    return 0;
                })
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllGenres() {
        return movieReponsitory.findAllGenres();
    }

    /**
     * Chuyển đổi Movie entity sang DTO mà không gây ra vấn đề lazy loading
     * Phương thức này chỉ map các field cơ bản để tránh các vấn đề cascade của
     * ModelMapper
     */
    private MovieDto convertToBasicDto(Movie movie) {
        MovieDto dto = new MovieDto();

        // Map các field cơ bản
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());

        // Xử lý Rating - lấy ID và set display text
        if (movie.getRating() != null) {
            dto.setRatingId(movie.getRating().getId());
            dto.setRatingDisplay(movie.getRating().getCode() + " - " + movie.getRating().getDescription());
        }

        // Xử lý Genres - lấy ID và set display text
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            Set<Integer> genreIds = movie.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            dto.setGenreIds(genreIds);

            String genreNames = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
            dto.setGenreDisplay(genreNames);
        }

        dto.setLanguage(movie.getLanguage());
        dto.setImage(movie.getImage());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setTrailer(movie.getTrailer());
        dto.setStatus(movie.getStatus());

        return dto;
    }

    /**
     * Chuyển đổi Movie entity sang DTO với đầy đủ thông tin cho movie detail page
     * Bao gồm genres, rating, directors, actors với eager loading
     */
    private MovieDto convertToDetailDto(Movie movie) {
        MovieDto dto = new MovieDto();

        // Map các field cơ bản
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setLanguage(movie.getLanguage());
        dto.setImage(movie.getImage());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setTrailer(movie.getTrailer());
        dto.setStatus(movie.getStatus());

        // Xử lý Rating - lấy ID và set display text
        if (movie.getRating() != null) {
            dto.setRatingId(movie.getRating().getId());
            dto.setRatingDisplay(movie.getRating().getCode() + " - " + movie.getRating().getDescription());
        }

        // Xử lý Genres - lấy ID và set display text
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            Set<Integer> genreIds = movie.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            dto.setGenreIds(genreIds);

            String genreNames = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
            dto.setGenreDisplay(genreNames);
        }

        // Xử lý Directors - chuyển đổi thành PersonSimpleDto
        if (movie.getDirectors() != null && !movie.getDirectors().isEmpty()) {
            List<PersonSimpleDto> directors = movie.getDirectors().stream()
                    .map(director -> new PersonSimpleDto(director.getId(), director.getName(), director.getImageUrl()))
                    .collect(Collectors.toList());
            dto.setDirectors(directors);
        }

        // Xử lý Actors - chuyển đổi thành PersonSimpleDto
        if (movie.getActors() != null && !movie.getActors().isEmpty()) {
            List<PersonSimpleDto> actors = movie.getActors().stream()
                    .map(actor -> new PersonSimpleDto(actor.getId(), actor.getName(), actor.getImageUrl()))
                    .collect(Collectors.toList());
            dto.setActors(actors);
        }

        // Xử lý Reviews - chuyển đổi thành ReviewDto
        if (movie.getReviews() != null && !movie.getReviews().isEmpty()) {
            List<ReviewDto> reviews = movie.getReviews().stream()
                    .map(review -> {
                        ReviewDto reviewDto = new ReviewDto();
                        reviewDto.setId(review.getId());
                        reviewDto.setUser("User " + review.getUserId()); // Có thể cải thiện để lấy tên thật
                        reviewDto.setComment(review.getComment());
                        reviewDto.setRating(review.getRating());
                        reviewDto.setDate(review.getDate());
                        return reviewDto;
                    })
                    .collect(Collectors.toList());
            dto.setReviews(reviews);
        }

        return dto;
    }
}
