# Microservicio de Puntos

Microservicio encargado de la acumulación y canje de puntos de fidelización. 
Asigna puntos automáticamente al completarse una compra (vía Kafka) y permite canjearlos como descuento en el carrito, a través de `carrito`.

---

## Configuración

**Puerto:** `8090`  
**Nombre de la aplicación:** `puntos`
**Base de datos:** `db_puntos`

**OpenAPI**
```
http://localhost:8090/swagger-ui.html
```

**Eureka**
```
http://localhost:8761/
```

**Gateway**
```
http://localhost:8080/
```

---

## Herramientas

- Java 25 · Spring Boot 4.0.6
- Spring Security + JWT
- Spring Data JPA + Flyway
- Spring Cloud Eureka Client
- Spring Kafka (consumidor de eventos)
- Spring HATEOAS
- Springdoc OpenAPI (Swagger UI)
- Docker
---

## Endpoints

### Puntos — `/api/v1/puntos`

| Método | Ruta                                        | Descripción                                | Rol requerido                |
|--------|----------------------------------------------|---------------------------------------------|---------------------------------|
| GET    | `/api/v1/puntos/{usuarioId}`                  | Consultar los puntos acumulados de un usuario  | FUNCIONARIO o CLIENTE (dueño)   |
| GET    | `/api/v1/puntos/{usuarioId}/canje/simular`    | Simular el canje de puntos (paso 1 de 2)       | FUNCIONARIO o CLIENTE (dueño)   |
| POST   | `/api/v1/puntos/{usuarioId}/canje/confirmar`  | Confirmar el canje de puntos (paso 2 de 2)     | CLIENTE (dueño)                 |
| POST   | `/api/v1/puntos`                              | Asignar puntos manualmente                     | FUNCIONARIO                     |

**Validaciones:**
- Un cliente solo puede consultar y canjear sus propios puntos (verificado contra el id del token JWT).
- Confirmar el canje es una acción exclusiva de `CLIENTE` — un `FUNCIONARIO` puede consultar y simular, pero no ejecuta canjes a nombre de otro usuario.
- El `usuarioId` es obligatorio.
- El `montoCompra` debe ser mayor a 0.
- Los puntos se calculan como `(int)(montoCompra / 100)`.
- Cada usuario tiene un único registro de puntos — los puntos nuevos se acumulan sobre el saldo existente.
- Los puntos acumulados no pueden ser negativos.
- El microservicio también escucha eventos de **Kafka** para recibir asignaciones de puntos de forma automática desde otros servicios.


---

## Flujo de canje (2 pasos, orquestado por `carrito`)

1. `carrito` llama a `GET .../canje/simular` para saber a cuánto descuento equivalen los puntos disponibles, sin gastarlos todavía.
2. Al confirmar la compra, `carrito` llama a `POST .../canje/confirmar`, que descuenta los puntos reales y queda registrado en el historial.

---

## Comunicación con otros servicios

**Vía Kafka (asíncrona)**

| Tópico consumido    | Grupo          | Acción disparada                                      |
|---------------------|----------------|-------------------------------------------------------|
| `compra-completada` | `puntos-group` | Asigna automáticamente puntos por la compra realizada |

Este servicio, a su vez, es consumido vía Feign por `carrito` para simular y confirmar canjes.
 
---

## Modelo de base de datos

```
puntos
├── id                  (PK)
├── usuario_id           (not null)
└── puntos_acumulados    (not null)
 
puntos_historial
├── id                 (PK)
├── usuario_id          (not null)
├── compra_id           (nullable — null si la asignación fue manual)
├── puntos_otorgados    (not null)
└── tipo                (not null — ej. GANADOS | CANJEADOS)
```
 
---

## Pruebas unitarias

| Clase de test    | Métodos cubiertos                                                            |
|------------------|------------------------------------------------------------------------------|
| `PuntosImplTest` | Consultar puntos, simular canje, confirmar canje, asignar puntos manualmente |

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
