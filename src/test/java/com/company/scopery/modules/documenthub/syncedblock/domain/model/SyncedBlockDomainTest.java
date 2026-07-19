package com.company.scopery.modules.documenthub.syncedblock.domain.model;

import com.company.scopery.modules.documenthub.syncedblock.domain.enums.SyncedBlockStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests BR-NDE-045: synced block must not be recursive (covered by PushSyncedBlockUpdateAction BFS).
 * Domain-level tests cover state transitions.
 */
class SyncedBlockDomainTest {

    final UUID workspaceId = UUID.randomUUID();
    final UUID projectId = UUID.randomUUID();

    @Test
    void create_statusActiveAndRevisionZero() {
        var block = SyncedBlock.create(workspaceId, projectId, "Finance Block", 1);

        assertThat(block.status()).isEqualTo(SyncedBlockStatus.ACTIVE);
        assertThat(block.currentRevisionNo()).isEqualTo(0L);
        assertThat(block.title()).isEqualTo("Finance Block");
        assertThat(block.workspaceId()).isEqualTo(workspaceId);
        assertThat(block.projectId()).isEqualTo(projectId);
    }

    @Test
    void withRevision_incrementsRevisionNo() {
        var block = SyncedBlock.create(workspaceId, projectId, "Block A", 1);
        assertThat(block.currentRevisionNo()).isEqualTo(0L);

        var updated = block.withRevision(1L);
        assertThat(updated.currentRevisionNo()).isEqualTo(1L);
        assertThat(updated.id()).isEqualTo(block.id());
        assertThat(updated.status()).isEqualTo(SyncedBlockStatus.ACTIVE);
    }

    @Test
    void withRevision_multipleIncrements() {
        var block = SyncedBlock.create(workspaceId, projectId, "Block A", 1);
        var rev1 = block.withRevision(1L);
        var rev2 = rev1.withRevision(2L);
        var rev3 = rev2.withRevision(3L);

        assertThat(rev3.currentRevisionNo()).isEqualTo(3L);
        assertThat(rev3.id()).isEqualTo(block.id());
    }

    @Test
    void archive_changesStatusToArchived() {
        var block = SyncedBlock.create(workspaceId, projectId, "Block A", 1);
        var archived = block.archive();

        assertThat(archived.status()).isEqualTo(SyncedBlockStatus.ARCHIVED);
        assertThat(archived.id()).isEqualTo(block.id());
        assertThat(archived.currentRevisionNo()).isEqualTo(block.currentRevisionNo());
    }

    @Test
    void archive_preservesRevisionNo() {
        var block = SyncedBlock.create(workspaceId, projectId, "Block A", 1).withRevision(5L);
        var archived = block.archive();

        assertThat(archived.currentRevisionNo()).isEqualTo(5L);
    }

    @Test
    void create_assignsRandomId() {
        var block1 = SyncedBlock.create(workspaceId, projectId, "Block", 1);
        var block2 = SyncedBlock.create(workspaceId, projectId, "Block", 1);

        assertThat(block1.id()).isNotEqualTo(block2.id());
    }
}
