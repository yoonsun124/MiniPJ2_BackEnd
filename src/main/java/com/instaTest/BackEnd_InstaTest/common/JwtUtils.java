package com.instaTest.BackEnd_InstaTest.common;

import com.instaTest.BackEnd_InstaTest.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtParserBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

	private final Key hmacKey;
	private final Long expirationTime;

	public JwtUtils(Environment env) {
		String secret = env.getProperty("token.secret");
		String expiration = env.getProperty("token.expiration-time");

		if (secret == null || expiration == null) {
			throw new IllegalArgumentException("Token secret or expiration time is not set in the environment properties");
		}

		this.hmacKey = new SecretKeySpec(
				Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName()
		);
		this.expirationTime = Long.parseLong(expiration);
	}

	public String generateToken(User user) {
		Instant now = Instant.now();
		String jwtToken = Jwts.builder()
				.claim("name", user.getUsername())
				.claim("email", user.getEmail())
				.setSubject(user.getUsername())
				.setId(String.valueOf(user.getId()))
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plus(expirationTime, ChronoUnit.MILLIS)))
				.signWith(hmacKey)
				.compact();
		log.debug(jwtToken);
		return jwtToken;
	}

	private Claims getAllClaimsFromToken(String token) {
		Jws<Claims> jwt = Jwts.parser().setSigningKey(hmacKey).build().parseClaimsJws(token);
		return jwt.getBody();
	}

	public String getSubjectFromToken(String token) {
		final Claims claims = getAllClaimsFromToken(token);
		return claims.getSubject();
	}

	private Date getExpirationDateFromToken(String token) {
		final Claims claims = getAllClaimsFromToken(token);
		return claims.getExpiration();
	}

	private boolean isTokenExpired(String token) {
		Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public boolean validateToken(String token, User user) {
		// 토큰 유효기간 체크
		if (isTokenExpired(token)) {
			return false;
		}

		// 토큰 내용을 검증
		String subject = getSubjectFromToken(token);
		String username = user.getUsername();

		return subject != null && username != null && subject.equals(username);
	}
}
