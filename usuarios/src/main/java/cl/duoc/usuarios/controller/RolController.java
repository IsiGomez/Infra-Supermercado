package cl.duoc.usuarios.controller;

import cl.duoc.usuarios.assemblers.RolModelAssembler;
import cl.duoc.usuarios.dto.response.ExceptionDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import cl.duoc.usuarios.dto.response.RolResponseDto;
import cl.duoc.usuarios.service.RolService;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Módulo de Roles", description = "Operaciones de consulta de roles disponibles en el sistema")
public class RolController {

    private final RolService rolService;
    private final RolModelAssembler assembler;

    @Operation(summary = "Obtener todos los roles",
            description = "Devuelve la lista completa de roles registradas con sus enlaces HATEOAS.",
            tags = {"Módulo de Roles → 1. Consultas de Roles"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = RolCollectionOpenApi.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<RolResponseDto>>> getAll() {
        List<EntityModel<RolResponseDto>> roles = rolService.getAll()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<RolResponseDto>> collection = CollectionModel.of(roles,
                linkTo(methodOn(RolController.class).getAll()).withSelfRel());

        return ResponseEntity.ok(collection);
    }


    @Operation(summary = "Obtener un rol por su ID",
            description = "Devuelve los datos de un rol junto con sus enlaces HATEOAS.",
            tags = {"Módulo de Roles → 1. Consultas de Roles"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol encontrado",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = RolHateoasOpenApi.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RolResponseDto>> getById(
            @Parameter(description = "ID del rol", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(rolService.getById(id)));
    }



    class RolHateoasOpenApi {
        @Schema(
                description = "Enlaces HATEOAS individuales para los roles",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8081/api/v1/roles/1\" },\n" +
                        "  \"roles\": { \"href\": \"http://localhost:8081/api/v1/roles\" },\n" +
                        "}"
        )

        public Object _links;

        @Schema(example = "1", description = "ID del rol")
        public Long id;

        @Schema(example = "FUNCIONARIO", description = "Nombre del rol")
        public String name;

        @Schema(example = "Controla el sistema", description = "Descripcion del rol")
        public String description;

    }

    class RolCollectionOpenApi {
        public EmbeddedData _embedded;

        @Schema(
                description = "Enlaces HATEOAS de rol",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8081/api/v1/roles\" }\n" +
                        "}"
        )
        public Object _links;

        public static class EmbeddedData {
            public List<RolHateoasOpenApi> roles;
        }
    }

}
