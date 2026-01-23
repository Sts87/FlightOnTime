package com.flightontime.api.dto;

/**
 * DTO para estadísticas por aerolínea.
 */
public record AirlineStatsDTO(
        String aerolinea,
        Long totalVuelos,
        Long vuelosRetrasados,
        Long vuelosPuntuales,
        Double porcentajeRetrasados,
        Double porcentajePuntuales
) {
    /**
     * Constructor que calcula los porcentajes automáticamente.
     */
    public AirlineStatsDTO(String aerolinea, Long totalVuelos, Long vuelosRetrasados) {
        this(
                aerolinea,
                totalVuelos,
                vuelosRetrasados,
                totalVuelos - vuelosRetrasados,
                totalVuelos > 0 ? (vuelosRetrasados * 100.0 / totalVuelos) : 0.0,
                totalVuelos > 0 ? ((totalVuelos - vuelosRetrasados) * 100.0 / totalVuelos) : 0.0
        );
    }
}