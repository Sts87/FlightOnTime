package com.flightontime.api.repository;

import com.flightontime.api.model.Flight;
import com.flightontime.api.model.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Cuenta el total de vuelos en un rango de fechas.
     */
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.fechaDePartida BETWEEN :startDate AND :endDate")
    Long countByFechaDePartidaBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Cuenta los vuelos por estado en un rango de fechas.
     */
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.estado = :status AND f.fechaDePartida BETWEEN :startDate AND :endDate")
    Long countByEstadoAndFechaDePartidaBetween(
            @Param("status") FlightStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Calcula la probabilidad promedio de vuelos en un rango de fechas.
     */
    @Query("SELECT AVG(f.probabilidad) FROM Flight f WHERE f.fechaDePartida BETWEEN :startDate AND :endDate")
    Double averageProbabilidadByFechaDePartidaBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}