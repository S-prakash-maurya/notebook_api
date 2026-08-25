//package com.micro.auth.filter;
//
//import com.generic.service.exception.GenericException;
//import com.generic.service.model.GenericLoggedInUserData;
//import com.generic.service.util.RequestContext;
//import com.micro.auth.constants.Constants;
//import com.micro.auth.dto.res.UserResDto;
//import com.micro.auth.entity.JwtTokenEntity;
//import com.micro.auth.enums.UserStatus;
//import com.micro.auth.service.JwtTokenProvider;
//import com.micro.auth.service.JwtTokenService;
//import com.micro.auth.service.UserService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.Optional;
//import java.util.UUID;
//
//@AllArgsConstructor
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final UserService userService;
//    private final JwtTokenService jwtTokenService;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if (request.getMethod().toLowerCase().contains("option")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        String authorization = request.getHeader(Constants.AUTHORIZATION);
//        if (authorization != null && !authorization.isBlank() && jwtTokenProvider.validateToken(authorization) && jwtTokenProvider.isAccessToken(authorization)) {
//            String jwtTokenId = jwtTokenProvider.getJwtTokenId(authorization);
//            Optional<JwtTokenEntity> jwtTokenEntity = jwtTokenService.getByField("jwtTokenId", UUID.fromString(jwtTokenId));
//            if (jwtTokenEntity.isPresent()) {
//                final UserResDto userResDto = userService.getById(UUID.fromString(jwtTokenProvider.getUserId(authorization)));
//                if (userResDto.getStatus() != UserStatus.ACTIVE) {
//                    throw new GenericException(HttpStatus.LOCKED.value(), "User id not active");
//                }
//                GenericLoggedInUserData genericLoggedInUserData = GenericLoggedInUserData.builder().userId(userResDto.getId()).tenantId(userResDto.getTenantId()).build();
//                RequestContext.setUserFromRequestContextHolder(genericLoggedInUserData);
//                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userResDto.getRole());
//                Authentication authentication = new UsernamePasswordAuthenticationToken(genericLoggedInUserData, null, Collections.singletonList(authority));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
//}
package com.micro.auth.filter;

import com.generic.service.exception.GenericException;
import com.generic.service.model.GenericLoggedInUserData;
import com.generic.service.util.RequestContext;
import com.micro.auth.constants.Constants;
import com.micro.auth.dto.res.UserResDto;
import com.micro.auth.entity.JwtTokenEntity;
import com.micro.auth.enums.UserStatus;
import com.micro.auth.service.JwtTokenProvider;
import com.micro.auth.service.JwtTokenService;
import com.micro.auth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().toLowerCase().contains("option")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(Constants.AUTHORIZATION);

        // BUG FIX: the raw header value is "Bearer <token>", but
        // jwtTokenProvider expects ONLY the raw JWT string. Passing the
        // header through unchanged (with the "Bearer " prefix still
        // attached) makes validateToken(...) fail to parse it, which
        // silently skips setting the SecurityContext authentication —
        // every authenticated request then falls through to
        // .anyRequest().authenticated() in SecurityConfig and gets
        // rejected with 401 + "WWW-Authenticate: Basic", even though
        // the client sent a perfectly valid token.
        String token = extractToken(authorizationHeader);

        if (token != null
                && jwtTokenProvider.validateToken(token)
                && jwtTokenProvider.isAccessToken(token)) {
            String jwtTokenId = jwtTokenProvider.getJwtTokenId(token);
            Optional<JwtTokenEntity> jwtTokenEntity = jwtTokenService.getByField("jwtTokenId", UUID.fromString(jwtTokenId));
            if (jwtTokenEntity.isPresent()) {
                final UserResDto userResDto = userService.getById(UUID.fromString(jwtTokenProvider.getUserId(token)));
                if (userResDto.getStatus() != UserStatus.ACTIVE) {
                    throw new GenericException(HttpStatus.LOCKED.value(), "User id not active");
                }
                GenericLoggedInUserData genericLoggedInUserData = GenericLoggedInUserData.builder().userId(userResDto.getId()).tenantId(userResDto.getTenantId()).build();
                RequestContext.setUserFromRequestContextHolder(genericLoggedInUserData);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userResDto.getRole());
                Authentication authentication = new UsernamePasswordAuthenticationToken(genericLoggedInUserData, null, Collections.singletonList(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Strips the "Bearer " scheme prefix from the Authorization header
     * and returns just the raw JWT. Returns null if the header is
     * missing, blank, or doesn't use the Bearer scheme.
     */
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }
}