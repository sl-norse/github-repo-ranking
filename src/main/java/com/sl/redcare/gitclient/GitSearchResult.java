package com.sl.redcare.gitclient;

import java.time.ZonedDateTime;

public record GitSearchResult(String name, String url, Integer stars, Integer forks, ZonedDateTime updated) {
}
