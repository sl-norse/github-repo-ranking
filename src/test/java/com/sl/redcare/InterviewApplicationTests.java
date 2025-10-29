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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InterviewApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitClient gitClient;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        // Clear cache before each test
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Test
    void shouldReturnScoredRepositoriesSuccessfully() throws Exception {
        // Given
        GitSearchResponse mockResponse = createMockGitSearchResponse();
        when(gitClient.searchGitRepositories(anyString(), anyString(), anyString())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/git/repo/scores").param("language", "java").param("earliestCreated", "2024-01-01").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$", hasSize(greaterThan(0)))).andExpect(jsonPath("$[0].name").exists()).andExpect(jsonPath("$[0].url").exists()).andExpect(jsonPath("$[0].score").isNumber());

        verify(gitClient, times(1)).searchGitRepositories(anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturnCachedResultsOnSecondRequest() throws Exception {
        // Given
        GitSearchResponse mockResponse = createMockGitSearchResponse();
        when(gitClient.searchGitRepositories(anyString(), anyString(), anyString())).thenReturn(mockResponse);

        String language = "java";
        String earliestCreated = "2024-01-01";

        // When - First request
        mockMvc.perform(get("/git/repo/scores").param("language", language).param("earliestCreated", earliestCreated).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        // When - Second request with same parameters
        mockMvc.perform(get("/git/repo/scores").param("language", language).param("earliestCreated", earliestCreated).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThan(0))));

        // Then - Feign client should be called only once (second call uses cache)
        verify(gitClient, times(1)).searchGitRepositories(anyString(), anyString(), anyString());
    }

    @Test
    void shouldHandleFeignClientError() throws Exception {
        // Given
        when(gitClient.searchGitRepositories(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("GitHub API unavailable"));

        // When & Then
        mockMvc.perform(get("/git/repo/scores")
                        .param("language", "java")
                        .param("earliestCreated", "2024-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.status").value("500"))
                .andExpect(jsonPath("$.message").value("Server is not able to fulfill request at the moment"))
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(gitClient, times(1)).searchGitRepositories(anyString(), anyString(), anyString());
    }

}
