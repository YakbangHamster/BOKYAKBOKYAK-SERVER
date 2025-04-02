package com.yakbang.server.security;

import com.yakbang.server.context.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakbang.server.dto.response.DefaultResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticateFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 헤더에서 토큰 받아오기
        String token = tokenProvider.resolveToken(request);

        // 유저 인증 정보
        Authentication authentication = null;

        // 토큰 유효 검사
        boolean isGood = false;
        int statusCode = -1;   // 에러 시 보낼 상태 코드
        String exceptionMessage = "";   // 에러 시 보낼 메시지
        try {
            // 토큰 만료 체크
            isGood = !tokenProvider.isTokenExpired(token);

            // 토큰으로부터 유저 정보를 받기
            authentication = tokenProvider.getAuthentication(token);
        } catch (ExpiredJwtException e) {
            log.error("토큰 만료");

            // 상태 코드 및 에러 메시지 세팅 (401)
            statusCode = StatusCode.UNAUTHORIZED;
            exceptionMessage = "토큰이 만료되었습니다";
        } catch (IllegalArgumentException e) {
            log.info("요청 uri : " + request.getRequestURI());
            log.error("토큰이 없음");

            // 상태 코드 및 에러 메시지 세팅 (400)
            statusCode = StatusCode.BAD_REQUEST;
            exceptionMessage = "토큰이 존재하지 않습니다.";
        } catch (MalformedJwtException e) {
            log.error("토큰의 값이 유효하지 않음");

            // 상태 코드 및 에러 메시지 세팅 (400)
            statusCode = StatusCode.BAD_REQUEST;
            exceptionMessage = "토큰의 값이 유효하지 않습니다.";
        } catch (io.jsonwebtoken.SignatureException e) {
            log.error("토큰의 서명이 올바르지 않음");

            // 상태 코드 및 에러 메시지 세팅 (401)
            statusCode = StatusCode.UNAUTHORIZED;
            exceptionMessage = "토큰의 서명이 올바르지 않습니다.";
        } catch (UsernameNotFoundException e) {
            log.error("해당 유저 없음");

            // 검증 실패 설정
            isGood = false;

            // 상태 코드 및 에러 메시지 세팅 (404)
            statusCode = StatusCode.NOT_FOUND;
            exceptionMessage = e.getMessage();
        }

        // 토큰이 무언가 잘못되었다면
        if (!isGood) {
            response.setStatus(statusCode);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), DefaultResponse.from(statusCode, exceptionMessage));
        }
        // 토큰이 유효하다면
        else if (token != null) {
            // 토큰으로부터 받은 유저 정보를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 다음 Filter 실행
            filterChain.doFilter(request, response);
        }
    }
}
