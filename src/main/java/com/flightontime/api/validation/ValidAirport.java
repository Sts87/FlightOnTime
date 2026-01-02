package com.flightontime.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación para validar que un código de aeropuerto sea válido.
 * Verifica que el código esté presente en la lista de códigos IATA permitidos.
 */
@Documented
@Constraint(validatedBy = ValidAirportValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAirport {
    String message() default "{com.flightontime.api.validation.ValidAirport.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}