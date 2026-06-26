package cl.duoc.inventario.service;


import cl.duoc.inventario.dto.request.InventoryRequestDto;
import cl.duoc.inventario.dto.response.InventoryResponseDto;

public interface InventoryService {

    InventoryResponseDto consultarStock(Long productId);
    InventoryResponseDto actualizarStock(InventoryRequestDto requestDto);

}
