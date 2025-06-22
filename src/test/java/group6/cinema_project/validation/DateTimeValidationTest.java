package group6.cinema_project.validation;

import group6.cinema_project.dto.ScreeningScheduleDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for date and time validation annotations
 */
public class DateTimeValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testFutureDateValidation_PastDate_ShouldFail() {
        ScreeningScheduleDto dto = createValidDto();
        dto.setScreeningDate(LocalDate.now().minusDays(1)); // Yesterday

        Set<ConstraintViolation<ScreeningScheduleDto>> violations = validator.validate(dto);
        
        boolean hasDateViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("quá khứ"));
        
        assertTrue(hasDateViolation, "Should have validation error for past date");
    }

    @Test
    void testFutureDateValidation_TodayDate_ShouldPass() {
        ScreeningScheduleDto dto = createValidDto();
        dto.setScreeningDate(LocalDate.now()); // Today
        dto.setStartTime(LocalTime.now().plusHours(1)); // Future time

        Set<ConstraintViolation<ScreeningScheduleDto>> violations = validator.validate(dto);
        
        boolean hasDateViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("quá khứ"));
        
        assertFalse(hasDateViolation, "Should not have validation error for today's date with future time");
    }

    @Test
    void testFutureDateValidation_FutureDate_ShouldPass() {
        ScreeningScheduleDto dto = createValidDto();
        dto.setScreeningDate(LocalDate.now().plusDays(1)); // Tomorrow

        Set<ConstraintViolation<ScreeningScheduleDto>> violations = validator.validate(dto);
        
        boolean hasDateViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("quá khứ"));
        
        assertFalse(hasDateViolation, "Should not have validation error for future date");
    }

    @Test
    void testFutureDateTimeValidation_PastTime_ShouldFail() {
        ScreeningScheduleDto dto = createValidDto();
        dto.setScreeningDate(LocalDate.now()); // Today
        dto.setStartTime(LocalTime.now().minusHours(1)); // Past time

        Set<ConstraintViolation<ScreeningScheduleDto>> violations = validator.validate(dto);
        
        boolean hasTimeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("quá khứ"));
        
        assertTrue(hasTimeViolation, "Should have validation error for past time on current date");
    }

    private ScreeningScheduleDto createValidDto() {
        ScreeningScheduleDto dto = new ScreeningScheduleDto();
        dto.setMovieId(1);
        dto.setScreeningRoomId(1);
        dto.setBranchId(1);
        dto.setScreeningDate(LocalDate.now().plusDays(1));
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(16, 0));
        dto.setStatus("ACTIVE");
        dto.setPrice(new BigDecimal("80000"));
        return dto;
    }
}
