package cl.duoc.usuarios.assemblers;

import cl.duoc.usuarios.config.SecurityUtil;
import cl.duoc.usuarios.controller.PersonController;
import cl.duoc.usuarios.dto.request.PersonRequestDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PersonModelAssembler
        implements RepresentationModelAssembler<PersonResponseDto, EntityModel<PersonResponseDto>> {

    @Override
    public EntityModel<PersonResponseDto> toModel(PersonResponseDto dto) {
        List<Link> links = new ArrayList<>();

        boolean esPropio = dto.getId() != null && dto.getId().equals(SecurityUtil.currentUserId());

        links.add(linkTo(methodOn(PersonController.class).getById(dto.getId())).withSelfRel());

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(PersonController.class).getAll()).withRel("persons"));
        }

        if (SecurityUtil.isFuncionario() || esPropio) {
            links.add(linkTo(methodOn(PersonController.class).update(dto.getId(), new PersonRequestDto())).withRel("update"));
            links.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete"));
        }

        return EntityModel.of(dto, links);
    }

}
