# Smart City Issue Reporter - Backend (Express + PostgreSQL + PostGIS)

Production-ready Express API with:
- PostgreSQL + PostGIS storage (raw SQL)
- Image uploads to Cloudinary
- Optional ML classification via Flask/FastAPI
- JWT auth for admin-only operations
- CORS, Helmet, rate limiting

## Endpoints

- GET /api/health
- GET /api/version
- POST /api/auth/login
  - body: { "email": "...", "password": "..." }
  - response: { "token": "JWT..." }
- GET /api/reports?category=...&status=...&sinceDays=30&search=&limit=500
- POST /api/reports (multipart/form-data)
  - fields: title, description, category (optional if auto), mode ("auto"|"manual"), lat, lng
  - file: image
- PATCH /api/reports/:id/status (admin JWT)
  - body: { "status": "open"|"in_progress"|"resolved" }
- DELETE /api/reports/:id (admin JWT)
- GET /api/reports/heatmap?bbox=minLng,minLat,maxLng,maxLat

## Environment

Copy backend/.env.example to backend/.env and fill values:

- PORT, CORS_ORIGIN
- JWT_SECRET
- ADMIN_EMAIL, ADMIN_PASSWORD_HASH or ADMIN_PASSWORD_PLAIN
- DATABASE_URL, DATABASE_SSL
- CLOUDINARY_* (cloud_name, api_key, api_secret, optional folder)
- ML_API_URL (optional), ML_API_TIMEOUT_MS

## Database

Run migrations:

- psql "$DATABASE_URL" -f scripts/sql/001_init.sql
- psql "$DATABASE_URL" -f scripts/sql/002_seed.sql

Note: Ensure PostGIS extension is available on your provider.

## Run locally

- cd backend
- npm install
- npm run dev
- API runs on http://localhost:8080

## Connect Frontend

Set NEXT_PUBLIC_API_BASE in your frontend to your API origin (e.g., http://localhost:8080). Then, replace local storage service with HTTP calls:

- POST ${NEXT_PUBLIC_API_BASE}/api/reports
- GET ${NEXT_PUBLIC_API_BASE}/api/reports
- PATCH/DELETE with Authorization: Bearer <token>

## Deployment

- Render/Railway for backend
- Enable PostGIS on your Postgres
- Set environment variables in dashboard
- Configure CORS_ORIGIN to your frontend domain

## ML API Contract (example)

Your Flask/FastAPI endpoint should accept multipart with 'image' and optional title/description. Return:

{
  "category": "pothole" | "garbage" | "streetlight" | "water-logging" | "other",
  "confidence": 0.87
}

If ML_API_URL is not set or errors, the backend falls back to keyword-based categorization.
