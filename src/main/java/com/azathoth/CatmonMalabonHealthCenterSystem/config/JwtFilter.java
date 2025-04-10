package com.azathoth.CatmonMalabonHealthCenterSystem.config;

import com.azathoth.CatmonMalabonHealthCenterSystem.service.CustomUserDetailService;
import com.azathoth.CatmonMalabonHealthCenterSystem.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationContext context;

    public JwtFilter(JwtService jwtService, ApplicationContext context) {
        this.jwtService = jwtService;
        this.context = context;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization"); // get the authorization from the header
        String token = null;
        String email = null;

        // extract the token from the header and extract the unique email from the token
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // extract the token by removing the "Bearer"
            email = jwtService.extractEmail(token); // extract unique email from the token
        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // extract role from jwt token
            String role = jwtService.extractRole(token);

            UserDetails userDetails = context.getBean(CustomUserDetailService.class)
                    .loadUserByUsername(email);

            // validate token
            if(jwtService.validateToken(token, userDetails)) {
                // create authorities based on role
                List<GrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority(role)
                );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
