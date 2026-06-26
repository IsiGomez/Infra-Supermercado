package cl.supermercado.seguimiento.service.impl;

import cl.supermercado.seguimiento.dto.request.SeguimientoRequestDto;
import cl.supermercado.seguimiento.dto.response.SeguimientoResponseDto;
import cl.supermercado.seguimiento.mapper.SeguimientoMapper;
import cl.supermercado.seguimiento.model.Seguimiento;
import cl.supermercado.seguimiento.repository.SeguimientoRepository;
import cl.supermercado.seguimiento.service.SeguimientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeguimientoServiceImpl implements SeguimientoService {

    private final SeguimientoRepository repository;
    private final SeguimientoMapper mapper;


    @Override
    @Transactional
    public SeguimientoResponseDto registrarSeguimiento(SeguimientoRequestDto request) {
        log.info("Registrando seguimiento: compra={}, usuario={}, estado={}",
                request.getCompraId(), request.getUsuarioId(), request.getEstado());
        Seguimiento s = mapper.toEntity(request);

        repository.save(s);
        return mapper.toDto(s);
    }


    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponseDto> listarSeguimientos() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponseDto> historialPorCompra(Long compraId) {
        return repository.findByCompraId(compraId).stream().map(mapper::toDto).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponseDto> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(mapper::toDto).toList();
    }

}
