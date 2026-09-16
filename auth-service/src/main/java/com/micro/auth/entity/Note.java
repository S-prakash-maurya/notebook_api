package com.micro.auth.entity;

import com.generic.service.entity.GenericEntity;
import com.micro.auth.enums.NoteType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@Entity
@Table
public class Note extends GenericEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoteType type;

    private String title;

    private String content;

    private String colorValue;

    @Builder.Default
    @Column(nullable = false)
    private Boolean pinned = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean favorite = false;

    @OneToMany(
            mappedBy = "note",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NotePoint> notePoints;
}