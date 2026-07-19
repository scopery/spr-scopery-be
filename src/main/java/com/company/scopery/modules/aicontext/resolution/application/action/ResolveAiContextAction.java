package com.company.scopery.modules.aicontext.resolution.application.action;

import com.company.scopery.modules.aicontext.audit.domain.model.AiContextResolutionAudit;
import com.company.scopery.modules.aicontext.audit.domain.model.AiContextResolutionAuditRepository;
import com.company.scopery.modules.aicontext.policy.domain.model.AiContextResolutionPolicy;
import com.company.scopery.modules.aicontext.policy.domain.model.AiContextResolutionPolicyRepository;
import com.company.scopery.modules.aicontext.resolution.application.command.ResolveAiContextCommand;
import com.company.scopery.modules.aicontext.resolution.application.response.AiContextResolutionResult;
import com.company.scopery.modules.aicontext.shared.error.AiContextExceptions;
import com.company.scopery.modules.documenthub.document.domain.model.Document;
import com.company.scopery.modules.documenthub.document.domain.model.DocumentRepository;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentBlockIndex;
import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentBlockIndexRepository;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * NOT @Transactional on execute() — resolves AI context across multiple queries
 * without holding a DB connection open. Audit is persisted in a separate transaction
 * so it is committed even on partial failure.
 */
@Component
public class ResolveAiContextAction {

    private static final Logger log = LoggerFactory.getLogger(ResolveAiContextAction.class);
    private static final int AVERAGE_CHARS_PER_TOKEN = 4;

    private final AiContextResolutionPolicyRepository policyRepo;
    private final AiContextResolutionAuditRepository auditRepo;
    private final DocumentRepository documentRepo;
    private final DocumentBlockIndexRepository blockIndexRepo;
    private final DocumentHubAuthorizationService authorization;

    public ResolveAiContextAction(AiContextResolutionPolicyRepository policyRepo,
                                   AiContextResolutionAuditRepository auditRepo,
                                   DocumentRepository documentRepo,
                                   DocumentBlockIndexRepository blockIndexRepo,
                                   DocumentHubAuthorizationService authorization) {
        this.policyRepo = policyRepo;
        this.auditRepo = auditRepo;
        this.documentRepo = documentRepo;
        this.blockIndexRepo = blockIndexRepo;
        this.authorization = authorization;
    }

    public AiContextResolutionResult execute(ResolveAiContextCommand c) {
        UUID actorId = resolveActorId();

        AiContextResolutionPolicy policy = null;
        if (c.policyId() != null) {
            policy = policyRepo.findById(c.policyId())
                    .orElseThrow(() -> AiContextExceptions.policyNotFound(c.policyId().toString()));
        }

        int maxTokens = policy != null ? policy.maxTokens() : 4000;

        try {
            if (c.documentId() != null) {
                authorization.requireView(c.projectId());
            }

            Document document = null;
            if (c.documentId() != null) {
                document = documentRepo.findByIdAndProjectId(c.documentId(), c.projectId())
                        .orElseThrow(() -> DocumentHubExceptions.documentNotFound(c.documentId()));
            }

            List<DocumentBlockIndex> blocks = c.documentId() != null
                    ? blockIndexRepo.findByDocumentId(c.documentId())
                    : List.of();

            List<DocumentBlockIndex> slicedBlocks = sliceByTokens(blocks, maxTokens);

            String contextText = buildContextText(slicedBlocks);
            List<AiContextResolutionResult.BlockCitation> citations = buildCitations(
                    slicedBlocks, document);

            int tokenCount = estimateTokens(contextText);
            UUID auditId = persistAudit(policy != null ? policy.id() : null,
                    c.documentId(), actorId, tokenCount, slicedBlocks.size(), null);

            return AiContextResolutionResult.of(c.documentId(), contextText,
                    tokenCount, slicedBlocks.size(), citations, auditId);

        } catch (Exception e) {
            UUID auditId = persistAudit(policy != null ? policy.id() : null,
                    c.documentId(), actorId, 0, 0, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public UUID persistAudit(UUID policyId, UUID documentId, UUID actorId,
                              int tokenCount, int blockCount, String errorMessage) {
        AiContextResolutionAudit audit = errorMessage != null
                ? AiContextResolutionAudit.failed(policyId, documentId, actorId, errorMessage)
                : AiContextResolutionAudit.create(policyId, documentId, actorId, tokenCount, blockCount);
        return auditRepo.save(audit).id();
    }

    private List<DocumentBlockIndex> sliceByTokens(List<DocumentBlockIndex> blocks, int maxTokens) {
        List<DocumentBlockIndex> result = new ArrayList<>();
        int accumulated = 0;
        for (DocumentBlockIndex block : blocks) {
            int blockTokens = estimateTokens(block.plainText());
            if (accumulated + blockTokens > maxTokens) break;
            result.add(block);
            accumulated += blockTokens;
        }
        return result;
    }

    private String buildContextText(List<DocumentBlockIndex> blocks) {
        StringBuilder sb = new StringBuilder();
        for (DocumentBlockIndex block : blocks) {
            if (block.plainText() != null && !block.plainText().isBlank()) {
                sb.append(block.plainText()).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private List<AiContextResolutionResult.BlockCitation> buildCitations(
            List<DocumentBlockIndex> blocks, Document document) {
        return blocks.stream()
                .map(b -> new AiContextResolutionResult.BlockCitation(
                        b.blockId(),
                        b.documentId(),
                        document != null ? document.title() : null,
                        b.headingText()))
                .toList();
    }

    private int estimateTokens(String text) {
        if (text == null || text.isBlank()) return 0;
        return Math.max(1, text.length() / AVERAGE_CHARS_PER_TOKEN);
    }

    private UUID resolveActorId() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof String userId) {
                return UUID.fromString(userId);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
