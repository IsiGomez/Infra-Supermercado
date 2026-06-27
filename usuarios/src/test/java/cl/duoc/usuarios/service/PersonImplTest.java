package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.request.PersonRequestDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import cl.duoc.usuarios.mapper.PersonMapper;
import cl.duoc.usuarios.model.Person;
import cl.duoc.usuarios.repository.PersonRepository;
import cl.duoc.usuarios.service.impl.PersonImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - PersonImpl")
public class PersonImplTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonImpl personService;

    private Person person;
    private PersonRequestDto requestDto;
    private PersonResponseDto responseDto;

    @BeforeEach
    void setUp() {
        person = new Person(1L, "12345678-9", "Claudia", "Gonzales",
                "claudia@supermercado.cl", "912345678");

        requestDto = new PersonRequestDto(
                "12345678-9", "Claudia", "Gonzales",
                "claudia@supermercado.cl", "912345678");

        responseDto = new PersonResponseDto(
                1L, "12345678-9", "Claudia", "Gonzales",
                "claudia@supermercado.cl", "912345678");
    }


    @Test
    @DisplayName("getById: debería retornar a la persona cuando el id existe")
    void getById_deberiaRetornarPersona_cuandoExiste() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personMapper.toDto(person)).thenReturn(responseDto);

        PersonResponseDto result = personService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo("12345678-9");
        verify(personRepository, times(1)).findById(1L);
    }


    @Test
    @DisplayName("getById: debería lanzar excepción cuando el id no existe")
    void getById_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(personRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(personMapper, never()).toDto(any());
    }


    @Test
    @DisplayName("getAll: debería retornar la lista de personas")
    void getAll_deberiaRetornarListaDePersonas() {
        when(personRepository.findAll()).thenReturn(List.of(person));
        when(personMapper.toDtoList(List.of(person))).thenReturn(List.of(responseDto));

        List<PersonResponseDto> result = personService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("claudia@supermercado.cl");
    }


    @Test
    @DisplayName("create: debería crear a la persona cuando el rut y el email son únicos")
    void create_deberiaCrearPersona_cuandoRutYEmailSonUnicos() {
        when(personRepository.existsByRutIgnoreCase(requestDto.getRut())).thenReturn(false);
        when(personRepository.existsByEmailIgnoreCase(requestDto.getEmail())).thenReturn(false);

        when(personMapper.toEntity(requestDto)).thenReturn(person);
        when(personRepository.save(person)).thenReturn(person);
        when(personMapper.toDto(person)).thenReturn(responseDto);

        PersonResponseDto result = personService.create(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo(requestDto.getRut());
        verify(personRepository, times(1)).save(person);
    }


    @Test
    @DisplayName("create: debería lanzar excepción cuando el rut ya existe")
    void create_deberiaLanzarExcepcion_cuandoRutYaExiste() {
        when(personRepository.existsByRutIgnoreCase(requestDto.getRut())).thenReturn(true);

        assertThatThrownBy(() -> personService.create(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rut");

        verify(personRepository, never()).existsByEmailIgnoreCase(any());
        verify(personRepository, never()).save(any());
    }


    @Test
    @DisplayName("create: debería lanzar excepción cuando el email ya existe")
    void create_deberiaLanzarExcepcion_cuandoEmailYaExiste() {
        when(personRepository.existsByRutIgnoreCase(requestDto.getRut())).thenReturn(false);
        when(personRepository.existsByEmailIgnoreCase(requestDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> personService.create(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");

        verify(personRepository, never()).save(any());
    }


    @Test
    @DisplayName("update: debería actualizar a la persona cuando los datos son válidos")
    void update_deberiaActualizarPersona_cuandoDatosSonValidos() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.existsByRutIgnoreCaseAndIdNot(requestDto.getRut(), 1L)).thenReturn(false);
        when(personRepository.existsByEmailIgnoreCaseAndIdNot(requestDto.getEmail(), 1L)).thenReturn(false);
        when(personRepository.save(any(Person.class))).thenReturn(person);
        when(personMapper.toDto(person)).thenReturn(responseDto);

        PersonResponseDto result = personService.update(1L, requestDto);

        assertThat(result).isNotNull();
        verify(personRepository, times(1)).save(person);
    }


    @Test
    @DisplayName("update: debería lanzar excepción cuando la persona no existe")
    void update_deberiaLanzarExcepcion_cuandoPersonaNoExiste() {
        when(personRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personService.update(99L, requestDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(personRepository, never()).save(any());
    }


    @Test
    @DisplayName("delete: debería retornar true cuando la persona existe y se elimina")
    void delete_deberiaRetornarTrue_cuandoPersonaExiste() {
        when(personRepository.existsById(1L)).thenReturn(true);

        boolean result = personService.delete(1L);

        assertThat(result).isTrue();
        verify(personRepository, times(1)).deleteById(1L);
    }


    @Test
    @DisplayName("delete: debería retornar false cuando la persona no existe")
    void delete_deberiaRetornarFalse_cuandoPersonaNoExiste() {
        when(personRepository.existsById(99L)).thenReturn(false);

        boolean result = personService.delete(99L);

        assertThat(result).isFalse();
        verify(personRepository, never()).deleteById(any());
    }
}
