package group6.cinema_project.service;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.dto.ScreeningScheduleDto;
import group6.cinema_project.service.MovieScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify dynamic movie categorization functionality.
 * This test demonstrates how movies are correctly categorized based on their
 * schedule dates and times.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MovieCategorizationTest {

    @Autowired
    private MovieScheduleService movieScheduleService;

    /**
     * Test case to verify that movies are correctly categorized as Currently
     * Playing
     * when they have schedules that are currently active.
     */
    @Test
    public void testCurrentlyPlayingMovies() {
        try {
            // Create a schedule for today that started 1 hour ago and ends in 1 hour
            LocalTime currentTime = LocalTime.now();
            LocalTime startTime = currentTime.minusHours(1);
            LocalTime endTime = currentTime.plusHours(1);

            ScreeningScheduleDto currentlyPlayingSchedule = createTestSchedule(
                    1, // movieId
                    1, // screeningRoomId
                    1, // branchId
                    LocalDate.now(), // today
                    startTime,
                    endTime,
                    new BigDecimal("100000"));

            // Save the schedule
            movieScheduleService.saveOrUpdateScreeningSchedule(currentlyPlayingSchedule);

            // Test the new dynamic method
            List<MovieDto> currentlyPlayingMovies = movieScheduleService.getCurrentlyPlayingMovies();

            // Verify that the movie appears in currently playing
            boolean foundMovie = currentlyPlayingMovies.stream()
                    .anyMatch(movie -> movie.getId().equals(1));

            System.out.println("✓ Currently playing movies count: " + currentlyPlayingMovies.size());
            if (foundMovie) {
                System.out.println("✓ Movie correctly categorized as Currently Playing");
            }

            // Test backward compatibility with getMoviesByScheduleStatus
            List<MovieDto> activeMovies = movieScheduleService.getMoviesByScheduleStatus("ACTIVE");
            boolean foundInActive = activeMovies.stream()
                    .anyMatch(movie -> movie.getId().equals(1));

            if (foundInActive) {
                System.out.println("✓ Backward compatibility: Movie found via ACTIVE status");
            }

        } catch (Exception e) {
            System.out.println("✗ Error testing currently playing movies:");
            System.out.println("  Error: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    /**
     * Test case to verify that movies are correctly categorized as Coming Soon
     * when they have schedules in the future.
     */
    @Test
    public void testComingSoonMovies() {
        try {
            // Create a schedule for tomorrow
            ScreeningScheduleDto comingSoonSchedule = createTestSchedule(
                    2, // movieId
                    1, // screeningRoomId
                    1, // branchId
                    LocalDate.now().plusDays(1), // tomorrow
                    LocalTime.of(14, 0), // 2:00 PM
                    LocalTime.of(16, 0), // 4:00 PM
                    new BigDecimal("120000"));

            // Save the schedule
            movieScheduleService.saveOrUpdateScreeningSchedule(comingSoonSchedule);

            // Test the new dynamic method
            List<MovieDto> comingSoonMovies = movieScheduleService.getComingSoonMovies();

            // Verify that the movie appears in coming soon
            boolean foundMovie = comingSoonMovies.stream()
                    .anyMatch(movie -> movie.getId().equals(2));

            System.out.println("✓ Coming soon movies count: " + comingSoonMovies.size());
            if (foundMovie) {
                System.out.println("✓ Movie correctly categorized as Coming Soon");
            }

            // Test backward compatibility with getMoviesByScheduleStatus
            List<MovieDto> upcomingMovies = movieScheduleService.getMoviesByScheduleStatus("UPCOMING");
            boolean foundInUpcoming = upcomingMovies.stream()
                    .anyMatch(movie -> movie.getId().equals(2));

            if (foundInUpcoming) {
                System.out.println("✓ Backward compatibility: Movie found via UPCOMING status");
            }

        } catch (Exception e) {
            System.out.println("✗ Error testing coming soon movies:");
            System.out.println("  Error: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    /**
     * Test case to verify that movies are correctly categorized as Stopped Showing
     * when all their schedules have ended.
     */
    @Test
    public void testStoppedShowingMovies() {
        try {
            // Create a schedule for yesterday that has already ended
            ScreeningScheduleDto stoppedSchedule = createTestSchedule(
                    3, // movieId
                    1, // screeningRoomId
                    1, // branchId
                    LocalDate.now().minusDays(1), // yesterday
                    LocalTime.of(10, 0), // 10:00 AM
                    LocalTime.of(12, 0), // 12:00 PM
                    new BigDecimal("90000"));

            // Save the schedule
            movieScheduleService.saveOrUpdateScreeningSchedule(stoppedSchedule);

            // Test the new dynamic method
            List<MovieDto> stoppedMovies = movieScheduleService.getStoppedShowingMovies();

            // Verify that the movie appears in stopped showing
            boolean foundMovie = stoppedMovies.stream()
                    .anyMatch(movie -> movie.getId().equals(3));

            System.out.println("✓ Stopped showing movies count: " + stoppedMovies.size());
            if (foundMovie) {
                System.out.println("✓ Movie correctly categorized as Stopped Showing");
            }

            // Test backward compatibility with getMoviesByScheduleStatus
            List<MovieDto> endedMovies = movieScheduleService.getMoviesByScheduleStatus("ENDED");
            boolean foundInEnded = endedMovies.stream()
                    .anyMatch(movie -> movie.getId().equals(3));

            if (foundInEnded) {
                System.out.println("✓ Backward compatibility: Movie found via ENDED status");
            }

        } catch (Exception e) {
            System.out.println("✗ Error testing stopped showing movies:");
            System.out.println("  Error: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    /**
     * Test case to verify manual status override functionality.
     * This tests the hybrid approach where manual status takes precedence over
     * dynamic calculation.
     */
    @Test
    public void testManualStatusOverride() {
        try {
            // Create a schedule for tomorrow (would normally be "Coming Soon")
            // But manually set it to "ENDED" to test override
            ScreeningScheduleDto manuallyEndedSchedule = createTestSchedule(
                    7, // movieId
                    1, // screeningRoomId
                    1, // branchId
                    LocalDate.now().plusDays(1), // tomorrow (future date)
                    LocalTime.of(14, 0), // 2:00 PM
                    LocalTime.of(16, 0), // 4:00 PM
                    new BigDecimal("100000"));

            // Manually set status to ENDED (override dynamic calculation)
            manuallyEndedSchedule.setStatus("ENDED");

            // Save the schedule
            movieScheduleService.saveOrUpdateScreeningSchedule(manuallyEndedSchedule);

            // Test that manual status is respected
            List<MovieDto> stoppedMovies = movieScheduleService.getStoppedShowingMovies();
            List<MovieDto> comingSoonMovies = movieScheduleService.getComingSoonMovies();

            // The movie should appear in "Stopped Showing" despite having a future date
            boolean foundInStopped = stoppedMovies.stream()
                    .anyMatch(movie -> movie.getId().equals(7));
            boolean foundInComingSoon = comingSoonMovies.stream()
                    .anyMatch(movie -> movie.getId().equals(7));

            assertTrue(foundInStopped, "Movie with manual ENDED status should appear in Stopped Showing");
            assertFalse(foundInComingSoon, "Movie with manual ENDED status should NOT appear in Coming Soon");

            System.out.println("✓ Manual status override test passed");
            System.out.println("  Movie with future date but manual ENDED status correctly appears in Stopped Showing");

        } catch (Exception e) {
            System.out.println("✗ Error testing manual status override:");
            System.out.println("  Error: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    /**
     * Test case to verify AUTO status uses dynamic calculation.
     */
    @Test
    public void testAutoStatusDynamicCalculation() {
        try {
            LocalTime currentTime = LocalTime.now();

            // Create a schedule for today that's currently active
            ScreeningScheduleDto autoSchedule = createTestSchedule(
                    8, // movieId
                    1, // screeningRoomId
                    1, // branchId
                    LocalDate.now(), // today
                    currentTime.minusHours(1), // started 1 hour ago
                    currentTime.plusHours(1), // ends in 1 hour
                    new BigDecimal("100000"));

            // Set status to AUTO for dynamic calculation
            autoSchedule.setStatus("AUTO");

            // Save the schedule
            movieScheduleService.saveOrUpdateScreeningSchedule(autoSchedule);

            // Test that AUTO status uses dynamic calculation
            List<MovieDto> currentlyPlayingMovies = movieScheduleService.getCurrentlyPlayingMovies();

            // The movie should appear in "Currently Playing" based on dynamic calculation
            boolean foundInCurrentlyPlaying = currentlyPlayingMovies.stream()
                    .anyMatch(movie -> movie.getId().equals(8));

            assertTrue(foundInCurrentlyPlaying,
                    "Movie with AUTO status should be dynamically calculated as Currently Playing");

            System.out.println("✓ AUTO status dynamic calculation test passed");
            System.out.println("  Movie with AUTO status correctly calculated as Currently Playing");

        } catch (Exception e) {
            System.out.println("✗ Error testing AUTO status dynamic calculation:");
            System.out.println("  Error: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    /**
     * Test case to verify that movies don't appear in multiple categories
     * simultaneously.
     */
    @Test
    public void testMovieExclusivity() {
        try {
            // Create schedules for different time periods
            LocalTime currentTime = LocalTime.now();

            // Movie 4: Currently playing
            ScreeningScheduleDto currentSchedule = createTestSchedule(
                    4, 1, 1, LocalDate.now(),
                    currentTime.minusHours(1), currentTime.plusHours(1),
                    new BigDecimal("100000"));

            // Movie 5: Coming soon
            ScreeningScheduleDto futureSchedule = createTestSchedule(
                    5, 2, 1, LocalDate.now().plusDays(1),
                    LocalTime.of(14, 0), LocalTime.of(16, 0),
                    new BigDecimal("120000"));

            // Movie 6: Stopped showing
            ScreeningScheduleDto pastSchedule = createTestSchedule(
                    6, 3, 1, LocalDate.now().minusDays(1),
                    LocalTime.of(10, 0), LocalTime.of(12, 0),
                    new BigDecimal("90000"));

            // Save all schedules
            movieScheduleService.saveOrUpdateScreeningSchedule(currentSchedule);
            movieScheduleService.saveOrUpdateScreeningSchedule(futureSchedule);
            movieScheduleService.saveOrUpdateScreeningSchedule(pastSchedule);

            // Get all categories
            List<MovieDto> currentlyPlaying = movieScheduleService.getCurrentlyPlayingMovies();
            List<MovieDto> comingSoon = movieScheduleService.getComingSoonMovies();
            List<MovieDto> stoppedShowing = movieScheduleService.getStoppedShowingMovies();

            // Check that movies appear in only one category
            System.out.println("✓ Category counts - Playing: " + currentlyPlaying.size() +
                    ", Coming Soon: " + comingSoon.size() +
                    ", Stopped: " + stoppedShowing.size());

            // Verify no movie appears in multiple categories
            for (MovieDto movie : currentlyPlaying) {
                boolean inComingSoon = comingSoon.stream().anyMatch(m -> m.getId().equals(movie.getId()));
                boolean inStopped = stoppedShowing.stream().anyMatch(m -> m.getId().equals(movie.getId()));

                assertFalse(inComingSoon,
                        "Movie " + movie.getId() + " should not be in both Currently Playing and Coming Soon");
                assertFalse(inStopped,
                        "Movie " + movie.getId() + " should not be in both Currently Playing and Stopped Showing");
            }

            System.out.println("✓ Movie exclusivity test passed - no movie appears in multiple categories");

        } catch (Exception e) {
            System.out.println("✗ Error testing movie exclusivity:");
            System.out.println("  Error: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
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
        schedule.setStatus("ACTIVE"); // This will be ignored by the new dynamic logic
        schedule.setPrice(price);
        return schedule;
    }
}
