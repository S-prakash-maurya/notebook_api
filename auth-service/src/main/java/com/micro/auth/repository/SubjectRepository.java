package com.micro.auth.repository;

import com.generic.service.repository.GenericRepository;
import com.micro.auth.entity.SubjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends GenericRepository<SubjectEntity> {
    @Query("SELECT s FROM SubjectEntity s WHERE s.id = :id AND s.userId = :userId AND s.deletedAt IS NULL")
    Optional<SubjectEntity> findActiveByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    Page<SubjectEntity> findByUserIdAndDeletedFalse(UUID userId, Pageable pageable);
}
