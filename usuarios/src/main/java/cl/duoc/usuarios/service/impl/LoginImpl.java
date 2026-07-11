package cl.duoc.usuarios.service.impl;

import cl.duoc.usuarios.dto.request.LoginRequestDto;
import cl.duoc.usuarios.dto.response.LoginAllResponseDto;
import cl.duoc.usuarios.dto.response.LoginResponseDto;
import cl.duoc.usuarios.mapper.LoginMapper;
import cl.duoc.usuarios.model.Login;
import cl.duoc.usuarios.model.Person;
import cl.duoc.usuarios.model.Rol;
import cl.duoc.usuarios.repository.LoginRepository;
import cl.duoc.usuarios.repository.PersonRepository;
import cl.duoc.usuarios.repository.RolRepository;
import cl.duoc.usuarios.service.LoginService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginImpl implements LoginService {

    private final LoginRepository loginRepository;
    private final PersonRepository personRepository;
    private final RolRepository rolRepository;
    private final LoginMapper loginMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public LoginAllResponseDto getByIdInfo(Long id) {
        Login login = loginRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Login no encontrado con id: " + id));

        return loginMapper.toDtoInfo(login);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto getById(Long id) {
        Login login = loginRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Login no encontrado con id: " + id));

        return loginMapper.toDto(login);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginAllResponseDto> getAll() {
        return loginMapper.toDtoList(loginRepository.findAll());
    }

    @Override
    @Transactional
    public LoginAllResponseDto create(LoginRequestDto dto) {
        if (loginRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            throw new IllegalArgumentException("Ya existe un login con el username: " + dto.getUsername());
        }
        if (loginRepository.existsByPersonId(dto.getPersonId())) {
            throw new IllegalArgumentException("La persona ya tiene un login asociado");
        }

        Person person = findPersonById(dto.getPersonId());
        Rol rol = findRolById(dto.getRolId());
        Login login = loginMapper.toEntity(dto);
        login.setPerson(person);
        login.setRol(rol);
        login.setPassword(passwordEncoder.encode(dto.getPassword()));

        return loginMapper.toDtoInfo(loginRepository.save(login));
    }

    @Override
    @Transactional
    public LoginAllResponseDto update(Long id, LoginRequestDto dto) {
        Login login = loginRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Login no encontrado con id: " + id));

        if (loginRepository.existsByUsernameIgnoreCaseAndIdNot(dto.getUsername(), id)) {
            throw new IllegalArgumentException("Ya existe un login con el username: " + dto.getUsername());
        }

        if (loginRepository.existsByPersonIdAndIdNot(dto.getPersonId(), id)) {
            throw new IllegalArgumentException("La persona ya tiene un login asociado");
        }

        Person person = findPersonById(dto.getPersonId());
        Rol rol = findRolById(dto.getRolId());

        login.setUsername(dto.getUsername());
        login.setPassword(passwordEncoder.encode(dto.getPassword()));
        login.setPerson(person);
        login.setRol(rol);

        return loginMapper.toDtoInfo(loginRepository.save(login));
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!loginRepository.existsById(id)) {
            return false;
        }

        loginRepository.deleteById(id);
        return true;
    }


    @Override
    @Transactional(readOnly = true)
    public boolean esPropietario(Long loginId, Long personIdToken) {
        if (personIdToken == null) {
            return false;
        }

        Long personIdDelLogin = loginRepository.findPersonIdByLoginId(loginId).orElse(null);

        return personIdDelLogin != null && personIdDelLogin.equals(personIdToken);
    }


    private Person findPersonById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada con id: " + personId));
    }

    private Rol findRolById(Long rolId){
        return rolRepository.findById(rolId)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrada con id: " + rolId));
    }

}
