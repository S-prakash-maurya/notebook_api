package com.micro.auth.service;

import com.generic.service.crypto.CryptoService;
import com.generic.service.exception.GenericException;
import com.generic.service.mapper.GenericMapper;
import com.generic.service.repository.GenericRepository;
import com.generic.service.service.impl.GenericService;
import com.micro.auth.dto.req.UserReqDto;
import com.micro.auth.dto.res.UserResDto;
import com.micro.auth.entity.UserEntity;
import com.micro.auth.enums.UserRole;
import com.micro.auth.enums.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService extends GenericService<UserReqDto, UserResDto, UserEntity> {
    private final CryptoService cryptoService;

    public UserService(GenericRepository<UserEntity> repository, CryptoService cryptoService) {
        super(repository, UserResDto.class, UserEntity.class);
        this.cryptoService = cryptoService;
    }

    @Transactional
    @Override
    public UserResDto create(UserReqDto createReq) {
        final Optional<UserEntity> user = super.getByField("email", createReq.getEmail().trim().toLowerCase());
        if (user.isPresent()) {
            throw new GenericException(HttpStatus.IM_USED.value(), "User already exists with email " + createReq.getEmail());
        }
        UserEntity userEntity = GenericMapper.map(createReq, UserEntity.class);
        userEntity
                .setPassword(cryptoService.encrypt(userEntity.getPassword()))
                .setStatus(UserStatus.VERIFICATION_PENDING)
                .setRole(UserRole.MEMBER);
        return super.create(userEntity);
    }
}
