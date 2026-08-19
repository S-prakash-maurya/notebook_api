package com.micro.auth.service;

import com.generic.service.repository.GenericRepository;
import com.generic.service.service.impl.GenericService;
import com.micro.auth.dto.req.UserReqDto;
import com.micro.auth.entity.JwtTokenEntity;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService extends GenericService<UserReqDto, JwtTokenEntity, JwtTokenEntity> {
    public JwtTokenService(GenericRepository<JwtTokenEntity> repository) {
        super(repository, JwtTokenEntity.class, JwtTokenEntity.class);
    }
}
