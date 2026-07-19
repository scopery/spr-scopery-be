package com.company.scopery.modules.aiassistant.application.port;

import com.company.scopery.modules.aiassistant.domain.model.AiContextSnapshot;
import com.company.scopery.modules.aiassistant.domain.model.AiContextSnapshotRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Component
public class AiAssistantContextBuilder {

    private final AiContextSnapshotRepository contextSnapshotRepository;

    public AiAssistantContextBuilder(AiContextSnapshotRepository contextSnapshotRepository) {
        this.contextSnapshotRepository = contextSnapshotRepository;
    }

    public record AiResolvedContext(
            UUID actorId,
            UUID workspaceId,
            UUID projectId,
            List<String> aclTokens,
            String permissionSignature,
            String contextHash,
            AiContextSnapshot snapshot
    ) {}

    @Transactional
    public AiResolvedContext build(UUID conversationId, UUID assistantMessageId, UUID turnId,
                                   UUID actorId, UUID workspaceId, UUID projectId) {
        String permissionSignature = "acl:v1:sha256:" + sha256Hex(
                actorId + ":" + workspaceId + ":" + projectId);

        String contextInput = toCanonicalJson(actorId, workspaceId, projectId, conversationId, turnId);
        String contextHash = "ctx:v1:sha256:" + sha256Hex(contextInput);

        Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);

        AiContextSnapshot snapshot = AiContextSnapshot.create(
                conversationId, assistantMessageId, turnId,
                actorId, workspaceId, projectId,
                permissionSignature, contextHash,
                "{}", expiresAt);

        contextSnapshotRepository.save(snapshot);

        return new AiResolvedContext(
                actorId, workspaceId, projectId,
                List.of(),
                permissionSignature, contextHash,
                snapshot);
    }

    private static String toCanonicalJson(UUID actorId, UUID workspaceId, UUID projectId,
                                          UUID conversationId, UUID turnId) {
        return "{\"actorId\":\"" + actorId
                + "\",\"workspaceId\":\"" + workspaceId
                + "\",\"projectId\":\"" + projectId
                + "\",\"conversationId\":\"" + conversationId
                + "\",\"turnId\":\"" + turnId + "\"}";
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
