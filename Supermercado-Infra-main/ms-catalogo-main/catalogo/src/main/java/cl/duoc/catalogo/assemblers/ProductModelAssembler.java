package cl.duoc.catalogo.assemblers;

import cl.duoc.catalogo.controller.ProductController;
import cl.duoc.catalogo.dto.response.ProductResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler
        implements RepresentationModelAssembler<ProductResponseDto, EntityModel<ProductResponseDto>>{

    @Override
    public EntityModel<ProductResponseDto> toModel(ProductResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ProductController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).getAll()).withRel("products"),
                linkTo(methodOn(ProductController.class)
                        .getByCategoryId(dto.getCategory() != null ? dto.getCategory().getId() : null))
                        .withRel("by-category"),
                linkTo(methodOn(ProductController.class).update(dto.getId(), null)).withRel("update"),
                linkTo(methodOn(ProductController.class).delete(dto.getId())).withRel("delete")
        );
    }

}
