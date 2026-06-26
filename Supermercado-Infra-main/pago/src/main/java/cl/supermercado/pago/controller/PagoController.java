package cl.supermercado.pago.controller;

import cl.supermercado.pago.assemblers.PagoModelAssembler;
import cl.supermercado.pago.config.SecurityUtil;
import cl.supermercado.pago.dto.request.PagoRequestDto;
import cl.supermercado.pago.dto.response.PagoResponseDto;
import cl.supermercado.pago.service.PagoService;
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
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@Tag(name = "Módulo de Pagos", description = "Procesamiento y consulta de pagos — exclusivo para CLIENTES")
public class PagoController {

    private final PagoService pagoService;
    private final PagoModelAssembler assembler;


    @Operation(summary = "Verificar si el usuario tiene un pago exitoso reciente",
               description = "Llamado internamente por compra antes de confirmar una compra.",
               tags = {"Módulo de Pagos → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado obtenido correctamente"),
            @ApiResponse(responseCode = "403", description = "No puedes consultar los pagos de otro usuario", content = @Content)
    })
    @GetMapping("/usuario/{usuarioId}/ultimo-exitoso")
    public ResponseEntity<?> tieneUltimoPagoExitoso(
            @Parameter(description = "ID del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {

        ResponseEntity<?> forbidden = verificarPropietario(usuarioId);
        if (forbidden != null) return forbidden;

        return ResponseEntity.ok(pagoService.tieneUltimoPagoExitoso(usuarioId));
    }


    @Operation(summary = "Obtener el detalle del último pago exitoso",
               description = "Retorna el pago completo, con enlaces HATEOAS.",
               tags = {"Módulo de Pagos → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagoResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes consultar los pagos de otro usuario", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un pago exitoso para el usuario", content = @Content)
    })
    @GetMapping("/usuario/{usuarioId}/ultimo-exitoso-detalle")
    public ResponseEntity<?> obtenerUltimoPagoExitoso(
            @Parameter(description = "ID del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {

        ResponseEntity<?> forbidden = verificarPropietario(usuarioId);
        if (forbidden != null) return forbidden;

        return ResponseEntity.ok(assembler.toModel(pagoService.obtenerUltimoPagoExitoso(usuarioId)));
    }


    @Operation(summary = "Procesar un pago",
               description = "Obtiene el total real desde el carrito del usuario y registra el pago. " +
                             "Métodos soportados: TARJETA, CREDITO, EFECTIVO.",
               tags = {"Módulo de Pagos → 2. Acciones"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago procesado correctamente"),
            @ApiResponse(responseCode = "400", description = "Carrito vacío o método de pago inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "No puedes procesar un pago a nombre de otro usuario", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> procesarPago(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Usuario y método de pago", required = true)
            @Valid @RequestBody PagoRequestDto request) {

        ResponseEntity<?> forbidden = verificarPropietario(request.getUsuarioId());
        if (forbidden != null) return forbidden;

        return ResponseEntity.ok(assembler.toModel(pagoService.procesarPago(request)));
    }


    @Operation(summary = "Listar todos los pagos (solo FUNCIONARIO)",
               description = "Retorna los pagos de todos los usuarios del sistema.",
               tags = {"Módulo de Pagos → 3. Administración"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content)
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PagoResponseDto>>> listarPagos() {
        List<EntityModel<PagoResponseDto>> pagos = pagoService.listarPagos()
                .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(pagos,
                linkTo(methodOn(PagoController.class).listarPagos()).withSelfRel()));
    }


    private ResponseEntity<?> verificarPropietario(Long usuarioIdSolicitado) {
        Long userIdDelToken = SecurityUtil.currentUserId();

        if (userIdDelToken == null || !userIdDelToken.equals(usuarioIdSolicitado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: no puedes operar sobre los pagos de otro usuario.");
        }
        return null;
    }

}
