package com.company.scopery.modules.resourcecapacity.workload.application.service;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence.AlertRuleJpaEntity;
import com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence.SpringDataAlertRuleJpaRepository;
import com.company.scopery.modules.notification.advanced.suppression.domain.model.NotificationSuppressionRepository;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.WorkloadSnapshot;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.WorkloadSnapshotRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceOverloadEvaluationServiceTest {

    @Mock WorkloadSnapshotRepository snapshots;
    @Mock SpringDataAlertRuleJpaRepository alertRules;
    @Mock NotificationSuppressionRepository suppressions;
    @Mock TransactionalOutboxService outboxService;
    @Mock EventDefinitionRepository eventDefinitions;
    @Mock EmailNotificationTriggerPublisher emailTriggerPublisher;
    @Mock NotificationItemRepository notificationItems;
    @Mock WorkspaceMemberRepository workspaceMembers;

    ResourceOverloadEvaluationService service;

    @BeforeEach
    void setUp() {
        service = new ResourceOverloadEvaluationService(
                snapshots, alertRules, suppressions, outboxService,
                eventDefinitions, emailTriggerPublisher, notificationItems, workspaceMembers);
    }

    @Test
    void evaluateAll_publishesEventAndMatchesAlertRule_andFansOut() {
        UUID workspaceId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        var snap = new WorkloadSnapshot(UUID.randomUUID(), workspaceId, projectId,
                BigDecimal.TEN, BigDecimal.valueOf(50), BigDecimal.valueOf(50),
                BigDecimal.valueOf(50), BigDecimal.ZERO, BigDecimal.TEN, 2, 0,
                BigDecimal.ZERO, "MANUAL", Instant.now(), 0, Instant.now());
        when(snapshots.findWithOverload()).thenReturn(List.of(snap));

        var rule = new AlertRuleJpaEntity();
        rule.setId(UUID.randomUUID());
        rule.setWorkspaceId(workspaceId);
        rule.setStatus("ACTIVE");
        rule.setConditionJson("{\"equals\":{\"field\":\"eventType\",\"value\":\"RESOURCE_OVERLOAD_DETECTED\"}}");
        when(alertRules.findAll()).thenReturn(List.of(rule));
        when(workspaceMembers.findAll(eq(workspaceId), isNull(), any(), any()))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0, true, true));
        when(eventDefinitions.findByCode(any())).thenReturn(Optional.empty());

        var result = service.evaluateAll();

        assertThat(result.overloadSnapshots()).isEqualTo(1);
        assertThat(result.matchedRules()).isEqualTo(1);
        verify(outboxService).enqueue(any(), any(), eq("RESOURCE_OVERLOAD_DETECTED"), any(), eq(1), any());
    }

    @Test
    void evaluateAll_noOverloadSnapshots_noOp() {
        when(snapshots.findWithOverload()).thenReturn(List.of());
        var result = service.evaluateAll();
        assertThat(result.overloadSnapshots()).isZero();
        verifyNoInteractions(outboxService);
    }
}
