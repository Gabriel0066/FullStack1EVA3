# Delivery de Mascotas Microservice

Microservicio para la gestión de delivery de mascotas de la veterinaria.

## Descripción

Este microservicio permite gestionar el servicio de delivery para recoger y devolver mascotas (pacientes) a sus hogares, asignando conductores y controlando el estado de cada traslado.

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
client/         - Clientes Feign para comunicación
dto/            - Objetos de transferencia de datos
```

## Entidades

### Traslado

| Campo | Tipo | Descripción |
|--------|------|-------------|
| id_traslado | Long | ID único del traslado (PK) |
| id_paciente | Long | ID del paciente (animal) |
| id_trabajador | Long | ID del trabajador asignado |
| direccion_hogar | String(200) | Dirección del hogar del paciente |
| hora_recogida | Timestamp | Hora programada para la recogida |
| hora_entrega | Timestamp | Hora real de entrega (opcional) |
| estado | String(20) | Estado del traslado |
| fecha_creacion | Timestamp | Fecha de creación |
| fecha_actualizacion | Timestamp | Fecha de actualización |

## Estados del Traslado

- **PENDIENTE**: Traslado programado pero no iniciado
- **EN_PROGRESO**: Conductor en camino o realizando el delivery
- **COMPLETADO**: Traslado finalizado exitosamente
- **CANCELADO**: Traslado cancelado

## API Endpoints

### Traslados

#### Obtener Traslados
- `GET /api/v1/traslados` - Obtener todos los traslados
- `GET /api/v1/traslados/{id}` - Obtener traslado por ID
- `GET /api/v1/traslados/trabajador/{idTrabajador}` - Obtener traslados por trabajador
- `GET /api/v1/traslados/paciente/{idPaciente}` - Obtener traslados por paciente
- `GET /api/v1/traslados/estado/{estado}` - Obtener traslados por estado
- `GET /api/v1/traslados/trabajador/{idTrabajador}/estado/{estado}` - Obtener traslados por trabajador y estado

#### Búsqueda por Fechas
- `GET /api/v1/traslados/fecha?fechaInicio={inicio}&fechaFin={fin}` - Obtener traslados por rango de fechas
- `GET /api/v1/traslados/estado/{estado}/fecha?fechaInicio={inicio}&fechaFin={fin}` - Obtener traslados por estado y rango de fechas

#### Crear/Actualizar Traslados
- `POST /api/v1/traslados` - Crear nuevo traslado
- `PUT /api/v1/traslados/{id}` - Actualizar traslado existente
- `PATCH /api/v1/traslados/{id}/estado?nuevoEstado={estado}` - Actualizar estado del traslado

#### Eliminar Traslados
- `DELETE /api/v1/traslados/{id}` - Eliminar traslado por ID

#### Estadísticas
- `GET /api/v1/traslados/estadisticas/estado/{estado}` - Contar traslados por estado
- `GET /api/v1/traslados/estadisticas/trabajador/{idTrabajador}/estado/{estado}` - Contar traslados por trabajador y estado

## Validaciones

- **ID Paciente**: Obligatorio, debe existir
- **ID Trabajador**: Obligatorio, debe existir en microservicio de personal
- **Dirección**: Obligatoria, máximo 200 caracteres
- **Hora Recogida**: Obligatoria, no puede ser anterior a la fecha actual
- **Estado**: Obligatorio, debe ser uno de los estados válidos

## Configuración

### Base de Datos
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/veterinaria_delivery
spring.datasource.username=root
spring.datasource.password=password
```

### Servidor
```properties
server.port=8082
```

## Ejecución

1. **Crear base de datos**:
```sql
CREATE DATABASE veterinaria_delivery;
```

2. **Ejecutar aplicación**:
```bash
mvn spring-boot:run
```

3. **Acceder a la API**:
```
http://localhost:8082/api/v1/traslados
```

## Ejemplos de Uso

### Crear Traslado
```json
POST /api/v1/traslados
{
    "idPaciente": 1,
    "idTrabajador": 4,
    "direccionHogar": "Calle Las Flores 123, Providencia",
    "horaRecogida": "2026-05-11T09:00:00",
    "estado": "PENDIENTE"
}
```

### Actualizar Estado
```bash
PATCH /api/v1/traslados/1/estado?nuevoEstado=COMPLETADO
```

### Buscar por Rango de Fechas
```bash
GET /api/v1/traslados/fecha?fechaInicio=2026-05-10T00:00:00&fechaFin=2026-05-12T23:59:59
```

## Integración con otros Microservicios

Este microservicio consume el microservicio de **Personal Médico** a través de OpenFeign:

```java
@FeignClient(name = "personal-medico-microservice", url = "http://localhost:8081/api")
public interface PersonalClient {
    @GetMapping("/v1/personal/{id}")
    PersonalDTO getPersonalById(@PathVariable("id") Long id);
    
    @GetMapping("/v1/personal/exists/rut/{rut}")
    boolean existsByRut(@PathVariable("rut") String rut);
}
```

## Reglas de Negocio

1. **Validación de Trabajador**: Antes de asignar un traslado, se valida que el trabajador exista
2. **Hora de Recogida**: No puede ser anterior a la fecha y hora actual
3. **Estado Final**: Al marcar como "COMPLETADO", se registra automáticamente la hora de entrega
4. **Cancelación**: Solo se pueden cancelar traslados en estado "PENDIENTE"

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
    "message": "Traslado no encontrado con ID: 1",
    "path": "/api/v1/traslados/1"
}
```

## Métricas

Se exponen métricas a través de Actuator:
- Health: `/actuator/health`
- Info: `/actuator/info`
- Metrics: `/actuator/metrics`

## Flujo de Trabajo Típico

1. **Solicitud**: Se crea un nuevo traslado con estado "PENDIENTE"
2. **Asignación**: Se asigna un conductor (trabajador) validado
3. **Ejecución**: Se actualiza estado a "EN_PROGRESO"
4. **Finalización**: Se actualiza estado a "COMPLETADO" con hora de entrega
5. **Seguimiento**: Se pueden consultar traslados por trabajador, paciente o estado
