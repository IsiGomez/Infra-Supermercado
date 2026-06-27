package cl.supermercado.puntos.controller;

import cl.supermercado.puntos.assemblers.PuntosModelAssembler;
import cl.supermercado.puntos.config.SecurityUtil;
import cl.supermercado.puntos.dto.request.PuntosRequestDto;
import cl.supermercado.puntos.dto.response.PuntosResponseDto;
import cl.supermercado.puntos.service.PuntosService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/puntos")
@RequiredArgsConstructor
@Tag(name = "Módulo de Puntos", description = "Consulta y canje de puntos acumulados por compras")
public class PuntosController {

    private final PuntosService puntosService;
    private final PuntosModelAssembler assembler;


    @Operation(summary = "Consultar puntos de un usuario",
               description = "Devuelve los puntos acumulados del usuario indicado, con enlaces HATEOAS.",
               tags = {"Módulo de Puntos → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Puntos encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PuntosResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "No puedes consultar los puntos de otro usuario", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario sin puntos registrados", content = @Content)
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> consultarPuntos(
            @Parameter(description = "Id del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {

        ResponseEntity<?> forbidden = verificarPropietario(usuarioId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(puntosService.consultarPuntos(usuarioId)));
    }


    @Operation(summary = "Simular canje de puntos (paso 1 de 2)",
               description = "Informa cuántos puntos se pueden canjear y cuánto equivalen en pesos, " +
                             "sin modificar nada.",
               tags = {"Módulo de Puntos → 2. Canje"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulación calculada correctamente"),
            @ApiResponse(responseCode = "403", description = "No puedes consultar los puntos de otro usuario", content = @Content)
    })
    @GetMapping("/{usuarioId}/canje/simular")
    public ResponseEntity<?> simularCanje(
            @Parameter(description = "Id del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {

        ResponseEntity<?> forbidden = verificarPropietario(usuarioId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(puntosService.simularCanje(usuarioId));
    }


    @Operation(summary = "Confirmar canje de puntos (paso 2 de 2)",
               description = "Descuenta los puntos canjeables de la cuenta del usuario y retorna " +
                             "el monto de descuento a aplicar. Regla: 10 puntos = $1.",
               tags = {"Módulo de Puntos → 2. Canje"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canje realizado correctamente"),
            @ApiResponse(responseCode = "400", description = "No hay suficientes puntos para canjear", content = @Content),
            @ApiResponse(responseCode = "403", description = "No puedes canjear los puntos de otro usuario", content = @Content)
    })
    @PostMapping("/{usuarioId}/canje/confirmar")
    public ResponseEntity<?> confirmarCanje(
            @Parameter(description = "Id del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {

        ResponseEntity<?> forbidden = verificarPropietario(usuarioId);
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(puntosService.confirmarCanje(usuarioId));
    }


    @Operation(summary = "Asignar puntos manualmente (solo FUNCIONARIO)",
               description = "Herramienta administrativa. El flujo normal es automático vía Kafka " +
                             "cuando el cliente completa una compra.",
               tags = {"Módulo de Puntos → 3. Administración"})
    @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO",
                 content = @Content)
    @PostMapping
    public ResponseEntity<EntityModel<PuntosResponseDto>> asignarPuntos(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Usuario, compra y total para calcular los puntos", required = true)
            @Valid @RequestBody PuntosRequestDto request) {
        return ResponseEntity.ok(assembler.toModel(puntosService.asignarPuntos(request)));
    }


    private ResponseEntity<?> verificarPropietario(Long usuarioIdSolicitado) {
        Long userIdDelToken = SecurityUtil.currentUserId();

        if (userIdDelToken == null || !userIdDelToken.equals(usuarioIdSolicitado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: no puedes operar sobre los puntos de otro usuario.");
        }

        return null;
    }

}
