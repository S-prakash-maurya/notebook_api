package com.micro.subject.service;

import com.micro.subject.config.CurrentUserResolver;
import com.micro.subject.dto.req.CreateSubjectReqDto;
import com.micro.subject.dto.req.UpdateSubjectReqDto;
import com.micro.subject.dto.res.PaginationResDto;
import com.micro.subject.dto.res.SubjectListResDto;
import com.micro.subject.dto.res.SubjectResDto;
import com.micro.subject.entity.SubjectEntity;
import com.micro.subject.exception.SubjectException;
import com.micro.subject.repository.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SubjectService {

    // Only these two columns are exposed for sorting, so a client can't
    // pass an arbitrary/invalid JPA property name into the ORDER BY clause.
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "updatedAt", "title");

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final CurrentUserResolver currentUserResolver;

    @Transactional
    public SubjectResDto create(CreateSubjectReqDto req) {
        UUID userId = currentUserResolver.getCurrentUserId();

        SubjectEntity entity = SubjectEntity.builder()
                .userId(userId)
                .title(req.getTitle().trim())
                .color(req.getColor().trim())
                .icon(req.getIcon().trim())
                .build();

        SubjectEntity saved = subjectRepository.save(entity);
        return subjectMapper.toResDto(saved);
    }

    @Transactional(readOnly = true)
    public SubjectListResDto list(int page, int limit, String search, String sortBy, String sortDir) {
        UUID userId = currentUserResolver.getCurrentUserId();

        int safePage = Math.max(page, 1);
        int safeLimit = Math.min(Math.max(limit, 1), 100); // cap page size to avoid abuse

        String sortField = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(safePage - 1, safeLimit, Sort.by(direction, sortField));
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        Page<SubjectEntity> resultPage = subjectRepository.searchActiveByUserId(userId, normalizedSearch, pageable);

        List<SubjectResDto> data = resultPage.getContent().stream()
                .map(subjectMapper::toResDto)
                .toList();

        PaginationResDto pagination = PaginationResDto.builder()
                .page(safePage)
                .limit(safeLimit)
                .total(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .build();

        return SubjectListResDto.builder().data(data).pagination(pagination).build();
    }

    @Transactional(readOnly = true)
    public SubjectResDto getById(UUID id) {
        UUID userId = currentUserResolver.getCurrentUserId();
        SubjectEntity entity = findOwnedOrThrow(id, userId);
        return subjectMapper.toResDto(entity);
    }

    @Transactional
    public SubjectResDto update(UUID id, UpdateSubjectReqDto req) {
        UUID userId = currentUserResolver.getCurrentUserId();
        SubjectEntity entity = findOwnedOrThrow(id, userId);

        // Only title/color/icon are ever written here. id, userId,
        // createdAt, and deletedAt are never read from the request DTO -
        // UpdateSubjectReqDto doesn't even declare those fields, so there
        // is no code path by which a client could overwrite them.
        entity.setTitle(req.getTitle().trim());
        entity.setColor(req.getColor().trim());
        entity.setIcon(req.getIcon().trim());
        entity.setUpdatedAt(LocalDateTime.now());

        SubjectEntity saved = subjectRepository.save(entity);
        return subjectMapper.toResDto(saved);
    }

    @Transactional
    public void softDelete(UUID id) {
        UUID userId = currentUserResolver.getCurrentUserId();
        SubjectEntity entity = findOwnedOrThrow(id, userId);

        entity.setDeletedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        subjectRepository.save(entity);
    }

    /**
     * Central ownership check. Looks up the subject scoped to BOTH id and
     * the authenticated user's id in a single query - a subject that
     * exists but belongs to someone else returns exactly the same 404 as
     * a subject that doesn't exist at all. This is deliberate: it avoids
     * leaking "this id exists but isn't yours" (an IDOR/enumeration
     * concern) via a 403 vs 404 distinction.
     */
    private SubjectEntity findOwnedOrThrow(UUID id, UUID userId) {
        return subjectRepository.findActiveByIdAndUserId(id, userId)
                .orElseThrow(SubjectException::notFound);
    }
}
