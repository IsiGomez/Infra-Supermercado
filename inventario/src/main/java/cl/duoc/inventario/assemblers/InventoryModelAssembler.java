package cl.duoc.inventario.assemblers;

import cl.duoc.inventario.controller.InventoryController;
import cl.duoc.inventario.dto.response.InventoryResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InventoryModelAssembler
        implements RepresentationModelAssembler<InventoryResponseDto, EntityModel<InventoryResponseDto>>{

    @Override
    public EntityModel<InventoryResponseDto> toModel(InventoryResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(InventoryController.class).getStock(dto.getProductId())).withSelfRel(),
                linkTo(methodOn(InventoryController.class).updateStock(null))
                        .withRel("update-stock")
        );
    }

}
