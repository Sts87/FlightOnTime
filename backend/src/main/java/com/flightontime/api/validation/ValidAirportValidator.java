package com.flightontime.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Validador para códigos de aeropuertos IATA.
 * Verifica que el código proporcionado esté en la lista de códigos válidos.
 */
@Component
public class ValidAirportValidator implements ConstraintValidator<ValidAirport, String> {

    @Override
    public void initialize(ValidAirport constraintAnnotation) {
        // Inicialización si es necesaria en el futuro
    }

    @Override
    public boolean isValid(String code, ConstraintValidatorContext context) {
        // null es válido - se debe manejar con @NotBlank si es requerido
        if (code == null) {
            return true;
        }

        boolean isValid = ValidationConstants.VALID_AIRPORT_CODES.contains(code);

        // Si es inválido, construir mensaje personalizado con el valor
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("El código de aeropuerto '%s' no es válido o no está en la lista de aeropuertos permitidos. Por favor, verifica el código IATA.",
                            code)
            ).addConstraintViolation();
        }

        return isValid;
    }
}