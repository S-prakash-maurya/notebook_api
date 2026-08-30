package com.micro.auth.service;

import com.generic.service.dto.GenericPaginationRes;
import com.generic.service.exception.GenericException;
import com.generic.service.mapper.GenericMapper;
import com.generic.service.repository.GenericRepository;
import com.generic.service.service.impl.GenericService;
import com.generic.service.util.RequestContext;
import com.micro.auth.dto.req.LessonReqDto;
import com.micro.auth.entity.Lesson;
import com.micro.auth.repository.LessonRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.micro.auth.constants.Constants.ANOTHER_ACCESS_ERR_MESSAGE;

@Service
@Log4j2
public class LessonService extends GenericService<LessonReqDto, Lesson, Lesson> {
    private final LessonRepository lessonRepository;
    private final UserService userService;
    private final SubjectEntityService subjectEntityService;

    public LessonService(GenericRepository<Lesson> repository, LessonRepository lessonRepository, UserService userService, SubjectEntityService subjectEntityService) {
        super(repository, Lesson.class, Lesson.class);
        this.lessonRepository = lessonRepository;
        this.userService = userService;
        this.subjectEntityService = subjectEntityService;
    }

    @Override
    public Lesson create(LessonReqDto createReq) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(createReq.getSubjectId());
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        return super.create(createReq);
    }

    @Override
    public Lesson update(LessonReqDto updateReq, UUID id) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(updateReq.getSubjectId());
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        final var lesson = getByIdAndSubjectId(id, updateReq.getSubjectId());
        lesson.setName(updateReq.getName());
        return lessonRepository.saveAndFlush(lesson);
    }

    public Lesson getByIdAndSubjectId(UUID id, UUID subjectId) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(subjectId);
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        return lessonRepository.findActiveByIdAndSubjectId(id, subjectId).orElseThrow(() -> new GenericException(HttpStatus.NOT_FOUND.value(), "Lesson not found"));
    }

    public GenericPaginationRes<Lesson> getAllPage(UUID subjectId, Pageable pageable) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(subjectId);
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        final var tEntityPage = lessonRepository.findBySubjectIdAndDeletedFalse(subjectId, pageable);
        return GenericPaginationRes.<Lesson>builder().totalPages(tEntityPage.getTotalPages()).totalElements(tEntityPage.getNumberOfElements()).pageSize((long) tEntityPage.getSize()).pageNumber(tEntityPage.getNumber()).lastPage(tEntityPage.isLast()).content(tEntityPage.getContent().stream().map((tEntity) -> GenericMapper.map(tEntity, Lesson.class)).toList()).build();
    }

    public Lesson deleteByIdAndSubjectId(UUID id, UUID subjectId) {
        final var user = userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId());
        final var subject = subjectEntityService.getById(subjectId);
        if (!user.getId().equals(subject.getUserId())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), ANOTHER_ACCESS_ERR_MESSAGE);
        }
        return super.deleteHard(id);
    }
}
