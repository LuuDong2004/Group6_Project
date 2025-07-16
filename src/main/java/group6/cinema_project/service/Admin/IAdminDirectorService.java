package group6.cinema_project.service.Admin;
import java.util.List;
import java.util.Set;
import group6.cinema_project.entity.Director;


public interface IAdminDirectorService {
    List<Director> getAllDirectors();
    Director getDirectorById(Integer id);
    Director getDirectorByName(String name);
    void addOrUpdateDirector(Director director);
    void deleteDirector(Integer id);
    Set<Director> findOrCreateDirectors(String directorsString);
}
