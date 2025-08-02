package group6.cinema_project.controller.User;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StaticResourceController {

    @GetMapping("/assets/css/{filename}")
    public ResponseEntity<Resource> serveCss(@PathVariable String filename) {
        Resource resource = new ClassPathResource("static/assets/css/" + filename);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/css"))
                .body(resource);
    }

    @GetMapping("/assets/js/{filename}")
    public ResponseEntity<Resource> serveJs(@PathVariable String filename) {
        Resource resource = new ClassPathResource("static/assets/js/" + filename);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/javascript"))
                .body(resource);
    }

    @GetMapping("/assets/images/{filename}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        Resource resource = new ClassPathResource("static/assets/images/" + filename);
        String contentType = getImageContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(contentType))
                .body(resource);
    }

    private String getImageContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            default:
                return "application/octet-stream";
        }
    }
} 