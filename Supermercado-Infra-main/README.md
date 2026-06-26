# Proyecto Semestral — Sistema de Supermercado con Microservicios

Sistema distribuido de gestión de supermercado desarrollado con arquitectura de microservicios en Spring Boot. Cubre el ciclo completo de una compra: autenticación, catálogo de productos, carrito, pago, compra, inventario, puntos de fidelización, promociones, seguimiento y notificaciones.

---

## Integrantes

- **Isidora Gómez**
- **Rayen Bettancourt**

---

## Microservicios implementados

| Microservicio     | Carpeta                                              | Puerto | Descripción                                               |
|-------------------|------------------------------------------------------|--------|-----------------------------------------------------------|
| ms-usuario        | `ms-usuario-main/`                                   | 8081   | Gestión de personas, cuentas y roles. Emite JWT           |
| ms-catalogo       | `ms-catalogo-main/catalogo/`                         | 8082   | Gestión de productos y categorías                         |
| ms-inventario     | `ms-inventario-main/inventario/`                     | 8083   | Control de stock por producto                             |
| ms-carrito        | `ms-carrito-main/carrito/`                           | 8084   | Carrito de compras, promociones y canje de puntos         |
| ms-compra         | `Proyecto-Supermercado-Microservicio-Compra-main/Compra/` | 8085 | Registro de compras y publicación de eventos Kafka      |
| ms-pago           | `pago/`                                              | 8086   | Procesamiento de pagos (TARJETA, CRÉDITO, EFECTIVO)       |
| ms-promociones    | `promociones/`                                       | 8087   | Gestión de promociones por código de descuento            |
| ms-seguimiento    | `seguimiento/`                                       | 8088   | Seguimiento del estado de una compra                      |
| ms-notificaciones | `notificaciones/`                                    | 8089   | Notificaciones generadas al completar una compra          |
| ms-puntos         | `puntos/`                                            | 8090   | Acumulación y canje de puntos de fidelización             |
| eureka-server     | `eureka-server/`                                     | 8761   | Servidor de descubrimiento de servicios                   |
| api-gateway       | `api-gateway/`                                       | 8080   | Punto de entrada único. Enruta y valida JWT               |

---

## Rutas del API Gateway

Todas las rutas pasan por `http://localhost:8080`. La ruta de autenticación es pública; el resto requiere token JWT en el header `Authorization: Bearer <token>`.

| Ruta                          | Microservicio destino | Protegida |
|-------------------------------|----------------------|-----------|
| `POST /api/v1/auth`           | ms-usuario           | No        |
| `/api/v1/persons/**`          | ms-usuario           | Sí        |
| `/api/v1/roles/**`            | ms-usuario           | Sí        |
| `/api/v1/logins/**`           | ms-usuario           | Sí        |
| `/api/v1/products/**`         | ms-catalogo          | Sí        |
| `/api/v1/categories/**`       | ms-catalogo          | Sí        |
| `/api/v1/inventory/**`        | ms-inventario        | Sí        |
| `/api/v1/carts/**`            | ms-carrito           | Sí        |
| `/api/v1/compras/**`          | ms-compra            | Sí        |
| `/api/v1/pagos/**`            | ms-pago              | Sí        |
| `/api/v1/promociones/**`      | ms-promociones       | Sí        |
| `/api/v1/seguimientos/**`     | ms-seguimiento       | Sí        |
| `/api/v1/notificaciones/**`   | ms-notificaciones    | Sí        |
| `/api/v1/puntos/**`           | ms-puntos            | Sí        |

---

## Documentación Swagger / OpenAPI

Cada microservicio expone su documentación de forma local. Acceder con el servidor corriendo:

| Microservicio     | URL Swagger UI                              |
|-------------------|---------------------------------------------|
| ms-usuario        | http://localhost:8081/swagger-ui.html       |
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
3. Cada microservicio se levanta con `./mvnw spring-boot:run` desde su carpeta raíz, o directamente desde IntelliJ IDEA.

### Verificación

- Eureka Dashboard: http://localhost:8761
- Kafka UI: http://localhost:8100
- Gateway (healthcheck): http://localhost:8080/api/v1/auth (POST)

---

## Autenticación

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
- MySQL 8.0
- Springdoc OpenAPI (Swagger UI)
- Docker · Docker Compose
- JUnit 5 + Mockito (pruebas unitarias)