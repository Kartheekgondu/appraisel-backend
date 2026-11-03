# Local backend

Run `mvnw.cmd spring-boot:run` on Windows or `./mvnw spring-boot:run` on macOS/Linux. The API listens on port 9090. Confirm MySQL is running and update `src/main/resources/application.properties` with your local database and SMTP settings. Hibernate `ddl-auto=update` adds the employee_id column to the existing table.
