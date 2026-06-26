# Microservicio de Catálogo

Microservicio encargado de la gestión de productos y categorías del sistema de supermercado. Permite crear, consultar, actualizar y eliminar productos y categorías, con validaciones como nombre único, precio dentro de rango válido y categoría existente al asociar un producto.

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
- Springdoc OpenAPI (Swagger UI)
- Docker

---

## Endpoints

### Categorías — `/api/v1/categories`

| Método | Ruta                       | Descripción                      |
|--------|----------------------------|----------------------------------|
| GET    | `/api/v1/categories`       | Obtener todas las categorías     |
| GET    | `/api/v1/categories/{id}`  | Obtener categoría por ID         |
| POST   | `/api/v1/categories`       | Crear nueva categoría            |
| PUT    | `/api/v1/categories/{id}`  | Actualizar categoría existente   |
| DELETE | `/api/v1/categories/{id}`  | Eliminar categoría por ID        |


**Validaciones:**
- Nombre único en el sistema (insensible a mayúsculas)
- No se puede eliminar una categoría que tiene productos asociados

---

### Productos — `/api/v1/products`

| Método | Ruta                                              | Descripción                               |
|--------|---------------------------------------------------|-------------------------------------------|
| GET    | `/api/v1/products`                                | Obtener todos los productos               |
| GET    | `/api/v1/products/{id}`                           | Obtener producto por ID                   |
| GET    | `/api/v1/products/by-ids`                         | Obtener productos por lista de IDs        |
| GET    | `/api/v1/products/search`                         | Buscar productos por nombre               |
| GET    | `/api/v1/products/category/{categoryId}`          | Obtener productos por categoría           |
| GET    | `/api/v1/products/category/{categoryId}/price`    | Filtrar por categoría y rango de precio   |
| POST   | `/api/v1/products`                                | Crear nuevo producto                      |
| PUT    | `/api/v1/products/{id}`                           | Actualizar producto existente             |
| DELETE | `/api/v1/products/{id}`                           | Eliminar producto por ID                  |


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

| Clase de test       | Métodos cubiertos                                                                                                          |
|---------------------|----------------------------------------------------------------------------------------------------------------------------|
| `CategoryImplTest`  | getById (existe / no existe), getAll (con datos / vacío), create (nombre único / duplicado), update (válido / no existe / nombre duplicado) |
| `ProductImplTest`   | getById, getByIds (todos existen / falta alguno), getByCategoryId, getByCategoryIdAndPriceBetween (min > max / precio negativo), create (nombre único / duplicado) |

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