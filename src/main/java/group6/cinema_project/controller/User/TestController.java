package group6.cinema_project.controller.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TestController {
    
    @GetMapping("/test-person-links")
    public String testPersonLinks() {
        log.info("Truy cập trang test person links");
        return "test_person_links";
    }
}
