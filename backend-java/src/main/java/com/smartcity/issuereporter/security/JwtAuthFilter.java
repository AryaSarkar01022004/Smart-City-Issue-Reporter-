package com.smartcity.issuereporter.security;

import com.smartcity.issuereporter.config.AppProperties;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final AppProperties props;

  public JwtAuthFilter(JwtService jwt, AppProperties props) {
    this.jwt = jwt;
    this.props = props;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      try {
        Claims claims = jwt.parse(token);
        String sub = claims.getSubject();
        if (props.getAdmin().getEmail() != null && props.getAdmin().getEmail().equalsIgnoreCase(sub)) {
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(sub, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (Exception e) {
        // ignore invalid token -> unauthenticated
      }
    }
    filterChain.doFilter(request, response);
  }
}
