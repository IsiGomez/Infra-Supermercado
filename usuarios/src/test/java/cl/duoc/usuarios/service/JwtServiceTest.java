package cl.duoc.usuarios.service;

import cl.duoc.usuarios.model.Login;
import cl.duoc.usuarios.model.Person;
import cl.duoc.usuarios.model.Rol;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests unitarios - JwtService")
public class JwtServiceTest {

    private static final String SECRET =
            "VGhpcy1Jcy1BLVZlcnktU2VjdXJlLVNlY3JldC1LZXktRm9yLUpXVC1BcGktQXV0aGVudGljYXRpb24=";

    private static final long EXPIRATION = 86400000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
    }

    private SecretKey signingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    @Test
    @DisplayName("generateToken: debe incluir el username como subject del token")
    void generateToken_deberiaIncluirUsernameComoSubject() {
        Rol rol = new Rol(1L, "FUNCIONARIO", "Rol de funcionario");
        Person person = new Person();
        person.setId(10L);
        Login login = new Login();
        login.setUsername("FunClaudia");
        login.setRol(rol);
        login.setPerson(person);

        String token = jwtService.generateToken(login);

        Claims claims = Jwts.parser()
                .verifyWith(signingKey()).build()
                .parseSignedClaims(token).getPayload();

        assertThat(claims.getSubject()).isEqualTo("FunClaudia");
    }


    @Test
    @DisplayName("generateToken: debe incluir rol con prefijo ROLE_ en el claim roles")
    void generateToken_deberiaIncluirRolConPrefijo() {
        Rol rol = new Rol(1L, "FUNCIONARIO", "Rol de funcionario");
        Person person = new Person();
        person.setId(10L);
        Login login = new Login();
        login.setUsername("FunClaudia");
        login.setRol(rol);
        login.setPerson(person);

        String token = jwtService.generateToken(login);

        Claims claims = Jwts.parser()
                .verifyWith(signingKey()).build()
                .parseSignedClaims(token).getPayload();

        assertThat(claims.get("rolName", String.class)).isEqualTo("FUNCIONARIO");

        @SuppressWarnings("unchecked")
        List<Map<String, String>> roles = claims.get("roles", List.class);
        assertThat(roles.get(0).get("authority")).isEqualTo("ROLE_FUNCIONARIO");
    }


    @Test
    @DisplayName("generateToken: debe usar CLIENTE por defecto cuando el login no tiene rol")
    void generateToken_deberiaUsarClientePorDefecto_cuandoRolEsNull() {
        Person person = new Person();
        person.setId(20L);
        Login login = new Login();
        login.setUsername("sinRol");
        login.setRol(null);
        login.setPerson(person);

        String token = jwtService.generateToken(login);

        Claims claims = Jwts.parser()
                .verifyWith(signingKey()).build()
                .parseSignedClaims(token).getPayload();

        assertThat(claims.get("rolName", String.class)).isEqualTo("CLIENTE");
    }


    @Test
    @DisplayName("generateToken: la fecha de expiración debe ser posterior a la de emisión")
    void generateToken_expiracionDebeSerPosteriorAEmision() {
        Rol rol = new Rol(2L, "CLIENTE", "Rol de cliente");
        Person person = new Person();
        person.setId(30L);
        Login login = new Login();
        login.setUsername("Juanito");
        login.setRol(rol);
        login.setPerson(person);

        String token = jwtService.generateToken(login);

        Claims claims = Jwts.parser()
                .verifyWith(signingKey()).build()
                .parseSignedClaims(token).getPayload();

        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

}
