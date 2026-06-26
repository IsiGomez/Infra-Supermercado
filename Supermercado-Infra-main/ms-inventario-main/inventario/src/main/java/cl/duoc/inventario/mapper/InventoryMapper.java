package cl.duoc.inventario.mapper;

import cl.duoc.inventario.dto.remote.ProductDto;
import cl.duoc.inventario.dto.request.InventoryRequestDto;
import cl.duoc.inventario.dto.response.InventoryResponseDto;
import cl.duoc.inventario.model.Inventory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class InventoryMapper {

    public InventoryResponseDto toDto(Inventory inventory, ProductDto product){
        InventoryResponseDto dto = new InventoryResponseDto();

        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProductId());
        dto.setQuantity(inventory.getQuantity());
        dto.setProductName(product.getName());
        dto.setPriceUnit(product.getPrice());

        return dto;

    }

    public Inventory toEntity(InventoryRequestDto dto){
        Inventory inventory = new Inventory();

        inventory.setQuantity(dto.getQuantity());

        return inventory;
    }

}
