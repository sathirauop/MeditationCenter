package com.isipathana.meditationcenter.rest.events.get;

import com.isipathana.meditationcenter.seeds.EventSeed;
import com.isipathana.meditationcenter.testspec.SpringWebIntegrationSpec;
import com.isipathana.meditationcenter.wrappers.TestDSLContextWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static com.isipathana.meditationcenter.jooq.Tables.EVENTS;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for GET /api/event endpoint.
 * Tests complete HTTP request/response flow with real database.
 *
 * @author Sathira Basnayake
 */
public class GetEventsIntegrationTest extends SpringWebIntegrationSpec {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDSLContextWrapper dslContextWrapper;

    @BeforeEach
    public void setUp() {
        dslContextWrapper.seedData(EVENTS, EventSeed.seed());
    }

    @Test
    void shouldReturn200_whenGettingEvents() throws Exception {
        mockMvc.perform(get("/api/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.currentOffset").isNumber())
                .andExpect(jsonPath("$.maxOffset").isNumber());
    }

    @Test
    void shouldReturnPaginatedData_withDefaultParameters() throws Exception {
        mockMvc.perform(get("/api/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(4)) // 4 active events
                .andExpect(jsonPath("$.currentOffset").value(0))
                .andExpect(jsonPath("$.maxOffset").value(4));
    }

    @Test
    void shouldRespectLimitParameter() throws Exception {
        mockMvc.perform(get("/api/event")
                        .param("limit", "2")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.maxOffset").value(4)); // Total count unchanged
    }

    @Test
    void shouldRespectOffsetParameter() throws Exception {
        // Get first page
        mockMvc.perform(get("/api/event")
                        .param("limit", "2")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.currentOffset").value(0))
                .andExpect(jsonPath("$.data[0].name").value("Wesak Celebration"))
                .andExpect(jsonPath("$.data[1].name").value("Meditation Retreat"));

        // Get second page
        mockMvc.perform(get("/api/event")
                        .param("limit", "2")
                        .param("offset", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.currentOffset").value(2)) // offset * limit = 1 * 2 = 2
                .andExpect(jsonPath("$.data[0].name").value("Dhamma Talk"))
                .andExpect(jsonPath("$.data[1].name").value("New Year Blessing"));
    }

    @Test
    void shouldReturnEmptyData_whenOffsetBeyondAvailable() throws Exception {
        mockMvc.perform(get("/api/event")
                        .param("limit", "20")
                        .param("offset", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.maxOffset").value(4)); // Total count still 4
    }

    @Test
    void shouldOnlyReturnActiveEvents() throws Exception {
        mockMvc.perform(get("/api/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(4)) // 4 active out of 5 total
                .andExpect(jsonPath("$.data[*].name").value(not(hasItem("Cancelled Workshop"))));
    }

    @Test
    void shouldReturnEventsInCorrectOrder() throws Exception {
        mockMvc.perform(get("/api/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Wesak Celebration"))
                .andExpect(jsonPath("$.data[1].name").value("Meditation Retreat"))
                .andExpect(jsonPath("$.data[2].name").value("Dhamma Talk"))
                .andExpect(jsonPath("$.data[3].name").value("New Year Blessing"));
    }

//    @Test
//    void shouldReturnAllEventFields() throws Exception {
//        mockMvc.perform(get("/api/event")
//                        .param("limit", "1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data[0].eventId").exists())
//                .andExpect(jsonPath("$.data[0].name").exists())
//                .andExpect(jsonPath("$.data[0].description").exists())
//                .andExpect(jsonPath("$.data[0].eventDate").exists())
//                .andExpect(jsonPath("$.data[0].startTime").exists())
//                .andExpect(jsonPath("$.data[0].endTime").exists())
//                .andExpect(jsonPath("$.data[0].location").exists())
//                .andExpect(jsonPath("$.data[0].images").exists());
//    }

    @Test
    void shouldReturnEmptyData_whenNoEvents() throws Exception {
        // Purge all events
        dslContextWrapper.purgeData(EVENTS);

        mockMvc.perform(get("/api/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.currentOffset").value(0))
                .andExpect(jsonPath("$.maxOffset").value(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20, 50, 100})
    void shouldAcceptValidLimits(int limit) throws Exception {
        mockMvc.perform(get("/api/event")
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldHandleLimit1() throws Exception {
        mockMvc.perform(get("/api/event")
                        .param("limit", "1")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void shouldHandleLimit100() throws Exception {
        mockMvc.perform(get("/api/event")
                        .param("limit", "100")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(4)); // Only 4 events available
    }

    @Test
    void shouldHandleOffset0() throws Exception {
        mockMvc.perform(get("/api/event")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentOffset").value(0));
    }

    @Test
    void shouldWorkWithoutParameters() throws Exception {
        // Should use defaults: limit=20, offset=0
        mockMvc.perform(get("/api/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.currentOffset").value(0));
    }

    @Test
    void shouldCalculatePaginationCorrectly() throws Exception {
        // With 4 events total, limit=2, offset=0 should show events 0-1
        mockMvc.perform(get("/api/event")
                        .param("limit", "2")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentOffset").value(0)) // offset * limit = 0 * 2 = 0
                .andExpect(jsonPath("$.maxOffset").value(4))
                .andExpect(jsonPath("$.data.length()").value(2));

        // offset=1 with limit=2 should show events 2-3
        mockMvc.perform(get("/api/event")
                        .param("limit", "2")
                        .param("offset", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentOffset").value(2)) // offset * limit = 1 * 2 = 2
                .andExpect(jsonPath("$.maxOffset").value(4))
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}
