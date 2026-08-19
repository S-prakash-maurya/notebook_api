package com.micro.subject.repository;

import com.micro.subject.entity.SubjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * NOTE: JwtTokenRepository extends the shared GenericRepository<T>
 * (com.generic.service.repository.GenericRepository), which wasn't shown
 * to me, so I don't know what convenience methods it already provides
 * (e.g. a built-in soft-delete-aware finder). To stay correct without
 * guessing its API, this repository extends plain JpaRepository directly
 * and defines explicit, soft-delete-aware queries below. If
 * GenericRepository already gives you equivalent methods, feel free to
 * extend that instead and drop the duplicated ones.
 */
public interface SubjectRepository extends JpaRepository<SubjectEntity, UUID> {

    @Query("SELECT s FROM SubjectEntity s WHERE s.id = :id AND s.userId = :userId AND s.deletedAt IS NULL")
    Optional<SubjectEntity> findActiveByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT s FROM SubjectEntity s WHERE s.userId = :userId AND s.deletedAt IS NULL " +
            "AND (:search IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SubjectEntity> searchActiveByUserId(
            @Param("userId") UUID userId,
            @Param("search") String search,
            Pageable pageable
    );
}
