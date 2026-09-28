# DengueTrace 

Backend de **DengueTrace**, una plataforma orientada al seguimiento y prevención del dengue mediante reportes geolocalizados, información por distrito, datos históricos, variables climáticas, alertas y estimaciones de riesgo.

La aplicación permite que los usuarios registren auto reportes de síntomas y utiliza la ubicación del reporte para asociarlo con un distrito. Además, dispone de herramientas administrativas para gestionar distritos, casos históricos, datos climáticos, alertas y noticias relacionadas con dengue.

---

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje principal |
| Spring Boot 4.1.1 | Framework backend |
| Spring Web | API REST |
| Spring Data JPA | Persistencia de datos |
| Hibernate | ORM |
| Spring Security | Seguridad y autorización |
| JWT 0.12.6 | Autenticación stateless |
| PostgreSQL | Base de datos |
| Maven | Gestión de dependencias y build |
| Lombok | Reducción de código repetitivo |
| SpringDoc OpenAPI | Documentación Swagger |
| Firebase Admin SDK | Notificaciones push |
| Twilio | Notificaciones por SMS |
| Google Maps Platform | Geocodificación |
| GDELT 2.0 Doc API | Obtención de noticias relacionadas con dengue |
| Spring Mail | Envío de correos mediante SMTP |

---

## Funcionalidades principales

- Registro e inicio de sesión de usuarios.
- Autenticación mediante JWT.
- Control de acceso mediante roles `USER`, `MODERATOR` y `ADMIN`.
- Gestión de distritos.
- Auto reportes de síntomas asociados a usuarios.
- Geolocalización de reportes mediante latitud y longitud.
- Identificación automática del distrito mediante reverse geocoding.
- Geocodificación automática de distritos mediante Google Maps.
- Gestión de casos históricos de dengue.
- Registro de datos climáticos.
- Generación de estimaciones de riesgo por distrito.
- Clasificación del riesgo en `LOW`, `MEDIUM`, `HIGH` y `CRITICAL`.
- Gestión de alertas por distrito.
- Notificaciones push mediante Firebase Cloud Messaging.
- Notificaciones SMS mediante Twilio.
- Correos de bienvenida mediante SMTP.
- Consulta y almacenamiento de noticias relacionadas con dengue mediante GDELT.
- Documentación interactiva de la API mediante Swagger/OpenAPI.

---

# Arquitectura

El backend utiliza una arquitectura organizada por dominios y separación de responsabilidades:

```text
Controller
    │
    ▼
Service
    │
    ▼
Repository
    │
    ▼
PostgreSQL
```

Cada módulo contiene sus propios controladores, DTOs, entidades, repositorios y servicios.

A nivel general:

```text
Cliente / Frontend
        │
        │ HTTP / REST
        ▼
┌───────────────────────────┐
│       Spring Boot         │
│                           │
│ Controllers               │
│ Services                  │
│ Spring Security + JWT     │
│ JPA / Hibernate           │
└─────────────┬─────────────┘
              │
              │ JDBC
              ▼
┌───────────────────────────┐
│        PostgreSQL         │
└───────────────────────────┘

Servicios externos:

Spring Boot
   ├── Google Maps API
   ├── Firebase Cloud Messaging
   ├── Twilio
   ├── GDELT
   └── SMTP / Gmail
```

---

# Estructura del proyecto

```text
src/main/java/com/example/denguetracebackend
│
├── alert/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── climatedata/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── common/
│   ├── config/
│   ├── dto/
│   ├── email/
│   ├── enums/
│   ├── event/
│   ├── exception/
│   ├── integration/
│   │   ├── gdelt/
│   │   ├── maps/
│   │   ├── push/
│   │   └── sms/
│   └── security/
│
├── district/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── historicalcase/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── news/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── notification/
│   ├── dto/
│   ├── entity/
│   └── repository/
│
├── predictivemodel/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── report/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
└── user/
    ├── controller/
    ├── dto/
    ├── entity/
    ├── repository/
    └── service/
```

---

# Configuración local

## Requisitos

Para ejecutar el backend localmente se necesita:

- Java 21
- Docker
- Docker Compose
- Maven Wrapper incluido en el proyecto

---

## 1. Ingresar al proyecto Spring Boot

Desde la raíz del repositorio:

```bash
cd DengueTrace-Backend
```

---

## 2. Levantar PostgreSQL

El proyecto incluye un archivo `docker-compose.yaml`.

Ejecutar:

```bash
docker compose up -d
```

Esto levanta PostgreSQL con la siguiente configuración de desarrollo:

| Parámetro | Valor |
|---|---|
| Imagen | `postgres:17` |
| Contenedor | `denguetrace-postgres` |
| Base de datos | `denguetrace` |
| Usuario | `postgres` |
| Puerto PostgreSQL dentro del contenedor | `5432` |
| Puerto expuesto localmente | `5433` |

La URL JDBC local utilizada por defecto es:

```text
jdbc:postgresql://localhost:5433/denguetrace
```

---

## 3. Ejecutar Spring Boot

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Por defecto, el servidor inicia en:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

---

# Variables de entorno

El proyecto permite reemplazar la configuración mediante variables de entorno.

## Base de datos

```env
DB_URL=jdbc:postgresql://localhost:5433/denguetrace
DB_USERNAME=postgres
DB_PASSWORD=postgres
DDL_AUTO=update
SHOW_SQL=false
```

## Seguridad

```env
JWT_SECRET=<jwt-secret>
DNI_HASH_SECRET=<dni-hash-secret>
CORS_ORIGINS=http://localhost:3000,http://localhost:5173
```

## Google Maps

```env
GOOGLE_MAPS_API_KEY=<google-maps-api-key>
```

Se utiliza para geocodificación y reverse geocoding.

---

## Firebase Cloud Messaging

```env
FIREBASE_CREDENTIALS_BASE64=<firebase-service-account-json-en-base64>
```

Las credenciales de Firebase se proporcionan codificando el Service Account JSON en Base64.

El archivo JSON original no debe almacenarse en el repositorio.

---

## Twilio

```env
TWILIO_ACCOUNT_SID=<twilio-account-sid>
TWILIO_AUTH_TOKEN=<twilio-auth-token>
TWILIO_FROM_NUMBER=<twilio-phone-number>
```

Se utiliza para el envío de notificaciones SMS.

---

## Correo SMTP

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<email>
MAIL_PASSWORD=<app-password>
```

Se utiliza Spring Mail para el envío de correos, incluyendo el correo de bienvenida al registrarse.

---

# Integraciones externas

## Google Maps Platform

DengueTrace utiliza Google Maps para resolver información geográfica.

Al crear un distrito, si no se proporcionan coordenadas, el backend puede obtener automáticamente su latitud y longitud utilizando:

```text
Distrito + Provincia + Departamento + Perú
                    │
                    ▼
        Google Maps Geocoding API
                    │
                    ▼
          Latitude / Longitude
```

También se utiliza reverse geocoding en los auto reportes:

```text
Latitude / Longitude
        │
        ▼
Google Maps Geocoding API
        │
        ▼
Distrito / Provincia / Departamento
```

Esto permite asociar un reporte con su distrito incluso cuando no se envía directamente un `districtId`.

---

## GDELT

El backend integra la **GDELT 2.0 Doc API** para obtener artículos relacionados con dengue.

La búsqueda utiliza información del distrito:

```text
"dengue" + distrito + "Peru"
          │
          ▼
     GDELT Doc API
          │
          ▼
 Artículos relacionados
          │
          ▼
      PostgreSQL
```

GDELT no requiere una API key para esta integración.

Un administrador puede iniciar la sincronización mediante:

```http
POST /api/v1/news/district/{districtId}/sync
```

La sincronización se realiza de manera asíncrona y evita guardar repetidamente noticias con la misma URL.

---

## Firebase

Firebase Cloud Messaging se utiliza para enviar notificaciones push a los usuarios que tengan registrado un `fcmToken`.

---

## Twilio

Twilio se utiliza para enviar alertas mediante SMS cuando el usuario tiene configurado dicho canal de notificación.

Los canales soportados actualmente son:

```text
PUSH
SMS
```

---

# Autenticación y seguridad

DengueTrace utiliza **Spring Security + JWT**.

La autenticación es stateless:

```text
Usuario
   │
   │ email + password
   ▼
POST /api/v1/auth/login
   │
   ▼
Access Token + Refresh Token
   │
   ▼
Authorization: Bearer <token>
   │
   ▼
Endpoints protegidos
```

Los roles disponibles son:

```text
USER
MODERATOR
ADMIN
```

Las contraseñas se almacenan utilizando `BCryptPasswordEncoder`.

---

# Principales endpoints

La documentación completa y actualizada de los endpoints puede consultarse mediante Swagger.

## Autenticación

| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/api/v1/auth/register` | Público |
| POST | `/api/v1/auth/login` | Público |
| POST | `/api/v1/auth/refresh` | Público |

---

## Distritos

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/v1/districts` | Público |
| GET | `/api/v1/districts/{id}` | Público |
| POST | `/api/v1/districts` | ADMIN |
| PUT | `/api/v1/districts/{id}` | ADMIN |
| DELETE | `/api/v1/districts/{id}` | ADMIN |

---

## Reportes

| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/api/v1/reports` | Autenticado |
| GET | `/api/v1/reports/me` | Autenticado |
| GET | `/api/v1/reports/{id}` | Autenticado |
| GET | `/api/v1/reports/district/{district}` | Autenticado |
| GET | `/api/v1/reports` | ADMIN / MODERATOR |
| DELETE | `/api/v1/reports/{id}` | ADMIN / MODERATOR |

---

## Alertas

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/v1/alerts` | Público |
| GET | `/api/v1/alerts/district/{districtId}` | Público |
| POST | `/api/v1/alerts` | ADMIN |
| PATCH | `/api/v1/alerts/{id}/deactivate` | ADMIN |

---

## Noticias

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/v1/news/district/{districtId}` | Público |
| POST | `/api/v1/news` | ADMIN |
| POST | `/api/v1/news/district/{districtId}/sync` | ADMIN |

---

## Modelo predictivo

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/v1/predictive-models/district/{districtId}/latest` | Público |
| GET | `/api/v1/predictive-models/district/{districtId}/history` | Público |
| POST | `/api/v1/predictive-models/district/{districtId}/generate` | ADMIN / MODERATOR |

---

## Datos climáticos

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/v1/climate-data/district/{districtId}` | Autenticado |
| POST | `/api/v1/climate-data` | ADMIN |

---

## Casos históricos

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/v1/historical-cases/district/{districtId}` | Autenticado |
| POST | `/api/v1/historical-cases` | ADMIN |

---

## Usuarios

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/v1/users/me` | Autenticado |
| PATCH | `/api/v1/users/me` | Autenticado |
| GET | `/api/v1/users/{id}` | ADMIN |
| GET | `/api/v1/admin/users` | ADMIN |

---

# Modelo de riesgo

El proyecto incluye un modelo académico y explicable para estimar el riesgo de dengue por distrito.

El cálculo utiliza:

- Casos históricos.
- Promedio histórico.
- Promedio de semanas recientes.
- Tendencia reciente de casos.
- Temperatura.
- Humedad.
- Precipitación.
- Población del distrito.

El resultado genera proyecciones para cuatro semanas y clasifica el riesgo como:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Para generar una estimación se requieren al menos cuatro semanas de datos históricos.

---

# Documentación OpenAPI

Swagger UI permite explorar y probar la API desde el navegador.

### Local

```text
http://localhost:8080/swagger-ui/index.html
```

### Producción

```text
http://3.139.217.27:8080/swagger-ui/index.html
```

Swagger también permite utilizar JWT mediante el esquema:

```text
Bearer <access-token>
```

---

# Deployment

El backend de DengueTrace está desplegado en **Amazon Web Services (AWS)** en la región:

```text
us-east-2
```

La infraestructura utilizada es:

```text
                       Internet
                           │
                           │ HTTP :8080
                           ▼
              ┌─────────────────────────┐
              │       Amazon EC2        │
              │                         │
              │   Amazon Linux 2023     │
              │   Java 21               │
              │   Spring Boot 4.1.1     │
              │   systemd               │
              └────────────┬────────────┘
                           │
                           │ JDBC :5432
                           ▼
              ┌─────────────────────────┐
              │       Amazon RDS        │
              │                         │
              │       PostgreSQL        │
              └─────────────────────────┘
```

---

## Acceso público

| Recurso | Dirección |
|---|---|
| Backend | `http://3.139.217.27:8080` |
| Swagger UI | `http://3.139.217.27:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://3.139.217.27:8080/v3/api-docs` |

La instancia utiliza la Elastic IP:

```text
3.139.217.27
```

Esto proporciona una dirección pública estable para el backend.

---

## Amazon EC2

El backend se ejecuta en una instancia EC2 con:

```text
Amazon Linux 2023
Java 21
Spring Boot 4.1.1
```

La aplicación se compila como un archivo JAR ejecutable mediante Maven:

```bash
./mvnw clean package -Dmaven.test.skip=true
```

El artefacto generado es:

```text
DengueTrace-Backend-0.0.1-SNAPSHOT.jar
```

---

## systemd

La aplicación se ejecuta como un servicio de Linux administrado mediante `systemd`.

Esto permite:

- Mantener el backend activo aunque se cierre la conexión SSH.
- Iniciar automáticamente la aplicación junto con la instancia.
- Reiniciar automáticamente el proceso ante una falla.

Comandos de administración:

```bash
sudo systemctl status denguetrace
sudo systemctl start denguetrace
sudo systemctl stop denguetrace
sudo systemctl restart denguetrace
```

Logs:

```bash
sudo journalctl -u denguetrace
```

---

## Amazon RDS

La base de datos de producción utiliza **PostgreSQL en Amazon RDS**.

La instancia RDS no se expone directamente como base de datos pública para los clientes.

La comunicación se realiza entre EC2 y RDS por el puerto:

```text
5432
```

```text
Spring Boot - EC2
       │
       │ JDBC :5432
       ▼
PostgreSQL - RDS
```

---

## Seguridad de red

La infraestructura utiliza AWS Security Groups.

| Puerto | Uso | Acceso |
|---:|---|---|
| `22` | SSH | Restringido para administración |
| `8080` | API Spring Boot | Público |
| `5432` | PostgreSQL | Comunicación EC2 → RDS |

---

## Variables de producción

Las credenciales y secretos del entorno de producción no se almacenan directamente en el repositorio.

El servidor utiliza variables como:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
DDL_AUTO
SHOW_SQL

JWT_SECRET
DNI_HASH_SECRET
CORS_ORIGINS

GOOGLE_MAPS_API_KEY

FIREBASE_CREDENTIALS_BASE64

TWILIO_ACCOUNT_SID
TWILIO_AUTH_TOKEN
TWILIO_FROM_NUMBER

MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
```

En EC2 estas variables son cargadas por el servicio de la aplicación desde la configuración del servidor.

> Nunca se deben subir contraseñas, tokens, Service Accounts, API keys ni secretos reales al repositorio.

---

# Flujo de deployment

```text
GitHub
   │
   │ git pull
   ▼
Amazon EC2
   │
   ├── Maven
   │
   ├── Spring Boot
   │
   ├── JAR
   │
   └── systemd
          │
          ▼
   Backend :8080
          │
          │ JDBC
          ▼
    Amazon RDS
    PostgreSQL
```

---

# Equipo

- Marco Sebastián Ruiz Camarena
- Gabriel Saavedra Peralta
- Elí Bernie Reyes Juárez
- Martin Gabriel Talavera Pinto

---


# 📄 Licencia

Este proyecto se distribuye bajo licencia **MIT**.
[Ver licencia MIT](https://github.com/elireyes-web/DengueTrace-Backend/blob/main/DengueTrace-Backend/LICENSE.txt)

---
