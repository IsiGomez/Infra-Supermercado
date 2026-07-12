package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.request.AuthRequestDto;
import cl.duoc.usuarios.dto.response.AuthResponseDto;
import cl.duoc.usuarios.model.Login;
import cl.duoc.usuarios.model.Rol;
import cl.duoc.usuarios.repository.LoginRepository;
import cl.duoc.usuarios.service.impl.AuthImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - AuthImpl")
public class AuthImplTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthImpl authService;

    private AuthRequestDto validRequest;
    private Login mockLogin;


    @BeforeEach
    void setUp() {
        validRequest = new AuthRequestDto("FunClaudia", "pass123");

        Rol rol = new Rol(1L, "FUNCIONARIO", "Rol de funcionario");
        mockLogin = new Login();
        mockLogin.setId(1L);
        mockLogin.setUsername("FunClaudia");
        mockLogin.setPassword("hashedPassword");
        mockLogin.setRol(rol);
    }


    @Test
    @DisplayName("login: debería autenticar correctamente y retornar el token")
    void login_deberiaRetornarToken_cuandoCredencialesSonCorrectas() {
        when(loginRepository.findByUsernameIgnoreCase("FunClaudia")).thenReturn(Optional.of(mockLogin));
        when(passwordEncoder.matches("pass123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(mockLogin)).thenReturn("mocked-jwt-token");

        AuthResponseDto response = authService.login(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked-jwt-token");
        assertThat(response.getUsername()).isEqualTo("FunClaudia");
        assertThat(response.getRol()).isEqualTo("FUNCIONARIO");

        verify(loginRepository, times(1)).findByUsernameIgnoreCase("FunClaudia");
        verify(passwordEncoder, times(1)).matches("pass123", "hashedPassword");
        verify(jwtService, times(1)).generateToken(mockLogin);
    }


    @Test
    @DisplayName("login: debería usar el rol CLIENTE por defecto si el usuario no tiene rol")
    void login_deberiaRetornarClientePorDefecto_cuandoUsuarioNoTieneRol() {
        mockLogin.setRol(null);
        when(loginRepository.findByUsernameIgnoreCase("FunClaudia")).thenReturn(Optional.of(mockLogin));
        when(passwordEncoder.matches("pass123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(mockLogin)).thenReturn("mocked-jwt-token");

        AuthResponseDto response = authService.login(validRequest);

        assertThat(response.getRol()).isEqualTo("CLIENTE");
    }


    @Test
    @DisplayName("login: debería lanzar BadCredentialsException si el username no existe")
    void login_deberiaLanzarExcepcion_cuandoUsernameNoExiste() {
        when(loginRepository.findByUsernameIgnoreCase("FunClaudia")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(validRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Credenciales incorrectas");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }


    @Test
    @DisplayName("login: debería lanzar BadCredentialsException si la contraseña no coincide")
    void login_deberiaLanzarExcepcion_cuandoContrasenaEsIncorrecta() {
        when(loginRepository.findByUsernameIgnoreCase("FunClaudia")).thenReturn(Optional.of(mockLogin));
        when(passwordEncoder.matches("pass123", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(validRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Credenciales incorrectas");

        verify(jwtService, never()).generateToken(any());
    }

}
