package cl.duoc.usuarios.assemblers;

import cl.duoc.usuarios.config.SecurityUtil;
import cl.duoc.usuarios.controller.LoginController;
import cl.duoc.usuarios.dto.request.LoginRequestDto;
import cl.duoc.usuarios.dto.response.LoginAllResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LoginModelAssembler
        implements RepresentationModelAssembler<LoginAllResponseDto, EntityModel<LoginAllResponseDto>> {

    @Override
    public EntityModel<LoginAllResponseDto> toModel(LoginAllResponseDto dto) {
        List<Link> links = new ArrayList<>();

        boolean esPropio = dto.getPerson() != null
                && dto.getPerson().getId() != null
                && dto.getPerson().getId().equals(SecurityUtil.currentUserId());

        links.add(linkTo(methodOn(LoginController.class)
                .getById(dto.getId())).withSelfRel());

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(LoginController.class)
                    .getAll()).withRel("logins"));
        }

        if (SecurityUtil.isFuncionario() || esPropio) {
            links.add(linkTo(methodOn(LoginController.class)
                    .update(dto.getId(), new LoginRequestDto())).withRel("update"));

            links.add(linkTo(methodOn(LoginController.class)
                    .delete(dto.getId())).withRel("delete"));
        }

        return EntityModel.of(dto, links);

    }

}
