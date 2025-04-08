package com.yakbang.server.security;

import com.yakbang.server.entity.User;
import com.yakbang.server.repository.UserRepository;
import com.yakbang.server.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticateFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticateFilter(TokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Swagger 및 API 문서 경로는 필터 적용 제외
//        String uri = request.getRequestURI();
//        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String accessToken = tokenProvider.resolveAccessToken(request);

        try {
            if (accessToken != null && tokenProvider.isValidToken(accessToken)) {
                String identity = tokenProvider.getIdentityFromToken(accessToken);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(identity);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // access token이 만료된 경우
            String refreshToken = tokenProvider.resolveRefreshToken(request);

            if (refreshToken != null && tokenProvider.isValidToken(refreshToken)) {
                String identity = tokenProvider.getIdentityFromToken(refreshToken);
                User user = userRepository.findByIdentity(identity);

                // 새 access token 발급
                String newAccessToken = tokenProvider.generateAccessToken(user);

                // 헤더에 새 access token 추가
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                // 인증 객체 설정
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(identity);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }

        filterChain.doFilter(request, response);
    }
}
