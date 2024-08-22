package com.instaTest.BackEnd_InstaTest.config;

import com.instaTest.BackEnd_InstaTest.security.CustomAuthenticationSuccessHandler;
import com.instaTest.BackEnd_InstaTest.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private CustomAuthenticationSuccessHandler successHandler;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((auth) -> auth
						.requestMatchers("/", "/login", "/home", "/join", "/joinProc", "/api/loginProc", "/favicon.ico").permitAll()
						.requestMatchers("/admin").hasRole("ADMIN")
						.requestMatchers("/board/**", "/api/**").hasAnyRole("ADMIN", "USER")
						.anyRequest().authenticated()
				)
				.formLogin((auth) -> auth
						.loginProcessingUrl("/api/loginProc")
						.permitAll()
						.successHandler(successHandler)
				)
				.csrf((auth) -> auth.disable())  // CSRF를 필요 시 활성화
				.logout((auth) -> auth
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")  // 로그아웃 시 세션과 쿠키 제거
				)
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}


}
