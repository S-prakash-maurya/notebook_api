package com.micro.auth.repository;

import com.generic.service.repository.GenericRepository;
import com.micro.auth.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends GenericRepository<UserEntity> {
}
