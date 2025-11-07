package com.isipathana.meditationcenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for CORS (Cross-Origin Resource Sharing).
 * <p>
 * Allows the frontend application to make requests to the backend API.
 * <p>
 * Configured Origins:
 * - http://localhost:3000 (Local frontend development)
 * - Production frontend URL (to be added when deployed)
 * <p>
 * Security Considerations:
 * - Only specific origins are allowed (not *)
 * - Credentials (cookies, authorization headers) are allowed
 * - Pre-flight requests are cached for 1 hour (3600 seconds)
 *
 * @author Sathira Basnayake
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3000",           // Local development
                        "http://localhost:3001",           // Alternative port
                        "https://isipathana-meditation-center.onrender.com",
                        "https://isipathana-meditation-center-ui.sathira97.workers.dev",
                        "https://meditationcenterui.onrender.com",// Production backend (for testing)
                        "https://iimc.lk"
                        // Add your production frontend URL here when deployed:
                        // "https://your-frontend-domain.com"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);  // Cache pre-flight response for 1 hour
    }
}
