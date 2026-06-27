package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.request.LoginRequestDto;
import cl.duoc.usuarios.dto.response.LoginAllResponseDto;
import cl.duoc.usuarios.dto.response.LoginResponseDto;
import java.util.List;

public interface LoginService {

    LoginResponseDto getById(Long id);
    LoginAllResponseDto getByIdInfo(Long id);
    List<LoginAllResponseDto> getAll();

    LoginAllResponseDto create(LoginRequestDto dto);
    LoginAllResponseDto update(Long id, LoginRequestDto dto);
    boolean delete(Long id);
}
