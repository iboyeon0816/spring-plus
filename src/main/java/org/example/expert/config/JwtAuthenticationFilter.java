package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String ERROR = "error";
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String jwt = jwtUtil.substringToken(authHeader);

                Claims claims = jwtUtil.extractClaims(jwt);
                AuthUser authUser = getAuthUser(claims);

                JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (SecurityException | MalformedJwtException e) {
                request.setAttribute(ERROR, "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            } catch (ExpiredJwtException e) {
                request.setAttribute(ERROR, "Expired JWT token, 만료된 JWT token 입니다.");
            } catch (UnsupportedJwtException e) {
                request.setAttribute(ERROR, "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            }
        }
        filterChain.doFilter(request, response);
    }

    private AuthUser getAuthUser(Claims claims) {
        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        String nickname = claims.get("nickname", String.class);
        UserRole userRole = UserRole.of(claims.get("userRole", String.class));
        return new AuthUser(userId, email, nickname, userRole);
    }
}
