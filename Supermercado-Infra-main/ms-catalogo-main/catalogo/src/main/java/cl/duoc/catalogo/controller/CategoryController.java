package cl.duoc.catalogo.controller;

import cl.duoc.catalogo.assemblers.CategoryModelAssembler;
import cl.duoc.catalogo.dto.request.CategoryRequestDto;
import cl.duoc.catalogo.dto.response.CategoryResponseDto;
import cl.duoc.catalogo.dto.response.ExceptionDto;
import cl.duoc.catalogo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Módulo de Categorías", description = "Operaciones para gestionar las categorías del catálogo")
public class CategoryController {

    private final CategoryService service;
    private final CategoryModelAssembler assembler;


    @Operation(summary = "Obtener categoria por ID",
               description = "Devuelve los datos de una categoría específica con sus enlaces HATEOAS.",
               tags = {"Módulo de Categorías → 1. Consultas de Categorías"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryHateoasOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para este recurso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoryResponseDto>> getById(
            @Parameter(description = "ID de la categoría", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.getById(id)));
    }


    @Operation(summary = "Obtener todas las categorías",
            description = "Devuelve la lista completa de categorías con sus enlaces HATEOAS.",
            tags = {"Módulo de Categorías → 1. Consultas de Categorías"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryCollectionOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para este recurso", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CategoryResponseDto>>> getAll() {
        List<EntityModel<CategoryResponseDto>> categories = service.getAll()
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(categories,
                linkTo(methodOn(CategoryController.class).getAll()).withSelfRel()));
    }


    @Operation(summary = "Crear nueva categoria",
               description = "Crea una nueva categoría en el catálogo. Requiere rol FUNCIONARIO en el JWT.",
               tags = {"Módulo de Categorías → 2. Acciones de Categorías"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryHateoasOpenApi.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<CategoryResponseDto>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la categoría a crear", required = true)
            @Valid @RequestBody CategoryRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(service.create(dto)));
    }


    @Operation(summary = "Actualizar categoria existente",
               description = "Actualiza el nombre de una categoría existente. Requiere rol FUNCIONARIO en el JWT.",
               tags = {"Módulo de Categorías → 2. Acciones de Categorías"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryHateoasOpenApi.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                         content = @Content(mediaType = "application/hal+json",
                         schema = @Schema(implementation = ExceptionDto.class))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CategoryResponseDto>> update(
            @Parameter(description = "Id de la categoría a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la categoría", required = true)
            @Valid @RequestBody CategoryRequestDto dto
    ) {
        return ResponseEntity.ok(assembler.toModel(service.update(id, dto)));
    }


    @Operation(summary = "Eliminar categoria por ID",
               description = "Elimina una categoría del catálogo. Requiere rol FUNCIONARIO en el JWT.",
               tags = {"Módulo de Categorías → 2. Acciones de Categorías"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la categoría a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        if (!service.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


    class CategoryHateoasOpenApi {
        @Schema(
                description = "Enlaces HATEOAS individuales para la categoria",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8081/api/v1/categories/1\" },\n" +
                        "  \"categories\": { \"href\": \"http://localhost:8081/api/v1/categories\" },\n" +
                        "  \"update\": { \"href\": \"http://localhost:8081/api/v1/categories/1\" },\n" +
                        "  \"delete\": { \"href\": \"http://localhost:8081/api/v1/categories/1\" }\n" +
                        "}"
        )

        public Object _links;

        @Schema(example = "1", description = "ID único de la categoria")
        public Long id;

        @Schema(example = "Electrodomesticos", description = "Nombre de la categoria")
        public String name;

    }

    class CategoryCollectionOpenApi {
        public EmbeddedData _embedded;

        @Schema(
                description = "Enlaces HATEOAS de la colección de categories",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8081/api/v1/categories\" }\n" +
                        "}"
        )
        public Object _links;

        public static class EmbeddedData {
            public List<CategoryHateoasOpenApi> categories;
        }
    }

}
