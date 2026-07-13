package cl.supermercado.seguimiento.controller;

import cl.supermercado.seguimiento.assemblers.SeguimientoModelAssembler;
import cl.supermercado.seguimiento.config.SecurityUtil;
import cl.supermercado.seguimiento.dto.request.SeguimientoRequestDto;
import cl.supermercado.seguimiento.dto.response.ExceptionDto;
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
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = SeguimientoCollectionOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping
    public ResponseEntity<?> listarSeguimientos() {
        if (!SecurityUtil.isFuncionario()) {
            ExceptionDto error = new ExceptionDto(
                    "Acceso denegado", "Se requiere rol FUNCIONARIO para ver todos los seguimientos.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

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
                         schema = @Schema(implementation = SeguimientoCollectionOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes ver el seguimiento de la compra de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/compra/{compraId}")
    public ResponseEntity<?> historialPorCompra(
            @Parameter(description = "ID de la compra", required = true, example = "15")
            @PathVariable Long compraId) {
        List<SeguimientoResponseDto> historial = seguimientoService.historialPorCompra(compraId);

        if (!SecurityUtil.isFuncionario() && !historial.isEmpty()) {
            ResponseEntity<?> forbidden = verificarPropietario(historial.get(0).getUsuarioId());
            if (forbidden != null) return forbidden;
        }

        List<EntityModel<SeguimientoResponseDto>> seguimientos = historial
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(seguimientos,
                linkTo(methodOn(SeguimientoController.class).historialPorCompra(compraId)).withSelfRel()));

    }


    @Operation(summary = "Listar seguimientos de un usuario",
               description = "Retorna el seguimiento de todas las compras del usuario indicado.",
               tags = {"Módulo de Seguimiento → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = SeguimientoCollectionOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes ver el seguimiento de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {
        if (!SecurityUtil.isFuncionario()) {
            ResponseEntity<?> forbidden = verificarPropietario(usuarioId);
            if (forbidden != null) return forbidden;
        }

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
            @ApiResponse(responseCode = "201", description = "Seguimiento registrado correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = SeguimientoHateoasOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<SeguimientoResponseDto>> registrarSeguimiento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo estado de seguimiento", required = true)
            @Valid @RequestBody SeguimientoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(seguimientoService.registrarSeguimiento(request)));
    }


    @Operation(summary = "Obtener un seguimiento por id",
            description = "Devuelve el detalle de un registro de seguimiento específico.",
            tags = {"Módulo de Seguimiento → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seguimiento encontrado",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = SeguimientoHateoasOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Seguimiento no encontrado",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @Parameter(description = "ID del seguimiento", required = true, example = "3")
            @PathVariable Long id) {
        SeguimientoResponseDto seguimiento = seguimientoService.obtenerPorId(id);

        if (!SecurityUtil.isFuncionario()) {
            ResponseEntity<?> forbidden = verificarPropietario(seguimiento.getUsuarioId());
            if (forbidden != null) return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(seguimiento));
    }



    private ResponseEntity<?> verificarPropietario(Long usuarioIdSolicitado) {
        Long userIdDelToken = SecurityUtil.currentUserId();

        if (userIdDelToken == null || !userIdDelToken.equals(usuarioIdSolicitado)) {
            ExceptionDto error = new ExceptionDto(
                    "Acceso denegado",
                    "No puedes operar sobre el seguimiento de otro usuario.");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        return null;
    }


    class SeguimientoHateoasOpenApi {
        @Schema(
                description = "Enlaces HATEOAS individuales para el seguimiento",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8088/api/v1/seguimientos/3\" },\n" +
                        "  \"historial-compra\": { \"href\": \"http://localhost:8088/api/v1/seguimientos/compra/15\" },\n" +
                        "  \"seguimientos-usuario\": { \"href\": \"http://localhost:8088/api/v1/seguimientos/usuario/2\" },\n" +
                        "  \"registrar-seguimiento\": { \"href\": \"http://localhost:8088/api/v1/seguimientos\" }\n" +
                        "}"
        )
        public Object _links;

        @Schema(example = "3", description = "ID del seguimiento")
        public Long id;

        @Schema(example = "15", description = "ID de la compra perteneciente al seguimiento")
        public Long compraId;

        @Schema(example = "2", description = "ID del usuario perteneciento")
        public Long usuarioId;

        @Schema(example = "PREPARACION", description = "Estado de la compra")
        public String estado;

        @Schema(example = "2026-07-11T14:30:00", description = "Fecha en la que se actualizo")
        public String fechaActualizacion;
    }


    class SeguimientoCollectionOpenApi {
        public EmbeddedData _embedded;

        @Schema(
                description = "Enlaces HATEOAS de la colección de seguimientos",
                example = "{\"self\":{\"href\":\"http://localhost:8088/api/v1/seguimientos\"}}"
        )
        public Object _links;

        public static class EmbeddedData {
            public List<SeguimientoHateoasOpenApi> seguimientos;
        }
    }
}
