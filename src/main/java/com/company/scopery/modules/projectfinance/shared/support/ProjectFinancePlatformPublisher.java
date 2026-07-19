package com.company.scopery.modules.projectfinance.shared.support;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.shared.listeners.ProjectFinanceEventDefinitionSeedInitializer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ProjectFinancePlatformPublisher {

    public static final String AGGREGATE_FINANCE_SCENARIO = "PROJECT_FINANCE_SCENARIO";

    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;

    public ProjectFinancePlatformPublisher(TransactionalOutboxService outboxService,
                                           ImmutableAuditEventService auditEventService) {
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
    }

    public void enqueueScenario(ProjectFinanceScenario scenario, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_FINANCE_SCENARIO,
                scenario.id(),
                eventCode,
                ProjectFinanceEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                mapOf(
                        "financeScenarioId", scenario.id(),
                        "projectId", scenario.projectId(),
                        "workspaceId", scenario.workspaceId(),
                        "estimationRunId", scenario.estimationRunId(),
                        "status", scenario.status().name(),
                        "currencyCode", scenario.currencyCode(),
                        "plannedRevenue", scenario.plannedRevenue()));
    }

    public void auditApproved(UUID actorUserId, Project project, ProjectFinanceScenario scenario) {
        auditEventService.record(AuditEventType.PROJECT_FINANCE_SCENARIO_APPROVED, actorUserId, "USER",
                AGGREGATE_FINANCE_SCENARIO, scenario.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()), "Finance scenario approved");
    }

    public void auditMarkedCurrent(UUID actorUserId, Project project, ProjectFinanceScenario scenario) {
        auditEventService.record(AuditEventType.PROJECT_FINANCE_SCENARIO_MARKED_CURRENT, actorUserId, "USER",
                AGGREGATE_FINANCE_SCENARIO, scenario.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()), "Finance scenario marked current");
    }

    public void auditRecalculated(UUID actorUserId, Project project, ProjectFinanceScenario scenario) {
        auditEventService.record(AuditEventType.PROJECT_FINANCE_SCENARIO_RECALCULATED, actorUserId, "USER",
                AGGREGATE_FINANCE_SCENARIO, scenario.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()), "Finance scenario recalculated");
    }

    public void auditRevenueChanged(UUID actorUserId, Project project, ProjectFinanceScenario scenario) {
        auditEventService.record(AuditEventType.PROJECT_FINANCE_REVENUE_CHANGED, actorUserId, "USER",
                AGGREGATE_FINANCE_SCENARIO, scenario.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()), "Finance revenue changed");
    }

    public void auditCostChanged(UUID actorUserId, Project project, ProjectFinanceScenario scenario) {
        auditEventService.record(AuditEventType.PROJECT_FINANCE_COST_CHANGED, actorUserId, "USER",
                AGGREGATE_FINANCE_SCENARIO, scenario.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()), "Finance cost changed");
    }

    public void auditOverheadChanged(UUID actorUserId, Project project, ProjectFinanceScenario scenario) {
        auditEventService.record(AuditEventType.PROJECT_FINANCE_OVERHEAD_CHANGED, actorUserId, "USER",
                AGGREGATE_FINANCE_SCENARIO, scenario.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()), "Finance overhead changed");
    }

    public void auditContingencyChanged(UUID actorUserId, Project project, ProjectFinanceScenario scenario) {
        auditEventService.record(AuditEventType.PROJECT_FINANCE_CONTINGENCY_CHANGED, actorUserId, "USER",
                AGGREGATE_FINANCE_SCENARIO, scenario.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()), "Finance contingency changed");
    }

    public static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
