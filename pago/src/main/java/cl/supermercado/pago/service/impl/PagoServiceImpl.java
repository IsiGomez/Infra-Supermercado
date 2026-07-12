package cl.supermercado.pago.service.impl;

import cl.supermercado.pago.dto.request.PagoRequestDto;
import cl.supermercado.pago.dto.response.PagoResponseDto;
import cl.supermercado.pago.mapper.PagoMapper;
import cl.supermercado.pago.model.Pago;
import cl.supermercado.pago.repository.PagoRepository;
import cl.supermercado.pago.service.PagoService;
import cl.supermercado.pago.service.api.CarritoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoServiceImpl implements PagoService {

    private final PagoRepository repository;
    private final PagoMapper mapper;
    private final CarritoClient carritoClient;


    @Override
    @Transactional(readOnly = true)
    public Boolean tieneUltimoPagoExitoso(Long usuarioId) {
        return repository.findTopByUsuarioIdAndExitosoTrueOrderByFechaPagoDesc(usuarioId)
                .isPresent();
    }


    @Override
    @Transactional(readOnly = true)
    public PagoResponseDto obtenerUltimoPagoExitoso(Long usuarioId) {
        Pago pago = repository.findTopByUsuarioIdAndExitosoTrueOrderByFechaPagoDesc(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un pago exitoso para el usuario " + usuarioId));

        return mapper.toDto(pago);
    }


    @Override
    @Transactional
    public PagoResponseDto procesarPago(PagoRequestDto request) {

        Double montoCarrito;
        try {
            montoCarrito = carritoClient.obtenerTotalCarrito(request.getUsuarioId());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "No se pudo obtener el carrito del usuario ID: " + request.getUsuarioId());
        }

        if (montoCarrito == null || montoCarrito < 0.01) {
            throw new IllegalArgumentException("El carrito del usuario está vacío");
        }

        Pago pago = new Pago();
        pago.setUsuarioId(request.getUsuarioId());
        pago.setMonto(montoCarrito);
        pago.setMetodo(request.getMetodo().toUpperCase());
        pago.setFechaPago(LocalDateTime.now());

        switch (request.getMetodo().toUpperCase()) {
            case "TARJETA" -> pago.setExitoso(true);
            case "CREDITO" -> pago.setExitoso(true);
            case "EFECTIVO" -> pago.setExitoso(true);
            default -> throw new IllegalArgumentException("Método de pago ingresado es inválido");
        }

        repository.save(pago);
        log.info("Pago procesado para usuario {} por ${}", request.getUsuarioId(), montoCarrito);
        return mapper.toDto(pago);
    }


    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDto> listarPagos() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public PagoResponseDto obtenerPorId(Long id) {
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "No existe un pago con id " + id));
        return mapper.toDto(pago);
    }

}
