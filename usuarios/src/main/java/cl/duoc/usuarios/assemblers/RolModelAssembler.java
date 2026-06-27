package cl.duoc.usuarios.assemblers;

import cl.duoc.usuarios.controller.RolController;
import cl.duoc.usuarios.dto.response.RolResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RolModelAssembler
        implements RepresentationModelAssembler<RolResponseDto, EntityModel<RolResponseDto>> {

    @Override
    public EntityModel<RolResponseDto> toModel(RolResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(RolController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(RolController.class).getAll()).withRel("roles")
        );

    }

}
