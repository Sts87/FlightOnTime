# ✈️ FlightOnTime – API de Predicción de Retrasos de Vuelos

## 📌 Descripción
FlightOnTime FastAPI es un microservicio en Python que predice si un vuelo llegará retrasado usando datos históricos y un modelo de Machine Learning (Logistic Regression con umbral optimizado).  
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

## 🧪 Ejemplo de Uso (curl)

```bash
curl -X POST http://127.0.0.1:8000/predict \
  -H "Content-Type: application/json" \
  -d '{
    "Airline": "AA",
    "AirportFrom": "SFO",
    "AirportTo": "LAX",
    "Time": 1430,
    "Length": 120,
    "TimeDay": "Afternoon", #Hay que retirar y adaptar a nuevo modelo
    "Duration": "Medium" #Hay que retirar y adaptar a nuevo modelo
  }'
```

**Respuesta esperada**:

```json
{
  "prediction": "Retrasado",
  "probability": 0.73
}
```

---

## 📂 Estructura del Proyecto

```
FlightOnTime/
├─ app/
│  ├─ main.py        # API FastAPI
│  ├─ utils.py       # Funciones auxiliares
│  ├─ schema.py      # Modelos Pydantic para request/response
│  ├─ model.py       # Carga y manejo del modelo ML
├─ model/
│  ├─ flight_delay_bundle.joblib  # Modelo exportado
│  ├─ flight_delay_model.onnx  # Modelo exportado
├─ requirements.txt
├─ README.md
```

---

## 🔧 Cómo consumir la API desde Java

El backend en Java puede consumir la API usando cualquier cliente HTTP (RestTemplate, HttpClient, OkHttp, etc.).
Ejemplo en pseudocódigo:

```java
POST http://127.0.0.1:8000/predict
Content-Type: application/json
Body: {
  "Airline": "AA",
  "AirportFrom": "SFO",
  "AirportTo": "LAX",
  "Time": 1430,
  "Length": 120
}

Response:
{
  "prediction": "Retrasado",
  "probability": 0.73
}
```

---

## ✅ Buenas Prácticas

* Mantener el modelo actualizado en `flight_delay_bundle.joblib` o `flight_delay_model.onnx`.
* Siempre usar un entorno virtual para evitar conflictos de dependencias.
* Versionar las dependencias en `requirements.txt`.
