package com.company.scopery.modules.estimation.shared.support;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.estimation.estimationrun.domain.model.EstimationRun;
import com.company.scopery.modules.estimation.shared.listeners.EstimationEventDefinitionSeedInitializer;
import com.company.scopery.modules.project.project.domain.model.Project;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class EstimationPlatformPublisher {

    public static final String AGGREGATE_ESTIMATION_RUN = "ESTIMATION_RUN";

    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;

    public EstimationPlatformPublisher(TransactionalOutboxService outboxService,
                                       ImmutableAuditEventService auditEventService) {
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
    }

    public void enqueueRun(EstimationRun run, String eventCode) {
        outboxService.enqueue(
                AGGREGATE_ESTIMATION_RUN,
                run.id(),
                eventCode,
                EstimationEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                mapOf(
                        "estimationRunId", run.id(),
                        "projectId", run.projectId(),
                        "workspaceId", run.workspaceId(),
                        "status", run.status().name()));
    }

    public void auditRunCompleted(UUID actorUserId, Project project, EstimationRun run) {
        auditEventService.record(AuditEventType.ESTIMATION_RUN_COMPLETED, actorUserId, "USER",
                AGGREGATE_ESTIMATION_RUN, run.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id(), "status", run.status().name()),
                "Estimation run completed");
    }

    public void auditRunFailed(UUID actorUserId, Project project, EstimationRun run) {
        auditEventService.record(AuditEventType.ESTIMATION_RUN_FAILED, actorUserId, "USER",
                AGGREGATE_ESTIMATION_RUN, run.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id(), "errorCode", run.errorCode()),
                "Estimation run failed");
    }

    public void auditMarkedCurrent(UUID actorUserId, Project project, EstimationRun run) {
        auditEventService.record(AuditEventType.ESTIMATION_RUN_MARKED_CURRENT, actorUserId, "USER",
                AGGREGATE_ESTIMATION_RUN, run.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()),
                "Estimation run marked current");
    }

    public void auditRateSnapshotUsed(UUID actorUserId, Project project, EstimationRun run) {
        auditEventService.record(AuditEventType.ESTIMATION_RATE_SNAPSHOT_USED, actorUserId, "USER",
                AGGREGATE_ESTIMATION_RUN, run.id(), project.organizationId(), project.workspaceId(),
                null, mapOf("projectId", project.id()),
                "Estimation rate snapshots used");
    }

    public static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
