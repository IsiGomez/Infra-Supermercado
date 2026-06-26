package cl.supermercado.seguimiento.controller;

import cl.supermercado.seguimiento.assemblers.SeguimientoModelAssembler;
import cl.supermercado.seguimiento.dto.request.SeguimientoRequestDto;
import cl.supermercado.seguimiento.dto.response.SeguimientoResponseDto;
import cl.supermercado.seguimiento.service.SeguimientoService;
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
@RequestMapping("/api/v1/seguimientos")
@RequiredArgsConstructor
@Tag(name = "Módulo de Seguimiento", description = "Estado de avance de las compras (PENDIENTE → ENTREGADO)")
public class SeguimientoController {

    private final SeguimientoService seguimientoService;
    private final SeguimientoModelAssembler assembler;

    @Operation(summary = "Listar todos los seguimientos (solo FUNCIONARIO)",
               description = "Retorna los seguimientos de todas las compras del sistema.",
               tags = {"Módulo de Seguimiento → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<SeguimientoResponseDto>>> listarSeguimientos() {
        List<EntityModel<SeguimientoResponseDto>> seguimientos = seguimientoService.listarSeguimientos()
                .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(seguimientos,
                linkTo(methodOn(SeguimientoController.class).listarSeguimientos()).withSelfRel()));
    }

    @Operation(summary = "Obtener historial de seguimiento de una compra",
               description = "Retorna todos los cambios de estado registrados para una compra específica.",
               tags = {"Módulo de Seguimiento → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SeguimientoResponseDto.class)))
    })
    @GetMapping("/compra/{compraId}")
    public ResponseEntity<CollectionModel<EntityModel<SeguimientoResponseDto>>> historialPorCompra(
            @Parameter(description = "ID de la compra", required = true, example = "15")
            @PathVariable Long compraId) {
        List<EntityModel<SeguimientoResponseDto>> seguimientos = seguimientoService.historialPorCompra(compraId)
                .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(seguimientos,
                linkTo(methodOn(SeguimientoController.class).historialPorCompra(compraId)).withSelfRel()));
    }

    @Operation(summary = "Listar seguimientos de un usuario",
               description = "Retorna el seguimiento de todas las compras del usuario indicado.",
               tags = {"Módulo de Seguimiento → 1. Consultas"})
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<SeguimientoResponseDto>>> listarPorUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {
        List<EntityModel<SeguimientoResponseDto>> seguimientos = seguimientoService.listarPorUsuario(usuarioId)
                .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(seguimientos,
                linkTo(methodOn(SeguimientoController.class).listarPorUsuario(usuarioId)).withSelfRel()));
    }

    @Operation(summary = "Registrar seguimiento manualmente (solo FUNCIONARIO)",
               description = "Registra un cambio de estado en el seguimiento de una compra. " +
                             "El registro inicial 'PENDIENTE' ocurre automáticamente vía Kafka " +
                             "al completar la compra; este endpoint es para actualizaciones manuales " +
                             "(PREPARACION, ENVIADO, ENTREGADO).",
               tags = {"Módulo de Seguimiento → 2. Acciones"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Seguimiento registrado correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<SeguimientoResponseDto>> registrarSeguimiento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo estado de seguimiento", required = true)
            @Valid @RequestBody SeguimientoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(seguimientoService.registrarSeguimiento(request)));
    }
}
