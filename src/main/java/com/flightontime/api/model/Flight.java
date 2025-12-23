package com.flightontime.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    private String aerolinea;
    private String origen;
    private String destino;
    private LocalDateTime fechaDePartida;
    private Integer distancia;
    private FlightStatus estado;
}
