package com.company.scopery.modules.aiagent.prompt.domain;

import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.valueobject.PromptTemplateCode;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class PromptTemplateTest {

    private static final UUID AGENT_ID = UUID.randomUUID();
    private static final PromptTemplateCode CV_CODE = PromptTemplateCode.of("CV_EXTRACTION_PROMPT");

    @Test
    void create_validTemplate_isActiveByDefault() {
        PromptTemplate template = PromptTemplate.create(AGENT_ID, "CV Extraction Prompt", CV_CODE, null);

        assertThat(template.id()).isNotNull();
        assertThat(template.status()).isEqualTo(PromptTemplateStatus.ACTIVE);
        assertThat(template.code().value()).isEqualTo("CV_EXTRACTION_PROMPT");
        assertThat(template.agentId()).isEqualTo(AGENT_ID);
    }

    @Test
    void create_blankName_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> PromptTemplate.create(AGENT_ID, "", CV_CODE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name is required");
    }

    @Test
    void deactivate_setsStatusToInactive() {
        PromptTemplate template = PromptTemplate.create(AGENT_ID, "CV Extraction Prompt", CV_CODE, null);
        template.deactivate();

        assertThat(template.status()).isEqualTo(PromptTemplateStatus.INACTIVE);
    }

    @Test
    void activate_fromInactive_setsStatusToActive() {
        PromptTemplate template = PromptTemplate.create(AGENT_ID, "CV Extraction Prompt", CV_CODE, null);
        template.deactivate();
        template.activate();

        assertThat(template.status()).isEqualTo(PromptTemplateStatus.ACTIVE);
    }

    @Test
    void activate_deprecatedTemplate_throwsIllegalStateException() {
        PromptTemplate template = PromptTemplate.reconstitute(UUID.randomUUID(), AGENT_ID,
                "Old Prompt", CV_CODE, null, PromptTemplateStatus.DEPRECATED,
                Instant.now(), Instant.now());

        assertThatThrownBy(template::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Deprecated prompt template cannot be activated again");
    }

    @Test
    void update_changesNameAndDescription() {
        PromptTemplate template = PromptTemplate.create(AGENT_ID, "CV Extraction Prompt", CV_CODE, null);
        template.update("CV Extraction Prompt v2", "Updated description");

        assertThat(template.name()).isEqualTo("CV Extraction Prompt v2");
        assertThat(template.description()).isEqualTo("Updated description");
    }

    @Test
    void update_codeRemainsImmutable() {
        PromptTemplate template = PromptTemplate.create(AGENT_ID, "CV Extraction Prompt", CV_CODE, null);
        template.update("Updated Name", "desc");

        assertThat(template.code().value()).isEqualTo("CV_EXTRACTION_PROMPT");
    }
}
