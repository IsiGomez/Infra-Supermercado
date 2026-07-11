package cl.supermercado.puntos.assemblers;

import cl.supermercado.puntos.config.SecurityUtil;
import cl.supermercado.puntos.controller.PuntosController;
import cl.supermercado.puntos.dto.response.PuntosResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PuntosModelAssembler
        implements RepresentationModelAssembler<PuntosResponseDto, EntityModel<PuntosResponseDto>> {

    @Override
    public EntityModel<PuntosResponseDto> toModel(PuntosResponseDto dto) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(PuntosController.class).consultarPuntos(dto.getUsuarioId())).withSelfRel());
        links.add(linkTo(methodOn(PuntosController.class).simularCanje(dto.getUsuarioId()))
                .withRel("simular-canje"));

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(PuntosController.class).asignarPuntos(null))
                    .withRel("asignar-puntos"));
        }

        if (SecurityUtil.isCliente()){
            links.add(linkTo(methodOn(PuntosController.class).confirmarCanje(null))
                    .withRel("confirmar-canje"));
        }

        return EntityModel.of(dto, links);
    }

}
