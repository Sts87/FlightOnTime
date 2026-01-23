# ✈️ FlightOnTime

FlightOnTime es un **MVP de predicción de retrasos de vuelos** que utiliza datos históricos y un modelo de **Machine Learning** para estimar la probabilidad de que un vuelo se retrase.

El proyecto está pensado como una **arquitectura desacoplada**, separando claramente:

* El análisis y entrenamiento del modelo
* El servicio de predicción (ML)
* El backend de negocio (API)
  
&nbsp;

---

&nbsp;


## 1️⃣ Visión General

El objetivo de FlightOnTime es responder a la siguiente pregunta:

> **¿Este vuelo tiene alta probabilidad de retrasarse?**

A partir de información básica del vuelo (aerolínea, hora, ruta, distancia, etc.), el sistema devuelve:

* Una **clasificación**: `Puntual` o `Retrasado`
* Una **probabilidad asociada** (valor entre 0 y 1)

&nbsp;

---

&nbsp;

## 2️⃣ Arquitectura del Sistema

La arquitectura está diseñada para ser **escalable y extensible**.

```
┌──────────────┐        ┌──────────────────┐        ┌───────────────────┐
│   Cliente    │  --->  │  Backend API     │  --->  │  ML Model Service │
│ (Postman /   │        │ (Spring Boot)    │        │ (FastAPI / ONNX)  │
│  Frontend)   │        └──────────────────┘        └───────────────────┘
└──────────────┘
```

### Tecnologías

* **Backend**: Java + Spring Boot
* **Machine Learning**: Python + scikit-learn
* **Modelo**:

  * Exportado a **ONNX** *o*
  * Servido como **microservicio FastAPI**

&nbsp;

---

&nbsp;

## 3️⃣ Análisis de Datos

### Dataset

* **Fuente**: Datos históricos de vuelos comerciales
* **Tamaño**: Miles de registros
* **Tipo de datos**: Tabular

### ¿Qué representa el dataset?

* Comportamiento histórico de vuelos
* Relación entre aerolínea, horario y retrasos
* Patrones temporales

### ¿Qué NO representa?

* Condiciones climáticas
* Tráfico aéreo en tiempo real
* Eventos extraordinarios (huelgas, emergencias)

&nbsp;

---

&nbsp;

### Variables Principales

| Variable | Descripción                                   |
| -------- | --------------------------------------------- |
| Airline  | Código IATA de la aerolínea                   |
| Time     | Hora programada de salida                     |
| Delay    | Variable objetivo: `0` puntual, `1` retrasado |

&nbsp;

---

&nbsp;

## 4️⃣ Proceso de Machine Learning

### Tipo de Problema

* **Clasificación binaria**

  * `0` → Vuelo puntual
  * `1` → Vuelo retrasado

### Modelo Utilizado

Se eligió un modelo de clasificación tradicional (por ejemplo, **Logistic Regression / Random Forest**) debido a:

* Interpretabilidad
* Buen desempeño con datos tabulares
* Bajo costo computacional

### Métricas de Evaluación

* Accuracy
* Precision
* Recall
* ROC-AUC

Estas métricas permiten evaluar no solo si el modelo acierta, sino **qué tan confiable es cuando predice un retraso**.

### Interpretación de la Probabilidad

El modelo devuelve una **probabilidad de retraso**.

Ejemplo:

* `probability = 0.78`

Significa que, según los datos históricos, existe un **78% de probabilidad** de que el vuelo se retrase.

&nbsp;

---

&nbsp;

## 5️⃣ Backend & API

### Endpoint Principal

```
POST /flights/predict
```

### Request (JSON)

```json
{
  "aerolinea": "UA",
  "origen": "BNA",
  "destino": "IAH",
  "fechaDePartida": "2026-03-20T22:45:00",
  "distancia": 400
}
```

### Response (JSON)

```json
{
  "prediction": "Retrasado",
  "probability": 0.73,
}
```

### Convenciones

* `prevision` es una clasificación legible para humanos
* `probability` es un valor continuo entre `0` y `1`

&nbsp;

---

&nbsp;

## 6️⃣ Modelo de Machine Learning

### Opción 1: FastAPI

```bash
uvicorn app:app --reload
```

### Opción 2: Modelo ONNX

* El modelo se carga directamente desde el backend
* Inferencia local sin red

&nbsp;

---

&nbsp;

### Pruebas con Postman

1. Crear una request `POST`
2. URL: `http://localhost:8080/predict`
3. Body → `raw` → `JSON`
4. Enviar request

&nbsp;

---

&nbsp;

## 7️⃣ Limitaciones y Trabajo Futuro

### Limitaciones Actuales

* No se consideran datos climáticos
* No hay información en tiempo real
* Modelo entrenado solo con datos históricos

### Mejoras Futuras

* Integración con APIs de clima
* Uso de streaming de datos en tiempo real
* Reentrenamiento automático del modelo
* Explicabilidad del modelo (SHAP / LIME)

&nbsp;

---

&nbsp;

## 📌 Estado del Proyecto

Este proyecto corresponde a un **MVP educativo**, enfocado en demostrar:

* Buenas prácticas de arquitectura
* Integración ML + Backend
* Exposición de modelos vía API

&nbsp;

---

&nbsp;

Proyecto académico / portafolio 🚀

&nbsp;
