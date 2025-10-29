package com.sl.redcare;

import com.sl.redcare.gitclient.GitClient;
import com.sl.redcare.gitclient.GitSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.sl.redcare.GitSearchResponseTestHelper.createMockGitSearchResponse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("rate-limit-test")
class RateLimiterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitClient gitClient;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());

        GitSearchResponse mockResponse = createMockGitSearchResponse();
        when(gitClient.searchGitRepositories(anyString(), anyString(), anyString()))
                .thenReturn(mockResponse);
    }

    @Test
    void shouldEnforceRateLimiting() throws Exception {
        int successfulRequests = 0;
        int rateLimitedRequests = 0;
        int totalRequests = 10;

        // Make requests exceeding rate limit
        for (int i = 0; i < totalRequests; i++) {
            var result = mockMvc.perform(get("/git/repo/scores")
                            .param("language", "java")
                            .param("earliestCreated", "2024-01-01")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            int status = result.getResponse().getStatus();
            if (status == 200) {
                successfulRequests++;
            } else if (status == 429) {
                rateLimitedRequests++;
            }
        }

        // Verify rate limiting occurred
        assert rateLimitedRequests > 0 : "Expected some requests to be rate-limited. Successful: " + successfulRequests + ", Rate limited: " + rateLimitedRequests;
    }

    @Test
    void shouldAllowRequestsAfterLimitRefreshPeriod() throws Exception {
        // First batch - use up the rate limit
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/git/repo/scores")
                    .param("language", "kotlin-" + i)
                    .param("earliestCreated", "2024-01-01")
                    .contentType(MediaType.APPLICATION_JSON));
        }

        // This should be rate limited
        mockMvc.perform(get("/git/repo/scores")
                        .param("language", "go")
                        .param("earliestCreated", "2024-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.status").value("429"))
                .andExpect(jsonPath("$.message").value("Too many requests. Please try again later."))
                .andExpect(jsonPath("$.error").value("Rate Limit Exceeded"));

        // Wait for rate limiter to refresh (1 second + buffer)
        Thread.sleep(1100);

        // This should succeed after refresh period
        mockMvc.perform(get("/git/repo/scores")
                        .param("language", "rust")
                        .param("earliestCreated", "2024-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
