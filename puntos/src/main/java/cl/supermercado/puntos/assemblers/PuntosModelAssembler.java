package cl.supermercado.puntos.assemblers;

import cl.supermercado.puntos.controller.PuntosController;
import cl.supermercado.puntos.dto.response.PuntosResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PuntosModelAssembler
        implements RepresentationModelAssembler<PuntosResponseDto, EntityModel<PuntosResponseDto>> {

    @Override
    public EntityModel<PuntosResponseDto> toModel(PuntosResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(PuntosController.class).consultarPuntos(dto.getUsuarioId())).withSelfRel(),
                linkTo(methodOn(PuntosController.class).simularCanje(dto.getUsuarioId()))
                        .withRel("simular-canje")
        );
    }

}
