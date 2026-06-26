package cl.duoc.inventario.service;

import cl.duoc.inventario.dto.remote.CategoryDto;
import cl.duoc.inventario.dto.remote.ProductDto;
import cl.duoc.inventario.dto.request.InventoryRequestDto;
import cl.duoc.inventario.dto.response.InventoryResponseDto;
import cl.duoc.inventario.mapper.InventoryMapper;
import cl.duoc.inventario.model.Inventory;
import cl.duoc.inventario.repository.InventoryRepository;
import cl.duoc.inventario.service.api.CatalogoClient;
import cl.duoc.inventario.service.impl.InventoryImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - InventoryImpl")
public class InventoryImplTest {

    @Mock
    private InventoryRepository repository;

    @Mock
    private CatalogoClient catalogoClient;

    private InventoryImpl inventoryService;

    private Inventory inventory;
    private ProductDto product;
    private final Long productId = 10L;

    @BeforeEach
    void setUp() {
        InventoryMapper mapper = new InventoryMapper();
        inventoryService = new InventoryImpl(repository, catalogoClient, mapper);

        inventory = new Inventory(1L, productId, 20);
        product = new ProductDto(productId, "Arroz 1kg", "Arroz grado 1", 1500,
                new CategoryDto(3L, "Abarrotes"));
    }


    @Test
    @DisplayName("consultarStock: debería devolver el stock combinado con los datos del catálogo")
    void consultarStock_deberiaDevolverStock_cuandoProductoEstaRegistrado() {
        when(repository.findByProductId(productId)).thenReturn(Optional.of(inventory));
        when(catalogoClient.obtenerProductoPorId(productId)).thenReturn(product);

        InventoryResponseDto result = inventoryService.consultarStock(productId);

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(20);
        assertThat(result.getProductName()).isEqualTo("Arroz 1kg");
        assertThat(result.getPriceUnit()).isEqualTo(1500);
    }


    @Test
    @DisplayName("consultarStock: debería lanzar una excepción cuando el producto no está registrado en inventario")
    void consultarStock_deberiaLanzarExcepcion_cuandoProductoNoRegistrado() {
        when(repository.findByProductId(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.consultarStock(productId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no esta registrado en el inventario");

        verify(catalogoClient, never()).obtenerProductoPorId(any());
    }


    @Test
    @DisplayName("actualizarStock: debería actualizar la cantidad cuando ya existe un registro de inventario")
    void actualizarStock_deberiaActualizarCantidad_cuandoYaExisteRegistro() {
        InventoryRequestDto request = new InventoryRequestDto(1L, productId, 35);

        when(catalogoClient.obtenerProductoPorId(productId)).thenReturn(product);
        when(repository.findByProductId(productId)).thenReturn(Optional.of(inventory));
        when(repository.save(inventory)).thenReturn(inventory);

        InventoryResponseDto result = inventoryService.actualizarStock(request);

        assertThat(result).isNotNull();
        assertThat(inventory.getQuantity()).isEqualTo(35); // se modificó el objeto existente
        verify(repository, times(1)).save(inventory);
    }


    @Test
    @DisplayName("actualizarStock: debería crear un nuevo registro cuando el producto no tenía inventario previo")
    void actualizarStock_deberiaCrearRegistro_cuandoNoExistiaInventarioPrevio() {
        InventoryRequestDto request = new InventoryRequestDto(null, productId, 50);

        when(catalogoClient.obtenerProductoPorId(productId)).thenReturn(product);
        when(repository.findByProductId(productId)).thenReturn(Optional.empty());
        when(repository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponseDto result = inventoryService.actualizarStock(request);

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(50);
        verify(repository, times(1)).save(any(Inventory.class));
    }

}
