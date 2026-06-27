package cl.duoc.inventario.controller;
import cl.duoc.inventario.assemblers.InventoryModelAssembler;
import cl.duoc.inventario.dto.request.InventoryRequestDto;
import cl.duoc.inventario.dto.response.ExceptionDto;
import cl.duoc.inventario.dto.response.InventoryResponseDto;
import cl.duoc.inventario.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Módulo de Inventario", description = "Operaciones para consultar y actualizar el stock de productos")
public class InventoryController {

    private final InventoryService service;
    private final InventoryModelAssembler assembler;

    @Operation(summary = "Obtener el stock de un producto",
               description = "Retorna la cantidad disponible en inventario para el producto indicado, con enlaces HATEOAS.",
               tags = {"Módulo de Inventario → 1. Consultas de Inventario"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock encontrado",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = InventoryHateoasOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no registrado en inventario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<EntityModel<InventoryResponseDto>> getStock(
            @Parameter(description = "ID del producto a consultar", required = true, example = "1")
            @PathVariable Long productId){
        return ResponseEntity.ok(assembler.toModel(service.consultarStock(productId)));
    }


    @Operation(summary = "Actualizar stock de producto",
               description = "Actualiza la cantidad disponible de un producto en el inventario. " +
                    "Si el producto no tiene registro previo, se crea uno nuevo. " +
                    "Requiere rol FUNCIONARIO en el JWT.",
               tags = {"Módulo de Inventario → 2. Acciones de Inventario"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = InventoryHateoasOpenApi.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en catálogo",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PatchMapping("/update")
    public ResponseEntity<EntityModel<InventoryResponseDto>> updateStock(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de actualización de stock", required = true)
            @Valid @RequestBody InventoryRequestDto request){
        return ResponseEntity.ok(assembler.toModel(service.actualizarStock(request)));
    }


    class InventoryHateoasOpenApi {

        @Schema(
                description = "Enlaces HATEOAS del inventario",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8083/api/v1/inventory/product/1\" },\n" +
                        "  \"update-stock\": { \"href\": \"http://localhost:8083/api/v1/inventory/update\" }\n" +
                        "}"
        )
        public Object _links;

        @Schema(example = "1", description = "Id del registro de inventario")
        public Long id;

        @Schema(example = "1", description = "Id del producto")
        public Long productId;

        @Schema(example = "Hervidor", description = "Nombre del producto")
        public String productName;

        @Schema(example = "50000", description = "Precio unitario del producto")
        public Integer priceUnit;

        @Schema(example = "46", description = "Cantidad disponible en inventario")
        public Integer quantity;

    }

}
