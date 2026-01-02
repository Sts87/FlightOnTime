package com.flightontime.api.validation;

import java.util.Set;

/**
 * Constantes utilizadas para validaciones en la aplicación.
 * Contiene listas de códigos válidos y patrones de validación.
 */
public final class ValidationConstants {

    private ValidationConstants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no debe ser instanciada");
    }

    /**
     * Códigos IATA válidos de aerolíneas.
     * Usando Set para búsquedas más eficientes O(1).
     */
    public static final Set<String> VALID_AIRLINE_CODES = Set.of(
            "AS", "AA", "AC", "AM", "CO", "DL", "FX", "HA",
            "NW", "PO", "SW", "UA", "5X", "VS", "VB", "WS"
    );

    /**
     * Códigos IATA válidos de aeropuertos (3 caracteres).
     * Usando Set para búsquedas más eficientes O(1).
     */
    public static final Set<String> VALID_AIRPORT_CODES = Set.of(
            "ATL", "AUS", "BNA", "BOS", "BWI", "CLT", "DAL", "DCA", "DEN", "DFW",
            "DTW", "EWR", "FLL", "HNL", "HOU", "IAD", "IAH", "JFK", "LAS", "LAX",
            "LGA", "MCO", "MDW", "MIA", "MSP", "MSY", "OAK", "ORD", "PDX", "PHL",
            "PHX", "RDU", "SAN", "SEA", "SFO", "SJC", "SLC", "SMF", "STL", "TPA", "ANC"
    );

    /**
     * Valor mínimo requerido para la distancia de vuelo en millas.
     */
    public static final int MIN_DISTANCE_REQUIRED = 200;

    /**
     * Patrón Regex para códigos de aerolínea de 2 a 3 caracteres alfanuméricos.
     * Valida el formato antes de verificar contra la lista de códigos válidos.
     */
    public static final String AIRLINE_CODE_PATTERN = "^[A-Z0-9]{2,3}$";

    /**
     * Patrón Regex para códigos de aeropuerto de 3 letras mayúsculas.
     * Valida el formato antes de verificar contra la lista de códigos válidos.
     */
    public static final String AIRPORT_CODE_PATTERN = "^[A-Z]{3}$";
}
