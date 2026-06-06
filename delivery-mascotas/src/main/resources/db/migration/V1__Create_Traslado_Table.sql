
CREATE TABLE traslado (
    id_traslado BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_paciente BIGINT NOT NULL COMMENT 'ID del paciente (animal)',
    id_trabajador BIGINT NOT NULL COMMENT 'ID del trabajador asignado',
    direccion_hogar VARCHAR(200) NOT NULL COMMENT 'Dirección del hogar del paciente',
    hora_recogida TIMESTAMP NOT NULL COMMENT 'Hora programada para la recogida',
    estado VARCHAR(20) NOT NULL COMMENT 'Estado del traslado (PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO)'
);


CREATE INDEX idx_traslado_id_paciente ON traslado(id_paciente);
CREATE INDEX idx_traslado_id_trabajador ON traslado(id_trabajador);
CREATE INDEX idx_traslado_estado ON traslado(estado);
CREATE INDEX idx_traslado_hora_recogida ON traslado(hora_recogida);
CREATE INDEX idx_traslado_estado_trabajador ON traslado(estado, id_trabajador);

INSERT INTO traslado (id_paciente, id_trabajador, direccion_hogar, hora_recogida, estado) VALUES
(101, 1, 'Av. Uno Oriente 340, Viña del Mar', '2026-05-20 08:30:00', 'COMPLETADO'),
(102, 2, 'Subida Ecuador 55, Valparaíso', '2026-05-20 10:15:00', 'EN_PROGRESO'),
(103, 1, 'Calle Valparaíso 820, Viña del Mar', '2026-05-20 14:00:00', 'PENDIENTE'),
(104, 3, 'Los Carrera 1140, Quilpué', '2026-05-20 16:45:00', 'PENDIENTE'),
(105, 2, 'Av. Marina 200, Viña del Mar', '2026-05-20 19:30:00', 'CANCELADO');