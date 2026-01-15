const form = document.getElementById('flightForm');
const result = document.getElementById('result');
const csvInput = document.getElementById("csvFile");
const formInputs = document.querySelectorAll("#flightForm input:not(#csvFile)");

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


// csvInput.addEventListener("change", () => {
//     const hasCsv = csvInput.files.length > 0;

//     formInputs.forEach(input => {
//         input.disabled = hasCsv;
//     });
// });

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
                "http://localhost:8000/predict/csv", //ruta fastapi
                // "http://localhost:8080/predict/csv", //ruta java spring
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

            resData.predictions.forEach((pred, index) => {
                predictionsHtml += `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${(pred.probability * 100).toFixed(2)}%</td>
                        <td>${pred.prediction === 1 ? "⏱️ Retrasado" : "✅ Puntual"}</td>
                    </tr>
                `;
            });

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

        // Validaciones y transformaciones
        const [hour, minute] = data.time.split(':').map(Number);
        const timeInMinutes = hour * 60 + minute;
        const numericLength = parseInt(data.distance, 10);
        const dayOfWeek = getDayOfWeek(data.date);

        const payload = {
            Airline: data.aeroline,
            AirportFrom: data.origin,
            AirportTo: data.destination,
            Time: timeInMinutes,
            Length: numericLength,
            DayOfWeek: dayOfWeek
        };
        console.log('Payload:', payload);

    //try {
        const response = await fetch(
                'http://localhost:8000/predict'// ruta fastapi
                //'http://localhost:8080/predict' //ruta java spring
            , {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error('Error al obtener la predicción. Por favor, inténtelo de nuevo más tarde.');
        }

        const resData = await response.json();
        console.log(resData);
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
                            <td>${(resData.probability * 100).toFixed(2)}%</td>
                            <td>
                                ${resData.prediction === 1
                                    ? "⏱️ Retrasado"
                                    : "✅ Puntual"}
                            </td>
                        </tr>
                    </tbody>
                </table>
            
            `;
        result.classList.add("is-visible");
    } catch (error) {
        console.error(error);
        result.textContent = 'Error al obtener la predicción. Por favor, inténtelo de nuevo más tarde.';
    }
});