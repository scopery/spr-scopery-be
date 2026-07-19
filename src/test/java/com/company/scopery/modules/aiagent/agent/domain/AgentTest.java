package com.company.scopery.modules.aiagent.agent.domain;

import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.valueobject.AgentCode;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentAutonomyLevel;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentScope;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AgentTest {

    private static final AgentCode CV_CODE = AgentCode.of("CV_EXTRACTION_AGENT");

    @Test
    void create_validAgent_isActiveByDefault() {
        Agent agent = Agent.create("CV Extraction Agent", CV_CODE,
                AgentType.EXTRACTION, null, null, AgentOutputFormat.JSON);

        assertThat(agent.id()).isNotNull();
        assertThat(agent.status()).isEqualTo(AgentStatus.ACTIVE);
        assertThat(agent.code().value()).isEqualTo("CV_EXTRACTION_AGENT");
        assertThat(agent.outputFormat()).isEqualTo(AgentOutputFormat.JSON);
        assertThat(agent.defaultModelDeploymentId()).isNull();
        assertThat(agent.autonomyLevel()).isEqualTo(AgentAutonomyLevel.SUGGEST_ONLY);
        assertThat(agent.scope()).isEqualTo(AgentScope.SYSTEM);
    }

    @Test
    void create_withNullDefaultDeployment_succeeds() {
        Agent agent = Agent.create("Email Drafting Agent", AgentCode.of("EMAIL_DRAFTING"),
                AgentType.GENERATION, "Drafts emails", null, AgentOutputFormat.TEXT);

        assertThat(agent.defaultModelDeploymentId()).isNull();
        assertThat(agent.status()).isEqualTo(AgentStatus.ACTIVE);
    }

    @Test
    void create_blankName_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> Agent.create("", CV_CODE,
                AgentType.EXTRACTION, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Agent name is required");
    }

    @Test
    void create_nullOutputFormat_isAllowed() {
        Agent agent = Agent.create("Contract Review", AgentCode.of("CONTRACT_REVIEW"),
                AgentType.VALIDATION, null, null, null);

        assertThat(agent.outputFormat()).isNull();
    }

    @Test
    void deactivate_setsStatusToInactive() {
        Agent agent = Agent.create("Test Agent", CV_CODE, AgentType.EXTRACTION, null, null, null);
        agent.deactivate();

        assertThat(agent.status()).isEqualTo(AgentStatus.INACTIVE);
    }

    @Test
    void activate_fromInactive_setsStatusToActive() {
        Agent agent = Agent.create("Test Agent", CV_CODE, AgentType.EXTRACTION, null, null, null);
        agent.deactivate();
        agent.activate();

        assertThat(agent.status()).isEqualTo(AgentStatus.ACTIVE);
    }

    @Test
    void activate_deprecatedAgent_throwsIllegalStateException() {
        Agent agent = Agent.reconstitute(UUID.randomUUID(), "Old Agent", CV_CODE,
                AgentType.EXTRACTION, null, null, null,
                AgentStatus.DEPRECATED, Instant.now(), Instant.now());

        assertThatThrownBy(agent::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Deprecated agent cannot be activated again");
    }

    @Test
    void update_changesAllowedFields() {
        Agent agent = Agent.create("CV Extraction Agent", CV_CODE,
                AgentType.EXTRACTION, null, null, null);
        UUID newDeploymentId = UUID.randomUUID();

        agent.update("CV Extraction Agent v2", AgentType.SUMMARIZATION,
                "Updated description", newDeploymentId, AgentOutputFormat.MARKDOWN);

        assertThat(agent.name()).isEqualTo("CV Extraction Agent v2");
        assertThat(agent.type()).isEqualTo(AgentType.SUMMARIZATION);
        assertThat(agent.description()).isEqualTo("Updated description");
        assertThat(agent.defaultModelDeploymentId()).isEqualTo(newDeploymentId);
        assertThat(agent.outputFormat()).isEqualTo(AgentOutputFormat.MARKDOWN);
    }

    @Test
    void update_codeRemainsImmutable() {
        Agent agent = Agent.create("CV Agent", CV_CODE, AgentType.EXTRACTION, null, null, null);
        agent.update("CV Agent Updated", AgentType.EXTRACTION, null, null, null);

        assertThat(agent.code().value()).isEqualTo("CV_EXTRACTION_AGENT");
    }
}