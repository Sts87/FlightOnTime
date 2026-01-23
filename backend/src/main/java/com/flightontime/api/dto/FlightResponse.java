package com.flightontime.api.dto;

import com.flightontime.api.model.Flight;
import com.flightontime.api.model.FlightStatus;

import java.time.LocalDateTime;

public record FlightResponse(
        Long id,
        String aerolinea,
        String origen,
        String destino,
        LocalDateTime fechaDePartida,
        Integer distancia,
        FlightStatus estado,
        Double probabilidad
) {
    public FlightResponse(Flight flight) {
        this(
             flight.getId(),
             flight.getAerolinea(),
             flight.getOrigen(),
             flight.getDestino(),
             flight.getFechaDePartida(),
             flight.getDistancia(),
             flight.getEstado(),
             flight.getProbabilidad()
        );
    }
}
