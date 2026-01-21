package com.flightontime.api.dto;

import java.util.List;

/**
 * DTO para rankings de aerolíneas (top 3 puntuales y top 3 retrasadas).
 */
public record AirlineRankingsDTO(
        List<AirlineStatsDTO> topPuntuales,
        List<AirlineStatsDTO> topRetrasadas
) {
}