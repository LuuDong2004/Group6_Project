package group6.cinema_project.controller;

import group6.cinema_project.entity.Actor;
import group6.cinema_project.service.ActorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/actors")
@RequiredArgsConstructor
@Slf4j
public class ActorController {

    private final ActorService actorService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/actors/";

    @PostConstruct
    public void init() {
        try {
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Could not create upload directory: {}", e.getMessage());
        }
    }

    @GetMapping("/list")
    public String listActors(Model model,
            @RequestParam(value = "searchTerm", required = false) String searchTerm) {
        log.info("Displaying actor list page with search term: {}", searchTerm);

        try {
            List<Actor> actors;

            // For now, we'll get all actors. Later we can implement search functionality
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                actors = actorService.getAllActors();
                log.info("Search functionality not yet implemented, showing all actors");
            } else {
                actors = actorService.getAllActors();
            }

            model.addAttribute("actors", actors);
            model.addAttribute("searchTerm", searchTerm != null ? searchTerm : "");

            log.info("Successfully loaded {} actors for display", actors.size());
            return "admin/admin_actor_list";

        } catch (Exception e) {
            log.error("Error loading actors list", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách diễn viên");
            model.addAttribute("actors", List.of());
            model.addAttribute("searchTerm", searchTerm != null ? searchTerm : "");
            return "admin/admin_actor_list";
        }
    }

    @GetMapping("/add")
    public String showAddActorForm(Model model) {
        log.info("Displaying add actor form");
        model.addAttribute("actor", new Actor());
        return "admin/admin_actor_add";
    }

    @PostMapping("/add")
    public String addActor(@Valid @ModelAttribute("actor") Actor actor,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        log.info("Processing add actor request for: {}", actor.getName());

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in add actor form");
            return "admin/admin_actor_add";
        }

        try {
            // Check if actor with same name already exists
            Actor existingActor = actorService.getActorByName(actor.getName());
            if (existingActor != null) {
                bindingResult.rejectValue("name", "error.actor", "Diễn viên với tên này đã tồn tại");
                return "admin/admin_actor_add";
            }

            // Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(UPLOAD_DIR);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                actor.setImageUrl("/uploads/actors/" + fileName);
                log.info("Successfully uploaded image for actor: {}", fileName);
            }

            actorService.addOrUpdateActor(actor);
            log.info("Successfully added actor: {}", actor.getName());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Diễn viên '" + actor.getName() + "' đã được thêm thành công!");
            return "redirect:/admin/actors/list";

        } catch (IOException e) {
            log.error("Error uploading image for actor", e);
            bindingResult.rejectValue("imageUrl", "error.actor", "Có lỗi xảy ra khi tải lên hình ảnh");
            return "admin/admin_actor_add";
        } catch (Exception e) {
            log.error("Error adding actor", e);
            bindingResult.rejectValue("name", "error.actor", "Có lỗi xảy ra khi thêm diễn viên");
            return "admin/admin_actor_add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditActorForm(@PathVariable Integer id, Model model) {
        log.info("Displaying edit actor form for ID: {}", id);

        try {
            Actor actor = actorService.getActorById(id);
            if (actor == null) {
                log.warn("Actor not found with ID: {}", id);
                model.addAttribute("error", "Không tìm thấy diễn viên với ID: " + id);
                return "redirect:/admin/actors/list";
            }

            model.addAttribute("actor", actor);
            return "admin/admin_actor_edit";

        } catch (Exception e) {
            log.error("Error loading actor for edit", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải thông tin diễn viên");
            return "redirect:/admin/actors/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateActor(@PathVariable Integer id,
            @Valid @ModelAttribute("actor") Actor actor,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        log.info("Processing update actor request for ID: {}", id);

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in edit actor form");
            return "admin/admin_actor_edit";
        }

        try {
            // Get existing actor to preserve image if no new image is uploaded
            Actor existingActor = actorService.getActorById(id);
            if (existingActor == null) {
                log.warn("Actor not found with ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy diễn viên với ID: " + id);
                return "redirect:/admin/actors/list";
            }

            // Ensure the ID is set correctly
            actor.setId(id);

            // Check if another actor with same name exists (excluding current actor)
            Actor actorWithSameName = actorService.getActorByName(actor.getName());
            if (actorWithSameName != null && !actorWithSameName.getId().equals(id)) {
                bindingResult.rejectValue("name", "error.actor", "Diễn viên với tên này đã tồn tại");
                return "admin/admin_actor_edit";
            }

            // Handle image upload (only if new image is provided)
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path filePath = java.nio.file.Paths.get(UPLOAD_DIR + fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                actor.setImageUrl("/uploads/actors/" + fileName);
                log.info("Successfully uploaded new image for actor: {}", fileName);
            } else {
                // Keep existing image if no new image provided
                actor.setImageUrl(existingActor.getImageUrl());
            }

            actorService.addOrUpdateActor(actor);
            log.info("Successfully updated actor: {}", actor.getName());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Diễn viên '" + actor.getName() + "' đã được cập nhật thành công!");
            return "redirect:/admin/actors/list";

        } catch (IOException e) {
            log.error("Error uploading image for actor", e);
            bindingResult.rejectValue("imageUrl", "error.actor", "Có lỗi xảy ra khi tải lên hình ảnh");
            return "admin/admin_actor_edit";
        } catch (Exception e) {
            log.error("Error updating actor", e);
            bindingResult.rejectValue("name", "error.actor", "Có lỗi xảy ra khi cập nhật diễn viên");
            return "admin/admin_actor_edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteActor(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        log.info("Processing delete actor request for ID: {}", id);

        try {
            Actor actor = actorService.getActorById(id);
            if (actor == null) {
                log.warn("Actor not found with ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy diễn viên với ID: " + id);
                return "redirect:/admin/actors/list";
            }

            String actorName = actor.getName();
            actorService.deleteActor(id);
            log.info("Successfully deleted actor: {}", actorName);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Diễn viên '" + actorName + "' đã được xóa thành công!");
            return "redirect:/admin/actors/list";

        } catch (Exception e) {
            log.error("Error deleting actor with ID: " + id, e);
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi xóa diễn viên. Có thể diễn viên này đang được sử dụng trong phim.");
            return "redirect:/admin/actors/list";
        }
    }
}
