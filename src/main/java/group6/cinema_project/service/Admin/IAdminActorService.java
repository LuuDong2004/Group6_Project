package group6.cinema_project.service.Admin;

import java.util.List;
import java.util.Set;
import group6.cinema_project.entity.Actor;
public interface IAdminActorService {
    List<Actor> getAllActors();
    Actor getActorById(Integer id);
    Actor getActorByName(String name);
    void addOrUpdateActor(Actor actor);
    void deleteActor(Integer id);
    Set<Actor> findOrCreateActors(String actorsString);
}
