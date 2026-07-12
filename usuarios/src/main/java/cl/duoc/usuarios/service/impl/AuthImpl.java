package cl.duoc.usuarios.service.impl;

import cl.duoc.usuarios.dto.request.AuthRequestDto;
import cl.duoc.usuarios.dto.response.AuthResponseDto;
import cl.duoc.usuarios.model.Login;
import cl.duoc.usuarios.repository.LoginRepository;
import cl.duoc.usuarios.service.AuthService;
import cl.duoc.usuarios.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthImpl implements AuthService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(AuthRequestDto request) {
        Login login = loginRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.getPassword(), login.getPassword())) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(login);
        String rolName = login.getRol() != null ? login.getRol().getName() : "CLIENTE";

        log.info("Login exitoso para usuario {}", login.getUsername());
        return new AuthResponseDto(token, login.getUsername(), rolName);
    }

}
