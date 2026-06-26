package cl.duoc.usuarios.mapper;

import cl.duoc.usuarios.dto.response.RolResponseDto;
import cl.duoc.usuarios.model.Rol;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RolMapper {

    RolResponseDto toDto(Rol rol);

    List<RolResponseDto> toDtoList(List<Rol> roles);
}
