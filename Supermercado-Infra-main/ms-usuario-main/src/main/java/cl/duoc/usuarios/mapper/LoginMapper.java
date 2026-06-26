package cl.duoc.usuarios.mapper;

import cl.duoc.usuarios.dto.request.LoginRequestDto;
import cl.duoc.usuarios.dto.response.LoginAllResponseDto;
import cl.duoc.usuarios.dto.response.LoginResponseDto;
import cl.duoc.usuarios.model.Login;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PersonMapper.class, RolMapper.class})
public interface LoginMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "rol", ignore = true)
    Login toEntity(LoginRequestDto dto);

    LoginAllResponseDto toDtoInfo(Login login);

    LoginResponseDto toDto(Login login);

    List<LoginAllResponseDto> toDtoList(List<Login> logins);
}
