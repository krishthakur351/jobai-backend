package com.krish.jobai.filter;

import com.krish.jobai.service.CustomUserDetailsService;
import com.krish.jobai.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Skip JWT filter for login and register APIs
    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String path =
                request.getServletPath();

        return path.equals("/api/users/login") ||
                path.equals("/api/users/register") ||
                path.startsWith("/api/resume") ||
                path.startsWith("/api/ai");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        // Check if Authorization header exists
        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            // Extract token
            token = authHeader.substring(7);

            // Extract email from token
            email = jwtUtil.extractEmail(token);
        }

        // Authenticate user if email exists
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                    customUserDetailsService.loadUserByUsername(email);

            // Validate token
            if (jwtUtil.validateToken(token, email)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Set authentication
                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}