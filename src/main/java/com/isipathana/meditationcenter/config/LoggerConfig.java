package com.isipathana.meditationcenter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configuration for SLF4J Logger bean injection.
 * Provides logger instances with the correct class name for each injection point.
 * <p>
 * Usage: Simply inject Logger in any Spring-managed bean:
 * <pre>
 * {@code
 * @Service
 * @RequiredArgsConstructor
 * public class UserService {
 *     private final Logger logger;
 *     // logger will be named "com.isipathana.meditationcenter.service.UserService"
 * }
 * }
 * </pre>
 */
@Configuration
public class LoggerConfig {

    /**
     * Creates a Logger bean with prototype scope.
     * Each injection point gets a logger with its own class name.
     *
     * @param injectionPoint Information about where the logger is being injected
     * @return Logger instance named after the injection point's class
     */
    @Bean
    @Scope("prototype")
    public Logger logger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(
                injectionPoint.getMember().getDeclaringClass()
        );
    }
}
