package cl.supermercado.puntos.service.impl;

import cl.supermercado.puntos.dto.request.PuntosRequestDto;
import cl.supermercado.puntos.dto.response.CanjeConfirmacionResponseDto;
import cl.supermercado.puntos.dto.response.CanjeSimulacionResponseDto;
import cl.supermercado.puntos.dto.response.PuntosResponseDto;
import cl.supermercado.puntos.mapper.PuntosMapper;
import cl.supermercado.puntos.model.Puntos;
import cl.supermercado.puntos.model.PuntosHistorial;
import cl.supermercado.puntos.repository.PuntosHistorialRepository;
import cl.supermercado.puntos.repository.PuntosRepository;
import cl.supermercado.puntos.service.PuntosService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PuntosServiceImpl implements PuntosService {

    private final PuntosRepository repository;
    private final PuntosHistorialRepository historialRepository;
    private final PuntosMapper mapper;

    private static final int PUNTOS_POR_PESO = 10;
    private static final int MINIMO_CANJEABLE = PUNTOS_POR_PESO;

    @Override
    @Transactional
    public PuntosResponseDto asignarPuntos(PuntosRequestDto request) {

        if (historialRepository.existsByCompraId(request.getCompraId())) {
            throw new IllegalArgumentException(
                    "Ya se asignaron puntos por la compra " + request.getCompraId()
            );
        }

        double monto = request.getTotal() != null ? request.getTotal() : 0.0;
        int puntos = (int) (monto / 100);

        Puntos entity = repository.findByUsuarioId(request.getUsuarioId())
                .orElse(new Puntos(null, request.getUsuarioId(), 0));

        entity.setPuntosAcumulados(entity.getPuntosAcumulados() + puntos);
        repository.save(entity);

        historialRepository.save(
                new PuntosHistorial(null, request.getUsuarioId(), request.getCompraId(), puntos, "ACUMULACION")
        );

        log.info("Puntos asignados: {} puntos al usuario {} por compra {} (monto: ${})",
                puntos, request.getUsuarioId(), request.getCompraId(), monto);

        return mapper.toDto(entity);
    }


    @Override
    @Transactional(readOnly = true)
    public PuntosResponseDto consultarPuntos(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Usuario sin puntos registrados"));
    }


    @Override
    @Transactional(readOnly = true)
    public CanjeSimulacionResponseDto simularCanje(Long usuarioId) {
        int puntosDisponibles = obtenerPuntosActuales(usuarioId);

        int puntosCanjeables = (puntosDisponibles / PUNTOS_POR_PESO) * PUNTOS_POR_PESO;
        int montoDescuento = puntosCanjeables / PUNTOS_POR_PESO;
        boolean puedeCanjear = puntosCanjeables >= MINIMO_CANJEABLE;

        String mensaje = puedeCanjear
                ? String.format("Tienes %d puntos. Puedes canjear %d por $%d de descuento.",
                        puntosDisponibles, puntosCanjeables, montoDescuento)
                : String.format("Tienes %d puntos. Necesitas al menos %d para canjear $1 de descuento.",
                        puntosDisponibles, MINIMO_CANJEABLE);

        return new CanjeSimulacionResponseDto(
                puntosDisponibles, puntosCanjeables, montoDescuento, puedeCanjear, mensaje
        );
    }


    @Override
    @Transactional
    public CanjeConfirmacionResponseDto confirmarCanje(Long usuarioId) {
        Puntos entity = repository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario sin puntos registrados"));

        int puntosDisponibles = entity.getPuntosAcumulados();
        int puntosCanjeables = (puntosDisponibles / PUNTOS_POR_PESO) * PUNTOS_POR_PESO;

        if (puntosCanjeables < MINIMO_CANJEABLE) {
            throw new IllegalArgumentException(
                    "No tienes suficientes puntos para canjear. Tienes " + puntosDisponibles +
                    ", necesitas al menos " + MINIMO_CANJEABLE);
        }

        int montoDescuento = puntosCanjeables / PUNTOS_POR_PESO;

        entity.setPuntosAcumulados(puntosDisponibles - puntosCanjeables);
        repository.save(entity);

        historialRepository.save(
                new PuntosHistorial(null, usuarioId, null, -puntosCanjeables, "CANJE")
        );

        log.info("Canje confirmado: usuario {} canjeó {} puntos por ${} de descuento. Puntos restantes: {}",
                usuarioId, puntosCanjeables, montoDescuento, entity.getPuntosAcumulados());

        return new CanjeConfirmacionResponseDto(
                puntosCanjeables, montoDescuento, entity.getPuntosAcumulados()
        );
    }


    private int obtenerPuntosActuales(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId)
                .map(Puntos::getPuntosAcumulados)
                .orElse(0);
    }

}
