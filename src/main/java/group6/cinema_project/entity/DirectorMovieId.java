package group6.cinema_project.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;

@Embeddable
public class DirectorMovieId implements Serializable {
    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "director_id")
    private Long directorId;

    public DirectorMovieId() {}

    public DirectorMovieId(Long movieId, Long directorId) {
        this.movieId = movieId;
        this.directorId = directorId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectorMovieId that = (DirectorMovieId) o;
        return Objects.equals(movieId, that.movieId) && Objects.equals(directorId, that.directorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, directorId);
    }
} 