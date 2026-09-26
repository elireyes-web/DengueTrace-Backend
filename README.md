# DengueTrace

## Descripción del Proyecto

Una aplicación móvil y web la cual muestre un mapa interactivo sobre los casos del Dengue en unos distritos en específicos que ayude a prevenir posibles rebrotes mediante datos y auto reportes.

---
## Tecnologías

| Herramienta         | Servicio |
|--------------------|---------|
| Lenguaje        | Java 21   |
| Framework    | SpringBoot 4.1.1.       |
| Base de Datos          | PostgreSQL 17     |
| Autenticación         | JwtServices 0.12.6 |
| Otros             | Lombok 1.18.46 |
---
## Configuración y arranque

### 1. Levantar la base de datos

```bash
docker compose up -d
```

Esto inicia un contenedor PostgreSQL 16 en el puerto `5432` con:

| Parámetro | Valor      |
|-----------|------------|
| Base de datos | `denguetrace-bd` |
| Usuario       | `postgres`  |
| Contraseña    | `postgres`  |

### 2. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080/api/v1`.

> **Nota:** `spring.jpa.hibernate.ddl-auto=create-drop` recrea el esquema en cada arranque.

---
## Variables de entorno

Incluido el archivo `.env.example` en la raíz del proyecto:

```env
DB_URL=jdbc:postgresql://localhost:5432/denguetrace_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
DDL_AUTO=update
SHOW_SQL=false

JWT_SECRET=supersecret
DNI_HASHSECRET=supersecret
CORS_ORIGINS=supersecret
```
---
## Endpoints REST

### Recurso Pacientes
| Método | URL                     | Descripción                             |
|--------|-------------------------|-----------------------------------------|
| GET    | `/pacientes`               | Listar paciente                         |
| GET    | `/pacientes/{id}`               | Obtener paciente por ID                          |
| POST   | `/pacientes`               | Crear paciente                             |
| PUT    | `/pacientes/{id}`        | Actualizar paciente                |
| DELETE   | `/pacientes/{id}`        | Eliminar paciente |


### Recurso Casos
| Método | URL                     | Descripción                             |
|--------|-------------------------|-----------------------------------------|
| GET    | `/casos`               | Listar casos                         |
| GET    | `/casos/{id}`               | Obtener caso por ID                          |
| POST   | `/casos`               | Crear caso                             |
| PUT    | `/casos/{id}`        | Actualizar caso                |
| DELETE   | `/casos/{id}`        | Eliminar caso |

---

## Estructura del proyecto

```
src/main/java/com/example/denguetracebackend
├── DengueTraceBackendApplication.java          # @SpringBootApplication
├── alert/
│   ├── controller/AlertController.java
│   ├── dto/
│   │   └── AlertCreateRequestDTO.java
│   │   └── AlertResponseDTO.java
│   ├── entity/Alert.java
│   ├── repository/AlertRepository.java
│   └── service/AlertService.java
├── climatedata/
│   ├── controller/ClimateDataController
│   ├── dto/
│   │   └── ClimateDataRequestDTO
│   │   └── ClimateDataResponseDTO
│   ├── entity/ClimateData
│   ├── repository/ClimateDataRepository
│   └── service/ClimateDataService
├── common/
│   ├── config/
│   │   ├── AsyncConfig
│   │   ├── OpenApiConfig
│   │   └── SecurityConfig
│   ├── dto/
│   │   ├── ErrorResponseDTO
│   │   └── PageResponseDTO
│   ├── email/
│   │   ├── EmailService
│   │   └── EmailTemaplates
│   ├── enums/
│   │   ├── NotificationChannel
│   │   ├── NotificationStatus
│   │   ├── RiskLevel
│   │   ├── Role
│   │   └── Symptom
│   ├── event/
│   │   ├── listener/
│   │   │   ├── AlertCreatedEventListener
│   │   │   └── UserRegisteredEventListener
│   │   ├── AlterCreatedEvent
│   │   └── UserRegisteredEvent
│   ├── exception/
│   │   ├── BusinessRuleViolationException
│   │   ├── DuplicateResourceException
│   │   ├── ForbiddenActionException
│   │   ├── GlobalExceptionHandler
│   │   ├── InvalidCredentialsException
│   │   ├── InvalidOperationException
│   │   ├── ResourceNotFoundException
│   │   ├── TokenExpiredException
│   │   └── UnauthorizedException
│   └── integration/
│   │   ├── gdelt/GdeltNewsService
│   │   ├── maps/
│   │   │   ├── GoogleGeolocationServer
│   │   │   └── GoogleMapsGeocodingService
│   │   ├── push/FcmPushService
│   │   └── sms/SmsService
│   └── security/
│   │   ├── JwtAuthenticationFilter
│   │   ├── JwtUtil
│   │   └── UserDetailsServiceImpl
├── district/
│   ├── controller/DistrictController
│   ├── dto/
│   │   ├── DisctricExternalRequestDTO
│   │   ├── DirectRequestDTO
│   │   ├── DirectResponseDTO
│   │   └── DistrictRiskSummaryDTO
│   ├── entity/District
│   ├── repository/DistrictRepository
│   └── service/DisctrictService
├── historicalcase/
│   ├── controller/HistoricalCaseController
│   ├── dto/
│   │   ├── HistoricalCaseRequestDTO
│   │   └── HistoricalCaseResponseDTO
│   ├── entity/HistoricalCase
│   ├── repository/HistoricalCaseRepository
│   └── service/HistoricalCaseService
├── news/
│   ├── controller/NewsController
│   ├── dto/
│   │   ├── NewsRequestDTO
│   │   └── NewsResponseDTO
│   ├── entity/News
│   ├── repository/NewsRepository
│   └── service/NewsService
├── notification/
│   ├── entity/Notification
│   ├── dto/NotificationResponseDTO
│   └── repository/NotificationRepository
├── predictivemodel/
│   ├── controller/PredictiveModelController
│   ├── dto/PredictiveModelResponseDTO
│   ├── entity/PredictiveModel
│   ├── repository/PredictiveModelRepository
│   └── service/PredictiveModelService
├── report/
│   ├── controller/ReportController
│   ├── entity/Report
│   ├── repository/ReportRepository
│   └── service/ReportService
├── user/
│   ├── controller/
│   │   ├── AdminController
│   │   ├── AuthController
│   │   └── UserController
│   ├── dto/
│   │   ├── AuthResponseDTO
│   │   ├── LoginRequestDTO
│   │   ├── RegisterRequestDTO
│   │   ├── UserResponseDTO
│   │   └── UserUpdateRequestDTO
│   ├── entity/
│   │   ├── DniHasher
│   │   └── User
│   ├── repository/UserRepository
│   ├── service/
│   │   ├── AuthService
│   │   ├── UserMapper
│   │   └── UserService
```
---
## Desiciones de Diseño
1. Se usó JTW para autenticación Stateless
2. Separación en capas controller/service/repository
---
## Equipo
- Marco Sebastián Ruiz Camarena
- Gabriel Saavedra Peralta
- Elí Bernie Reyes Juárez
- Yamile Valentina Morales Zumaeta
- Martin Gabriel Talavera Pinto
---
## Deployment
El backend está desplegado y disponible públicamente en:

**URL:**

---
## Licencia
Este proyecto está bajo la licencia **MIT**. Ver el archivo [LICENSE](./LICENSE) para más detalles.

--
