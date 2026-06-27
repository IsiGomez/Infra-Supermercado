# Microservicio de Pago

Microservicio encargado del procesamiento de pagos asociados a compras en el sistema de supermercado. Permite registrar un pago indicando el método utilizado y consultar el historial de pagos por compra o de forma general.

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

| Método | Ruta                    | Descripción                           |
|--------|-------------------------|---------------------------------------|
| POST   | `/`                     | Procesar un pago                      |
| GET    | `/`                     | Listar todos los pagos                |
| GET    | `/compra/{compraId}`    | Listar pagos de una compra específica |

---

### POST `/api/v1/pagos`

Registra y procesa un pago para una compra.

**Body (JSON):**
```json
{
  "compraId": 1,
  "monto": 15870.00,
  "metodo": "TARJETA"
}
```

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "compraId": 1,
  "monto": 15870.00,
  "metodo": "TARJETA",
  "exitoso": true,
  "fechaPago": "2025-05-29T18:05:00"
}
```

---

### GET `/api/v1/pagos/compra/{compraId}`

Retorna todos los pagos registrados para una compra específica.

**Ejemplo:** `GET http://localhost:8086/api/v1/pagos/compra/1`

---

## Reglas de negocio

- El método de pago debe ser uno de los valores válidos: `TARJETA`, `CREDITO` o `EFECTIVO`. Cualquier otro valor retorna error.
- El monto del pago debe ser mayor a 0.
- El `compraId` es obligatorio.
- Todos los métodos de pago (`TARJETA`, `CREDITO`, `EFECTIVO`) son procesados como exitosos (`exitoso: true`).
- La fecha del pago se asigna automáticamente al momento del procesamiento.

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
