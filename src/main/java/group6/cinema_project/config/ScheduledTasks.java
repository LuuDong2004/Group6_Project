package group6.cinema_project.config;


import group6.cinema_project.service.Admin.IAdminScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
    private final IAdminScheduleService scheduleService;

    /**
     * Cập nhật tự động trạng thái lịch chiếu mỗi 1 phút
     * Tìm và cập nhật tất cả lịch chiếu đã kết thúc từ ACTIVE thành INACTIVE
     */
    @Scheduled(fixedRate = 60000) // Chạy mỗi phút (60,000 ms)
    public void updateScheduleStatuses() {
        log.info("Đang chạy tác vụ cập nhật trạng thái lịch chiếu...");
        int updatedCount = scheduleService.updateExpiredScheduleStatuses();

        if (updatedCount > 0) {
            log.info("Đã cập nhật {} lịch chiếu từ ACTIVE thành INACTIVE", updatedCount);
        }
    }
}
