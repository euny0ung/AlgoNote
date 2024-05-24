package com.ssafy.algonote.problem.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProblemCustomRepositoryImplTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Test
    public void test(){
        Long memberId = 8L;
        String group = "math_theory";
        problemRepository.findSolvedProblemIdByGroup(memberId, group);
    }


}