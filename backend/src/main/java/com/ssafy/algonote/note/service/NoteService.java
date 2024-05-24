package com.ssafy.algonote.note.service;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import com.ssafy.algonote.exception.CustomException;
import com.ssafy.algonote.exception.ErrorCode;
import com.ssafy.algonote.member.domain.Member;
import com.ssafy.algonote.member.repository.MemberRepository;
import com.ssafy.algonote.note.domain.Note;
import com.ssafy.algonote.note.domain.NoteDocument;
import com.ssafy.algonote.note.domain.TempNote;
import com.ssafy.algonote.note.dto.response.NoteSearchDto;
import com.ssafy.algonote.note.dto.response.TempNoteResDto;
import com.ssafy.algonote.note.repository.*;
import com.ssafy.algonote.problem.domain.ProblemDocument;
import com.ssafy.algonote.problem.domain.SolvedProblem;
import com.ssafy.algonote.problem.domain.WritingStatus;
import com.ssafy.algonote.problem.repository.ProblemDocumentRepository;
import com.ssafy.algonote.problem.repository.SolvedProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.elasticsearch.client.elc.Queries.matchAllQuery;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final MemberRepository memberRepository;
    private final SolvedProblemRepository solvedProblemRepository;
    private final HeartRepository heartRepository;
    private final BookmarkRepository bookmarkRepository;
    private final NoteDocumentRepository noteDocumentRepository;
    private final ProblemDocumentRepository problemDocumentRepository;
    private final TempNoteRepository tempNoteRepository;
    private final ReviewRepository reviewRepository;
    private final ElasticsearchOperations operations;

    // 노트 생성
    public void saveNote(Long memberId, Long problemId, String title, String content, Long tempNoteId) {
        Member member = getMemberOrException(memberId);
        SolvedProblem solvedProblem = getSolvedProblemOrException(memberId, problemId);
        if (tempNoteId != null) {
            TempNote tempNote = getTempNoteOrException(tempNoteId);
            tempNoteRepository.delete(tempNote);
        }

        Note note = noteRepository.save(Note.of(member, solvedProblem.getProblem(), title.trim(), content));
        noteDocumentRepository.save(NoteDocument.of(
                note.getId(),
                member.getNickname(),
                String.valueOf(solvedProblem.getProblem().getId()),
                solvedProblem.getProblem().getTitle(),
                title.trim(),
                content
        ));
        // solved_problem 노트 작성 상태 수정
        if (solvedProblem.getComplete() == WritingStatus.NOT_YET) {
            solvedProblem.setComplete(WritingStatus.DONE);
        }
    }


    // 노트 삭제
    public void deleteNote(Long memberId, Long noteId) {
        Member member = getMemberOrException(memberId);
        Note note = getNoteOrException(noteId);
        NoteDocument noteDocument = getNoteDocumentOrException(noteId);

        if (note.getMember() != member) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        Long problemId = note.getProblem().getId();
        bookmarkRepository.deleteAllByNote(note);
        heartRepository.deleteAllByNote(note);
        reviewRepository.deleteAllByNote(note);
        noteRepository.delete(note);
        noteDocumentRepository.delete(noteDocument);

        List<Note> notes = noteRepository.findByMember_IdAndProblem_Id(memberId, problemId);
        if(notes.isEmpty()){
            SolvedProblem solvedProblem = getSolvedProblemOrException(memberId, problemId);
            solvedProblem.setComplete(WritingStatus.NOT_YET);
        }
    }

    // 노트 id로 조회
    @Transactional(readOnly = true)
    public Note getNoteById(Long noteId) {
        return getNoteOrException(noteId);
    }

    // 사용자별로 노트 조회 & 내가 작성한 노트 조회
    @Transactional(readOnly = true)
    public List<Note> getNotesByMember(Long memberId) {
        Member member = getMemberOrException(memberId);
        return noteRepository.findByMember(member);
    }

    // 노트 수정
    @Transactional
    public Note update(Long memberId, Long noteId, String title, String content) {
        Member member = getMemberOrException(memberId);
        Note note = getNoteOrException(noteId);
        NoteDocument noteDocument = getNoteDocumentOrException(noteId);

        if (note.getMember() != member) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        if (title != null) {
            note.setTitle(title);
            noteDocument.setNoteTitle(title);
        }

        if (content != null) {
            note.setContent(content);
            noteDocument.setContent(content);
        }
        noteDocumentRepository.save(noteDocument);
        return noteRepository.saveAndFlush(note);
    }

    public List<NoteSearchDto> fulltextNoteSearch(String keyword, Pageable pageable) {

        SearchHits<NoteDocument> noteHits = searchNoteDocument(keyword, pageable);
        return parseSearchHits(noteHits);
    }

    public List<Note> getNotesByProblem(Long memberId, Long problemId) {
        return noteRepository.findByMember_IdAndProblem_Id(memberId, problemId);
    }

    public Long countAllNotes(){
        return noteRepository.count();
    }
    public List<NoteSearchDto> getAllNotes(Pageable pageable){
//        MatchAllQuery matchAllQuery = matchAllQuery();
//        Query query = createQuery(List.of(matchAllQuery._toQuery()));
//
//        query.setPageable(pageable);
//
//        return parseSearchHits(operations.search(query, NoteDocument.class));

        return noteRepository.searchAll(pageable);
    }

    private SearchHits<NoteDocument> searchNoteDocument(String keyword, Pageable pageable) {

//        MatchQuery problemIdMatch = MatchQuery.of(m -> m.field("problemId").query(keyword));
//        MatchQuery problemTitleMatch = MatchQuery.of(m -> m.field("problemTitle").query(keyword));
//        MatchQuery noteTitleMatch = MatchQuery.of(m -> m.field("noteTitle").query(keyword));
//        Query query = createQuery(Arrays.asList(problemIdMatch._toQuery(), problemTitleMatch._toQuery(), noteTitleMatch._toQuery()));

        Criteria noteCriteria = new Criteria().or()
                .subCriteria(new Criteria("problemId").matches(keyword))
                .subCriteria(new Criteria("noteTitle").matches(keyword))
                .subCriteria(new Criteria("problemTitle").matches(keyword))
                .subCriteria(new Criteria("memberNickname").matches(keyword));

        CriteriaQuery query = new CriteriaQuery(noteCriteria);

        query.setPageable(pageable);

        return operations.search(query, NoteDocument.class);
}

@Transactional
    public List<NoteSearchDto> parseSearchHits(SearchHits<NoteDocument> noteHits) {
        List<NoteSearchDto> noteSearchResults = new ArrayList<>();

        for (SearchHit<NoteDocument> hit : noteHits) {
            NoteDocument noteDocument = hit.getContent();
            ProblemDocument problemDocument = problemDocumentRepository.findById(
                    noteDocument.getProblemId()).orElse(null);
            int tier = problemDocument.getTier();

            noteSearchResults.add(NoteSearchDto.of(
                    noteDocument,
                    tier
            ));
        }

        return noteSearchResults;

    }

    private Query createQuery(List<co.elastic.clients.elasticsearch._types.query_dsl.Query> queries) {
        return new NativeQueryBuilder()
                .withQuery(
                        q -> q.bool(
                                b -> b.should(
                                        queries
                                )))
                .build();

    }

    // 임시 노트 저장(리턴 있음)
    public TempNoteResDto saveTempNote(Long memberId, Long problemId, String title, String content) {
        Member member = getMemberOrException(memberId);
        SolvedProblem solvedProblem = getSolvedProblemOrException(memberId, problemId);
        return TempNoteResDto.from(tempNoteRepository.save(TempNote.of(member, solvedProblem.getProblem(), title.trim(), content)));
    }

    // 임시 노트 수정
    public TempNoteResDto updateTempNote(Long memberId, Long tempNoteId, String title, String content) {
        Member member = getMemberOrException(memberId);
        TempNote tempNote = getTempNoteOrException(tempNoteId);

        if (tempNote.getMember() != member) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }
        if (title != null) {
            tempNote.setTitle(title);
        }
        if (content != null) {
            tempNote.setContent(content);
        }
        return TempNoteResDto.from(tempNoteRepository.saveAndFlush(tempNote));

    }

    // 임시 노트 삭제
    public void deleteTempNote(Long memberId, Long tempNoteId) {
        Member member = getMemberOrException(memberId);
        TempNote note = getTempNoteOrException(tempNoteId);

        if (note.getMember() != member) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }
        tempNoteRepository.delete(note);
    }

    // 문제에 해당하는 임시 노트 전체 조회(해당 문제에 존재하는)
    public List<TempNoteResDto> getTempNoteList(Long memberId, Long problemId) {
        getMemberOrException(memberId);
        return tempNoteRepository.findAllByMember_IdAndProblem_Id(memberId, problemId)
                .stream()
                .map(TempNoteResDto::from)
                .collect(Collectors.toList());
    }


    private Note getNoteOrException(Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTE));
    }

    private TempNote getTempNoteOrException(Long tempNoteId) {
        return tempNoteRepository.findById(tempNoteId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_TEMP_NOTE));
    }

    private NoteDocument getNoteDocumentOrException(Long noteId) {
        return noteDocumentRepository.findById(noteId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ES_NOTE));
    }

    private Member getMemberOrException(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    private SolvedProblem getSolvedProblemOrException(Long memberId, Long problemId) {
        return solvedProblemRepository.findByMember_IdAndProblem_Id(memberId, problemId)
                .orElseThrow(() -> new CustomException((ErrorCode.NOT_SOLVED)));
    }
}
