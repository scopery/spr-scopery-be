package com.company.scopery.modules.eventregistry.eventdefinition.domain;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class EventDefinitionTest {

    private EventDefinition sampleActive() {
        return EventDefinition.create(
                EventDefinitionCode.of("HRM_CV_UPLOADED"),
                "CV Uploaded",
                SourceSystemCode.of("HRM"),
                EventKey.of("CV_UPLOADED"),
                "Triggered when a CV is uploaded",
                null, null);
    }

    @Test
    void create_setsActiveStatusAndVersionOne() {
        EventDefinition e = sampleActive();
        assertThat(e.status()).isEqualTo(EventDefinitionStatus.ACTIVE);
        assertThat(e.eventVersion()).isEqualTo(EventDefinition.INITIAL_VERSION);
        assertThat(e.systemEvent()).isTrue();
    }

    @Test
    void deactivate_changesStatusToInactive() {
        EventDefinition e = sampleActive();
        e.deactivate();
        assertThat(e.status()).isEqualTo(EventDefinitionStatus.INACTIVE);
    }

    @Test
    void activate_fromInactive_succeeds() {
        EventDefinition e = sampleActive();
        e.deactivate();
        e.activate();
        assertThat(e.status()).isEqualTo(EventDefinitionStatus.ACTIVE);
    }

    @Test
    void activate_whenDeprecated_throwsIllegalState() {
        EventDefinition deprecated = EventDefinition.reconstitute(
                UUID.randomUUID(),
                EventDefinitionCode.of("OLD_EVENT"),
                "Old Event",
                SourceSystemCode.of("HRM"),
                EventKey.of("OLD_THING"),
                null, null, null,
                EventDefinitionStatus.DEPRECATED,
                EventDefinition.INITIAL_VERSION, null,
                null, null, true,
                Instant.now(), null, null,
                Instant.now(), Instant.now());

        assertThatThrownBy(deprecated::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Deprecated");
    }

    @Test
    void deprecateActiveEvent_success() {
        EventDefinition e = sampleActive();
        UUID replacement = UUID.randomUUID();
        UUID actor = UUID.randomUUID();
        e.deprecate(replacement, actor);
        assertThat(e.status()).isEqualTo(EventDefinitionStatus.DEPRECATED);
        assertThat(e.replacementEventDefinitionId()).isEqualTo(replacement);
        assertThat(e.deprecatedBy()).isEqualTo(actor);
        assertThat(e.deprecatedAt()).isNotNull();
    }

    @Test
    void update_whenDeprecated_throwsIllegalState() {
        EventDefinition e = sampleActive();
        e.deprecate(null, null);
        assertThatThrownBy(() -> e.update("x", null, null, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void update_changesAllowedFields() {
        EventDefinition e = sampleActive();
        e.update("Updated Name", "new description", "{}", null);
        assertThat(e.name()).isEqualTo("Updated Name");
        assertThat(e.description()).isEqualTo("new description");
        assertThat(e.inputSchema()).isEqualTo("{}");
        assertThat(e.outputSchema()).isNull();
    }

    @Test
    void create_requiresNonBlankName() {
        assertThatThrownBy(() -> EventDefinition.create(
                EventDefinitionCode.of("CODE"),
                "",
                SourceSystemCode.of("SYS"),
                EventKey.of("KEY"),
                null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name is required");
    }
}
