package cl.duoc.usuarios.mapper;

import cl.duoc.usuarios.dto.request.PersonRequestDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import cl.duoc.usuarios.model.Person;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-23T14:44:50-0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26 (Oracle Corporation)"
)
@Component
public class PersonMapperImpl implements PersonMapper {

    @Override
    public Person toEntity(PersonRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Person person = new Person();

        person.setRut( dto.getRut() );
        person.setName( dto.getName() );
        person.setLastName( dto.getLastName() );
        person.setEmail( dto.getEmail() );
        person.setPhone( dto.getPhone() );

        return person;
    }

    @Override
    public PersonResponseDto toDto(Person person) {
        if ( person == null ) {
            return null;
        }

        PersonResponseDto personResponseDto = new PersonResponseDto();

        personResponseDto.setId( person.getId() );
        personResponseDto.setRut( person.getRut() );
        personResponseDto.setName( person.getName() );
        personResponseDto.setLastName( person.getLastName() );
        personResponseDto.setEmail( person.getEmail() );
        personResponseDto.setPhone( person.getPhone() );

        return personResponseDto;
    }

    @Override
    public List<PersonResponseDto> toDtoList(List<Person> persons) {
        if ( persons == null ) {
            return null;
        }

        List<PersonResponseDto> list = new ArrayList<PersonResponseDto>( persons.size() );
        for ( Person person : persons ) {
            list.add( toDto( person ) );
        }

        return list;
    }
}
