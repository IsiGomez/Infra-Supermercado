# Microservicio de Catálogo

Microservicio encargado de la gestión de productos y categorías del sistema de supermercado. 
Permite crear, consultar, actualizar y eliminar productos y categorías, con validaciones como nombre único, precio dentro de rango válido y categoría existente al asociar un producto.

Es consultado vía Feign por `carrito` (para obtener datos del producto al agregarlo) e `inventario` (para validar que un producto exista antes de actualizar su stock).

---

## Configuración

**Puerto:** `8082`  
**Nombre de la aplicación:** `catalogo`  
**Base de datos:** `db_catalogo`

**OpenAPI**
```
http://localhost:8082/swagger-ui.html
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
- Spring HATEOAS
- Springdoc OpenAPI (Swagger UI)
- Docker

---

## Endpoints

### Categorías — `/api/v1/categories`

| Método | Ruta                        | Descripción                    | Rol requerido          |
|--------|-----------------------------|--------------------------------|------------------------|
| GET    | `/api/v1/categories/{id}`   | Obtener categoría por ID       | FUNCIONARIO o CLIENTE  |
| GET    | `/api/v1/categories`        | Obtener todas las categorías   | FUNCIONARIO o CLIENTE  |
| POST   | `/api/v1/categories`        | Crear nueva categoría          | FUNCIONARIO            |
| PUT    | `/api/v1/categories/{id}`   | Actualizar categoría existente | FUNCIONARIO            |
| DELETE | `/api/v1/categories/{id}`   | Eliminar categoría por ID      | FUNCIONARIO            |

> Regla general de seguridad: cualquier lectura (`GET`) está disponible para ambos roles; cualquier escritura (`POST`/`PUT`/`DELETE`) requiere `FUNCIONARIO`.

**Validaciones:**
- Nombre único en el sistema (insensible a mayúsculas)
- No se puede eliminar una categoría que tiene productos asociados

---

### Productos — `/api/v1/products`

| Método | Ruta                                           | Descripción                             | Rol requerido         |
|--------|------------------------------------------------|-----------------------------------------|-----------------------|
| GET    | `/api/v1/products/{id}`                        | Obtener producto por ID                 | FUNCIONARIO o CLIENTE |
| GET    | `/api/v1/products/by-ids`                      | Obtener productos por lista de IDs      | FUNCIONARIO o CLIENTE |
| GET    | `/api/v1/products`                             | Obtener todos los productos             | FUNCIONARIO o CLIENTE |
| GET    | `/api/v1/products/search`                      | Buscar productos por nombre             | FUNCIONARIO o CLIENTE |
| GET    | `/api/v1/products/category/{categoryId}`       | Obtener productos por categoría         | FUNCIONARIO o CLIENTE |
| GET    | `/api/v1/products/category/{categoryId}/price` | Filtrar por categoría y rango de precio | FUNCIONARIO o CLIENTE |
| POST   | `/api/v1/products`                             | Crear nuevo producto                    | FUNCIONARIO           |
| PUT    | `/api/v1/products/{id}`                        | Actualizar producto existente           | FUNCIONARIO           |
| DELETE | `/api/v1/products/{id}`                        | Eliminar producto por ID                | FUNCIONARIO           |

**Validaciones:**
- Nombre único en el sistema (insensible a mayúsculas)
- Precio debe ser mayor a 0 y menor o igual a 1.000.000
- La categoría indicada debe existir
- Al buscar por múltiples IDs, todos deben existir
- El precio mínimo no puede ser mayor al precio máximo en filtro por rango
- Los precios del rango no pueden ser negativos

---

## Modelo de base de datos

```
category
├── id      (PK)
└── name    (unique)

product
├── id           (PK)
├── name         (unique)
├── description
├── price        (> 0 y <= 1.000.000)
└── category_id  (FK → category)
```

---

## Pruebas unitarias

Los tests cubren la capa de servicio con JUnit 5 + Mockito:

| Clase de test       | Métodos cubiertos                                             |
|---------------------|---------------------------------------------------------------|
| `ProductImplTest`   | CRUD, búsqueda por nombre, por categoría, por rango de precio |
| `CategoryImplTest`  | CRUD de categorías                                            |

---

## Datos de prueba

**Categorías**

| ID | Nombre              |
|----|---------------------|
| 1  | _Electródomesticos_ |
| 2  | _Hogar_             |

**Productos**

| ID | Nombre            | Descripción                                          | Precio | Categoría |
|----|-------------------|------------------------------------------------------|--------|-----------|
| 1  | _Hervidor_        | Calienta los líquidos                                | 50.000 | 1         |
| 2  | _Silla de madera_ | Mueble para sentarse, hecho de un material de madera | 3.000  | 2         |

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**