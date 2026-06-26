package cl.supermercado.compra.controller;
import cl.supermercado.compra.assemblers.CompraModelAssembler;
import cl.supermercado.compra.config.SecurityUtil;
import cl.supermercado.compra.dto.request.CompraRequestDto;
import cl.supermercado.compra.dto.response.CompraResponseDto;
import cl.supermercado.compra.dto.response.ExceptionDto;
import cl.supermercado.compra.model.Compra;
import cl.supermercado.compra.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/compras")
@RequiredArgsConstructor
@Tag(name = "Módulo de Compras", description = "Operaciones de compras")
public class CompraController {

    private final CompraService compraService;
    private final CompraModelAssembler assembler;


    @Operation(summary = "Crear y confirmar una compra",
            description = "Verifica que el pago esté exitoso, toma el carrito del usuario, " +
                    "registra la compra y publica el evento que dispara la asignación " +
                    "automática de puntos, el seguimiento inicial y la notificación.",
            tags = {"Módulo de Compras → 1. Acciones"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compra registrada correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = CompraHateoasOpenApi.class))),
            @ApiResponse(responseCode = "400", description = "No hay pago exitoso o el carrito esta vacío", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "No puedes comprar a nombre de otro usuario", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> crearCompra(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Id del usuario que confirma la compra", required = true)
            @Valid @RequestBody CompraRequestDto request) {

        ResponseEntity<?> forbidden = verificarPropietario(request.getUsuarioId());
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(compraService.crearCompra(request)));
    }


    @Operation(summary = "Listar todas las compras",
            description = "Devuelve las compras de todos los usuarios del sistema.",
            tags = {"Módulo de Compras → 2. Administración"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = CompraCollectionOpenApi.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CompraResponseDto>>> listarCompras() {
        List<EntityModel<CompraResponseDto>> compras = compraService.listarCompras()
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(compras,
                linkTo(methodOn(CompraController.class).listarCompras()).withSelfRel()));
    }


    @Operation(summary = "Listar compras de un usuario",
            description = "Devuelve el historial de compras del usuario indicado.",
            tags = {"Módulo de Compras → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = CompraCollectionOpenApi.class))),
            @ApiResponse(responseCode = "403", description = "No puedes ver las compras de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarComprasPorUsuario(
            @Parameter(description = "Id del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {
        ResponseEntity<?> forbidden = verificarPropietario(usuarioId);
        if (forbidden != null) {
            return forbidden;
        }

        List<EntityModel<CompraResponseDto>> compras = compraService.listarComprasPorUsuario(usuarioId)
                .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(compras,
                linkTo(methodOn(CompraController.class).listarComprasPorUsuario(usuarioId)).withSelfRel()));
    }


    private ResponseEntity<?> verificarPropietario(Long usuarioIdSolicitado) {
        Long userIdDelToken = SecurityUtil.currentUserId();

        if (userIdDelToken == null || !userIdDelToken.equals(usuarioIdSolicitado)) {
            ExceptionDto error = new ExceptionDto(
                    "Acceso denegado",
                    "No puedes operar sobre las compras de otro usuario.");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        return null;
    }


    class CompraHateoasOpenApi {
        @Schema(
                description = "Enlaces HATEOAS individuales para la compra",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8085/api/compras/usuario/2\" },\n" +
                        "  \"historial-usuario\": { \"href\": \"http://localhost:8085/api/compras/usuario/2\" },\n" +
                        "  \"compras\": { \"href\": \"http://localhost:8085/api/compras\" },\n" +
                        "  \"crear-compra\": { \"href\": \"http://localhost:8085/api/compras\" }\n" +
                        "}"
        )
        public Object _links;

        @Schema(example = "1", description = "Id de la compra")
        public Long id;

        @Schema(example = "2", description = "Id del usuario")
        public Long usuarioId;

        @Schema(example = "15990.0", description = "Total de la compra")
        public Double total;

        @Schema(description = "Fecha de la compra")
        public String fechaCompra;

        @Schema(example = "true", description = "Si es que la compra se finalizo")
        public Boolean finalizada;

        @Schema(example = "true", description = "Si es que el pago esta confirmado")
        public Boolean pagoConfirmado;
    }


    class CompraCollectionOpenApi {
        public EmbeddedData _embedded;

        @Schema(
                description = "Enlaces HATEOAS de la colección de compras",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8085/api/compras\" }\n" +
                        "}"
        )
        public Object _links;

        public static class EmbeddedData {
            public List<CompraHateoasOpenApi> compras;
        }

    }

}
