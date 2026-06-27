# Microservicio de Puntos

Microservicio encargado del sistema de puntos de fidelización del supermercado. Permite acumular puntos automáticamente en base al monto de una compra y consultar el saldo de puntos de un usuario. También recibe eventos de Kafka para asignación automática de puntos.

---

## Configuración

**Puerto:** `8090`  
**Base de datos:** `db_puntos`

**OpenAPI**
```
http://localhost:8090/swagger-ui.html
```

**Eureka**
```
http://localhost:8761/
```

---

## Base de datos

Las tablas son creadas automáticamente por Flyway al iniciar la aplicación.

### `puntos`
| Campo             | Tipo        | Descripción                                          |
|-------------------|-------------|------------------------------------------------------|
| id                | BIGINT (PK) | Identificador único del registro                     |
| user_id           | BIGINT      | ID del usuario (único — un registro por usuario)     |
| puntos_acumulados | INT         | Total de puntos acumulados por el usuario (≥ 0)      |

---

## URL base

```
http://localhost:8090
```

---

## Endpoints

### Puntos — `/api/v1/puntos`

| Método | Ruta                | Descripción                              |
|--------|---------------------|------------------------------------------|
| POST   | `/`                 | Asignar puntos a un usuario por compra   |
| GET    | `/{usuarioId}`      | Consultar puntos acumulados de un usuario |

---

### POST `/api/v1/puntos`

Asigna puntos a un usuario en base al monto de su compra.

> **Cálculo:** `puntos = monto_compra / 100` (parte entera).  
> Ejemplo: una compra de $15.870 otorga **158 puntos**.

Si el usuario ya tiene un registro de puntos, los nuevos puntos se **suman** a los acumulados. Si no tiene registro previo, se crea uno nuevo.

**Body (JSON):**
```json
{
  "usuarioId": 1,
  "montoCompra": 15870.00
}
```

**Respuesta (200 OK):**
```json
{
  "usuarioId": 1,
  "puntosAcumulados": 158
}
```

---

### GET `/api/v1/puntos/{usuarioId}`

Retorna el saldo total de puntos acumulados de un usuario.

**Ejemplo:** `GET http://localhost:8090/api/v1/puntos/1`

**Respuesta (200 OK):**
```json
{
  "usuarioId": 1,
  "puntosAcumulados": 158
}
```

> Si el usuario no tiene puntos registrados, retorna error.

---

## Reglas de negocio

- El `usuarioId` es obligatorio.
- El `montoCompra` debe ser mayor a 0.
- Los puntos se calculan como `(int)(montoCompra / 100)`.
- Cada usuario tiene un único registro de puntos — los puntos nuevos se acumulan sobre el saldo existente.
- Los puntos acumulados no pueden ser negativos.
- El microservicio también escucha eventos de **Kafka** para recibir asignaciones de puntos de forma automática desde otros servicios.

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
