package com.ssafy.algonote.recommend.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.algonote.exception.CustomException;
import com.ssafy.algonote.exception.ErrorCode;
import com.ssafy.algonote.member.domain.Member;
import com.ssafy.algonote.member.repository.MemberRepository;
import com.ssafy.algonote.problem.repository.ProblemRepository;
import com.ssafy.algonote.problem.repository.SolvedProblemRepository;
import com.ssafy.algonote.recommend.dto.RecommendDto;
import com.ssafy.algonote.recommend.dto.RecommendGroupDto;
import com.ssafy.algonote.recommend.dto.response.RecommendProblemResDto;
import com.ssafy.algonote.recommend.dto.response.RecommendResDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendService {

    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final SolvedProblemRepository solvedProblemRepository;


    @Value("${fastapi.url}")
    private String fastApiUrl;

    public void test(){
        String url = fastApiUrl + "/python/recommend/test";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        log.info("response: {}", response);

    }


    @Transactional
    public Page<RecommendProblemResDto> recommendUnsolvedProblem(Long memberId, String tag, int page, int size){

        JsonNode node = getResponseJsonNode(memberId, tag);
        List<Long> list = new ArrayList<>();
        JsonNode  unsolvedProblemIds = node.get("recommendedProblemIds");

        for(JsonNode id : unsolvedProblemIds){
            list.add(id.asLong());
        }
        log.info("list: {}", list);

        Pageable pageable = PageRequest.of(page, size);
        return problemRepository.findByIds(list, pageable);
    }


    public RecommendGroupDto recommendByTags(Long memberId, String group, int page, int size){
        RecommendGroupDto groupDto = problemRepository.findSolvedProblemIdByGroup(memberId, group);

        return groupDto;
    }

    private JsonNode getJsonNodeByTag(RecommendGroupDto groupDto){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String url = fastApiUrl + "/python/recommend/group";

        HttpEntity<RecommendGroupDto> requestEntity = new HttpEntity<>(groupDto, headers);
        RestTemplate restTemplate = new RestTemplate();


        String response = restTemplate.postForObject(url, requestEntity, String.class);
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            return objectMapper.readTree(response);
        }catch (IOException e){
            log.error("error: {}", e.getMessage());
            throw new CustomException(ErrorCode.JSON_MAPPING_ERROR);
        }
    }

    private JsonNode getResponseJsonNode(Long memberId, String tag){
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(
            ErrorCode.NOT_FOUND_MEMBER));

        String nickname = member.getNickname();

        List<Long> solvedProblemIds = problemRepository.findSolvedProblemIdByTag(memberId, tag);

        String url = fastApiUrl + "/python/recommend";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        RecommendDto recommendDto = RecommendDto.of(nickname, tag, solvedProblemIds);

        HttpEntity<RecommendDto> requestEntity = new HttpEntity<>(recommendDto, headers);
        RestTemplate restTemplate = new RestTemplate();


        String response = restTemplate.postForObject(url, requestEntity, String.class);
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            return objectMapper.readTree(response);
        }catch (IOException e){
            log.error("error: {}", e.getMessage());
            throw new CustomException(ErrorCode.JSON_MAPPING_ERROR);
        }
    }

}