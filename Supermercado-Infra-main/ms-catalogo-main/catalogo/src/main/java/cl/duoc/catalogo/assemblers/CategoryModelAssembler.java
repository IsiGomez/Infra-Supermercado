package cl.duoc.catalogo.assemblers;

import cl.duoc.catalogo.controller.CategoryController;
import cl.duoc.catalogo.dto.response.CategoryResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssembler
        implements RepresentationModelAssembler<CategoryResponseDto, EntityModel<CategoryResponseDto>>{

    @Override
    public EntityModel<CategoryResponseDto> toModel(CategoryResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(CategoryController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAll()).withRel("categories"),
                linkTo(methodOn(CategoryController.class).update(dto.getId(), null)).withRel("update"),
                linkTo(methodOn(CategoryController.class).delete(dto.getId())).withRel("delete")
        );
    }

}
