# Microservicio de Seguimiento

Microservicio encargado de registrar y consultar el estado de una compra a lo largo del tiempo (por ejemplo: `PREPARACION`, `ENVIADO`, `ENTREGADO`). 
Recibe automáticamente un registro inicial al completarse una compra, vía Kafka.

---

## Configuración

**Puerto:** `8088`  
**Nombre de la aplicación:** `seguimiento`
**Base de datos:** `db_seguimiento`

**OpenAPI**
```
http://localhost:8088/swagger-ui.html
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

### Seguimiento — `/api/v1/seguimientos`

| Método | Ruta                                        | Descripción                                        | Rol requerido                 |
|--------|--------------------------------------------------|---------------------------------------------------------|---------------------------------|
| GET    | `/api/v1/seguimientos`                             | Listar todos los seguimientos del sistema                  | FUNCIONARIO                     |
| GET    | `/api/v1/seguimientos/compra/{compraId}`           | Obtener el historial de estados de una compra                | FUNCIONARIO o CLIENTE (dueño)   |
| GET    | `/api/v1/seguimientos/usuario/{usuarioId}`         | Listar los seguimientos de todas las compras de un usuario     | FUNCIONARIO o CLIENTE (dueño)   |
| POST   | `/api/v1/seguimientos`                             | Registrar un nuevo estado de seguimiento manualmente           | FUNCIONARIO                     |
| GET    | `/api/v1/seguimientos/{id}`                        | Obtener un seguimiento por id                                  | FUNCIONARIO o CLIENTE (dueño)   |

**Validaciones:**
- Un `FUNCIONARIO` puede ver el seguimiento de cualquier compra o usuario, y registrar estados manualmente.
- Un `CLIENTE` solo puede ver el historial de sus propias compras (verificado contra el id del token JWT).
- El estado debe ser uno de los valores válidos: `PENDIENTE`, `PREPARACION`, `ENVIADO` o `ENTREGADO`. Cualquier otro valor retorna error.
- El `compraId` es obligatorio.
- Cada registro de seguimiento es independiente — se pueden registrar múltiples cambios de estado para la misma compra, generando un historial.
- La fecha de actualización se asigna automáticamente al momento del registro.

---

## Comunicación con otros servicios

**Vía Kafka (asíncrona)**

| Tópico consumido    | Grupo               | Acción disparada                                           |
|---------------------|---------------------|------------------------------------------------------------|
| `compra-completada` | `seguimiento-group` | Crea el registro inicial de seguimiento para la compra     |
 
---

## Modelo de base de datos

```
seguimiento
├── id                    (PK)
├── compra_id              (not null)
├── usuario_id             (not null)
├── estado                 (not null)
└── fecha_actualizacion    (not null)
```
 
---

## Pruebas unitarias

| Clase de test         | Métodos cubiertos                                                        |
|-----------------------|--------------------------------------------------------------------------|
| `SeguimientoImplTest` | Listar todos, historial por compra, listar por usuario, registrar estado |

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**
