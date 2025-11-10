package com.isipathana.meditationcenter.testcontainer;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * PostgreSQL testcontainer singleton for integration tests.
 * Provides a real PostgreSQL 15 database for testing with Flyway migrations.
 *
 * @author Sathira Basnayake
 */
public class PostgreSQLTestContainer {

    private static final String POSTGRESQL_IMAGE = "postgres:15-alpine";

    private static PostgreSQLContainer<?> container;

    private PostgreSQLTestContainer() {}

    /**
     * Get or create the singleton PostgreSQL container.
     * Container is started lazily and reused across all tests.
     */
    public static PostgreSQLContainer<?> getInstance() {
        if (container == null) {
            container = new PostgreSQLContainer<>(POSTGRESQL_IMAGE)
                    .withDatabaseName("meditation_test_db")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true); // Reuse container across test runs for performance
        }

        return container;
    }

    /**
     * Spring ApplicationContextInitializer that starts the PostgreSQL container
     * and injects JDBC connection properties into the application context.
     */
    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            PostgreSQLContainer<?> container = PostgreSQLTestContainer.getInstance();

            container.start();

            // Inject PostgreSQL connection properties
            TestPropertyValues.of(
                    "spring.datasource.url=" + container.getJdbcUrl(),
                    "spring.datasource.username=" + container.getUsername(),
                    "spring.datasource.password=" + container.getPassword(),
                    "spring.jpa.hibernate.ddl-auto=validate", // Flyway manages schema
                    "spring.flyway.enabled=true" // Ensure Flyway runs
            ).applyTo(applicationContext);
        }
    }
}
