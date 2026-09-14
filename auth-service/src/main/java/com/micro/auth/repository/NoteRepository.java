package com.micro.auth.repository;

import com.generic.service.repository.GenericRepository;
import com.micro.auth.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends GenericRepository<Note> {
    Optional<Note> findByIdAndUserIdAndDeletedFalse(UUID noteId, UUID currentUserId);

    Page<Note> findByUserIdAndDeletedFalse(UUID userId, Pageable pageable);
}
