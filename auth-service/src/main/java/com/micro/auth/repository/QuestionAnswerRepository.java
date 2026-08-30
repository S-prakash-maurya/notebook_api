package com.micro.auth.repository;

import com.generic.service.repository.GenericRepository;
import com.micro.auth.entity.Lesson;
import com.micro.auth.entity.QuestionAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionAnswerRepository extends GenericRepository<QuestionAnswer> {
    @Query("SELECT s FROM QuestionAnswer s WHERE s.id = :id AND s.lessonId = :lessonId")
    Optional<QuestionAnswer> findActiveByIdAndLessonId(@Param("id") UUID id, @Param("lessonId") UUID lessonId);

    Page<QuestionAnswer> findByLessonIdAndDeletedFalse(UUID lessonId, Pageable pageable);
}
