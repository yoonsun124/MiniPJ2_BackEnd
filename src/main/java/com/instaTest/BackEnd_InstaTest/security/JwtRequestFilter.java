package com.instaTest.BackEnd_InstaTest.security;

import com.instaTest.BackEnd_InstaTest.common.JwtUtils;
import com.instaTest.BackEnd_InstaTest.dto.CustomUserDetail;
import com.instaTest.BackEnd_InstaTest.entity.User;
import com.instaTest.BackEnd_InstaTest.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private UserRepository repository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// /, /login, /loginProc 에 대해서는 헤더 검증을 생략
		String uri = request.getRequestURI();
		if (uri.equals("/") || uri.equals("/login") || uri.equals("/api/loginProc") || uri.equals("/joinProc")) {
			filterChain.doFilter(request, response);
			
			//log.debug("필터 오류");
			
			return;
			
			
		}
		
		
		
		
		String jwtToken = null;
		String subject = null;
		
		// Authorization 요청 헤더 존재 여부를 확인하고, 헤더 정보를 추출 
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		// Authorization 요청 헤더의 값이 Bearer 문자로 시작하는 확인 후 토큰값을 추출
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwtToken = authorizationHeader.substring(7);	// "Bearer " 이후의 모든 내용
			subject = jwtUtils.getSubjectFromToken(jwtToken);
		} else {
			log.error("Authorization 헤더 누락 또는 토큰 형식 오류");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
            response.getWriter().flush();
            return;

		}
		
		if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = repository.findByUsername(subject);
            
            CustomUserDetail userDetail = new CustomUserDetail(user);
            if (jwtUtils.validateToken(jwtToken, user)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetail.getUsername(), userDetail.getPassword(), userDetail.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }

		filterChain.doFilter(request, response);
	}
}