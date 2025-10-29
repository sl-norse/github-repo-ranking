package com.sl.redcare.gitclient;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitSearchService {

    private final GitClient client;
    private final GitSearchMapper mapper;

    private static final String QUERY_TEMPLATE = "language:%s+created:>%s";
    private final SortParameter DEFAULT_SORT_PARAMETER = SortParameter.STARS;
    private final SortOrder DEFAULT_SORT_ORDER = SortOrder.DESC;

    // TODO consider adding SortParam and SortOrder as args for more flexibility
    public List<GitSearchResult> searchGitRepositories(String language, LocalDate earliestCreated) {
        var query = searchQuery(language, earliestCreated);
        var response = gitSearchResponse(query, DEFAULT_SORT_PARAMETER, DEFAULT_SORT_ORDER);
        return response.stream().map(mapper::responseToDto).toList();
    }

    private List<GitSearchItem> gitSearchResponse(String query, SortParameter sortParameter, SortOrder sortOrder) {
        try {
            return client.searchGitRepositories(query, sortParameter.queryView, sortOrder.queryView).getItems();
        } catch (Exception e) {
            log.error("Failed to fetch data from Git Api, because of error: {}", e.getMessage());
            throw e;
        }
    }

    private String searchQuery(String language, LocalDate earliestCreated) {
        return String.format(QUERY_TEMPLATE, language, earliestCreated);
    }

    @Getter
    @RequiredArgsConstructor
    private enum SortParameter {
        STARS("stars"), FORKS("forks"), HELP_WANTED_ISSUES("help-wanted-issues"), UPDATED("updated");

        private final String queryView;

    }

    @Getter
    @RequiredArgsConstructor
    private enum SortOrder {
        ASC("asc"), DESC("desc");

        private final String queryView;

    }

}
