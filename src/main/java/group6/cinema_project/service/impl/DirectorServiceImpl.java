package group6.cinema_project.service.impl;

import group6.cinema_project.entity.Director;
import group6.cinema_project.repository.DirectorRepository;
import group6.cinema_project.service.DirectorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DirectorServiceImpl implements DirectorService {

    private final DirectorRepository directorRepository;

    @Override
    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    @Override
    public Director getDirectorById(Integer id) {
        return directorRepository.findById(id).orElse(null);
    }

    @Override
    public Director getDirectorByName(String name) {
        return directorRepository.findFirstByName(name).orElse(null);
    }

    @Override
    public void addOrUpdateDirector(Director director) {
        directorRepository.save(director);
    }

    @Override
    public void deleteDirector(Integer id) {
        directorRepository.deleteById(id);
    }

    // Phương thức để tìm hoặc tạo mới directors từ danh sách tên
    @Override
    public Set<Director> findOrCreateDirectors(String directorsString) {
        Set<Director> directors = new HashSet<>();
        if (directorsString == null || directorsString.trim().isEmpty()) {
            return directors;
        }

        String[] directorNames = directorsString.split(",");
        for (String name : directorNames) {
            String trimmedName = name.trim();
            if (!trimmedName.isEmpty()) {
                Director director = directorRepository.findFirstByName(trimmedName).orElse(null);
                if (director == null) {
                    try {
                        director = new Director();
                        director.setName(trimmedName);
                        director = directorRepository.save(director);
                    } catch (Exception e) {
                        // If save fails due to duplicate, try to find again
                        director = directorRepository.findFirstByName(trimmedName).orElse(null);
                        if (director == null) {
                            throw new RuntimeException("Failed to create or find director: " + trimmedName, e);
                        }
                    }
                }
                directors.add(director);
            }
        }
        return directors;
    }
}