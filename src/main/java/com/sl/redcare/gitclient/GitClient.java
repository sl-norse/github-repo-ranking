package com.sl.redcare.gitclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "git-client", url = "${redcare.interview.git-api.base-url}", configuration = FeignEncodingFixConfig.class)
public interface GitClient {

    @GetMapping(value = "/search/repositories", headers = {"Accept=application/vnd.github+json", "X-GitHub-Api-Version=2022-11-28"})
    GitSearchResponse searchGitRepositories(
            @RequestParam(name = "q") String query,
            @RequestParam(name = "sort") String sort,
            @RequestParam(name = "order") String order);

}
