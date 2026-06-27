# Microservicio de Carrito

Microservicio encargado de la gestión del carrito de compras del sistema de supermercado. Permite agregar, actualizar y eliminar productos del carrito, aplicar promociones mediante código, y canjear puntos de fidelización como descuento. Se comunica con otros microservicios (catálogo, inventario, promociones y puntos) a través de Feign.

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
- Spring Cloud Eureka Client + OpenFeign
- Springdoc OpenAPI (Swagger UI)
- Docker

---

## Endpoints

### Carrito — `/api/v1/carts`

| Método | Ruta                                        | Descripción                                      |
|--------|---------------------------------------------|--------------------------------------------------|
| GET    | `/api/v1/carts/user/{userId}`               | Obtener el carrito completo del usuario          |
| GET    | `/api/v1/carts/user/{userId}/total`         | Obtener el total actual del carrito              |
| POST   | `/api/v1/carts/user/{userId}/item`          | Agregar un producto al carrito                   |
| PATCH  | `/api/v1/carts/user/{userId}/item/{productId}` | Actualizar la cantidad de un producto         |
| DELETE | `/api/v1/carts/user/{userId}/item/{productId}` | Eliminar un producto del carrito             |
| DELETE | `/api/v1/carts/user/{userId}/clear`         | Vaciar el carrito completo                       |
| POST   | `/api/v1/carts/user/{userId}/promocion`     | Aplicar una promoción por código                 |
| GET    | `/api/v1/carts/user/{userId}/canje/simular` | Simular canje de puntos (paso 1 de 2)            |
| POST   | `/api/v1/carts/user/{userId}/canje/confirmar` | Confirmar canje de puntos (paso 2 de 2)        |


**Validaciones:**
- Solo el propio usuario puede operar sobre su carrito (validación por JWT)
- No se puede agregar un producto que ya existe en el carrito
- No se puede agregar ni actualizar si el stock disponible es insuficiente
- No se puede aplicar una promoción inválida, expirada o a un carrito vacío
- No se puede simular ni confirmar canje de puntos con el carrito vacío

---

## Modelo de base de datos

```
cart
├── id       (PK)
├── user_id
└── total

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

| Clase de test   | Métodos cubiertos                                                                                                                                                                          |
|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `CartImplTest`  | getCart (crea carrito si no existe), addItem (agrega correctamente / producto no existe / stock insuficiente / producto duplicado), updateQuantity (actualiza / producto no en carrito), removeItem (elimina / producto no en carrito), clearCart (vacía y pone total en cero) |

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