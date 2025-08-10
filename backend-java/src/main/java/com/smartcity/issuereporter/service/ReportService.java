package com.smartcity.issuereporter.service;

import com.smartcity.issuereporter.domain.Report;
import com.smartcity.issuereporter.dto.ReportDtos;
import com.smartcity.issuereporter.repo.ReportRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {
  private final ReportRepository repo;
  private final GeometryFactory gf = new GeometryFactory();

  public ReportService(ReportRepository repo) {
    this.repo = repo;
  }

  public List<ReportDtos.ReportResponse> listFiltered(int sinceDays, String category, String status, String search) {
    OffsetDateTime since = OffsetDateTime.now().minusDays(Math.max(1, Math.min(365, sinceDays)));
    List<Report> list = repo.findFiltered(since, category == null ? "all" : category, status == null ? "all" : status, search == null ? "" : search);
    return list.stream().map(this::toDto).collect(Collectors.toList());
  }

  @Transactional
  public ReportDtos.ReportResponse create(String title, String description, String category, boolean autoCategorized, double lat, double lng, String imageUrl) {
    Report r = new Report();
    r.setTitle(title);
    r.setDescription(description);
    r.setCategory(category);
    r.setStatus("open");
    r.setAutoCategorized(autoCategorized);
    r.setLat(lat);
    r.setLng(lng);
    r.setImageUrl(imageUrl);
    Point p = gf.createPoint(new Coordinate(lng, lat));
    p.setSRID(4326);
    r.setGeom(p);
    Report saved = repo.save(r);
    return toDto(saved);
  }

  @Transactional
  public ReportDtos.ReportResponse updateStatus(UUID id, String status) {
    Report r = repo.findById(id).orElseThrow();
    r.setStatus(status);
    return toDto(repo.save(r));
  }

  @Transactional
  public void delete(UUID id) {
    repo.deleteById(id);
  }

  public List<ReportDtos.HeatPoint> heatmap(Double minLng, Double minLat, Double maxLng, Double maxLat) {
    List<Object[]> rows;
    if (minLng != null && minLat != null && maxLng != null && maxLat != null) {
      rows = repo.findInBbox(minLng, minLat, maxLng, maxLat);
    } else {
      rows = repo.fetchHeatmapAll();
    }
    return rows.stream().map(r -> {
      ReportDtos.HeatPoint h = new ReportDtos.HeatPoint();
      h.id = java.util.UUID.fromString(r[0].toString());
      h.category = (String) r[1];
      h.status = (String) r[2];
      h.lat = ((Number) r[3]).doubleValue();
      h.lng = ((Number) r[4]).doubleValue();
      h.createdAt = ((java.sql.Timestamp) r[5]).toInstant().atOffset(java.time.ZoneOffset.UTC);
      return h;
    }).collect(Collectors.toList());
  }

  private ReportDtos.ReportResponse toDto(Report r) {
    ReportDtos.ReportResponse d = new ReportDtos.ReportResponse();
    d.id = r.getId();
    d.title = r.getTitle();
    d.description = r.getDescription();
    d.category = r.getCategory();
    d.status = r.getStatus();
    d.autoCategorized = r.isAutoCategorized();
    d.imageUrl = r.getImageUrl();
    d.lat = r.getLat();
    d.lng = r.getLng();
    d.createdAt = r.getCreatedAt();
    return d;
  }
}
