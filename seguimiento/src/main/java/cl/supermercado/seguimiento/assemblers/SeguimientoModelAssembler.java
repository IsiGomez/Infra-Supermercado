package cl.supermercado.seguimiento.assemblers;

import cl.supermercado.seguimiento.config.SecurityUtil;
import cl.supermercado.seguimiento.controller.SeguimientoController;
import cl.supermercado.seguimiento.dto.response.SeguimientoResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SeguimientoModelAssembler
        implements RepresentationModelAssembler<SeguimientoResponseDto, EntityModel<SeguimientoResponseDto>> {

    @Override
    public EntityModel<SeguimientoResponseDto> toModel(SeguimientoResponseDto dto) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(SeguimientoController.class)
                .historialPorCompra(dto.getCompraId())).withSelfRel());

        links.add(linkTo(methodOn(SeguimientoController.class)
                .listarPorUsuario(dto.getUsuarioId())).withRel("seguimientos-usuario"));

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(SeguimientoController.class)
                    .registrarSeguimiento(null)).withRel("registrar-seguimiento"));
        }

        return EntityModel.of(dto, links);
    }

}
