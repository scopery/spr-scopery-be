package com.company.scopery.common.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    void withExplicitErrorCode_storesCodeAndMessage() {
        BusinessException ex = new BusinessException("PROVIDER_CODE_ALREADY_EXISTS", "Provider code exists: OPENAI");

        assertThat(ex.getErrorCode()).isEqualTo("PROVIDER_CODE_ALREADY_EXISTS");
        assertThat(ex.getMessage()).isEqualTo("Provider code exists: OPENAI");
    }

    @Test
    void withMessageOnly_defaultsToBusinessRuleViolation() {
        BusinessException ex = new BusinessException("some rule failed");

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BUSINESS_RULE_VIOLATION);
        assertThat(ex.getMessage()).isEqualTo("some rule failed");
    }
}
