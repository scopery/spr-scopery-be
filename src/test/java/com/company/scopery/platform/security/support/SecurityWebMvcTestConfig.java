package com.company.scopery.platform.security.support;

import com.company.scopery.common.privacy.SensitiveDataRedactor;
import com.company.scopery.platform.web.idempotency.IdempotencyKeyFilter;
import com.company.scopery.platform.web.idempotency.IdempotencyKeyJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class SecurityWebMvcTestConfig {

    @Bean
    @Primary
    IdempotencyKeyFilter noopIdempotencyKeyFilter() {
        return new IdempotencyKeyFilter(
                Mockito.mock(IdempotencyKeyJpaRepository.class),
                Mockito.mock(SensitiveDataRedactor.class),
                new ObjectMapper(),
                24,
                120,
                false,
                "/api/projects/*/tasks") {
            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) {
                return true;
            }
        };
    }
}
