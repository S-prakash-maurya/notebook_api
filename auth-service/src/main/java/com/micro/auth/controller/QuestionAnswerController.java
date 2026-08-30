package com.micro.auth.controller;

import com.generic.service.dto.GenericPaginationRes;
import com.micro.auth.dto.req.QuestionAnswerReqDto;
import com.micro.auth.entity.QuestionAnswer;
import com.micro.auth.service.QuestionAnswerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/question")
@AllArgsConstructor
@Log4j2
public class QuestionAnswerController {
    private final QuestionAnswerService questionAnswerService;

    @PostMapping("/{subjectId}")
    public ResponseEntity<?> create(@PathVariable("subjectId") UUID subjectId, @Valid @RequestBody QuestionAnswerReqDto questionAnswerReqDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionAnswerService.create(subjectId, questionAnswerReqDto));
    }

    @PutMapping("/{id}/{subjectId}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @PathVariable("subjectId") UUID subjectId, @Valid @RequestBody QuestionAnswerReqDto questionAnswerReqDto) {
        return ResponseEntity.status(HttpStatus.OK).body(questionAnswerService.update(questionAnswerReqDto, id, subjectId));
    }

    @GetMapping("/{id}/{subjectId}/{lessonId}")
    public ResponseEntity<?> getById(@PathVariable("id") UUID id, @PathVariable("subjectId") UUID subjectId, @PathVariable("lessonId") UUID lessonId) {
        return ResponseEntity.status(HttpStatus.OK).body(questionAnswerService.getByIdAndLessonId(id, subjectId, lessonId));
    }

    @DeleteMapping("/{id}/{subjectId}/{lessonId}")
    public ResponseEntity<?> deleteById(@PathVariable("id") UUID id, @PathVariable("subjectId") UUID subjectId, @PathVariable("lessonId") UUID lessonId) {
        return ResponseEntity.status(HttpStatus.OK).body(questionAnswerService.deleteByIdAndLessonId(id, subjectId, lessonId));
    }

    @GetMapping("/get-all-page/{subjectId}/{lessonId}")
    public ResponseEntity<GenericPaginationRes<QuestionAnswer>> getPage(@PathVariable("subjectId") UUID subjectId, @PathVariable("lessonId") UUID lessonId, @RequestParam(name = "pageNumber", defaultValue = "0") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize, @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortFieldName, @RequestParam(name = "sortOrder", defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(this.questionAnswerService.getAllPage(subjectId, lessonId, PageRequest.of(pageNum, pageSize, Sort.by(sortDirection, new String[]{sortFieldName}))));
    }
}
