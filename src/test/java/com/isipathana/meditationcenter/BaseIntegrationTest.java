package com.isipathana.meditationcenter;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that require a database.
 * Uses Testcontainers to spin up a PostgreSQL container for tests.
 * <p>
 * Benefits:
 * - Tests run against real PostgreSQL (not H2/in-memory)
 * - No local database setup required
 * - Isolated test environment
 * - Same database version as production
 * <p>
 * Usage: Extend this class in your test classes:
 * <pre>
 * class UserRepositoryTest extends BaseIntegrationTest {
 *     // Your tests here
 * }
 * </pre>
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("meditation_db_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
