package com.smartcity.issuereporter.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ReportDtos {
  public static class CreateRequest {
    public String title;
    public String description;
    public String category; // optional if mode=auto
    public String mode; // "auto" | "manual"
    public double lat;
    public double lng;
    public MultipartFile image;
  }

  public static class UpdateStatusRequest {
    public String status;
  }

  public static class ReportResponse {
    public UUID id;
    public String title;
    public String description;
    public String category;
    public String status;
    public boolean autoCategorized;
    public String imageUrl;
    public double lat;
    public double lng;
    public OffsetDateTime createdAt;
  }

  public static class HeatPoint {
    public UUID id;
    public String category;
    public String status;
    public double lat;
    public double lng;
    public OffsetDateTime createdAt;
  }
}
