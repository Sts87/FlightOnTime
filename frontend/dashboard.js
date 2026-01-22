// ========================================
// CONFIGURACIÓN Y CONSTANTES
// ========================================

//const API = "http://localhost:8080"; // localhost para desarrollo
const API = "https://fly-on-time-production.up.railway.app"; // production
const REFRESH_INTERVAL = 5000;

// 🗃️ Estado anterior (para comparar)
let lastStats = null;
let lastAirlines = null;
let lastRecent = null;

// 🔍 API REST
async function apiGet(path) {
  const res = await fetch(`${API}${path}`);
  if (!res.ok) throw new Error(`Error ${res.status}`);
  return res.json();
}

// 🔍 Adaptar datos de estadísticas
function adaptStats(data) {
  return {
    total: data.totalConsultas,
    delayedPct: data.porcentajeRetrasados,
    onTimePct: data.porcentajePuntuales
  };
}

// 🔍 Adaptar datos de predicciones recientes
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

// 🔍 Renderizar gráfico de donut
function renderDonutChart(containerId, delayed, onTime, title) {
  const el = document.getElementById(containerId);
  el.innerHTML = "";

  // Si no hay datos, mostrar mensaje
  if ((delayed + onTime) === 0) {
    el.innerHTML = `<p style="opacity:.6">Sin datos disponibles por el momento para consultas de el día de hoy.</p>`;
    return;
  }

  // Opciones de gráfico
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

  // Renderizar gráfico
  new ApexCharts(el, options).render();
}

// 🔍 Renderizar tabla scrollable
function renderScrollableTable(containerId, headers, rowsHtml) {
  const container = document.getElementById(containerId);

  // Renderizar tabla
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

// 🔍 Renderizar tabla de aerolíneas
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

  // Renderizar tabla
  renderScrollableTable(
    containerId,
    ["Aerolínea", "Total", `% ${mode === "puntuales" ? "Puntualidad" : "Retraso"}`],
    rows
  );
}

// 🔍 Comparar dos objetos, datos recientes y actuales
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

  // Si cambió, renderizar gráficos
  if (todayChanged || allChanged) {
    renderDonutChart(
      "chartToday",
      today.delayedPct,
      today.onTimePct,
      "Hoy"
    );

    // Renderizar gráfico de historial
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

  // Si no hay datos, mostrar mensaje
  if (!res.topPuntuales || !res.topRetrasadas) {
    console.warn("Formato inesperado stats/airlines:", res);
    return;
  }

  // 🆚 Comparar
  const airlinesChanged = !lastAirlines || !deepEqual(lastAirlines, res);

  // Si cambió, renderizar tablas
  if (airlinesChanged) {
    renderAirlinesTable(
      "airlinesPuntuales",
      res.topPuntuales,
      "puntuales"
    );

    // Renderizar tabla de retrasos
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

  // Si cambió, renderizar tabla
  if (recentChanged) {
    const rows = flights.map(f => `
      <tr>
        <td>${f.route}</td>
        <td>${f.airline}</td>
        <td>${f.delayed ? "⏱️ Retrasado" : "✅ Puntual"}</td>
        <td>${f.time}</td>
      </tr>
    `).join("");

    // Renderizar tabla
    renderScrollableTable(
      "recentTable",
      ["Ruta", "Aerolínea", "Retraso", "Hora de partida"],
      rows
    );

    // 📦 Guardar nuevo estado
    lastRecent = flights;
  }
}

// 🔄 Cargar y comparar estadísticas, predicciones recientes y aerolíneas
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

// Cargar y actualizar
loadDashboard();
// Actualizar cada 5 segundos
setInterval(loadDashboard, REFRESH_INTERVAL);