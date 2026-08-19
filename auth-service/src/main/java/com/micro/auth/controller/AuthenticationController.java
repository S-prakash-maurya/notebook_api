package com.micro.auth.controller;

import com.micro.auth.dto.req.LoginReqDto;
import com.micro.auth.dto.req.RefreshTokenReqDto;
import com.micro.auth.dto.req.UserReqDto;
import com.micro.auth.service.AuthenticationService;
import com.micro.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqDto loginReqDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.login(loginReqDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenReqDto refreshTokenReqDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.refreshToken(refreshTokenReqDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserReqDto userReqDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userReqDto));
    }
}
