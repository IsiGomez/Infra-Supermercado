package cl.supermercado.promociones.assemblers;

import cl.supermercado.promociones.controller.PromocionController;
import cl.supermercado.promociones.dto.response.PromocionResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PromocionModelAssembler
        implements RepresentationModelAssembler<PromocionResponseDto, EntityModel<PromocionResponseDto>> {

    @Override
    public EntityModel<PromocionResponseDto> toModel(PromocionResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(PromocionController.class).obtenerPorCodigo(dto.getCodigo())).withSelfRel(),
                linkTo(methodOn(PromocionController.class).listarPromociones()).withRel("promociones"),
                linkTo(methodOn(PromocionController.class).listarVigentes()).withRel("vigentes")
        );
    }
}
