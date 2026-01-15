package com.flightontime.api.service;

import ai.onnxruntime.*;
import ai.onnxruntime.OnnxMap;
import com.flightontime.api.exception.ModelLoadException;
import com.flightontime.api.exception.PredictionException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Servicio responsable de cargar y ejecutar el modelo ONNX.
 * Maneja el ciclo de vida del modelo (carga y cierre de recursos).
 */
@Service
public class OnnxModelService {

    @Value("${model.path:models/flight-model.onnx}")
    private String modelPath;

    private OrtEnvironment environment;
    private OrtSession session;

    @PostConstruct
    public void init() {
        try {
            environment = OrtEnvironment.getEnvironment();

            // Intentar cargar desde diferentes ubicaciones
            String absolutePath = loadModel();

            System.out.println("Cargando modelo ONNX desde: " + absolutePath);
            session = environment.createSession(absolutePath, new OrtSession.SessionOptions());
            System.out.println("Modelo ONNX cargado exitosamente");

            // DIAGNÓSTICO: Mostrar información del modelo
            printModelInfo();

        } catch (OrtException e) {
            throw new ModelLoadException("Error al cargar el modelo ONNX: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ModelLoadException("No se pudo encontrar el archivo del modelo: " + e.getMessage(), e);
        }
    }

    private void printModelInfo() {
        try {
            System.out.println("\n========== INFORMACIÓN DEL MODELO ONNX ==========");

            // Inputs
            System.out.println("\n📥 INPUTS:");
            for (Map.Entry<String, NodeInfo> entry : session.getInputInfo().entrySet()) {
                String name = entry.getKey();
                NodeInfo info = entry.getValue();
                System.out.println("  - Nombre: " + name);
                System.out.println("    Tipo: " + info.getInfo());
                if (info.getInfo() instanceof TensorInfo) {
                    TensorInfo tensorInfo = (TensorInfo) info.getInfo();
                    System.out.println("    Shape: " + java.util.Arrays.toString(tensorInfo.getShape()));
                    System.out.println("    Tipo de dato: " + tensorInfo.type);
                }
                System.out.println();
            }

            // Outputs
            System.out.println("\n📤 OUTPUTS:");
            for (Map.Entry<String, NodeInfo> entry : session.getOutputInfo().entrySet()) {
                String name = entry.getKey();
                NodeInfo info = entry.getValue();
                System.out.println("  - Nombre: " + name);
                System.out.println("    Tipo: " + info.getInfo());
                if (info.getInfo() instanceof TensorInfo) {
                    TensorInfo tensorInfo = (TensorInfo) info.getInfo();
                    System.out.println("    Shape: " + java.util.Arrays.toString(tensorInfo.getShape()));
                    System.out.println("    Tipo de dato: " + tensorInfo.type);
                }
                System.out.println();
            }

            System.out.println("================================================\n");

        } catch (Exception e) {
            System.err.println("Error al obtener información del modelo: " + e.getMessage());
        }
    }

    private String loadModel() throws IOException {
        System.out.println("=== Intentando cargar modelo ONNX ===");
        System.out.println("Ruta configurada: " + modelPath);
        System.out.println("Directorio de trabajo: " + System.getProperty("user.dir"));

        // Opción 1: Relativo al directorio de trabajo actual
        Path path = Paths.get(modelPath);
        System.out.println("Probando ruta 1: " + path.toAbsolutePath());
        if (Files.exists(path)) {
            System.out.println("✓ Modelo encontrado en ruta 1");
            return path.toAbsolutePath().toString();
        }

        // Opción 2: Desde el directorio de trabajo del sistema
        Path workingDir = Paths.get(System.getProperty("user.dir"), modelPath);
        System.out.println("Probando ruta 2: " + workingDir.toAbsolutePath());
        if (Files.exists(workingDir)) {
            System.out.println("✓ Modelo encontrado en ruta 2");
            return workingDir.toAbsolutePath().toString();
        }

        // Opción 3: Desde el classpath (resources)
        try {
            var resource = getClass().getClassLoader().getResource(modelPath);
            if (resource != null) {
                Path resourcePath = Paths.get(resource.toURI());
                System.out.println("Probando ruta 3 (classpath): " + resourcePath.toAbsolutePath());
                if (Files.exists(resourcePath)) {
                    System.out.println("✓ Modelo encontrado en classpath");
                    return resourcePath.toAbsolutePath().toString();
                }
            }
        } catch (Exception e) {
            System.out.println("No se encontró en classpath: " + e.getMessage());
        }

        // Opción 4: Buscar en el directorio target/classes (común en Maven)
        Path targetPath = Paths.get(System.getProperty("user.dir"), "target", "classes", modelPath);
        System.out.println("Probando ruta 4 (target): " + targetPath.toAbsolutePath());
        if (Files.exists(targetPath)) {
            System.out.println("✓ Modelo encontrado en target");
            return targetPath.toAbsolutePath().toString();
        }

        // Si no se encuentra en ninguna ubicación
        String errorMsg = String.format(
                "❌ No se encontró el modelo en ninguna ubicación intentada:\n" +
                        "  1. %s (existe: %b)\n" +
                        "  2. %s (existe: %b)\n" +
                        "  3. Classpath: %s\n" +
                        "  4. %s (existe: %b)\n\n" +
                        "Por favor, verifica que el archivo 'flight-model.onnx' esté en: %s",
                path.toAbsolutePath(), Files.exists(path),
                workingDir.toAbsolutePath(), Files.exists(workingDir),
                modelPath,
                targetPath.toAbsolutePath(), Files.exists(targetPath),
                workingDir.toAbsolutePath()
        );

        throw new IOException(errorMsg);
    }

    /**
     * Realiza una predicción utilizando el modelo ONNX.
     *
     * @param features Map con los tensores de entrada preparados
     * @return Map con los resultados: "probability" y "label"
     */
    public Map<String, Object> predict(Map<String, OnnxTensor> features) {
        // Usamos try-with-resources para asegurar que el Result se cierre y libere memoria
        try (OrtSession.Result result = session.run(features)) {

            // 1. Obtener el label predicho (Clase 0 o 1)
            long[] labels = (long[]) result.get("output_label").get().getValue();
            long predictedLabel = labels[0];

            // 2. Obtener las probabilidades (ZipMap)
            // ZipMap siempre devuelve una secuencia (List) de mapas
            Object probabilityOutput = result.get("output_probability").get().getValue();

            double probabilityDelayed = 0.0;

            if (probabilityOutput instanceof List<?> probList && !probList.isEmpty()) {
                Object firstElement = probList.get(0);

                // El "truco" está aquí: OnnxMap tiene su propio método .getValue()
                if (firstElement instanceof OnnxMap onnxMap) {
                    // El keyType es INT64 (Long), valueType es FLOAT (Float)
                    Map<Long, Float> mapValores = (Map<Long, Float>) onnxMap.getValue();

                    // Buscamos la probabilidad de la clase 1 (Retrasado)
                    // Usamos 1L porque el tipo es Long
                    Float prob1 = mapValores.get(1L);
                    if (prob1 != null) {
                        probabilityDelayed = prob1.doubleValue();
                    }
                }
                // Caso alternativo si por alguna razón ya viene como Map estándar
                else if (firstElement instanceof Map<?, ?> standardMap) {
                    Object p1 = standardMap.get(1L);
                    if (p1 instanceof Number num) {
                        probabilityDelayed = num.doubleValue();
                    }
                }
            } else {
                throw new PredictionException("El formato de salida de probabilidades es inválido o está vacío");
            }

            return Map.of(
                    "probability", probabilityDelayed,
                    "label", predictedLabel
            );

        } catch (OrtException e) {
            throw new PredictionException("Error al ejecutar la predicción ONNX: " + e.getMessage(), e);
        }
    }

    /**
     * Extrae la probabilidad de clase 1 desde un string con formato {0=x, 1=y}
     */
    private double extractProbabilityFromString(String mapString) {
        try {
            // Buscar "1=" en el string
            int index = mapString.indexOf("1=");
            if (index == -1) {
                throw new PredictionException("No se encontró '1=' en el string: " + mapString);
            }

            // Extraer el número después de "1="
            String afterEquals = mapString.substring(index + 2);

            // Encontrar dónde termina el número (coma o llave de cierre)
            int endIndex = afterEquals.length();
            for (int i = 0; i < afterEquals.length(); i++) {
                char c = afterEquals.charAt(i);
                if (c == ',' || c == '}') {
                    endIndex = i;
                    break;
                }
            }

            String numberStr = afterEquals.substring(0, endIndex).trim();
            return Double.parseDouble(numberStr);

        } catch (Exception e) {
            throw new PredictionException("Error al parsear probabilidad del string: " + mapString, e);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (session != null) {
                session.close();
            }
            if (environment != null) {
                environment.close();
            }
        } catch (OrtException e) {
            System.err.println("Error al cerrar recursos ONNX: " + e.getMessage());
        }
    }

    public OrtEnvironment getEnvironment() {
        return environment;
    }
}