package com.micro.auth.entity;

import com.generic.service.entity.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table(
        name = "subject_entity",
        indexes = {
                @Index(name = "idx_subject_user_id", columnList = "user_id"),
                @Index(name = "idx_subject_user_id_deleted_at", columnList = "user_id, deleted_at"),
                @Index(name = "idx_subject_user_id_title", columnList = "user_id, title")
        }
)
public class SubjectEntity extends GenericEntity {
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(nullable = false, length = 120, unique = true)
    private String title;

    @Column(length = 20)
    private String color;

    @Column(length = 60)
    private String icon;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
