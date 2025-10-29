package com.sl.redcare;

import com.sl.redcare.gitclient.GitSearchItem;
import com.sl.redcare.gitclient.GitSearchResponse;

import java.time.ZonedDateTime;
import java.util.List;

public class GitSearchResponseTestHelper {
    public static GitSearchResponse createMockGitSearchResponse() {
        GitSearchItem item = new GitSearchItem();
        item.setName("test-repo");
        item.setUrl("https://github.com/user/test-repo");
        item.setStars(100);
        item.setForks(20);
        item.setUpdated(ZonedDateTime.now().minusMonths(6));

        GitSearchResponse response = new GitSearchResponse();
        response.setItems(List.of(item));
        return response;
    }
}
