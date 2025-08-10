package com.smartcity.issuereporter.controller;

import com.smartcity.issuereporter.dto.ReportDtos;
import com.smartcity.issuereporter.service.ClassificationService;
import com.smartcity.issuereporter.service.ImageStorageService;
import com.smartcity.issuereporter.service.ReportService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
  private final ReportService reports;
  private final ImageStorageService images;
  private final ClassificationService classifier;

  public ReportController(ReportService reports, ImageStorageService images, ClassificationService classifier) {
    this.reports = reports;
    this.images = images;
    this.classifier = classifier;
  }

  @GetMapping
  public List<ReportDtos.ReportResponse> list(
      @RequestParam(defaultValue = "30") @Min(1) @Max(365) int sinceDays,
      @RequestParam(defaultValue = "all") String category,
      @RequestParam(defaultValue = "all") String status,
      @RequestParam(defaultValue = "") String search,
      @RequestParam(defaultValue = "500") @Min(1) @Max(1000) int limit
  ) {
    var list = reports.listFiltered(sinceDays, category, status, search);
    if (list.size() > limit) return list.subList(0, limit);
    return list;
  }

  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<?> create(
      @RequestParam String title,
      @RequestParam String description,
      @RequestParam double lat,
      @RequestParam double lng,
      @RequestParam(defaultValue = "auto") String mode,
      @RequestParam(required = false) String category,
      @RequestPart("image") MultipartFile image
  ) {
    try {
      if (image == null || image.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Image is required"));

      String resolvedCategory = category;
      boolean autoCategorized = false;
      if (!StringUtils.hasText(category) || "auto".equalsIgnoreCase(mode)) {
        var res = classifier.classify(title, description, image.getBytes());
        resolvedCategory = res.category;
        autoCategorized = true;
      }
      if (!List.of("pothole","garbage","streetlight","water-logging","other").contains(resolvedCategory)) {
        return ResponseEntity.badRequest().body(Map.of("error","Invalid category"));
      }

      String imageUrl = images.upload(image);
      var dto = reports.create(title, description, resolvedCategory, autoCategorized, lat, lng, imageUrl);
      return ResponseEntity.status(201).body(dto);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(Map.of("error","Server error"));
    }
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestBody ReportDtos.UpdateStatusRequest body) {
    if (body == null || body.status == null) return ResponseEntity.badRequest().body(Map.of("error","Status required"));
    if (!List.of("open","in_progress","resolved").contains(body.status)) return ResponseEntity.badRequest().body(Map.of("error","Invalid status"));
    try {
      var dto = reports.updateStatus(id, body.status);
      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      return ResponseEntity.status(404).body(Map.of("error","Not found"));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable UUID id) {
    try {
      reports.delete(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(404).body(Map.of("error","Not found"));
    }
  }

  @GetMapping("/heatmap")
  public List<ReportDtos.HeatPoint> heatmap(
      @RequestParam(required = false) Double bbox // comma separated: minLng,minLat,maxLng,maxLat
  ) {
    return List.of(); // placeholder, overload method below
  }

  @GetMapping(value = "/heatmap", params = "bbox")
  public List<ReportDtos.HeatPoint> heatmapWithBbox(@RequestParam String bbox) {
    try {
      String[] parts = bbox.split(",");
      if (parts.length != 4) return reports.heatmap(null, null, null, null);
      double minLng = Double.parseDouble(parts[0].trim());
      double minLat = Double.parseDouble(parts[1].trim());
      double maxLng = Double.parseDouble(parts[2].trim());
      double maxLat = Double.parseDouble(parts[3].trim());
      return reports.heatmap(minLng, minLat, maxLng, maxLat);
    } catch (Exception e) {
      return reports.heatmap(null, null, null, null);
    }
  }

  @GetMapping(value = "/heatmap", params = "!bbox")
  public List<ReportDtos.HeatPoint> heatmapAll() {
    return reports.heatmap(null, null, null, null);
  }
}
