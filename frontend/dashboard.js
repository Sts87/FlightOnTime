const API = "http://localhost:8080";
const REFRESH_INTERVAL = 15000;

async function apiGet(path) {
    const res = await fetch(`${API}${path}`);
    if (!res.ok) throw new Error(`Error ${res.status}`);
    return res.json();
}

function adaptStats(data) {
    return {
        total: data.totalConsultas,
        delayedPct: data.porcentajeRetrasados,
        onTimePct: data.porcentajePuntuales
    };
}

function adaptRecent(data) {
    return data.map(f => ({
        route: `${f.origen} → ${f.destino}`,
        airline: f.aerolinea,
        delayed: f.estado === "Retrasado",
        porcentajeConfianza: f.porcentajeConfianza,
        time: f.fechaConsulta
            ? new Date(f.fechaConsulta).toLocaleTimeString()
            : "-"
    }));
}

function renderDonutChart(containerId, delayed, onTime, title) {
    const el = document.getElementById(containerId);
    el.innerHTML = "";

    if ((delayed + onTime) === 0) {
        el.innerHTML = `<p style="opacity:.6">Sin datos disponibles</p>`;
        return;
    }

    const options = {
        chart: {
            type: "donut",
            foreColor: "#f6f6f6",
            //foreColor: "#060606",
            height: 320
        },
        series: [delayed, onTime],
        labels: ["Retrasados", "Puntuales"],
        title: { text: title },
        dataLabels: {
            formatter: v => v.toFixed(1) + "%"
        }
    };

    new ApexCharts(el, options).render();
}

function renderScrollableTable(containerId, headers, rowsHtml) {
    const container = document.getElementById(containerId);

    container.innerHTML = `
        <div class="table-container">
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

async function loadStats() {
    const [todayRaw, allRaw] = await Promise.all([
        apiGet("/flights/stats"),
        apiGet("/flights/stats/all")
    ]);

    const today = adaptStats(todayRaw);
    const all = adaptStats(allRaw);

    renderDonutChart(
        "chartToday",
        today.delayedPct,
        today.onTimePct,
        "Hoy"
    );

    renderDonutChart(
        "chartAll",
        all.delayedPct,
        all.onTimePct,
        "Histórico"
    );
}

function renderAirlinesTable(containerId, airlines, mode) {
    const rows = airlines.map(a => `
        <tr>
            <td>${a.aerolinea}</td>
            <td>${a.totalVuelos}</td>
            <td>${
                mode === "puntuales"
                    ? a.porcentajePuntuales.toFixed(1)
                    : a.porcentajeRetrasados.toFixed(1)
            }%</td>
        </tr>
    `).join("");

    renderScrollableTable(
        containerId,
        ["Aerolínea", "Total", `% ${mode === "puntuales" ? "Puntualidad" : "Retraso"}`],
        rows
    );
}

async function loadAirlines() {
    const res = await apiGet("/flights/stats/airlines");

    if (!res.topPuntuales || !res.topRetrasadas) {
        console.warn("Formato inesperado stats/airlines:", res);
        return;
    }

    renderAirlinesTable(
        "airlinesPuntuales",
        res.topPuntuales,
        "puntuales"
    );

    renderAirlinesTable(
        "airlinesRetrasadas",
        res.topRetrasadas,
        "retrasadas"
    );
}

async function loadRecent() {
    const raw = await apiGet("/flights/stats/recent");
    const flights = adaptRecent(raw);

    const rows = flights.map(f => `
        <tr>
            <td>${f.route}</td>
            <td>${f.airline}</td>
            <td>${f.delayed ? "⏱ Sí" : "✔ No"}</td>
            <td>${f.time}</td>
        </tr>
    `).join("");

    renderScrollableTable(
        "recentTable",
        ["Ruta", "Aerolínea", "Retraso", "Hora"],
        rows
    );
}

async function loadDashboard() {
    try {
        await Promise.all([
        loadStats(),
        loadAirlines(),
        loadRecent()
        ]);
    } catch (err) {
        console.error("Dashboard error:", err);
    }
}

loadDashboard();
setInterval(loadDashboard, REFRESH_INTERVAL);
