package com.company.scopery.modules.reporting.run.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.reporting.dashboard.application.service.ProjectDashboardQueryService;
import com.company.scopery.modules.reporting.definition.domain.enums.ReportDefinitionStatus;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinition;
import com.company.scopery.modules.reporting.definition.domain.model.ReportDefinitionRepository;
import com.company.scopery.modules.reporting.run.application.command.CreateReportRunCommand;
import com.company.scopery.modules.reporting.run.application.response.CreateReportRunResponse;
import com.company.scopery.modules.reporting.run.domain.model.ReportRunRepository;
import com.company.scopery.modules.reporting.shared.activity.ReportingActivityLogger;
import com.company.scopery.modules.reporting.shared.authorization.ReportingAuthorizationService;
import com.company.scopery.modules.reporting.shared.error.ReportingErrorCatalog;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshot;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateReportRunActionTest {

    @Mock ReportDefinitionRepository definitions;
    @Mock ReportRunRepository runs;
    @Mock ReportSnapshotRepository snapshots;
    @Mock ProjectRepository projects;
    @Mock ProjectDashboardQueryService dashboard;
    @Mock ReportingAuthorizationService authorization;
    @Mock CurrentUserAuthorizationService currentUser;
    @Mock ReportingActivityLogger activityLogger;
    @Mock IamUser iamUser;

    private CreateReportRunAction action;
    private final UUID projectId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new CreateReportRunAction(definitions, runs, snapshots, projects, dashboard,
                authorization, currentUser, activityLogger, new ObjectMapper());
    }

    @Test
    void createReportRunValidSuccess() {
        ReportDefinition definition = definition(ReportDefinitionStatus.ACTIVE);
        when(definitions.findByCode("PROJECT_TASK_RISK_REPORT")).thenReturn(Optional.of(definition));
        when(projects.findById(projectId)).thenReturn(Optional.of(project()));
        when(currentUser.resolveCurrentUser()).thenReturn(iamUser);
        when(iamUser.id()).thenReturn(UUID.randomUUID());
        when(runs.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(snapshots.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(dashboard.resolveReportData("TASK_RISK", projectId)).thenReturn(Map.of("totalTasks", 2));
        when(authorization.canViewFinance(projectId)).thenReturn(true);
        when(authorization.canViewQuote(projectId)).thenReturn(true);

        CreateReportRunResponse response = action.execute(new CreateReportRunCommand(
                "PROJECT_TASK_RISK_REPORT", projectId, Map.of("includeArchived", false), null));

        assertThat(response.status()).isEqualTo("COMPLETED");
        assertThat(response.data().path("totalTasks").asInt()).isEqualTo(2);
        assertThat(response.snapshotId()).isNotNull();
        verify(snapshots).save(any(ReportSnapshot.class));
    }

    @Test
    void createReportRunInvalidReportCodeRejected() {
        when(definitions.findByCode("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> action.execute(new CreateReportRunCommand("MISSING", projectId, Map.of(), null)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ReportingErrorCatalog.REPORT_DEFINITION_NOT_FOUND.code()));
    }

    @Test
    void createReportRunProjectRequired() {
        assertThatThrownBy(() -> action.execute(new CreateReportRunCommand("PROJECT_TASK_RISK_REPORT", null, Map.of(), null)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ReportingErrorCatalog.REPORT_PROJECT_REQUIRED.code()));
    }

    @Test
    void deprecatedReportCannotRun() {
        when(definitions.findByCode("OLD")).thenReturn(Optional.of(definition(ReportDefinitionStatus.DEPRECATED)));

        assertThatThrownBy(() -> action.execute(new CreateReportRunCommand("OLD", projectId, Map.of(), null)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ReportingErrorCatalog.REPORT_DEFINITION_DEPRECATED.code()));
    }

    private ReportDefinition definition(ReportDefinitionStatus status) {
        Instant now = Instant.now();
        return new ReportDefinition(UUID.randomUUID(), "PROJECT_TASK_RISK_REPORT", "Task Risk", "desc",
                "PROJECT", "TASK_RISK", "[]", "[\"CSV\"]", null, "[]", status, 0, now, now);
    }

    private Project project() {
        return Project.create(workspaceId, UUID.randomUUID(), "P1", "Project", null,
                UUID.randomUUID(), "USD", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))
                .activate(UUID.randomUUID());
    }
}
