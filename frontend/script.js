const form = document.getElementById('flightForm');
const result = document.getElementById('result');
const csvInput = document.getElementById("csvFile");
const formInputs = document.querySelectorAll("#flightForm input:not(#csvFile)");



// function getTimeDay(hour) {
//     if (hour >= 6 && hour < 12) return 'Morning';
//     if (hour >= 12 && hour < 18) return 'Afternoon';
//     if (hour >= 18 && hour < 24) return 'Evening';
//     return 'Night';
// }

// function categorizeDuration(duration) {
//     if (duration <= 60) return 'Short';
//     if (duration <= 180) return 'Medium';
//     if (duration <= 960) return 'Long';
//     return 'Very Long';
// }

function getDayOfWeek(dateString) {
    const date = new Date(dateString);
    return date.getDay() === 0 ? 7 : date.getDay();
}

csvInput.addEventListener("change", () => {
    const hasCsv = csvInput.files.length > 0;

    formInputs.forEach(input => {
        input.disabled = hasCsv;
    });
});


form.addEventListener('submit', async (e) => {
    e.preventDefault();

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
                "http://127.0.0.1:8000/predict/csv",
                {
                    method: "POST",
                    body: csvFormData
                }
            );

            if (!response.ok) {
                throw new Error("Error al procesar el CSV");
            }

            const resData = await response.json();

            result.innerHTML = `
                <h2>📄 Predicción desde CSV</h2>
                <p>Total de filas procesadas: ${resData.total}</p>
                <p>✔️ Predicciones generadas correctamente</p>
            `;

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

        // const hour = parseInt(data.time.split(':')[0], 10);
        // const minute = parseInt(data.time.split(':')[1], 10);
        //const numericTime = hour * 100 + minute;

        const numericLength = parseInt(data.distance, 10);
        //const timeDay = getTimeDay(hour);
        //const durationCategory = categorizeDuration(numericLength);
        const dayOfWeek = getDayOfWeek(data.date);

        const payload = {
            Airline: data.aeroline,
            AirportFrom: data.origin,
            AirportTo: data.destination,
            Time: timeInMinutes,
            Length: numericLength,
            //TimeDay: timeDay,
            //Duration: durationCategory,
            DayOfWeek: dayOfWeek
        };
        console.log('Payload:', payload);

    //try {
        const response = await fetch('http://localhost:8080/predict', {
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
            <p>🔮 Predicción: ${resData.prediction}</p>
            <p>✈️ Probabilidad: ${resData.probability}</p>
        `;
    } catch (error) {
        console.error(error);
        result.textContent = 'Error al obtener la predicción. Por favor, inténtelo de nuevo más tarde.';
    }

});