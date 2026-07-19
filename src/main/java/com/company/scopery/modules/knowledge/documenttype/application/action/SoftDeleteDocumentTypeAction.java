package com.company.scopery.modules.knowledge.documenttype.application.action;

import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentTypeResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Deprecated soft-delete alias — delegates to {@link ArchiveDocumentTypeAction}.
 */
@Component
public class SoftDeleteDocumentTypeAction {

    private final ArchiveDocumentTypeAction archiveDocumentTypeAction;

    public SoftDeleteDocumentTypeAction(ArchiveDocumentTypeAction archiveDocumentTypeAction) {
        this.archiveDocumentTypeAction = archiveDocumentTypeAction;
    }

    @Transactional
    public DocumentTypeResponse execute(UUID id) {
        return archiveDocumentTypeAction.execute(id);
    }
}
