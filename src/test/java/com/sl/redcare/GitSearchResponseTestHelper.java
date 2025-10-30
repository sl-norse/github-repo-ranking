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

    public static GitSearchResponse createMockGitSearchResponseWithMultipleRepos(ZonedDateTime now) {
        GitSearchItem item1 = new GitSearchItem();
        item1.setName("repo-1");
        item1.setUrl("https://github.com/user/repo-1");
        item1.setStars(200);  // Max stars
        item1.setForks(50);   // Max forks
        item1.setUpdated(now.minusDays(1));  // 1 day ago

        GitSearchItem item2 = new GitSearchItem();
        item2.setName("repo-2");
        item2.setUrl("https://github.com/user/repo-2");
        item2.setStars(100);
        item2.setForks(30);
        item2.setUpdated(now.minusDays(5));  // 5 days ago

        GitSearchItem item3 = new GitSearchItem();
        item3.setName("repo-3");
        item3.setUrl("https://github.com/user/repo-3");
        item3.setStars(40);
        item3.setForks(10);
        item3.setUpdated(now.minusDays(4));  // 10 days ago

        GitSearchResponse response = new GitSearchResponse();
        response.setItems(List.of(item1, item2, item3));
        return response;
    }
}
