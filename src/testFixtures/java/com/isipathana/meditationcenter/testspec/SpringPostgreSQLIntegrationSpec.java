package com.isipathana.meditationcenter.testspec;

import com.isipathana.meditationcenter.testcontainer.PostgreSQLTestContainer;
import com.isipathana.meditationcenter.wrappers.TestDSLContextWrapper;
import org.jooq.DSLContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration spec for tests requiring PostgreSQL database.
 * Uses Testcontainers to provide a real PostgreSQL instance.
 *
 * @author Sathira Basnayake
 */
@ContextConfiguration(initializers = PostgreSQLTestContainer.Initializer.class)
public class SpringPostgreSQLIntegrationSpec extends SpringContextIntegrationSpec {

    @TestConfiguration
    public static class Config {
        @Bean
        public TestDSLContextWrapper testDSLContextWrapper(DSLContext dslContext) {
            return new TestDSLContextWrapper(dslContext);
        }
    }
}
