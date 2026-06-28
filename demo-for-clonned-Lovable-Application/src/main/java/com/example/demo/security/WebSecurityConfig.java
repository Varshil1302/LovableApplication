package com.example.demo.security;

import com.example.demo.config.CorsConfig;
import jakarta.servlet.DispatcherType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class WebSecurityConfig
{

    JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
    {
         return httpSecurity
                 .csrf(csrfConfig->csrfConfig.disable())
                 .cors(Customizer.withDefaults())
                 .sessionManagement(sessionConfig->sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .authorizeHttpRequests(auth->auth
                         .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                         .requestMatchers("/api/auth/**","/webhooks/**","/swagger-ui/**","/swagger-ui.html",
                                 "/v3/api-docs/**").permitAll()
                         .requestMatchers("/api/projects/**").authenticated()
                         .anyRequest().authenticated()
                 )
                 .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                 .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

}
