package cl.supermercado.promociones.assemblers;

import cl.supermercado.promociones.config.SecurityUtil;
import cl.supermercado.promociones.controller.PromocionController;
import cl.supermercado.promociones.dto.response.PromocionResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PromocionModelAssembler
        implements RepresentationModelAssembler<PromocionResponseDto, EntityModel<PromocionResponseDto>> {

    @Override
    public EntityModel<PromocionResponseDto> toModel(PromocionResponseDto dto) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(PromocionController.class)
                .listarPromociones()).withSelfRel());

        links.add(linkTo(methodOn(PromocionController.class)
                .listarVigentes()).withRel("promociones-vigente"));

        links.add(linkTo(methodOn(PromocionController.class)
                .obtenerPorCodigo(dto.getCodigo())).withRel("codigo-promocion"));

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(PromocionController.class)
                    .crearPromocion(null)).withRel("crear-promocion"));
        }

        return EntityModel.of(dto, links);
    }
}
