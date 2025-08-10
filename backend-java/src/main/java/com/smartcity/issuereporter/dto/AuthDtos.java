package com.smartcity.issuereporter.dto;

public class AuthDtos {
  public static class LoginRequest {
    public String email;
    public String password;
  }
  public static class LoginResponse {
    public String token;
    public LoginResponse(String token) { this.token = token; }
  }
}
