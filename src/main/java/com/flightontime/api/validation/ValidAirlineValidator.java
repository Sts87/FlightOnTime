package com.flightontime.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Validador para códigos de aerolíneas IATA.
 * Verifica que el código proporcionado esté en la lista de códigos válidos.
 */
@Component
public class ValidAirlineValidator implements ConstraintValidator<ValidAirline, String> {

    @Override
    public void initialize(ValidAirline constraintAnnotation) {
        // Inicialización si es necesaria en el futuro
    }

    @Override
    public boolean isValid(String code, ConstraintValidatorContext context) {
        // null es válido - se debe manejar con @NotBlank si es requerido
        if (code == null) {
            return true;
        }

        return ValidationConstants.VALID_AIRLINE_CODES.contains(code);
    }
}
