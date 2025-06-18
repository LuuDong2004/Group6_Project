package group6.cinema_project.service;

import group6.cinema_project.entity.Director;
import java.util.List;
import java.util.Set;

public interface DirectorService {
    List<Director> getAllDirectors();
    Director getDirectorById(Integer id);
    Director getDirectorByName(String name);
    void addOrUpdateDirector(Director director);
    void deleteDirector(Integer id);
    Set<Director> findOrCreateDirectors(String directorsString);
}