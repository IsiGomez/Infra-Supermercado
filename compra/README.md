# Microservicio de Compra

Microservicio encargado de confirmar y registrar las compras del sistema de supermercado. 
Verifica que el usuario tenga un pago exitoso registrado, obtiene su carrito actual, registra la compra, limpia el carrito y publica un evento que dispara la asignación automática de puntos, el seguimiento inicial y la notificación al usuario.

---

## Configuración

**Puerto:** `8085`
**Nombre de la aplicación:** `compra`
**Base de datos:** `db_compra`

**OpenAPI**
```
http://localhost:8085/swagger-ui.html
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
- Spring Cloud OpenFeign (comunicación síncrona con `carrito` y `pago`)
- Spring Kafka (publicación de eventos)
- Spring HATEOAS
- Springdoc OpenAPI (Swagger UI)
- Docker

---

## Endpoints

### Compras — `/api/v1/compras`

| Método | Ruta                                  | Descripción                                | Rol requerido            |
|--------|-----------------------------------------|--------------------------------------------|-----------------------------|
| POST   | `/api/v1/compras`                        | Crear y confirmar una compra                  | CLIENTE                     |
| GET    | `/api/v1/compras`                        | Listar todas las compras del sistema           | FUNCIONARIO                 |
| GET    | `/api/v1/compras/usuario/{usuarioId}`    | Listar historial de compras de un usuario       | CLIENTE (solo las propias)  |
| GET    | `/api/v1/compras/{id}`                   | Obtener el detalle de una compra específica     | FUNCIONARIO o CLIENTE (dueño) |

**Validaciones:**
- El usuario debe tener un pago exitoso registrado antes de confirmar la compra
- El carrito del usuario no puede estar vacío
- Un cliente solo puede crear compras y ver el historial y ver el detalle a nombre de su propio usuario (verificado contra el id del token JWT)
- Solo un usuario con rol `FUNCIONARIO` puede listar las compras de todos los usuarios y ver el detalle de cualquier compra por id, pero no puede consultar el historial de un usuario específico vía `/usuario/{usuarioId}`

---

## Flujo de creación de una compra

1. Se valida que el `usuarioId` del request coincida con el usuario autenticado en el token.
2. Se consulta al microservicio **Pago** (vía Feign) si el usuario tiene un último pago exitoso registrado.
3. Se consulta al microservicio **Carrito** (vía Feign) para obtener los items y el total del carrito del usuario.
4. Se registra la compra en la base de datos como `finalizada` y `pagoConfirmado`.
5. Se limpia el carrito del usuario mediante el microservicio **Carrito**.
6. Se publica el evento `compra-completada` en Kafka, consumido por los microservicios de **Inventario**, **Puntos**, **Seguimiento** y **Notificaciones**.

---

## Comunicación con otros servicios

**Vía Feign (síncrona)**

| Cliente          | Servicio destino   | Endpoint consumido                                         |
|------------------|--------------------|------------------------------------------------------------|
| `PagoClient`     | pago               | `GET /api/v1/pagos/usuario/{usuarioId}/ultimo-exitoso`     |
| `CarritoClient`  | carrito            | `GET /api/v1/carts/user/{userId}`                          |
| `CarritoClient`  | carrito            | `GET /api/v1/carts/user/{userId}/total`                    |
| `CarritoClient`  | carrito            | `DELETE /api/v1/carts/user/{userId}/clear`                 |

**Vía Kafka (asíncrona)**

| Evento publicado        | Tópico                | Consumido por                                   |
|-------------------------|-----------------------|-------------------------------------------------|
| `CompraCompletadaEvent` | `compra-completada`   | inventario, puntos, seguimiento, notificaciones |

---

## Modelo de base de datos

```
compra
├── id                (PK)
├── usuario_id        (not null)
├── total             (not null, >= 0)
├── fecha_compra      (not null)
├── finalizada        (not null)
└── pago_confirmado   (not null)
```

---

## Pruebas unitarias

Los tests cubren la capa de servicio con JUnit 5 + Mockito:

| Clase de test    | Métodos cubiertos                                                                                           |
|------------------|-------------------------------------------------------------------------------------------------------------|
| `CompraImplTest` | crearCompra (pago exitoso / sin pago / carrito vacío), listarCompras, listarComprasPorUsuario, obtenerPorId |

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**