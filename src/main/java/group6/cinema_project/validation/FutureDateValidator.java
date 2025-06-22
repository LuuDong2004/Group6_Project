package group6.cinema_project.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * Validator implementation for the @FutureDate annotation.
 * Validates that a LocalDate is not in the past.
 */
public class FutureDateValidator implements ConstraintValidator<FutureDate, LocalDate> {
    
    private boolean allowToday;
    
    @Override
    public void initialize(FutureDate constraintAnnotation) {
        this.allowToday = constraintAnnotation.allowToday();
    }
    
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        // Null values are considered valid (use @NotNull for null checks)
        if (value == null) {
            return true;
        }
        
        LocalDate today = LocalDate.now();
        
        if (allowToday) {
            // Allow today and future dates
            return !value.isBefore(today);
        } else {
            // Only allow future dates (not today)
            return value.isAfter(today);
        }
    }
}
