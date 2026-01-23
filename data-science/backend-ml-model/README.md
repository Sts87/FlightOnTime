# ✈️ FlightOnTime – API de Predicción de Retrasos de Vuelos

## 📌 Descripción
FlightOnTime FastAPI es un microservicio en Python que predice si un vuelo llegará retrasado usando datos históricos y un modelo de Machine Learning (Logistic Regression con calibracion y umbral optimizado).  
Se expone como una **API REST** que puede ser consumida por cualquier frontend o servicio backend, incluyendo proyectos en Java.

---

## ⚙️ Requisitos Previos
- Python 3.10+ (recomendado instalar vía [Miniconda](https://docs.conda.io/en/latest/miniconda.html) o [Python oficial](https://www.python.org/downloads/))
- Git
- Navegador o herramienta de pruebas de API (Postman, curl)

---

## 🛠️ Instalación y Setup del Entorno

1. **Clonar el repositorio**:
```bash
git clone https://github.com/Sts87/FlightOnTime.git
cd FlightOnTime/backend-ml-model
````

2. **Crear un entorno virtual (opcional pero recomendado)**:

```bash
python -m venv venv
```
3. **Activar entorno**:
```bash
# Windows:
. venv/Scripts/activate
```

```bash
# Mac/Linux:
source venv/bin/activate
```

4. **Instalar dependencias**:

```bash
pip install -r requirements.txt
```

5. **Verificar instalación**:

```bash
python -c "import sklearn, joblib; print('✅ sklearn:', sklearn.__version__); print('✅ joblib:', joblib.__version__)"
```

> ⚠️ Nota: si tu `.gitignore` excluye el modelo `flight_delay_bundle.joblib` o `flight_delay_model.onnx`, asegúrate de tenerlo en la carpeta `app/model/` para que la API funcione.

---

## 🚀 Ejecutar la API

1. Entrar a la carpeta del proyecto:

```bash
cd FlightOnTime/backend-ml-model
```

2. Levantar la API con Uvicorn:

```bash
uvicorn app.main:app --reload
```

3. La API quedará corriendo en:

```
http://127.0.0.1:8000
```

---

4. Documentación de la API REST:

```
http://127.0.0.1:8000/docs
```
# 🧪 Contrato de la API

1️⃣ Predicción individual (JSON)

* Ruta: /predict
* Método: POST
* Headers: Content-Type: application/json
* Body esperado:

```json
{
  "Airline": "AA",
  "AirportFrom": "SFO",
  "AirportTo": "LAX",
  "Time": 1430,
  "Length": 120,
  "DayOfWeek": 3
}
```
Respuesta esperada:

```json
{
  "prediction": 1,          // 1 = Retrasado, 0 = Puntual
  "probability": 0.73       // Probabilidad de retraso
}
```

🧪 Ejemplo de Uso (curl)

```bash
curl -X POST http://127.0.0.1:8000/predict \
  -H "Content-Type: application/json" \
  -d '{
    "Airline": "AA",
    "AirportFrom": "SFO",
    "AirportTo": "LAX",
    "Time": 1430,
    "Length": 120,
    "DayOfWeek": 3
  }'
```

**Respuesta esperada**:

```json
{
  "prediction": 1, //"Retrasado"
  "probability": 0.73
}
```

2️⃣ Predicción por CSV (batch)

* Ruta: /predict/csv
* Método: POST
* Headers: Content-Type: multipart/form-data
* Body: archivo CSV con las mismas columnas que espera el modelo (Airline, AirportFrom, AirportTo, Time, Length, DayOfWeek).

🧪 Ejemplo de Uso (curl):

```bash
curl -X POST http://127.0.0.1:8000/predict/csv \
  -F "file=@D:/DEPLOY/FlightOnTime/backend-ml-model/app/csv_test.csv" //ajustar la ruta real del archivo
```

Respuesta esperada:
```json
{
  "total": 3,
  "predictions": [
    {"prediction": 1, "probability": 0.73},
    {"prediction": 0, "probability": 0.21},
    {"prediction": 1, "probability": 0.65}
  ]
}
```
---

## 📂 Estructura del Proyecto

```
FlightOnTime/
├─ app/
│  ├─ main.py                 # API FastAPI
│  ├─ utils.py                # Mapping columnas y verificación de datos para consumo del modelo
│  ├─ schemas.py              # Modelos Pydantic para request/response
│  ├─ onnx_model.py           # Lógica para cargar y predecir con ONNX
├─ model/fastapi-model/
│  ├─ flight_delay_model_calibrado.onnx
│  ├─ flight_delay_threshold_calibrado.json
├─ requirements.txt
├─ README.md
```
---

## ✅ Buenas Prácticas


* Mantener los modelos actualizados en `model/fastapi-model/`.
* Siempre usar un entorno virtual para evitar conflictos de dependencias.
* Versionar las dependencias en `requirements.txt`.
* Validar los CSV antes de enviarlos para evitar errores de parsing.
* Usar la ruta `/predict/csv` para batch y `/predict` para predicciones individuales.
