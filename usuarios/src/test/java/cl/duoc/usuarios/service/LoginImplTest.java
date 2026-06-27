package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.request.LoginRequestDto;
import cl.duoc.usuarios.dto.response.LoginAllResponseDto;
import cl.duoc.usuarios.dto.response.LoginResponseDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import cl.duoc.usuarios.dto.response.RolResponseDto;
import cl.duoc.usuarios.mapper.LoginMapper;
import cl.duoc.usuarios.model.Login;
import cl.duoc.usuarios.model.Person;
import cl.duoc.usuarios.model.Rol;
import cl.duoc.usuarios.repository.LoginRepository;
import cl.duoc.usuarios.repository.PersonRepository;
import cl.duoc.usuarios.repository.RolRepository;
import cl.duoc.usuarios.service.impl.LoginImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - LoginImpl")
public class LoginImplTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private LoginMapper loginMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginImpl service;

    private Login login;
    private LoginRequestDto requestDto;
    private LoginAllResponseDto allResponseDto;
    private LoginResponseDto responseDto;
    private Person person;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol(2L, "CLIENTE", "Rol de cliente");
        person = new Person();
        person.setId(1L);

        login = new Login();
        login.setId(1L);
        login.setUsername("cliente01");
        login.setPassword("hashedPassword");
        login.setPerson(person);
        login.setRol(rol);

        requestDto = new LoginRequestDto("cliente01", "pass123", 1L, 2L);

        RolResponseDto rolDto = new RolResponseDto(2L, "CLIENTE", "Rol de cliente");
        PersonResponseDto personDto = new PersonResponseDto();
        allResponseDto = new LoginAllResponseDto(1L, "cliente01", personDto, rolDto);
        responseDto = new LoginResponseDto(1L, "cliente01", rolDto);
    }

    @Test
    @DisplayName("getById: debe retornar el LoginResponseDto cuando el id existe")
    void getById_deberiaRetornarDto_cuandoExiste() {
        when(loginRepository.findById(1L)).thenReturn(Optional.of(login));
        when(loginMapper.toDto(login)).thenReturn(responseDto);

        LoginResponseDto resultado = service.getById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("cliente01");
        verify(loginRepository, times(1)).findById(1L);
    }


    @Test
    @DisplayName("getById: debe lanzar una excepción si el id no existe")
    void getById_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(loginRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }


    @Test
    @DisplayName("getAll: debe retornar la lista de todos los logins")
    void getAll_deberiaRetornarLista() {
        when(loginRepository.findAll()).thenReturn(List.of(login));
        when(loginMapper.toDtoList(List.of(login))).thenReturn(List.of(allResponseDto));

        List<LoginAllResponseDto> resultado = service.getAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsername()).isEqualTo("cliente01");
    }


    @Test
    @DisplayName("create: debe crear el login cuando username y persona son únicos")
    void create_deberiaCrearLogin_cuandoDatosSonUnicos() {
        when(loginRepository.existsByUsernameIgnoreCase("cliente01")).thenReturn(false);
        when(loginRepository.existsByPersonId(1L)).thenReturn(false);
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rol));

        when(loginMapper.toEntity(requestDto)).thenReturn(login);
        when(passwordEncoder.encode("pass123")).thenReturn("hashedPassword");
        when(loginRepository.save(any(Login.class))).thenReturn(login);
        when(loginMapper.toDtoInfo(login)).thenReturn(allResponseDto);

        LoginAllResponseDto resultado = service.create(requestDto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("cliente01");

        verify(passwordEncoder, times(1)).encode("pass123");
        verify(loginRepository, times(1)).save(any(Login.class));
    }


    @Test
    @DisplayName("create: debe lanzar excepción si el username ya existe")
    void create_deberiaLanzarExcepcion_cuandoUsernameYaExiste() {
        when(loginRepository.existsByUsernameIgnoreCase("cliente01")).thenReturn(true);

        assertThatThrownBy(() -> service.create(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cliente01");

        verify(loginRepository, never()).save(any());
    }


    @Test
    @DisplayName("create: debe lanzar excepción si la persona ya tiene un login")
    void create_deberiaLanzarExcepcion_cuandoPersonaYaTieneLogin() {
        when(loginRepository.existsByUsernameIgnoreCase("cliente01")).thenReturn(false);
        when(loginRepository.existsByPersonId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.create(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La persona ya tiene un login asociado");

        verify(loginRepository, never()).save(any());
    }


    @Test
    @DisplayName("delete: debe retornar true cuando el login existe y se elimina")
    void delete_deberiaRetornarTrue_cuandoExiste() {
        when(loginRepository.existsById(1L)).thenReturn(true);

        boolean resultado = service.delete(1L);

        assertThat(resultado).isTrue();
        verify(loginRepository, times(1)).deleteById(1L);
    }


    @Test
    @DisplayName("delete: debe retornar false cuando el login no existe")
    void delete_deberiaRetornarFalse_cuandoNoExiste() {
        when(loginRepository.existsById(99L)).thenReturn(false);

        boolean resultado = service.delete(99L);

        assertThat(resultado).isFalse();
        verify(loginRepository, never()).deleteById(any());
    }
}
