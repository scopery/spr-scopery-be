package com.company.scopery.modules.reporting.snapshot.domain.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReportSnapshotImmutabilityTest {

    @Test
    void reportSnapshotHasNoUpdateMethods() {
        Method[] methods = ReportSnapshot.class.getDeclaredMethods();
        assertThat(Arrays.stream(methods).map(Method::getName))
                .noneMatch(name -> name.startsWith("update") || name.startsWith("with") || name.equals("set"));
    }

    @Test
    void reportSnapshotCreatedAfterRun() {
        UUID runId = UUID.randomUUID();
        ReportSnapshot snap = ReportSnapshot.create(
                runId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "TASK_RISK", "{\"totalTasks\":1}", "{}", "{}");

        assertThat(snap.reportRunId()).isEqualTo(runId);
        assertThat(snap.dataJson()).contains("totalTasks");
        assertThat(snap.generatedAt()).isNotNull();
    }
}
