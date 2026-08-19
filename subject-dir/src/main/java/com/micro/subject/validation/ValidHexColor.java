package com.micro.subject.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a string is a CSS-style hex color: #RGB, #RRGGBB, or
 * #RRGGBBAA. Matches the "#4CAF50" style used throughout the Flutter app.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HexColorValidator.class)
public @interface ValidHexColor {
    String message() default "color must be a valid hex color (e.g. #4CAF50)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
