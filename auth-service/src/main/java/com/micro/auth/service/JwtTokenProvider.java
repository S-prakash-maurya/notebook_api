package com.micro.auth.service;

import com.generic.service.exception.GenericException;
import com.generic.service.util.GenericUtil;
import com.micro.auth.constants.Constants;
import com.micro.auth.dto.res.TokenResDto;
import com.micro.auth.entity.JwtTokenEntity;
import com.micro.auth.properties.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Log4j2
@EnableConfigurationProperties(ApplicationProperties.class)
public class JwtTokenProvider {
    private final ApplicationProperties applicationProperties;
    private final JwtTokenService jwtTokenService;

    public TokenResDto createToken(String userId) {
        String tokenId = GenericUtil.generateTimeBaseUUID().toString();
        TokenResDto tokenResDto = TokenResDto.builder().accessToken(generateAccessToken(userId, tokenId)).refreshToken(generateRefreshToken(userId, tokenId)).build();
        jwtTokenService.create(JwtTokenEntity.builder()
                .accessToken(tokenResDto.getAccessToken())
                .jwtTokenId(UUID.fromString(tokenId))
                .refreshToken(tokenResDto.getRefreshToken())
                .userId(UUID.fromString(userId))
                .build());
        return tokenResDto;
    }

    private String generateAccessToken(String userId, String tokenId) {
        Map<String, String> claims = new HashMap<>();
        claims.put(Constants.TOKEN_TYPE, Constants.ACCESS_TOKEN);
        claims.put(Constants.TOKEN_ID, tokenId);
        claims.put(Constants.USER_ID, userId);
        Date currentDate = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(currentDate)
                .expiration(new Date(currentDate.getTime() + TimeUnit.MINUTES.toMillis(applicationProperties.getJwtAccessTokenExpiration())))
                .id(tokenId)
                .issuer(Constants.DEV_COMMUNITY)
                .signWith(key())
                .compact();
    }

    private String generateRefreshToken(String userId, String tokenId) {
        Map<String, String> claims = new HashMap<>();
        claims.put(Constants.TOKEN_TYPE, Constants.REFRESH_TOKEN);
        claims.put(Constants.TOKEN_ID, tokenId);
        claims.put(Constants.USER_ID, userId);
        Date currentDate = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(currentDate)
                .expiration(new Date(currentDate.getTime() + TimeUnit.MINUTES.toMillis(applicationProperties.getJwtRefreshTokenExpiration())))
                .id(tokenId)
                .issuer(Constants.DEV_COMMUNITY)
                .signWith(key())
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(applicationProperties.getJwtSecretKey()));
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserId(String token) {
        return extractAllClaims(token).getSubject();
    }


    public String getJwtTokenId(String token) {
        return extractAllClaims(token).getId();
    }

    public Boolean isAccessToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(Constants.TOKEN_TYPE).equals(Constants.ACCESS_TOKEN);
    }

    public Boolean isRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(Constants.TOKEN_TYPE).equals(Constants.REFRESH_TOKEN);
    }

    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            log.error(e);
            throw new GenericException(HttpStatus.FORBIDDEN.value(), "Invalid or expired jwt token");
        }
    }
}
