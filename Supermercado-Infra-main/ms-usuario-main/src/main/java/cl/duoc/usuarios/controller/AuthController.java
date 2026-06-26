package cl.duoc.usuarios.controller;

import cl.duoc.usuarios.dto.request.AuthRequestDto;
import cl.duoc.usuarios.dto.response.AuthResponseDto;
import cl.duoc.usuarios.dto.response.ExceptionDto;
import cl.duoc.usuarios.model.Login;
import cl.duoc.usuarios.repository.LoginRepository;
import cl.duoc.usuarios.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Módulo de Autenticación", description = "Operación para obtener token de autenticacion")
public class AuthController {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Operation(summary = "Autenticarse y obtener JWT",
               description = "De ingresar correctamente el login, se devuelve un token de autenticación.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticacion exitosa",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos - Error de validación",
                    content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDto req) {
        Login login = loginRepository.findByUsernameIgnoreCase(req.getUsername())
                .orElse(null);

        if (login == null || !passwordEncoder.matches(req.getPassword(), login.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Credenciales incorrectas"));
        }

        String token = jwtService.generateToken(login);
        String rolName = login.getRol() != null ? login.getRol().getName() : "CLIENTE";

        return ResponseEntity.ok(new AuthResponseDto(token, login.getUsername(), rolName));
    }

}
