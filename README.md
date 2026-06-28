# Navium | Backend for Frontend Operación (bff-operacion)

En las operaciones del Puerto el operador de patio requiere una visión consolidada y en tiempo real del estado de los andenes, asignaciones y agendamientos. Este componente BFF (Backend for Frontend) es el responsable de orquestar y simplificar las consultas a múltiples microservicios downstream para proveer una API optimizada y de bajo acoplamiento al portal web de operaciones.

Su objetivo es asegurar que la UI del operador consuma datos consolidados en un único punto de entrada, aplicando patrones de resiliencia y traducción de contratos.

### Responsabilidades del BFF

1. **Orquestación y Consolidación de Agendamientos:** Consume el microservicio de Agendamientos por patente e integra en tiempo real la información detallada del Contenedor (sigla, estado TATC, estado general y empresa de transporte) consultando al microservicio de Contenedores.
2. **Mapa consolidado de Andenes:** Agrupa la información física de los andenes y expone la asignación activa (transporte + contenedor) en un único flujo operativo.
3. **Manejo de Resiliencia y Tolerancia a Fallos:** Implementa *Circuit Breaker* (Resilience4j) en los clientes de microservicios para permitir una degradación controlada del servicio (ej. retornar agendamientos sin detalles de contenedor si el servicio de contenedores no responde).
4. **Seguridad y Autorización:** Integra la validación y firma de tokens JWT (usando `navium-security-lib`) para proteger los recursos y asegurar que solo operarios autenticados realicen asignaciones.

### Dependencias

- **Java:** 21
- **Framework:** Spring Boot 4.0.6 (con AOP)
- **API:** Spring Web (RestClient)
- **Seguridad:** Spring Security + JJWT (Json Web Token) + `navium-security-lib`
- **Resiliencia:** Resilience4j (Circuit Breakers + Fallbacks)
- **Build:** Maven (incluye Maven Wrapper `mvnw` / `mvnw.cmd`)
- **Utilidades:** Lombok

### Configuración de entorno de desarrollo

#### Entorno local sin Docker

- Por defecto el proyecto utiliza el puerto `8086`.
- **JDK 21** instalado y configurado en `JAVA_HOME`.
- Los microservicios de **Andenes** (`8083`), **Contenedores** (`8080`) y **Agendamientos** (`8082`) deben estar accesibles (URL configuradas en `application.properties` o mediante variables de entorno).

Para compilación ejecuta:
```bash
./mvnw clean package
```

Ejecuta la aplicación localmente:
```bash
./mvnw.cmd spring-boot:run
```

#### Entorno con Docker

Dirígete al directorio del proyecto y crea un archivo `.env` utilizando como plantilla el `.env.example` provisto.

```bash
cd navium-api-bff-operacion/
```

Levanta el contenedor. Docker se encarga de ejecutar la creación de la imagen (con `Dockerfile`) automáticamente con el comando compose.
```bash
docker compose up --build -d
```