package group6.cinema_project.config;

import group6.cinema_project.entity.User;
import group6.cinema_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataLoader để tạo dữ liệu mẫu cho hệ thống.
 * Chạy khi ứng dụng khởi động và tạo các user mẫu nếu chưa có.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        tạoDữLiệuUserMẫu();
    }

    /**
     * Tạo dữ liệu user mẫu nếu bảng Users trống.
     */
    private void tạoDữLiệuUserMẫu() {
        try {
            // Kiểm tra xem đã có user nào chưa
            long userCount = userRepository.count();
            
            if (userCount == 0) {
                log.info("Bảng Users trống. Đang tạo dữ liệu mẫu...");
                
                // Tạo Admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123"); // Trong thực tế nên mã hóa password
                admin.setEmail("admin@cinema.com");
                admin.setPhone("0123456789");
                admin.setAddress("123 Đường ABC, Quận 1, TP.HCM");
                admin.setRole("ADMIN");
                admin.setDateOfBrith("1990-01-01");
                
                userRepository.save(admin);
                log.info("Đã tạo admin user: {}", admin.getUsername());
                
                // Tạo Manager user
                User manager = new User();
                manager.setUsername("manager");
                manager.setPassword("manager123");
                manager.setEmail("manager@cinema.com");
                manager.setPhone("0987654321");
                manager.setAddress("456 Đường XYZ, Quận 2, TP.HCM");
                manager.setRole("MANAGER");
                manager.setDateOfBrith("1985-05-15");
                
                userRepository.save(manager);
                log.info("Đã tạo manager user: {}", manager.getUsername());
                
                // Tạo Staff user
                User staff = new User();
                staff.setUsername("staff");
                staff.setPassword("staff123");
                staff.setEmail("staff@cinema.com");
                staff.setPhone("0369852147");
                staff.setAddress("789 Đường DEF, Quận 3, TP.HCM");
                staff.setRole("STAFF");
                staff.setDateOfBrith("1995-12-20");
                
                userRepository.save(staff);
                log.info("Đã tạo staff user: {}", staff.getUsername());
                
                // Tạo Customer user mẫu
                User customer = new User();
                customer.setUsername("customer");
                customer.setPassword("customer123");
                customer.setEmail("customer@gmail.com");
                customer.setPhone("0147258369");
                customer.setAddress("321 Đường GHI, Quận 4, TP.HCM");
                customer.setRole("CUSTOMER");
                customer.setDateOfBrith("2000-03-10");
                
                userRepository.save(customer);
                log.info("Đã tạo customer user: {}", customer.getUsername());
                
                log.info("Hoàn thành tạo {} users mẫu", userRepository.count());
                
            } else {
                log.info("Bảng Users đã có {} records. Bỏ qua việc tạo dữ liệu mẫu.", userCount);
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi tạo dữ liệu user mẫu", e);
        }
    }
}
