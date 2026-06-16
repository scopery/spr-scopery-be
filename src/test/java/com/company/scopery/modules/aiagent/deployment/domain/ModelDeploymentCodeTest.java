package com.company.scopery.modules.aiagent.deployment.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ModelDeploymentCodeTest {

    @Test
    void of_normalizesLowercaseToUppercase() {
        ModelDeploymentCode code = ModelDeploymentCode.of("gpt_4_1_prod");
        assertThat(code.value()).isEqualTo("GPT_4_1_PROD");
    }

    @Test
    void of_tripsWhitespaceAndNormalizes() {
        ModelDeploymentCode code = ModelDeploymentCode.of("  gemini_flash_dev  ");
        assertThat(code.value()).isEqualTo("GEMINI_FLASH_DEV");
    }

    @Test
    void of_acceptsValidPatterns() {
        assertThatCode(() -> ModelDeploymentCode.of("GPT_4_1_PROD")).doesNotThrowAnyException();
        assertThatCode(() -> ModelDeploymentCode.of("CLAUDE_SONNET_UAT")).doesNotThrowAnyException();
        assertThatCode(() -> ModelDeploymentCode.of("DEPLOY_123")).doesNotThrowAnyException();
    }

    @Test
    void of_rejectsCodeWithHyphen() {
        assertThatThrownBy(() -> ModelDeploymentCode.of("gpt-4.1-prod"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsCodeWithSpace() {
        assertThatThrownBy(() -> ModelDeploymentCode.of("gpt 4 prod"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsBlankCode() {
        assertThatThrownBy(() -> ModelDeploymentCode.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_rejectsNullCode() {
        assertThatThrownBy(() -> ModelDeploymentCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalCodes_areEqual() {
        assertThat(ModelDeploymentCode.of("GPT_4_1_PROD")).isEqualTo(ModelDeploymentCode.of("gpt_4_1_prod"));
    }
}
