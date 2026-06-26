# Microservicio de Seguimiento

Microservicio encargado del seguimiento del estado de los pedidos asociados a compras en el sistema de supermercado. Permite registrar actualizaciones de estado para una compra y consultar su historial de seguimiento.

---

## Configuración

**Puerto:** `8088`  
**Base de datos:** `db_seguimiento`

**OpenAPI**
```
http://localhost:8088/swagger-ui.html
```

**Eureka**
```
http://localhost:8761/
```

---

## Base de datos

Las tablas son creadas automáticamente por Flyway al iniciar la aplicación.

### `seguimiento`
| Campo               | Tipo         | Descripción                                                          |
|---------------------|--------------|----------------------------------------------------------------------|
| id                  | BIGINT (PK)  | Identificador único del registro                                     |
| compra_id           | BIGINT       | ID de la compra asociada                                             |
| estado              | VARCHAR(20)  | Estado del pedido (`PENDIENTE`, `PREPARACION`, `ENVIADO`, `ENTREGADO`) |
| fecha_actualizacion | DATETIME     | Fecha y hora del registro (se asigna automáticamente)                |

---

## URL base

```
http://localhost:8088
```

---

## Endpoints

### Seguimiento — `/api/v1/seguimientos`

| Método | Ruta                     | Descripción                                      |
|--------|--------------------------|--------------------------------------------------|
| POST   | `/`                      | Registrar un nuevo estado de seguimiento         |
| GET    | `/`                      | Listar todos los seguimientos                    |
| GET    | `/compra/{compraId}`     | Obtener historial de seguimiento de una compra   |

---

### POST `/api/v1/seguimientos`

Registra un nuevo estado para el seguimiento de una compra.

**Body (JSON):**
```json
{
  "compraId": 1,
  "estado": "PREPARACION"
}
```

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "compraId": 1,
  "estado": "PREPARACION",
  "fechaActualizacion": "2025-05-29T18:10:00"
}
```

---

### GET `/api/v1/seguimientos/compra/{compraId}`

Retorna el historial completo de estados de seguimiento para una compra específica, ordenado cronológicamente.

**Ejemplo:** `GET http://localhost:8088/api/v1/seguimientos/compra/1`

---

## Reglas de negocio

- El estado debe ser uno de los valores válidos: `PENDIENTE`, `PREPARACION`, `ENVIADO` o `ENTREGADO`. Cualquier otro valor retorna error.
- El `compraId` es obligatorio.
- Cada registro de seguimiento es independiente — se pueden registrar múltiples cambios de estado para la misma compra, generando un historial.
- La fecha de actualización se asigna automáticamente al momento del registro.

**Flujo típico de estados:**
```
PENDIENTE → PREPARACION → ENVIADO → ENTREGADO
```

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
