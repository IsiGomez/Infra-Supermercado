package cl.supermercado.seguimiento.assemblers;

import cl.supermercado.seguimiento.controller.SeguimientoController;
import cl.supermercado.seguimiento.dto.response.SeguimientoResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SeguimientoModelAssembler
        implements RepresentationModelAssembler<SeguimientoResponseDto, EntityModel<SeguimientoResponseDto>> {

    @Override
    public EntityModel<SeguimientoResponseDto> toModel(SeguimientoResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(SeguimientoController.class).historialPorCompra(dto.getCompraId()))
                        .withSelfRel(),
                linkTo(methodOn(SeguimientoController.class).listarPorUsuario(dto.getUsuarioId()))
                        .withRel("seguimientos-usuario")
        );
    }

}
