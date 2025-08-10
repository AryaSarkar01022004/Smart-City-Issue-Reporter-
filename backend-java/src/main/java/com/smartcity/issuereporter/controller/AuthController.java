package com.smartcity.issuereporter.controller;

import com.smartcity.issuereporter.config.AppProperties;
import com.smartcity.issuereporter.dto.AuthDtos;
import com.smartcity.issuereporter.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AppProperties props;
  private final JwtService jwt;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthController(AppProperties props, JwtService jwt) {
    this.props = props;
    this.jwt = jwt;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthDtos.LoginRequest req) {
    if (req.email == null || req.password == null) return ResponseEntity.badRequest().body(Map.of("error","Email and password required"));
    if (!req.email.equalsIgnoreCase(props.getAdmin().getEmail())) return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
    boolean ok = false;
    if (props.getAdmin().getPasswordHash() != null && !props.getAdmin().getPasswordHash().isBlank()) {
      ok = encoder.matches(req.password, props.getAdmin().getPasswordHash());
    } else if (props.getAdmin().getPasswordPlain() != null && !props.getAdmin().getPasswordPlain().isBlank()) {
      ok = req.password.equals(props.getAdmin().getPasswordPlain());
    }
    if (!ok) return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
    String token = jwt.generateToken(req.email, Map.of("role","admin"), 7L * 24 * 60 * 60 * 1000);
    return ResponseEntity.ok(new AuthDtos.LoginResponse(token));
  }
}
