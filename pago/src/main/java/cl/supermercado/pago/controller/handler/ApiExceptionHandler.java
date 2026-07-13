package cl.supermercado.pago.controller.handler;
import cl.supermercado.pago.dto.response.ExceptionDto;
import cl.supermercado.pago.exception.RecursoRemotoNoEncontradoException;
import cl.supermercado.pago.exception.ServicioRemotoException;
import feign.RetryableException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ExceptionDto>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ExceptionDto> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(new ExceptionDto(error.getField(), error.getDefaultMessage()))
        );

        log.error("Error de validacion: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleEntityNotFoundException(EntityNotFoundException ex) {
        ExceptionDto error = new ExceptionDto("Recurso no encontrado", ex.getMessage());

        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ExceptionDto error = new ExceptionDto("Solicitud invalida", ex.getMessage());

        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleException(Exception ex) {
        ExceptionDto error = new ExceptionDto("Ocurrio un error", ex.getMessage());

        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ExceptionDto> handleRetryableException(RetryableException ex) {
        ExceptionDto error = new ExceptionDto(
                "Error comunicando con otra API",
                "Una de las APIs necesarias no esta disponible. Intenta nuevamente mas tarde."
        );

        log.error(ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(RecursoRemotoNoEncontradoException.class)
    public ResponseEntity<ExceptionDto> handleRecursoRemotoNoEncontradoException(RecursoRemotoNoEncontradoException ex) {
        ExceptionDto error = new ExceptionDto("Recurso no encontrado en servicio externo", ex.getMessage());

        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ServicioRemotoException.class)
    public ResponseEntity<ExceptionDto> handleServicioRemotoException(ServicioRemotoException ex) {
        ExceptionDto error = new ExceptionDto("Error de comunicacion con servicio externo", ex.getMessage());
        HttpStatus status = ex.getStatus() >= 500 ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.BAD_GATEWAY;

        log.error(ex.getMessage());
        return ResponseEntity.status(status).body(error);
    }

}