package cl.supermercado.compra.exception;

public class RecursoRemotoNoEncontradoException extends RuntimeException {

    public RecursoRemotoNoEncontradoException(String servicio, String detalle) {
        super("El servicio '" + servicio + "' no encontro el recurso solicitado: " + detalle);
    }

}
