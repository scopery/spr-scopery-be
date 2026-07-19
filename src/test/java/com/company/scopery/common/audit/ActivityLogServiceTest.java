package com.company.scopery.common.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

    @Mock private ActivityLogRepository activityLogRepository;

    private ActivityLogService service;

    @BeforeEach
    void setUp() {
        service = new ActivityLogService(activityLogRepository);
        lenient().when(activityLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void logSuccess_persistsActorEntityActionAndTrace() {
        MDC.put("traceId", "act-trace");
        service.logSuccess("IAM", "IAM_USER", "user-1", "LOGIN_IAM_USER",
                "actor-1", "alice", "User logged in", null);

        ArgumentCaptor<ActivityLogJpaEntity> captor = ArgumentCaptor.forClass(ActivityLogJpaEntity.class);
        verify(activityLogRepository).save(captor.capture());
        ActivityLogJpaEntity entity = captor.getValue();
        assertThat(entity.getModuleCode()).isEqualTo("IAM");
        assertThat(entity.getEntityType()).isEqualTo("IAM_USER");
        assertThat(entity.getAction()).isEqualTo("LOGIN_IAM_USER");
        assertThat(entity.getActorId()).isEqualTo("actor-1");
        assertThat(entity.getTraceId()).isEqualTo("act-trace");
        assertThat(entity.getStatus()).isEqualTo(ActivityStatus.SUCCESS.name());
        MDC.clear();
    }

    @Test
    void activityActionConstants_areStable() {
        assertThat(ActivityAction.SYSTEM_STARTUP).isEqualTo("SYSTEM_STARTUP");
    }
}
