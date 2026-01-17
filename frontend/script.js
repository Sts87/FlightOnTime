const form = document.getElementById('flightForm');
const result = document.getElementById('result');
const csvInput = document.getElementById("csvFile");
const formInputs = document.querySelectorAll("#flightForm input:not(#csvFile)");

const VALID_AIRLINES = [
    "CO", "US", "AA", "AS", "DL", "B6", "HA", "OO",
    "9E", "OH", "EV", "XE", "YV", "UA", "MQ", "FL", "F9", "WN"
];

const VALID_AIRPORTS = [
    "ATL", "AUS", "BNA", "BOS", "BWI", "CLT", "DAL", "DCA", "DEN", "DFW",
    "DTW", "EWR", "FLL", "HNL", "HOU", "IAD", "IAH", "JFK", "LAS", "LAX",
    "LGA", "MCO", "MDW", "MIA", "MSP", "MSY", "OAK", "ORD", "PDX", "PHL",
    "PHX", "RDU", "SAN", "SEA", "SFO", "SJC", "SLC", "SMF", "STL", "TPA", "ANC"
];


function getDayOfWeek(dateString) {
    const date = new Date(dateString);
    return date.getDay() === 0 ? 7 : date.getDay();
}

function disableManualInputs() {
    formInputs.forEach(input => input.disabled = true);
}

function enableManualInputs() {
    formInputs.forEach(input => input.disabled = false);
}

csvInput.addEventListener("change", () => {
    if (csvInput.files.length > 0) {
        disableManualInputs();
    } else {
        enableManualInputs();
    }
});

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    result.classList.remove("is-visible");

    result.textContent = "Procesando... ⏳";

    const csvFile = csvInput.files[0];

    try {
        // =============================
        // 🟢 MODO CSV (batch)
        // =============================
        if (csvFile) {
            const csvFormData = new FormData();
            csvFormData.append("file", csvFile);

            const response = await fetch(
                //"http://localhost:8000/predict/csv", //ruta fastapi
                 "http://localhost:8080/flights/batch/predict", //ruta java spring
                {
                    method: "POST",
                    body: csvFormData
                }
            );

            if (!response.ok) {
                throw new Error("Error al procesar el CSV");
            }

            const resData = await response.json();

            let predictionsHtml = `
                <h2>✨ Resultados desde CSV ✨</h2>
                <p>📄 Total de filas procesadas: ${resData.total}</p>

                <table border="1" cellpadding="6">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Probabilidad de retraso</th>
                            <th>Resultado</th>
                        </tr>
                    </thead>
                    <tbody>
            `;

            resData.forEach((pred, index) => { //java spring
                predictionsHtml += `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${(pred.probabilidad * 100).toFixed(2)}%</td>
                        <td>${pred.estado === "DELAYED" ? "⏱️ Retrasado" : "✅ Puntual"}</td>
                    </tr>
                `;
            });
            // resData.predictions.forEach((pred, index) => { //fastapi
            
            //     predictionsHtml += `
            //         <tr>
            //             <td>${index + 1}</td>
            //             <td>${(pred.probability * 100).toFixed(2)}%</td>
            //             <td>${pred.prediction === 1 ? "⏱️ Retrasado" : "✅ Puntual"}</td>
            //         </tr>
            //     `;
            // });

            predictionsHtml += `
                    </tbody>
                </table>
            `;

            result.innerHTML = predictionsHtml;
            result.classList.add("is-visible");

            // 🔄 liberar formulario manual después del CSV
            csvInput.value = "";      // limpia el input file
            enableManualInputs();     // reactiva inputs manuales

            return;
        }

        // =============================
        // 🔵 MODO MANUAL
        // =============================
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        data.aerolinea = data.aeroline
            ?.trim()
            .toUpperCase();
        // console.log("FORM DATA:", data);
        // console.log("AEROLINEA NORMALIZADA:", data.aerolinea);
        // console.log("VALID AIRLINES:", VALID_AIRLINES);


        // =============================
        // 🔍 VALIDACIONES FRONTEND
        // =============================

        // Validar distancia
        const distance = Number(data.distance);

        // entero, no NaN, mínimo 200
        if (
            !Number.isInteger(distance) ||
            distance < 200
        ) {
            result.innerHTML = `
                <div style="color: red; font-weight: bold;">
                    ⚠️ La distancia debe ser un número entero mayor o igual a 200.
                </div>
            `;
            result.classList.add("is-visible");
            return;
        }

        // Validar aerolínea
        if (!VALID_AIRLINES.includes(data.aerolinea)) { //spring
        //if (!VALID_AIRLINES.includes(data.aeroline)) { //fastapi
            result.innerHTML = `
                <div style="color: red; font-weight: bold;">
                    ⚠️ La aerolínea seleccionada no es válida.
                </div>
            `;
            result.classList.add("is-visible");
            return;
        }

        if (data.origin === data.destination) {
            result.innerHTML = `
                <div style="color: red; font-weight: bold;">
                    ⚠️ El aeropuerto de origen y destino no pueden ser el mismo.
                </div>
            `;
            result.classList.add("is-visible");
            return;
        }

        if (!VALID_AIRPORTS.includes(data.origin)) {
            result.innerHTML = `
                <div style="color: red; font-weight: bold;">
                    ⚠️ El aeropuerto de origen no es válido.
                </div>
            `;
            result.classList.add("is-visible");
            return;
        }

        if (!VALID_AIRPORTS.includes(data.destination)) {
            result.innerHTML = `
                <div style="color: red; font-weight: bold;">
                    ⚠️ El aeropuerto de destino no es válido.
                </div>
            `;
            result.classList.add("is-visible");
            return;
        }

        if (!data.time || !/^\d{2}:\d{2}$/.test(data.time)) {
            result.innerHTML = `
                <div style="color: red; font-weight: bold;">
                    ⚠️ Hora inválida.
                </div>
            `;
            result.classList.add("is-visible");
            return;
        }

        if (!data.date || isNaN(new Date(data.date).getTime())) {
            result.innerHTML = `
                <div style="color: red; font-weight: bold;">
                    ⚠️ Fecha inválida.
                </div>
            `;
            result.classList.add("is-visible");
            return;
        }

        // Validaciones y transformaciones
        const [hour, minute] = data.time.split(':').map(Number);
        const timeInMinutes = hour * 60 + minute;
        //const numericLength = parseInt(data.distance, 10);
        const numericLength = distance;

        const dayOfWeek = getDayOfWeek(data.date);

        // const payload = { //fastapi payload
        //     Airline: data.aeroline,
        //     AirportFrom: data.origin,
        //     AirportTo: data.destination,
        //     Time: timeInMinutes,
        //     Length: numericLength,
        //     DayOfWeek: dayOfWeek
        // };
        const payload = { //java spring payload
            aerolinea: data.aerolinea,
            origen: data.origin,
            destino: data.destination,
            fechaDePartida: `${data.date}T${data.time}:00`,
            distancia: numericLength
        };

        //console.log('Payload:', payload);

    //try {
        const response = await fetch(
                //'http://localhost:8000/predict'// ruta fastapi
                'http://localhost:8080/flights/predict' //ruta java spring
            , {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error('Error al obtener la predicción. Por favor, inténtelo de nuevo más tarde.');
        }

        const resData = await response.json();
        //console.log(resData);
        //java spring
        result.innerHTML = `
                <h2>✨ Resultados ✨</h2>
                <p>🔮 Predicción: </p>

                <table border="1" cellpadding="6">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Probabilidad de retraso</th>
                            <th>Resultado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>1</td>
                            <td>${(resData.probabilidad * 100).toFixed(2)}%</td>
                            <td>
                                ${resData.estado === "DELAYED"
                                    ? "⏱️ Retrasado"
                                    : "✅ Puntual"}
                            </td>
                        </tr>
                    </tbody>
                </table>
            
            `;
        //fastapi
        // result.innerHTML = ` 
        //         <h2>✨ Resultados ✨</h2>
        //         <p>🔮 Predicción: </p>

        //         <table border="1" cellpadding="6">
        //             <thead>
        //                 <tr>
        //                     <th>#</th>
        //                     <th>Probabilidad de retraso</th>
        //                     <th>Resultado</th>
        //                 </tr>
        //             </thead>
        //             <tbody>
        //                 <tr>
        //                     <td>1</td>
        //                     <td>${(resData.probability * 100).toFixed(2)}%</td>
        //                     <td>
        //                         ${resData.prediction === 1
        //                             ? "⏱️ Retrasado"
        //                             : "✅ Puntual"}
        //                     </td>
        //                 </tr>
        //             </tbody>
        //         </table>
            
        //     `;
        result.classList.add("is-visible");
    } catch (error) {
        console.error(error);

        // Diferenciar errores de red (microservicio caído) de otros errores
        if (error instanceof TypeError && error.message.includes("Failed to fetch")) {
            result.innerHTML = `
                <div style="color: red; font-weight: bold;">
                    ❌ No se pudo conectar con el servidor.<br>
                    Por favor, inténtalo nuevamente más tarde.
                </div>
            `;
        } else {
            result.innerHTML = `
                <div style="color: red; font-weight: bold;">
                    ⚠️ Error al obtener la predicción.<br>
                    Intenta nuevamente más tarde.
                </div>
            `;
        }

        result.classList.add("is-visible");
    }
});
