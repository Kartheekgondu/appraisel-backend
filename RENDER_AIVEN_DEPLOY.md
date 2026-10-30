# Appraisel Spring Boot backend — Render deployment

## Deploy

- Java: 21
- Build: `./mvnw clean package -DskipTests`
- Start: `java -jar target/appraisal-letter-backend-0.0.1-SNAPSHOT.jar`
- Render should provide the `PORT` environment variable; the application uses `${PORT:8080}`.

## Required Render environment variables

Set these in Render → backend service → Environment:

- `SPRING_DATASOURCE_URL`: hosted MySQL JDBC URL, e.g. `jdbc:mysql://HOST:PORT/appraisel_db?sslMode=REQUIRED`
- `SPRING_DATASOURCE_USERNAME`: database username
- `SPRING_DATASOURCE_PASSWORD`: database password
- `FRONTEND_URL`: your Vercel origin, e.g. `https://your-project.vercel.app`

Optional email settings:
- `SPRING_MAIL_HOST=smtp.gmail.com`
- `SPRING_MAIL_PORT=587`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD` (use a newly generated Gmail App Password)

Never use `localhost` as the database host on Render. `localhost` refers to the Render container, not your computer or hosted database.

## Vercel proxy

The matching frontend project includes `vercel.json` to proxy `/backend/*` to this Render service. The browser uses the Vercel origin for API requests. CORS is still configured for direct API access and local development.

## Health check

`GET /api/appraisels/health`

Expected JSON:
`{"status":"UP","message":"Appraisal backend is running"}`

## Security

Do not commit database or email passwords. Keep them only in the hosting provider's environment-variable settings. If credentials were previously committed or shared in an uploaded project, rotate them immediately.
