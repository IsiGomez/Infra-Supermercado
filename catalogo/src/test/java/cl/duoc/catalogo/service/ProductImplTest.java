package cl.duoc.catalogo.service;

import cl.duoc.catalogo.dto.request.ProductRequestDto;
import cl.duoc.catalogo.dto.response.ProductResponseDto;
import cl.duoc.catalogo.mapper.ProductMapper;
import cl.duoc.catalogo.model.Category;
import cl.duoc.catalogo.model.Product;
import cl.duoc.catalogo.repository.CategoryRepository;
import cl.duoc.catalogo.repository.ProductRepository;
import cl.duoc.catalogo.service.impl.ProductImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - ProductImpl")
public class ProductImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductImpl productService;

    private Product product;
    private Category category;
    private ProductRequestDto requestDto;
    private ProductResponseDto responseDto;

    @BeforeEach
    void setUp() {
        category = new Category(2L, "Lácteos");
        product = new Product(1L, "Leche Descremada 1L", "Leche sin lactosa", 1290, category);
        requestDto = new ProductRequestDto("Leche Descremada 1L", "Leche sin lactosa", 1290, 2L);
        responseDto = new ProductResponseDto(1L, "Leche Descremada 1L", "Leche sin lactosa", 1290, null);
    }


    @Test
    @DisplayName("getById: debería devolver el producto cuando el id existe")
    void getById_deberiaDevolverProducto_cuandoExiste() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(responseDto);

        ProductResponseDto result = productService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Leche Descremada 1L");
    }


    @Test
    @DisplayName("getById: debería lanzar una excepción cuando el producto no existe")
    void getById_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(productMapper, never()).toDto(any());
    }


    @Test
    @DisplayName("getByIds: debería lanzar una excepción cuando falta alguno de los ids solicitados")
    void getByIds_deberiaLanzarExcepcion_cuandoFaltaAlgunId() {
        List<Long> idsSolicitados = List.of(1L, 50L);
        when(productRepository.findByIdIn(idsSolicitados)).thenReturn(List.of(product));

        assertThatThrownBy(() -> productService.getByIds(idsSolicitados))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("50");

        verify(productMapper, never()).toDtoList(any());
    }


    @Test
    @DisplayName("getByIds: debería devolver todos los productos cuando todos los ids existen")
    void getByIds_deberiaDevolverProductos_cuandoTodosExisten() {
        List<Long> idsSolicitados = List.of(1L);
        when(productRepository.findByIdIn(idsSolicitados)).thenReturn(List.of(product));
        when(productMapper.toDtoList(List.of(product))).thenReturn(List.of(responseDto));

        List<ProductResponseDto> result = productService.getByIds(idsSolicitados);

        assertThat(result).hasSize(1);
    }


    @Test
    @DisplayName("getByCategoryId: debería lanzar una excepción cuando la categoría no existe")
    void getByCategoryId_deberiaLanzarExcepcion_cuandoCategoriaNoExiste() {
        when(categoryRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> productService.getByCategoryId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");

        verify(productRepository, never()).findByCategoryId(any());
    }


    @Test
    @DisplayName("getByCategoryIdAndPriceBetween: debería lanzar una excepción cuando precio mínimo es mayor al máximo")
    void getByCategoryIdAndPriceBetween_deberiaLanzarExcepcion_cuandoMinMayorQueMax() {
        assertThatThrownBy(() -> productService.getByCategoryIdAndPriceBetween(2L, 5000, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio minimo");

        verify(categoryRepository, never()).existsById(any());
    }


    @Test
    @DisplayName("getByCategoryIdAndPriceBetween: debería lanzar una excepción cuando el precio mínimo es negativo")
    void getByCategoryIdAndPriceBetween_deberiaLanzarExcepcion_cuandoPrecioEsNegativo() {
        assertThatThrownBy(() -> productService.getByCategoryIdAndPriceBetween(2L, -100, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativos");
    }


    @Test
    @DisplayName("create: debería crear el producto cuando el nombre es único y la categoría existe")
    void create_deberiaCrearProducto_cuandoNombreEsUnicoYCategoriaExiste() {
        when(productRepository.existsByNameIgnoreCase(requestDto.getName())).thenReturn(false);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        when(productMapper.toEntity(requestDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(responseDto);

        ProductResponseDto result = productService.create(requestDto);

        assertThat(result).isNotNull();
        verify(productRepository, times(1)).save(product);
    }


    @Test
    @DisplayName("create: debería lanzar una excepción cuando el nombre del producto ya existe")
    void create_deberiaLanzarExcepcion_cuandoNombreYaExiste() {
        when(productRepository.existsByNameIgnoreCase(requestDto.getName())).thenReturn(true);

        assertThatThrownBy(() -> productService.create(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un producto");

        verify(categoryRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }


    @Test
    @DisplayName("create: debería lanzar una excepción cuando la categoría no existe")
    void create_deberiaLanzarExcepcion_cuandoCategoriaNoExiste() {
        when(productRepository.existsByNameIgnoreCase(requestDto.getName())).thenReturn(false);
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Categoría no encontrada");

        verify(productRepository, never()).save(any());
    }


    @Test
    @DisplayName("update: debería actualizar el producto cuando los datos son válidos")
    void update_deberiaActualizarProducto_cuandoDatosSonValidos() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByNameIgnoreCaseAndIdNot(requestDto.getName(), 1L)).thenReturn(false);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(responseDto);

        ProductResponseDto result = productService.update(1L, requestDto);

        assertThat(result).isNotNull();
        verify(productRepository, times(1)).save(product);
    }


    @Test
    @DisplayName("update: debería lanzar una excepción cuando el producto no existe")
    void update_deberiaLanzarExcepcion_cuandoProductoNoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, requestDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(productRepository, never()).save(any());
    }


    @Test
    @DisplayName("update: debería lanzar excepción cuando el nuevo nombre ya lo usa otro producto")
    void update_deberiaLanzarExcepcion_cuandoNombreYaLoUsaOtroProducto() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByNameIgnoreCaseAndIdNot(requestDto.getName(), 1L)).thenReturn(true);

        assertThatThrownBy(() -> productService.update(1L, requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un producto");

        verify(categoryRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }


    @Test
    @DisplayName("delete: debería devolver true cuando el producto existe y se elimina")
    void delete_deberiaDevolverTrue_cuandoProductoExiste() {
        when(productRepository.existsById(1L)).thenReturn(true);

        boolean result = productService.delete(1L);

        assertThat(result).isTrue();
        verify(productRepository, times(1)).deleteById(1L);
    }


    @Test
    @DisplayName("delete: debería devolver false cuando el producto no existe")
    void delete_deberiaDevolverFalse_cuandoProductoNoExiste() {
        when(productRepository.existsById(99L)).thenReturn(false);

        boolean result = productService.delete(99L);

        assertThat(result).isFalse();
        verify(productRepository, never()).deleteById(anyLong());
    }

}
