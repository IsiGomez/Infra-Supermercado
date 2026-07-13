# Microservicio de Carrito

Microservicio encargado de la gestión del carrito de compras del sistema de supermercado.
Permite agregar/actualizar/quitar productos del carrito (validando stock contra `inventario` y datos contra `catalogo`), 
aplicar promociones (`promociones`) y canjear puntos de fidelización como descuento (`puntos`).

---

## Configuración

**Puerto:** `8084`  
**Nombre de la aplicación:** `carrito`  
**Base de datos:** `db_carrito`

**OpenAPI**
```
http://localhost:8084/swagger-ui.html
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
- Spring Cloud OpenFeign (comunicación síncrona con `inventario`, `catalogo`, `puntos` y `promociones`)
- Spring HATEOAS
- Springdoc OpenAPI (Swagger UI)
- Docker

---

## Endpoints

### Carrito — `/api/v1/carts`

Todas las rutas de este servicio requieren rol `CLIENTE` — no hay operaciones para `FUNCIONARIO` 
(el carrito es siempre el de quien está autenticado, verificado contra el `userId` del token).

| Método | Ruta                                           | Descripción                                        |
|--------|------------------------------------------------|----------------------------------------------------|
| GET    | `/api/v1/carts/user/{userId}/total`            | Obtener el total actual del carrito                |
| GET    | `/api/v1/carts/user/{userId}`                  | Obtener el carrito completo del usuario            |
| POST   | `/api/v1/carts/user/{userId}/item`             | Agregar un producto al carrito                     |
| PATCH  | `/api/v1/carts/user/{userId}/item/{productId}` | Actualizar la cantidad de un producto ya existente |
| DELETE | `/api/v1/carts/user/{userId}/item/{productId}` | Eliminar un producto del carrito                   |
| DELETE | `/api/v1/carts/user/{userId}/clear`            | Vaciar el carrito completo                         |
| POST   | `/api/v1/carts/user/{userId}/promocion`        | Aplicar una promoción por código                   |
| GET    | `/api/v1/carts/user/{userId}/canje/simular`    | Simular canje de puntos (paso 1 de 2)              |
| POST   | `/api/v1/carts/user/{userId}/canje/confirmar`  | Confirmar canje de puntos (paso 2 de 2)            |

**Validaciones:**
- Solo el propio usuario puede operar sobre su carrito (validación por JWT)
- No se puede agregar un producto que ya existe en el carrito
- No se puede agregar ni actualizar si el stock disponible es insuficiente
- No se puede aplicar una promoción inválida, expirada o a un carrito vacío
- No se puede simular ni confirmar canje de puntos con el carrito vacío

---

## Flujo de canje de puntos (2 pasos)

1. **Simular** (`GET .../canje/simular`): consulta a `puntos` cuántos puntos tiene el usuario y calcula el descuento equivalente, sin aplicar nada todavía.
2. **Confirmar** (`POST .../canje/confirmar`): descuenta los puntos en `puntos` y aplica el descuento al total del carrito.

---

## Modelo de base de datos

```
cart
├── id       (PK)
├── user_id
└── total    (por defecto 0)

cart_item
├── id          (PK)
├── cart_id     (FK → cart)
├── product_id
├── quantity
└── subtotal
```

---

## Pruebas unitarias

Los tests cubren la capa de servicio con JUnit 5 + Mockito:

| Clase de test  | Métodos cubiertos                                                                                    |
|----------------|------------------------------------------------------------------------------------------------------|
| `CartImplTest` | Total, obtener carrito, agregar/actualizar/eliminar item, vaciar, aplicar promoción, canje de puntos |

---

## Datos de prueba

**Carritos**

| ID | Usuario ID | Total |
|----|------------|-------|
| 1  | _2_        | _0_   |

> El carrito se crea automáticamente la primera vez que el usuario realiza cualquier operación sobre él.

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**