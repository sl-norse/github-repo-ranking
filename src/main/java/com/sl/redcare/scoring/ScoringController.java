package com.sl.redcare.scoring;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScoringController {

    private final ScoringService scoringService;

    @GetMapping(value = "/git/repo/scores", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimiter(name = "gitRepoScoring") // TODO should be at API gateway/load balancer level
    List<ScoringResponse> gitRepositoriesScored(String language, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate earliestCreated) {
        return scoringService.scoreRepositories(language, earliestCreated);
    }

}
