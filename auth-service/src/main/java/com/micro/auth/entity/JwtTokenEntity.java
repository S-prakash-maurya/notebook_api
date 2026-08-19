package com.micro.auth.entity;

import com.generic.service.entity.GenericEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "jwt_token_entity")
public class JwtTokenEntity extends GenericEntity {
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(columnDefinition = "text", nullable = false, updatable = false)
    private String accessToken;

    @Column(columnDefinition = "text", nullable = false, updatable = false)
    private String refreshToken;

    @Column(unique = true, nullable = false, name = "jwt_token_id", columnDefinition = "uuid")
    private UUID jwtTokenId;

    private LocalDateTime lastFetchedAt;
}
