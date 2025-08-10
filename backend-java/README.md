# Smart City Issue Reporter - Spring Boot Backend

Features:
- Spring Boot 3 (Web, Security, Data JPA, Validation)
- PostgreSQL + PostGIS via Hibernate Spatial
- JWT auth for admin endpoints
- Cloudinary image uploads
- Optional ML classification (multipart POST to ML_API_URL)
- Flyway migrations

Run locally:
- Set env vars or edit application.yml (DB URL, JWT_SECRET, ADMIN_EMAIL, ADMIN_PASSWORD_HASH or ADMIN_PASSWORD_PLAIN, Cloudinary keys)
- mvn spring-boot:run

Endpoints:
- GET /api/health
- POST /api/auth/login -> { token }
- GET /api/reports
- POST /api/reports (multipart: title, description, lat, lng, mode, category?, image)
- PATCH /api/reports/{id}/status (Bearer)
- DELETE /api/reports/{id} (Bearer)
- GET /api/reports/heatmap?bbox=minLng,minLat,maxLng,maxLat
