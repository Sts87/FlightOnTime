package com.flightontime.api.controller.doc;

import com.flightontime.api.dto.*;
import com.flightontime.api.model.Flight;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Vuelos y Predicciones", description = "API para predicción de retrasos de vuelos y gestión de datos")
public interface FlightApi {

    @Operation(summary = "Predecir retraso de un vuelo", description = "Realiza una predicción individual utilizando ML.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Predicción exitosa",
                    content = @Content(schema = @Schema(implementation = PredictionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    ResponseEntity<PredictionResponse> predict(FlightData datos);

    @Operation(summary = "Predicción por lote (CSV)", description = "Procesa un archivo CSV con múltiples vuelos.")
    ResponseEntity<List<BatchPredictionResultDTO>> predecirBatch(MultipartFile file);

    @Operation(summary = "Estadísticas por fecha", description = "Obtiene estadísticas del día actual o fecha específica.")
    ResponseEntity<FlightStatsDTO> getStats(LocalDate date);

    @Operation(summary = "Estadísticas globales")
    ResponseEntity<FlightStatsDTO> getAllTimeStats();

    @Operation(summary = "Rankings de aerolíneas")
    ResponseEntity<AirlineRankingsDTO> getAirlineRankings();

    @Operation(summary = "Últimas predicciones")
    ResponseEntity<List<RecentPredictionDTO>> getRecentPredictions();

    @Operation(summary = "Listar vuelos", description = "Lista vuelos registrados con paginación.")
    ResponseEntity<Page<Flight>> listar(Pageable pageable);
}