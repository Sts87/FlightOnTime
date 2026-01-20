package com.flightontime.api.dto;

import java.time.LocalDate;

/**
 * DTO para estadísticas de vuelos.
 */
public record FlightStatsDTO(
        LocalDate fecha,
        Long totalVuelos,
        Long vuelosRetrasados,
        Long vuelosPuntuales,
        Double porcentajeRetrasados,
        Double porcentajePuntuales,
        Double probabilidadPromedio
) {
    /**
     * Constructor de conveniencia para crear estadísticas con cálculos automáticos.
     */
    public FlightStatsDTO(LocalDate fecha, Long totalVuelos, Long vuelosRetrasados, Double probabilidadPromedio) {
        this(
                fecha,
                totalVuelos,
                vuelosRetrasados,
                totalVuelos - vuelosRetrasados,
                totalVuelos > 0 ? (vuelosRetrasados * 100.0 / totalVuelos) : 0.0,
                totalVuelos > 0 ? ((totalVuelos - vuelosRetrasados) * 100.0 / totalVuelos) : 0.0,
                probabilidadPromedio
        );
    }
}
