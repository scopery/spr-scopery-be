package com.company.scopery.modules.aiagent.prompt.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PromptTemplateCodeTest {

    @Test
    void of_lowercaseInput_normalizesToUppercase() {
        PromptTemplateCode code = PromptTemplateCode.of("cv_extraction_prompt");
        assertThat(code.value()).isEqualTo("CV_EXTRACTION_PROMPT");
    }

    @Test
    void of_mixedCase_normalizesToUppercase() {
        PromptTemplateCode code = PromptTemplateCode.of("ContractReviewPrompt");
        assertThat(code.value()).isEqualTo("CONTRACTREVIEWPROMPT");
    }

    @Test
    void of_withUnderscore_accepted() {
        PromptTemplateCode code = PromptTemplateCode.of("INVOICE_MATCHING_PROMPT");
        assertThat(code.value()).isEqualTo("INVOICE_MATCHING_PROMPT");
    }

    @Test
    void of_withNumbers_accepted() {
        PromptTemplateCode code = PromptTemplateCode.of("PROMPT_V2");
        assertThat(code.value()).isEqualTo("PROMPT_V2");
    }

    @Test
    void of_withDash_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> PromptTemplateCode.of("cv-extraction-prompt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CV-EXTRACTION-PROMPT");
    }

    @Test
    void of_withSpace_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> PromptTemplateCode.of("CV PROMPT"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_blank_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> PromptTemplateCode.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void of_null_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> PromptTemplateCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalityByValue() {
        PromptTemplateCode a = PromptTemplateCode.of("cv_extraction_prompt");
        PromptTemplateCode b = PromptTemplateCode.of("CV_EXTRACTION_PROMPT");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
