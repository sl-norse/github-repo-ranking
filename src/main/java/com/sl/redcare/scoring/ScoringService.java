package com.sl.redcare.scoring;

import com.sl.redcare.gitclient.GitSearchResult;
import com.sl.redcare.gitclient.GitSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(ScoringCalculationCoefficientsConfig.class)
public class ScoringService {

    private final GitSearchService gitSearchService;
    private final ScoringCalculationCoefficientsConfig coefficients;

    @Cacheable(cacheNames = "scoredRepos", key = "#language + '_' + #earliestCreated") // TODO change to Redis, Hazelcast etc to work properly with multiple instances
    public List<ScoringResponse> scoreRepositories(String language, LocalDate earliestCreated) {
        var searchResults = gitSearchService.searchGitRepositories(language, earliestCreated);
        if (searchResults.isEmpty()) return Collections.emptyList();
        var maxStars = Double.valueOf(searchResults.getFirst().stars());
        var maxForks = searchResults.stream().mapToDouble(GitSearchResult::forks).max().orElse(1);
        return searchResults.stream()
                .map(searchResult -> getScoringResponse(searchResult, maxStars, maxForks))
                .sorted().toList();
    }

    private ScoringResponse getScoringResponse(GitSearchResult searchResult, Double maxStars, Double maxForks) {
        var score = calculateScore(searchResult, maxStars, maxForks);
        return new ScoringResponse(searchResult.name(), searchResult.url(), score);
    }

    private Double calculateScore(GitSearchResult searchResult, Double maxStars, Double maxForks) {
        return getStarsParameter(searchResult, maxStars) * coefficients.stars() +
                getForksParameter(searchResult, maxForks) * coefficients.forks() +
                getRecencyUpdateParameter(searchResult) * coefficients.updated();
    }

    private Double getStarsParameter(GitSearchResult searchResult, Double maxStars) {
        return searchResult.stars() / maxStars;
    }

    private Double getForksParameter(GitSearchResult searchResult, Double maxForks) {
        return searchResult.forks() / maxForks;
    }

    private Double getRecencyUpdateParameter(GitSearchResult searchResult) {
        return 1.0 / (Period.between(searchResult.updated().toLocalDate(), LocalDate.now()).getDays() + 1);
    }

}
