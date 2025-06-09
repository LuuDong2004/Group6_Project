package group6.cinema_project.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.repository.ActorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import group6.cinema_project.service.ActorService;

@Service
@RequiredArgsConstructor
@Transactional
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepository;

    @Override
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    @Override
    public Actor getActorById(Integer id) {
        return actorRepository.findById(id).orElse(null);
    }

    @Override
    public Actor getActorByName(String name) {
        return actorRepository.findFirstByName(name).orElse(null);
    }

    @Override
    public void addOrUpdateActor(Actor actor) {
        actorRepository.save(actor);
    }

    @Override
    public void deleteActor(Integer id) {
        actorRepository.deleteById(id);
    }

    // Phương thức để tìm hoặc tạo mới actors từ danh sách tên
    public Set<Actor> findOrCreateActors(String actorsString) {
        Set<Actor> actors = new HashSet<>();
        if (actorsString == null || actorsString.trim().isEmpty()) {
            return actors;
        }

        String[] actorNames = actorsString.split(",");
        for (String name : actorNames) {
            String trimmedName = name.trim();
            if (!trimmedName.isEmpty()) {
                Actor actor = actorRepository.findFirstByName(trimmedName).orElse(null);
                if (actor == null) {
                    try {
                        actor = new Actor();
                        actor.setName(trimmedName);
                        actor = actorRepository.save(actor);
                    } catch (Exception e) {
                        // If save fails due to duplicate, try to find again
                        actor = actorRepository.findFirstByName(trimmedName).orElse(null);
                        if (actor == null) {
                            throw new RuntimeException("Failed to create or find actor: " + trimmedName, e);
                        }
                    }
                }
                actors.add(actor);
            }
        }
        return actors;
    }
}
