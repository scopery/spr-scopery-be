package com.company.scopery.modules.quality.defect.domain.model;
import com.company.scopery.modules.quality.defect.domain.enums.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class DefectLifecycleTest {
    private Defect newDefect(DefectSeverity severity) {
        return Defect.create(UUID.randomUUID(), UUID.randomUUID(), "D-X", "Bug", null,
                DefectCategory.FUNCTIONAL, severity, DefectPriority.P1, UUID.randomUUID(), null, null, null, null);
    }
    @Test void closeRequiresResolution() {
        var d = newDefect(DefectSeverity.CRITICAL);
        assertThrows(IllegalArgumentException.class, () -> d.close(UUID.randomUUID(), null, null));
        var closed = d.close(UUID.randomUUID(), DefectResolutionType.FIXED, "fixed in PR");
        assertEquals(DefectStatus.CLOSED, closed.status());
        assertNotNull(closed.closedAt());
        assertEquals(DefectResolutionType.FIXED, closed.resolutionType());
    }
    @Test void reopenRequiresReason() {
        var d = newDefect(DefectSeverity.MAJOR).close(UUID.randomUUID(), DefectResolutionType.FIXED, "done");
        assertThrows(IllegalArgumentException.class, () -> d.reopen(UUID.randomUUID(), " "));
        assertThrows(IllegalArgumentException.class, () -> d.reopen(UUID.randomUUID(), null));
        assertEquals(DefectStatus.REOPENED, d.reopen(UUID.randomUUID(), "regressed").status());
    }
    @Test void openBlockerDetection() {
        var blocker = newDefect(DefectSeverity.BLOCKER);
        assertTrue(blocker.isOpenBlocker());
        assertFalse(blocker.close(UUID.randomUUID(), DefectResolutionType.FIXED, "x").isOpenBlocker());
    }
    @Test void criticalIsOpenBlocker() {
        assertTrue(newDefect(DefectSeverity.CRITICAL).isOpenBlocker());
    }
    @Test void majorIsNotOpenBlocker() {
        assertFalse(newDefect(DefectSeverity.MAJOR).isOpenBlocker());
    }
    @Test void triageDefect_success() {
        var d = newDefect(DefectSeverity.MAJOR).triage();
        assertEquals(DefectStatus.TRIAGED, d.status());
    }
    @Test void assignDefect_success() {
        var assignee = UUID.randomUUID();
        var d = newDefect(DefectSeverity.MAJOR).assign(assignee);
        assertEquals(DefectStatus.ASSIGNED, d.status());
        assertEquals(assignee, d.assignedToUserId());
    }
    @Test void markFixed_success() {
        var actor = UUID.randomUUID();
        var d = newDefect(DefectSeverity.MAJOR).markFixed(actor);
        assertEquals(DefectStatus.FIXED, d.status());
        assertNotNull(d.resolvedAt());
        assertEquals(actor, d.resolvedBy());
    }
    @Test void readyForRetest_success() {
        var d = newDefect(DefectSeverity.MINOR).readyForRetest();
        assertEquals(DefectStatus.READY_FOR_RETEST, d.status());
    }
    @Test void verify_success() {
        var d = newDefect(DefectSeverity.MINOR).verify();
        assertEquals(DefectStatus.VERIFIED, d.status());
    }
    @Test void verifiedIsNotOpenBlocker() {
        var d = newDefect(DefectSeverity.BLOCKER).verify();
        assertFalse(d.isOpenBlocker());
    }
    @Test void fullLifecycle_openToVerified() {
        var d = newDefect(DefectSeverity.MAJOR)
                .triage()
                .assign(UUID.randomUUID())
                .markFixed(UUID.randomUUID())
                .readyForRetest()
                .verify();
        assertEquals(DefectStatus.VERIFIED, d.status());
    }
    @Test void archive_success() {
        var actor = UUID.randomUUID();
        var d = newDefect(DefectSeverity.MINOR).archive(actor);
        assertEquals(DefectStatus.ARCHIVED, d.status());
        assertNotNull(d.archivedAt());
        assertFalse(d.isOpenBlocker());
    }
    @Test void create_hasExpectedDefaults() {
        var projectId = UUID.randomUUID();
        var wsId = UUID.randomUUID();
        var d = Defect.create(projectId, wsId, "D-1", "Title", null, DefectCategory.INTEGRATION,
                DefectSeverity.MAJOR, DefectPriority.P2, UUID.randomUUID(), null, null, null, null);
        assertEquals(projectId, d.projectId());
        assertEquals(wsId, d.workspaceId());
        assertEquals(DefectStatus.OPEN, d.status());
        assertNull(d.assignedToUserId());
        assertNull(d.resolutionType());
        assertNotNull(d.id());
    }
}
