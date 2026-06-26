# Microservicio de Promociones

Microservicio encargado de la gestión de promociones y descuentos del sistema de supermercado. Permite crear promociones con código único, porcentaje de descuento y rango de fechas de vigencia, y consultarlas por código o listado general.

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

## Base de datos

Las tablas son creadas automáticamente por Flyway al iniciar la aplicación.

### `promocion`
| Campo        | Tipo         | Descripción                                        |
|--------------|--------------|----------------------------------------------------|
| id           | BIGINT (PK)  | Identificador único de la promoción                |
| codigo       | VARCHAR(50)  | Código único de la promoción                       |
| descuento    | DOUBLE       | Porcentaje de descuento (entre 0.1 y 100)          |
| fecha_inicio | DATE         | Fecha de inicio de la vigencia                     |
| fecha_fin    | DATE         | Fecha de fin de la vigencia (≥ fecha_inicio)       |
| acumulable   | BOOLEAN      | Indica si la promoción es acumulable con otras     |

**Datos iniciales:**
| codigo        | descuento | fecha_inicio | fecha_fin  | acumulable |
|---------------|-----------|--------------|------------|------------|
| BIENVENIDO10  | 10.0      | 2025-01-01   | 2025-12-31 | false      |
| VERANO20      | 20.0      | 2025-12-01   | 2026-02-28 | true       |

---

## URL base

```
http://localhost:8087
```

---

## Endpoints

### Promociones — `/api/v1/promociones`

| Método | Ruta          | Descripción                              |
|--------|---------------|------------------------------------------|
| POST   | `/`           | Crear nueva promoción                    |
| GET    | `/`           | Listar todas las promociones             |
| GET    | `/{codigo}`   | Obtener una promoción por su código      |

---

### POST `/api/v1/promociones`

Registra una nueva promoción en el sistema.

**Body (JSON):**
```json
{
  "codigo": "NAVIDAD25",
  "descuento": 25.0,
  "fechaInicio": "2025-12-20",
  "fechaFin": "2025-12-31",
  "acumulable": false
}
```

**Respuesta (200 OK):**
```json
{
  "id": 3,
  "codigo": "NAVIDAD25",
  "descuento": 25.0,
  "fechaInicio": "2025-12-20",
  "fechaFin": "2025-12-31",
  "acumulable": false
}
```

---

### GET `/api/v1/promociones/{codigo}`

Retorna una promoción buscada por su código único.

**Ejemplo:** `GET http://localhost:8087/api/v1/promociones/BIENVENIDO10`

---

## Reglas de negocio

- El código de la promoción debe ser único.
- El descuento debe ser mayor a 0 y menor o igual a 100 (es un porcentaje).
- La `fechaFin` debe ser igual o posterior a la `fechaInicio`.
- Todos los campos son obligatorios.
- Las fechas deben enviarse en formato `YYYY-MM-DD`.

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
