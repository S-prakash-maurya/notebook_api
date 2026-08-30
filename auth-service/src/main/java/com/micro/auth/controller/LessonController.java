package com.micro.auth.controller;

import com.generic.service.dto.GenericPaginationRes;
import com.micro.auth.dto.req.LessonReqDto;
import com.micro.auth.entity.Lesson;
import com.micro.auth.service.LessonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@RequestMapping("/lesson")
@AllArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LessonReqDto lessonReqDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.create(lessonReqDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @Valid @RequestBody LessonReqDto lessonReqDto) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.update(lessonReqDto, id));
    }

    @GetMapping("/{id}/{subjectId}")
    public ResponseEntity<?> getByIdAndSubjectId(@PathVariable("id") UUID id, @PathVariable("subjectId") UUID subjectId) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.getByIdAndSubjectId(id, subjectId));
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<GenericPaginationRes<Lesson>> getPage(@PathVariable("subjectId") UUID subjectId, @RequestParam(name = "pageNumber", defaultValue = "0") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize, @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortFieldName, @RequestParam(name = "sortOrder", defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(this.lessonService.getAllPage(subjectId, PageRequest.of(pageNum, pageSize, Sort.by(sortDirection, new String[]{sortFieldName}))));
    }

    @DeleteMapping("/{id}/{subjectId}")
    public ResponseEntity<?> deleteByIdAndSubjectId(@PathVariable("id") UUID id, @PathVariable("subjectId") UUID subjectId) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.deleteByIdAndSubjectId(id, subjectId));
    }
}
