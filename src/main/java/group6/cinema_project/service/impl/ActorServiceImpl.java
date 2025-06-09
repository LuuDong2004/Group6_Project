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
        return actorRepository.findByName(name);
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
                Actor actor = actorRepository.findByName(trimmedName);
                if (actor == null) {
                    actor = new Actor();
                    actor.setName(trimmedName);
                    actorRepository.save(actor);
                }
                actors.add(actor);
            }
        }
        return actors;
    }
}
