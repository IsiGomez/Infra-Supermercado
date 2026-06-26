package cl.duoc.usuarios.service;

import cl.duoc.usuarios.model.Login;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Login login) {
        String rolName = login.getRol() != null ? login.getRol().getName() : "CLIENTE";
        Long personId = login.getPerson() != null ? login.getPerson().getId() : null;

        return Jwts.builder()
                .subject(login.getUsername())
                .claims(Map.of(
                        "roles", List.of(Map.of("authority", "ROLE_" + rolName)),
                        "rolName", rolName,
                        "userId", personId
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

}
