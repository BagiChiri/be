package com.pos.be.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private JwtDecoder jwtDecoder;

    public JwtCookieAuthenticationFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public JwtCookieAuthenticationFilter() {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract token from cookies
        String token = getJwtFromCookies(request);
        if (token !=null) {
            try {
                // Decode the JWT
                Jwt decodedJwt = jwtDecoder.decode(token);
                System.out.println("decodedJwt: " + decodedJwt);
                // Extract roles (authorities) from the JWT claims
                List<SimpleGrantedAuthority> authorities = extractAuthorities(decodedJwt);

                // Create the Authentication object
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        decodedJwt.getSubject(), null, authorities
                );

                // Set authentication to the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Handle invalid token (e.g., expired, tampered with)
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);  // Continue filter chain
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        // Logic to extract JWT from cookies
        String token = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    System.out.println(token);
                    break;
                }
                System.out.println("no token");
            }
        }
        return token;
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extract roles (authorities) from the "roles" claim in the JWT
        // Ensure that the "roles" claim exists in your JWT and contains a list of roles.
        List<String> roles = jwt.getClaimAsStringList("roles");

        return roles != null ? roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()) : List.of();
    }
}


