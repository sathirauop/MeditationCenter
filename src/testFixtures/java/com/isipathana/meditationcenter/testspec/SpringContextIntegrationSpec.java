package com.isipathana.meditationcenter.testspec;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Base integration spec for Spring context tests.
 * Provides fixed Clock bean for deterministic testing.
 *
 * @author Sathira Basnayake
 */
@ContextConfiguration
public class SpringContextIntegrationSpec implements SpringContextTest {

    @TestConfiguration
    public static class Config {

        /**
         * Provides a fixed Clock for deterministic testing.
         * All time-based operations will use this fixed time: 2024-05-21 08:30:00 UTC.
         */
        @Bean
        @Primary
        public Clock fixedClock() {
            return Clock.fixed(
                    LocalDateTime.of(2024, 5, 21, 8, 30, 0)
                            .atZone(ZoneId.of("UTC"))
                            .toInstant(),
                    ZoneOffset.UTC
            );
        }
    }
}
