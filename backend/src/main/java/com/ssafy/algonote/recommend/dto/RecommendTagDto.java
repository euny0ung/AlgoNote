package com.ssafy.algonote.recommend.dto;

import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record RecommendTagDto(
    String tag,
    Set<Long> solvedProblemIds
) {
    public static RecommendTagDto of(String tag, Set<Long> solvedProblemIds) {
        return RecommendTagDto.builder()
            .tag(tag)
            .solvedProblemIds(solvedProblemIds)
            .build();
    }
}
