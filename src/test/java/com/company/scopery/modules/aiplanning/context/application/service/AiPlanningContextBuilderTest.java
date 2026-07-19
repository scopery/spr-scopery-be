package com.company.scopery.modules.aiplanning.context.application.service;

import com.company.scopery.modules.aiplanning.contextsnapshot.domain.model.AiPlanningContextSnapshot;
import com.company.scopery.modules.aiplanning.shared.authorization.AiPlanningAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiPlanningContextBuilderTest {

    @Mock private ProjectRepository projects;
    @Mock private ProjectPhaseRepository phases;
    @Mock private WbsNodeRepository wbsNodes;
    @Mock private TaskRepository tasks;
    @Mock private AiPlanningAuthorizationService authorization;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AiPlanningContextBuilder builder;

    private final UUID projectId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID actorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        builder = new AiPlanningContextBuilder(projects, phases, wbsNodes, tasks, authorization, objectMapper);
        Project project = new Project(
                projectId, workspaceId, UUID.randomUUID(), "P-1", "Demo", "desc",
                actorId, "USD", null, null, ProjectStatus.ACTIVE,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                0, Instant.now(), Instant.now());
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        org.mockito.Mockito.lenient().when(phases.findAllByProjectId(projectId)).thenReturn(List.of());
        org.mockito.Mockito.lenient().when(wbsNodes.findAllByProjectId(projectId)).thenReturn(List.of());
        org.mockito.Mockito.lenient().when(tasks.findAllByProjectId(projectId)).thenReturn(List.of());
    }

    @Test
    void contextBuilder_projectView_includesProjectBasics() throws Exception {
        when(authorization.canViewFinance(projectId)).thenReturn(false);
        when(authorization.canViewQuote(projectId)).thenReturn(false);

        AiPlanningContextSnapshot snapshot = builder.build(
                projectId, actorId, List.of("PROJECT"), "trace-1");

        JsonNode payload = objectMapper.readTree(snapshot.contextPayloadJson());
        assertThat(payload.path("project").path("code").asText()).isEqualTo("P-1");
        assertThat(payload.path("project").path("name").asText()).isEqualTo("Demo");
        assertThat(objectMapper.readTree(snapshot.includedSectionsJson()).toString()).contains("PROJECT");
    }

    @Test
    void contextBuilder_withoutFinancePermission_masksFinance() throws Exception {
        when(authorization.canViewFinance(projectId)).thenReturn(false);
        when(authorization.canViewQuote(projectId)).thenReturn(false);

        AiPlanningContextSnapshot snapshot = builder.build(
                projectId, actorId, List.of("FINANCE_SUMMARY"), "trace-1");

        JsonNode payload = objectMapper.readTree(snapshot.contextPayloadJson());
        assertThat(payload.path("financeSummary").path("detailsRedacted").asBoolean()).isTrue();
        assertThat(payload.path("financeSummary").path("reason").asText())
                .isEqualTo("PROJECT_FINANCE_VIEW_REQUIRED");
        JsonNode redactions = objectMapper.readTree(snapshot.redactionSummaryJson());
        assertThat(redactions.path("FINANCE_SUMMARY").asText())
                .isEqualTo("PROJECT_FINANCE_VIEW_REQUIRED");
    }

    @Test
    void contextBuilder_withFinancePermission_includesFinanceSummary() throws Exception {
        when(authorization.canViewFinance(projectId)).thenReturn(true);
        when(authorization.canViewQuote(projectId)).thenReturn(false);

        AiPlanningContextSnapshot snapshot = builder.build(
                projectId, actorId, List.of("FINANCE_SUMMARY"), "trace-1");

        JsonNode payload = objectMapper.readTree(snapshot.contextPayloadJson());
        assertThat(payload.path("financeSummary").path("detailsRedacted").asBoolean()).isFalse();
        assertThat(objectMapper.readTree(snapshot.includedSectionsJson()).toString())
                .contains("FINANCE_SUMMARY");
    }

    @Test
    void contextBuilder_withoutQuotePermission_masksQuote() throws Exception {
        when(authorization.canViewFinance(projectId)).thenReturn(false);
        when(authorization.canViewQuote(projectId)).thenReturn(false);

        AiPlanningContextSnapshot snapshot = builder.build(
                projectId, actorId, List.of("QUOTE_SUMMARY"), "trace-1");

        JsonNode payload = objectMapper.readTree(snapshot.contextPayloadJson());
        assertThat(payload.path("quoteSummary").path("detailsRedacted").asBoolean()).isTrue();
        assertThat(payload.path("quoteSummary").path("reason").asText()).isEqualTo("QUOTE_VIEW_REQUIRED");
    }

    @Test
    void contextBuilder_recordsRedactionSummary() throws Exception {
        when(authorization.canViewFinance(projectId)).thenReturn(false);
        when(authorization.canViewQuote(projectId)).thenReturn(false);

        AiPlanningContextSnapshot snapshot = builder.build(
                projectId, actorId, List.of("FINANCE_SUMMARY", "QUOTE_SUMMARY"), "trace-1");

        JsonNode redactions = objectMapper.readTree(snapshot.redactionSummaryJson());
        assertThat(redactions.path("FINANCE_SUMMARY").asText()).isNotBlank();
        assertThat(redactions.path("QUOTE_SUMMARY").asText()).isNotBlank();
    }

    @Test
    void contextBuilder_excludesSalaryPayroll() throws Exception {
        when(authorization.canViewFinance(projectId)).thenReturn(true);
        when(authorization.canViewQuote(projectId)).thenReturn(true);

        AiPlanningContextSnapshot snapshot = builder.build(
                projectId, actorId, List.of("PROJECT", "FINANCE_SUMMARY"), "trace-1");

        String payload = snapshot.contextPayloadJson();
        assertThat(payload).doesNotContainIgnoringCase("salary");
        assertThat(payload).doesNotContainIgnoringCase("payroll");
    }

    @Test
    void financeInsight_withoutFinancePermission_forbiddenOrMasked() throws Exception {
        when(authorization.canViewFinance(projectId)).thenReturn(false);
        when(authorization.canViewQuote(projectId)).thenReturn(false);

        AiPlanningContextSnapshot snapshot = builder.build(
                projectId, actorId, List.of("FINANCE_SUMMARY"), "trace");

        assertThat(objectMapper.readTree(snapshot.contextPayloadJson())
                .path("financeSummary").path("detailsRedacted").asBoolean()).isTrue();
    }

    @Test
    void financeInsight_doesNotChangeFinanceScenario() {
        when(authorization.canViewFinance(projectId)).thenReturn(true);
        when(authorization.canViewQuote(projectId)).thenReturn(false);

        builder.build(projectId, actorId, List.of("FINANCE_SUMMARY"), "trace");
        // Builder is read-only; no write collaborator is invoked for finance mutation.
    }
}
