package cl.supermercado.notificaciones.controller;

import cl.supermercado.notificaciones.assemblers.NotificacionModelAssembler;
import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;
import cl.supermercado.notificaciones.service.NotificacionService;
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
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Módulo de Notificaciones", description = "Mensajes enviados a los usuarios sobre el estado de sus compras")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final NotificacionModelAssembler assembler;


    @Operation(summary = "Listar notificaciones de un usuario",
               description = "Retorna todas las notificaciones del usuario indicado, con enlaces HATEOAS.",
               tags = {"Módulo de Notificaciones → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificacionResponseDto.class)))
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<NotificacionResponseDto>>> listarPorUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {
        List<EntityModel<NotificacionResponseDto>> notificaciones = notificacionService.listarPorUsuario(usuarioId)
                .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(notificaciones,
                linkTo(methodOn(NotificacionController.class).listarPorUsuario(usuarioId)).withSelfRel()));
    }


    @Operation(summary = "Marcar notificación como leída",
               description = "Actualiza el estado de lectura de una notificación específica.",
               tags = {"Módulo de Notificaciones → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    @PutMapping("/{id}/leida")
    public ResponseEntity<EntityModel<NotificacionResponseDto>> marcarComoLeida(
            @Parameter(description = "ID de la notificación", required = true, example = "5")
            @PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(notificacionService.marcarComoLeida(id)));
    }


    @Operation(summary = "Enviar notificación manualmente (solo FUNCIONARIO)",
               description = "El envío automático tras completar una compra ocurre vía Kafka; " +
                             "este endpoint es para envíos manuales adicionales.",
               tags = {"Módulo de Notificaciones → 2. Acciones"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notificación enviada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<NotificacionResponseDto>> enviar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Usuario destinatario y mensaje", required = true)
            @Valid @RequestBody NotificacionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(notificacionService.enviarNotificacion(request)));
    }

}
