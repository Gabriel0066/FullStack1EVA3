# Personal Médico Microservice

Microservicio para la gestión del personal médico de la veterinaria.

## Descripción

Este microservicio permite gestionar la información del personal médico que trabaja en la veterinaria, incluyendo veterinarios, asistentes, recepcionistas y conductores.

## Tecnologías Utilizadas

- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistencia de datos
- **Spring Web** - API REST
- **Spring Validation** - Validación de datos
- **Spring Cloud OpenFeign** - Comunicación entre microservicios
- **Flyway** - Migraciones de base de datos
- **MySQL** - Base de datos relacional
- **Maven** - Gestión de dependencias

## Arquitectura

El microservicio sigue el patrón **CSR (Controller-Service-Repository)**:

```
controller/    - Endpoints REST
service/        - Lógica de negocio
repository/     - Acceso a datos
model/          - Entidades JPA
exception/      - Manejo de excepciones
config/         - Configuración global
```

## Entidades

### Personal

| Campo | Tipo | Descripción |
|--------|------|-------------|
| id_trabajador | Long | ID único del trabajador (PK) |
| rol | String(50) | Rol que cumple dentro del local |
| nombre | String(100) | Nombre del trabajador |
| apellido | String(100) | Apellido del trabajador |
| rut | String(12) | RUT del trabajador (único) |
| correo | String(100) | Correo electrónico (único) |
| telefono | String(15) | Número de teléfono |
| direccion | String(200) | Dirección del trabajador |
| fecha_creacion | Timestamp | Fecha de creación |
| fecha_actualizacion | Timestamp | Fecha de actualización |

## API Endpoints

### Personal

#### Obtener Personal
- `GET /api/v1/personal` - Obtener todo el personal
- `GET /api/v1/personal/{id}` - Obtener personal por ID
- `GET /api/v1/personal/rut/{rut}` - Obtener personal por RUT
- `GET /api/v1/personal/correo/{correo}` - Obtener personal por correo
- `GET /api/v1/personal/rol/{rol}` - Obtener personal por rol
- `GET /api/v1/personal/buscar?termino={termino}` - Buscar por nombre o apellido
- `GET /api/v1/personal/rol/{rol}/buscar?termino={termino}` - Buscar por rol y nombre/apellido

#### Crear/Actualizar Personal
- `POST /api/v1/personal` - Crear nuevo personal
- `PUT /api/v1/personal/{id}` - Actualizar personal existente

#### Eliminar Personal
- `DELETE /api/v1/personal/{id}` - Eliminar personal por ID

#### Verificaciones
- `GET /api/v1/personal/exists/rut/{rut}` - Verificar si existe RUT
- `GET /api/v1/personal/exists/correo/{correo}` - Verificar si existe correo

## Validaciones

- **RUT**: Formato XXXXXXX-X o XXXXXXXX-X
- **Correo**: Formato de email válido
- **Teléfono**: 8-15 dígitos, puede incluir +
- **Nombre/Apellido**: Solo letras y espacios
- **Rol**: Máximo 50 caracteres
- **Dirección**: Máximo 200 caracteres

## Configuración

### Base de Datos
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/veterinaria_personal
spring.datasource.username=root
spring.datasource.password=password
```

### Servidor
```properties
server.port=8081
```

## Ejecución

1. **Crear base de datos**:
```sql
CREATE DATABASE veterinaria_personal;
```

2. **Ejecutar aplicación**:
```bash
mvn spring-boot:run
```

3. **Acceder a la API**:
```
http://localhost:8081/api/v1/personal
```

## Ejemplos de Uso

### Crear Personal
```json
POST /api/v1/personal
{
    "rol": "Veterinario",
    "nombre": "Juan",
    "apellido": "Pérez",
    "rut": "12345678-9",
    "correo": "juan.perez@veterinaria.cl",
    "telefono": "+56912345678",
    "direccion": "Calle Principal 123, Santiago"
}
```

### Buscar Personal
```bash
GET /api/v1/personal/buscar?termino=Juan
```

## Logs

La aplicación genera logs estructurados con información detallada de las operaciones:
- Nivel: DEBUG para desarrollo
- Formato: Timestamp + Nivel + Mensaje
- Archivo: logs/application.log

## Manejo de Errores

La API retorna respuestas de error consistentes:

```json
{
    "timestamp": "2026-05-10T15:30:00",
    "status": 404,
    "error": "Recurso No Encontrado",
    "message": "Personal no encontrado con ID: 1",
    "path": "/api/v1/personal/1"
}
```

## Integración con otros Microservicios

Este microservicio expone endpoints para ser consumidos por otros microservicios a través de OpenFeign:
- Validación de personal por ID
- Verificación de RUT y correo

## Métricas

Se exponen métricas a través de Actuator:
- Health: `/actuator/health`
- Info: `/actuator/info`
- Metrics: `/actuator/metrics`
