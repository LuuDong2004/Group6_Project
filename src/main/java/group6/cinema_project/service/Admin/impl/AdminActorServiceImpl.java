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

    // Sử dụng AdminActorRepository để thao tác với cơ sở dữ liệu
    private final AdminActorRepository adminActorRepository;

    /**
     * Lấy danh sách tất cả diễn viên
     * Sử dụng: adminActorRepository.findAll() từ JpaRepository
     * 
     * @return Danh sách tất cả diễn viên
     */
    @Override
    public List<Actor> getAllActors() {
        return adminActorRepository.findAll();
    }

    /**
     * Lấy diễn viên theo ID
     * Sử dụng: adminActorRepository.findById() từ JpaRepository
     * 
     * @param id ID của diễn viên
     * @return Diễn viên tìm được hoặc null nếu không tìm thấy
     */
    @Override
    public Actor getActorById(Integer id) {
        return adminActorRepository.findById(id).orElse(null);
    }

    /**
     * Lấy diễn viên theo tên
     * Sử dụng: adminActorRepository.findFirstByName() từ AdminActorRepository
     * 
     * @param name Tên diễn viên
     * @return Diễn viên đầu tiên tìm được hoặc null nếu không tìm thấy
     */
    @Override
    public Actor getActorByName(String name) {
        return adminActorRepository.findFirstByName(name).orElse(null);
    }

    /**
     * Thêm mới hoặc cập nhật diễn viên
     * Sử dụng: adminActorRepository.save() từ JpaRepository
     * 
     * @param actor Thông tin diễn viên cần lưu
     */
    @Override
    public void addOrUpdateActor(Actor actor) {
        adminActorRepository.save(actor);
    }

    /**
     * Xóa diễn viên theo ID
     * Sử dụng: adminActorRepository.deleteById() từ JpaRepository
     * 
     * @param id ID của diễn viên cần xóa
     */
    @Override
    public void deleteActor(Integer id) {
        adminActorRepository.deleteById(id);
    }

    /**
     * Tìm hoặc tạo mới diễn viên từ danh sách tên (phân cách bằng dấu phẩy)
     * Sử dụng: adminActorRepository.findFirstByName() và
     * adminActorRepository.save()
     * 
     * @param actorsString Chuỗi tên diễn viên phân cách bằng dấu phẩy
     * @return Set chứa các diễn viên đã tìm được hoặc tạo mới
     */
    public Set<Actor> findOrCreateActors(String actorsString) {
        Set<Actor> actors = new HashSet<>();
        if (actorsString == null || actorsString.trim().isEmpty()) {
            return actors;
        }

        String[] actorNames = actorsString.split(",");
        for (String name : actorNames) {
            String trimmedName = name.trim();
            if (!trimmedName.isEmpty()) {
                // Tìm diễn viên theo tên sử dụng adminActorRepository.findFirstByName()
                Actor actor = adminActorRepository.findFirstByName(trimmedName).orElse(null);
                if (actor == null) {
                    try {
                        // Tạo mới diễn viên nếu chưa tồn tại
                        actor = new Actor();
                        actor.setName(trimmedName);
                        // Lưu diễn viên mới sử dụng adminActorRepository.save()
                        actor = adminActorRepository.save(actor);
                    } catch (Exception e) {
                        // Nếu lưu thất bại do trùng lặp, thử tìm lại
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
