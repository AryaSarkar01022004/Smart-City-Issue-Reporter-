package com.smartcity.issuereporter.config;

import com.smartcity.issuereporter.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

  private final AppProperties props;
  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(AppProperties props, JwtAuthFilter jwtAuthFilter) {
    this.props = props;
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.GET, "/api/health", "/api/version").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/reports/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/reports").permitAll()
        .requestMatchers(HttpMethod.PATCH, "/api/reports/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/reports/**").hasRole("ADMIN")
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(props.getCorsOrigin() != null ? props.getCorsOrigin() : "*"));
    configuration.setAllowedMethods(List.of("GET","POST","PATCH","DELETE","OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));
    configuration.setAllowCredentials(false);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
