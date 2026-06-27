package cl.supermercado.pago.assemblers;

import cl.supermercado.pago.controller.PagoController;
import cl.supermercado.pago.dto.response.PagoResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PagoModelAssembler
        implements RepresentationModelAssembler<PagoResponseDto, EntityModel<PagoResponseDto>> {

    @Override
    public EntityModel<PagoResponseDto> toModel(PagoResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(PagoController.class)
                        .obtenerUltimoPagoExitoso(dto.getUsuarioId())).withSelfRel(),
                linkTo(methodOn(PagoController.class)
                        .tieneUltimoPagoExitoso(dto.getUsuarioId())).withRel("estado")
        );
    }
}
