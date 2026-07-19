package com.company.scopery.modules.projectbaseline.shared.support;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequest;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrder;
import com.company.scopery.modules.projectbaseline.shared.listeners.ProjectBaselineEventDefinitionSeedInitializer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ProjectBaselinePlatformPublisher {
    public static final String AGG_BASELINE = "PROJECT_BASELINE";
    public static final String AGG_CHANGE_REQUEST = "CHANGE_REQUEST";
    public static final String AGG_CHANGE_ORDER = "CHANGE_ORDER";

    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;

    public ProjectBaselinePlatformPublisher(TransactionalOutboxService outboxService,
                                            ImmutableAuditEventService auditEventService) {
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
    }

    public void enqueueBaseline(ProjectBaseline b, String eventCode) {
        outboxService.enqueue(AGG_BASELINE, b.id(), eventCode,
                ProjectBaselineEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1,
                mapOf("baselineId", b.id(), "projectId", b.projectId(), "workspaceId", b.workspaceId(),
                        "baselineNumber", b.baselineNumber(), "status", b.status().name()));
    }

    public void enqueueChangeRequest(ChangeRequest cr, String eventCode) {
        outboxService.enqueue(AGG_CHANGE_REQUEST, cr.id(), eventCode,
                ProjectBaselineEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1,
                mapOf("changeRequestId", cr.id(), "projectId", cr.projectId(), "workspaceId", cr.workspaceId(),
                        "code", cr.code(), "status", cr.status().name()));
    }

    public void enqueueChangeOrder(ChangeOrder co, String eventCode) {
        outboxService.enqueue(AGG_CHANGE_ORDER, co.id(), eventCode,
                ProjectBaselineEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1,
                mapOf("changeOrderId", co.id(), "changeRequestId", co.changeRequestId(),
                        "projectId", co.projectId(), "code", co.code(), "status", co.status().name()));
    }

    public void audit(AuditEventType type, UUID actorUserId, Project project, String aggregateType,
                      UUID aggregateId, String message) {
        auditEventService.record(type, actorUserId, "USER", aggregateType, aggregateId,
                project.organizationId(), project.workspaceId(), null,
                mapOf("projectId", project.id()), message);
    }

    public static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
