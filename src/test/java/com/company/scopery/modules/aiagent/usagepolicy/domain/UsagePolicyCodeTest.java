package com.company.scopery.modules.aiagent.usagepolicy.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UsagePolicyCodeTest {

    @Test
    void of_lowercaseInput_normalizesToUppercase() {
        UsagePolicyCode code = UsagePolicyCode.of("global_limit");
        assertThat(code.value()).isEqualTo("GLOBAL_LIMIT");
    }

    @Test
    void of_withUnderscore_accepted() {
        UsagePolicyCode code = UsagePolicyCode.of("AGENT_DAILY_LIMIT");
        assertThat(code.value()).isEqualTo("AGENT_DAILY_LIMIT");
    }

    @Test
    void of_withNumbers_accepted() {
        UsagePolicyCode code = UsagePolicyCode.of("POLICY_V2");
        assertThat(code.value()).isEqualTo("POLICY_V2");
    }

    @Test
    void of_withSpecialChars_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> UsagePolicyCode.of("policy-limit"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("POLICY-LIMIT");
    }

    @Test
    void of_blank_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> UsagePolicyCode.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void of_null_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> UsagePolicyCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalityByValue() {
        UsagePolicyCode a = UsagePolicyCode.of("global_limit");
        UsagePolicyCode b = UsagePolicyCode.of("GLOBAL_LIMIT");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
