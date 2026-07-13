package cl.supermercado.compra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de compra API")
                        .version("1.0.0")
                        .description("Documentación interactiva de los endpoints de compra" +
                                "Obtén tu JWT desde ms-usuarios y haz clic en 'Authorize' para ingresarlo."))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));

    }

    @Bean
    public GroupedOpenApi comprasApi() {
        return GroupedOpenApi.builder()
                .group("1. Módulo de compras")
                .pathsToMatch("/api/v1/compras/**")
                .build();
    }
}
