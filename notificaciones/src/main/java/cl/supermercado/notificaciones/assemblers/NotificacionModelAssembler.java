package cl.supermercado.notificaciones.assemblers;

import cl.supermercado.notificaciones.controller.NotificacionController;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificacionModelAssembler
        implements RepresentationModelAssembler<NotificacionResponseDto, EntityModel<NotificacionResponseDto>> {

    @Override
    public EntityModel<NotificacionResponseDto> toModel(NotificacionResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(NotificacionController.class).listarPorUsuario(dto.getUsuarioId()))
                        .withSelfRel(),
                linkTo(methodOn(NotificacionController.class).marcarComoLeida(dto.getId()))
                        .withRel("marcar-leida")
        );

    }

}
