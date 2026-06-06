CREATE TABLE personal (
    id_trabajador BIGINT AUTO_INCREMENT PRIMARY KEY,
    rol VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rut VARCHAR(12) NOT NULL UNIQUE,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(15) NOT NULL,
    direccion VARCHAR(200) NOT NULL
);

CREATE INDEX idx_personal_rol ON personal(rol);
CREATE INDEX idx_personal_nombre ON personal(nombre);
CREATE INDEX idx_personal_apellido ON personal(apellido);
CREATE INDEX idx_personal_rut ON personal(rut);
CREATE INDEX idx_personal_correo ON personal(correo);

INSERT INTO personal (id_trabajador, rol, nombre, apellido, rut, correo, telefono, direccion) VALUES
(1, 'Veterinario', 'Carlos', 'Mendoza', '12345678-9', 'carlos.mendoza@veterinaria.com', '+56911112222', 'Av. Libertad 450, Viña del Mar'),
(2, 'Asistente', 'Ana', 'Silva', '18765432-1', 'ana.silva@veterinaria.com', '+56933334444', 'Calle Los Alerces 12, Valparaíso'),
(3, 'Veterinario Urgencias', 'Pedro', 'Gómez', '15987654-K', 'pedro.gomez@veterinaria.com', '+56955556666', 'Pasaje El Sol 404, Quilpué'),
(4, 'Cirujano', 'María José', 'Herrera', '14321987-6', 'mariajose.herrera@veterinaria.com', '+56977778888', 'Álvarez 1520, Viña del Mar');