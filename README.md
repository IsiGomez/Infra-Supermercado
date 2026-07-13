# Proyecto Semestral — Sistema de Supermercado con Microservicios

Sistema distribuido de gestión de supermercado desarrollado con arquitectura de microservicios en Spring Boot. Cubre el ciclo completo de una compra: autenticación, catálogo de productos, carrito, pago, compra, inventario, puntos de fidelización, promociones, seguimiento y notificaciones.

---

## Integrantes

- **Isidora Gómez**
- **Rayen Bettancourt**

---

## Microservicios implementados

| Microservicio     | Carpeta             | Puerto | Descripción                                           |
|-------------------|---------------------|--------|-------------------------------------------------------|
| ms-usuario        | `usuarios/`         | 8081   | Gestión de personas, cuentas y roles. Emite JWT       |
| ms-catalogo       | `catalogo/`         | 8082   | Gestión de productos y categorías                     |
| ms-inventario     | `inventario/`       | 8083   | Control de stock por producto                         |
| ms-carrito        | `carrito/`          | 8084   | Carrito de compras, promociones y canje de puntos     |
| ms-compra         | `compra/`           | 8085   | Registro de compras y publicación de eventos Kafka    |
| ms-pago           | `pago/`             | 8086   | Procesamiento de pagos (TARJETA, CRÉDITO, EFECTIVO)   |
| ms-promociones    | `promociones/`      | 8087   | Gestión de promociones por código de descuento        |
| ms-seguimiento    | `seguimiento/`      | 8088   | Seguimiento del estado de una compra                  |
| ms-notificaciones | `notificaciones/`   | 8089   | Notificaciones generadas al completar una compra      |
| ms-puntos         | `puntos/`           | 8090   | Acumulación y canje de puntos de fidelización         |
| eureka-server     | `eureka-server/`    | 8761   | Servidor de descubrimiento de servicios               |
| api-gateway       | `api-gateway/`      | 8080   | Punto de entrada único. Enruta y valida JWT           |

---

## Rutas del API Gateway

Todas las rutas pasan por `http://localhost:8080`. Algunas rutas como el de autenticación y roles son públicas, pero el resto requiere token JWT en el header `Authorization: Bearer <token>`.

| Ruta                        | Microservicio destino | Protegida (`JWT`)    |
|-----------------------------|-----------------------|----------------------|
| `POST /api/v1/auth`         | ms-usuarios           | No                   |
| `POST /api/v1/persons`      | ms-usuarios           | No — es el registro  |
| `/api/v1/persons/**`        | ms-usuarios           | Sí                   |
| `POST /api/v1/logins`       | ms-usuarios           | No — crea la cuenta  |
| `/api/v1/logins/**`         | ms-usuarios           | Sí                   |
| `/api/v1/roles/**`          | ms-usuarios           | No                   |
| `/api/v1/products/**`       | ms-catalogo           | Sí                   |
| `/api/v1/categories/**`     | ms-catalogo           | Sí                   |
| `/api/v1/inventory/**`      | ms-inventario         | Sí                   |
| `/api/v1/carts/**`          | ms-carrito            | Sí                   |
| `/api/v1/compras/**`        | ms-compra             | Sí                   |
| `/api/v1/pagos/**`          | ms-pago               | Sí                   |
| `/api/v1/promociones/**`    | ms-promociones        | Sí                   |
| `/api/v1/seguimientos/**`   | ms-seguimiento        | Sí                   |
| `/api/v1/notificaciones/**` | ms-notificaciones     | Sí                   |
| `/api/v1/puntos/**`         | ms-puntos             | Sí                   |

---

## Documentación Swagger / OpenAPI

Cada microservicio expone su documentación de forma local. Acceder con el servidor corriendo:

| Microservicio     | URL Swagger UI                              |
|-------------------|---------------------------------------------|
| ms-usuarios       | http://localhost:8081/swagger-ui.html       |
| ms-catalogo       | http://localhost:8082/swagger-ui.html       |
| ms-inventario     | http://localhost:8083/swagger-ui.html       |
| ms-carrito        | http://localhost:8084/swagger-ui.html       |
| ms-compra         | http://localhost:8085/swagger-ui.html       |
| ms-pago           | http://localhost:8086/swagger-ui.html       |
| ms-promociones    | http://localhost:8087/swagger-ui.html       |
| ms-seguimiento    | http://localhost:8088/swagger-ui.html       |
| ms-notificaciones | http://localhost:8089/swagger-ui.html       |
| ms-puntos         | http://localhost:8090/swagger-ui.html       |

---

## Arquitectura de eventos (Kafka)

Cuando `ms-compra` confirma una compra, publica el evento `compra-completada` que es consumido por **4 microservicios en paralelo**:

```
                      ┌──────────────┐
                      │  ms-compra   │
                      │ (productor)  │
                      └──────┬───────┘
                             │ compra-completada
              ┌──────────────┼──────────────┬──────────────┐
              ▼              ▼              ▼              ▼
      ms-inventario     ms-puntos    ms-seguimiento  ms-notificaciones
       (descuenta        (asigna      (crea estado     (envía la
        stock)            puntos)       inicial)        notificación)
```

Cada consumidor tiene su propio `groupId` (`inventario-group`, `puntos-group`, `seguimiento-group`, `notificaciones-group`), por lo que los 4 reciben el mismo evento de forma independiente.

---

## Comunicación síncrona entre microservicios (Feign)

| Servicio origen | Servicio destino  | Motivo                                                              |
|-----------------|-------------------|---------------------------------------------------------------------|
| `carrito`       | `inventario`      | Verificar stock disponible al agregar un producto                   |
| `carrito`       | `catalogo`        | Obtener datos del producto (nombre, precio) al agregarlo            |
| `carrito`       | `puntos`          | Simular/confirmar el canje de puntos como descuento                 |
| `carrito`       | `promociones`     | Validar y aplicar un código de promoción al carrito                 |
| `compra`        | `pago`            | Verificar que el usuario tenga un pago exitoso antes de comprar     |
| `compra`        | `carrito`         | Obtener el detalle del carrito y limpiarlo tras confirmar la compra |
| `inventario`    | `catalogo`        | Validar que el producto exista antes de actualizar su stock         |
| `pago`          | `carrito`         | Obtener el total del carrito para procesar el monto del pago        |

---

## Instrucciones de ejecución local

### Opción A — Docker Compose (recomendado)

Requiere Docker Desktop instalado y en ejecución.

```bash
# Desde la raíz del repositorio (donde está el docker-compose.yml)
docker compose up --build
```

El comando levanta en orden: MySQL → Kafka + Zookeeper → Eureka → API Gateway → todos los microservicios.

Para detener y eliminar los contenedores:

```bash
docker compose down -v
```

> **Nota:** La base de datos se inicializa automáticamente con el archivo `init-databases.sql`. Flyway crea las tablas al arrancar cada microservicio.

### Opción B — Ejecución manual desde el IDE

1. Asegurarse de tener corriendo MySQL en el puerto 3306 y Kafka en el puerto 9092.
2. Levantar los servicios en el siguiente orden:
    1. `eureka-server`
    2. `api-gateway`
    3. Cualquier microservicio de negocio (sin orden entre ellos)
3. Cada microservicio se levanta directamente desde IntelliJ IDEA.

### Verificación

- Eureka Dashboard: http://localhost:8761
- Kafka UI: http://localhost:8100
- Gateway (healthcheck): http://localhost:8080/api/v1/auth (POST)

---

## Autenticación

El flujo completo de alta de un usuario nuevo es:

1. **Crear la persona** — `POST http://localhost:8080/api/v1/persons` (público, sin token)
2. **Crear la cuenta/credenciales** — `POST http://localhost:8080/api/v1/logins` (público, sin token), asociando la persona creada y un rol (`CLIENTE` o `FUNCIONARIO`)
3. **Autenticarse para obtener el JWT:**
Todos los endpoints protegidos requieren un token JWT obtenido mediante:

```
POST http://localhost:8080/api/v1/auth
Content-Type: application/json

{
  "username": "FunClaudia",
  "password": "password"
}
```

Usar el token retornado en el header de las siguientes peticiones:

```
Authorization: Bearer <token>
```

---

## Stack tecnológico

- Java 25 · Spring Boot 4.0.6
- Spring Security + JWT
- Spring Data JPA + Flyway
- Spring Cloud: Eureka, OpenFeign, Gateway (WebMVC)
- Apache Kafka (eventos asincrónicos entre servicios)
- Spring HATEOAS
- MySQL 8.0
- Springdoc OpenAPI (Swagger UI)
- Docker · Docker Compose
- JUnit 5 + Mockito (pruebas unitarias)

---

## Despliegue remoto (Railway)

El sistema está desplegado en Railway. Cada microservicio, Eureka y MySQL corren como servicios independientes dentro del mismo proyecto, comunicándose por red privada (`RAILWAY_PRIVATE_DOMAIN`). 
El único punto de entrada pensado para uso externo es el **API Gateway**.

### URL pública

- **API Gateway:** `https://<tu-gateway>.up.railway.app`

### Variables de entorno por tipo de servicio

**Servicios con base de datos (usuarios, catalogo, inventario, carrito, compra, pago, promociones, seguimiento, notificaciones, puntos):**

| Variable                     | Descripción                                                 |
|------------------------------|-------------------------------------------------------------|
| `SPRING_PROFILES_ACTIVE`     | `prod`                                                      |
| `DB_HOST`, `DB_PORT`         | Referencian el servicio MySQL de Railway                    |
| `DB_NAME`                    | Nombre de la base propia del servicio (ej. `db_usuarios`)   |
| `DB_USERNAME`, `DB_PASSWORD` | Credenciales de MySQL (definidas en Railway, no en el repo) |
| `EUREKA_URL`                 | URL interna de Eureka                                       |
| `JWT_SECRET`                 | Clave para firmar/validar JWT                               |

**Eureka Server:**

| Variable                 | Descripción |
|--------------------------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod`      |

**API Gateway:**

| Variable                 | Descripción                                    |
|--------------------------|------------------------------------------------|
| `SPRING_PROFILES_ACTIVE` | `prod`                                         |
| `EUREKA_URL`             | URL interna de Eureka                          |
| `JWT_SECRET`             | Clave para validar JWT en las rutas protegidas |

> Las variables reales (contraseñas, secretos) se configuran directamente en el panel de Railway y no se versionan en el repositorio.

### Probar el flujo remoto

```
POST https://<tu-gateway>.up.railway.app/api/v1/auth
Content-Type: application/json

{
  "username": "FunClaudia",
  "password": "password"
}
```

### Zookeeper y Kafka

Al igual que en Docker Compose local, Kafka corre como dos servicios independientes desplegados desde imagen Docker (Confluent), comunicados por red privada de Railway.

**Servicio `zookeeper`:**

| Variable                | Valor  |
|-------------------------|--------|
| `ZOOKEEPER_CLIENT_PORT` | `2181` |
| `ZOOKEEPER_TICK_TIME`   | `2000` |

**Servicio `kafka`:**

| Variable                                         | Valor                                                    |
|--------------------------------------------------|----------------------------------------------------------|
| `KAFKA_BROKER_ID`                                | `1`                                                      |
| `KAFKA_ZOOKEEPER_CONNECT`                        | `${{zookeeper.RAILWAY_PRIVATE_DOMAIN}}:2181`             |
| `KAFKA_ADVERTISED_LISTENERS`                     | `PLAINTEXT_INTERNAL://${{RAILWAY_PRIVATE_DOMAIN}}:29092` |
| `KAFKA_LISTENER_SECURITY_PROTOCOL_MAP`           | `PLAINTEXT_INTERNAL:PLAINTEXT`                           |
| `KAFKA_INTER_BROKER_LISTENER_NAME`               | `PLAINTEXT_INTERNAL`                                     |
| `KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR`         | `1`                                                      |
| `KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR` | `1`                                                      |
| `KAFKA_TRANSACTION_STATE_LOG_MIN_ISR`            | `1`                                                      |
| `KAFKA_AUTO_CREATE_TOPICS_ENABLE`                | `true`                                                   |

Ninguno de los dos servicios necesita Networking público — solo se consumen internamente por los microservicios que publican/consumen eventos 
(`compra`, `inventario`, `puntos`, `seguimiento`, `notificaciones`) mediante la variable `KAFKA_BOOTSTRAP_SERVERS`

KAFKA_BOOTSTRAP_SERVERS=${{kafka.RAILWAY_PRIVATE_DOMAIN}}:29092
