package com.micro.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth.service")
public class ApplicationProperties {
    private Integer jwtAccessTokenExpiration;
    private Integer jwtRefreshTokenExpiration;
    private String jwtSecretKey;
}
