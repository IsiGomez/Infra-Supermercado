package cl.duoc.carrito.assemblers;

import cl.duoc.carrito.controller.CartController;
import cl.duoc.carrito.dto.response.CartResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CartModelAssembler
        implements RepresentationModelAssembler<CartResponseDto, EntityModel<CartResponseDto>> {

    @Override
    public EntityModel<CartResponseDto> toModel(CartResponseDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(CartController.class).getCart(dto.getUserId())).withSelfRel(),
                linkTo(methodOn(CartController.class).getTotal(dto.getUserId())).withRel("total"),
                linkTo(methodOn(CartController.class).addItem(dto.getUserId(), null)).withRel("add-item"),
                linkTo(methodOn(CartController.class).clearCart(dto.getUserId())).withRel("clear")
        );
    }
}
