package group6.cinema_project.controller.User;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.entity.Director;
import group6.cinema_project.service.User.IActorDirectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PersonDetailController {
    
    @Autowired
    private IActorDirectorService actorDirectorService;
    
    @GetMapping("/person_detail")
    public String getPersonDetail(@RequestParam("type") String type, 
                                 @RequestParam("id") Integer id, 
                                 Model model) {
        if ("actor".equalsIgnoreCase(type)) {
            Actor actor = actorDirectorService.getActorById(id);
            if (actor != null) {
                model.addAttribute("person", actor);
                model.addAttribute("personType", "actor");
                return "person_detail";
            }
        } else if ("director".equalsIgnoreCase(type)) {
            Director director = actorDirectorService.getDirectorById(id);
            if (director != null) {
                model.addAttribute("person", director);
                model.addAttribute("personType", "director");
                return "person_detail";
            }
        }
        
        // Nếu không tìm thấy, redirect về trang movies
        return "redirect:/movie/view";
    }
} 