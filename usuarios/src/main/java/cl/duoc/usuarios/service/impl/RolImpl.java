package cl.duoc.usuarios.service.impl;

import cl.duoc.usuarios.dto.response.RolResponseDto;
import cl.duoc.usuarios.mapper.RolMapper;
import cl.duoc.usuarios.model.Rol;
import cl.duoc.usuarios.repository.RolRepository;
import cl.duoc.usuarios.service.RolService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RolImpl implements RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    @Override
    @Transactional(readOnly = true)
    public RolResponseDto getById(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + id));

        return rolMapper.toDto(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponseDto> getAll() {
        return rolMapper.toDtoList(rolRepository.findAll());
    }

}
