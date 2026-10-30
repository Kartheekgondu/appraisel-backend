# Deploy Appraisel Spring Boot backend to Render with Aiven MySQL

## Render service settings
- Create **New + -> Web Service** and connect the backend GitHub repository.
- Runtime/language: **Docker**.
- Dockerfile path: `./Dockerfile`.
- If this extracted project folder is the repository root, leave Root Directory blank.
- Deploy after adding the environment variables below.

## Required Render environment variables
Set these in Render -> Web Service -> Environment. Use the exact host, port, database, username and password shown in your Aiven service's connection information:

- `SPRING_DATASOURCE_URL` = `jdbc:mysql://<AIVEN_HOST>:<AIVEN_PORT>/appraisel_db?sslMode=REQUIRED`
- `SPRING_DATASOURCE_USERNAME` = Aiven MySQL username
- `SPRING_DATASOURCE_PASSWORD` = Aiven MySQL password

Optional mail and CORS variables:
- `SPRING_MAIL_HOST` = `smtp.gmail.com`
- `SPRING_MAIL_PORT` = `587`
- `SPRING_MAIL_USERNAME` = Gmail address
- `SPRING_MAIL_PASSWORD` = newly generated Gmail App Password
- `FRONTEND_URL` = exact Vercel frontend origin, e.g. `https://your-app.vercel.app` (no trailing slash)

Render automatically supplies `PORT`; do not add `SERVER_PORT`.

## Aiven checklist
1. Create a MySQL service in Aiven and create the `appraisel_db` database (or use the exact database name you created).
2. Copy the Aiven host, port, service username and password from the service's connection information.
3. Use the Aiven host and port in `SPRING_DATASOURCE_URL`; never use `localhost`.
4. Keep `sslMode=REQUIRED` in the JDBC URL for encrypted connection. If your Aiven connection instructions provide a CA certificate / stricter verification setup, follow those instructions.
5. Ensure the Aiven service is running and accepts connections from the Render service. If IP allowlisting is enabled, allow the Render outbound IPs or use the provider's supported network access configuration.

## Important
- Do not commit real passwords, API keys or `.env` files to GitHub.
- Revoke and replace any Gmail App Password or database password previously pasted into chat/logs.
- `ddl-auto=update` is retained for this app. For production, use schema migrations (Flyway/Liquibase) before scaling.
- This package was updated for environment-based DB/mail configuration and Render's PORT expansion. A successful live deployment still requires valid Aiven credentials/network access.
