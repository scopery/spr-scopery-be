package com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter;

import com.company.scopery.modules.knowledge.indexing.infrastructure.extraction.DocumentTextExtractor;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.shared.storage.ObjectStorageProvider;
import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import com.company.scopery.modules.documenthub.version.domain.model.DocumentVersionRepository;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class DocumentVersionKnowledgeSourceAdapter {

    private final DocumentVersionRepository versions;
    private final ProjectRepository projects;
    private final List<DocumentTextExtractor> extractors;
    private final ObjectStorageProvider storage;

    public DocumentVersionKnowledgeSourceAdapter(DocumentVersionRepository versions,
                                                  ProjectRepository projects,
                                                  List<DocumentTextExtractor> extractors,
                                                  ObjectStorageProvider storage) {
        this.versions = versions;
        this.projects = projects;
        this.extractors = extractors;
        this.storage = storage;
    }

    public Optional<KnowledgeSourceSnapshot> buildSnapshot(UUID projectId, UUID versionId) {
        return versions.findByIdAndProjectId(versionId, projectId).map(version -> {
            if (!"AVAILABLE".equals(version.storageStatus())) return null;
            if (version.storageKey() == null) return null;

            var project = projects.findById(version.projectId()).orElse(null);
            if (project == null) return null;

            String contentType = version.contentType();
            DocumentTextExtractor extractor = extractors.stream()
                    .filter(e -> e.supports(contentType))
                    .findFirst()
                    .orElseThrow(() -> KnowledgeExceptions.knowledgeExtractionUnsupported(contentType));

            String text = extractFromStorage(version.storageKey(), contentType, extractor);
            List<String> aclTokens = buildAclTokens(project.workspaceId(), version.projectId());

            return new KnowledgeSourceSnapshot(
                    project.workspaceId(),
                    version.projectId(),
                    KnowledgeSourceType.DOCUMENT_VERSION,
                    version.documentId(),
                    versionId,
                    version.fileName(),
                    "und",
                    "INTERNAL",
                    text,
                    Map.of(
                            "documentId", version.documentId().toString(),
                            "versionNumber", version.versionNumber(),
                            "contentType", contentType != null ? contentType : "",
                            "storageKey", version.storageKey()
                    ),
                    aclTokens,
                    version.checksum() != null ? version.checksum() : String.valueOf(version.versionNumber()),
                    "/documents/" + version.documentId() + "/versions/" + versionId,
                    version.updatedAt()
            );
        });
    }

    private String extractFromStorage(String storageKey, String contentType, DocumentTextExtractor extractor) {
        var meta = storage.head(storageKey);
        if (meta == null) throw KnowledgeExceptions.documentStorageUploadNotComplete(storageKey);
        // For extraction we need to download the bytes.
        // Phase 41 MVP: generate a presigned URL and stream — simplified to throw if storage unavailable
        throw new UnsupportedOperationException(
                "Direct storage streaming not yet wired in Phase 41 MVP — use indexing pipeline with byte array");
    }

    private List<String> buildAclTokens(UUID workspaceId, UUID projectId) {
        List<String> tokens = new ArrayList<>();
        tokens.add("workspace:" + workspaceId);
        tokens.add("project:" + projectId);
        tokens.sort(String::compareTo);
        return List.copyOf(tokens);
    }

    public Optional<KnowledgeSourceSnapshot> buildSnapshotWithBytes(UUID projectId, UUID versionId, byte[] bytes) {
        return versions.findByIdAndProjectId(versionId, projectId).map(version -> {
            if (!"AVAILABLE".equals(version.storageStatus())) return null;

            var project = projects.findById(version.projectId()).orElse(null);
            if (project == null) return null;

            String contentType = version.contentType();
            DocumentTextExtractor extractor = extractors.stream()
                    .filter(e -> e.supports(contentType))
                    .findFirst()
                    .orElseThrow(() -> KnowledgeExceptions.knowledgeExtractionUnsupported(contentType));

            String text;
            try (InputStream is = new ByteArrayInputStream(bytes)) {
                text = extractor.extract(is, contentType);
            } catch (Exception e) {
                throw new RuntimeException("Extraction failed", e);
            }

            List<String> aclTokens = buildAclTokens(project.workspaceId(), version.projectId());

            return new KnowledgeSourceSnapshot(
                    project.workspaceId(),
                    version.projectId(),
                    KnowledgeSourceType.DOCUMENT_VERSION,
                    version.documentId(),
                    versionId,
                    version.fileName(),
                    "und",
                    "INTERNAL",
                    text,
                    Map.of(
                            "documentId", version.documentId().toString(),
                            "versionNumber", version.versionNumber(),
                            "contentType", contentType != null ? contentType : ""
                    ),
                    aclTokens,
                    version.checksum() != null ? version.checksum() : String.valueOf(version.versionNumber()),
                    "/documents/" + version.documentId() + "/versions/" + versionId,
                    version.updatedAt()
            );
        });
    }
}
