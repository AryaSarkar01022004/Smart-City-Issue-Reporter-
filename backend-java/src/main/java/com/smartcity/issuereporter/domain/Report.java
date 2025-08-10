package com.smartcity.issuereporter.domain;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
public class Report {
  @Id
  @GeneratedValue
  private UUID id;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  private String category; // pothole, garbage, ...
  private String status;   // open, in_progress, resolved

  @Column(name = "auto_categorized")
  private boolean autoCategorized;

  @Column(name = "image_url")
  private String imageUrl;

  private double lat;
  private double lng;

  @Column(columnDefinition = "geometry(Point,4326)")
  private Point geom;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @PrePersist
  public void onCreate() {
    if (createdAt == null) createdAt = OffsetDateTime.now();
  }

  // getters and setters
  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public boolean isAutoCategorized() { return autoCategorized; }
  public void setAutoCategorized(boolean autoCategorized) { this.autoCategorized = autoCategorized; }
  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
  public double getLat() { return lat; }
  public void setLat(double lat) { this.lat = lat; }
  public double getLng() { return lng; }
  public void setLng(double lng) { this.lng = lng; }
  public Point getGeom() { return geom; }
  public void setGeom(Point geom) { this.geom = geom; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
