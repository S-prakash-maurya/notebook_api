package com.micro.auth.repository;

import com.generic.service.repository.GenericRepository;
import com.micro.auth.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonRepository extends GenericRepository<Lesson> {
    @Query("SELECT s FROM Lesson s WHERE s.id = :id AND s.subjectId = :subjectId")
    Optional<Lesson> findActiveByIdAndSubjectId(@Param("id") UUID id, @Param("subjectId") UUID subjectId);

    Page<Lesson> findBySubjectIdAndDeletedFalse(UUID subjectId, Pageable pageable);
}
