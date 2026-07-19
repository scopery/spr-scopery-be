package com.company.scopery.modules.aiplanning.planningrun.domain.model;

import com.company.scopery.modules.aiplanning.planningrun.domain.enums.PlanningRunStatus;
import com.company.scopery.modules.aiplanning.planningrun.domain.enums.PlanningRunType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiPlanningRunDomainTest {

    private AiPlanningRun pending() {
        return AiPlanningRun.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), PlanningRunType.PROJECT_PLAN_DRAFT, "{}", "trace");
    }

    @Test
    void createPlanningRun_valid_success() {
        AiPlanningRun run = pending();
        assertThat(run.status()).isEqualTo(PlanningRunStatus.PENDING);
        assertThat(run.completedAt()).isNull();
    }

    @Test
    void planningRun_doesNotMutateProjectData() {
        AiPlanningRun run = pending();
        assertThat(run.outputSummaryJson()).isNull();
        assertThat(run.contextSnapshotId()).isNull();
    }

    @Test
    void markRunning_andCompleted() {
        AiPlanningRun completed = pending().markRunning().markCompleted(UUID.randomUUID(), "{\"ok\":true}");
        assertThat(completed.status()).isEqualTo(PlanningRunStatus.COMPLETED);
        assertThat(completed.outputSummaryJson()).contains("ok");
    }

    @Test
    void cancel_fromPending_success() {
        assertThat(pending().cancel().status()).isEqualTo(PlanningRunStatus.CANCELLED);
    }

    @Test
    void cancel_fromCompleted_rejected() {
        AiPlanningRun completed = pending().markCompleted(UUID.randomUUID(), "{}");
        assertThatThrownBy(completed::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void markFailed_recordsError() {
        AiPlanningRun failed = pending().markFailed("PARSE_ERROR", "bad json");
        assertThat(failed.status()).isEqualTo(PlanningRunStatus.FAILED);
        assertThat(failed.errorCode()).isEqualTo("PARSE_ERROR");
    }
}
