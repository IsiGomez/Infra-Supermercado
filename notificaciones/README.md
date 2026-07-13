# Microservicio de Notificaciones

Microservicio encargado de generar y gestionar las notificaciones enviadas a los usuarios sobre el estado de sus compras. 
Recibe automáticamente una notificación al completarse una compra, vía Kafka, y también permite el envío manual por parte de un `FUNCIONARIO`.

---

## Configuración

**Puerto:** `8089`  
**Nombre de la aplicación:** `notificaciones`
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

## Herramientas

- Java 25 · Spring Boot 4.0.6
- Spring Security + JWT
- Spring Data JPA + Flyway
- Spring Cloud Eureka Client
- Spring Kafka (consumidor de eventos)
- Spring HATEOAS
- Springdoc OpenAPI (Swagger UI)
- Docker

---

## Endpoints

### Notificaciones — `/api/v1/notificaciones`

| Método | Ruta                                         | Descripción                            | Rol requerido                 |
|--------|----------------------------------------------|----------------------------------------|-------------------------------|
| GET    | `/api/v1/notificaciones/usuario/{usuarioId}` | Listar notificaciones de un usuario    | FUNCIONARIO o CLIENTE (dueño) |
| PUT    | `/api/v1/notificaciones/{id}/leida`          | Marcar una notificación como leída     | CLIENTE (dueño)               |
| POST   | `/api/v1/notificaciones`                     | Enviar una notificación manualmente    | FUNCIONARIO                   |
| GET    | `/api/v1/notificaciones/{id}`                | Obtener el detalle de una notificación | FUNCIONARIO o CLIENTE (dueño) |

**Validaciones:**
- Un cliente solo puede listar, marcar como leída y ver el detalle de sus propias notificaciones (verificado contra el id del token JWT, comparado contra el `usuarioId` real de la notificación — no contra un valor recibido en la URL).
- Un `FUNCIONARIO` puede ver el detalle de cualquier notificación y enviar notificaciones manuales, pero no puede marcarlas como leídas (esa acción es exclusiva de `CLIENTE`, ya que representa que el destinatario la leyó).
- El `usuarioId` es obligatorio y debe ser un valor válido.
- El `mensaje` es obligatorio y no puede estar vacío.
- La fecha de envío se asigna automáticamente al momento de crear la notificación.
- Las notificaciones se crean con `leido: false` por defecto.
- El endpoint `PUT /{id}/leida` actualiza únicamente el campo `leido` a `true`.


---

## Comunicación con otros servicios

**Vía Kafka (asíncrona)**

| Tópico consumido    | Grupo                  | Acción disparada                                    |
|---------------------|------------------------|-----------------------------------------------------|
| `compra-completada` | `notificaciones-group` | Genera automáticamente la notificación al usuario   |
 
---

## Modelo de base de datos

```
notificacion
├── id            (PK)
├── usuario_id     (not null)
├── mensaje        (not null)
├── fecha_envio    (not null)
├── enviado        (not null)
└── leido          (not null, por defecto false)
```
 
---

## Pruebas unitarias

| Clase de test           | Métodos cubiertos                                                          |
|-------------------------|----------------------------------------------------------------------------|
| `NotificacionImplTest`  | Enviar notificación, listar por usuario, marcar como leída, obtener por id |

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
