package com.company.scopery.modules.aiagent.capability.domain;

import com.company.scopery.modules.aiagent.capability.domain.valueobject.ModelParameterName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ModelParameterNameTest {

    @Test
    void of_lowercaseInput_normalizesToUppercase() {
        ModelParameterName name = ModelParameterName.of("temperature");
        assertThat(name.value()).isEqualTo("TEMPERATURE");
    }

    @Test
    void of_mixedCase_normalizesToUppercase() {
        ModelParameterName name = ModelParameterName.of("maxOutputTokens");
        assertThat(name.value()).isEqualTo("MAXOUTPUTTOKENS");
    }

    @Test
    void of_withUnderscore_accepted() {
        ModelParameterName name = ModelParameterName.of("MAX_OUTPUT_TOKENS");
        assertThat(name.value()).isEqualTo("MAX_OUTPUT_TOKENS");
    }

    @Test
    void of_withNumbers_accepted() {
        ModelParameterName name = ModelParameterName.of("TOP_P_2");
        assertThat(name.value()).isEqualTo("TOP_P_2");
    }

    @Test
    void of_withSpecialChars_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterName.of("temp@rature"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TEMP@RATURE");
    }

    @Test
    void of_withDash_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterName.of("max-tokens"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_blank_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterName.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void of_null_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterName.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalityByValue() {
        ModelParameterName a = ModelParameterName.of("temperature");
        ModelParameterName b = ModelParameterName.of("TEMPERATURE");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}