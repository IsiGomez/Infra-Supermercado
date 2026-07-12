package cl.duoc.usuarios.controller;

import cl.duoc.usuarios.assemblers.LoginModelAssembler;
import cl.duoc.usuarios.config.SecurityUtil;
import cl.duoc.usuarios.dto.request.LoginRequestDto;
import cl.duoc.usuarios.dto.response.ExceptionDto;
import cl.duoc.usuarios.dto.response.LoginAllResponseDto;
import cl.duoc.usuarios.dto.response.LoginResponseDto;
import cl.duoc.usuarios.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/logins")
@RequiredArgsConstructor
@Tag(name = "Módulo de Cuentas", description = "Operaciones para gestionar cuentas de acceso al sistema")
public class LoginController {

    private final LoginService loginService;
    private final LoginModelAssembler assembler;


    @Operation(summary = "Obtener todas las cuentas",
               description = "Devuelve la lista de todas las cuentas con sus enlaces HATEOAS",
               tags = {"Módulo de Cuentas → 1. Consultas de Cuentas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = LoginCollectionOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<LoginAllResponseDto>>> getAll() {
        List<EntityModel<LoginAllResponseDto>> logins = loginService.getAll()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<LoginAllResponseDto>> collection = CollectionModel.of(logins,
                linkTo(methodOn(LoginController.class).getAll()).withSelfRel());

        return ResponseEntity.ok(collection);
    }


    @Operation(summary = "Obtener cuenta por ID",
               description = "Devuelve los datos completos de una cuenta junto con sus enlaces HATEOAS." +
                             "Un CLIENTE solo puede consultar su propia cuenta; un FUNCIONARIO puede consultar cualquiera.",
               tags = {"Módulo de Cuentas → 1. Consultas de Cuentas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = LoginHateoasOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes operar sobre la cuenta de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @Parameter(description = "ID de la cuenta", required = true, example = "1")
            @PathVariable Long id) {
        ResponseEntity<?> forbidden = verificarPropietarioLogin(id);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(loginService.getByIdInfo(id)));
    }


    @Operation(summary = "Obtener el rol de una cuenta por su ID",
               description = "Devuelve solamente el rol asociado a la cuenta indicada." +
                             "Un CLIENTE solo puede consultar su propia cuenta; un FUNCIONARIO puede consultar cualquiera.",
               tags = {"Módulo de Cuentas → 1. Consultas de Cuentas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes operar sobre la cuenta de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/{id}/rol")
    public ResponseEntity<?> getRolById(
            @Parameter(description = "ID de la cuenta", required = true, example = "1")
            @PathVariable Long id){
        ResponseEntity<?> forbidden = verificarPropietarioLogin(id);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(loginService.getById(id));
    }


    @Operation(summary = "Crear nueva cuenta",
               description = "Registra una nueva cuenta de acceso. La contraseña se almacena encriptada con BCrypt." +
                             "No requiere token: es el paso final del registro, después de crear la Person.",
               tags = {"Módulo de Cuentas → 2. Acciones de Cuentas"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = LoginHateoasOpenApi.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Datos no encontrados",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
    })
    @PostMapping
    public ResponseEntity<EntityModel<LoginAllResponseDto>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la cuenta a crear", required = true)
            @Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(loginService.create(dto)));
    }


    @Operation(summary = "Actualizar cuenta existente",
               description = "Actualiza username, password y/o rol de una cuenta existente." +
                             "Un CLIENTE solo puede actualizar su propia cuenta; un FUNCIONARIO puede actualizar cualquiera.",
               tags = {"Módulo de Cuentas → 2. Acciones de Cuentas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = LoginHateoasOpenApi.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes operar sobre la cuenta de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Datos no encontrados",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "ID de la cuenta a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la cuenta", required = true)
            @Valid @RequestBody LoginRequestDto dto
    ) {
        ResponseEntity<?> forbidden = verificarPropietarioLogin(id);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(loginService.update(id, dto)));
    }


    @Operation(summary = "Eliminar cuenta por ID",
               description = "Elimina una cuenta de acceso." +
                             "Un CLIENTE solo puede eliminar su propia cuenta; un FUNCIONARIO puede eliminar cualquiera.",
               tags = {"Módulo de Cuentas → 2. Acciones de Cuentas"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes operar sobre la cuenta de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID de la cuenta a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        ResponseEntity<?> forbidden = verificarPropietarioLogin(id);
        if (forbidden != null) {
            return forbidden;
        }

        if (!loginService.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


    private ResponseEntity<?> verificarPropietarioLogin(Long loginId) {
        if (SecurityUtil.isFuncionario()) {
            return null;
        }

        if (!loginService.esPropietario(loginId, SecurityUtil.currentUserId())) {
            ExceptionDto error = new ExceptionDto(
                    "Acceso denegado",
                    "No puedes operar sobre la cuenta de otro usuario.");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        return null;
    }


    class LoginHateoasOpenApi {
        @Schema(
                description = "Enlaces HATEOAS individuales para el login",
                example = "{\n" +
                    "  \"self\": { \"href\": \"http://localhost:8081/api/v1/logins/1\" },\n" +
                    "  \"logins\": { \"href\": \"http://localhost:8081/api/v1/logins\" },\n" +
                    "  \"update\": { \"href\": \"http://localhost:8081/api/v1/logins/1\" },\n" +
                    "  \"delete\": { \"href\": \"http://localhost:8081/api/v1/logins/1\" }\n" +
                    "}"
        )

        public Object _links;

        @Schema(example = "1", description = "ID único del login")
        public Long id;

        @Schema(example = "FunClaudia", description = "Nombre de usuario")
        public String username;

        public PersonNestedOpenApi person;

        public RolNestedOpenApi rol;
    }

    class LoginCollectionOpenApi {
        public EmbeddedData _embedded;

        @Schema(
                description = "Enlaces HATEOAS de la colección de logins",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8081/api/v1/logins\" }\n" +
                        "}"
        )
        public Object _links;

        public static class EmbeddedData {
            public List<LoginHateoasOpenApi> logins;
        }
    }


    class PersonNestedOpenApi {
        @Schema(example = "1")
        public Long id;

        @Schema(example = "2342424-3")
        public String rut;

        @Schema(example = "Catalina")
        public String name;

        @Schema(example = "Matus")
        public String lastName;

        @Schema(example = "cata.matus@supermercado.cl")
        public String email;

        @Schema(example = "912345373")
        public String phone;
    }

    class RolNestedOpenApi {
        @Schema(example = "1")
        public Long id;
        @Schema(example = "FUNCIONARIO")
        public String name;
        @Schema(example = "Controla el sistema")
        public String description;
    }

}
