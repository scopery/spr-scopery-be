package com.company.scopery.modules.quality.qualityplan.domain.model;
import com.company.scopery.modules.quality.qualityplan.domain.enums.QualityPlanStatus;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class QualityPlanDomainTest {
    @Test void approveAndMarkCurrent() {
        var plan = QualityPlan.create(UUID.randomUUID(), UUID.randomUUID(), "QP-1", "Plan", null, null, null, null, null);
        assertEquals(QualityPlanStatus.DRAFT, plan.status());
        plan = plan.approve(UUID.randomUUID());
        assertEquals(QualityPlanStatus.APPROVED, plan.status());
        plan = plan.markCurrent();
        assertEquals(QualityPlanStatus.CURRENT, plan.status());
        assertTrue(plan.currentFlag());
    }
    @Test void updateApprovedPlan_immutable() {
        var plan = QualityPlan.create(UUID.randomUUID(), UUID.randomUUID(), "QP-2", "Plan", null, null, null, null, null);
        plan = plan.approve(UUID.randomUUID());
        var approved = plan;
        assertThrows(IllegalStateException.class, () -> approved.update("New", null, null, null, null, null));
    }
    @Test void archivePlan_success() {
        var plan = QualityPlan.create(UUID.randomUUID(), UUID.randomUUID(), "QP-3", "Plan", null, null, null, null, null);
        plan = plan.approve(UUID.randomUUID()).markCurrent().archive(UUID.randomUUID());
        assertEquals(QualityPlanStatus.ARCHIVED, plan.status());
        assertFalse(plan.currentFlag());
        assertNotNull(plan.archivedAt());
    }
    @Test void clearCurrentFlag_downgradesStatus() {
        var plan = QualityPlan.create(UUID.randomUUID(), UUID.randomUUID(), "QP-4", "Plan", null, null, null, null, null);
        plan = plan.approve(UUID.randomUUID()).markCurrent().clearCurrent();
        assertEquals(QualityPlanStatus.APPROVED, plan.status());
        assertFalse(plan.currentFlag());
    }
    @Test void draftCanBeUpdated() {
        var plan = QualityPlan.create(UUID.randomUUID(), UUID.randomUUID(), "QP-5", "Old", null, null, null, null, null);
        var updated = plan.update("New Name", "desc", "obj", "strategy", "entry", "exit");
        assertEquals("New Name", updated.name());
        assertEquals(QualityPlanStatus.DRAFT, updated.status());
    }
    @Test void approveFromArchivedThrows() {
        var plan = QualityPlan.create(UUID.randomUUID(), UUID.randomUUID(), "QP-6", "Plan", null, null, null, null, null);
        plan = plan.archive(UUID.randomUUID());
        var archived = plan;
        assertThrows(IllegalStateException.class, () -> archived.approve(UUID.randomUUID()));
    }
    @Test void create_hasExpectedDefaults() {
        var projectId = UUID.randomUUID();
        var wsId = UUID.randomUUID();
        var plan = QualityPlan.create(projectId, wsId, "QP-7", "Plan", null, null, null, null, null);
        assertEquals(projectId, plan.projectId());
        assertEquals(wsId, plan.workspaceId());
        assertEquals(QualityPlanStatus.DRAFT, plan.status());
        assertFalse(plan.currentFlag());
        assertNotNull(plan.id());
        assertNotNull(plan.createdAt());
    }
}
