package com.micro.subject.controller;

import com.micro.subject.dto.req.CreateSubjectReqDto;
import com.micro.subject.dto.req.UpdateSubjectReqDto;
import com.micro.subject.dto.res.ApiResponse;
import com.micro.subject.dto.res.SubjectListResDto;
import com.micro.subject.dto.res.SubjectResDto;
import com.micro.subject.service.SubjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * All endpoints here run behind the same JwtAuthenticationFilter already
 * used by the auth service (see SecurityConfig: "/auth/**" is
 * permitAll(), everything else requires authentication). No second
 * authentication mechanism is introduced - userId is read exclusively
 * from CurrentUserResolver (backed by RequestContext), never from any
 * request parameter, path variable, or body field.
 */
@RestController
@RequestMapping("/subjects")
@AllArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResDto>> create(@Valid @RequestBody CreateSubjectReqDto req) {
        SubjectResDto created = subjectService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject created successfully", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SubjectListResDto>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        SubjectListResDto result = subjectService.list(page, limit, search, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Subjects fetched successfully", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResDto>> getById(@PathVariable UUID id) {
        SubjectResDto subject = subjectService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Subject fetched successfully", subject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubjectReqDto req
    ) {
        SubjectResDto updated = subjectService.update(id, req);
        return ResponseEntity.ok(ApiResponse.success("Subject updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        subjectService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully", null));
    }
}
