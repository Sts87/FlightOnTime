# ✈️ FlightOnTime — Predicción de retrasos de vuelos

> **Web App para predecir retrasos de vuelos en tiempo real, con dashboard estadístico y soporte para carga masiva de CSV.**

---

## 📌 Descripción

FlightOnTime es una aplicación web frontend que permite a los usuarios:

- **Predecir si un vuelo se retrasará** basado en datos como aerolínea, ruta, distancia y fecha.
- **Consultar estadísticas en tiempo real** (gráficas de dona, tablas de aerolíneas, últimas predicciones).
- **Cargar archivos CSV** para procesar múltiples predicciones a la vez.
- **Optimización de rendimiento**: solo actualiza el dashboard si los datos cambian (evita renderizados innecesarios).

Ideal para demostrar habilidades en:
- Validación de formularios
- Integración con API REST
- Manejo de estado y optimización de UI
- Visualización de datos con ApexCharts
- Uso de datalists y eventos DOM
- Optimización de polling con comparación de estado

---

## 📁 Estructura del proyecto

```
FlightOnTime/
├── csv/                    # Archivos CSV de prueba
├── image/                 # Imágenes y favicon
├── index.html              # Página de predicción
├── dashboard.html          # Dashboard estadístico
├── style.css               # Estilos globales
├── script.js               # Lógica de predicción y CSV
├── dashboard.js            # Lógica de dashboard + optimización
└── README.md               # Este archivo
```

---

## 🎯 Características

✅ **Predicción individual de vuelos**  
- Validación de aerolínea, aeropuertos, distancia, fecha y hora  
- Resultado con probabilidad y estado (puntual/retrasado)  
- Visualización con tabla y colores según riesgo

✅ **Dashboard en tiempo real**  
- Gráficas de dona (hoy vs histórico)  
- Tablas de aerolíneas más puntuales y retrasadas  
- Lista de últimas predicciones con hora y resultado
- Actualización cada 5 segundos (optimizada: solo renderiza si hay cambios)

✅ **Carga masiva con CSV**  
- Procesa múltiples vuelos desde archivo  
- Muestra errores por línea si hay formato inválido  
- Resultados en tabla con iconos y colores

✅ **Optimización de rendimiento**  
- Comparación de estado previo → evita renderizados innecesarios  
- Uso de `deepEqual` para objetos y arrays  
- Estado guardado en variables globales

---

## 🛠 Tecnologías

- **HTML5 / CSS3 / JavaScript (ES6+)** — Frontend puro, sin frameworks
- **ApexCharts** — Gráficas interactivas
- **Fetch API** — Comunicación con backend Spring Boot
- **Datalists** — Autocompletado de aerolíneas y aeropuertos
- **Responsive Design** — Funciona en móvil y desktop

---

## 🚀 Instalación y Uso

### Requisitos

- Servidor backend Spring Boot corriendo en `http://localhost:8080`
- Live Server (VS Code) o algún otro servidor local para evitar problemas de CORS

### Pasos

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/FlightOnTime.git
   cd FlightOnTime

* Asegúrate de que tu backend esté corriendo en http://localhost:8080

* Abre index.html en tu navegador (o usa un servidor local como Live Server en VS Code)

#### ¡Listo! Puedes probar:
* Predicción individual
* Carga de CSV
* Dashboard en dashboard.html

---

### 📈 Dashboard (dashboard.html)
* Gráficas de dona: % retrasados vs puntuales (hoy e histórico) 
* Ranking de aerolíneas: top 5 más puntuales y más retrasadas
* Últimas predicciones: ruta, aerolínea, estado, hora
* Actualización automática: cada 5 segundos (solo si hay cambios)

---

### 📥 Carga de CSV
* Formato esperado: CSV con columnas aerolinea, origen, destino, fechaDePartida, distancia
* Si hay errores, se muestran por línea con el contenido de la fila
* Resultados en tabla con iconos y colores según probabilidad


---

### 📝 Validación de Datos

El backend de la aplicación valida los datos que se reciben de los usuarios. Esto incluye:

* Validación de campos obligatorios
* Validación de formato de fecha y hora
* Validación de aerolínea
* Validación de origen y destino diferentes
* Validación de distancia
* Validación de aeropuerto de origen
* Validación de aeropuerto de destino
* Validación de hora
* Validación de fecha

---

### 📈 Visualización de Datos

La aplicación muestra los datos en una tabla de formato HTML. Esto incluye:

* Tabla de predicciones individuales
* Tabla de predicciones de todas las rutas con dashbords
* Tabla de últimas predicciones
* Tabla de aerolíneas más puntuales y retrasadas

---

## 📊 Predicción de Datos

La aplicación realiza una predicción de retraso de vuelo en tiempo real. Esto incluye:

* Predicción de retraso de vuelo individual
* Predicción de retraso de vuelo de todas las rutas en batch en un solo archivo CSV

---

## 📌 Estado del Proyecto

Este proyecto corresponde a un **MVP educativo**, enfocado en demostrar:

- 📈 El funcionamiento de la aplicación
- 📥 El funcionamiento de la carga de datos
- 📝 El funcionamiento de la validación de datos
- 📊 El funcionamiento de la visualización de datos
- 📈 El funcionamiento de la predicción de datos
- 📈 El funcionamiento de la actualización automática de datos

---
Proyecto académico / portafolio 🚀