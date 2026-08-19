package com.micro.subject.service;

import com.micro.subject.dto.res.SubjectResDto;
import com.micro.subject.entity.SubjectEntity;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {

    public SubjectResDto toResDto(SubjectEntity entity) {
        return SubjectResDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .color(entity.getColor())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}
