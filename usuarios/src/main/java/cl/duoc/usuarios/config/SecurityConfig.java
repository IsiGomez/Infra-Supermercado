package cl.duoc.usuarios.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/auth").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/persons").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/logins").permitAll()
                        .requestMatchers("/api/v1/roles/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/persons").hasRole("FUNCIONARIO")
                        .requestMatchers(HttpMethod.GET, "/api/v1/persons/**").hasAnyRole("FUNCIONARIO", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/persons/**").hasAnyRole("FUNCIONARIO", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/persons/**").hasAnyRole("FUNCIONARIO", "CLIENTE")

                        .requestMatchers(HttpMethod.GET, "/api/v1/logins").hasRole("FUNCIONARIO")
                        .requestMatchers(HttpMethod.GET, "/api/v1/logins/**").hasAnyRole("FUNCIONARIO", "CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/logins/**").hasAnyRole("FUNCIONARIO", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/logins/**").hasAnyRole("FUNCIONARIO", "CLIENTE")

                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"No autorizado\", \"message\": \"Falta el token o es invalido\"}");
                        })

                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\": \"Acceso denegado\", \"message\": \"No tienes permisos para realizar esta accion\"}"
                            );
                        })
                )

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
