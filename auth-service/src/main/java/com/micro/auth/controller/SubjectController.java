package com.micro.auth.controller;

import com.generic.service.dto.GenericPaginationRes;
import com.generic.service.util.RequestContext;
import com.micro.auth.dto.req.SubjectEntityReqDto;
import com.micro.auth.entity.SubjectEntity;
import com.micro.auth.service.SubjectEntityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/subject")
@AllArgsConstructor
public class SubjectController {
    private final SubjectEntityService subjectEntityService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SubjectEntityReqDto subjectEntityReqDto) {
        subjectEntityReqDto.setUserId(RequestContext.getUserFromRequestContextHolder().getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectEntityService.create(subjectEntityReqDto));
    }


    @GetMapping("/{subjectId}")
    public ResponseEntity<?> getCurrentUserSubject(@PathVariable("subjectId") UUID subjectId) {
        return ResponseEntity.status(HttpStatus.OK).body(subjectEntityService.getById(subjectId));
    }

    @GetMapping
    public ResponseEntity<GenericPaginationRes<SubjectEntity>> getPage(@RequestParam(name = "pageNumber", defaultValue = "0") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize, @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortFieldName, @RequestParam(name = "sortOrder", defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(this.subjectEntityService.getAllPage(PageRequest.of(pageNum, pageSize, Sort.by(sortDirection, new String[]{sortFieldName}))));
    }
}
