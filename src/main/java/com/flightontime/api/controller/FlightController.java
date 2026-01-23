package com.flightontime.api.controller;

import com.flightontime.api.controller.doc.FlightApi;
import com.flightontime.api.dto.*;
import com.flightontime.api.model.Flight;
import com.flightontime.api.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flights")
@CrossOrigin(origins = "*") // Considera mover esto a una configuración global de WebMvcConfigurer
@RequiredArgsConstructor
public class FlightController implements FlightApi {

    private final FlightService flightService;
    private final PredictionService predictionService;
    private final CsvService csvService;
    private final StatsService statsService;

    @Override
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> predict(@RequestBody @Valid FlightData datos) {
        return ResponseEntity.ok(predictionService.predict(datos));
    }

    @Override
    @PostMapping(value = "/batch/predict", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<BatchPredictionResultDTO>> predecirBatch(@RequestParam("file") MultipartFile file) {
        List<FlightData> vuelos = csvService.parse(file);
        return ResponseEntity.ok(predictionService.processBatchPrediction(vuelos));
    }

    @Override
    @GetMapping("/stats")
    public ResponseEntity<FlightStatsDTO> getStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        FlightStatsDTO stats = (date != null)
                ? statsService.getStatsForDate(date)
                : statsService.getStatsForToday();

        return ResponseEntity.ok(stats);
    }

    @Override
    @GetMapping("/stats/all")
    public ResponseEntity<FlightStatsDTO> getAllTimeStats() {
        return ResponseEntity.ok(statsService.getAllTimeStats());
    }

    @Override
    @GetMapping("/stats/airlines")
    public ResponseEntity<AirlineRankingsDTO> getAirlineRankings() {
        return ResponseEntity.ok(statsService.getAirlineRankings());
    }

    @Override
    @GetMapping("/stats/recent")
    public ResponseEntity<List<RecentPredictionDTO>> getRecentPredictions() {
        return ResponseEntity.ok(statsService.getRecentPredictions());
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<Flight>> listar(@PageableDefault(size = 10, sort = "aerolinea") Pageable pageable) {
        // Spring convierte automáticamente ?page=0&size=10&sort=campo,asc en el objeto Pageable
        return ResponseEntity.ok(flightService.listarTodo(pageable));
    }
}