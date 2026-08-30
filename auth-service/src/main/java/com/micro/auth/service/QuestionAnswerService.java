package com.micro.auth.service;

import com.generic.service.dto.GenericPaginationRes;
import com.generic.service.exception.GenericException;
import com.generic.service.mapper.GenericMapper;
import com.generic.service.repository.GenericRepository;
import com.generic.service.service.impl.GenericService;
import com.generic.service.util.RequestContext;
import com.micro.auth.dto.req.QuestionAnswerReqDto;
import com.micro.auth.entity.QuestionAnswer;
import com.micro.auth.repository.QuestionAnswerRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.micro.auth.constants.Constants.ANOTHER_ACCESS_ERR_MESSAGE;

@Service
@Log4j2
public class QuestionAnswerService extends GenericService<QuestionAnswerReqDto, QuestionAnswer, QuestionAnswer> {
    private final LessonService lessonService;
    private final UserService userService;
    private final SubjectEntityService subjectEntityService;
    private final QuestionAnswerRepository questionAnswerRepository;

    public QuestionAnswerService(GenericRepository<QuestionAnswer> repository, LessonService lessonService, UserService userService, SubjectEntityService subjectEntityService, QuestionAnswerRepository questionAnswerRepository) {
        super(repository, QuestionAnswer.class, QuestionAnswer.class);
        this.lessonService = lessonService;
        this.userService = userService;
        this.subjectEntityService = subjectEntityService;
        this.questionAnswerRepository = questionAnswerRepository;
    }


    public QuestionAnswer create(UUID subjectId, QuestionAnswerReqDto createReq) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(subjectId);
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        // just for validating the lesson and subject id
        lessonService.getByIdAndSubjectId(createReq.getLessonId(), subjectId);
        return super.create(createReq);
    }


    public QuestionAnswer update(QuestionAnswerReqDto updateReq, UUID id, UUID subjectId) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(subjectId);
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        // just for validating the lesson and subject id
        lessonService.getByIdAndSubjectId(updateReq.getLessonId(), subjectId);
        final var questionAns = questionAnswerRepository.findActiveByIdAndLessonId(id, updateReq.getLessonId())
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Question not found"));
        questionAns.setQuestion(updateReq.getQuestion())
                .setAnswerText(updateReq.getAnswerText())
                .setAnswerImageUrl(updateReq.getAnswerImageUrl());
        return questionAnswerRepository.saveAndFlush(questionAns);
    }

    public QuestionAnswer getByIdAndLessonId(UUID id, UUID subjectId, UUID lessonId) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(subjectId);
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        // just for validating the lesson and subject id
        lessonService.getByIdAndSubjectId(lessonId, subjectId);
        return questionAnswerRepository.findActiveByIdAndLessonId(id, lessonId)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Question not found"));
    }

    public GenericPaginationRes<QuestionAnswer> getAllPage(UUID subjectId, UUID lessonId, Pageable pageable) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(subjectId);
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        // just for validating the lesson and subject id
        lessonService.getByIdAndSubjectId(lessonId, subjectId);
        var tEntityPage = questionAnswerRepository.findByLessonIdAndDeletedFalse(lessonId, pageable);
        return GenericPaginationRes.<QuestionAnswer>builder().totalPages(tEntityPage.getTotalPages()).totalElements(tEntityPage.getNumberOfElements()).pageSize((long) tEntityPage.getSize()).pageNumber(tEntityPage.getNumber()).lastPage(tEntityPage.isLast()).content(tEntityPage.getContent().stream().map((tEntity) -> GenericMapper.map(tEntity, QuestionAnswer.class)).toList()).build();
    }

    public QuestionAnswer deleteByIdAndLessonId(UUID id, UUID subjectId, UUID lessonId) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(subjectId);
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        // just for validating the lesson and subject id
        lessonService.getByIdAndSubjectId(lessonId, subjectId);
        final var questionAns = questionAnswerRepository.findActiveByIdAndLessonId(id, lessonId)
                .orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Question not found"));
        return super.deleteHard(questionAns.getId());
    }
}
