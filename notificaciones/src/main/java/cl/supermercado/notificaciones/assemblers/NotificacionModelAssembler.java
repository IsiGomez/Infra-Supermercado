package cl.supermercado.notificaciones.assemblers;

import cl.supermercado.notificaciones.config.SecurityUtil;
import cl.supermercado.notificaciones.controller.NotificacionController;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificacionModelAssembler
        implements RepresentationModelAssembler<NotificacionResponseDto, EntityModel<NotificacionResponseDto>> {

    @Override
    public EntityModel<NotificacionResponseDto> toModel(NotificacionResponseDto dto) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(NotificacionController.class)
                .obtenerPorId(dto.getId())).withSelfRel());

        links.add(linkTo(methodOn(NotificacionController.class)
                .listarPorUsuario(dto.getUsuarioId())).withRel("notificaciones-usuario"));

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(NotificacionController.class)
                    .enviar(null)).withRel("enviar-notificacion"));
        }

        if (SecurityUtil.isCliente()) {
            links.add(linkTo(methodOn(NotificacionController.class)
                    .marcarComoLeida(dto.getId())).withRel("marcar-leida"));
        }

        return EntityModel.of(dto, links);
    }

}
