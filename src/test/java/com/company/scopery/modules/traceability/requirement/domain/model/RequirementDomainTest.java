package com.company.scopery.modules.traceability.requirement.domain.model;

import com.company.scopery.modules.traceability.requirement.domain.enums.*;
import com.company.scopery.common.exception.AppException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequirementDomainTest {

    private Requirement draft() {
        return Requirement.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                "REQ-1",
                "Login",
                "desc",
                RequirementType.FUNCTIONAL,
                RequirementPriority.HIGH,
                null,
                null,
                null,
                null);
    }

    @Test
    void approve() {
        var r = draft();
        assertEquals(RequirementStatus.DRAFT, r.status());
        assertEquals(RequirementStatus.APPROVED, r.approve().status());
    }

    @Test
    void approved_blocksContentUpdate() {
        var approved = draft().approve();
        AppException ex = assertThrows(AppException.class, () ->
                approved.update("New title", null, null, null, null, null, null, null, null, null));
        assertEquals("REQUIREMENT_APPROVED_IMMUTABLE", ex.getErrorCode());
    }

    @Test
    void approved_allowsFunctionalItemLinkUpdate() {
        UUID functionId = UUID.randomUUID();
        var approved = draft().approve();
        var linked = approved.update(null, null, null, null, null, functionId, null, null, null, null);
        assertEquals(RequirementStatus.APPROVED, linked.status());
        assertEquals(functionId, linked.functionalItemId());
        assertEquals("Login", linked.title());
    }

    @Test
    void archived_allowsContentUpdate() {
        var archived = draft().archive();
        var updated = archived.update("Restored title", null, null, null, null, null, null, null, null, null);
        assertEquals(RequirementStatus.ARCHIVED, updated.status());
        assertEquals("Restored title", updated.title());
    }
}
