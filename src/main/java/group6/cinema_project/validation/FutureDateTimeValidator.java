package group6.cinema_project.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Validator implementation for the @FutureDateTime annotation.
 * Validates that a combination of date and time fields is not in the past.
 */
public class FutureDateTimeValidator implements ConstraintValidator<FutureDateTime, Object> {
    
    private String dateField;
    private String timeField;
    private boolean allowCurrentTime;
    
    @Override
    public void initialize(FutureDateTime constraintAnnotation) {
        this.dateField = constraintAnnotation.dateField();
        this.timeField = constraintAnnotation.timeField();
        this.allowCurrentTime = constraintAnnotation.allowCurrentTime();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        
        try {
            // Get the date and time fields using reflection
            Field dateFieldObj = value.getClass().getDeclaredField(dateField);
            Field timeFieldObj = value.getClass().getDeclaredField(timeField);
            
            dateFieldObj.setAccessible(true);
            timeFieldObj.setAccessible(true);
            
            LocalDate date = (LocalDate) dateFieldObj.get(value);
            LocalTime time = (LocalTime) timeFieldObj.get(value);
            
            // If either field is null, let other validators handle it
            if (date == null || time == null) {
                return true;
            }
            
            // Combine date and time
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            LocalDateTime now = LocalDateTime.now();
            
            if (allowCurrentTime) {
                // Allow current time and future times
                return !dateTime.isBefore(now);
            } else {
                // Only allow future times (not current time)
                return dateTime.isAfter(now);
            }
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // If we can't access the fields, consider it valid and let other validation handle it
            return true;
        } catch (ClassCastException e) {
            // If the fields are not of the expected type, consider it invalid
            return false;
        }
    }
}
