package cl.supermercado.seguimiento.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }

        String expected = "ROLE_" + role;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (expected.equals(authority.getAuthority())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isFuncionario() {
        return hasRole("FUNCIONARIO");
    }

    public static boolean isCliente() {
        return hasRole("CLIENTE");
    }

    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        if (auth.getPrincipal() instanceof Long userId) {
            return userId;
        }

        return null;
    }

}
