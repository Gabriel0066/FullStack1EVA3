# 🐾 FullStack1EVA3 — Sistema de Gestión Veterinaria (Microservicios)

Sistema backend para una clínica veterinaria compuesto por dos microservicios Spring Boot que se comunican entre sí vía **OpenFeign**.

---

## 📦 Microservicios

| Microservicio | Puerto | Descripción |
|---|---|---|
| **`personal-medico`** | `8081` | Gestión del personal médico (veterinarios, asistentes, recepcionistas, conductores) |
| **`delivery-mascotas`** | `8082` | Gestión de traslados (recogida y devolución de mascotas a domicilio) |

**`delivery-mascotas`** consume la API de **`personal-medico`** para validar que un trabajador existe antes de asignarle un traslado.

---

## 🛠️ Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.0 |
| Spring Cloud OpenFeign | 2023.0.0 |
| Spring Data JPA / Hibernate | — |
| Spring Security (HTTP Basic) | — |
| Spring HATEOAS | — |
| Flyway | — |
| MySQL | 8.x |
| Lombok | 1.18.30 |
| springdoc-openapi (Swagger) | 2.1.0 |
| JUnit 5 + Mockito | — |
| Maven | 3.11.0+ |

---

## 📁 Estructura del Proyecto

```
FullStack1EVA3/
├── personal-medico/           # Microservicio 1
│   ├── pom.xml
│   └── src/main/java/com/veterinaria/personalmedico/
│       ├── PersonalMedicoApplication.java
│       ├── SecurityConfig.java
│       ├── controller/PersonalController.java
│       ├── dto/PersonalDTO.java
│       ├── model/Personal.java
│       ├── repository/PersonalRepository.java
│       └── service/PersonalService.java
├── delivery-mascotas/         # Microservicio 2
│   ├── pom.xml
│   └── src/main/java/com/veterinaria/deliverymascotas/
│       ├── DeliveryMascotasApplication.java
│       ├── SecurityConfig.java
│       ├── client/PersonalClient.java         # Feign client
│       ├── config/FeignClientConfig.java
│       ├── controller/TrasladoController.java
│       ├── dto/PersonalDTO.java
│       ├── model/Traslado.java
│       ├── repository/TrasladoRepository.java
│       └── service/TrasladoService.java
└── README.md
```

---

## ⚙️ Prerrequisitos

- **Java 17 JDK**
- **Maven** (o usar `mvnw`)
- **MySQL Server** corriendo en `localhost:3306`
- **Lombok** configurado en el IDE

---

## 🚀 Ejecución

### 1. Crear las bases de datos

```sql
CREATE DATABASE veterinaria_personal;
CREATE DATABASE veterinaria_delivery;
```

### 2. Configurar credenciales de MySQL

En ambos `application.properties` (`personal-medico/src/main/resources/` y `delivery-mascotas/src/main/resources/`), ajustar si es necesario:

```properties
spring.datasource.username=root
spring.datasource.password=tu_password
```

### 3. Iniciar los microservicios (dos terminales)

**Terminal 1 — Personal Médico:**
```bash
cd personal-medico
mvn spring-boot:run
```

**Terminal 2 — Delivery Mascotas:**
```bash
cd delivery-mascotas
mvn spring-boot:run
```

> ⚠️ **Importante:** `personal-medico` debe estar corriendo antes que `delivery-mascotas`, ya que este último lo consulta al crear traslados.

### 4. Construir JARs (alternativa)

```bash
cd personal-medico && mvn clean package -DskipTests
cd ../delivery-mascotas && mvn clean package -DskipTests
java -jar personal-medico/target/personal-medico-microservice-1.0.0.jar
java -jar delivery-mascotas/target/delivery-mascotas-microservice-1.0.0.jar
```

---

## 🔐 Seguridad

Ambos microservicios usan **HTTP Basic Auth** con credenciales fijas en memoria:

- **Usuario:** `admin`
- **Contraseña:** `admin123`

Endpoints públicos (sin autenticación):
- `/actuator/health`, `/actuator/info`
- `/swagger-ui/**`, `/v3/api-docs/**`
- `/api/v1/personal/exists/**` (solo en personal-medico)

---

## 📡 API — Personal Médico (`localhost:8081`)

Base URL: `http://localhost:8081/api/v1/personal`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Listar todo el personal |
| `GET` | `/{id}` | Obtener personal por ID |
| `POST` | `/` | Crear nuevo personal |
| `PUT` | `/{id}` | Actualizar personal |
| `DELETE` | `/{id}` | Eliminar personal |
| `GET` | `/exists/id/{id}` | Verificar si existe por ID |
| `GET` | `/exists/rut/{rut}` | Verificar si existe por RUT |
| `GET` | `/exists/correo/{correo}` | Verificar si existe por correo |
| `POST` | `/migrate` | Ejecutar migraciones Flyway manualmente |

### Entidad Personal

| Campo | Tipo | Descripción |
|---|---|---|
| idTrabajador | Long | ID único |
| rol | String | Rol (veterinario, asistente, etc.) |
| nombre | String | Nombre |
| apellido | String | Apellido |
| rut | String | RUT único (formato XXXXXXX-X) |
| correo | String | Correo único |
| telefono | String | Teléfono (8-15 dígitos, + opcional) |
| direccion | String | Dirección (máx. 200 caracteres) |

---

## 📡 API — Delivery Mascotas (`localhost:8082`)

Base URL: `http://localhost:8082/api/v1/traslados`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Listar todos los traslados |
| `GET` | `/{id}` | Obtener traslado por ID |
| `POST` | `/` | Crear traslado |
| `PUT` | `/{id}/estado/{nuevoEstado}` | Actualizar estado |
| `DELETE` | `/{id}` | Eliminar traslado |
| `GET` | `/estadisticas/estado/{estado}` | Contar por estado |
| `GET` | `/estadisticas/trabajador/{id}/estado/{estado}` | Contar por trabajador + estado |
| `POST` | `/migrate` | Ejecutar migraciones Flyway manualmente |

### Estados del Traslado

- `PENDIENTE` — Programado, no iniciado
- `EN_PROGRESO` — En curso
- `COMPLETADO` — Finalizado (registra auto. hora de entrega)
- `CANCELADO` — Cancelado (solo si está en PENDIENTE)

### Entidad Traslado

| Campo | Tipo | Descripción |
|---|---|---|
| idTraslado | Long | ID único |
| idPaciente | Long | ID del paciente (mascota) |
| idTrabajador | Long | ID del trabajador asignado |
| direccionHogar | String | Dirección de recogida/entrega |
| horaRecogida | Timestamp | Hora programada |
| horaEntrega | Timestamp | Hora real de entrega |
| estado | String | Estado del traslado |
| fechaCreacion | Timestamp | Fecha de creación |

### Reglas de Negocio

1. El trabajador asignado debe existir en `personal-medico` (validación vía Feign)
2. La hora de recogida no puede ser anterior a la fecha/hora actual
3. Al marcar `COMPLETADO` se registra automáticamente la hora de entrega
4. Solo se pueden cancelar traslados en estado `PENDIENTE`

---

## 📖 Swagger UI

- Personal Médico: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- Delivery Mascotas: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

---

## 🩺 Health Check (Actuator)

- `http://localhost:8081/actuator/health`
- `http://localhost:8082/actuator/health`

---

## 🧪 Tests

Cada microservicio incluye tests unitarios con **Mockito** para la capa de servicio:

```bash
cd personal-medico
mvn test

cd ../delivery-mascotas
mvn test
```

- `PersonalServiceTest.java` — 13 casos (CRUD, duplicados, validaciones)
- `TrasladoServiceTest.java` — 11 casos (CRUD, transiciones de estado, validaciones)

---

## 📝 Ejemplos de Uso

### Crear personal médico

```bash
curl -u admin:admin123 -X POST http://localhost:8081/api/v1/personal \
  -H "Content-Type: application/json" \
  -d '{
    "rol": "Veterinario",
    "nombre": "Carlos",
    "apellido": "Muñoz",
    "rut": "12345678-9",
    "correo": "carlos@vet.cl",
    "telefono": "+56912345678",
    "direccion": "Av. Siempre Viva 742"
  }'
```

### Crear un traslado

```bash
curl -u admin:admin123 -X POST http://localhost:8082/api/v1/traslados \
  -H "Content-Type: application/json" \
  -d '{
    "idPaciente": 1,
    "idTrabajador": 4,
    "direccionHogar": "Calle Las Flores 123, Providencia",
    "horaRecogida": "2026-06-10T09:00:00",
    "estado": "PENDIENTE"
  }'
```

### Actualizar estado de un traslado

```bash
curl -u admin:admin123 -X PUT "http://localhost:8082/api/v1/traslados/1/estado/EN_PROGRESO"
```

---

## 📄 Logs

Ambos microservicios generan logs a nivel DEBUG en consola y archivo (`logs/application.log`).

---

## 🖥️ Requisitos del IDE

Si usas VS Code, el archivo `.vscode/settings.json` ya incluye:

```json
{
  "java.compile.nullAnalysis.mode": "automatic",
  "java.configuration.updateBuildConfiguration": "interactive",
  "java.debug.settings.onBuildFailureProceed": true
}
```

Asegúrate de tener instalada la **extensión de Lombok** en tu IDE.
