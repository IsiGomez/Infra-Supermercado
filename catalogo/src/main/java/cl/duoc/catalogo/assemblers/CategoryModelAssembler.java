package cl.duoc.catalogo.assemblers;

import cl.duoc.catalogo.config.SecurityUtil;
import cl.duoc.catalogo.controller.CategoryController;
import cl.duoc.catalogo.dto.response.CategoryResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssembler
        implements RepresentationModelAssembler<CategoryResponseDto, EntityModel<CategoryResponseDto>>{

    @Override
    public EntityModel<CategoryResponseDto> toModel(CategoryResponseDto dto) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(CategoryController.class).getById(dto.getId())).withSelfRel());
        links.add(linkTo(methodOn(CategoryController.class).getAll()).withRel("categories"));

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(CategoryController.class).update(dto.getId(), null)).withRel("update"));
            links.add(linkTo(methodOn(CategoryController.class).delete(dto.getId())).withRel("delete"));
        }

        return EntityModel.of(dto, links);
    }

}
