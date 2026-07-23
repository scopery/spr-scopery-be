package com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter;

import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContentRepository;
import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class NativeDocumentContentSourceAdapter {

    private final DocumentContentRepository contentRepo;
    private final DocumentRepository documentRepo;

    public NativeDocumentContentSourceAdapter(DocumentContentRepository contentRepo,
                                               DocumentRepository documentRepo) {
        this.contentRepo = contentRepo;
        this.documentRepo = documentRepo;
    }

    public Optional<KnowledgeSourceSnapshot> buildSnapshot(UUID projectId, UUID documentId) {
        var content = contentRepo.findByDocumentId(documentId).orElse(null);
        if (content == null) return Optional.empty();

        var document = documentRepo.findByIdAndProjectId(documentId, projectId).orElse(null);
        if (document == null) return Optional.empty();

        List<String> aclTokens = new ArrayList<>();
        aclTokens.add("workspace:" + document.workspaceId());
        aclTokens.add("project:" + projectId);
        aclTokens.sort(String::compareTo);

        return Optional.of(new KnowledgeSourceSnapshot(
                document.workspaceId(),
                projectId,
                KnowledgeSourceType.NATIVE_DOCUMENT_CONTENT,
                documentId,
                content.id(),
                document.title(),
                "und",
                "INTERNAL",
                content.plainText(),
                Map.of(
                        "documentId", documentId.toString(),
                        "revisionNo", content.revisionNo(),
                        "wordCount", content.wordCount(),
                        "characterCount", content.characterCount()
                ),
                List.copyOf(aclTokens),
                content.checksum(),
                "/documents/" + documentId,
                content.updatedAt()
        ));
    }
}
