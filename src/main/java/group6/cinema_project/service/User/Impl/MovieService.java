package group6.cinema_project.service.User.Impl;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.PersonSimpleDto;
import group6.cinema_project.dto.ReviewDto;
import group6.cinema_project.entity.Movie;
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
import java.util.stream.Collectors;

@Service
public class MovieService implements IMovieService {
    @Autowired
    private MovieRepository movieReponsitory;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ModelMapper modelMapper;

//    public MovieService() {
//    }
//
//    @Autowired
//    public MovieService(MovieRepository movieReponsitory) {
//
//        this.movieReponsitory = movieReponsitory;
//    }

    @Override
    public List<MovieDto> getAllMovie() {
        List<Movie> movies = movieReponsitory.findAll();
        return movies.stream()
                .map(movie -> modelMapper.map(movie , MovieDto.class))
                .collect(Collectors.toList());
    }
    //    @Override
//    public List<MovieDto> getMoviesByGenre(String gener){
//        List<Movie> movies = movieReponsitory.getMoviesByGenre(gener);
//
//        return movies.stream()
//                .map(movie -> modelMapper.map(movie, MovieDto.class))
//                .collect(Collectors.toList());
//    }
//    @Override
//    public List<MovieDto> getMoviesByTop3Rating(){
//        Pageable top3 = PageRequest.of(0, 3);
//        List<Movie> movies = movieReponsitory.getMoviesByTop3Rating(top3);
//        return movies.stream()
//                .map(movie -> modelMapper.map(movie, MovieDto.class))
//                .collect(Collectors.toList());
//    }
    @Override
    public List<MovieDto> getMoviesWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Movie> movies = movieReponsitory.findAll(pageable).getContent();
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }

    public List<MovieDto> findMovieById(Integer moiveId){
        List<Movie> movies = movieReponsitory.findMovieById(moiveId);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }

    public List<MovieDto> getTopMoviesToday() {
        int topN = 3;
        List<Movie> movies = movieReponsitory.findTopMovies7Days(topN);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }

    public List<MovieDto> getTopMovies7Days() {
        int topN = 3;
        List<Movie> movies = movieReponsitory.findTopMovies7Days(topN);
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> filterMovies(String genre, Integer year, String sort, String search) {
        List<Movie> movies = movieReponsitory.findAll();
        return movies.stream()
                .filter(m -> genre == null || genre.isEmpty() || (m.getGenre() != null && m.getGenre().toLowerCase().contains(genre.toLowerCase())))
                .filter(m -> search == null || search.isEmpty() || (m.getName() != null && m.getName().toLowerCase().contains(search.toLowerCase())))
                .sorted((m1, m2) -> {
                    if (sort == null || sort.equals("rating")) {
                        // Sắp xếp theo rating giảm dần
                        return m2.getRating().compareTo(m1.getRating());
                    } else if (sort.equals("az")) {
                        // Sắp xếp theo tên A-Z
                        return m1.getName().compareToIgnoreCase(m2.getName());
                    }
                    return 0;
                })
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllGenres() {
        return movieReponsitory.findAllGenres();
    }

    @Override
    public MovieDto getMovieById(Integer movieId) {
        List<Movie> movies = movieReponsitory.findMovieById(movieId);
        if (movies != null && !movies.isEmpty()) {
            return modelMapper.map(movies.get(0), MovieDto.class);
        }
        return null;
    }
    
    // Method mới để lấy chi tiết phim với actors, directors và reviews
    public MovieDto getMovieDetail(Integer movieId) {
        Movie movie = movieReponsitory.findById(movieId).orElse(null);
        if (movie == null) {
            return null;
        }
        
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        // Sửa đường dẫn hình ảnh nếu cần
        String imagePath = movie.getImage();
        if (imagePath != null && !imagePath.startsWith("/assets/")) {
            imagePath = "/assets/images/" + imagePath;
        }
        dto.setImage(imagePath);
        dto.setDuration(movie.getDuration());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setRating(movie.getRating());
        dto.setGenre(movie.getGenre());
        dto.setLanguage(movie.getLanguage());
        dto.setTrailer(movie.getTrailer());
        dto.setDescription(movie.getDescription());

        // Map actors
        if (movie.getActors() != null) {
            dto.setActors(movie.getActors().stream()
                .map(actor -> new PersonSimpleDto(actor.getId(), actor.getName(), actor.getImageUrl()))
                .collect(Collectors.toList()));
        }

        // Map directors
        if (movie.getDirectors() != null) {
            dto.setDirectors(movie.getDirectors().stream()
                .map(director -> new PersonSimpleDto(director.getId(), director.getName(), director.getImageUrl()))
                .collect(Collectors.toList()));
        }

        // Map reviews
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

        return dto;
    }
}
