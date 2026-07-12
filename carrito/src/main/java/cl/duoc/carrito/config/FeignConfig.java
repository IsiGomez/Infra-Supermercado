package cl.duoc.carrito.config;

import cl.duoc.carrito.exception.RecursoRemotoNoEncontradoException;
import cl.duoc.carrito.exception.ServicioRemotoException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Configuration
public class FeignConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Bean
    public RequestInterceptor authorizationHeaderInterceptor() {
        return (RequestTemplate template) -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (authHeader != null && !authHeader.isBlank()) {
                template.header(AUTHORIZATION_HEADER, authHeader);
            }
        };
    }


    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String servicio = methodKey.substring(0, methodKey.indexOf('#'));

            return switch (response.status()) {
                case 404 -> new RecursoRemotoNoEncontradoException(servicio, methodKey);

                case 400, 401, 403 -> new ServicioRemotoException(
                        servicio, response.status(), "Solicitud rechazada por " + servicio);

                case 500, 502, 503, 504 -> new ServicioRemotoException(
                        servicio, response.status(), servicio + " no esta disponible");

                default -> new ErrorDecoder.Default().decode(methodKey, response);
            };
        };
    }

}
