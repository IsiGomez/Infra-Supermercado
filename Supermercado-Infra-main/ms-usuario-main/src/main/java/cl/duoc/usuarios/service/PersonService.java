package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.request.PersonRequestDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import java.util.List;

public interface PersonService {

    PersonResponseDto getById(Long id);
    List<PersonResponseDto> getAll();

    PersonResponseDto create(PersonRequestDto dto);
    PersonResponseDto update(Long id, PersonRequestDto dto);
    boolean delete(Long id);

}
