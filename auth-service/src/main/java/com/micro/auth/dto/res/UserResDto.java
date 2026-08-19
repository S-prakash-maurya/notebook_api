package com.micro.auth.dto.res;

import com.generic.service.entity.GenericEntity;
import com.micro.auth.enums.UserRole;
import com.micro.auth.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserResDto extends GenericEntity {
    private String email;
    private String name;
    private UserRole role;
    private UserStatus status;
}
