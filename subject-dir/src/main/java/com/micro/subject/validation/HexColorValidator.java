package com.micro.subject.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class HexColorValidator implements ConstraintValidator<ValidHexColor, String> {

    private static final Pattern HEX_COLOR_PATTERN =
            Pattern.compile("^#([A-Fa-f0-9]{3}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            // @NotBlank on the field already reports the "required" case;
            // returning true here avoids a duplicate/confusing error message.
            return true;
        }
        return HEX_COLOR_PATTERN.matcher(value.trim()).matches();
    }
}
