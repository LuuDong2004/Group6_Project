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
     * - Cập nhật UPCOMING thành ACTIVE khi đến thời gian chiếu
     * - Cập nhật ACTIVE thành ENDED khi kết thúc chiếu
     */
    @Scheduled(fixedRate = 60000) // Chạy mỗi phút (60,000 ms)
    public void updateScheduleStatuses() {
        log.info("Đang chạy tác vụ cập nhật trạng thái lịch chiếu...");

        // Cập nhật UPCOMING -> ACTIVE
        int upcomingToActiveCount = scheduleService.updateUpcomingToActiveSchedules();
        if (upcomingToActiveCount > 0) {
            log.info("Đã cập nhật {} lịch chiếu từ UPCOMING thành ACTIVE", upcomingToActiveCount);
        }

        // Cập nhật ACTIVE -> ENDED
        int activeToEndedCount = scheduleService.updateExpiredScheduleStatuses();
        if (activeToEndedCount > 0) {
            log.info("Đã cập nhật {} lịch chiếu từ ACTIVE thành ENDED", activeToEndedCount);
        }

        int totalUpdated = upcomingToActiveCount + activeToEndedCount;
        if (totalUpdated > 0) {
            log.info("Tổng cộng đã cập nhật {} lịch chiếu", totalUpdated);
        }
    }
}
