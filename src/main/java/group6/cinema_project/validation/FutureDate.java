package group6.cinema_project.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure a date is not in the past.
 * This annotation validates that the date is today or in the future.
 */
@Documented
@Constraint(validatedBy = FutureDateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDate {
    
    String message() default "Ngày chiếu không được là ngày trong quá khứ";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Whether to allow today's date or only future dates
     * @return true if today is allowed, false if only future dates are allowed
     */
    boolean allowToday() default true;
}
