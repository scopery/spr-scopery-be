package com.company.scopery.modules.documenthub.folder.domain.model;

import com.company.scopery.modules.documenthub.folder.domain.enums.FolderStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentFolderDomainTest {

    @Test
    void archiveTransitionsActiveToArchived() {
        var folder = DocumentFolder.create(UUID.randomUUID(), UUID.randomUUID(), null, "Specs", null, 1);
        assertEquals(FolderStatus.ACTIVE, folder.status());

        var archived = folder.archive(UUID.randomUUID());

        assertEquals(FolderStatus.ARCHIVED, archived.status());
        assert archived.archivedAt() != null;
    }

    @Test
    void archiveRejectsAlreadyArchivedFolder() {
        var folder = DocumentFolder.create(UUID.randomUUID(), UUID.randomUUID(), null, "Specs", null, 1)
                .archive(UUID.randomUUID());

        assertThrows(IllegalStateException.class, () -> folder.archive(UUID.randomUUID()));
    }
}
