package com.emantahir.finance_tracker.service.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.emantahir.finance_tracker.util.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailServiceImpl userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                System.out.println("=== JWT FILTER DEBUG ===");
                System.out.println("Request URI: " + request.getRequestURI());


        final String authHeader = request.getHeader("Authorization");
        System.out.println("Auth Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        System.out.println("DEBUG: Extracted username: " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("DEBUG: Loading user details for: " + username);
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("DEBUG: User authorities: " + userDetails.getAuthorities());

            if (jwtService.isTokenValid(token, userDetails)) {
                System.out.println("DEBUG: Token is valid, setting authentication");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);System.out.println("DEBUG: Authentication set successfully");
            } else {
                System.out.println("DEBUG: Token is INVALID");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error loading user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    filterChain.doFilter(request, response);
}
}