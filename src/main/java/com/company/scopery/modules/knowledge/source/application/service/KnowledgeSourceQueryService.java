package com.company.scopery.modules.knowledge.source.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.knowledge.shared.error.KnowledgeExceptions;
import com.company.scopery.modules.knowledge.source.application.response.KnowledgeChunkResponse;
import com.company.scopery.modules.knowledge.source.application.response.KnowledgeSourceResponse;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeChunk;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeChunkRepository;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSource;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class KnowledgeSourceQueryService {

    private final KnowledgeSourceRepository sources;
    private final KnowledgeChunkRepository chunks;

    public KnowledgeSourceQueryService(KnowledgeSourceRepository sources, KnowledgeChunkRepository chunks) {
        this.sources = sources;
        this.chunks = chunks;
    }

    @Transactional(readOnly = true)
    public KnowledgeSourceResponse findById(UUID sourceId) {
        KnowledgeSource source = sources.findById(sourceId)
                .orElseThrow(() -> KnowledgeExceptions.knowledgeSourceNotFound(sourceId));
        return toResponse(source);
    }

    @Transactional(readOnly = true)
    public PageResponse<KnowledgeChunkResponse> findChunks(UUID sourceId, int page, int size) {
        sources.findById(sourceId)
                .orElseThrow(() -> KnowledgeExceptions.knowledgeSourceNotFound(sourceId));
        List<KnowledgeChunk> all = chunks.findCurrentBySourceId(sourceId);
        int total = all.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<KnowledgeChunkResponse> items = all.subList(from, to).stream().map(this::toChunkResponse).toList();
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PageResponse<>(items, page, size, total, totalPages, page == 0, page >= totalPages - 1);
    }

    private KnowledgeSourceResponse toResponse(KnowledgeSource s) {
        return new KnowledgeSourceResponse(s.id(), s.workspaceId(), s.projectId(),
                s.sourceType().name(), s.sourceRefId(), s.sourceVersionRefId(),
                s.title(), s.language(), s.classification(), s.status().name(),
                s.aclTokens(), s.lastIndexedAt(), s.updatedAt());
    }

    private KnowledgeChunkResponse toChunkResponse(KnowledgeChunk c) {
        return new KnowledgeChunkResponse(c.id(), c.sourceId(), c.chunkOrdinal(),
                c.strategyVersion(), c.chunkType(), c.headingPath(),
                c.plainText(), c.tokenCount(), c.contentHash(), c.isCurrent());
    }
}
