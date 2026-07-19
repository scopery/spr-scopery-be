package com.company.scopery.modules.knowledge.indexing.application.service;

import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeStatus;
import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeType;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphNode;
import com.company.scopery.modules.knowledge.graph.domain.model.KnowledgeGraphNodeRepository;
import com.company.scopery.modules.knowledge.indexing.domain.model.EmbeddingProfileRepository;
import com.company.scopery.modules.knowledge.indexing.infrastructure.embedding.EmbeddingProvider;
import com.company.scopery.modules.knowledge.indexing.infrastructure.postgres.KnowledgeChunkIndexRecord;
import com.company.scopery.modules.knowledge.indexing.infrastructure.postgres.PostgresKnowledgeIndexService;
import com.company.scopery.modules.knowledge.retrieval.application.service.AclSignatureService;
import com.company.scopery.modules.knowledge.retrieval.application.service.KnowledgeChunkingService;
import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceStatus;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeChunk;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeChunkRepository;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeProjection;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeProjectionRepository;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSource;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSourceRepository;
import com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter.KnowledgeSourceSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class KnowledgeSourceIndexingService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeSourceIndexingService.class);
    private static final String DEFAULT_EMBEDDING_PROFILE = "OPENAI_TEXT_EMBEDDING_3_SMALL_1536_V1";

    private final KnowledgeSourceRepository sources;
    private final KnowledgeProjectionRepository projections;
    private final KnowledgeChunkRepository chunks;
    private final KnowledgeGraphNodeRepository graphNodes;
    private final EmbeddingProfileRepository embeddingProfiles;
    private final EmbeddingProvider embeddingProvider;
    private final KnowledgeChunkingService chunkingService;
    private final AclSignatureService aclSignatureService;
    private final PostgresKnowledgeIndexService postgresIndexService;

    public KnowledgeSourceIndexingService(KnowledgeSourceRepository sources,
                                           KnowledgeProjectionRepository projections,
                                           KnowledgeChunkRepository chunks,
                                           KnowledgeGraphNodeRepository graphNodes,
                                           EmbeddingProfileRepository embeddingProfiles,
                                           EmbeddingProvider embeddingProvider,
                                           KnowledgeChunkingService chunkingService,
                                           AclSignatureService aclSignatureService,
                                           PostgresKnowledgeIndexService postgresIndexService) {
        this.sources = sources;
        this.projections = projections;
        this.chunks = chunks;
        this.graphNodes = graphNodes;
        this.embeddingProfiles = embeddingProfiles;
        this.embeddingProvider = embeddingProvider;
        this.chunkingService = chunkingService;
        this.aclSignatureService = aclSignatureService;
        this.postgresIndexService = postgresIndexService;
    }

    @Transactional
    public UUID upsertSource(KnowledgeSourceSnapshot snapshot) {
        String contentHash = sha256(snapshot.normalizedText());
        String permSig = aclSignatureService.computeSignature(
                snapshot.workspaceId(), snapshot.projectId(),
                snapshot.classification(), snapshot.aclTokens(),
                snapshot.sourceAccessVersion());

        Optional<KnowledgeSource> existing = sources.findByWorkspaceAndTypeAndRef(
                snapshot.workspaceId(), snapshot.sourceType(),
                snapshot.sourceRefId(), snapshot.sourceVersionRefId());

        if (existing.isPresent() && existing.get().contentHash().equals(contentHash)
                && existing.get().permissionSignature().equals(permSig)
                && existing.get().status() == KnowledgeSourceStatus.INDEXED) {
            log.debug("Source unchanged, skipping index: {}", existing.get().id());
            return existing.get().id();
        }

        KnowledgeSource source = buildOrUpdateSource(existing, snapshot, contentHash, permSig);
        source = sources.save(source);
        UUID sourceId = source.id();

        KnowledgeProjection projection = buildProjection(sourceId, snapshot);
        projection = projections.save(projection);

        chunks.markSupersededBySourceId(sourceId);

        List<KnowledgeChunkingService.ChunkResult> chunkResults =
                chunkingService.chunk(snapshot.sourceVersionRefId(), snapshot.sourceType(), snapshot.normalizedText());

        List<KnowledgeChunk> domainChunks = buildDomainChunks(sourceId, projection.id(), chunkResults);
        List<KnowledgeChunk> savedChunks = chunks.saveAll(domainChunks);

        List<String> texts = savedChunks.stream().map(KnowledgeChunk::plainText).toList();
        String modelCode = resolveModelCode();
        List<float[]> embeddings = embeddingProvider.embed(texts, modelCode);

        List<KnowledgeChunkIndexRecord> indexRecords =
                buildIndexRecords(source, savedChunks, embeddings, snapshot);
        postgresIndexService.bulkIndex(indexRecords);

        source = sources.save(source.withStatus(KnowledgeSourceStatus.INDEXED, Instant.now()));

        upsertGraphNode(source, snapshot);

        log.info("Indexed {} chunks for source {} ({})", savedChunks.size(), sourceId, snapshot.sourceType());
        return sourceId;
    }

    @Transactional
    public void invalidateSource(UUID workspaceId, UUID sourceRefId) {
        sources.findByWorkspaceId(workspaceId).stream()
                .filter(s -> s.sourceRefId().equals(sourceRefId))
                .forEach(s -> {
                    chunks.markSupersededBySourceId(s.id());
                    sources.save(s.withStatus(KnowledgeSourceStatus.INVALIDATED, Instant.now()));
                });
    }

    private KnowledgeSource buildOrUpdateSource(Optional<KnowledgeSource> existing,
                                                 KnowledgeSourceSnapshot snapshot,
                                                 String contentHash, String permSig) {
        Instant now = Instant.now();
        if (existing.isPresent()) {
            KnowledgeSource s = existing.get();
            return new KnowledgeSource(s.id(), snapshot.workspaceId(), snapshot.projectId(),
                    snapshot.sourceType(), snapshot.sourceRefId(), snapshot.sourceVersionRefId(),
                    snapshot.title(), snapshot.language(), snapshot.classification(),
                    contentHash, permSig, snapshot.aclTokens(),
                    KnowledgeSourceStatus.INDEXING, now, null,
                    s.createdAt(), s.createdBy(), now, null, s.version() + 1);
        }
        return new KnowledgeSource(UUID.randomUUID(), snapshot.workspaceId(), snapshot.projectId(),
                snapshot.sourceType(), snapshot.sourceRefId(), snapshot.sourceVersionRefId(),
                snapshot.title(), snapshot.language(), snapshot.classification(),
                contentHash, permSig, snapshot.aclTokens(),
                KnowledgeSourceStatus.INDEXING, now, null,
                now, null, now, null, 0L);
    }

    private KnowledgeProjection buildProjection(UUID sourceId, KnowledgeSourceSnapshot snapshot) {
        String contentHash = sha256(snapshot.normalizedText());
        Instant now = Instant.now();
        return new KnowledgeProjection(UUID.randomUUID(), sourceId, 1,
                "SNAPSHOT", "1", "chunk-v1",
                snapshot.normalizedText(), snapshot.structuredMetadata(),
                contentHash, "READY", null, null,
                now, null);
    }

    private List<KnowledgeChunk> buildDomainChunks(UUID sourceId, UUID projectionId,
                                                    List<KnowledgeChunkingService.ChunkResult> results) {
        Instant now = Instant.now();
        List<KnowledgeChunk> list = new ArrayList<>(results.size());
        for (KnowledgeChunkingService.ChunkResult r : results) {
            list.add(new KnowledgeChunk(UUID.randomUUID(), sourceId, projectionId,
                    r.ordinal(), r.strategyVersion(), r.chunkType().name(),
                    r.headingPath(), r.plainText(), r.tokenCount(),
                    r.startCodePoint(), r.endCodePoint(),
                    r.contentHash(), Map.of(), true, now));
        }
        return list;
    }

    private List<KnowledgeChunkIndexRecord> buildIndexRecords(KnowledgeSource source,
                                                               List<KnowledgeChunk> savedChunks,
                                                               List<float[]> embeddings,
                                                               KnowledgeSourceSnapshot snapshot) {
        List<KnowledgeChunkIndexRecord> records = new ArrayList<>(savedChunks.size());
        for (int i = 0; i < savedChunks.size(); i++) {
            KnowledgeChunk chunk = savedChunks.get(i);
            float[] embedding = i < embeddings.size() ? embeddings.get(i) : new float[0];
            records.add(new KnowledgeChunkIndexRecord(
                    chunk.id(),
                    source.workspaceId(),
                    source.projectId(),
                    source.title(),
                    chunk.plainText(),
                    embedding,
                    source.language(),
                    source.classification(),
                    source.sourceType().name(),
                    source.status().name(),
                    source.aclTokens(),
                    snapshot.appRoute()
            ));
        }
        return records;
    }

    private void upsertGraphNode(KnowledgeSource source, KnowledgeSourceSnapshot snapshot) {
        GraphNodeType nodeType = toGraphNodeType(source.sourceType());
        if (nodeType == null) return;

        Optional<KnowledgeGraphNode> existingNode = graphNodes.findByRef(
                source.workspaceId(), nodeType, source.sourceRefId(), source.sourceVersionRefId());

        Instant now = Instant.now();
        KnowledgeGraphNode node;
        if (existingNode.isPresent()) {
            KnowledgeGraphNode existing = existingNode.get();
            node = new KnowledgeGraphNode(existing.id(), source.workspaceId(), source.projectId(),
                    nodeType, source.sourceRefId(), source.sourceVersionRefId(),
                    source.title(), source.permissionSignature(), source.aclTokens(),
                    GraphNodeStatus.ACTIVE, existing.createdAt(), now, existing.version() + 1);
        } else {
            node = new KnowledgeGraphNode(UUID.randomUUID(), source.workspaceId(), source.projectId(),
                    nodeType, source.sourceRefId(), source.sourceVersionRefId(),
                    source.title(), source.permissionSignature(), source.aclTokens(),
                    GraphNodeStatus.ACTIVE, now, now, 0L);
        }
        graphNodes.save(node);
    }

    private GraphNodeType toGraphNodeType(com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType sourceType) {
        return switch (sourceType) {
            case TASK -> GraphNodeType.TASK;
            case DOCUMENT_VERSION, NATIVE_DOCUMENT_CONTENT -> GraphNodeType.DOCUMENT_VERSION;
            case MEETING_MINUTE -> GraphNodeType.MEETING_MINUTE;
        };
    }

    private String resolveModelCode() {
        return embeddingProfiles.findByCode(DEFAULT_EMBEDDING_PROFILE)
                .map(p -> p.model())
                .orElse("text-embedding-3-small");
    }

    private static String sha256(String text) {
        if (text == null) return "";
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
