package group6.cinema_project.controller.User;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.service.User.IActorDirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class PersonDetailController {

    @Autowired
    private IActorDirectorService actorDirectorService;

    @GetMapping("/person_detail")
    public String getPersonDetail(@RequestParam("type") String type,
            @RequestParam("id") Integer id,
            Model model) {
        log.info("Nhận request person_detail với type: {} và id: {}", type, id);

        if ("actor".equalsIgnoreCase(type)) {
            Actor actor = actorDirectorService.getActorById(id);
            log.info("Tìm thấy actor: {}", actor != null ? actor.getName() : "null");
            if (actor != null) {
                model.addAttribute("person", actor);
                model.addAttribute("personType", "actor");
                log.info("Trả về trang person_detail cho actor: {}", actor.getName());
                return "person_detail";
            }
        } else if ("director".equalsIgnoreCase(type)) {
            Director director = actorDirectorService.getDirectorById(id);
            log.info("Tìm thấy director: {}", director != null ? director.getName() : "null");
            if (director != null) {
                model.addAttribute("person", director);
                model.addAttribute("personType", "director");
                log.info("Trả về trang person_detail cho director: {}", director.getName());
                return "person_detail";
            }
        }

        log.warn("Không tìm thấy person với type: {} và id: {}, redirect về movies", type, id);
        // Nếu không tìm thấy, redirect về trang movies
        return "redirect:/movie/view";
    }
}