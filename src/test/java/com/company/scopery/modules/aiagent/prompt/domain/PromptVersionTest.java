package com.company.scopery.modules.aiagent.prompt.domain;

import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class PromptVersionTest {

    private static final UUID TEMPLATE_ID = UUID.randomUUID();

    @Test
    void create_validVersion_isDraftByDefault() {
        PromptVersion version = PromptVersion.create(TEMPLATE_ID, 1, "v1 Title",
                "Extract CV data", PromptContentFormat.TEXT, null, "Initial version");

        assertThat(version.id()).isNotNull();
        assertThat(version.status()).isEqualTo(PromptVersionStatus.DRAFT);
        assertThat(version.versionNumber()).isEqualTo(1);
        assertThat(version.templateId()).isEqualTo(TEMPLATE_ID);
    }

    @Test
    void create_blankContent_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> PromptVersion.create(TEMPLATE_ID, 1, null,
                "", PromptContentFormat.TEXT, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content is required");
    }

    @Test
    void update_whenDraft_succeeds() {
        PromptVersion version = PromptVersion.create(TEMPLATE_ID, 1, null,
                "Original content", PromptContentFormat.TEXT, null, null);

        version.update("New Title", "Updated content", PromptContentFormat.MARKDOWN, null, "Updated");

        assertThat(version.content()).isEqualTo("Updated content");
        assertThat(version.contentFormat()).isEqualTo(PromptContentFormat.MARKDOWN);
        assertThat(version.title()).isEqualTo("New Title");
        assertThat(version.changeNote()).isEqualTo("Updated");
    }

    @Test
    void update_whenActive_throwsIllegalStateException() {
        PromptVersion version = PromptVersion.create(TEMPLATE_ID, 1, null,
                "Content", PromptContentFormat.TEXT, null, null);
        version.activate();

        assertThatThrownBy(() -> version.update(null, "New content", PromptContentFormat.TEXT, null, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DRAFT");
    }

    @Test
    void update_whenArchived_throwsIllegalStateException() {
        PromptVersion version = PromptVersion.create(TEMPLATE_ID, 1, null,
                "Content", PromptContentFormat.TEXT, null, null);
        version.archive();

        assertThatThrownBy(() -> version.update(null, "New content", PromptContentFormat.TEXT, null, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DRAFT");
    }

    @Test
    void activate_fromDraft_setsStatusToActive() {
        PromptVersion version = PromptVersion.create(TEMPLATE_ID, 1, null,
                "Content", PromptContentFormat.TEXT, null, null);
        version.activate();

        assertThat(version.status()).isEqualTo(PromptVersionStatus.ACTIVE);
    }

    @Test
    void activate_archivedVersion_throwsIllegalStateException() {
        PromptVersion version = PromptVersion.reconstitute(UUID.randomUUID(), TEMPLATE_ID, 1,
                null, "Content", PromptContentFormat.TEXT, null, null,
                PromptVersionStatus.ARCHIVED, Instant.now(), Instant.now());

        assertThatThrownBy(version::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Archived prompt version cannot be activated again");
    }

    @Test
    void archive_setsStatusToArchived() {
        PromptVersion version = PromptVersion.create(TEMPLATE_ID, 1, null,
                "Content", PromptContentFormat.TEXT, null, null);
        version.archive();

        assertThat(version.status()).isEqualTo(PromptVersionStatus.ARCHIVED);
    }
}