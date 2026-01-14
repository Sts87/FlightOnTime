package com.flightontime.api.service;

import com.flightontime.api.dto.FlightData;
import com.flightontime.api.exception.CsvParsingException;
import com.flightontime.api.exception.InvalidFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio responsable de parsear archivos CSV y convertirlos en objetos FlightData.
 */
@Service
public class CsvService {

    private static final String CSV_DELIMITER = ",";
    private static final int EXPECTED_FIELDS = 5;

    /**
     * Parsea un archivo CSV MultipartFile y retorna una lista de FlightData.
     */
    public List<FlightData> parse(MultipartFile file) {
        validateFile(file);

        List<FlightData> flights = new ArrayList<>();
        List<String> errors = new ArrayList<>();

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
                    flights.add(flightData);
                } catch (Exception e) {
                    String errorMsg = String.format(
                            "Línea %d: %s - Contenido: %s",
                            lineNumber,
                            e.getMessage(),
                            line
                    );
                    errors.add(errorMsg);
                }
            }

        } catch (Exception e) {
            throw new CsvParsingException(
                    "Error al procesar el archivo CSV: " + e.getMessage(), e
            );
        }

        // Si no se parseó ningún vuelo válido
        if (flights.isEmpty()) {
            String errorDetails = errors.isEmpty()
                    ? "El formato del archivo es incorrecto o no contiene datos válidos."
                    : "Errores encontrados:\n" + String.join("\n", errors);

            throw new CsvParsingException(
                    "No se pudo parsear ningún vuelo válido del CSV. " + errorDetails
            );
        }

        // Log de advertencia si hubo errores pero se parsearon algunos vuelos
        if (!errors.isEmpty()) {
            System.err.println("Se encontraron " + errors.size() +
                    " líneas con errores que fueron omitidas:");
            errors.forEach(System.err::println);
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
                        !contentType.equals("application/vnd.ms-excel"))) {
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
        String[] fields = line.split(CSV_DELIMITER);

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
                    "Formato de fecha inválido. Se esperaba ISO 8601 (YYYY-MM-DDTHH:MM:SS)"
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Distancia inválida. Debe ser un número entero: " + fields[4]
            );
        }
    }

    private String parseAndValidateAirline(String value) {
        String airline = value.trim().toUpperCase();
        if (airline.isEmpty()) {
            throw new IllegalArgumentException("La aerolínea no puede estar vacía");
        }
        return airline;
    }

    private String parseAndValidateAirport(String value, String fieldName) {
        String airport = value.trim().toUpperCase();
        if (airport.isEmpty()) {
            throw new IllegalArgumentException("El " + fieldName + " no puede estar vacío");
        }
        if (airport.length() != 3) {
            throw new IllegalArgumentException(
                    "El código de " + fieldName + " debe tener exactamente 3 caracteres"
            );
        }
        return airport;
    }

    private LocalDateTime parseDateTime(String value) {
        return LocalDateTime.parse(value.trim());
    }

    private Integer parseDistance(String value) {
        int distance = Integer.parseInt(value.trim());
        if (distance < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa");
        }
        return distance;
    }
}
