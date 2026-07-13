package cl.supermercado.compra.service.impl;

import cl.supermercado.compra.dto.remote.CartDto;
import cl.supermercado.compra.dto.remote.ItemCompradoDto;
import cl.supermercado.compra.dto.request.CompraRequestDto;
import cl.supermercado.compra.dto.response.CompraResponseDto;
import cl.supermercado.compra.event.CompraCompletadaEvent;
import cl.supermercado.compra.mapper.CompraMapper;
import cl.supermercado.compra.model.Compra;
import cl.supermercado.compra.repository.CompraRepository;
import cl.supermercado.compra.service.CompraService;
import cl.supermercado.compra.service.api.CarritoClient;
import cl.supermercado.compra.service.api.PagoClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompraServiceImpl implements CompraService {

    private final CompraRepository repository;
    private final CompraMapper mapper;
    private final CarritoClient carritoClient;
    private final PagoClient pagoClient;
    private final KafkaTemplate<String, CompraCompletadaEvent> kafkaTemplate;


    @Override
    @Transactional
    public CompraResponseDto crearCompra(CompraRequestDto request) {

        Boolean pagoExitoso = pagoClient.tieneUltimoPagoExitoso(request.getUsuarioId());
        if (!Boolean.TRUE.equals(pagoExitoso)) {
            throw new IllegalArgumentException(
                    "El usuario " + request.getUsuarioId() + " no tiene un pago exitoso registrado.");
        }
        log.info("Pago exitoso confirmado para usuario {}", request.getUsuarioId());

        CartDto carrito = carritoClient.obtenerCarrito(request.getUsuarioId());
        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "El carrito está vacío para el usuario " + request.getUsuarioId());
        }

        Double totalCarrito = carrito.getTotal().doubleValue();
        log.info("Carrito obtenido: {} items, total {}", carrito.getItems().size(), totalCarrito);

        List<ItemCompradoDto> items = carrito.getItems().stream()
                .map(item -> new ItemCompradoDto(item.getProductId(), item.getQuantity()))
                .toList();

        Compra compra = new Compra();
        compra.setUsuarioId(request.getUsuarioId());
        compra.setTotal(totalCarrito);
        compra.setFechaCompra(LocalDateTime.now());
        compra.setFinalizada(true);
        compra.setPagoConfirmado(true);
        repository.save(compra);
        log.info("Compra {} creada para usuario {}, total: {}", compra.getId(), request.getUsuarioId(), totalCarrito);

        carritoClient.limpiarCarrito(request.getUsuarioId());
        log.info("Carrito del usuario {} limpiado", request.getUsuarioId());

        CompraCompletadaEvent evento = new CompraCompletadaEvent(
                compra.getId(),
                compra.getUsuarioId(),
                compra.getTotal(),
                compra.getFechaCompra(),
                items
        );
        kafkaTemplate.send("compra-completada", String.valueOf(compra.getUsuarioId()), evento)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Fallo al enviar evento 'compra-completada' para compra {}: {}",
                                compra.getId(), ex.getMessage(), ex);
                    } else {
                        log.info("Evento 'compra-completada' confirmado por Kafka para compra {} (partition={}, offset={})",
                                compra.getId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });;
        log.info("Evento 'compra-completada' enviado para compra {}", compra.getId());

        return mapper.toDto(compra);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CompraResponseDto> listarCompras() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<CompraResponseDto> listarComprasPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(mapper::toDto).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public CompraResponseDto obtenerPorId(Long id) {
        Compra compra = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe una compra con id " + id));
        return mapper.toDto(compra);
    }

}