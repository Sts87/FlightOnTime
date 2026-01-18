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

        boolean isValid = ValidationConstants.VALID_AIRLINE_CODES.contains(code);

        // Si es inválido, construir mensaje personalizado con el valor
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("El código de aerolínea '%s' no es válido. Aerolíneas soportadas: %s",
                            code,
                            String.join(", ", ValidationConstants.VALID_AIRLINE_CODES))
            ).addConstraintViolation();
        }

        return isValid;
    }
}