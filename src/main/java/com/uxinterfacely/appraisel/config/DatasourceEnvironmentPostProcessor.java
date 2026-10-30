package com.uxinterfacely.appraisel.config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Makes the datasource settings tolerant of the way hosting dashboards present them.
 *
 * <ul>
 *   <li>Accepts a plain "mysql://user:pass@host:port/db?ssl-mode=REQUIRED" URI (as copied from
 *       Aiven) and converts it to a proper JDBC URL plus username/password.</li>
 *   <li>Trims whitespace/quotes that are often pasted into environment variables by accident.</li>
 *   <li>Adds sslMode=REQUIRED for remote hosts when no SSL option was given.</li>
 *   <li>Fails fast with a readable message if the app runs on Render but still points at localhost
 *       (which is the real cause of Hibernate's "Unable to determine Dialect without JDBC metadata").</li>
 * </ul>
 */
public class DatasourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String URL_KEY = "spring.datasource.url";
    private static final String USER_KEY = "spring.datasource.username";
    private static final String PASS_KEY = "spring.datasource.password";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String rawUrl = clean(environment.getProperty(URL_KEY));
        if (rawUrl == null || rawUrl.isEmpty()) {
            return;
        }

        Map<String, Object> overrides = new HashMap<>();
        String url = rawUrl;

        // Convert "mysql://user:pass@host:port/db?x=y" into a JDBC URL.
        String lower = url.toLowerCase();
        String prefix = null;
        if (lower.startsWith("mysql://")) {
            prefix = "mysql://";
        } else if (lower.startsWith("jdbc:mysql://") && url.substring(13).split("/", 2)[0].contains("@")) {
            prefix = "jdbc:mysql://";
        }
        if (prefix != null) {
            String rest = url.substring(prefix.length());
            int slash = rest.indexOf('/');
            String authority = slash >= 0 ? rest.substring(0, slash) : rest;
            String pathAndQuery = slash >= 0 ? rest.substring(slash) : "";
            int at = authority.lastIndexOf('@');
            String hostPort = at >= 0 ? authority.substring(at + 1) : authority;
            if (at >= 0) {
                String userInfo = authority.substring(0, at);
                int colon = userInfo.indexOf(':');
                String user = colon >= 0 ? userInfo.substring(0, colon) : userInfo;
                String pass = colon >= 0 ? userInfo.substring(colon + 1) : "";
                if (isBlank(System.getenv("SPRING_DATASOURCE_USERNAME"))) {
                    overrides.put(USER_KEY, decode(user));
                }
                if (isBlank(System.getenv("SPRING_DATASOURCE_PASSWORD"))) {
                    overrides.put(PASS_KEY, decode(pass));
                }
            }
            url = "jdbc:mysql://" + hostPort + pathAndQuery;
        }

        // Aiven's "ssl-mode" spelling is for the mysql CLI; Connector/J wants "sslMode".
        url = url.replace("ssl-mode=", "sslMode=");

        boolean local = url.contains("//localhost") || url.contains("//127.0.0.1");

        // Fail fast with a clear explanation instead of Hibernate's confusing dialect error.
        if ("true".equalsIgnoreCase(System.getenv("RENDER")) && local) {
            throw new IllegalStateException(
                    "Datasource points to localhost while running on Render. Set the environment variable "
                            + "SPRING_DATASOURCE_URL (jdbc:mysql://<host>:<port>/<db>?sslMode=REQUIRED), "
                            + "SPRING_DATASOURCE_USERNAME and SPRING_DATASOURCE_PASSWORD in the Render service "
                            + "Environment tab, then redeploy. Current URL: " + url);
        }

        // Remote MySQL providers (Aiven etc.) require TLS.
        if (!local && !url.toLowerCase().contains("sslmode=") && !url.toLowerCase().contains("usessl=")) {
            url = url + (url.contains("?") ? "&" : "?") + "sslMode=REQUIRED";
        }

        if (!url.equals(environment.getProperty(URL_KEY))) {
            overrides.put(URL_KEY, url);
        }

        // Clean stray whitespace/quotes on username and password too.
        String user = environment.getProperty(USER_KEY);
        if (user != null && !overrides.containsKey(USER_KEY) && !user.equals(clean(user))) {
            overrides.put(USER_KEY, clean(user));
        }
        String pass = environment.getProperty(PASS_KEY);
        if (pass != null && !overrides.containsKey(PASS_KEY) && !pass.equals(clean(pass))) {
            overrides.put(PASS_KEY, clean(pass));
        }

        if (!overrides.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("normalizedDatasource", overrides));
        }
    }

    private static String clean(String value) {
        if (value == null) {
            return null;
        }
        String v = value.trim();
        if (v.length() >= 2 && ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'")))) {
            v = v.substring(1, v.length() - 1).trim();
        }
        return v;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String decode(String s) {
        try {
            return URLDecoder.decode(s.replace("+", "%2B"), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return s;
        }
    }

    @Override
    public int getOrder() {
        // Run after ConfigData (application.properties) has been loaded.
        return Ordered.LOWEST_PRECEDENCE;
    }
}
