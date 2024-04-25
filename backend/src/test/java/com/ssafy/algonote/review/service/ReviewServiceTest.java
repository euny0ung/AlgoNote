package com.ssafy.algonote.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ssafy.algonote.exception.CustomException;
import com.ssafy.algonote.exception.ErrorCode;
import com.ssafy.algonote.member.domain.Member;
import com.ssafy.algonote.member.repository.MemberRepository;
import com.ssafy.algonote.note.domain.Note;
import com.ssafy.algonote.note.repository.NoteRepository;
import com.ssafy.algonote.review.dto.request.ReviewReqDto;
import com.ssafy.algonote.review.repository.ReviewRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    NoteRepository noteRepository;

    @Mock
    MemberRepository memberRepository;

    @Nested
    @DisplayName("리뷰 생성")
    class testCreate {

        private Long noteId;
        private Member member;
        private Note note;
        private ReviewReqDto reviewReqDto;

        @BeforeEach
        void setup() {
            noteId = 1L;
            member = getMember();
            note = getNote();
            reviewReqDto = getReviewReqDto();
        }

        @Test
        @DisplayName("[성공]")
        void success() {
            // given
            willReturn(Optional.of(member)).given(memberRepository).findById(any());
            willReturn(Optional.of(note)).given(noteRepository).findById(any());

            // when
            reviewService.create(reviewReqDto, noteId);

            // then
            verify(reviewRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("[실패] 멤버 조회 실패")
        void fail_member() {
            // given
            willReturn(Optional.empty()).given(memberRepository).findById(any());

            //when
            CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.create(reviewReqDto, noteId));

            //then
            assertEquals(ErrorCode.NOT_FOUND_ID, exception.getErrorCode());
        }

        @Test
        @DisplayName("[실패] 노트 조회 실패")
        void fail_note() {
            // given
            willReturn(Optional.of(member)).given(memberRepository).findById(any());
            willReturn(Optional.empty()).given(noteRepository).findById(any());

            //when
            CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.create(reviewReqDto, noteId));

            //then
            assertEquals(ErrorCode.NOT_FOUND_ID, exception.getErrorCode());
        }

        @Test
        @DisplayName("[실패] endLine이 startLine보다 크다")
        void fail_line() {
            // given
            ReviewReqDto invalidReviewReqDto = new ReviewReqDto(100, 1, "content");
            willReturn(Optional.of(member)).given(memberRepository).findById(any());
            willReturn(Optional.of(note)).given(noteRepository).findById(any());

            //when
            CustomException exception = assertThrows(CustomException.class,
                () -> reviewService.create(invalidReviewReqDto, noteId));

            //then
            assertEquals(ErrorCode.INVALID_LINE_RANGE, exception.getErrorCode());
        }

    }

    ReviewReqDto getReviewReqDto() {
        return new ReviewReqDto(1, 3, "content");
    }

    Note getNote() {
        return Note.builder()
            .id(1L)
            .build();
    }

    Member getMember() {
        return Member.builder()
            .id(1L)
            .build();
    }

}