package cl.duoc.usuarios.service.impl;

import cl.duoc.usuarios.dto.request.PersonRequestDto;
import cl.duoc.usuarios.dto.response.PersonResponseDto;
import cl.duoc.usuarios.mapper.PersonMapper;
import cl.duoc.usuarios.model.Person;
import cl.duoc.usuarios.model.Rol;
import cl.duoc.usuarios.repository.PersonRepository;
import cl.duoc.usuarios.repository.RolRepository;
import cl.duoc.usuarios.service.PersonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Override
    @Transactional(readOnly = true)
    public PersonResponseDto getById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada con id: " + id));

        return personMapper.toDto(person);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonResponseDto> getAll() {
        return personMapper.toDtoList(personRepository.findAll());
    }

    @Override
    public PersonResponseDto create(PersonRequestDto dto) {
        if (personRepository.existsByRutIgnoreCase(dto.getRut())) {
            throw new IllegalArgumentException("Ya existe una persona con el rut: " + dto.getRut());
        }
        if (personRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe una persona con el email: " + dto.getEmail());
        }

        return personMapper.toDto(personRepository.save(personMapper.toEntity(dto)));
    }

    @Override
    public PersonResponseDto update(Long id, PersonRequestDto dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada con id: " + id));

        if (personRepository.existsByRutIgnoreCaseAndIdNot(dto.getRut(), id)) {
            throw new IllegalArgumentException("Ya existe una persona con el rut: " + dto.getRut());
        }
        if (personRepository.existsByEmailIgnoreCaseAndIdNot(dto.getEmail(), id)) {
            throw new IllegalArgumentException("Ya existe una persona con el email: " + dto.getEmail());
        }

        person.setRut(dto.getRut());
        person.setName(dto.getName());
        person.setLastName(dto.getLastName());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());

        return personMapper.toDto(personRepository.save(person));
    }

    @Override
    public boolean delete(Long id) {
        if (!personRepository.existsById(id)) {
            return false;
        }

        personRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean esPropietario(Long personId, Long personIdToken) {
        return personId != null && personId.equals(personIdToken);
    }

}
