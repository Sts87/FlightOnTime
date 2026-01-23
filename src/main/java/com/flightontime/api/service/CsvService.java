package com.flightontime.api.service;

import com.flightontime.api.dto.FlightData;
import com.flightontime.api.exception.CsvParsingException;
import com.flightontime.api.exception.InvalidFileException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio responsable de parsear archivos CSV y convertirlos en objetos FlightData.
 */
@Service
public class CsvService {

    private static final String CSV_DELIMITER = ",";
    private static final int EXPECTED_FIELDS = 5;

    private final Validator validator;

    public CsvService(Validator validator) {
        this.validator = validator;
    }

    /**
     * Parsea un archivo CSV MultipartFile y retorna una lista de FlightData.
     */
    public List<FlightData> parse(MultipartFile file) {
        validateFile(file);

        List<FlightData> flights = new ArrayList<>();
        List<CsvLineError> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            // Omitir cabecera
            String header = reader.readLine();
            if (header == null) {
                throw new CsvParsingException("El archivo CSV está vacío");
            }

            int lineNumber = 1; // Comenzamos en 1 después de la cabecera
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue; // Ignorar líneas vacías
                }

                try {
                    FlightData flightData = parseLine(line, lineNumber);

                    // VALIDAR con Bean Validation
                    Set<ConstraintViolation<FlightData>> violations = validator.validate(flightData);

                    if (!violations.isEmpty()) {
                        // Hay errores de validación
                        String validationErrors = violations.stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .collect(Collectors.joining(", "));

                        errors.add(new CsvLineError(lineNumber, line, validationErrors));
                    } else {
                        // Validación exitosa, agregar a la lista
                        flights.add(flightData);
                    }

                } catch (Exception e) {
                    errors.add(new CsvLineError(lineNumber, line, e.getMessage()));
                }
            }

        } catch (Exception e) {
            throw new CsvParsingException(
                    "Error al procesar el archivo CSV: " + e.getMessage(), e
            );
        }

        // Si hubo errores, lanzar excepción con detalles
        if (!errors.isEmpty()) {
            throw new CsvParsingException(
                    "Se encontraron " + errors.size() + " errores en el archivo CSV",
                    errors
            );
        }

        // Si no se parseó ningún vuelo válido
        if (flights.isEmpty()) {
            throw new CsvParsingException(
                    "El archivo CSV no contiene datos válidos"
            );
        }

        return flights;
    }

    /**
     * Valida que el archivo sea válido antes de procesarlo.
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("El archivo está vacío o no se ha proporcionado");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("text/csv") &&
                        !contentType.equals("application/vnd.ms-excel") &&
                        !contentType.equals("application/csv"))) {
            throw new InvalidFileException(
                    "Tipo de archivo no válido. Se esperaba CSV, se recibió: " + contentType
            );
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new InvalidFileException(
                    "El archivo debe tener extensión .csv"
            );
        }
    }

    /**
     * Parsea una línea individual del CSV.
     */
    private FlightData parseLine(String line, int lineNumber) {
        String[] fields = line.split(CSV_DELIMITER, -1); // -1 para incluir campos vacíos

        if (fields.length < EXPECTED_FIELDS) {
            throw new IllegalArgumentException(
                    "Línea incompleta. Se esperaban " + EXPECTED_FIELDS +
                            " campos, se encontraron " + fields.length
            );
        }

        try {
            String aerolinea = parseAndValidateAirline(fields[0]);
            String origen = parseAndValidateAirport(fields[1], "origen");
            String destino = parseAndValidateAirport(fields[2], "destino");
            LocalDateTime fechaDePartida = parseDateTime(fields[3]);
            Integer distancia = parseDistance(fields[4]);

            return new FlightData(aerolinea, origen, destino, fechaDePartida, distancia);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Formato de fecha inválido en campo 'fechaDePartida'. Se esperaba ISO 8601 (YYYY-MM-DDTHH:MM:SS), se recibió: '" + fields[3] + "'"
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Distancia inválida en campo 'distancia'. Debe ser un número entero, se recibió: '" + fields[4] + "'"
            );
        }
    }

    private String parseAndValidateAirline(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("La aerolínea no puede estar vacía");
        }
        return value.trim().toUpperCase();
    }

    private String parseAndValidateAirport(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El " + fieldName + " no puede estar vacío");
        }
        String airport = value.trim().toUpperCase();
        if (airport.length() != 3) {
            throw new IllegalArgumentException(
                    "El código de " + fieldName + " debe tener exactamente 3 caracteres, se recibió: '" + airport + "'"
            );
        }
        return airport;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de partida no puede estar vacía");
        }
        return LocalDateTime.parse(value.trim());
    }

    private Integer parseDistance(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("La distancia no puede estar vacía");
        }

        try {
            int distance = Integer.parseInt(value.trim());
            if (distance < 0) {
                throw new IllegalArgumentException("La distancia no puede ser negativa");
            }
            return distance;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La distancia debe ser un número entero válido");
        }
    }

    /**
     * Clase interna para representar errores de líneas CSV
     */
    public static class CsvLineError {
        private final int lineNumber;
        private final String lineContent;
        private final String error;

        public CsvLineError(int lineNumber, String lineContent, String error) {
            this.lineNumber = lineNumber;
            this.lineContent = lineContent;
            this.error = error;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getLineContent() {
            return lineContent;
        }

        public String getError() {
            return error;
        }

        @Override
        public String toString() {
            return String.format("Línea %d: %s - Contenido: %s",
                    lineNumber, error, lineContent);
        }
    }
}