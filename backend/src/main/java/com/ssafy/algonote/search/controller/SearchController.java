package com.ssafy.algonote.search.controller;

import com.ssafy.algonote.search.dto.response.SearchResDto;
import com.ssafy.algonote.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "키워드로 검색하기")
    @GetMapping("/full-text")
    public ResponseEntity<SearchResDto> fullTextSearch(@RequestParam("keyword") String keyword,
                                                     @RequestParam("page") int page) {

        log.info("fullTextSearch keyword: {}, page: {}", keyword, page);

        return ResponseEntity.ok(searchService.fullTextSearch(keyword, page));
    }

}
