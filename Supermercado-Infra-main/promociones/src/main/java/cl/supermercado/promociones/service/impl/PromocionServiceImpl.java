package cl.supermercado.promociones.service.impl;

import cl.supermercado.promociones.dto.request.PromocionRequestDto;
import cl.supermercado.promociones.dto.response.PromocionResponseDto;
import cl.supermercado.promociones.mapper.PromocionMapper;
import cl.supermercado.promociones.model.Promocion;
import cl.supermercado.promociones.repository.PromocionRepository;
import cl.supermercado.promociones.service.PromocionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromocionServiceImpl implements PromocionService {

    private final PromocionRepository repository;
    private final PromocionMapper mapper;


    @Override
    @Transactional
    public PromocionResponseDto crearPromocion(PromocionRequestDto request) {
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la fecha inicio");
        }
        Promocion p = mapper.toEntity(request);

        repository.save(p);
        log.info("Promoción '{}' creada", p.getCodigo());
        return mapper.toDto(p);
    }


    @Override
    @Transactional(readOnly = true)
    public List<PromocionResponseDto> listarPromociones() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public PromocionResponseDto obtenerPorCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada"));
    }


    @Override
    public List<PromocionResponseDto> listarVigentes() {
        LocalDate hoy = LocalDate.now();
        return repository.findAll().stream()
                .filter(p -> !hoy.isBefore(p.getFechaInicio()) && !hoy.isAfter(p.getFechaFin()))
                .map(mapper::toDto)
                .toList();
    }

}
