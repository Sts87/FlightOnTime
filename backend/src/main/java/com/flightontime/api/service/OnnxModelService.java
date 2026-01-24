package com.flightontime.api.service;

import ai.onnxruntime.*;
import com.flightontime.api.exception.ModelLoadException;
import com.flightontime.api.exception.PredictionException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Servicio optimizado para cargar el modelo ONNX desde el JAR (Railway compatible).
 */
@Service
public class OnnxModelService {

    @Value("${model.path:models/flight_delay_model_v2.onnx}")
    private String modelPath;

    private OrtEnvironment environment;
    private OrtSession session;

    @PostConstruct
    public void init() {
        try {
            environment = OrtEnvironment.getEnvironment();
            
            // LEER EL MODELO COMO BYTES (Solución para JAR/Railway)
            byte[] modelBytes = loadModelBytes();

            System.out.println("✓ Cargando modelo ONNX desde memoria (bytes)...");
            session = environment.createSession(modelBytes, new OrtSession.SessionOptions());
            System.out.println("✓ Modelo ONNX cargado exitosamente");

            printModelInfo();

        } catch (OrtException | IOException e) {
            throw new ModelLoadException("Error crítico al cargar el modelo ONNX: " + e.getMessage(), e);
        }
    }

    private byte[] loadModelBytes() throws IOException {
        // Limpiamos la ruta de posibles prefijos
        String cleanPath = modelPath.replace("classpath:", "");
        if (cleanPath.startsWith("/")) cleanPath = cleanPath.substring(1);

        System.out.println("Buscando recurso en: " + cleanPath);
        
        // Usamos el ClassLoader para leer dentro del JAR
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(cleanPath)) {
            if (is == null) {
                throw new IOException("No se encontró el archivo .onnx en la ruta: " + cleanPath + 
                    ". Verifica que esté en src/main/resources/" + cleanPath);
            }
            return is.readAllBytes();
        }
    }

    public Map<String, Object> predict(Map<String, OnnxTensor> features) {
        try (OrtSession.Result result = session.run(features)) {
            // 1. Label
            long[] labels = (long[]) result.get("output_label").get().getValue();
            
            // 2. Probabilidades
            Object probabilityOutput = result.get("output_probability").get().getValue();
            double probabilityDelayed = 0.0;

            if (probabilityOutput instanceof List<?> probList && !probList.isEmpty()) {
                Object firstElement = probList.get(0);
                if (firstElement instanceof OnnxMap onnxMap) {
                    Map<Long, Float> mapValores = (Map<Long, Float>) onnxMap.getValue();
                    Float prob1 = mapValores.get(1L); // Clase 1 = Retrasado
                    if (prob1 != null) probabilityDelayed = prob1.doubleValue();
                }
            }

            return Map.of(
                "probability", probabilityDelayed,
                "label", labels[0]
            );
        } catch (OrtException e) {
            throw new PredictionException("Error en la ejecución del modelo: " + e.getMessage());
        }
    }

    private void printModelInfo() {
        try {
            System.out.println("=== INFO DEL MODELO ===");
            System.out.println("Inputs: " + session.getInputNames());
            System.out.println("Outputs: " + session.getOutputNames());
            System.out.println("=======================");
        } catch (Exception e) {
            System.out.println("No se pudo imprimir la info del modelo.");
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (session != null) session.close();
            if (environment != null) environment.close();
        } catch (OrtException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }

    // EL MÉTODO QUE FALTABA:
    public OrtEnvironment getEnvironment() {
        return environment;
    }
}
