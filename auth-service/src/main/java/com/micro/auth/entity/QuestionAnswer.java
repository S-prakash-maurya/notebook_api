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
public class QuestionAnswer extends GenericEntity {
    @Column(nullable = false)
    private UUID lessonId;

    @Column(nullable = false, unique = true, columnDefinition = "text")
    private String question;

    @Column(columnDefinition = "text")
    private String answerText;

    @Column(columnDefinition = "text")
    private String answerImageUrl;
}
