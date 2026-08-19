package com.micro.gateway.clients;

import com.generic.service.exception.GenericException;
import com.micro.gateway.dto.UserResDto;
import com.micro.gateway.enums.UserStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service("userServiceClient")
public class UserServiceClient {

    @Qualifier("userServiceClient")
    private final WebClient userServiceWebClient;

    public UserServiceClient(WebClient userServiceWebClient) {
        this.userServiceWebClient = userServiceWebClient;
    }

    public UserResDto validateAuthorization(String authorization) {
        final UserResDto userResDto = userServiceWebClient.get()
                .uri("/user")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Client Error " + body))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Server Error " + body))
                )
                .bodyToMono(UserResDto.class)
                .block();
        if (userResDto == null || userResDto.getId() == null) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access");
        }
        if (userResDto.getStatus() != UserStatus.ACTIVE) {
            throw new GenericException(HttpStatus.UNAUTHORIZED.value(), "User not activated");
        }
        return userResDto;
    }

}
