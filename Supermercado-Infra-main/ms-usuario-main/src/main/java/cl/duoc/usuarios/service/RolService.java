package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.response.RolResponseDto;
import java.util.List;

public interface RolService {

    RolResponseDto getById(Long id);
    List<RolResponseDto> getAll();

}
