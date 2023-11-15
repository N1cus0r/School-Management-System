package com.example.schoolmanagementsystem.util.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Pattern(regexp = ".*\\S.*", message = "must not be empty")
@Constraint(validatedBy = {})
@ReportAsSingleViolation
public @interface NullableNotBlank {
    String message() default "must be null or not empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
