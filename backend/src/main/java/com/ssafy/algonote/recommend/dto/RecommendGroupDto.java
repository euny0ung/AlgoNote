package com.ssafy.algonote.recommend.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record RecommendGroupDto(
    String group,
    List<RecommendTagDto> tags
) {

    public static RecommendGroupDto of(String group, List<RecommendTagDto> tags) {
        return RecommendGroupDto.builder()
            .group(group)
            .tags(tags)
            .build();
    }
}
