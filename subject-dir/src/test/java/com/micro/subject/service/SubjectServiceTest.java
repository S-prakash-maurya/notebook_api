package com.micro.subject.service;

import com.micro.subject.config.CurrentUserResolver;
import com.micro.subject.dto.req.CreateSubjectReqDto;
import com.micro.subject.dto.req.UpdateSubjectReqDto;
import com.micro.subject.dto.res.SubjectListResDto;
import com.micro.subject.dto.res.SubjectResDto;
import com.micro.subject.entity.SubjectEntity;
import com.micro.subject.exception.SubjectException;
import com.micro.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private CurrentUserResolver currentUserResolver;

    private SubjectMapper subjectMapper;
    private SubjectService subjectService;

    private final UUID ownerId = UUID.randomUUID();
    private final UUID otherUserId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        subjectMapper = new SubjectMapper();
        subjectService = new SubjectService(subjectRepository, subjectMapper, currentUserResolver);
    }

    private SubjectEntity sampleEntity() {
        SubjectEntity entity = SubjectEntity.builder()
                .userId(ownerId)
                .title("Mathematics")
                .color("#4CAF50")
                .icon("calculate")
                .build();
        entity.setId(subjectId);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    // 1. create subject
    @Test
    void create_savesSubjectUnderAuthenticatedUser() {
        when(currentUserResolver.getCurrentUserId()).thenReturn(ownerId);
        when(subjectRepository.save(any(SubjectEntity.class))).thenAnswer(inv -> {
            SubjectEntity e = inv.getArgument(0);
            e.setId(subjectId);
            e.setCreatedAt(LocalDateTime.now());
            e.setUpdatedAt(LocalDateTime.now());
            return e;
        });

        CreateSubjectReqDto req = new CreateSubjectReqDto();
        req.setTitle("Mathematics");
        req.setColor("#4CAF50");
        req.setIcon("calculate");

        SubjectResDto result = subjectService.create(req);

        assertThat(result.getUserId()).isEqualTo(ownerId);
        assertThat(result.getTitle()).isEqualTo("Mathematics");

        verify(subjectRepository).save(argThat(saved -> saved.getUserId().equals(ownerId)));
    }

    // 2. get user's subjects (list is scoped to the authenticated user)
    @Test
    void list_returnsOnlyAuthenticatedUsersSubjects() {
        when(currentUserResolver.getCurrentUserId()).thenReturn(ownerId);
        Page<SubjectEntity> page = new PageImpl<>(List.of(sampleEntity()));
        when(subjectRepository.searchActiveByUserId(eq(ownerId), any(), any(Pageable.class))).thenReturn(page);

        SubjectListResDto result = subjectService.list(1, 20, null, "createdAt", "desc");

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getPagination().getTotal()).isEqualTo(1);
        verify(subjectRepository).searchActiveByUserId(eq(ownerId), any(), any(Pageable.class));
    }

    // 3. get single subject
    @Test
    void getById_returnsSubjectWhenOwnedByUser() {
        when(currentUserResolver.getCurrentUserId()).thenReturn(ownerId);
        when(subjectRepository.findActiveByIdAndUserId(subjectId, ownerId))
                .thenReturn(Optional.of(sampleEntity()));

        SubjectResDto result = subjectService.getById(subjectId);

        assertThat(result.getId()).isEqualTo(subjectId);
    }

    // 4. update subject
    @Test
    void update_changesTitleColorIconAndBumpsUpdatedAt() {
        SubjectEntity existing = sampleEntity();
        LocalDateTime originalUpdatedAt = existing.getUpdatedAt();

        when(currentUserResolver.getCurrentUserId()).thenReturn(ownerId);
        when(subjectRepository.findActiveByIdAndUserId(subjectId, ownerId)).thenReturn(Optional.of(existing));
        when(subjectRepository.save(any(SubjectEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateSubjectReqDto req = new UpdateSubjectReqDto();
        req.setTitle("Advanced Mathematics");
        req.setColor("#000000");
        req.setIcon("functions");

        SubjectResDto result = subjectService.update(subjectId, req);

        assertThat(result.getTitle()).isEqualTo("Advanced Mathematics");
        assertThat(result.getColor()).isEqualTo("#000000");
        assertThat(result.getIcon()).isEqualTo("functions");
        assertThat(result.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    // 5. soft delete subject
    @Test
    void softDelete_setsDeletedAtInsteadOfRemovingRow() {
        SubjectEntity existing = sampleEntity();
        when(currentUserResolver.getCurrentUserId()).thenReturn(ownerId);
        when(subjectRepository.findActiveByIdAndUserId(subjectId, ownerId)).thenReturn(Optional.of(existing));
        when(subjectRepository.save(any(SubjectEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        subjectService.softDelete(subjectId);

        verify(subjectRepository).save(argThat(saved -> saved.getDeletedAt() != null));
        // Never a hard delete:
        verify(subjectRepository, never()).delete(any());
        verify(subjectRepository, never()).deleteById(any());
    }

    // 6. unauthorized request (no authenticated user resolvable)
    @Test
    void getById_throwsUnauthorizedWhenNoAuthenticatedUser() {
        when(currentUserResolver.getCurrentUserId()).thenThrow(SubjectException.unauthorized());

        assertThatThrownBy(() -> subjectService.getById(subjectId))
                .isInstanceOf(SubjectException.class)
                .hasMessage("Unauthorized");
    }

    // 7. accessing another user's subject -> 404, not 403 (avoids leaking existence)
    @Test
    void getById_returnsNotFoundWhenSubjectBelongsToAnotherUser() {
        when(currentUserResolver.getCurrentUserId()).thenReturn(otherUserId);
        // The repository query is scoped by (id, userId) together, so a
        // subject owned by someone else simply won't be found for otherUserId.
        when(subjectRepository.findActiveByIdAndUserId(subjectId, otherUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.getById(subjectId))
                .isInstanceOf(SubjectException.class)
                .hasMessage("Subject not found");
    }

    @Test
    void update_returnsNotFoundWhenSubjectBelongsToAnotherUser() {
        when(currentUserResolver.getCurrentUserId()).thenReturn(otherUserId);
        when(subjectRepository.findActiveByIdAndUserId(subjectId, otherUserId)).thenReturn(Optional.empty());

        UpdateSubjectReqDto req = new UpdateSubjectReqDto();
        req.setTitle("Hacked title");
        req.setColor("#000000");
        req.setIcon("bug");

        assertThatThrownBy(() -> subjectService.update(subjectId, req))
                .isInstanceOf(SubjectException.class)
                .hasMessage("Subject not found");
    }

    @Test
    void softDelete_returnsNotFoundWhenSubjectBelongsToAnotherUser() {
        when(currentUserResolver.getCurrentUserId()).thenReturn(otherUserId);
        when(subjectRepository.findActiveByIdAndUserId(subjectId, otherUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.softDelete(subjectId))
                .isInstanceOf(SubjectException.class)
                .hasMessage("Subject not found");
    }
}
