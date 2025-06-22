package group6.cinema_project.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure a date and time combination is not in the past.
 * This annotation validates that the date and time is in the future or at least the current time.
 * 
 * This annotation should be placed on a class that has both date and time fields.
 * The field names for date and time can be specified using the dateField and timeField attributes.
 */
@Documented
@Constraint(validatedBy = FutureDateTimeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDateTime {
    
    String message() default "Thời gian chiếu không được là thời gian trong quá khứ";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * The name of the date field in the class
     * @return the field name containing the date
     */
    String dateField() default "screeningDate";
    
    /**
     * The name of the time field in the class
     * @return the field name containing the time
     */
    String timeField() default "startTime";
    
    /**
     * Whether to allow current date and time or only future date and time
     * @return true if current time is allowed, false if only future time is allowed
     */
    boolean allowCurrentTime() default false;
}
