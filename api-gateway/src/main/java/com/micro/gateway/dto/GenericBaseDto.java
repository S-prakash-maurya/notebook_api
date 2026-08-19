package com.micro.gateway.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GenericBaseDto {
    private UUID id;
    private UUID tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
    private UUID createdBy;
}
