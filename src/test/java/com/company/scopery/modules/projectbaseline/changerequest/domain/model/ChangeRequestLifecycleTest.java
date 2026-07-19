package com.company.scopery.modules.projectbaseline.changerequest.domain.model;

import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangeRequestStatus;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangeType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeRequestLifecycleTest {

    @Test
    void createChangeRequest_valid_success() {
        ChangeRequest cr = draft();
        assertThat(cr.status()).isEqualTo(ChangeRequestStatus.DRAFT);
        assertThat(cr.isEditable()).isTrue();
    }

    @Test
    void submitWithItems_success() {
        ChangeRequest cr = draft().submit(UUID.randomUUID());
        assertThat(cr.status()).isEqualTo(ChangeRequestStatus.SUBMITTED);
        assertThat(cr.isEditable()).isFalse();
    }

    @Test
    void approveSubmitted_success() {
        ChangeRequest cr = draft().submit(UUID.randomUUID()).approve(UUID.randomUUID());
        assertThat(cr.status()).isEqualTo(ChangeRequestStatus.APPROVED);
    }

    @Test
    void rejectRequiresReason() {
        ChangeRequest cr = draft().submit(UUID.randomUUID()).reject(UUID.randomUUID(), "not needed");
        assertThat(cr.status()).isEqualTo(ChangeRequestStatus.REJECTED);
        assertThat(cr.rejectionReason()).isEqualTo("not needed");
    }

    @Test
    void approvedCanApply() {
        ChangeRequest cr = draft().submit(UUID.randomUUID()).approve(UUID.randomUUID()).apply(UUID.randomUUID());
        assertThat(cr.status()).isEqualTo(ChangeRequestStatus.APPLIED);
        assertThat(cr.appliedAt()).isNotNull();
    }

    @Test
    void appliedCannotEdit() {
        ChangeRequest cr = draft().submit(UUID.randomUUID()).approve(UUID.randomUUID()).apply(UUID.randomUUID());
        assertThat(cr.isEditable()).isFalse();
    }

    @Test
    void cancelDraft_success() {
        ChangeRequest cr = draft().cancel(UUID.randomUUID());
        assertThat(cr.status()).isEqualTo(ChangeRequestStatus.CANCELLED);
    }

    private ChangeRequest draft() {
        return ChangeRequest.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "CR-001", "Title", "desc", ChangeType.SCOPE_CHANGE, null, "reason",
                UUID.randomUUID(), "trace");
    }
}
