package com.plutocart.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plutocart.user_service.dto.ErrorResponse;
import com.plutocart.user_service.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            final String jwtToken = authHeader.substring(7);

            final String userEmail = jwtService.getEmailFromToken(jwtToken);
            final String role = jwtService.getRoleFromToken(jwtToken);
            final UUID userId = jwtService.getUserIdFromToken(jwtToken);

                if(userEmail !=null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(role != null ?  role : "UNKNOWN")));

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Successfully authenticated user: {}", userEmail);
                }

            }  catch (ExpiredJwtException e)  {
                log.error("JWT token has expired: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired: " + e.getMessage(), request.getRequestURI());
            } catch (JwtException e){
                log.error("An error occurred while processing the JWT token: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "You are not authorised to Access this resource", request.getRequestURI());
            } catch (Exception e) {
            log.error("An unexpected error occurred during JWT authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
        }


    private void sendErrorResponse(HttpServletResponse response, int status, String message, String path) throws java.io.IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorStatus = status == HttpServletResponse.SC_UNAUTHORIZED ? "Unauthorized" : "Forbidden";
        ErrorResponse errorResponse = new ErrorResponse(status, message, Instant.now(), errorStatus, path);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
