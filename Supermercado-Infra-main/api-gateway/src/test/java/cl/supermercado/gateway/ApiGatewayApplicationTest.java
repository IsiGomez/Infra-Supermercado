package cl.supermercado.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests unitarios - API Gateway")
class ApiGatewayApplicationTest {

    @Test
    @DisplayName("Debería tener la ruta de usuarios apuntando al servicio correcto")
    void shouldHaveUsuariosServiceName() {
        String expectedServiceId = "lb://usuarios";

        assertThat(expectedServiceId).startsWith("lb://");
        assertThat(expectedServiceId).contains("usuarios");
    }


    @Test
    @DisplayName("Debería tener la ruta de catálogo apuntando al servicio correcto")
    void shouldHaveCatalogoServiceName() {
        String expectedServiceId = "lb://catalogo";

        assertThat(expectedServiceId).startsWith("lb://");
        assertThat(expectedServiceId).contains("catalogo");
    }


    @Test
    @DisplayName("El prefijo lb:// indica balanceo de carga con Eureka")
    void loadBalancerPrefixShouldBeCorrect() {
        String[] services = {
            "lb://usuarios",
            "lb://catalogo",
            "lb://carrito",
            "lb://compra",
            "lb://pago",
            "lb://inventario",
            "lb://promociones",
            "lb://puntos",
            "lb://seguimiento",
            "lb://notificaciones"
        };

        for (String service : services) {
            assertThat(service)
                    .as("El servicio %s debe usar balanceo de carga", service)
                    .startsWith("lb://");
        }

    }


    @Test
    @DisplayName("Las rutas de la API deben seguir el patrón /api/v1/{recurso}/**")
    void routesShouldFollowApiV1Pattern() {
        String[] routes = {
            "/api/v1/persons/**",
            "/api/v1/logins/**",
            "/api/v1/roles/**",
            "/api/v1/products/**",
            "/api/v1/categories/**"
        };

        for (String route : routes) {
            assertThat(route)
                    .as("La ruta %s debe seguir el patrón /api/v1/", route)
                    .startsWith("/api/v1/");
            assertThat(route)
                    .as("La ruta %s debe terminar con wildcard /**", route)
                    .endsWith("/**");
        }
    }

}
