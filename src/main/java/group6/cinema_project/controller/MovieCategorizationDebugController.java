package group6.cinema_project.controller;

import group6.cinema_project.service.MovieScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * Debug controller for movie categorization system.
 * This controller provides endpoints to help diagnose and fix categorization issues.
 */
@Controller
@RequestMapping("/admin/debug/categorization")
@RequiredArgsConstructor
@Slf4j
public class MovieCategorizationDebugController {

    private final MovieScheduleService movieScheduleService;

    /**
     * Display the debug information page
     */
    @GetMapping
    public String showDebugPage(Model model) {
        log.info("Loading movie categorization debug page");
        
        try {
            Map<String, Object> debugInfo = movieScheduleService.getCategorizationDebugInfo();
            model.addAttribute("debugInfo", debugInfo);
            
            log.info("Successfully loaded debug information");
            return "admin/admin_categorization_debug";
            
        } catch (Exception e) {
            log.error("Error loading debug information", e);
            model.addAttribute("error", "Error loading debug information: " + e.getMessage());
            return "admin/admin_categorization_debug";
        }
    }

    /**
     * Get debug information as JSON for AJAX requests
     */
    @GetMapping("/json")
    @ResponseBody
    public Map<String, Object> getDebugInfoJson() {
        log.info("Fetching categorization debug info as JSON");
        
        try {
            Map<String, Object> debugInfo = movieScheduleService.getCategorizationDebugInfo();
            log.info("Successfully generated debug info JSON");
            return debugInfo;
            
        } catch (Exception e) {
            log.error("Error generating debug info JSON", e);
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Error generating debug info: " + e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            return errorResponse;
        }
    }

    /**
     * Update null statuses to AUTO for dynamic calculation
     */
    @PostMapping("/update-null-statuses")
    public String updateNullStatuses(RedirectAttributes redirectAttributes) {
        log.info("Updating null statuses to AUTO");
        
        try {
            int updatedCount = movieScheduleService.updateNullStatusesToAuto();
            
            if (updatedCount > 0) {
                redirectAttributes.addFlashAttribute("success", 
                    "Successfully updated " + updatedCount + " schedules from null status to AUTO. " +
                    "These schedules will now use dynamic date-based categorization.");
                log.info("Successfully updated {} schedules to AUTO status", updatedCount);
            } else {
                redirectAttributes.addFlashAttribute("info", 
                    "No schedules needed updating. All schedules already have status values.");
                log.info("No schedules needed updating - all have status values");
            }
            
        } catch (Exception e) {
            log.error("Error updating null statuses", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error updating statuses: " + e.getMessage());
        }
        
        return "redirect:/admin/debug/categorization";
    }

    /**
     * Test the categorization logic with current data
     */
    @GetMapping("/test")
    @ResponseBody
    public Map<String, Object> testCategorization() {
        log.info("Testing categorization logic");
        
        Map<String, Object> testResults = new java.util.HashMap<>();
        
        try {
            // Test each category method
            testResults.put("currentlyPlaying", movieScheduleService.getCurrentlyPlayingMovies().size());
            testResults.put("comingSoon", movieScheduleService.getComingSoonMovies().size());
            testResults.put("stoppedShowing", movieScheduleService.getStoppedShowingMovies().size());
            
            // Test backward compatibility
            testResults.put("activeStatus", movieScheduleService.getMoviesByScheduleStatus("ACTIVE").size());
            testResults.put("upcomingStatus", movieScheduleService.getMoviesByScheduleStatus("UPCOMING").size());
            testResults.put("endedStatus", movieScheduleService.getMoviesByScheduleStatus("ENDED").size());
            
            // Check consistency
            boolean consistent = 
                testResults.get("currentlyPlaying").equals(testResults.get("activeStatus")) &&
                testResults.get("comingSoon").equals(testResults.get("upcomingStatus")) &&
                testResults.get("stoppedShowing").equals(testResults.get("endedStatus"));
            
            testResults.put("backwardCompatible", consistent);
            testResults.put("timestamp", java.time.LocalDateTime.now().toString());
            testResults.put("status", "success");
            
            log.info("Categorization test completed successfully");
            
        } catch (Exception e) {
            log.error("Error testing categorization", e);
            testResults.put("error", "Error testing categorization: " + e.getMessage());
            testResults.put("status", "error");
        }
        
        return testResults;
    }

    /**
     * Get a simple status summary for quick checking
     */
    @GetMapping("/summary")
    @ResponseBody
    public Map<String, Object> getStatusSummary() {
        Map<String, Object> summary = new java.util.HashMap<>();
        
        try {
            summary.put("currentlyPlaying", movieScheduleService.getCurrentlyPlayingMovies().size());
            summary.put("comingSoon", movieScheduleService.getComingSoonMovies().size());
            summary.put("stoppedShowing", movieScheduleService.getStoppedShowingMovies().size());
            summary.put("timestamp", java.time.LocalDateTime.now().toString());
            
        } catch (Exception e) {
            summary.put("error", e.getMessage());
        }
        
        return summary;
    }
}
