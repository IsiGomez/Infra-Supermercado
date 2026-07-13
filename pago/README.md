# Microservicio de Pago

Microservicio encargado del procesamiento de pagos asociados a compras en el sistema de supermercado. 
Permite registrar un pago indicando el método utilizado y consultar los pagos de los usuarios.

---

## Configuración

**Puerto:** `8086`  
**Base de datos:** `db_pago`

**OpenAPI**
```
http://localhost:8086/swagger-ui.html
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
- Spring Cloud OpenFeign (comunicación síncrona con `carrito`)
- Spring HATEOAS
- Springdoc OpenAPI (Swagger UI)
- Docker

---

## Base de datos

Las tablas son creadas automáticamente por Flyway al iniciar la aplicación.

### `pago`
| Campo      | Tipo         | Descripción                                          |
|------------|--------------|------------------------------------------------------|
| id         | BIGINT (PK)  | Identificador único del pago                         |
| compra_id  | BIGINT       | ID de la compra asociada                             |
| monto      | DOUBLE       | Monto pagado (> 0)                                   |
| metodo     | VARCHAR(20)  | Método de pago (`TARJETA`, `CREDITO`, `EFECTIVO`)    |
| exitoso    | BOOLEAN      | Indica si el pago fue procesado exitosamente         |
| fecha_pago | DATETIME     | Fecha y hora del pago (se asigna automáticamente)    |

---

## URL base

```
http://localhost:8086
```

---

## Endpoints

### Pagos — `/api/v1/pagos`

| Método | Ruta                                                       | Descripción                                            | Rol requerido                    |
|--------|------------------------------------------------------------|--------------------------------------------------------|----------------------------------|
| GET    | `/api/v1/pagos/usuario/{usuarioId}/ultimo-exitoso`         | Verificar si el usuario tiene un pago exitoso reciente | CLIENTE (dueño)                  |
| GET    | `/api/v1/pagos/usuario/{usuarioId}/ultimo-exitoso-detalle` | Obtener el detalle del último pago exitoso             | CLIENTE (dueño)                  |
| POST   | `/api/v1/pagos`                                            | Procesar un pago                                       | CLIENTE                          |
| GET    | `/api/v1/pagos`                                            | Listar todos los pagos                                 | FUNCIONARIO                      |
| GET    | `/api/v1/pagos/{id}`                                       | Obtener un pago por id                                 | FUNCIONARIO o CLIENTE (dueño)    |

**Métodos de pago soportados:** `TARJETA`, `CREDITO`, `EFECTIVO`.

**Validaciones:**
- El carrito no puede estar vacío ni el método de pago ser inválido al procesar un pago.
- Un cliente solo puede procesar pagos y consultar el detalle a nombre de su propio usuario (verificado contra el id del token JWT).
- Un `FUNCIONARIO` puede listar todos los pagos y ver el detalle de cualquier pago por id.
- El monto del pago debe ser mayor a 0.
- El `compraId` es obligatorio.
- La fecha del pago se asigna automáticamente al momento del procesamiento.

---

## Comunicación con otros servicios

**Vía Feign (síncrona)**

| Cliente          | Servicio destino | Endpoint consumido                      | Motivo                                  |
|------------------|------------------|-----------------------------------------|-----------------------------------------|
| `CarritoClient`  | carrito          | `GET /api/v1/carts/user/{userId}/total` | Obtener el total del carrito a cobrar   |

Este servicio, a su vez, es consumido vía Feign por `compra` para verificar que exista un pago exitoso antes de confirmar la compra.

---

## Modelo de base de datos

```
pago
├── id            (PK)
├── usuario_id     (not null)
├── monto          (not null)
├── metodo         (not null — TARJETA | CREDITO | EFECTIVO)
├── exitoso        (not null)
└── fecha_pago     (not null)
```
 
---

## Pruebas unitarias

| Clase de test  | Métodos cubiertos                                                        |
|----------------|--------------------------------------------------------------------------|
| `PagoImplTest` | Procesar pago, verificar último exitoso, detalle, listar, obtener por id |

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
