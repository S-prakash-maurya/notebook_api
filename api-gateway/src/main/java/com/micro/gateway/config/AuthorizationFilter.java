package com.micro.gateway.config;

import com.generic.service.constants.Constant;
import com.generic.service.exception.GenericException;
import com.generic.service.util.RequestContext;
import com.micro.gateway.clients.UserServiceClient;
import com.micro.gateway.dto.UserResDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.micro.gateway.constants.Constant.BLANK_QUOTE;

@Slf4j
@AllArgsConstructor
public class AuthorizationFilter implements WebFilter {
    private static final String TOKEN_QUERY_PARAM = "token";
    private final UserServiceClient userServiceClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpResponse response = exchange.getResponse();

        // Extract token: prefer Authorization header, fall back to query param (for WebSocket handshake)
        String tokenFromHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String tokenFromQuery = exchange.getRequest().getQueryParams().getFirst(TOKEN_QUERY_PARAM);

        final String authorization = (tokenFromHeader != null && !tokenFromHeader.isBlank()) ? tokenFromHeader : tokenFromQuery;

        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();

        // Strip internal/sensitive headers regardless
        requestBuilder.headers(httpHeaders -> {
            httpHeaders.remove("x-chat-tenant-id");
            httpHeaders.remove("x-chat-user-id");
            httpHeaders.remove("x-chat-user-role");
            httpHeaders.remove("x-chat-authorization");
            httpHeaders.remove(Constant.X_GATEWAY_SECURITY_TOKEN);
        });

        // If token came from query param, inject it as a proper Authorization header downstream
        if (authorization != null && !authorization.isBlank() && (tokenFromHeader == null || tokenFromHeader.isBlank())) {
            requestBuilder.headers(httpHeaders -> httpHeaders.set(HttpHeaders.AUTHORIZATION, authorization));
        }

        ServerWebExchange serverWebExchange = exchange.mutate().request(requestBuilder.build()).build();

        if (!serverWebExchange.getRequest().getPath().value().contains("/chat/v1/chat-socket-event-registry") && authorization != null && !authorization.isBlank()) {
            System.out.println("AuthPath - " + serverWebExchange.getRequest().getPath().value());
            System.out.println("AuthQuery - " + serverWebExchange.getRequest().getQueryParams().getFirst("token"));
            return Mono.fromCallable(() -> userServiceClient.validateAuthorization(authorization)).flatMap(user -> handleUserAuthentication(serverWebExchange, chain, user, authorization)).onErrorResume(GenericException.class, e -> handleTokenExpired(response)).onErrorResume(e -> handleAuthenticationError(response, e));
        } else {
            System.out.println("Path - " + serverWebExchange.getRequest().getPath().value());
            System.out.println("Protocol - " + serverWebExchange.getRequest().getURI().getScheme());
            System.out.println("Query - " + serverWebExchange.getRequest().getQueryParams().getFirst("token"));
            return chain.filter(serverWebExchange);
        }

    }

    private Mono<Void> handleUserAuthentication(ServerWebExchange exchange, WebFilterChain chain, UserResDto user, String token) {
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().header("x-chat-tenant-id", user.getTenantId() == null ? BLANK_QUOTE : user.getTenantId().toString()).header("x-chat-user-id", user.getId() == null ? BLANK_QUOTE : user.getId().toString()).header("x-chat-user-role", user.getRole().name()).header("x-chat-authorization", token).build();
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        return chain.filter(modifiedExchange).contextWrite(context -> context.put("userId", user.getId()).put("tenantId", user.getTenantId() == null ? BLANK_QUOTE : user.getTenantId()));
    }

    private Mono<Void> handleTokenExpired(ServerHttpResponse response) {
        return buildErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token expired. Please log in again.");
    }

    private Mono<Void> handleAuthenticationError(ServerHttpResponse response, Throwable e) {
        log.error("Authentication error", e);
        return buildErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized Access");
    }

    private Mono<Void> buildErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);

        Map<String, String> errorMessage = new HashMap<>();
        errorMessage.put("message", message);

        try {
            String json = RequestContext.convertObjectToJsonString(errorMessage);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);

            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response.writeWith(Flux.just(buffer));
        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }

}
