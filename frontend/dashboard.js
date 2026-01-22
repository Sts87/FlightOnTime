const API = "http://localhost:8080";
const REFRESH_INTERVAL = 5000;

// 🗃️ Estado anterior (para comparar)
let lastStats = null;
let lastAirlines = null;
let lastRecent = null;

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

// 🔍 Comparar dos objetos (simple, para este caso)
function deepEqual(obj1, obj2) {
  if (obj1 === obj2) return true;
  if (obj1 == null || obj2 == null) return false;
  if (typeof obj1 !== 'object' || typeof obj2 !== 'object') return false;

  const keys1 = Object.keys(obj1);
  const keys2 = Object.keys(obj2);

  if (keys1.length !== keys2.length) return false;

  for (let key of keys1) {
    if (!keys2.includes(key)) return false;
    if (!deepEqual(obj1[key], obj2[key])) return false;
  }

  return true;
}

// 🔄 Cargar y comparar stats
async function loadStats() {
  const [todayRaw, allRaw] = await Promise.all([
    apiGet("/flights/stats"),
    apiGet("/flights/stats/all")
  ]);

  const today = adaptStats(todayRaw);
  const all = adaptStats(allRaw);

  // 🆚 Comparar con último estado
  const todayChanged = !lastStats || !deepEqual(lastStats.today, today);
  const allChanged = !lastStats || !deepEqual(lastStats.all, all);

  if (todayChanged || allChanged) {
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

    // 📦 Guardar nuevo estado
    lastStats = { today, all };
  }
}

// 🔄 Cargar y comparar airlines
async function loadAirlines() {
  const res = await apiGet("/flights/stats/airlines");

  if (!res.topPuntuales || !res.topRetrasadas) {
    console.warn("Formato inesperado stats/airlines:", res);
    return;
  }

  // 🆚 Comparar
  const airlinesChanged = !lastAirlines || !deepEqual(lastAirlines, res);

  if (airlinesChanged) {
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

    // 📦 Guardar nuevo estado
    lastAirlines = res;
  }
}

// 🔄 Cargar y comparar recent
async function loadRecent() {
  const raw = await apiGet("/flights/stats/recent");
  const flights = adaptRecent(raw);

  // 🆚 Comparar (array de objetos)
  const recentChanged = !lastRecent || !deepEqual(lastRecent, flights);

  if (recentChanged) {
    const rows = flights.map(f => `
      <tr>
        <td>${f.route}</td>
        <td>${f.airline}</td>
        <td>${f.delayed ? "⏱️ Retrasado" : "✅ Puntual"}</td>
        <td>${f.time}</td>
      </tr>
    `).join("");

    renderScrollableTable(
      "recentTable",
      ["Ruta", "Aerolínea", "Retraso", "Hora de partida"],
      rows
    );

    // 📦 Guardar nuevo estado
    lastRecent = flights;
  }
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