package com.ssafy.algonote.problem.repository;

import static com.ssafy.algonote.problem.domain.QProblem.problem;
import static com.ssafy.algonote.problem.domain.QSolvedProblem.solvedProblem;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.algonote.problem.domain.Problem;
import com.ssafy.algonote.recommend.dto.RecommendGroupDto;
import com.ssafy.algonote.recommend.dto.RecommendTagDto;
import com.ssafy.algonote.recommend.dto.response.RecommendProblemResDto;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class ProblemCustomRepositoryImpl implements ProblemCustomRepository {
    private final JPAQueryFactory queryFactory;
    private Set<String> algorithmTags = new HashSet<>(Arrays.asList(
        "simulation", "implementation", "math", "graphs", "graph_traversal", "bfs", "dp",
        "topological_sorting", "dfs", "backtracking", "sorting", "bitmask", "bruteforcing",
        "data_structures", "priority_queue", "greedy", "string", "tree_set", "knapsack",
        "arithmetic", "shortest_path", "floyd_warshall", "two_pointer", "binary_search",
        "stack", "sliding_window", "divide_and_conquer", "recursion", "dijkstra",
        "parametric_search", "deque", "queue", "mst", "kmp", "sieve", "primality_test",
        "prefix_sum", "segtree", "tsp", "hashing", "trie", "lca", "rabin_karp", "manacher"
    ));

    public ProblemCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);

    }

    @Override
    public List<Long> findSolvedProblemIdByTag(Long memberId, String tag) {
        return queryFactory.select(solvedProblem.problem.id)
            .from(solvedProblem)
            .where(solvedProblem.member.id.eq(memberId))
            .join(solvedProblem.problem)
            .where(solvedProblem.problem.tags.any().nameEn.eq(tag))
            .fetch();
    }

    @Override
    @Transactional
    public RecommendGroupDto findSolvedProblemIdByGroup(Long memberId, String group) {
        List<Problem> problems =  queryFactory.select(solvedProblem.problem)
            .from(solvedProblem)
            .where(solvedProblem.member.id.eq(memberId))
            .join(solvedProblem.problem)
            .where(solvedProblem.problem.tags.any().group.eq(group))
            .fetch();

        Map<String, Set<Long>> tagNames = new HashMap<>();
        problems.forEach(problem -> {
            problem.getTags().forEach(tag -> {
                if(!tagNames.containsKey(tag.getNameEn()) && algorithmTags.contains(tag.getNameEn())){
                    tagNames.put(tag.getNameEn(), new HashSet<>());
                }
            });
        });

        for (String s : tagNames.keySet()) {
            problems.forEach(problem -> {
                problem.getTags().forEach(tag -> {
                    if(tag.getNameEn().equals(s)){
                        tagNames.get(s).add(problem.getId());
                    }
                });
            });
        }

        List<RecommendTagDto> tagDtos = new ArrayList<>();
        for(String key : tagNames.keySet()){
            tagDtos.add(new RecommendTagDto(key, tagNames.get(key)));
        }

        return RecommendGroupDto.of(group, tagDtos);

    }


    @Override
    public Page<RecommendProblemResDto> findByIds(List<Long> ids, Pageable pageable) {
        List<RecommendProblemResDto> results = queryFactory
            .select(Projections.constructor(RecommendProblemResDto.class,
                problem.id,
                problem.tier,
                problem.title,
                problem.acceptedUserCount,
                problem.averageTries
            ))
            .from(problem)
            .where(problem.id.in(ids))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(problem.acceptedUserCount.desc())
            .fetch();

        long totalCount = queryFactory
            .select(problem.count())
            .from(problem)
            .where(problem.id.in(ids))
            .fetchOne();
        return new PageImpl<>(results, pageable, totalCount);
    }
}
