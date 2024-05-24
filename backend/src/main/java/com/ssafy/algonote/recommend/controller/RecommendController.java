package com.ssafy.algonote.recommend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.algonote.config.security.SecurityUtil;
import com.ssafy.algonote.member.service.MemberService;
import com.ssafy.algonote.recommend.dto.RecommendGroupDto;
import com.ssafy.algonote.recommend.dto.response.RecommendProblemResDto;

import com.ssafy.algonote.recommend.dto.response.RecommendResDto;
import com.ssafy.algonote.recommend.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/recommend")
@Tag(name = "추천", description = "문제 추천 API")
public class RecommendController {

    private final RecommendService recommendService;
    private final MemberService memberService;

    @Operation(
        summary = "문제 추천",
        description = "tag에 해당하는 문제 중 사용자가 풀지 않은 문제를 추천한다"
    )
    @GetMapping("/{tag}")
    public ResponseEntity<Page<RecommendProblemResDto>> recommendUnsolvedProblem(@PathVariable("tag") String tag,@RequestParam int page,@RequestParam int size){
        Long memberId = SecurityUtil.getMemberId();
        log.info("tag: {} page: {} size: {}", tag, page, size);
        return ResponseEntity.ok(recommendService.recommendUnsolvedProblem(memberId, tag, page, size));
    }

    @GetMapping("/test")
    public ResponseEntity<Void> test(){
        recommendService.test();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/group/{group}")
    public ResponseEntity<RecommendGroupDto> recommendByTags(@PathVariable("group") String group, @RequestParam int page, @RequestParam int size){
        Long memberId = SecurityUtil.getMemberId();
        log.info("group: {}", group);
//        memberService.recommendByTags(memberId, group, page, size);
//        return ResponseEntity.ok().build();
        return ResponseEntity.ok(recommendService.recommendByTags(memberId, group, page, size));
    }
}
