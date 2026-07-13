package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.request.AuthRequestDto;
import cl.duoc.usuarios.dto.response.AuthResponseDto;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto request);

}
