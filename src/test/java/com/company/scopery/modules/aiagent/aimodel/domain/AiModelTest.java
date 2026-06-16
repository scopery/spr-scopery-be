package com.company.scopery.modules.aiagent.aimodel.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AiModelTest {

    @Test
    void create_withValidData_createsActiveModel() {
        AiModel model = AiModel.create(
                UUID.randomUUID(), "GPT-4.1", AiModelCode.of("GPT_4_1"),
                "gpt-4.1", AiModelType.CHAT, "OpenAI GPT-4.1");

        assertThat(model.id()).isNotNull();
        assertThat(model.status()).isEqualTo(AiModelStatus.ACTIVE);
        assertThat(model.code().value()).isEqualTo("GPT_4_1");
        assertThat(model.createdAt()).isNotNull();
    }

    @Test
    void create_withoutProviderModelId_throwsException() {
        assertThatThrownBy(() -> AiModel.create(
                UUID.randomUUID(), "GPT-4.1", AiModelCode.of("GPT_4_1"),
                null, AiModelType.CHAT, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provider model ID");
    }

    @Test
    void create_withBlankName_throwsException() {
        assertThatThrownBy(() -> AiModel.create(
                UUID.randomUUID(), "", AiModelCode.of("GPT_4_1"),
                "gpt-4.1", AiModelType.CHAT, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void create_withNullProviderId_throwsException() {
        assertThatThrownBy(() -> AiModel.create(
                null, "GPT-4.1", AiModelCode.of("GPT_4_1"),
                "gpt-4.1", AiModelType.CHAT, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provider ID");
    }

    @Test
    void deactivate_setsStatusToInactive() {
        AiModel model = activeModel();
        model.deactivate();
        assertThat(model.status()).isEqualTo(AiModelStatus.INACTIVE);
    }

    @Test
    void activate_fromInactive_succeeds() {
        AiModel model = inactiveModel();
        model.activate();
        assertThat(model.status()).isEqualTo(AiModelStatus.ACTIVE);
    }

    @Test
    void activate_fromDeprecated_throwsException() {
        AiModel model = deprecatedModel();
        assertThatThrownBy(model::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DEPRECATED_AI_MODEL_CANNOT_BE_ACTIVATED");
    }

    @Test
    void update_changesAllowedFields() {
        AiModel model = activeModel();
        model.update("New Name", "gpt-4.1-updated", AiModelType.EMBEDDING, "new desc");
        assertThat(model.name()).isEqualTo("New Name");
        assertThat(model.providerModelId()).isEqualTo("gpt-4.1-updated");
        assertThat(model.type()).isEqualTo(AiModelType.EMBEDDING);
        assertThat(model.description()).isEqualTo("new desc");
    }

    // --- helpers ---

    private AiModel activeModel() {
        return AiModel.create(UUID.randomUUID(), "GPT-4.1", AiModelCode.of("GPT_4_1"),
                "gpt-4.1", AiModelType.CHAT, null);
    }

    private AiModel inactiveModel() {
        return AiModel.reconstitute(UUID.randomUUID(), UUID.randomUUID(), "GPT-4.1",
                AiModelCode.of("GPT_4_1"), "gpt-4.1", AiModelType.CHAT, null,
                AiModelStatus.INACTIVE, Instant.now(), Instant.now());
    }

    private AiModel deprecatedModel() {
        return AiModel.reconstitute(UUID.randomUUID(), UUID.randomUUID(), "Old Model",
                AiModelCode.of("OLD_MODEL"), "old-model-v1", AiModelType.CHAT, null,
                AiModelStatus.DEPRECATED, Instant.now(), Instant.now());
    }
}
