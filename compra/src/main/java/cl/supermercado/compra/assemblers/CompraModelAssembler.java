package cl.supermercado.compra.assemblers;

import cl.supermercado.compra.config.SecurityUtil;
import cl.supermercado.compra.controller.CompraController;
import cl.supermercado.compra.dto.response.CompraResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CompraModelAssembler
        implements RepresentationModelAssembler<CompraResponseDto, EntityModel<CompraResponseDto>> {

    @Override
    public EntityModel<CompraResponseDto> toModel(CompraResponseDto dto) {
        List<Link> links = new ArrayList<>();

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(CompraController.class)
                    .listarCompras()).withSelfRel());
        } else {
            links.add(linkTo(methodOn(CompraController.class)
                    .listarComprasPorUsuario(dto.getUsuarioId())).withSelfRel());
        }

        if (SecurityUtil.isCliente()) {
            links.add(linkTo(methodOn(CompraController.class)
                    .crearCompra(null)).withRel("crear-compra"));
        }

        return EntityModel.of(dto, links);
    }

}
