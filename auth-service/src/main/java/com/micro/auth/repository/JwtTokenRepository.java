package com.micro.auth.repository;

import com.generic.service.repository.GenericRepository;
import com.micro.auth.entity.JwtTokenEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends GenericRepository<JwtTokenEntity> {
}
