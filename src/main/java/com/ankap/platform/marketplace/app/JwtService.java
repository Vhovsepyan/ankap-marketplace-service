package com.ankap.platform.marketplace.app;

import com.ankap.platform.marketplace.domain.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

  private final byte[] secret;
  private final String issuer;
  private final int minutes;

  public JwtService(@Value("${security.jwt.secret}") String jwtSecret,
                    @Value("${security.jwt.issuer}") String issuer,
                    @Value("${security.jwt.access-token-minutes}") int minutes) {
    this.secret = jwtSecret.getBytes(StandardCharsets.UTF_8);
    this.issuer = issuer;
    this.minutes = minutes;
  }

  public String issueAccessToken(long userId, UserRole role) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(minutes * 60L);

    return Jwts.builder()
            .issuer(issuer)
            .subject(Long.toString(userId))
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .claim("roles", List.of(role.name()))
            .signWith(Keys.hmacShaKeyFor(secret), Jwts.SIG.HS256)
            .compact();
  }

}