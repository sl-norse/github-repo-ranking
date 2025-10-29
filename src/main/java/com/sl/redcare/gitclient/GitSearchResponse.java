package com.sl.redcare.gitclient;

import lombok.Data;

import java.util.List;

@Data
public class GitSearchResponse {
    private List<GitSearchItem> items;
}
