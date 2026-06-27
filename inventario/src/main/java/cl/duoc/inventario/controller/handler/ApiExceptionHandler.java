package cl.duoc.inventario.controller.handler;

import cl.duoc.inventario.dto.response.ExceptionDto;
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

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ExceptionDto> handleRetryableException(RetryableException ex) {
        ExceptionDto error = new ExceptionDto(
                "Error comunicando con otra API",
                "Una de las APIs necesarias no esta disponible."
        );

        log.error(ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleException(Exception ex) {
        ExceptionDto error = new ExceptionDto("Ocurrio un error", ex.getMessage());

        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(error);
    }

}
