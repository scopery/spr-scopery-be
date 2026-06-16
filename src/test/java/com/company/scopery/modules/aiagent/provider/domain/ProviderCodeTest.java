package com.company.scopery.modules.aiagent.provider.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProviderCodeTest {

    @Test
    void of_normalizesLowercaseToUppercase() {
        ProviderCode code = ProviderCode.of("openai");
        assertThat(code.value()).isEqualTo("OPENAI");
    }

    @Test
    void of_tripsWhitespaceAndNormalizes() {
        ProviderCode code = ProviderCode.of("  google_gemini  ");
        assertThat(code.value()).isEqualTo("GOOGLE_GEMINI");
    }

    @Test
    void of_acceptsValidPatterns() {
        assertThatCode(() -> ProviderCode.of("ANTHROPIC")).doesNotThrowAnyException();
        assertThatCode(() -> ProviderCode.of("INTERNAL_LLM")).doesNotThrowAnyException();
        assertThatCode(() -> ProviderCode.of("PROVIDER_123")).doesNotThrowAnyException();
    }

    @Test
    void of_rejectsCodeWithHyphen() {
        assertThatThrownBy(() -> ProviderCode.of("google-gemini"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsCodeWithSpace() {
        assertThatThrownBy(() -> ProviderCode.of("open ai"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsCodeWithDot() {
        assertThatThrownBy(() -> ProviderCode.of("open.ai"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsBlankCode() {
        assertThatThrownBy(() -> ProviderCode.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsNullCode() {
        assertThatThrownBy(() -> ProviderCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalCodes_areEqual() {
        assertThat(ProviderCode.of("OPENAI")).isEqualTo(ProviderCode.of("openai"));
    }
}
