package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.response.RolResponseDto;
import cl.duoc.usuarios.mapper.RolMapper;
import cl.duoc.usuarios.model.Rol;
import cl.duoc.usuarios.repository.RolRepository;
import cl.duoc.usuarios.service.impl.RolImpl;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - RolImpl")
public class RolImplTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private RolMapper rolMapper;

    @InjectMocks
    private RolImpl rolService;

    private Rol rol;
    private RolResponseDto rolDto;

    @BeforeEach
    void setUp() {
        rol = new Rol(1L, "FUNCIONARIO", "Rol de funcionario del supermercado");
        rolDto = new RolResponseDto(1L, "FUNCIONARIO", "Rol de funcionario del supermercado");
    }


    @Test
    @DisplayName("getById: debería retornar el rol cuando el id existe")
    void getById_deberiaRetornarRol_cuandoExiste() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(rolMapper.toDto(rol)).thenReturn(rolDto);

        RolResponseDto resultado = rolService.getById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getName()).isEqualTo("FUNCIONARIO");
        verify(rolRepository, times(1)).findById(1L);
    }


    @Test
    @DisplayName("getById: debería lanzar una excepción cuando el id no existe")
    void getById_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rolService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(rolMapper, never()).toDto(any());
    }


    @Test
    @DisplayName("getAll: debería retornar la lista completa de roles")
    void getAll_deberiaRetornarListaDeRoles() {
        Rol rol2 = new Rol(2L, "CLIENTE", "Rol de cliente");
        RolResponseDto dto2 = new RolResponseDto(2L, "CLIENTE", "Rol de cliente");
        when(rolRepository.findAll()).thenReturn(List.of(rol, rol2));
        when(rolMapper.toDtoList(List.of(rol, rol2))).thenReturn(List.of(rolDto, dto2));

        List<RolResponseDto> resultado = rolService.getAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(RolResponseDto::getName)
                .containsExactly("FUNCIONARIO", "CLIENTE");
        verify(rolRepository, times(1)).findAll();
    }


    @Test
    @DisplayName("getAll: debería retornar una lista vacía si no hay roles")
    void getAll_deberiaRetornarVacio_siNoHayRoles() {
        when(rolRepository.findAll()).thenReturn(List.of());
        when(rolMapper.toDtoList(List.of())).thenReturn(List.of());

        List<RolResponseDto> resultado = rolService.getAll();

        assertThat(resultado).isEmpty();
    }

}
