package com.ssafy.algonote.problem.repository;

import com.ssafy.algonote.problem.domain.Problem;
import com.ssafy.algonote.problem.domain.SolvedProblem;
import com.ssafy.algonote.problem.dto.response.AnalysisResDto;
import java.util.List;

public interface SolvedProblemCustomRepository {
   AnalysisResDto analyzeSolvedProblem(Long memberId);
   List<Long> findSolvedProblemIdByTag(Long memberId, String tag);

}
