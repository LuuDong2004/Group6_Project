package group6.cinema_project.service.Admin.impl;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.repository.Admin.AdminActorRepository;
import group6.cinema_project.service.Admin.IAdminActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminActorServiceImpl implements IAdminActorService {

    private final AdminActorRepository adminActorRepository;


    @Override
    public List<Actor> getAllActors() {
        return adminActorRepository.findAll();
    }

    @Override
    public Actor getActorById(Integer id) {
        return adminActorRepository.findById(id).orElse(null);
    }

    @Override
    public Actor getActorByName(String name) {
        return adminActorRepository.findFirstByName(name).orElse(null);
    }

    @Override
    public void addOrUpdateActor(Actor actor) {
        adminActorRepository.save(actor);
    }

    @Override
    public void deleteActor(Integer id) {
        adminActorRepository.deleteById(id);
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
                Actor actor = adminActorRepository.findFirstByName(trimmedName).orElse(null);
                if (actor == null) {
                    try {
                        actor = new Actor();
                        actor.setName(trimmedName);
                        actor =adminActorRepository.save(actor);
                    } catch (Exception e) {
                        // If save fails due to duplicate, try to find again
                        actor = adminActorRepository.findFirstByName(trimmedName).orElse(null);
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
