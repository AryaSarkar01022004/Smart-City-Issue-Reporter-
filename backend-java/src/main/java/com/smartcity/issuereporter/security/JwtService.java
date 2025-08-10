package com.smartcity.issuereporter.security;

import com.smartcity.issuereporter.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
  private final AppProperties props;

  public JwtService(AppProperties props) {
    this.props = props;
  }

  private Key key() {
    byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(props.getJwtSecret().getBytes()));
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String subject, Map<String, Object> claims, long ttlMillis) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
      .setClaims(claims)
      .setSubject(subject)
      .setIssuedAt(new Date(now))
      .setExpiration(new Date(now + ttlMillis))
      .signWith(key(), SignatureAlgorithm.HS256)
      .compact();
  }

  public Claims parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
  }
}
