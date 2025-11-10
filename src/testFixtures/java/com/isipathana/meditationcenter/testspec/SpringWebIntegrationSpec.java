package com.isipathana.meditationcenter.testspec;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

/**
 * Integration spec for web layer tests.
 * Provides MockMvc for testing HTTP endpoints with real database.
 *
 * @author Sathira Basnayake
 */
@AutoConfigureMockMvc
public class SpringWebIntegrationSpec extends SpringPostgreSQLIntegrationSpec {
    // MockMvc and TestDSLContextWrapper are automatically available for injection
}
