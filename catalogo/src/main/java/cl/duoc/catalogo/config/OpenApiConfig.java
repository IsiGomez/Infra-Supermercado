package cl.duoc.catalogo.config;

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
                        .title("Microservicio de Cátalogo API")
                        .version("1.0.0")
                        .description("Documentación interactiva de los endpoints de catalogo" +
                                "Obtén tu JWT desde ms-usuarios y haz clic en 'Authorize' para ingresarlo."))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa directamente tu token JWT aquí")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }


    @Bean
    public GroupedOpenApi categoriesApi() {
        return GroupedOpenApi.builder()
                .group("1. Módulo de Categorías")
                .pathsToMatch("/api/v1/categories/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productsApi(){
        return GroupedOpenApi.builder()
                .group("2. Módulo de Productos")
                .pathsToMatch("/api/v1/products/**")
                .build();
    }

}
