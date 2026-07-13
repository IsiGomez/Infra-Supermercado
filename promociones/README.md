# Microservicio de Promociones

Microservicio encargado de la gestión de promociones y descuentos del sistema de supermercado. 
Permite crear promociones con código único, porcentaje de descuento y rango de fechas de vigencia, y consultarlas por código o listado general.

---

## Configuración

**Puerto:** `8087`  
**Base de datos:** `db_promociones`

**OpenAPI**
```
http://localhost:8087/swagger-ui.html
```

**Eureka**
```
http://localhost:8761/
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

### Promociones — `/api/v1/promociones`

| Método | Ruta                              | Descripción                   | Rol requerido           |
|--------|-----------------------------------|-------------------------------|-------------------------|
| GET    | `/api/v1/promociones/vigentes`    | Listar promociones vigentes   | FUNCIONARIO o CLIENTE   |
| GET    | `/api/v1/promociones`             | Listar todas las promociones  | FUNCIONARIO o CLIENTE   |
| GET    | `/api/v1/promociones/{codigo}`    | Obtener promoción por código  | FUNCIONARIO o CLIENTE   |
| POST   | `/api/v1/promociones/user/create` | Crear una nueva promoción     | FUNCIONARIO             |

**Campos de una promoción:** código, porcentaje de descuento, fecha de inicio, fecha de fin, y si es acumulable con otras promociones.

**Validaciones:**
- El código de la promoción debe ser único.
- El descuento debe ser mayor a 0 y menor o igual a 100 (es un porcentaje).
- La `fechaFin` debe ser igual o posterior a la `fechaInicio`.
- Todos los campos son obligatorios.
- Las fechas deben enviarse en formato `YYYY-MM-DD`.

---

## Comunicación con otros servicios

Este servicio no llama a ningún otro microservicio. Es consumido vía Feign por:

| Cliente   | Endpoint consumido                 | Motivo                                          |
|-----------|------------------------------------|-------------------------------------------------|
| `carrito` | `GET /api/v1/promociones/{codigo}` | Validar el código antes de aplicarlo al carrito |

---

## Modelo de base de datos

```
promocion
├── id             (PK)
├── codigo          (not null)
├── descuento       (not null, porcentaje)
├── fecha_inicio    (not null)
├── fecha_fin       (not null)
└── acumulable      (not null)
```
 
---

## Pruebas unitarias

| Clase de test     - | Métodos cubiertos                                                  |
|---------------------|--------------------------------------------------------------------|
| `PromocionImplTest` | Listar vigentes, listar todas, obtener por código, crear promoción |

---

**Datos iniciales:**

| codigo        | descuento | fecha_inicio | fecha_fin  | acumulable |
|---------------|-----------|--------------|------------|------------|
| BIENVENIDO10  | 10.0      | 2025-01-01   | 2025-12-31 | false      |
| VERANO20      | 20.0      | 2025-12-01   | 2026-02-28 | true       |

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
