package cl.duoc.inventario.service.impl;
import cl.duoc.inventario.dto.remote.ProductDto;
import cl.duoc.inventario.dto.request.InventoryRequestDto;
import cl.duoc.inventario.dto.response.InventoryResponseDto;
import cl.duoc.inventario.mapper.InventoryMapper;
import cl.duoc.inventario.model.Inventory;
import cl.duoc.inventario.repository.InventoryRepository;
import cl.duoc.inventario.service.InventoryService;
import cl.duoc.inventario.service.api.CatalogoClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryImpl implements InventoryService {

    private final InventoryRepository repository;
    private final CatalogoClient catalogoClient;
    private final InventoryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDto consultarStock(Long productId){
        log.info("Consultando existencia del producto ID: {}", productId);
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("El producto con ID " + productId + " no esta registrado en el inventario"));

        log.info("Stock encontrado para producto {}. Solicitando datos a catalogo", productId);
        ProductDto product = catalogoClient.obtenerProductoPorId(productId);

        return mapper.toDto(inventory, product);

    }


    @Override
    @Transactional
    public InventoryResponseDto actualizarStock(InventoryRequestDto request){

        log.info("Actualización de stock para producto ID: {}", request.getProductId());
        ProductDto product = catalogoClient.obtenerProductoPorId(request.getProductId());

        Optional<Inventory> inventoryExists = repository.findByProductId(request.getProductId());
        Inventory inventory;

        if (inventoryExists.isPresent()){
            inventory = inventoryExists.get();

            log.info("El producto con ID: {} ya cuenta con registro de inventario. Modificando cantidad a: {}",
                    request.getProductId(), request.getQuantity());

            inventory.setQuantity(request.getQuantity());
        } else {
            log.warn("No se encontró un registro previo de inventario para el producto ID: {}. Creando nuevo registro base.",
                    request.getProductId());

            inventory = mapper.toEntity(request);
        }

        repository.save(inventory);

        log.info("Registro de inventario guardado exitosamente para el producto ID: {}", request.getProductId());
        return mapper.toDto(inventory, product);

    }

}
