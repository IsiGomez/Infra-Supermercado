package cl.duoc.usuarios.mapper;

import cl.duoc.usuarios.dto.request.PersonRequestDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import cl.duoc.usuarios.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    Person toEntity(PersonRequestDto dto);

    PersonResponseDto toDto(Person person);

    List<PersonResponseDto> toDtoList(List<Person> persons);
}
