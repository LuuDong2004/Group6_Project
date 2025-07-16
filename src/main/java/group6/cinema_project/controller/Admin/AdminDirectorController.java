package group6.cinema_project.controller.Admin;



import group6.cinema_project.entity.Director;

import group6.cinema_project.service.Admin.IAdminActorService;
import group6.cinema_project.service.Admin.IAdminDirectorService;
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
@RequestMapping("/admin/directors")
@RequiredArgsConstructor
@Slf4j
public class AdminDirectorController {

    private final IAdminDirectorService directorService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/directors/";

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
    public String listDirectors(Model model,
                                @RequestParam(value = "searchTerm", required = false) String searchTerm) {
        log.info("Displaying director list page with search term: {}", searchTerm);

        try {
            List<Director> directors;

            // For now, we'll get all directors. Later we can implement search functionality
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                directors = directorService.getAllDirectors();
                log.info("Search functionality not yet implemented, showing all directors");
            } else {
                directors = directorService.getAllDirectors();
            }

            model.addAttribute("directors", directors);
            model.addAttribute("searchTerm", searchTerm != null ? searchTerm : "");

            log.info("Successfully loaded {} directors for display", directors.size());
            return "admin/admin_director_list";

        } catch (Exception e) {
            log.error("Error loading directors list", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách đạo diễn");
            model.addAttribute("directors", List.of());
            model.addAttribute("searchTerm", searchTerm != null ? searchTerm : "");
            return "admin/admin_director_list";
        }
    }

    @GetMapping("/add")
    public String showAddDirectorForm(Model model) {
        log.info("Displaying add director form");
        model.addAttribute("director", new Director());
        return "admin/admin_director_add";
    }

    @PostMapping("/add")
    public String addDirector(@Valid @ModelAttribute("director") Director director,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        log.info("Processing add director request for: {}", director.getName());

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in add director form");
            return "admin/admin_director_add";
        }

        try {
            // Check if director with same name already exists
            Director existingDirector = directorService.getDirectorByName(director.getName());
            if (existingDirector != null) {
                bindingResult.rejectValue("name", "error.director", "Đạo diễn với tên này đã tồn tại");
                return "admin/admin_director_add";
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

                director.setImageUrl("/uploads/directors/" + fileName);
                log.info("Successfully uploaded image for director: {}", fileName);
            }

            directorService.addOrUpdateDirector(director);
            log.info("Successfully added director: {}", director.getName());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đạo diễn '" + director.getName() + "' đã được thêm thành công!");
            return "redirect:/admin/directors/list";

        } catch (IOException e) {
            log.error("Error uploading image for director", e);
            bindingResult.rejectValue("imageUrl", "error.director", "Có lỗi xảy ra khi tải lên hình ảnh");
            return "admin/admin_director_add";
        } catch (Exception e) {
            log.error("Error adding director", e);
            bindingResult.rejectValue("name", "error.director", "Có lỗi xảy ra khi thêm đạo diễn");
            return "admin/admin_director_add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditDirectorForm(@PathVariable Integer id, Model model) {
        log.info("Displaying edit director form for ID: {}", id);

        try {
            Director director = directorService.getDirectorById(id);
            if (director == null) {
                log.warn("Director not found with ID: {}", id);
                model.addAttribute("error", "Không tìm thấy đạo diễn với ID: " + id);
                return "redirect:/admin/directors/list";
            }

            model.addAttribute("director", director);
            return "admin/admin_director_edit";

        } catch (Exception e) {
            log.error("Error loading director for edit", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải thông tin đạo diễn");
            return "redirect:/admin/directors/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateDirector(@PathVariable Integer id,
                                 @Valid @ModelAttribute("director") Director director,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        log.info("Processing update director request for ID: {}", id);

        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in edit director form");
            return "admin/admin_director_edit";
        }

        try {
            // Get existing director to preserve image if no new image is uploaded
            Director existingDirector = directorService.getDirectorById(id);
            if (existingDirector == null) {
                log.warn("Director not found with ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đạo diễn với ID: " + id);
                return "redirect:/admin/directors/list";
            }

            // Ensure the ID is set correctly
            director.setId(id);

            // Check if another director with same name exists (excluding current director)
            Director directorWithSameName = directorService.getDirectorByName(director.getName());
            if (directorWithSameName != null && !directorWithSameName.getId().equals(id)) {
                bindingResult.rejectValue("name", "error.director", "Đạo diễn với tên này đã tồn tại");
                return "admin/admin_director_edit";
            }

            // Handle image upload (only if new image is provided)
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path filePath = java.nio.file.Paths.get(UPLOAD_DIR + fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                director.setImageUrl("/uploads/directors/" + fileName);
                log.info("Successfully uploaded new image for director: {}", fileName);
            } else {
                // Keep existing image if no new image provided
                director.setImageUrl(existingDirector.getImageUrl());
            }

            directorService.addOrUpdateDirector(director);
            log.info("Successfully updated director: {}", director.getName());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đạo diễn '" + director.getName() + "' đã được cập nhật thành công!");
            return "redirect:/admin/directors/list";

        } catch (IOException e) {
            log.error("Error uploading image for director", e);
            bindingResult.rejectValue("imageUrl", "error.director", "Có lỗi xảy ra khi tải lên hình ảnh");
            return "admin/admin_director_edit";
        } catch (Exception e) {
            log.error("Error updating director", e);
            bindingResult.rejectValue("name", "error.director", "Có lỗi xảy ra khi cập nhật đạo diễn");
            return "admin/admin_director_edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteDirector(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        log.info("Processing delete director request for ID: {}", id);

        try {
            Director director = directorService.getDirectorById(id);
            if (director == null) {
                log.warn("Director not found with ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đạo diễn với ID: " + id);
                return "redirect:/admin/directors/list";
            }

            String directorName = director.getName();
            directorService.deleteDirector(id);
            log.info("Successfully deleted director: {}", directorName);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đạo diễn '" + directorName + "' đã được xóa thành công!");
            return "redirect:/admin/directors/list";

        } catch (Exception e) {
            log.error("Error deleting director with ID: " + id, e);
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi xóa đạo diễn. Có thể đạo diễn này đang được sử dụng trong phim.");
            return "redirect:/admin/directors/list";
        }
    }
}
