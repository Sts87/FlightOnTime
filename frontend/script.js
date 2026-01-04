const form = document.getElementById('flightForm');
const result = document.getElementById('result');

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = new FormData(form);
    const payload = Object.fromEntries(formData.entries());

    try {
        const response = await fetch('http://127.0.0.1:8000/predict', {
            method: 'POST',
            body: JSON.stringify(payload),
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Error al obtener la predicción. Por favor, inténtelo de nuevo más tarde.');
        }

        const data = await response.json();
        console.log(data);
        result.innerHTML = `
            <h2>Resultados</h2>
            <p>Predicción: ${data.prediction}</p>
            <p>Probabilidad: ${data.probability}</p>
            <p>Umbral: ${data.threshold}</p>
        `;
    } catch (error) {
        console.error(error);
        result.textContent = 'Error al obtener la predicción. Por favor, inténtelo de nuevo más tarde.';
    }

});