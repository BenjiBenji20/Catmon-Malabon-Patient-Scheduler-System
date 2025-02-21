package com.azathoth.CatmonMalabonHealthCenter.config;

import com.azathoth.CatmonMalabonHealthCenter.service.CustomUserDetailService;
import com.azathoth.CatmonMalabonHealthCenter.service.JwtService;
import org.springframework.context.ApplicationContext;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtService jwtService;
    private final ApplicationContext context;

    public SecurityConfiguration(JwtService jwtService, ApplicationContext context) {
        this.jwtService = jwtService;
        this.context = context;
    }

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
                                    .requestMatchers("/api/admin/public/**").permitAll() // permit specific request
                                    .requestMatchers("/api/doctor/public/**").permitAll() // permit specific request
                                    .requestMatchers("/api/doctor/private/**").hasRole("DOCTOR") // only authenticated doctor can access
                                    .requestMatchers("/api/admin/private/**").hasAuthority("ROLE_ADMIN") // only authenticated admin can access
                                    .anyRequest().authenticated()) // require authentication other request
                    .addFilterBefore(new JwtFilter(jwtService, context), UsernamePasswordAuthenticationFilter.class);


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
