package com.instaTest.BackEnd_InstaTest.security;

import com.instaTest.BackEnd_InstaTest.common.JwtUtils;
import com.instaTest.BackEnd_InstaTest.entity.User;
import com.instaTest.BackEnd_InstaTest.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private JwtUtils jwtUtils;

	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		User user = userRepository.findByUsername(userDetails.getUsername());
		
		// 로그인 성공 후 수행할 작업들을 기술
		// 예) 데이터베이스에 접속 로그를 기록, 알림 이메일을 발송, ...
		
		String jwtToken = jwtUtils.generateToken(user);
		log.debug(jwtToken);
		
		// 세션에 사용자 정보를 저장
		request.getSession().setAttribute("user", user);
		
		// 응답 헤더에 생성한 토큰을 설정
		response.setHeader("token", jwtToken);
		
		
		/*
        CustomUserDetail userDetail = new CustomUserDetail(userEntity);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetail.getUsername(), userDetail.getPassword(), userDetail.getAuthorities());
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		*/
		
		// 리다이렉트 
		// response.sendRedirect("/");
	}
}