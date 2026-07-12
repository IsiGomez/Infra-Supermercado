package cl.duoc.inventario.exception;

public class ServicioRemotoException extends RuntimeException {

    private final int status;

    public ServicioRemotoException(String servicio, int status, String detalle) {
        super("Error del servicio '" + servicio + "' (HTTP " + status + "): " + detalle);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
