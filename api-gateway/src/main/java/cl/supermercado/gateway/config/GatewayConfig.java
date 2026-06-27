package cl.supermercado.gateway.config;

import cl.supermercado.gateway.filter.JwtWebMvcFilter;
import org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;


@Configuration
public class GatewayConfig {

    private static JwtWebMvcFilter FILTER_INSTANCE;

    public GatewayConfig(JwtWebMvcFilter jwtWebMvcFilter) {
        GatewayConfig.FILTER_INSTANCE = jwtWebMvcFilter;
    }

    public static HandlerFilterFunction<ServerResponse, ServerResponse> jwtFilter() {
        if (FILTER_INSTANCE == null) {
            throw new IllegalStateException("JwtWebMvcFilter no inicializado");
        }
        return FILTER_INSTANCE;
    }

    @Bean
    public FilterSupplier jwtFilterSupplier() {
        return () -> {
            try {
                return List.of(GatewayConfig.class.getMethod("jwtFilter"));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        };
    }


}
