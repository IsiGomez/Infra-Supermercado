package cl.duoc.usuarios.mapper;

import cl.duoc.usuarios.dto.response.RolResponseDto;
import cl.duoc.usuarios.model.Rol;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-12T14:41:51-0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26 (Oracle Corporation)"
)
@Component
public class RolMapperImpl implements RolMapper {

    @Override
    public RolResponseDto toDto(Rol rol) {
        if ( rol == null ) {
            return null;
        }

        RolResponseDto rolResponseDto = new RolResponseDto();

        rolResponseDto.setId( rol.getId() );
        rolResponseDto.setName( rol.getName() );
        rolResponseDto.setDescription( rol.getDescription() );

        return rolResponseDto;
    }

    @Override
    public List<RolResponseDto> toDtoList(List<Rol> roles) {
        if ( roles == null ) {
            return null;
        }

        List<RolResponseDto> list = new ArrayList<RolResponseDto>( roles.size() );
        for ( Rol rol : roles ) {
            list.add( toDto( rol ) );
        }

        return list;
    }
}
