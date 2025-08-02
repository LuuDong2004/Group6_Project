package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.PersonSimpleDto;
import group6.cinema_project.dto.ReviewDto;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.entity.Genre;
import group6.cinema_project.entity.Qa.Review;
import group6.cinema_project.repository.User.ReviewRepository;

import group6.cinema_project.service.User.IMovieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import group6.cinema_project.repository.User.MovieRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieService implements IMovieService {
    @Autowired
    private MovieRepository movieReponsitory;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ModelMapper modelMapper;

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

    public List<MovieDto> findMovieById(Integer moiveId) {
        List<Movie> movies = movieReponsitory.findMovieById(moiveId);
        return movies.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
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

        // Xử lý Rating - chỉ lấy ID để tránh lazy loading
        if (movie.getRating() != null) {
            dto.setRatingId(movie.getRating().getId());
        }

        // Xử lý Genres - chỉ lấy ID để tránh lazy loading
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            Set<Integer> genreIds = movie.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            dto.setGenreIds(genreIds);
        }

        dto.setLanguage(movie.getLanguage());
        dto.setImage(movie.getImage());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setTrailer(movie.getTrailer());
        dto.setStatus(movie.getStatus());

        return dto;
    }

    @Override
    public MovieDto getMovieById(Integer movieId) {
        List<Movie> movies = movieReponsitory.findMovieById(movieId);
        if (movies != null && !movies.isEmpty()) {
            return convertToBasicDto(movies.get(0));
        }
        return null;
    }

    // Method mới để lấy chi tiết phim với actors, directors và reviews
    public MovieDto getMovieDetail(Integer movieId) {
        Movie movie = movieReponsitory.findById(movieId).orElse(null);
        if (movie == null) {
            return null;
        }

        MovieDto dto = convertToBasicDto(movie);

        // Map actors với thông tin chi tiết
        if (movie.getActors() != null) {
            dto.setActorsDetail(movie.getActors().stream()
                    .map(actor -> new PersonSimpleDto(actor.getId(), actor.getName(), actor.getImageUrl()))
                    .collect(Collectors.toList()));
        }

        // Map directors với thông tin chi tiết
        if (movie.getDirectors() != null) {
            dto.setDirectorsDetail(movie.getDirectors().stream()
                    .map(director -> new PersonSimpleDto(director.getId(), director.getName(), director.getImageUrl()))
                    .collect(Collectors.toList()));
        }

        // Map reviews
        if (reviewRepository != null) {
            List<Review> reviews = reviewRepository.findByMovie_Id(movieId.longValue());
            if (reviews != null) {
                dto.setReviews(reviews.stream()
                        .map(review -> {
                            ReviewDto rd = new ReviewDto();
                            rd.setId(review.getId());
                            rd.setUser("User " + review.getUserId()); // hoặc lấy tên user nếu có
                            rd.setComment(review.getComment());
                            rd.setRating(review.getRating());
                            rd.setDate(review.getDate());
                            return rd;
                        }).collect(Collectors.toList()));
            }
        }

        return dto;
    }
}
