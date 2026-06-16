package com.company.scopery.modules.aiagent.aimodel.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AiModelCodeTest {

    @Test
    void of_normalizesLowercaseToUppercase() {
        AiModelCode code = AiModelCode.of("gpt_4_1_mini");
        assertThat(code.value()).isEqualTo("GPT_4_1_MINI");
    }

    @Test
    void of_tripsWhitespaceAndNormalizes() {
        AiModelCode code = AiModelCode.of("  gemini_flash  ");
        assertThat(code.value()).isEqualTo("GEMINI_FLASH");
    }

    @Test
    void of_acceptsValidPatterns() {
        assertThatCode(() -> AiModelCode.of("CLAUDE_SONNET")).doesNotThrowAnyException();
        assertThatCode(() -> AiModelCode.of("GPT_4_1")).doesNotThrowAnyException();
        assertThatCode(() -> AiModelCode.of("MODEL_123")).doesNotThrowAnyException();
    }

    @Test
    void of_rejectsCodeWithHyphen() {
        assertThatThrownBy(() -> AiModelCode.of("gpt-4.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsCodeWithDot() {
        assertThatThrownBy(() -> AiModelCode.of("gemini.1.5"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsCodeWithSpace() {
        assertThatThrownBy(() -> AiModelCode.of("gpt 4"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsBlankCode() {
        assertThatThrownBy(() -> AiModelCode.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsNullCode() {
        assertThatThrownBy(() -> AiModelCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalCodes_areEqual() {
        assertThat(AiModelCode.of("GPT_4_1")).isEqualTo(AiModelCode.of("gpt_4_1"));
    }
}
