package cl.duoc.usuarios.assemblers;

import cl.duoc.usuarios.controller.LoginController;
import cl.duoc.usuarios.dto.request.LoginRequestDto;
import cl.duoc.usuarios.dto.response.LoginAllResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LoginModelAssembler
        implements RepresentationModelAssembler<LoginAllResponseDto, EntityModel<LoginAllResponseDto>> {

    @Override
    public EntityModel<LoginAllResponseDto> toModel(LoginAllResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(LoginController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(LoginController.class).getAll()).withRel("logins"),
                linkTo(methodOn(LoginController.class).update(dto.getId(), new LoginRequestDto())).withRel("update"),
                linkTo(methodOn(LoginController.class).delete(dto.getId())).withRel("delete")
        );

    }

}
