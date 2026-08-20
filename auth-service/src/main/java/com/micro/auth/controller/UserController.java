package com.micro.auth.controller;

import com.generic.service.util.RequestContext;
import com.micro.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Also need to verify the token of a user
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<?> fetchUser() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getById(RequestContext.getUserFromRequestContextHolder().getUserId()));
    }


    @DeleteMapping
    public ResponseEntity<?> deleteUser(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteSoft(RequestContext.getUserFromRequestContextHolder().getUserId()));
    }
}
