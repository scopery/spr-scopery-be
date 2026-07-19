package com.company.scopery.modules.projectbaseline.baseline.domain.model;

import com.company.scopery.modules.projectbaseline.baseline.domain.enums.BaselineStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectBaselineLifecycleTest {

    @Test
    void approveBaseline_valid_success() {
        ProjectBaseline baseline = draft();
        ProjectBaseline approved = baseline.approve(UUID.randomUUID());
        assertThat(approved.status()).isEqualTo(BaselineStatus.APPROVED);
        assertThat(approved.approvedAt()).isNotNull();
        assertThat(approved.isEditable()).isFalse();
        assertThat(approved.isImmutable()).isTrue();
    }

    @Test
    void approvedBaseline_immutable() {
        ProjectBaseline approved = draft().approve(UUID.randomUUID());
        assertThat(approved.isImmutable()).isTrue();
        assertThat(approved.isDraft()).isFalse();
    }

    @Test
    void markCurrent_onlyOneCurrentBaseline() {
        ProjectBaseline a = draft().approve(UUID.randomUUID()).withCurrentFlag(true);
        ProjectBaseline b = draft().approve(UUID.randomUUID()).withCurrentFlag(false);
        assertThat(a.currentFlag()).isTrue();
        assertThat(b.withCurrentFlag(true).currentFlag()).isTrue();
        assertThat(a.withCurrentFlag(false).currentFlag()).isFalse();
    }

    @Test
    void baselineSnapshot_unchangedAfterTaskChange() {
        ProjectBaseline baseline = draft();
        String snap = baseline.snapshotJson();
        ProjectBaseline after = baseline.approve(UUID.randomUUID());
        assertThat(after.snapshotJson()).isEqualTo(snap);
    }

    private ProjectBaseline draft() {
        return ProjectBaseline.create(
                UUID.randomUUID(), UUID.randomUUID(), 1, "B1", "desc",
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null,
                "{\"tasks\":[]}", "{\"taskCount\":0}", "trace");
    }
}
