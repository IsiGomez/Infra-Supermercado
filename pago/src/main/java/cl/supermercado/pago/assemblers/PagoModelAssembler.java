package cl.supermercado.pago.assemblers;

import cl.supermercado.pago.config.SecurityUtil;
import cl.supermercado.pago.controller.PagoController;
import cl.supermercado.pago.dto.response.PagoResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PagoModelAssembler
        implements RepresentationModelAssembler<PagoResponseDto, EntityModel<PagoResponseDto>> {

    @Override
    public EntityModel<PagoResponseDto> toModel(PagoResponseDto dto) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(PagoController.class)
                .obtenerUltimoPagoExitoso(dto.getUsuarioId())).withSelfRel());
        links.add(linkTo(methodOn(PagoController.class)
                .tieneUltimoPagoExitoso(dto.getUsuarioId())).withRel("estado"));

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(PagoController.class).listarPagos())
                    .withRel("todos-los-pagos"));
        }

        return EntityModel.of(dto, links);
    }

}
