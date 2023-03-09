package com.example.demo.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.config.AppProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class TokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

	private final AppProperties appProperties;

	public TokenProvider(AppProperties appProperties) {
		this.appProperties = appProperties;
	}

	public String createAccessToken(Long memberId) {

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

		return Jwts.builder()
			.setSubject(Long.toString(memberId))
			.setIssuedAt(new Date())
			.setExpiration(expiryDate)
			.signWith(appProperties.getAuth().getTokenSecret())
			.compact();
	}

	public String createRefrshToken(Long memberId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getRefreshTokenExpirationMsec());
		return Jwts.builder()
			.setSubject(Long.toString(memberId))
			.setIssuedAt(new Date())
			.setExpiration(expiryDate)
			.signWith(appProperties.getAuth().getRefreshTokenSecret())
			.compact();
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(appProperties.getAuth().getTokenSecret())
			.build()
			.parseClaimsJws(token)
			.getBody();

		return Long.parseLong(claims.getSubject());
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(appProperties.getAuth().getTokenSecret())
				.build()
				.parseClaimsJws(authToken);
			return true;
		} catch (SecurityException ex) {
			logger.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			logger.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}
		return false;
	}

	public boolean isExpiredToken(String authToken) {

		try {
			Jwts.parserBuilder()
				.setSigningKey(appProperties.getAuth().getTokenSecret())
				.build()
				.parseClaimsJws(authToken);
			return false;
		} catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
			return false;
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	public boolean isWillExpiredInThreeDays(String token) {
		int threeDayMesc = 259200000; //1000 * 60 * 60 * 24 * 3
		Date expiration = Jwts.parserBuilder()
			.build()
			.parseClaimsJws(token)
			.getBody().getExpiration();

		long timeToExpired = expiration.getTime() - new Date().getTime();
		if (timeToExpired < threeDayMesc) {
			return true;
		} else {
			return false;
		}
	}
}
