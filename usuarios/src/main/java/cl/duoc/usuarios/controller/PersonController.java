package cl.duoc.usuarios.controller;

import cl.duoc.usuarios.assemblers.PersonModelAssembler;
import cl.duoc.usuarios.config.SecurityUtil;
import cl.duoc.usuarios.dto.request.PersonRequestDto;
import cl.duoc.usuarios.dto.response.ExceptionDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import cl.duoc.usuarios.service.PersonService;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
@Tag(name = "Módulo de Personas", description = "Operaciones para gestionar personas del sistema")
public class PersonController {

    private final PersonService personService;
    private final PersonModelAssembler assembler;


    @Operation(summary = "Obtener todas las personas",
            description = "Devuelve la lista completa de personas registradas con sus enlaces HATEOAS.",
            tags = {"Módulo de Personas → 1. Consultas de Personas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = PersonCollectionOpenApi.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PersonResponseDto>>> getAll() {
        List<EntityModel<PersonResponseDto>> persons = personService.getAll()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<PersonResponseDto>> collection = CollectionModel.of(persons,
                linkTo(methodOn(PersonController.class).getAll()).withSelfRel());

        return ResponseEntity.ok(collection);
    }


    @Operation(summary = "Obtener persona por ID",
               description = "Devuelve los datos de una persona específica junto con sus enlaces HATEOAS." +
                             "Un CLIENTE solo puede consultar sus propios datos; un FUNCIONARIO puede consultar cualquiera.",
               tags = {"Módulo de Personas → 1. Consultas de Personas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Persona encontrada",
                         content = @Content(mediaType = "application/hal+json",
                         schema = @Schema(implementation = PersonHateoasOpenApi.class))),
            @ApiResponse(responseCode = "403", description = "No puedes operar sobre los datos de otra persona",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @Parameter(description = "ID de la persona", required = true, example = "1")
            @PathVariable Long id) {
        ResponseEntity<?> forbidden = verificarPropietarioPersona(id);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(personService.getById(id)));
    }


    @Operation(summary = "Crear nueva informacion de persona",
               description = "Registra una nueva persona en el sistema.",
               tags = {"Módulo de Personas → 2. Acciones de Personas"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Persona creada exitosamente",
                         content = @Content(mediaType = "application/hal+json",
                         schema = @Schema(implementation = PersonHateoasOpenApi.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<PersonResponseDto>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la persona a crear", required = true)
            @Valid @RequestBody PersonRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(personService.create(dto)));
    }


    @Operation(summary = "Actualizar persona existente",
               description = "Actualiza los datos de una persona existente." +
                             "Un CLIENTE solo puede actualizar sus propios datos; un FUNCIONARIO puede actualizar cualquiera.",
               tags = {"Módulo de Personas → 2. Acciones de Personas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Persona actualizada correctamente",
                         content = @Content(mediaType = "application/hal+json",
                         schema = @Schema(implementation = PersonHateoasOpenApi.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "No puedes operar sobre los datos de otra persona",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "ID de la persona a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la persona", required = true)
            @Valid @RequestBody PersonRequestDto dto
    ) {
        ResponseEntity<?> forbidden = verificarPropietarioPersona(id);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(personService.update(id, dto)));
    }


    @Operation(summary = "Eliminar información de persona por ID",
               description = "Elimina una persona del sistema de forma permanente." +
                             "Un CLIENTE solo puede eliminar sus propios datos; un FUNCIONARIO puede eliminar cualquiera." +
                             "La persona no debe estar asociada a ninguna cuenta",
               tags = {"Módulo de Personas → 2. Acciones de Personas"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Persona eliminada correctamente"),
            @ApiResponse(responseCode = "400", description = "Persona asociada a un login, no se puede borrar", content = @Content),
            @ApiResponse(responseCode = "403", description = "No puedes operar sobre los datos de otra persona", content = @Content),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada", content = @Content),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID de la persona a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        ResponseEntity<?> forbidden = verificarPropietarioPersona(id);
        if (forbidden != null) {
            return forbidden;
        }

        if (!personService.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }



    private ResponseEntity<?> verificarPropietarioPersona(Long personId) {
        if (SecurityUtil.isFuncionario()) {
            return null;
        }

        if (!personService.esPropietario(personId, SecurityUtil.currentUserId())) {
            ExceptionDto error = new ExceptionDto(
                    "Acceso denegado",
                    "No puedes operar sobre los datos de otra persona.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        return null;
    }


    @JsonPropertyOrder({"_links", "id", "rut", "name", "lastName", "email", "phone"})
    class PersonHateoasOpenApi {
        @Schema(
                description = "Enlaces HATEOAS individuales para la persona",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8081/api/v1/persons/1\" },\n" +
                        "  \"persons\": { \"href\": \"http://localhost:8081/api/v1/persons\" },\n" +
                        "  \"update\": { \"href\": \"http://localhost:8081/api/v1/persons/1\" },\n" +
                        "  \"delete\": { \"href\": \"http://localhost:8081/api/v1/persons/1\" }\n" +
                        "}"
        )

        public Object _links;

        @Schema(example = "1", description = "ID único de la persona")
        public Long id;

        @Schema(example = "12342885-3", description = "RUT de la persona")
        public String rut;

        @Schema(example = "Claudia", description = "Nombre de la persona")
        public String name;

        @Schema(example = "Gonzales", description = "Apellido de la persona")
        public String lastName;

        @Schema(example = "clau.gon@funcionarios.com", description = "Correo electrónico")
        public String email;

        @Schema(example = "+56923542352", description = "Teléfono de contacto")
        public String phone;
    }

    class PersonCollectionOpenApi {
         public EmbeddedData _embedded;

        @Schema(
                description = "Enlaces HATEOAS de la colección de personas",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8081/api/v1/persons\" }\n" +
                        "}"
        )
        public Object _links;

        public static class EmbeddedData {
            public List<PersonHateoasOpenApi> persons;
        }
    }
}
