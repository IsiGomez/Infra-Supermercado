package cl.duoc.usuarios.assemblers;

import cl.duoc.usuarios.controller.PersonController;
import cl.duoc.usuarios.dto.request.PersonRequestDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PersonModelAssembler
        implements RepresentationModelAssembler<PersonResponseDto, EntityModel<PersonResponseDto>> {

    @Override
    public EntityModel<PersonResponseDto> toModel(PersonResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(PersonController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(PersonController.class).getAll()).withRel("persons"),
                linkTo(methodOn(PersonController.class).update(dto.getId(), new PersonRequestDto())).withRel("update"),
                linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete")
        );

    }

}
