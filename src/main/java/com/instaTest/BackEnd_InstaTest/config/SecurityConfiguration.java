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
				.requestMatchers("/", "/login", "/home", "/join", "/joinProc", "/api/loginProc").permitAll()	
				.requestMatchers("/admin").hasRole("ADMIN")
				.requestMatchers("/baseball/**", "/api/**").hasAnyRole("ADMIN", "USER")
				.anyRequest().authenticated()
			);
		
		http
			.formLogin((auth) -> auth 
				// .loginPage("/login")
				.loginProcessingUrl("/api/loginProc")
				.permitAll()
				.successHandler(successHandler)
			);
		
		http
			.csrf((auth) -> auth.disable());
		
		/*
		http
		.sessionManagement((auth) -> auth
			.sessionFixation((sessionFixation) -> sessionFixation
					.newSession()
					.maximumSessions(1)
					.maxSessionsPreventsLogin(true))
		);
		*/
		
		http
		.logout((auth) -> auth
			.logoutUrl("/logout")
			.logoutSuccessUrl("/")
		);
		
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}