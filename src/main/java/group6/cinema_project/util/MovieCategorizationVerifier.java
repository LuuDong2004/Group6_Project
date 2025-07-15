package group6.cinema_project.util;

import group6.cinema_project.dto.MovieDto;
import group6.cinema_project.service.IMovieScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility class to verify that the movie categorization logic is working
 * correctly.
 * This can be run as a Spring Boot command line runner to test the
 * implementation.
 */
@Component
@RequiredArgsConstructor
public class MovieCategorizationVerifier implements CommandLineRunner {

    private final IMovieScheduleService movieScheduleService;

    @Override
    public void run(String... args) throws Exception {
        // Only run verification if specifically requested
        if (args.length > 0 && "verify-categorization".equals(args[0])) {
            verifyMovieCategorization();
        }
    }

    public void verifyMovieCategorization() {
        System.out.println("=== Movie Categorization Verification ===");
        System.out.println();

        try {
            // Test the new dynamic methods
            System.out.println("Testing new dynamic categorization methods:");

            List<MovieDto> currentlyPlaying = movieScheduleService.getCurrentlyPlayingMovies();
            List<MovieDto> comingSoon = movieScheduleService.getComingSoonMovies();
            List<MovieDto> stoppedShowing = movieScheduleService.getStoppedShowingMovies();

            System.out.println("✓ Currently Playing Movies: " + currentlyPlaying.size());
            printMovieList(currentlyPlaying, "  ");

            System.out.println("✓ Coming Soon Movies: " + comingSoon.size());
            printMovieList(comingSoon, "  ");

            System.out.println("✓ Stopped Showing Movies: " + stoppedShowing.size());
            printMovieList(stoppedShowing, "  ");

            System.out.println();

            // Test backward compatibility
            System.out.println("Testing backward compatibility with getMoviesByScheduleStatus:");

            List<MovieDto> activeMovies = movieScheduleService.getMoviesByScheduleStatus("ACTIVE");
            List<MovieDto> upcomingMovies = movieScheduleService.getMoviesByScheduleStatus("UPCOMING");
            List<MovieDto> endedMovies = movieScheduleService.getMoviesByScheduleStatus("ENDED");

            System.out.println("✓ ACTIVE status movies: " + activeMovies.size());
            System.out.println("✓ UPCOMING status movies: " + upcomingMovies.size());
            System.out.println("✓ ENDED status movies: " + endedMovies.size());

            System.out.println();

            // Verify exclusivity (no movie appears in multiple categories)
            System.out.println("Verifying category exclusivity:");
            boolean hasOverlap = false;

            // Check for overlaps between categories
            for (MovieDto movie : currentlyPlaying) {
                boolean inComingSoon = comingSoon.stream().anyMatch(m -> m.getId().equals(movie.getId()));
                boolean inStopped = stoppedShowing.stream().anyMatch(m -> m.getId().equals(movie.getId()));

                if (inComingSoon) {
                    System.out.println(
                            "✗ Movie '" + movie.getName() + "' appears in both Currently Playing and Coming Soon");
                    hasOverlap = true;
                }
                if (inStopped) {
                    System.out.println(
                            "✗ Movie '" + movie.getName() + "' appears in both Currently Playing and Stopped Showing");
                    hasOverlap = true;
                }
            }

            for (MovieDto movie : comingSoon) {
                boolean inStopped = stoppedShowing.stream().anyMatch(m -> m.getId().equals(movie.getId()));
                if (inStopped) {
                    System.out.println(
                            "✗ Movie '" + movie.getName() + "' appears in both Coming Soon and Stopped Showing");
                    hasOverlap = true;
                }
            }

            if (!hasOverlap) {
                System.out.println("✓ No overlaps found - movies appear in only one category each");
            }

            System.out.println();

            // Test different status string variations
            System.out.println("Testing status string variations:");
            testStatusVariation("PLAYING");
            testStatusVariation("CURRENTLY_PLAYING");
            testStatusVariation("COMINGSOON");
            testStatusVariation("COMING_SOON");
            testStatusVariation("STOPPED");
            testStatusVariation("STOPPED_SHOWING");

            System.out.println();
            System.out.println("=== Verification Complete ===");

        } catch (Exception e) {
            System.out.println("✗ Error during verification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testStatusVariation(String status) {
        try {
            List<MovieDto> movies = movieScheduleService.getMoviesByScheduleStatus(status);
            System.out.println("✓ Status '" + status + "': " + movies.size() + " movies");
        } catch (Exception e) {
            System.out.println("✗ Status '" + status + "' failed: " + e.getMessage());
        }
    }

    private void printMovieList(List<MovieDto> movies, String indent) {
        if (movies.isEmpty()) {
            System.out.println(indent + "(No movies)");
        } else {
            for (MovieDto movie : movies) {
                System.out.println(indent + "- " + movie.getName() + " (ID: " + movie.getId() + ")");
            }
        }
    }

    /**
     * Manual verification method that can be called from other parts of the
     * application
     */
    public void manualVerification() {
        System.out.println("Manual Movie Categorization Verification");
        System.out.println("Current time: " + java.time.LocalDateTime.now());
        System.out.println();

        try {
            // Get counts for each category
            int currentlyPlayingCount = movieScheduleService.getCurrentlyPlayingMovies().size();
            int comingSoonCount = movieScheduleService.getComingSoonMovies().size();
            int stoppedShowingCount = movieScheduleService.getStoppedShowingMovies().size();

            System.out.println("Movie Category Counts:");
            System.out.println("- Currently Playing: " + currentlyPlayingCount);
            System.out.println("- Coming Soon: " + comingSoonCount);
            System.out.println("- Stopped Showing: " + stoppedShowingCount);
            System.out.println("- Total: " + (currentlyPlayingCount + comingSoonCount + stoppedShowingCount));

            // Test backward compatibility
            int activeCount = movieScheduleService.getMoviesByScheduleStatus("ACTIVE").size();
            int upcomingCount = movieScheduleService.getMoviesByScheduleStatus("UPCOMING").size();
            int endedCount = movieScheduleService.getMoviesByScheduleStatus("ENDED").size();

            System.out.println();
            System.out.println("Backward Compatibility Check:");
            System.out.println("- ACTIVE status: " + activeCount + " (should match Currently Playing: "
                    + currentlyPlayingCount + ")");
            System.out.println(
                    "- UPCOMING status: " + upcomingCount + " (should match Coming Soon: " + comingSoonCount + ")");
            System.out.println(
                    "- ENDED status: " + endedCount + " (should match Stopped Showing: " + stoppedShowingCount + ")");

            boolean backwardCompatible = (activeCount == currentlyPlayingCount) &&
                    (upcomingCount == comingSoonCount) &&
                    (endedCount == stoppedShowingCount);

            System.out.println("Backward compatibility: " + (backwardCompatible ? "✓ PASS" : "✗ FAIL"));

        } catch (Exception e) {
            System.out.println("✗ Verification failed: " + e.getMessage());
        }
    }
}
