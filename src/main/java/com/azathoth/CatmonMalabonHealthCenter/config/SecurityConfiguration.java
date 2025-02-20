package com.azathoth.CatmonMalabonHealthCenter.config;

import com.azathoth.CatmonMalabonHealthCenter.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * customized the spring security filter chain
     * disable the csrf, manage api endpoints availability by permitting specific
     * request and restricted other request by requiring authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        try {
            httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(
                            request -> request
                                    .requestMatchers("/api/patient/**").permitAll() // permitted all request
                                    .requestMatchers("/api/admin/create", "/api/admin/auth").permitAll() // permit specific request
                                    .requestMatchers("/api/doctor/register", "/api/doctor/login").permitAll() // permit specific request
                                    .anyRequest().authenticated() // require authentication other request
                    );

            return httpSecurity.build();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * set password encoder strength
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * customized authentication manager to authenticate custom user details service
     */
    // Override the authenticationManagerBean method and configure it to use your custom UserDetailsService
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig,
            CustomUserDetailService customUserDetailService, PasswordEncoder passwordEncoder  ) throws Exception {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }
}
