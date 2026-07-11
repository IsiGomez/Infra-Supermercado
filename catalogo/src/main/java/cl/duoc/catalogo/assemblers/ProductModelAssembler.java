package cl.duoc.catalogo.assemblers;

import cl.duoc.catalogo.config.SecurityUtil;
import cl.duoc.catalogo.controller.ProductController;
import cl.duoc.catalogo.dto.response.ProductResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler
        implements RepresentationModelAssembler<ProductResponseDto, EntityModel<ProductResponseDto>>{

    @Override
    public EntityModel<ProductResponseDto> toModel(ProductResponseDto dto) {
        List<Link> links = new ArrayList<>();

        links.add(linkTo(methodOn(ProductController.class).getById(dto.getId())).withSelfRel());
        links.add(linkTo(methodOn(ProductController.class).getAll()).withRel("products"));
        links.add(linkTo(methodOn(ProductController.class)
                .getByCategoryId(dto.getCategory() != null ? dto.getCategory().getId() : null))
                .withRel("by-category"));

        if (SecurityUtil.isFuncionario()) {
            links.add(linkTo(methodOn(ProductController.class).update(dto.getId(), null)).withRel("update"));
            links.add(linkTo(methodOn(ProductController.class).delete(dto.getId())).withRel("delete"));
        }

        return EntityModel.of(dto, links);
    }

}
