package com.company.scopery.platform.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "scopery.jwt")
public class JwtProperties {

    private String secret;
    private long expirationMs        = 900_000L;       // 15 minutes
    private long refreshExpirationMs = 604_800_000L;   // 7 days
    private boolean cookieSecure     = true;
}
