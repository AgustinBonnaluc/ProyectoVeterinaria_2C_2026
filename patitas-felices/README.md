# Patitas Felices — Microservicios

Clínica veterinaria descompuesta en microservicios con Spring Boot 4.1, Spring Cloud 2025.1,
arquitectura hexagonal, Eureka, Config Server, Actuator, OpenFeign, API Gateway, Resilience4j y JWT.

## Componentes

| Componente     | Puerto | Rol                                                          |
|----------------|--------|--------------------------------------------------------------|
| api-gateway    | 8080   | **Único punto de entrada** (Spring Cloud Gateway, WebFlux)   |
| ms-auth        | 8085   | Registro, login y emisión de JWT (BD `patitas_auth`)         |
| config-server  | 8888   | Configuración centralizada (repo Git `patitas-config`)       |
| eureka-server  | 8761   | Service Discovery                                            |
| ms-dueno       | 8081   | Bounded Context: dueños (BD `dueno_db`)                      |
| ms-mascota     | 8082   | Bounded Context: mascotas (BD `mascota_db`)                  |
| ms-veterinario | 8083   | Bounded Context: veterinarios (BD `veterinario_db`)          |
| ms-turno       | 8084   | Bounded Context: turnos (BD `turno_db`) — valida vía Feign   |

Cada microservicio es dueño de sus datos: las bases se crean solas (`createDatabaseIfNotExist=true`)
y las referencias entre contextos se guardan como IDs (`duenoId`, `mascotaId`, `veterinarioId`),
nunca con `@ManyToOne`.

## Orden de arranque

0. **Redis** (Docker): `docker start patitas-redis`
1. **config-server** (los microservicios le piden su configuración al arrancar)
2. **eureka-server**
3. **ms-auth**
4. **ms-dueno, ms-mascota, ms-veterinario, ms-turno** (en cualquier orden)
5. **api-gateway**

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run -pl config-server
./mvnw spring-boot:run -pl eureka-server
./mvnw spring-boot:run -pl ms-auth
./mvnw spring-boot:run -pl ms-dueno
./mvnw spring-boot:run -pl ms-mascota
./mvnw spring-boot:run -pl ms-veterinario
./mvnw spring-boot:run -pl ms-turno
./mvnw spring-boot:run -pl api-gateway
```

> ⚠️ El gateway usa el puerto **8080**. Si tenés Oracle XE instalado, su listener (`TNSLSNR.EXE`)
> ocupa ese puerto: detené el servicio de Oracle o cambiale el puerto HTTP antes de levantar el gateway.

> Después de levantar todo, esperar ~30 s antes de crear turnos: es lo que tarda ms-turno en
> refrescar el registro de Eureka y "ver" a ms-mascota y ms-veterinario.

## Verificación

- Dashboard Eureka: http://localhost:8761 (5 instancias: 4 microservicios + API-GATEWAY)
- Config de un servicio: http://localhost:8888/ms-dueno/default
- Rutas del gateway: http://localhost:8080/actuator/gateway/routes
- API (siempre por el gateway): http://localhost:8080/api/duenos, `/api/mascotas`, `/api/veterinarios`, `/api/turnos`
- Swagger (documentación interna de cada servicio): http://localhost:8081/swagger-ui/index.html (8082, 8083, 8084 igual)
- Actuator: `/actuator/health`, `/actuator/info`, `/actuator/metrics` en cada microservicio

## Arquitectura

Cada microservicio sigue Arquitectura Hexagonal (Ports & Adapters):

```
com/patitas/<servicio>/
├── domain/
│   ├── model/        entidad
│   ├── port/in/      XxxService            (casos de uso que ofrece el dominio)
│   ├── port/out/     XxxRepositoryPort     (lo que el dominio necesita de afuera)
│   └── exception/    excepciones de negocio
├── application/      XxxServiceImpl        (implementa port.in, usa port.out)
└── adapter/
    ├── in/web/           Controller, GlobalExceptionHandler, DTOs
    └── out/persistence/  XxxJpaRepository (Spring Data implementa port.out)
        out/feign/        (solo ms-turno) clientes HTTP hacia otros microservicios
```

**Dependency Rule:** las dependencias apuntan hacia adentro. `application` y `domain` no importan
nada de `adapter.*`.

**¿Por qué el controller no usa `DuenoServiceImpl`?** Porque depende de la interface `DuenoService`
(el port de entrada). Así la capa web no queda acoplada a la implementación: el caso de uso se puede
cambiar o mockear sin tocar el controller, y Spring inyecta la implementación por detrás.

## Comunicación entre microservicios (Sprint 11)

Al crear un turno, `TurnoServiceImpl` valida contra los ports `MascotaServicePort` y
`VeterinarioServicePort`. Sus implementaciones (`MascotaFeignAdapter`, `VeterinarioFeignAdapter`)
usan clientes `@FeignClient(name = "ms-mascota")` / `"ms-veterinario"`, que Eureka resuelve a la
IP:puerto real. El dominio no sabe nada de HTTP.

| Situación                                   | Respuesta de `POST /api/turnos` |
|---------------------------------------------|---------------------------------|
| Mascota y veterinario existen               | 201 Created (estado PENDIENTE)  |
| Mascota o veterinario inexistente           | 404 Not Found                   |
| El veterinario ya tiene turno a esa hora    | 409 Conflict                    |
| ms-mascota / ms-veterinario caído           | 503 Service Unavailable         |

Timeouts Feign (en `patitas-config/ms-turno.yml`): connect 3 s, read 5 s.

## API Gateway (Sprint 12)

A partir de este sprint **ningún cliente externo llama a :8081-8084**: todo entra por `:8080` y el
gateway enruta vía Eureka (`lb://nombre-del-servicio`), balanceando entre instancias.

| Path externo (:8080)   | Destino            | Puerto interno |
|------------------------|--------------------|----------------|
| `/api/duenos/**`       | `lb://ms-dueno`       | 8081 |
| `/api/mascotas/**`     | `lb://ms-mascota`     | 8082 |
| `/api/veterinarios/**` | `lb://ms-veterinario` | 8083 |
| `/api/turnos/**`       | `lb://ms-turno`       | 8084 |
| `/v1/duenos/**`        | `lb://ms-dueno` (reescrito a `/api/duenos/**` con `RewritePath`) | 8081 |
| `/actuator/**`         | api-gateway (propio) | 8080 |

- **Configuración:** rutas, CORS y actuator del gateway están en `patitas-config/api-gateway.yml`.
- **Discovery locator:** además, cada servicio es accesible como `/<nombre-servicio>/**`
  (ej. `/ms-veterinario/api/veterinarios`).
- **CORS:** habilitado para cualquier origen (`*`) — solo para desarrollo.
- **Logging centralizado:** `LoggingFilter` (GlobalFilter, orden -1) registra cada request:
  `[GATEWAY] --> GET /api/duenos` / `[GATEWAY] <-- GET /api/duenos 200 15ms`.
- El gateway es reactivo (WebFlux): **no** lleva `spring-boot-starter-webmvc`.

## Resiliencia (Sprint 13)

`MascotaFeignAdapter` y `VeterinarioFeignAdapter` usan `@CircuitBreaker` y `@Retry` de Resilience4j.
La configuración está en `patitas-config/ms-turno.yml`.

- **Retry:** hasta 3 intentos por request, con espera de 1 s y luego 2 s.
- **Circuit Breaker:** ventana de 10 llamadas, mínimo 5, umbral de fallos 50 %, 10 s en OPEN y 3 llamadas de prueba en HALF_OPEN.
- **Orden:** el Circuit Breaker envuelve al Retry, así que cada request fallido cuenta como un solo fallo.
- **Fallback:** lanza `ServicioNoDisponibleException`, que responde 503.

Un 404 del servicio remoto no cuenta como fallo: significa que el recurso no existe, no que el servicio esté caído.

| Endpoint de ms-turno (:8084)                     | Qué muestra                                   |
|--------------------------------------------------|-----------------------------------------------|
| `/actuator/health`                               | Estado de cada circuit breaker                |
| `/actuator/circuitbreakers`                      | Métricas de cada circuit breaker              |
| `/actuator/circuitbreakerevents`                 | Historial de eventos y transiciones de estado |
| `/actuator/metrics/resilience4j.circuitbreaker.state` | Métrica del estado actual                |

### Rate Limiter

La ruta `/api/turnos/**` del gateway limita por IP con `RequestRateLimiter`: 10 requests por segundo y picos de hasta 20.
Al superar el límite responde `429 Too Many Requests`.

Requiere Redis en `localhost:6379`. Sin Redis el gateway arranca igual, pero no limita y su health queda en DOWN.

## Seguridad con JWT (Sprint 14)

`ms-auth` registra usuarios con la contraseña encriptada con BCrypt y emite tokens JWT válidos por 24 h.
El `AuthenticationFilter` del gateway valida el token en cada request. Los microservicios de negocio no validan nada.

| Request                                    | Resultado                         |
|--------------------------------------------|-----------------------------------|
| `POST /auth/register` `{username, password, rol}` | 200 + `{"token": "..."}` (409 si el usuario ya existe) |
| `POST /auth/login` `{username, password}`  | 200 + `{"token": "..."}` (401 si las credenciales son incorrectas) |
| `GET /api/...` con `Authorization: Bearer <token>` | Pasa al microservicio, con el header `X-Auth-User` |
| `GET /api/...` sin token, con token falso o vencido | 401 Unauthorized |

- Las rutas `/auth/**` son públicas. Todo lo demás que pasa por el gateway requiere token.
- La clave `jwt.secret` está en `patitas-config/ms-auth.yml` y en `patitas-config/api-gateway.yml`. Tiene que ser idéntica en los dos.
