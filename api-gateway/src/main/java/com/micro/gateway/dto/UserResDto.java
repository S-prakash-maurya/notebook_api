package com.micro.gateway.dto;

import com.micro.gateway.enums.UserRole;
import com.micro.gateway.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserResDto extends GenericBaseDto {
    private String email;
    private String name;
    private UserRole role;
    private UserStatus status;
}
