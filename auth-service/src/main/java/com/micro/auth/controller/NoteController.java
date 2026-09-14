package com.micro.auth.controller;

import com.generic.service.dto.GenericPaginationRes;
import com.micro.auth.dto.req.CreateNoteRequest;
import com.micro.auth.dto.res.NoteResponse;
import com.micro.auth.entity.SubjectEntity;
import com.micro.auth.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/note")
@AllArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateNoteRequest createNoteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.create(createNoteRequest));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<?> update(@PathVariable UUID noteId, @RequestBody CreateNoteRequest request) {
        return ResponseEntity.ok(noteService.update(noteId, request));
    }


    @GetMapping
    public ResponseEntity<GenericPaginationRes<NoteResponse>> getPage(@RequestParam(name = "pageNumber", defaultValue = "0") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize, @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortFieldName, @RequestParam(name = "sortOrder", defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(this.noteService.getAllPage(PageRequest.of(pageNum, pageSize, Sort.by(sortDirection, new String[]{sortFieldName}))));
    }

}
