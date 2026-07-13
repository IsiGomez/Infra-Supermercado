# Microservicio de Usuarios

Microservicio encargado de la gestión de personas, cuentas de acceso y roles del sistema de supermercado. 
Permite crear, consultar, actualizar y eliminar personas y cuentas, con validaciones como RUT único, email único, username único y asociación correcta entre persona y rol.
Además es el único que emite tokens JWT para la autenticación.

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
- Spring Security + JWT (BCrypt para el hash de contraseñas)
- Spring Data JPA + Flyway
- Spring Cloud Eureka Client
- Spring HATEOAS
- Springdoc OpenAPI (Swagger UI)
- Docker

---

## Flujo de alta de un usuario

1. `POST /api/v1/persons` — crea los datos personales (rut, nombre, email, teléfono). **Público.**
2. `POST /api/v1/logins` — crea la cuenta de acceso (username, password), asociada a la persona creada y a un `Rol` (`CLIENTE` o `FUNCIONARIO`). **Público.**
3. `POST /api/v1/auth` — autentica con username/password y devuelve el JWT. **Público.**

Los 3 pasos son necesariamente públicos: sin ellos nadie podría registrarse ni obtener su primer token

---

## Endpoints

### Autenticación — `/api/v1/auth`

| Método | Ruta              | Descripción                  | Acceso   |
|--------|-------------------|------------------------------|----------|
| POST   | `/api/v1/auth`    | Autenticarse y obtener JWT   | Público  |

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

| Método | Ruta                   | Descripción                             | Rol requerido          |
|--------|------------------------|-----------------------------------------|------------------------|
| POST   | `/api/v1/persons`      | Crear información de persona (registro) | Público                |
| GET    | `/api/v1/persons`      | Listar todas las personas               | FUNCIONARIO            |
| GET    | `/api/v1/persons/{id}` | Obtener persona por id                  | FUNCIONARIO o CLIENTE  |
| PUT    | `/api/v1/persons/{id}` | Actualizar persona existente            | FUNCIONARIO o CLIENTE  |
| DELETE | `/api/v1/persons/{id}` | Eliminar información de persona         | FUNCIONARIO o CLIENTE  |

**Validaciones:**
- RUT único en el sistema
- Email único en el sistema

---

### Cuentas — `/api/v1/logins`

| Método | Ruta                      | Descripción                    | Rol requerido          |
|--------|---------------------------|--------------------------------|------------------------|
| POST   | `/api/v1/logins`          | Crear nueva cuenta (registro)  | Público                |
| GET    | `/api/v1/logins`          | Listar todas las cuentas       | FUNCIONARIO            |
| GET    | `/api/v1/logins/{id}`     | Obtener cuenta por id          | FUNCIONARIO o CLIENTE  |
| GET    | `/api/v1/logins/{id}/rol` | Obtener el rol de una cuenta   | FUNCIONARIO o CLIENTE  |
| PUT    | `/api/v1/logins/{id}`     | Actualizar cuenta existente    | FUNCIONARIO o CLIENTE  |
| DELETE | `/api/v1/logins/{id}`     | Eliminar cuenta                | FUNCIONARIO o CLIENTE  |

**Validaciones:**
- Username único en el sistema
- Una persona solo puede tener un login asociado

---

### Roles — `/api/v1/roles`

| Método | Ruta                 | Descripción             | Acceso   |
|--------|----------------------|-------------------------|----------|
| GET    | `/api/v1/roles`      | Obtener todos los roles | Público  |
| GET    | `/api/v1/roles/{id}` | Obtener rol por ID      | Público  |

**Roles disponibles (preinsertados):**

| ID | Nombre      | Descripción         |
|----|-------------|---------------------|
| 1  | FUNCIONARIO | Controla el sistema |
| 2  | CLIENTE     | Ocupa el sistema    |

---

## JWT

- El token usa un secreto compartido (`app.jwt.secret`), el mismo que usa `api-gateway` para poder validar el token sin llamar de vuelta a `usuarios`.
- El claim principal es el `id` de la persona (`Long`), recuperable en cualquier microservicio con `SecurityUtil.currentUserId()`.
- El rol viaja como authority `ROLE_CLIENTE` o `ROLE_FUNCIONARIO`.

---

## Modelo de base de datos

```
rol
├── id           (PK)
├── name         (unique) (CLIENTE | FUNCIONARIO)
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
├── password    (BCrypt)
├── person_id   (FK → person, unique)
└── rol_id      (FK → rol)
```

---

## Pruebas unitarias

Los tests cubren la capa de servicio con JUnit 5 + Mockito:

| Clase de test       | Métodos cubiertos                           |
|---------------------|---------------------------------------------|
| `PersonImplTest`    | CRUD completo de personas                   |
| `LoginImplTest`     | CRUD de cuentas, obtención de rol asociado  |
| `RolImplTest`       | Listado y obtención de roles                |
| `AuthImplTest`      | Login exitoso, credenciales incorrectas     |
| `JwtServiceTest`    | Generación y validación de tokens           |

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