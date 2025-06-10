package group6.cinema_project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "director_movie")
public class DirectorMovie {
    @EmbeddedId
    private DirectorMovieId id;

    @ManyToOne
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @MapsId("directorId")
    @JoinColumn(name = "director_id")
    private Director director;

    public DirectorMovie() {}

    public DirectorMovie(Movie movie, Director director) {
        this.movie = movie;
        this.director = director;
        this.id = new DirectorMovieId(movie.getId(), director.getId());
    }

    public DirectorMovieId getId() {
        return id;
    }

    public void setId(DirectorMovieId id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }
}