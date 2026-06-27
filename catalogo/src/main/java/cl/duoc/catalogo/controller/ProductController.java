package cl.duoc.catalogo.controller;

import cl.duoc.catalogo.assemblers.ProductModelAssembler;
import cl.duoc.catalogo.dto.request.ProductRequestDto;
import cl.duoc.catalogo.dto.response.ProductResponseDto;
import cl.duoc.catalogo.service.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Módulo de Productos", description = "Operaciones para gestionar el catálogo de productos")
public class ProductController {

    private final ProductService service;
    private final ProductModelAssembler assembler;


    @Operation(summary = "Obtener producto por ID",
            description = "Retorna los datos de un producto específico con sus enlaces HATEOAS.",
            tags = {"Módulo de Productos → 1. Consultas de Productos"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductEntityModelDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para este recurso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductResponseDto>> getById(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.getById(id)));
    }


    @Operation(summary = "Obtener productos por sus IDs",
            description = "Retorna una lista de productos filtrando por una lista de IDs.",
            tags = {"Módulo de Productos → 1. Consultas de Productos"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Productos encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductCollectionModelDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para este recurso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Algún producto no fue encontrado", content = @Content)
    })
    @GetMapping("/by-ids")
    public ResponseEntity<CollectionModel<EntityModel<ProductResponseDto>>> getByIds(
            @Parameter(description = "Lista de IDs de productos", example = "1,2,3")
            @RequestParam List<Long> ids)
    {
        List<EntityModel<ProductResponseDto>> products = service.getByIds(ids)
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(products,
                linkTo(methodOn(ProductController.class).getByIds(ids)).withSelfRel()));
    }


    @Operation(summary = "Obtener todos los productos",
            description = "Retorna la lista completa de productos del catálogo con sus enlaces HATEOAS.",
            tags = {"Módulo de Productos → 1. Consultas de Productos"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductCollectionModelDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para este recurso", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProductResponseDto>>> getAll() {
        List<EntityModel<ProductResponseDto>> products = service.getAll()
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(products,
                linkTo(methodOn(ProductController.class).getAll()).withSelfRel()));
    }


    @Operation(summary = "Buscar productos por nombre",
            description = "Retorna productos cuyo nombre contenga el texto indicado (sin distinguir mayúsculas).",
            tags = {"Módulo de Productos → 1. Consultas de Productos"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados de búsqueda",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductCollectionModelDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para este recurso", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<ProductResponseDto>>> getByName(
            @Parameter(description = "Texto a buscar en el nombre", example = "leche")
            @RequestParam String name) {
        List<EntityModel<ProductResponseDto>> products = service.getByNameContainingIgnoreCase(name)
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(products,
                linkTo(methodOn(ProductController.class).getByName(name)).withSelfRel()));
    }


    @Operation(summary = "Obtener productos por categoría",
            description = "Retorna todos los productos que pertenecen a la categoría indicada.",
            tags = {"Módulo de Productos → 1. Consultas de Productos"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Productos encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductCollectionModelDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para este recurso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CollectionModel<EntityModel<ProductResponseDto>>> getByCategoryId(
            @Parameter(description = "ID de la categoría", required = true, example = "1")
            @PathVariable Long categoryId) {
        List<EntityModel<ProductResponseDto>> products = service.getByCategoryId(categoryId)
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(products,
                linkTo(methodOn(ProductController.class).getByCategoryId(categoryId)).withSelfRel()));
    }


    @Operation(summary = "Obtener productos por categoría y rango de precio",
            description = "Filtra productos por categoría y rango de precio (inclusivo en ambos extremos).",
            tags = {"Módulo de Productos → 1. Consultas de Productos"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Productos encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductCollectionModelDto.class))),
            @ApiResponse(responseCode = "400", description = "Rango de precios inválido", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos para este recurso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @GetMapping("/category/{categoryId}/price")
    public ResponseEntity<CollectionModel<EntityModel<ProductResponseDto>>> getByCategoryIdAndPriceBetween(
            @Parameter(description = "ID de la categoría", required = true, example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "Precio mínimo", required = true, example = "100")
            @RequestParam Integer minPrice,
            @Parameter(description = "Precio máximo", required = true, example = "5000")
            @RequestParam Integer maxPrice
    ) {
        List<EntityModel<ProductResponseDto>> products =
                service.getByCategoryIdAndPriceBetween(categoryId, minPrice, maxPrice)
                        .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(products,
                linkTo(methodOn(ProductController.class)
                        .getByCategoryIdAndPriceBetween(categoryId, minPrice, maxPrice)).withSelfRel()));

    }


    @Operation(summary = "Crear producto",
            description = "Crea un nuevo producto en el catálogo. Requiere rol FUNCIONARIO en el JWT.",
            tags = {"Módulo de Productos → 2. Acciones de Productos"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductEntityModelDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<ProductResponseDto>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a crear", required = true)
            @Valid @RequestBody ProductRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(service.create(dto)));
    }


    @Operation(summary = "Actualizar producto",
            description = "Actualiza los datos de un producto existente. Requiere rol FUNCIONARIO en el JWT.",
            tags = {"Módulo de Productos → 2. Acciones de Productos"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductEntityModelDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProductResponseDto>> update(
            @Parameter(description = "ID del producto a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del producto", required = true)
            @Valid @RequestBody ProductRequestDto dto
    ) {
        return ResponseEntity.ok(assembler.toModel(service.update(id, dto)));
    }


    @Operation(summary = "Eliminar producto",
            description = "Elimina un producto del catálogo. Requiere rol FUNCIONARIO en el JWT.",
            tags = {"Módulo de Productos → 2. Acciones de Productos"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Id del producto a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        if (!service.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


    class ProductEntityModelDto {
        @Schema(
                description = "Enlaces HATEOAS individuales para el producto",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8081/api/v1/products/1\" },\n" +
                        "  \"products\": { \"href\": \"http://localhost:8081/api/v1/products\" },\n" +
                        "  \"update\": { \"href\": \"http://localhost:8081/api/v1/products/1\" },\n" +
                        "  \"delete\": { \"href\": \"http://localhost:8081/api/v1/products/1\" }\n" +
                        "}"
        )

        public Object _links;

        @Schema(example = "1", description = "ID único del producto")
        public Long id;

        @Schema(example = "Arroz Grado 1", description = "Nombre del producto")
        public String name;

        @Schema(example = "1kg en bolsa", description = "Descripción del producto")
        public String description;

        @Schema(example = "1590", description = "Precio del producto")
        public Integer price;

        @Schema(example = "1", description = "Categoria a la que pertenece")
        public Integer category_id;

    }

    class ProductCollectionModelDto {
        public EmbeddedData _embedded;

        @Schema(
                description = "Enlaces HATEOAS de la colección de productos",
                example = "{\"self\":{\"href\":\"http://localhost:8081/api/v1/products\"}}"
        )
        public Object _links;

        public static class EmbeddedData {
            public List<ProductEntityModelDto> products;
        }
    }

}
