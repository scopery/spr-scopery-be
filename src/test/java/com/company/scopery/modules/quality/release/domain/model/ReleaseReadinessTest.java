package com.company.scopery.modules.quality.release.domain.model;
import com.company.scopery.modules.quality.release.domain.enums.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class ReleaseReadinessTest {
    private ReleasePackage newRelease() {
        return ReleasePackage.create(UUID.randomUUID(), UUID.randomUUID(), "R1", "1.0.0", "Release", null, ReleaseType.MINOR, null);
    }
    @Test void markReadyBlockedWhenNotReady() {
        var rel = newRelease().withReadiness(ReadinessStatus.NOT_READY, "{}");
        assertThrows(IllegalStateException.class, rel::markReady);
        rel = rel.withReadiness(ReadinessStatus.READY, "{}");
        assertEquals(ReleasePackageStatus.READY_FOR_RELEASE, rel.markReady().status());
    }
    @Test void markReadyAllowedWithOverride() {
        var rel = newRelease().withReadiness(ReadinessStatus.OVERRIDE, "{\"override\":true}");
        assertEquals(ReleasePackageStatus.READY_FOR_RELEASE, rel.markReady().status());
    }
    @Test void markReleased_requiresReadyStatus() {
        var rel = newRelease().withReadiness(ReadinessStatus.READY, "{}");
        assertThrows(IllegalStateException.class, () -> rel.markReleased(UUID.randomUUID()));
        var ready = rel.markReady();
        var actor = UUID.randomUUID();
        var released = ready.markReleased(actor);
        assertEquals(ReleasePackageStatus.RELEASED, released.status());
        assertEquals(actor, released.releasedBy());
        assertNotNull(released.releasedAt());
        assertNotNull(released.actualReleaseDate());
    }
    @Test void markRolledBack_success() {
        var rel = newRelease().withReadiness(ReadinessStatus.READY, "{}").markReady().markRolledBack();
        assertEquals(ReleasePackageStatus.ROLLED_BACK, rel.status());
    }
    @Test void approve_success() {
        var actor = UUID.randomUUID();
        var rel = newRelease().approve(actor);
        assertEquals(actor, rel.approvedBy());
        assertNotNull(rel.approvedAt());
    }
    @Test void archive_success() {
        var rel = newRelease().archive(UUID.randomUUID());
        assertEquals(ReleasePackageStatus.ARCHIVED, rel.status());
        assertNotNull(rel.archivedAt());
    }
    @Test void create_hasExpectedDefaults() {
        var projectId = UUID.randomUUID();
        var wsId = UUID.randomUUID();
        var rel = ReleasePackage.create(projectId, wsId, "R-99", "2.0.0", "Name", null, ReleaseType.MAJOR, null);
        assertEquals(projectId, rel.projectId());
        assertEquals(wsId, rel.workspaceId());
        assertEquals(ReleasePackageStatus.DRAFT, rel.status());
        assertEquals(ReadinessStatus.NOT_CHECKED, rel.readinessStatus());
        assertNotNull(rel.id());
        assertNull(rel.releasedAt());
    }
    @Test void readinessCheck_notCheckedByDefault() {
        assertEquals(ReadinessStatus.NOT_CHECKED, newRelease().readinessStatus());
    }
    @Test void withReadiness_updatesStatus() {
        var rel = newRelease().withReadiness(ReadinessStatus.READY, "{\"passed\":9}");
        assertEquals(ReadinessStatus.READY, rel.readinessStatus());
        assertEquals("{\"passed\":9}", rel.readinessSummaryJson());
    }
}
