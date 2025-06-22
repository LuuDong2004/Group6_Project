package group6.cinema_project.service;

import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.exception.ScheduleConflictException;
import group6.cinema_project.service.MovieScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify schedule conflict validation functionality.
 * This test demonstrates how the schedule conflict detection works.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MovieScheduleConflictTest {

    @Autowired
    private MovieScheduleService movieScheduleService;

    /**
     * Test case to demonstrate schedule conflict validation.
     * This test shows how to create conflicting schedules and verify that
     * the validation properly detects and prevents overlapping schedules.
     */
    @Test
    public void testScheduleConflictValidation() {
        // Create first schedule: Movie 1 from 10:00 to 12:00 (assuming 120 min duration)
        ScreeningScheduleDto schedule1 = createTestSchedule(
                1, // movieId
                1, // screeningRoomId  
                1, // branchId
                LocalDate.now().plusDays(1), // tomorrow
                LocalTime.of(10, 0), // 10:00 AM
                LocalTime.of(12, 0), // 12:00 PM
                new BigDecimal("100000")
        );

        // Create conflicting schedule: Movie 2 from 11:00 to 13:00 (same room, overlapping time)
        ScreeningScheduleDto conflictingSchedule = createTestSchedule(
                2, // different movieId
                1, // same screeningRoomId
                1, // same branchId
                LocalDate.now().plusDays(1), // same date
                LocalTime.of(11, 0), // 11:00 AM (overlaps with first schedule)
                LocalTime.of(13, 0), // 1:00 PM
                new BigDecimal("120000")
        );

        try {
            // First schedule should save successfully
            ScreeningScheduleDto savedSchedule1 = movieScheduleService.saveOrUpdateScreeningScheduleWithValidation(schedule1);
            assertNotNull(savedSchedule1.getId());
            System.out.println("✓ First schedule saved successfully: " + savedSchedule1.getId());

            // Second schedule should throw ScheduleConflictException
            assertThrows(ScheduleConflictException.class, () -> {
                movieScheduleService.saveOrUpdateScreeningScheduleWithValidation(conflictingSchedule);
            }, "Expected ScheduleConflictException for overlapping schedules");

            System.out.println("✓ Schedule conflict validation working correctly!");

        } catch (ScheduleConflictException e) {
            System.out.println("✓ Schedule conflict detected as expected:");
            System.out.println("  Error: " + e.getDetailedMessage());
        } catch (Exception e) {
            System.out.println("✗ Unexpected error during test:");
            System.out.println("  Error: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with unexpected exception: " + e.getMessage());
        }
    }

    /**
     * Test case for non-conflicting schedules in the same room.
     */
    @Test
    public void testNonConflictingSchedules() {
        // Create first schedule: 10:00 to 12:00
        ScreeningScheduleDto schedule1 = createTestSchedule(
                1, 1, 1,
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                new BigDecimal("100000")
        );

        // Create non-conflicting schedule: 14:00 to 16:00 (same room, different time)
        ScreeningScheduleDto schedule2 = createTestSchedule(
                2, 1, 1,
                LocalDate.now().plusDays(2),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                new BigDecimal("120000")
        );

        try {
            // Both schedules should save successfully
            ScreeningScheduleDto saved1 = movieScheduleService.saveOrUpdateScreeningScheduleWithValidation(schedule1);
            ScreeningScheduleDto saved2 = movieScheduleService.saveOrUpdateScreeningScheduleWithValidation(schedule2);

            assertNotNull(saved1.getId());
            assertNotNull(saved2.getId());
            System.out.println("✓ Non-conflicting schedules saved successfully!");

        } catch (Exception e) {
            System.out.println("✗ Unexpected error with non-conflicting schedules:");
            System.out.println("  Error: " + e.getMessage());
            fail("Non-conflicting schedules should save successfully");
        }
    }

    /**
     * Helper method to create a test schedule DTO
     */
    private ScreeningScheduleDto createTestSchedule(Integer movieId, Integer screeningRoomId, Integer branchId,
                                                   LocalDate date, LocalTime startTime, LocalTime endTime, BigDecimal price) {
        ScreeningScheduleDto schedule = new ScreeningScheduleDto();
        schedule.setMovieId(movieId);
        schedule.setScreeningRoomId(screeningRoomId);
        schedule.setBranchId(branchId);
        schedule.setScreeningDate(date);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setStatus("ACTIVE");
        schedule.setPrice(price);
        return schedule;
    }
}
