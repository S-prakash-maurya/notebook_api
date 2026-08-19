package com.micro.gateway.config;

import com.micro.gateway.clients.UserServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    private final AuthorizationFilter authorizationFilter;

    public SecurityConfig(UserServiceClient userServiceClient) {
        this.authorizationFilter = new AuthorizationFilter(userServiceClient);
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                )
                .addFilterAt(authorizationFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    @Bean
    public ServerAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (exchange, exception) -> {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String responseJson = "{\"message\": \"Unauthorized\"}";
            byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBytes)));
        };
    }


    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        String username = "shivmohan";
        String rawPassword = "MohanShiv32!2";
        return inputUsername -> {
            if (!inputUsername.equals(username)) {
                return Mono.empty();
            }
            UserDetails user = User.withUsername(username)
                    .password(rawPassword)
                    .roles("USER")
                    .build();
            return Mono.just(user);
        };
    }
}