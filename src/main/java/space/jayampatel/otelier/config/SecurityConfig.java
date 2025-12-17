package space.jayampatel.otelier.config;

import space.jayampatel.otelier.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for REST API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No
                                                                                                             // sessions,
                                                                                                             // JWT only
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/health",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/hotels").hasRole("ADMIN")
                        .requestMatchers("/api/hotel-assignments").hasRole("ADMIN")
                        .requestMatchers("/api/hotels/*/bookings").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }
}
