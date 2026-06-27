# Microservicio de Usuarios

Microservicio encargado de la gestión de personas, cuentas de acceso y roles del sistema de supermercado. Permite crear, consultar, actualizar y eliminar personas y cuentas, con validaciones como RUT único, email único, username único y asociación correcta entre persona y rol.

---

## Configuración

**Puerto:** `8081`  
**Nombre de la aplicación:** `usuarios`     
**Base de datos:** `db_usuarios`

**OpenAPI**
```
http://localhost:8081/swagger-ui.html
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

---

## Endpoints

### Autenticación — `/api/v1/auth`

| Método | Ruta           | Descripción                |
|--------|----------------|----------------------------|
| POST   | `/api/v1/auth` | Autenticarse y obtener JWT |

**Body de ejemplo:**
```json
{
  "username": "FunClaudia",
  "password": "password"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "FunClaudia",
  "rol": "FUNCIONARIO"
}
```

---

### Personas — `/api/v1/persons`

| Método | Ruta                   | Descripción                  |
|--------|------------------------|------------------------------|
| GET    | `/api/v1/persons`      | Obtener todas las personas   | 
| GET    | `/api/v1/persons/{id}` | Obtener persona por ID       | 
| POST   | `/api/v1/persons`      | Crear nueva persona          |
| PUT    | `/api/v1/persons/{id}` | Actualizar persona existente | 
| DELETE | `/api/v1/persons/{id}` | Eliminar persona por ID      |

**Validaciones:**
- RUT único en el sistema
- Email único en el sistema

---

### Cuentas — `/api/v1/logins`

| Método | Ruta                      | Descripción                  |
|--------|---------------------------|------------------------------|
| GET    | `/api/v1/logins`          | Obtener todas las cuentas    |
| GET    | `/api/v1/logins/{id}`     | Obtener cuenta por ID        |
| GET    | `/api/v1/logins/{id}/rol` | Obtener el rol de una cuenta |
| POST   | `/api/v1/logins`          | Crear nueva cuenta           |
| PUT    | `/api/v1/logins/{id}`     | Actualizar cuenta existente  |
| DELETE | `/api/v1/logins/{id}`     | Eliminar cuenta por ID       |

**Validaciones:**
- Username único en el sistema
- Una persona solo puede tener un login asociado

---

### Roles — `/api/v1/roles`

| Método | Ruta                 | Descripción             |
|--------|----------------------|-------------------------|
| GET    | `/api/v1/roles`      | Obtener todos los roles |
| GET    | `/api/v1/roles/{id}` | Obtener rol por ID      |

**Roles disponibles (preinsertados):**

| ID | Nombre      | Descripción         |
|----|-------------|---------------------|
| 1  | FUNCIONARIO | Controla el sistema |
| 2  | CLIENTE     | Ocupa el sistema    |

---

## Modelo de base de datos

```
rol
├── id           (PK)
├── name         (unique)
└── description

person
├── id          (PK)
├── rut         (unique)
├── name
├── last_name
├── email       (unique)
└── phone

login
├── id          (PK)
├── username    (unique)
├── password    (bcrypt)
├── person_id   (FK → person, unique)
└── rol_id      (FK → rol)
```

---

## Pruebas unitarias

Los tests cubren la capa de servicio con JUnit 5 + Mockito:

| Clase de test    | Métodos cubiertos                                                        |
|------------------|--------------------------------------------------------------------------|
| `PersonImplTest` | getById, getAll, create (rut único, email único), update, delete         |
| `LoginImplTest`  | getById, getAll, create (username único, persona ya tiene login), delete |
| `RolImplTest`    | getById (existe / no existe), getAll (con datos / vacío)                 |

---

## Datos de prueba

**Login**

| Username   | Password          | Rol         |
|------------|-------------------|-------------|
| FunClaudia | _(ver migration)_ | FUNCIONARIO |
| Juanito    | _(ver migration)_ | CLIENTE     |        

**Person**

| Id | Rut        | Name    | Last Name  | Email                     | Phone        |
|----|------------|---------|------------|---------------------------|--------------|
| 1  | 12342885-3 | Claudia | Gonzales   | clau.gon@funcionarios.com | +56923542352 |
| 2  | 99853188-4 | Juan    | Perez      | ju.perez@client.com       | +56913541254 |

> Las contraseñas están hasheadas con BCrypt.   
> Ademas usa el endpoint `/api/v1/auth` para obtener el token JWT.

---

### Integrantes

**- Isidora Gómez**

**- Rayen Bettancourt**