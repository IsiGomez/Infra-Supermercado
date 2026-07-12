package cl.supermercado.notificaciones.controller;

import cl.supermercado.notificaciones.assemblers.NotificacionModelAssembler;
import cl.supermercado.notificaciones.config.SecurityUtil;
import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.dto.response.ExceptionDto;
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
                         schema = @Schema(implementation = NotificacionCollectionOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "2")
            @PathVariable Long usuarioId) {
        ResponseEntity<?> forbidden = verificarPropietario(usuarioId);
        if (forbidden != null) {
            return forbidden;
        }

        List<EntityModel<NotificacionResponseDto>> notificaciones = notificacionService.listarPorUsuario(usuarioId)
                .stream().map(assembler::toModel).toList();

        return ResponseEntity.ok(CollectionModel.of(notificaciones,
                linkTo(methodOn(NotificacionController.class).listarPorUsuario(usuarioId)).withSelfRel()));
    }


    @Operation(summary = "Marcar notificación como leída",
               description = "Actualiza el estado de lectura de una notificación específica.",
               tags = {"Módulo de Notificaciones → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación actualizada correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = NotificacionHateoasOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "Se requiere rol CLIENTE y no puedes marcar como leida" +
                         " notificaciones que son de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PutMapping("/{id}/leida")
    public ResponseEntity<?> marcarComoLeida(
            @Parameter(description = "ID de la notificación", required = true, example = "5")
            @PathVariable Long id) {
        NotificacionResponseDto notificacion = notificacionService.obtenerPorId(id);

        ResponseEntity<?> forbidden = verificarPropietario(notificacion.getUsuarioId());
        if (forbidden != null) {
            return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(notificacionService.marcarComoLeida(id)));
    }


    @Operation(summary = "Enviar notificación manualmente (solo FUNCIONARIO)",
               description = "El envío automático tras completar una compra ocurre vía Kafka; " +
                             "este endpoint es para envíos manuales adicionales.",
               tags = {"Módulo de Notificaciones → 2. Acciones"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notificación enviada correctamente",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = NotificacionHateoasOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere rol FUNCIONARIO",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<NotificacionResponseDto>> enviar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Usuario destinatario y mensaje", required = true)
            @Valid @RequestBody NotificacionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assembler.toModel(notificacionService.enviarNotificacion(request)));
    }


    @Operation(summary = "Obtener una notificación por id",
            description = "Devuelve el detalle de una notificación específica. Solo el dueño o un FUNCIONARIO pueden verla.",
            tags = {"Módulo de Notificaciones → 1. Consultas"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación encontrada",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = NotificacionHateoasOpenApi.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "403", description = "No puedes ver la notificación de otro usuario",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @Parameter(description = "ID de la notificación", required = true, example = "8")
            @PathVariable Long id) {

        NotificacionResponseDto notificacion = notificacionService.obtenerPorId(id);

        if (!SecurityUtil.isFuncionario()) {
            ResponseEntity<?> forbidden = verificarPropietario(notificacion.getUsuarioId());
            if (forbidden != null) return forbidden;
        }

        return ResponseEntity.ok(assembler.toModel(notificacion));
    }



    private ResponseEntity<?> verificarPropietario(Long usuarioId) {
        Long userIdDelToken = SecurityUtil.currentUserId();

        if (userIdDelToken == null || !userIdDelToken.equals(usuarioId)) {
            ExceptionDto error = new ExceptionDto(
                    "Acceso denegado",
                    "No puedes operar sobre los puntos de otro usuario."
            );

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(error);
        }

        return null;
    }


    class NotificacionHateoasOpenApi {
        @Schema(
                description = "Enlaces HATEOAS individuales para la notificación",
                example = "{\n" +
                        "  \"self\": { \"href\": \"http://localhost:8089/api/v1/notificaciones/8\" },\n" +
                        "  \"notificaciones-usuario\": { \"href\": \"http://localhost:8089/api/v1/notificaciones/usuario/2\" },\n" +
                        "  \"marcar-leida\": { \"href\": \"http://localhost:8089/api/v1/notificaciones/8/leida\" },\n" +
                        "  \"enviar-notificacion\": { \"href\": \"http://localhost:8089/api/v1/notificaciones\" }\n" +
                        "}"
        )
        public Object _links;

        @Schema(example = "8", description = "ID de la notificación")
        public Long id;

        @Schema(example = "2", description = "ID del usuario destinatario")
        public Long usuarioId;

        @Schema(example = "Tu pedido fue enviado", description = "Mensaje de la notificación")
        public String mensaje;

        @Schema(example = "2026-07-11T14:30:00", description = "Fecha de envio de la notificación")
        public String fechaEnvio;

        @Schema(example = "true", description = "Si es que se envio la notificación")
        public Boolean enviado;

        @Schema(example = "false", description = "Si es que se leyo la notificación")
        public Boolean leido;
    }


    class NotificacionCollectionOpenApi {
        public EmbeddedData _embedded;

        @Schema(
                description = "Enlaces HATEOAS de la colección de notificaciones",
                example = "{\"self\":{\"href\":\"http://localhost:8089/api/v1/notificaciones/usuario/2\"}}"
        )
        public Object _links;

        public static class EmbeddedData {
            public List<NotificacionHateoasOpenApi> notificaciones;
        }
    }

}
