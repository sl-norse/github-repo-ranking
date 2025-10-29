package com.sl.redcare.gitclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class GitSearchItem {
    private String name;
    @JsonProperty("html_url")
    private String url;
    @JsonProperty("stargazers_count")
    private Integer stars;
    @JsonProperty("forks_count")
    private Integer forks;
    @JsonProperty("pushed_at")
    private ZonedDateTime updated;
}
