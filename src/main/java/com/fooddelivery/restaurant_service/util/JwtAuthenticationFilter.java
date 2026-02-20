package com.fooddelivery.restaurant_service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/** Filter to validate JWT token and set restaurant authentication */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String restaurantName = tokenProvider.getRestaurantNameFromToken(jwt);

                // Build authorities from token roles, ensuring Spring expects the ROLE_ prefix
                String[] roles = tokenProvider.getRolesFromToken(jwt);
                List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

                // Prefer numeric id in token (id, restaurantId, or numeric subject) so controllers
                // that parse authentication.getName() as Long will work.
                Long idFromToken = tokenProvider.getIdFromToken(jwt);
                String principalName = idFromToken != null ? String.valueOf(idFromToken) : restaurantName;

                logger.debug("Authenticated principal={}, authorities={}", principalName, authorities);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principalName, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set restaurant authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /** Extract token from Authorization header */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
