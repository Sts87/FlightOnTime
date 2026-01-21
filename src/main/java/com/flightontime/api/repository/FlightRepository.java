package com.flightontime.api.repository;

import com.flightontime.api.model.Flight;
import com.flightontime.api.model.FlightStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Cuenta el total de consultas de vuelos en un rango de fechas.
     */
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.fechaDePartida BETWEEN :startDate AND :endDate")
    Long countByFechaDePartidaBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Cuenta las consultas de vuelos por estado en un rango de fechas.
     */
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.estado = :status AND f.fechaDePartida BETWEEN :startDate AND :endDate")
    Long countByEstadoAndFechaDePartidaBetween(
            @Param("status") FlightStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Calcula la probabilidad promedio de consultas de vuelos en un rango de fechas.
     */
    @Query("SELECT AVG(f.probabilidad) FROM Flight f WHERE f.fechaDePartida BETWEEN :startDate AND :endDate")
    Double averageProbabilidadByFechaDePartidaBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Obtiene todas las aerolíneas distintas.
     */
    @Query("SELECT DISTINCT f.aerolinea FROM Flight f ORDER BY f.aerolinea")
    List<String> findDistinctAirlines();

    /**
     * Cuenta vuelos totales por aerolínea.
     */
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.aerolinea = :airline")
    Long countByAerolinea(@Param("airline") String airline);

    /**
     * Cuenta vuelos retrasados por aerolínea.
     */
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.aerolinea = :airline AND f.estado = 'Retrasado'")
    Long countDelayedByAerolinea(@Param("airline") String airline);

    /**
     * Obtiene los últimos N vuelos ordenados por ID descendente (las consultas más recientes).
     */
    @Query("SELECT f FROM Flight f ORDER BY f.id DESC")
    List<Flight> findRecentPredictions(Pageable pageable);
}

