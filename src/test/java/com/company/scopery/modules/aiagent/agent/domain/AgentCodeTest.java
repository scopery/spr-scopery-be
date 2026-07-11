package com.company.scopery.modules.aiagent.agent.domain;

import com.company.scopery.modules.aiagent.agent.domain.valueobject.AgentCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AgentCodeTest {

    @Test
    void of_lowercaseInput_normalizesToUppercase() {
        AgentCode code = AgentCode.of("cv_extraction_agent");
        assertThat(code.value()).isEqualTo("CV_EXTRACTION_AGENT");
    }

    @Test
    void of_mixedCase_normalizesToUppercase() {
        AgentCode code = AgentCode.of("ContractReview");
        assertThat(code.value()).isEqualTo("CONTRACTREVIEW");
    }

    @Test
    void of_withUnderscore_accepted() {
        AgentCode code = AgentCode.of("INVOICE_MATCHING_AGENT");
        assertThat(code.value()).isEqualTo("INVOICE_MATCHING_AGENT");
    }

    @Test
    void of_withNumbers_accepted() {
        AgentCode code = AgentCode.of("AGENT_V2");
        assertThat(code.value()).isEqualTo("AGENT_V2");
    }

    @Test
    void of_withSpecialChars_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> AgentCode.of("cv-extraction"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CV-EXTRACTION");
    }

    @Test
    void of_withSpace_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> AgentCode.of("CV AGENT"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_blank_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> AgentCode.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void of_null_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> AgentCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalityByValue() {
        AgentCode a = AgentCode.of("cv_extraction_agent");
        AgentCode b = AgentCode.of("CV_EXTRACTION_AGENT");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
