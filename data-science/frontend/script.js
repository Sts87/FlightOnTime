// ========================================
// CONFIGURACIÓN Y CONSTANTES
// ========================================

//const API_BASE_URL = "http://localhost:8080/flights"; // localhost para desarrollo
const API_BASE_URL = "https://fly-on-time-production.up.railway.app/flights"; // production

// Lista de aerolíneas válidas del backend
const VALID_AIRLINES = [
    "CO", "US", "AA", "AS", "DL", "B6", "HA", "OO",
    "9E", "OH", "EV", "XE", "YV", "UA", "MQ", "FL", "F9", "WN"
];

// Lista completa de aeropuertos válidos del backend
const VALID_AIRPORTS = [
    "ABE", "ABI", "ABQ", "ABY", "ACT", "ACV", "ACY", "ADQ", "AEX", "AGS",
    "ALB", "AMA", "ANC", "ASE", "ATL", "ATW", "AUS", "AVL", "AVP", "AZO",
    "BDL", "BET", "BFL", "BGM", "BGR", "BHM", "BIL", "BIS", "BKG", "BLI",
    "BMI", "BNA", "BOI", "BOS", "BQK", "BQN", "BRO", "BRW", "BTM", "BTR",
    "BTV", "BUF", "BUR", "BWI", "BZN", "CAE", "CAK", "CDC", "CDV", "CEC",
    "CHA", "CHO", "CHS", "CIC", "CID", "CLD", "CLE", "CLL", "CLT", "CMH",
    "CMI", "CMX", "COD", "COS", "COU", "CPR", "CRP", "CRW", "CSG", "CVG",
    "CWA", "CYS", "DAB", "DAL", "DAY", "DBQ", "DCA", "DEN", "DFW", "DHN",
    "DLH", "DRO", "DSM", "DTW", "EAU", "ECP", "EGE", "EKO", "ELM", "ELP",
    "ERI", "EUG", "EVV", "EWR", "EYW", "FAI", "FAR", "FAT", "FAY", "FCA",
    "FLG", "FLL", "FLO", "FNT", "FSD", "FSM", "FWA", "GCC", "GEG", "GFK",
    "GGG", "GJT", "GNV", "GPT", "GRB", "GRK", "GRR", "GSO", "GSP", "GTF",
    "GTR", "GUC", "HDN", "HLN", "HNL", "HOU", "HPN", "HRL", "HSV", "HTS",
    "IAD", "IAH", "ICT", "IDA", "ILM", "IND", "IPL", "ISP", "ITH", "ITO",
    "IYK", "JAC", "JAN", "JAX", "JFK", "JNU", "KOA", "KTN", "LAN", "LAS",
    "LAX", "LBB", "LCH", "LEX", "LFT", "LGA", "LGB", "LIH", "LIT", "LMT",
    "LNK", "LRD", "LSE", "LWB", "LWS", "LYH", "MAF", "MBS", "MCI", "MCO",
    "MDT", "MDW", "MEI", "MEM", "MFE", "MFR", "MGM", "MHK", "MHT", "MIA",
    "MKE", "MKG", "MLB", "MLI", "MLU", "MOB", "MOD", "MOT", "MQT", "MRY",
    "MSN", "MSO", "MSP", "MSY", "MTJ", "MYR", "OAJ", "OAK", "OGG", "OKC",
    "OMA", "OME", "ONT", "ORD", "ORF", "OTH", "OTZ", "PAH", "PBI", "PDX",
    "PHF", "PHL", "PHX", "PIA", "PIH", "PIT", "PLN", "PNS", "PSC", "PSE",
    "PSP", "PVD", "PWM", "RAP", "RDD", "RDM", "RDU", "RIC", "RKS", "RNO",
    "ROA", "ROC", "ROW", "RST", "RSW", "SAF", "SAN", "SAT", "SAV", "SBA",
    "SBN", "SBP", "SCC", "SCE", "SDF", "SEA", "SFO", "SGF", "SGU", "SHV",
    "SIT", "SJC", "SJT", "SJU", "SLC", "SMF", "SMX", "SNA", "SPI", "SRQ",
    "STL", "STT", "STX", "SUN", "SWF", "SYR", "TEX", "TLH", "TOL", "TPA",
    "TRI", "TUL", "TUS", "TVC", "TWF", "TXK", "TYS", "UTM", "VLD", "VPS",
    "XNA", "YAK", "YUM"
];

// Mínimo y máximo de distancia permitida
const MIN_DISTANCE = 200;
const MAX_DISTANCE = 8000;

// ========================================
// ELEMENTOS DOM
// ========================================

const form = document.getElementById('flightForm');
const result = document.getElementById('result');
const csvInput = document.getElementById("csvFile");
const formInputs = document.querySelectorAll("#flightForm input:not(#csvFile)");

// ========================================
// UTILIDADES
// ========================================

// Deshabilitar todos los campos de entrada manual
function disableManualInputs() {
    formInputs.forEach(input => input.disabled = true);
}

// Habilitar todos los campos de entrada manual
function enableManualInputs() {
    formInputs.forEach(input => input.disabled = false);
}

// Mostrar mensaje de error
function showError(message) {
    result.innerHTML = `
        <div style="color: red; font-weight: bold;">
            ⚠️ ${message}
        </div>
    `;
    result.classList.add("is-visible");
}

// Mostrar mensaje de cargando
function showLoading() {
    result.classList.remove("is-visible");
    result.textContent = "Procesando... ⏳";
}

// Renderizar tabla scrollable
function renderScrollableTable(container, headers, rowsHtml, options = {}) {
    const maxHeight = options.maxHeight || "420px";

    container.innerHTML = `
        <div class="table-container" style="max-height:${maxHeight}">
            <table class="data-table">
                <thead>
                    <tr>
                        ${headers.map(h => `<th>${h}</th>`).join("")}
                    </tr>
                </thead>
                <tbody>
                    ${rowsHtml}
                </tbody>
            </table>
        </div>
    `;
}

// Cargar datos de aeropuertos
async function cargarAirportsData() {
    try {
        const response = await fetch(AIRPORTS_AVG_URL);
        airports_arr = await response.json();
        console.log("✔ Datos de duración cargados");
    } catch (error) {
        console.error("❌ Error cargando duración promedio:", error);
    }
}

// ========================================
// VALIDACIONES
// ========================================

// Validar datos de entrada manual
function validateManualInput(data) {
    const distance = Number(data.distance);

    // Validar distancia
    if (!Number.isInteger(distance) || distance < MIN_DISTANCE  || distance > MAX_DISTANCE) {
        return `La distancia debe ser un número positivo y entero mayor o igual a ${MIN_DISTANCE} km y menor o igual a ${MAX_DISTANCE} km.`;
    }

    // Validar aerolínea
    if (!VALID_AIRLINES.includes(data.aerolinea)) {
        return `La aerolínea "${data.aerolinea}" no es válida. Aerolíneas soportadas: ${VALID_AIRLINES.join(", ")}`;
    }

    // Validar origen y destino diferentes
    if (data.origin === data.destination) {
        return "El aeropuerto de origen y destino no pueden ser el mismo.";
    }

    // Validar aeropuerto de origen
    if (!VALID_AIRPORTS.includes(data.origin)) {
        return `El aeropuerto de origen "${data.origin}" no es válido.`;
    }

    // Validar aeropuerto de destino
    if (!VALID_AIRPORTS.includes(data.destination)) {
        return `El aeropuerto de destino "${data.destination}" no es válido.`;
    }

    // Validar hora
    if (!data.time || !/^\d{2}:\d{2}$/.test(data.time)) {
        return "Hora inválida. Formato esperado: HH:MM";
    }

    // Validar fecha
    if (!data.date || isNaN(new Date(data.date).getTime())) {
        return "Fecha inválida.";
    }

    // Validar que la fecha sea futura o presente
    const selectedDate = new Date(`${data.date}T${data.time}:00`);
    const now = new Date();
    
    if (selectedDate < now) {
        return "La fecha y hora deben ser futuras o presentes.";
    }

    return null; // Sin errores
}

// ========================================
// MANEJO DE CSV (BATCH)
// ========================================

async function handleCsvSubmission(csvFile) {

    // Crear formulario de datos
    const formData = new FormData();
    formData.append("file", csvFile);

    // Enviar formulario
    try {
        const response = await fetch(`${API_BASE_URL}/batch/predict`, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            const errorData = await response.json();
            
            // Manejar errores de validación CSV
            if (errorData.codigo === "ERROR_VALIDACION_CSV" && errorData.errores) {
                let errorHtml = `
                    <h3 style="color: red;">❌ Errores en el archivo CSV</h3>
                    <p>${errorData.mensaje}</p>
                    <ul style="text-align: left; max-height: 400px; overflow-y: auto;">
                `;
                
                // Crear HTML de cada error
                errorData.errores.forEach(err => {
                    errorHtml += `
                        <li>
                            <strong>Línea ${err.lineaNumero}:</strong> ${err.error}<br>
                            <small style="color: white;">${err.contenidoLinea}</small>
                        </li>
                    `;
                });
                
                errorHtml += `</ul>`;
                result.innerHTML = errorHtml;
                result.classList.add("is-visible");
                return;
            }
            
            throw new Error(errorData.mensaje || "Error al procesar el CSV");
        }

        // Obtener predicciones
        const predictions = await response.json();

        // Crear cabeceras
        const headers = [
            "#",
            "Aerolínea",
            "Ruta",
            "Distancia",
            "Probabilidad",
            "Resultado"
        ];

        // Mostrar resultados
        result.innerHTML = `
            <h2>✨ Resultados desde CSV ✨</h2>
            <p>📄 Total de filas procesadas: ${predictions.length}</p>

            <div id="csv-table"></div>
        `;

        // Crear HTML de tabla
        let rowsHtml = "";

        // Recorrer cada predicción
        predictions.forEach((pred, index) => {

            // Obtener porcentaje de probabilidad
            const probabilityPercent = (pred.probabilidad * 100).toFixed(2);

            // Obtener estado de la predicción
            const isDelayed =
                pred.estadoPredicho === "Retrasado" || pred.probabilidad >= 0.44;

            // Obtener icono de estado
            const statusIcon = isDelayed ? "⏱️ Retrasado" : "✅ Puntual";
            const statusClass = isDelayed ? "status-delayed" : "status-ok";
            const statusColor = isDelayed ? "#ff6b6b" : "#51cf66";

            // Crear HTML de fila
            rowsHtml += `
                <tr>
                    <td>${index + 1}</td>
                    <td>${pred.aerolinea}</td>
                    <td>${pred.origen} → ${pred.destino}</td>
                    <td>${pred.distancia} km</td>
                    <td>${probabilityPercent}%</td>
                    <td style="background-color: ${statusColor}; color: white; font-weight: bold;" class="${statusClass}">
                        ${statusIcon}
                    </td>
                </tr>
            `;
        });

        // Crear contenedor de tabla
        const csvTableContainer = document.getElementById("csv-table");

        // Renderizar tabla
        renderScrollableTable(
            csvTableContainer,
            headers,
            rowsHtml,
            {
                maxHeight: "500px"
            }
        );

        // Mostrar resultado
        result.classList.add("is-visible");

        // Limpiar formulario
        csvInput.value = "";

        // Habilitar campos manuales
        enableManualInputs();

    // Si ocurre algún error, mostrar mensaje
    } catch (error) {
        console.error("Error en CSV:", error);
        showError(error.message || "Error al procesar el archivo CSV");
    }
}

// ========================================
// MANEJO DE PREDICCIÓN INDIVIDUAL
// ========================================

async function handleManualSubmission(data) {

    // Validar entrada
    const validationError = validateManualInput(data);
    if (validationError) {
        showError(validationError);
        return;
    }

    // Crear payload
    const payload = {
        aerolinea: data.aerolinea,
        origen: data.origin,
        destino: data.destination,
        fechaDePartida: `${data.date}T${data.time}:00`,
        distancia: Number(data.distance)
    };

    // Enviar predicción
    try {
        const response = await fetch(`${API_BASE_URL}/predict`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        // Verificar respuesta
        if (!response.ok) {
            const errorData = await response.json();
            
            // Manejar errores de validación del backend
            if (Array.isArray(errorData)) {
                const errors = errorData.map(err => `${err.campo}: ${err.mensaje}`).join("<br>");
                showError(`Errores de validación:<br>${errors}`);
                return;
            }
            
            throw new Error(errorData.mensaje || "Error al obtener la predicción");
        }

        // Obtener predicción
        const prediction = await response.json();

        // Obtener porcentaje de probabilidad
        const probabilityPercent = (prediction.probabilidad * 100).toFixed(2);
        
        // Obtener estado de la predicción
        const isDelayed = prediction.estado === "Retrasado" || prediction.probabilidad >= 0.44;

        // Obtener icono de estado
        const statusIcon = isDelayed ? "⏱️ Retrasado" : "✅ Puntual";
        const statusColor = isDelayed ? "#ff6b6b" : "#51cf66";

        // Crear cabeceras
        const headers = [
            "Probabilidad de Retraso",
            "Resultado"
        ];

        // Crear HTML de fila
        const rowsHtml = `
            <tr>
                <td style="font-size: 1.4em; font-weight: bold;">
                    ${probabilityPercent}%
                </td>
                <td style="background-color: ${statusColor}; color: white; font-weight: bold;" class="${isDelayed ? "status-delayed" : "status-ok"}">
                    ${statusIcon}
                </td>
            </tr>
        `;

        // Mostrar resultado
        result.innerHTML = `
            <h2>✨ Resultado de la Predicción ✨</h2>

            <div style="margin: 20px 0;">
                <p><strong>Ruta:</strong> ${payload.origen} → ${payload.destino}</p>
                <p><strong>Aerolínea:</strong> ${payload.aerolinea}</p>
                <p><strong>Distancia:</strong> ${payload.distancia} km</p>
                <p><strong>Fecha:</strong> ${payload.fechaDePartida}</p>
            </div>

            <div id="manual-table"></div>
        `;

        // Crear contenedor de tabla de resultados
        const manualTableContainer = document.getElementById("manual-table");

        // Renderizar tabla de resultados
        renderScrollableTable(
            manualTableContainer,
            headers,
            rowsHtml,
            { maxHeight: "200px" }
        );

        // Mostrar resultado
        result.classList.add("is-visible");

    // Si ocurre algún error, mostrar mensaje
    } catch (error) {
        console.error("Error en predicción individual:", error);
        
        if (error instanceof TypeError && error.message.includes("Failed to fetch")) {
            showError("No se pudo conectar con el servidor. Por favor, verifica que el backend esté ejecutándose.");
        } else {
            showError(error.message || "Error al obtener la predicción");
        }
    }
}

// ========================================
// EVENTO PRINCIPAL DEL FORMULARIO
// ========================================

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    showLoading();

    const csvFile = csvInput.files[0];

    // Modo CSV (batch)
    if (csvFile) {
        await handleCsvSubmission(csvFile);
        return;
    }

    // Modo manual (individual)
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    data.aerolinea = data.aeroline?.trim().toUpperCase();
    data.origin = data.origin?.trim().toUpperCase();
    data.destination = data.destination?.trim().toUpperCase();

    await handleManualSubmission(data);
});

// ========================================
// EVENTO DE CAMBIO DE CSV
// ========================================

csvInput.addEventListener("change", () => {
    if (csvInput.files.length > 0) {
        disableManualInputs();
    } else {
        enableManualInputs();
    }
});
