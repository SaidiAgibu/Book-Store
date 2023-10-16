package com.saidi.book_store.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

    private final JwtAuthEntryPoint authEntryPoint;

    public static final String[] UN_SECURED_URLS = {
            "/api/v1/auth/**",
            "/api/v1/user/**",
            "/api/v1/admin/**",
            "/api/v1/books/**",
            "/api/v1/cart/**",
            "/api/v1/order/**",
            "/v2/api-docs"
            ,"/v3/api-docs"
            ,"/v3/api-docs/**"
            ,"/swagger-resources"
            ,"/swagger-resources/**"
            ,"/configuration/ui"
            ,"/configuration/settings"
            ,"/swagger-ui/**"
            ,"/webjars/**"
            ,"/swagger-ui.html"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session ->  session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers(UN_SECURED_URLS).permitAll()
                            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")   // Admin endpoints require ADMIN role
                            .requestMatchers("/api/v1/user/**").hasRole("USER")   // Member endpoints require USER role
                            .anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults())
                        .logout(logout -> logout.logoutUrl("/api/v1/auth/logout"));
        http.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter authenticationFilter(){
        return new JwtAuthenticationFilter();
    }
}
