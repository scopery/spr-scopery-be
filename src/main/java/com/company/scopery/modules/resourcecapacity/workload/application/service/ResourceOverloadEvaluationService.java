package com.company.scopery.modules.resourcecapacity.workload.application.service;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence.SpringDataAlertRuleJpaRepository;
import com.company.scopery.modules.notification.advanced.shared.util.SimpleConditionEvaluator;
import com.company.scopery.modules.notification.advanced.suppression.domain.model.NotificationSuppressionEntry;
import com.company.scopery.modules.notification.advanced.suppression.domain.model.NotificationSuppressionRepository;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationPriority;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationSeverity;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.resourcecapacity.shared.listeners.CapacityEventDefinitionSeedInitializer;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.WorkloadSnapshot;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.WorkloadSnapshotRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Overload → Phase 35 alert match + in-app NotificationItem fan-out + email trigger publish
 * into the existing EmailDispatch → EmailOutbox path (does not claim SMTP success itself).
 */
@Service
public class ResourceOverloadEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(ResourceOverloadEvaluationService.class);
    private static final String AGGREGATE_WORKLOAD_SNAPSHOT = "WORKLOAD_SNAPSHOT";
    private static final String EVENT_CODE = "RESOURCE_OVERLOAD_DETECTED";

    private final WorkloadSnapshotRepository snapshots;
    private final SpringDataAlertRuleJpaRepository alertRules;
    private final NotificationSuppressionRepository suppressions;
    private final TransactionalOutboxService outboxService;
    private final EventDefinitionRepository eventDefinitions;
    private final EmailNotificationTriggerPublisher emailTriggerPublisher;
    private final NotificationItemRepository notificationItems;
    private final WorkspaceMemberRepository workspaceMembers;

    public ResourceOverloadEvaluationService(
            WorkloadSnapshotRepository snapshots,
            SpringDataAlertRuleJpaRepository alertRules,
            NotificationSuppressionRepository suppressions,
            TransactionalOutboxService outboxService,
            EventDefinitionRepository eventDefinitions,
            EmailNotificationTriggerPublisher emailTriggerPublisher,
            NotificationItemRepository notificationItems,
            WorkspaceMemberRepository workspaceMembers) {
        this.snapshots = snapshots;
        this.alertRules = alertRules;
        this.suppressions = suppressions;
        this.outboxService = outboxService;
        this.eventDefinitions = eventDefinitions;
        this.emailTriggerPublisher = emailTriggerPublisher;
        this.notificationItems = notificationItems;
        this.workspaceMembers = workspaceMembers;
    }

    @Transactional
    public EvaluationResult evaluateAll() {
        int overloadSnapshots = 0;
        int matchedRules = 0;
        int suppressedRules = 0;
        int inAppCreated = 0;
        int emailTriggers = 0;
        for (WorkloadSnapshot snapshot : snapshots.findWithOverload()) {
            overloadSnapshots++;
            publishOverloadEvent(snapshot);
            boolean anyMatched = false;
            for (var rule : alertRules.findAll()) {
                if (!"ACTIVE".equals(rule.getStatus())) continue;
                if (!snapshot.workspaceId().equals(rule.getWorkspaceId())) continue;
                Map<String, String> ctx = Map.of(
                        "eventType", EVENT_CODE,
                        "overloadCount", String.valueOf(snapshot.overloadCount()),
                        "status", "ACTIVE");
                if (SimpleConditionEvaluator.matches(rule.getConditionJson(), ctx)) {
                    matchedRules++;
                    anyMatched = true;
                } else {
                    suppressedRules++;
                    suppressions.save(NotificationSuppressionEntry.create(
                            rule.getWorkspaceId(), null, null, "ALERT", "IN_APP",
                            "OVERLOAD_CONDITION_NOT_MATCHED", "ALERT_RULE", rule.getId()));
                }
            }
            if (anyMatched || alertRules.findAll().stream().noneMatch(r ->
                    "ACTIVE".equals(r.getStatus()) && snapshot.workspaceId().equals(r.getWorkspaceId()))) {
                // Fan-out when rules match, or when no alert rules exist yet for the workspace.
                inAppCreated += createInAppNotifications(snapshot);
                if (publishEmailTrigger(snapshot)) {
                    emailTriggers++;
                }
            }
        }
        if (overloadSnapshots > 0) {
            log.info("Resource overload evaluation: snapshots={}, matchedRules={}, suppressedRules={}, inApp={}, emailTriggers={}",
                    overloadSnapshots, matchedRules, suppressedRules, inAppCreated, emailTriggers);
        }
        return new EvaluationResult(overloadSnapshots, matchedRules, suppressedRules, inAppCreated, emailTriggers);
    }

    private int createInAppNotifications(WorkloadSnapshot snapshot) {
        int created = 0;
        List<WorkspaceMember> members = workspaceMembers
                .findAll(snapshot.workspaceId(), null, WorkspaceMemberStatus.ACTIVE, PageQuery.of(0, 100))
                .content();
        for (WorkspaceMember member : members) {
            String dedup = "OVERLOAD:" + snapshot.id() + ":" + member.userId();
            if (notificationItems.existsByRecipientUserIdAndDedupKey(member.userId(), dedup)) {
                continue;
            }
            try {
                NotificationItem item = NotificationItem.create(
                        member.userId(),
                        null,
                        CapacityEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                        AGGREGATE_WORKLOAD_SNAPSHOT,
                        snapshot.id(),
                        null,
                        snapshot.workspaceId(),
                        snapshot.projectId(),
                        "Resource overload detected",
                        "Overload count=" + snapshot.overloadCount() + " on workload snapshot",
                        NotificationSeverity.WARNING,
                        NotificationPriority.HIGH,
                        "VIEW_WORKLOAD",
                        null,
                        dedup,
                        false,
                        MDC.get("traceId"));
                notificationItems.save(item);
                created++;
            } catch (DataIntegrityViolationException | IllegalArgumentException ex) {
                log.debug("Skip in-app overload notification for user {}: {}", member.userId(), ex.getMessage());
            }
        }
        return created;
    }

    private boolean publishEmailTrigger(WorkloadSnapshot snapshot) {
        EventDefinition def = eventDefinitions.findByCode(EventDefinitionCode.of(EVENT_CODE)).orElse(null);
        if (def == null) {
            return false;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventCode", EVENT_CODE);
        payload.put("aggregateId", snapshot.id().toString());
        payload.put("workspace", Map.of("id", snapshot.workspaceId().toString()));
        if (snapshot.projectId() != null) {
            payload.put("project", Map.of("id", snapshot.projectId().toString()));
        }
        payload.put("overloadCount", snapshot.overloadCount());
        payload.put("capacityGapHours", snapshot.capacityGapHours());
        emailTriggerPublisher.publish(new EmailNotificationTriggerPayload(
                def.id(),
                CapacityEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                EVENT_CODE,
                snapshot.workspaceId(),
                null,
                payload));
        return true;
    }

    private void publishOverloadEvent(WorkloadSnapshot snapshot) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("snapshotId", snapshot.id());
        payload.put("workspaceId", snapshot.workspaceId());
        payload.put("projectId", snapshot.projectId());
        payload.put("overloadCount", snapshot.overloadCount());
        payload.put("capacityGapHours", snapshot.capacityGapHours());
        payload.put("workspace", Map.of("id", snapshot.workspaceId().toString()));
        outboxService.enqueue(
                AGGREGATE_WORKLOAD_SNAPSHOT,
                snapshot.id(),
                EVENT_CODE,
                CapacityEventDefinitionSeedInitializer.SOURCE_SYSTEM,
                1,
                payload);
    }

    public record EvaluationResult(
            int overloadSnapshots,
            int matchedRules,
            int suppressedRules,
            int inAppCreated,
            int emailTriggers) {}
}
