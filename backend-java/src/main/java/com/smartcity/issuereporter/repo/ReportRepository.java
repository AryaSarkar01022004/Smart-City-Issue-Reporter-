package com.smartcity.issuereporter.repo;

import com.smartcity.issuereporter.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
  @Query("""
    SELECT r FROM Report r
    WHERE r.createdAt >= :since
      AND (:category = 'all' OR r.category = :category)
      AND (:status = 'all' OR r.status = :status)
      AND (:search = '' OR LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))
    ORDER BY r.createdAt DESC
  """)
  List<Report> findFiltered(OffsetDateTime since, String category, String status, String search);

  @Query(value = """
    SELECT id, category, status, lat, lng, created_at
    FROM reports
    WHERE geom && ST_MakeEnvelope(?1, ?2, ?3, ?4, 4326)
    ORDER BY created_at DESC
    LIMIT 5000
  """, nativeQuery = true)
  List<Object[]> findInBbox(double minLng, double minLat, double maxLng, double maxLat);

  @Query(value = """
    SELECT id, category, status, lat, lng, created_at
    FROM reports
    ORDER BY created_at DESC
    LIMIT 5000
  """, nativeQuery = true)
  List<Object[]> fetchHeatmapAll();
}
