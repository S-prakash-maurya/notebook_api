package com.micro.auth.service;

import com.generic.service.crypto.CryptoService;
import com.generic.service.exception.GenericException;
import com.micro.auth.dto.req.LoginReqDto;
import com.micro.auth.dto.req.RefreshTokenReqDto;
import com.micro.auth.dto.res.TokenResDto;
import com.micro.auth.dto.res.UserResDto;
import com.micro.auth.entity.JwtTokenEntity;
import com.micro.auth.entity.UserEntity;
import com.micro.auth.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Log4j2
public class AuthenticationService {
    private final CryptoService cryptoService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenService jwtTokenService;


    public TokenResDto login(LoginReqDto loginReqDto) {
        UserEntity userEntity = userService.getByField("email", loginReqDto.getEmail()).orElseThrow(() -> new GenericException(HttpStatus.EXPECTATION_FAILED.value(), "Bad credentials"));
        if (userEntity.getStatus() != UserStatus.ACTIVE) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), "User id not activated");
        }
        if (!cryptoService.matches(loginReqDto.getPassword(), userEntity.getPassword())) {
            throw new GenericException(HttpStatus.EXPECTATION_FAILED.value(), "Bad credentials");
        }
        return jwtTokenProvider.createToken(userEntity.getId().toString());
    }

    @Transactional
    public TokenResDto refreshToken(RefreshTokenReqDto refreshTokenReqDto) {
        if (Boolean.FALSE.equals(jwtTokenProvider.validateToken(refreshTokenReqDto.getRefreshToken())) || Boolean.FALSE.equals(jwtTokenProvider.isRefreshToken(refreshTokenReqDto.getRefreshToken()))) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "Refresh token invalid or expired");
        }
        JwtTokenEntity jwtTokenEntity = jwtTokenService.getByField("jwtTokenId", UUID.fromString(jwtTokenProvider.getJwtTokenId(refreshTokenReqDto.getRefreshToken()))).orElseThrow(() -> new GenericException(HttpStatus.UNAUTHORIZED.value(), "Refresh token not found"));
        UserResDto user = userService.getById(UUID.fromString(jwtTokenProvider.getUserId(refreshTokenReqDto.getRefreshToken())));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "User not active");
        }
        jwtTokenService.deleteHard(jwtTokenEntity.getId());
        return jwtTokenProvider.createToken(user.getId().toString());
    }
}
