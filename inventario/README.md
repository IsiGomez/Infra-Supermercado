# Microservicio de Inventario

Microservicio encargado de la gestión del stock de productos del sistema de supermercado. Permite consultar y actualizar la cantidad disponible de cada producto en inventario, con validaciones como stock no negativo y límite máximo por producto. Se comunica con el microservicio de catálogo para enriquecer las respuestas con datos del producto, y consume eventos Kafka para descontar stock automáticamente al completarse una compra.

---

## Configuración

**Puerto:** `8083`  
**Nombre de la aplicación:** `inventario`  
**Base de datos:** `db_inventario`

**OpenAPI**
```
http://localhost:8083/swagger-ui.html
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
- Apache Kafka (consumidor)
- Springdoc OpenAPI (Swagger UI)
- Docker

---

## Endpoints

### Inventario — `/api/v1/inventory`

| Método | Ruta                              | Descripción                       | Rol requerido          |
|--------|-----------------------------------|-----------------------------------|------------------------|
| GET    | `/api/v1/inventory/product/{id}`  | Obtener stock de un producto      | FUNCIONARIO o CLIENTE  |
| PATCH  | `/api/v1/inventory/update`        | Actualizar stock de un producto   | FUNCIONARIO            |

**Validaciones:**
- El stock no puede ser negativo (mínimo 0)
- El stock no puede superar 150 unidades por producto
- El producto debe existir en el catálogo (verificación via Feign)
- Si el producto no tiene registro previo de inventario, se crea uno nuevo automáticamente

---

## Comunicación entre servicios

### Feign Client — ms-catalogo
Al consultar o actualizar stock, se llama al catálogo para obtener el nombre y precio del producto y enriquecer la respuesta.

| Servicio destino | Endpoint consumido              | Propósito                         |
|------------------|---------------------------------|-----------------------------------|
| `catalogo`       | `GET /api/v1/products/{id}`     | Obtener datos del producto        |

### Kafka Consumer — topic `compra-completada`
Al completarse una compra, el inventario descuenta automáticamente el stock de cada producto comprado.

| Topic              | Grupo              | Acción                                      |
|--------------------|--------------------|---------------------------------------------|
| `compra-completada`| `inventario-group` | Descuenta la cantidad comprada por producto |

---

## Modelo de base de datos

```
inventario
├── id          (PK)
├── product_id  (unique)
└── quantity    (>= 0)
```

---

## Pruebas unitarias

Los tests cubren la capa de servicio con JUnit 5 + Mockito:

| Clase de test       | Métodos cubiertos                                                                                      |
|---------------------|--------------------------------------------------------------------------------------------------------|
| `InventoryImplTest` | consultarStock (registrado / no registrado), actualizarStock (registro existente / registro nuevo) |

---

## Datos de prueba

| ID | Producto ID | Cantidad |
|----|-------------|----------|
| 1  | 1           | 45       |
| 2  | 2           | 30       |

> Los datos de producto (nombre y precio) son obtenidos en tiempo real desde el ms-catalogo.

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**