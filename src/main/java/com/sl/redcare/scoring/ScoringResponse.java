package com.sl.redcare.scoring;

public record ScoringResponse(String name, String url, Double score) implements Comparable<ScoringResponse> {

    @Override
    public int compareTo(ScoringResponse other) {
        return Double.compare(other.score, score); // other object stands first for descending order
    }
}
