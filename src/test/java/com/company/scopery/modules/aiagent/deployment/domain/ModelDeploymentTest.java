package com.company.scopery.modules.aiagent.deployment.domain;

import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.valueobject.ModelDeploymentCode;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentEnvironment;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ModelDeploymentTest {

    @Test
    void create_withValidData_createsActiveDeployment() {
        ModelDeployment deployment = ModelDeployment.create(
                UUID.randomUUID(), "GPT-4.1 Prod", ModelDeploymentCode.of("GPT_4_1_PROD"),
                ModelDeploymentEnvironment.PROD, "gpt-4.1",
                null, null, null, false, null);

        assertThat(deployment.id()).isNotNull();
        assertThat(deployment.status()).isEqualTo(ModelDeploymentStatus.ACTIVE);
        assertThat(deployment.code().value()).isEqualTo("GPT_4_1_PROD");
        assertThat(deployment.isDefault()).isFalse();
    }

    @Test
    void create_withoutProviderDeploymentId_throwsException() {
        assertThatThrownBy(() -> ModelDeployment.create(
                UUID.randomUUID(), "GPT-4.1 Prod", ModelDeploymentCode.of("GPT_4_1_PROD"),
                ModelDeploymentEnvironment.PROD, null, null, null, null, false, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provider deployment ID");
    }

    @Test
    void create_withBlankName_throwsException() {
        assertThatThrownBy(() -> ModelDeployment.create(
                UUID.randomUUID(), "", ModelDeploymentCode.of("GPT_4_1_PROD"),
                ModelDeploymentEnvironment.PROD, "gpt-4.1", null, null, null, false, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void create_withTemperatureOutOfRange_throwsException() {
        assertThatThrownBy(() -> ModelDeployment.create(
                UUID.randomUUID(), "GPT-4.1 Prod", ModelDeploymentCode.of("GPT_4_1_PROD"),
                ModelDeploymentEnvironment.PROD, "gpt-4.1", null,
                new BigDecimal("2.5"), null, false, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("temperature");
    }

    @Test
    void create_withNegativeMaxOutputTokens_throwsException() {
        assertThatThrownBy(() -> ModelDeployment.create(
                UUID.randomUUID(), "GPT-4.1 Prod", ModelDeploymentCode.of("GPT_4_1_PROD"),
                ModelDeploymentEnvironment.PROD, "gpt-4.1", null, null, -1, false, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("max output tokens");
    }

    @Test
    void deactivate_setsStatusToInactive() {
        ModelDeployment deployment = activeDeployment();
        deployment.deactivate();
        assertThat(deployment.status()).isEqualTo(ModelDeploymentStatus.INACTIVE);
    }

    @Test
    void activate_fromInactive_succeeds() {
        ModelDeployment deployment = inactiveDeployment();
        deployment.activate();
        assertThat(deployment.status()).isEqualTo(ModelDeploymentStatus.ACTIVE);
    }

    @Test
    void activate_fromDeprecated_throwsException() {
        ModelDeployment deployment = deprecatedDeployment();
        assertThatThrownBy(deployment::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DEPRECATED_MODEL_DEPLOYMENT_CANNOT_BE_ACTIVATED");
    }

    @Test
    void setDefault_setsIsDefaultTrue() {
        ModelDeployment deployment = activeDeployment();
        deployment.setDefault();
        assertThat(deployment.isDefault()).isTrue();
    }

    @Test
    void unsetDefault_setsIsDefaultFalse() {
        ModelDeployment deployment = defaultDeployment();
        deployment.unsetDefault();
        assertThat(deployment.isDefault()).isFalse();
    }

    @Test
    void create_withValidTemperature_succeeds() {
        assertThatCode(() -> ModelDeployment.create(
                UUID.randomUUID(), "Test", ModelDeploymentCode.of("TEST"),
                ModelDeploymentEnvironment.DEV, "gpt-4.1", null,
                new BigDecimal("1.0"), null, false, null))
                .doesNotThrowAnyException();
    }

    // --- helpers ---

    private ModelDeployment activeDeployment() {
        return ModelDeployment.create(UUID.randomUUID(), "GPT-4.1 Prod",
                ModelDeploymentCode.of("GPT_4_1_PROD"), ModelDeploymentEnvironment.PROD,
                "gpt-4.1", null, null, null, false, null);
    }

    private ModelDeployment inactiveDeployment() {
        return ModelDeployment.reconstitute(UUID.randomUUID(), UUID.randomUUID(), "GPT-4.1 Prod",
                ModelDeploymentCode.of("GPT_4_1_PROD"), ModelDeploymentEnvironment.PROD,
                "gpt-4.1", null, null, null, false, null,
                ModelDeploymentStatus.INACTIVE, Instant.now(), Instant.now());
    }

    private ModelDeployment deprecatedDeployment() {
        return ModelDeployment.reconstitute(UUID.randomUUID(), UUID.randomUUID(), "Old Deployment",
                ModelDeploymentCode.of("OLD_DEPLOY"), ModelDeploymentEnvironment.PROD,
                "gpt-3.5", null, null, null, false, null,
                ModelDeploymentStatus.DEPRECATED, Instant.now(), Instant.now());
    }

    private ModelDeployment defaultDeployment() {
        return ModelDeployment.reconstitute(UUID.randomUUID(), UUID.randomUUID(), "Default Deploy",
                ModelDeploymentCode.of("DEFAULT"), ModelDeploymentEnvironment.PROD,
                "gpt-4.1", null, null, null, true, null,
                ModelDeploymentStatus.ACTIVE, Instant.now(), Instant.now());
    }
}
