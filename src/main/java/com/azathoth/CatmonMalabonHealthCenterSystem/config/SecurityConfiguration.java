package com.azathoth.CatmonMalabonHealthCenterSystem.config;

import com.azathoth.CatmonMalabonHealthCenterSystem.service.CustomUserDetailService;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

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
                    .cors(cors -> cors.configurationSource(corsConfigurationSource())) // enable CORS
                    .authorizeHttpRequests(
                            request -> request
                                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allow to use options request
                                    .requestMatchers("/api/patient/**").permitAll() // permitted all request
                                    .requestMatchers("/api/admin/public/**").permitAll() // permit specific request
                                    .requestMatchers("/api/doctor/public/**").permitAll() // permit specific request
                                    .requestMatchers("/api/doctor/private/**").hasAuthority("ROLE_DOCTOR") // only authenticated doctor can access
                                    .requestMatchers("/api/admin/private/**").hasAuthority("ROLE_ADMIN") // only authenticated admin can access
                                    .anyRequest().authenticated()) // require authentication other request
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // enable the session application to become stateless
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

    /**
     * CONFIGURING CORS TO ALLOW MY FRONTENDS ORIGIN
     */
    @Value("${frontend.origin}")
    private String myFrontendOrigin;
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList(myFrontendOrigin)); // allow my frontend origin
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // allowed method request to use
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // allowed headers
        config.setAllowCredentials(true); // allow to pass jwt

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
