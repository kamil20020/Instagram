package pl.instagram.instagram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

         /*
            This is where we configure the security required for our endpoints and setup our app to serve as
            an OAuth2 Resource Server, using JWT validation.
        */

        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/users/register", "/users/*/profile", "/users/*/header").permitAll()
            .requestMatchers("/users/user-account/*/header", "/users/ids").permitAll()
            .requestMatchers(HttpMethod.GET, "/users").permitAll()
            .requestMatchers(HttpMethod.GET, "/users/*/followers").permitAll()
            .requestMatchers(HttpMethod.GET, "/users/*/followed").permitAll()
            .requestMatchers(HttpMethod.GET, "/posts/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/posts/author/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/comments/*/likes").permitAll()
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
        );

        http
            .csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(withDefaults())
            );

        return http.build();
    }
}
