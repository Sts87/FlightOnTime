CREATE TABLE flights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aerolinea VARCHAR(100) NOT NULL,
    origen VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    fecha_de_partida DATETIME NOT NULL,
    distancia INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    CONSTRAINT chk_distancia CHECK (distancia >= 0)
);