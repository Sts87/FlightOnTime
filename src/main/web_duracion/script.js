let airports_arr = [];
const airports_avg_url = "https://raw.githubusercontent.com/Sts87/FlightOnTime/feature/backend/src/main/resources/airports_length_avg.json"


async function cargarAirportsData() {
    try {
        const response = await fetch(airports_avg_url);
        airports_arr = await response.json();
        console.log('Airports data loaded:', airports_arr);
        return airports_arr;
    } catch (error) {
        console.error('Error loading airports data:', error);
    }
}


function printDuracion() {
    const txtDuracion = document.getElementById('duracion');
    let origen = document.getElementById('origen').value.trim().toUpperCase();
    let destino = document.getElementById('destino').value.trim().toUpperCase();
    
    let duracion = buscarDuracion(origen, destino);
    txtDuracion.value = duracion;
}


function buscarDuracion(origen, destino) {
    let duracion = airports_arr.find(airport => 
        airport.AirportFrom === origen && airport.AirportTo === destino
    );
    
    duracion = duracion ? Math.round(duracion.LengthAvg) : 0;
    return duracion;
}


document.addEventListener('DOMContentLoaded', function() {
    console.log('Flight On Time loaded');
    cargarAirportsData();
});