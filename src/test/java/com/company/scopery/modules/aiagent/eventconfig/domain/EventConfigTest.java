package com.company.scopery.modules.aiagent.eventconfig.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class EventConfigTest {

    private EventConfig sampleActive() {
        return EventConfig.create(
                EventConfigCode.of("HRM_CV_UPLOAD_DEV"),
                "HRM CV Upload DEV",
                UUID.randomUUID(),
                EventConfigEnvironment.DEV,
                EventTriggerType.EVENT,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                "CV upload event config for dev"
        );
    }

    @Test
    void create_setsActiveStatusByDefault() {
        EventConfig config = sampleActive();
        assertThat(config.status()).isEqualTo(EventConfigStatus.ACTIVE);
    }

    @Test
    void deactivate_changesStatusToInactive() {
        EventConfig config = sampleActive();
        config.deactivate();
        assertThat(config.status()).isEqualTo(EventConfigStatus.INACTIVE);
    }

    @Test
    void activate_fromInactive_succeeds() {
        EventConfig config = sampleActive();
        config.deactivate();
        config.activate();
        assertThat(config.status()).isEqualTo(EventConfigStatus.ACTIVE);
    }

    @Test
    void activate_whenDeprecated_throwsIllegalState() {
        EventConfig deprecated = EventConfig.reconstitute(
                UUID.randomUUID(),
                EventConfigCode.of("OLD_CONFIG"),
                "Old Config",
                UUID.randomUUID(),
                EventConfigEnvironment.DEV,
                EventTriggerType.MANUAL,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null, null,
                EventConfigStatus.DEPRECATED,
                Instant.now(), Instant.now()
        );

        assertThatThrownBy(deprecated::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Deprecated");
    }

    @Test
    void create_requiresNonBlankName() {
        assertThatThrownBy(() -> EventConfig.create(
                EventConfigCode.of("CODE"),
                "",
                UUID.randomUUID(),
                EventConfigEnvironment.DEV,
                EventTriggerType.EVENT,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name is required");
    }

    @Test
    void create_requiresNonNullAgentId() {
        assertThatThrownBy(() -> EventConfig.create(
                EventConfigCode.of("CODE"),
                "Name",
                UUID.randomUUID(),
                EventConfigEnvironment.DEV,
                EventTriggerType.EVENT,
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("agentId");
    }

    @Test
    void update_changesAllowedFields() {
        EventConfig config = sampleActive();
        UUID newAgentId = UUID.randomUUID();
        UUID newPromptVersionId = UUID.randomUUID();
        UUID newModelDeploymentId = UUID.randomUUID();

        config.update("Updated Name", EventTriggerType.MANUAL, newAgentId,
                newPromptVersionId, newModelDeploymentId, "expr", "updated desc");

        assertThat(config.name()).isEqualTo("Updated Name");
        assertThat(config.triggerType()).isEqualTo(EventTriggerType.MANUAL);
        assertThat(config.agentId()).isEqualTo(newAgentId);
        assertThat(config.conditionExpression()).isEqualTo("expr");
        assertThat(config.description()).isEqualTo("updated desc");
    }
}