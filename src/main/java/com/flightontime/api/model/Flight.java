package com.flightontime.api.model;

import com.flightontime.api.dto.FlightData;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "flights")
@Entity(name = "Flight")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aerolinea;

    @Column(nullable = false)
    private String origen;

    @Column(nullable = false)
    private String destino;

    @Column(name = "fecha_de_partida", nullable = false)
    private LocalDateTime fechaDePartida;

    @Column(nullable = false)
    private Integer distancia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus estado;

    public Flight(FlightData datos) {
        this.id = null;
        this.aerolinea = datos.aerolinea();
        this.origen = datos.origen();
        this.destino = datos.destino();
        this.fechaDePartida = datos.fechaDePartida();
        this.distancia = datos.distancia();
        this.estado = datos.estado();
    }
}