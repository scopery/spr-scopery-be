package com.company.scopery.modules.documenthub.documentlink.application.action;

import com.company.scopery.modules.documenthub.documentlink.application.command.BulkCreateDocumentLinksCommand;
import com.company.scopery.modules.documenthub.documentlink.application.response.BulkCreateDocumentLinksResult;
import com.company.scopery.modules.documenthub.documentlink.application.response.DocumentLinkResponse;
import com.company.scopery.modules.documenthub.documentlink.domain.model.DocumentLink;
import com.company.scopery.modules.documenthub.documentlink.domain.model.DocumentLinkRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BulkCreateDocumentLinksAction {

    private final DocumentLinkRepository linkRepository;
    private final DocumentHubActivityLogger activityLogger;

    public BulkCreateDocumentLinksAction(DocumentLinkRepository linkRepository,
                                          DocumentHubActivityLogger activityLogger) {
        this.linkRepository = linkRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public BulkCreateDocumentLinksResult execute(BulkCreateDocumentLinksCommand command) {
        List<DocumentLink> toCreate = new ArrayList<>();
        List<UUID> skippedDocuments = new ArrayList<>();

        String targetType = command.linkedEntityType().name();
        String linkType = command.relationType().name();

        for (UUID documentId : command.documentIds()) {
            boolean duplicate = linkRepository.existsByDocumentIdAndTargetTypeAndTargetIdAndLinkType(
                    documentId, targetType, command.linkedEntityId(), linkType);
            if (duplicate) {
                skippedDocuments.add(documentId);
            } else {
                toCreate.add(DocumentLink.create(
                        documentId,
                        command.projectId(),
                        command.linkedEntityType(),
                        command.linkedEntityId(),
                        command.relationType()
                ));
            }
        }

        List<DocumentLinkResponse> createdLinks = new ArrayList<>();
        if (!toCreate.isEmpty()) {
            List<DocumentLink> saved = linkRepository.saveAll(toCreate);
            createdLinks = saved.stream().map(DocumentLinkResponse::from).toList();
        }

        activityLogger.logSuccess(
                DocumentHubEntityTypes.DOCUMENT_LINK,
                command.linkedEntityId(),
                DocumentHubActivityActions.DOCUMENT_LINK_BULK_CREATED,
                "Bulk created " + createdLinks.size() + " document links for entity " + command.linkedEntityId()
        );

        return new BulkCreateDocumentLinksResult(
                createdLinks.size(),
                skippedDocuments.size(),
                0,
                createdLinks,
                skippedDocuments
        );
    }
}
