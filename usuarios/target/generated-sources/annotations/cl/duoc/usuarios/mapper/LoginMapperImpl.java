package cl.duoc.usuarios.mapper;

import cl.duoc.usuarios.dto.request.LoginRequestDto;
import cl.duoc.usuarios.dto.response.LoginAllResponseDto;
import cl.duoc.usuarios.dto.response.LoginResponseDto;
import cl.duoc.usuarios.model.Login;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-12T14:41:51-0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26 (Oracle Corporation)"
)
@Component
public class LoginMapperImpl implements LoginMapper {

    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private RolMapper rolMapper;

    @Override
    public Login toEntity(LoginRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Login login = new Login();

        login.setUsername( dto.getUsername() );
        login.setPassword( dto.getPassword() );

        return login;
    }

    @Override
    public LoginAllResponseDto toDtoInfo(Login login) {
        if ( login == null ) {
            return null;
        }

        LoginAllResponseDto loginAllResponseDto = new LoginAllResponseDto();

        loginAllResponseDto.setId( login.getId() );
        loginAllResponseDto.setUsername( login.getUsername() );
        loginAllResponseDto.setPerson( personMapper.toDto( login.getPerson() ) );
        loginAllResponseDto.setRol( rolMapper.toDto( login.getRol() ) );

        return loginAllResponseDto;
    }

    @Override
    public LoginResponseDto toDto(Login login) {
        if ( login == null ) {
            return null;
        }

        LoginResponseDto loginResponseDto = new LoginResponseDto();

        loginResponseDto.setId( login.getId() );
        loginResponseDto.setUsername( login.getUsername() );
        loginResponseDto.setRol( rolMapper.toDto( login.getRol() ) );

        return loginResponseDto;
    }

    @Override
    public List<LoginAllResponseDto> toDtoList(List<Login> logins) {
        if ( logins == null ) {
            return null;
        }

        List<LoginAllResponseDto> list = new ArrayList<LoginAllResponseDto>( logins.size() );
        for ( Login login : logins ) {
            list.add( toDtoInfo( login ) );
        }

        return list;
    }
}
