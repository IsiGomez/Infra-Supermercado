package cl.duoc.catalogo.service;

import cl.duoc.catalogo.dto.request.CategoryRequestDto;
import cl.duoc.catalogo.dto.response.CategoryResponseDto;
import cl.duoc.catalogo.mapper.CategoryMapper;
import cl.duoc.catalogo.model.Category;
import cl.duoc.catalogo.repository.CategoryRepository;
import cl.duoc.catalogo.service.impl.CategoryImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - CategoryImpl")
public class CategoryImplTest {

    @Mock
    private CategoryRepository repository;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryImpl service;

    private Category category;
    private CategoryRequestDto requestDto;
    private CategoryResponseDto responseDto;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Lácteos");
        requestDto = new CategoryRequestDto("Lácteos");
        responseDto = new CategoryResponseDto(1L, "Lácteos");
    }


    @Test
    @DisplayName("getById: debe devolver la categoría cuando el id existe")
    void getById_deberiaDevolverCategoria_cuandoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(mapper.toDto(category)).thenReturn(responseDto);

        CategoryResponseDto resultado = service.getById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getName()).isEqualTo("Lácteos");
        verify(repository, times(1)).findById(1L);
    }


    @Test
    @DisplayName("getById: debe lanzar una excepcion cuando el id no existe")
    void getById_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(mapper, never()).toDto(any());
    }


    @Test
    @DisplayName("getAll: debe devolver la lista de categorías")
    void getAll_deberiaDevolverListaDeCategorias() {
        Category cat2 = new Category(2L, "Carnes");
        CategoryResponseDto dto2 = new CategoryResponseDto(2L, "Carnes");

        when(repository.findAll()).thenReturn(List.of(category, cat2));
        when(mapper.toDtoList(anyList())).thenReturn(List.of(responseDto, dto2));

        List<CategoryResponseDto> resultado = service.getAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(CategoryResponseDto::getName)
                .containsExactly("Lácteos", "Carnes");
        verify(repository, times(1)).findAll();
    }


    @Test
    @DisplayName("getAll: debe devolver lista vacía si no hay categorías")
    void getAll_deberiaDevolverVacio_siNoHayCategorias() {
        when(repository.findAll()).thenReturn(List.of());
        when(mapper.toDtoList(List.of())).thenReturn(List.of());

        List<CategoryResponseDto> resultado = service.getAll();

        assertThat(resultado).isEmpty();
    }


    @Test
    @DisplayName("create: debe crear la categoría cuando el nombre es único")
    void create_deberiaCrearCategoria_cuandoNombreEsUnico() {
        when(repository.existsByNameIgnoreCase("Lácteos")).thenReturn(false);

        when(mapper.toEntity(requestDto)).thenReturn(category);
        when(repository.save(category)).thenReturn(category);
        when(mapper.toDto(category)).thenReturn(responseDto);

        CategoryResponseDto resultado = service.create(requestDto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getName()).isEqualTo("Lácteos");
        verify(repository, times(1)).save(category);
    }


    @Test
    @DisplayName("create: debe lanzar una excepción cuando el nombre ya existe")
    void create_deberiaLanzarExcepcion_cuandoNombreYaExiste() {
        when(repository.existsByNameIgnoreCase("Lácteos")).thenReturn(true);

        assertThatThrownBy(() -> service.create(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lácteos");

        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("update: debe actualizar la categoría cuando el id existe y el nombre es único")
    void update_deberiaActualizarCategoria_cuandoDatosSonValidos() {
        CategoryRequestDto updateDto = new CategoryRequestDto("Bebidas");

        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.existsByNameIgnoreCaseAndIdNot("Bebidas", 1L)).thenReturn(false);

        when(repository.save(category)).thenReturn(category);
        when(mapper.toDto(category)).thenReturn(new CategoryResponseDto(1L, "Bebidas"));

        CategoryResponseDto resultado = service.update(1L, updateDto);

        assertThat(resultado).isNotNull();
        verify(repository, times(1)).save(category);
    }


    @Test
    @DisplayName("update: debe lanzar una excepción si la categoría no existe")
    void update_deberiaLanzarExcepcion_cuandoCategoriaNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("update: debe lanzar excepción si el nuevo nombre ya lo usa otra categoría")
    void update_deberiaLanzarExcepcion_cuandoNombreYaUsadoPorOtra() {
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.existsByNameIgnoreCaseAndIdNot("Lácteos", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(1L, requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lácteos");

        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("delete: debe devolver true cuando la categoría existe y se elimina")
    void delete_deberiaDevolverTrue_cuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean resultado = service.delete(1L);

        assertThat(resultado).isTrue();
        verify(repository, times(1)).deleteById(1L);
    }


    @Test
    @DisplayName("delete: debe devolver false cuando la categoría no existe")
    void delete_deberiaDevolverFalse_cuandoNoExiste() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean resultado = service.delete(99L);

        assertThat(resultado).isFalse();
        verify(repository, never()).deleteById(any());
    }

}
