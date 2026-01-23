# ☕ FlightOnTime - Backend API

**FlightOnTime** es un MVP de predicción de retrasos de vuelos que utiliza una arquitectura robusta en **Java 21** para gestionar la lógica de negocio y la persistencia de datos. Este módulo actúa como el orquestador central, validando la integridad de los vuelos y calculando probabilidades de puntualidad mediante reglas de negocio avanzadas.

El proyecto está diseñado bajo una **arquitectura desacoplada**, separando claramente la lógica de validación, el servicio de cálculo y la persistencia en base de datos.

---

## 1️⃣ Visión General

El objetivo del backend de **FlightOnTime** es responder a la pregunta:

> **¿Es este vuelo propenso a sufrir un retraso?**

A partir de la información proporcionada por el cliente, el sistema procesa los datos para devolver:

- **Una clasificación**: `Puntual` o `Retrasado`.
- **Una probabilidad asociada**: Un valor entre 0 y 1 calculado mediante heurísticas de tiempo y distancia.
- **Persistencia**: Almacenamiento automático del historial de consultas en MySQL.

---

## 2️⃣ Arquitectura del Sistema

La arquitectura está diseñada para ser escalable, utilizando **Spring Boot 4.0.0** y **JPA** para una gestión eficiente de los datos.
```
┌──────────────┐        ┌──────────────────┐        ┌───────────────────┐
│   Cliente    │  --->  │   Backend API    │  --->  │   Base de Datos   │
│ (Postman /   │        │  (Spring Boot)   │        │      (MySQL)      │
│  Frontend)   │        └──────────────────┘        └───────────────────┘
└──────────────┘
```

### Tecnologías

- **Lenguaje**: Java 21
- **Framework**: Spring Boot 4.0.0-SNAPSHOT
- **Persistencia**: Spring Data JPA con MySQL
- **Migraciones**: Flyway
- **Validación**: Bean Validation (Hibernate Validator)

---

## 3️⃣ Validación y Estructura de Datos

### DTO (FlightData)

El sistema utiliza **Records de Java** para garantizar la inmutabilidad de los datos de entrada, aplicando validaciones estrictas mediante anotaciones personalizadas.

- **Aerolínea**: Validación de código IATA (2-3 caracteres) mediante `@ValidAirline`.
- **Origen/Destino**: Validación de códigos de aeropuertos de 3 letras mediante `@ValidAirport`.
- **Distancia**: Validación de valor positivo y mínimo requerido.
- **Fecha**: Restricción de fecha futura mediante `@FutureOrPresent`.

---

## 4️⃣ Lógica de Predicción (Heurística)

El servicio implementa un **motor de cálculo** que estima el riesgo de retraso basándose en patrones históricos de vuelo.

### Factores de Riesgo

| Factor         | Condición                | Incremento de Probabilidad |
|----------------|--------------------------|----------------------------|
| Distancia      | > 800 unidades           | +20%                       |
| Horario Pico   | Entre 18:00 y 22:00 hrs  | +25%                       |
| Base           | Valor inicial            | 10%                        |

### Interpretación del Resultado

El sistema utiliza un **umbral de 0.5 (50%)** para clasificar el vuelo:

- **Probabilidad < 0.5**: El vuelo se clasifica como `Puntual`.
- **Probabilidad ≥ 0.5**: El vuelo se clasifica como `Retrasado`.

---

## 5️⃣ API Reference

### Realizar Predicción y Guardar

**POST** `/flights/predict`

**Request Body (JSON):**
```json
{
  "aerolinea": "AA",
  "origen": "SFO",
  "destino": "LAX",
  "fechaDePartida": "2024-12-31T20:00:00",
  "distancia": 950
}
```

**Response (JSON):**
```json
{
  "estado": "Retrasado",
  "probabilidad": 0.55
}
```

### Listar Historial

**GET** `/flights`

Soporta paginación por defecto (10 registros por página) y ordenamiento por aerolínea.

---

## 6️⃣ Ejecución del Proyecto

### Requisitos

- **Java JDK 21**
- **IntelliJ IDEA** (Community o Ultimate)
- **MySQL Server** con una base de datos llamada `flight_on_time`

### Configuración

1. **Configurar la Base de Datos**

   Crea la base de datos en MySQL:
```sql
   CREATE DATABASE flight_on_time;
```

2. **Configurar Credenciales**

   Edita el archivo `src/main/resources/application.properties`:
```properties
   spring.datasource.url=jdbc:mysql://localhost/flight_on_time
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
```

### Ejecución con IntelliJ IDEA

1. Abre el proyecto en IntelliJ IDEA
2. Navega hasta la clase principal (`FlightOnTimeApplication.java`)
3. Haz clic en el botón **▶️ Run** (o presiona `Shift + F10`)

### Verificar la Ejecución

Una vez iniciada la aplicación, deberías ver en la consola:
```
Started FlightOnTimeApplication in X.XXX seconds
```

La API estará disponible en: `http://localhost:8080`

### Probar los Endpoints

Puedes usar **Postman** o **curl** para probar los endpoints:
```bash
# Realizar una predicción
curl -X POST http://localhost:8080/flights/predict \
  -H "Content-Type: application/json" \
  -d '{
    "aerolinea": "AA",
    "origen": "SFO",
    "destino": "LAX",
    "fechaDePartida": "2024-12-31T20:00:00",
    "distancia": 950
  }'

# Listar historial
curl http://localhost:8080/flights
```

---

## 7️⃣ Limitaciones y Trabajo Futuro

- **Integración ML**: Actualmente utiliza una lógica de reglas fijas. La siguiente fase incluye la integración con un microservicio de FastAPI para inferencia real con el modelo entrenado.
- **Seguridad**: Implementar Spring Security para la protección de los endpoints de consulta de historial.
- **Clima**: Incorporar APIs externas para ajustar la probabilidad basada en condiciones meteorológicas en tiempo real.

---

## 📌 Estado del Proyecto

**MVP Backend Operativo / Académico** 🚀
