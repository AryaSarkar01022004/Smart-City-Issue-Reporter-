package com.smartcity.issuereporter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private String corsOrigin;
  private String jwtSecret;

  public static class Admin {
    private String email;
    private String passwordHash;
    private String passwordPlain;
    // getters/setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPasswordPlain() { return passwordPlain; }
    public void setPasswordPlain(String passwordPlain) { this.passwordPlain = passwordPlain; }
  }

  public static class Cloudinary {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
    private String folder;
    // getters/setters
    public String getCloudName() { return cloudName; }
    public void setCloudName(String cloudName) { this.cloudName = cloudName; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }
  }

  public static class Ml {
    private String url;
    private int timeoutMs;
    // getters/setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public int getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }
  }

  private Admin admin = new Admin();
  private Cloudinary cloudinary = new Cloudinary();
  private Ml ml = new Ml();

  // getters/setters
  public String getCorsOrigin() { return corsOrigin; }
  public void setCorsOrigin(String corsOrigin) { this.corsOrigin = corsOrigin; }
  public String getJwtSecret() { return jwtSecret; }
  public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }
  public Admin getAdmin() { return admin; }
  public Cloudinary getCloudinary() { return cloudinary; }
  public Ml getMl() { return ml; }
}
