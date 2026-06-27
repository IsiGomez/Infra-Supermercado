# Microservicio de Notificaciones

Microservicio encargado del envío y gestión de notificaciones a los usuarios del sistema de supermercado. Permite enviar mensajes a un usuario, consultar sus notificaciones y marcarlas como leídas.

---

## Configuración

**Puerto:** `8089`  
**Base de datos:** `db_notificaciones`

**OpenAPI**
```
http://localhost:8089/swagger-ui.html
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

### `notificacion`
| Campo       | Tipo         | Descripción                                          |
|-------------|--------------|------------------------------------------------------|
| id          | BIGINT (PK)  | Identificador único de la notificación               |
| user_id     | BIGINT       | ID del usuario destinatario                          |
| mensaje     | VARCHAR(500) | Contenido del mensaje                                |
| fecha_envio | DATETIME     | Fecha y hora de envío (se asigna automáticamente)    |
| enviado     | BOOLEAN      | Indica si la notificación fue enviada                |
| leido       | BOOLEAN      | Indica si el usuario la marcó como leída             |

---

## URL base

```
http://localhost:8089
```

---

## Endpoints

### Notificaciones — `/api/v1/notificaciones`

| Método | Ruta                       | Descripción                                      |
|--------|----------------------------|--------------------------------------------------|
| POST   | `/`                        | Enviar una notificación a un usuario             |
| GET    | `/`                        | Listar todas las notificaciones                  |
| GET    | `/usuario/{usuarioId}`     | Listar notificaciones de un usuario específico   |
| PUT    | `/{id}/leida`              | Marcar una notificación como leída               |

---

### POST `/api/v1/notificaciones`

Envía una notificación a un usuario del sistema.

**Body (JSON):**
```json
{
  "usuarioId": 1,
  "mensaje": "Tu pedido #1 ha sido enviado y llegará pronto."
}
```

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "mensaje": "Tu pedido #1 ha sido enviado y llegará pronto.",
  "fechaEnvio": "2025-05-29T18:15:00",
  "enviado": false,
  "leido": false
}
```

---

### GET `/api/v1/notificaciones/usuario/{usuarioId}`

Retorna todas las notificaciones de un usuario específico.

**Ejemplo:** `GET http://localhost:8089/api/v1/notificaciones/usuario/1`

---

### PUT `/api/v1/notificaciones/{id}/leida`

Marca una notificación como leída.

**Ejemplo:** `PUT http://localhost:8089/api/v1/notificaciones/1/leida`

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "mensaje": "Tu pedido #1 ha sido enviado y llegará pronto.",
  "fechaEnvio": "2025-05-29T18:15:00",
  "enviado": false,
  "leido": true
}
```

---

## Reglas de negocio

- El `usuarioId` es obligatorio y debe ser un valor válido.
- El `mensaje` es obligatorio y no puede estar vacío.
- La fecha de envío se asigna automáticamente al momento de crear la notificación.
- Las notificaciones se crean con `leido: false` por defecto.
- El endpoint `PUT /{id}/leida` actualiza únicamente el campo `leido` a `true`.

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
