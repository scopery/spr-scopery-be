package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.modules.aiaction.application.command.CreateAiActionRequestCommand;
import com.company.scopery.modules.aiaction.application.port.AiActionPhase21CompatibilityPort;
import com.company.scopery.modules.aiaction.application.port.AiActionPhase43SuggestionPort;
import com.company.scopery.modules.aiaction.application.response.AiActionRequestResponse;
import com.company.scopery.modules.aiaction.request.domain.enums.AiActionOriginType;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequest;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequestRepository;
import com.company.scopery.modules.aiaction.shared.activity.AiActionActivityLogger;
import com.company.scopery.modules.aiaction.shared.constant.AiActionActivityActions;
import com.company.scopery.modules.aiaction.shared.constant.AiActionEntityTypes;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Component
public class CreateAiActionRequestAction {


    private final AiActionRequestRepository requestRepository;
    private final AiActionPhase43SuggestionPort phase43SuggestionPort;
    private final AiActionPhase21CompatibilityPort phase21CompatibilityPort;
    private final AiActionActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public CreateAiActionRequestAction(AiActionRequestRepository requestRepository,
                                        AiActionPhase43SuggestionPort phase43SuggestionPort,
                                        AiActionPhase21CompatibilityPort phase21CompatibilityPort,
                                        AiActionActivityLogger activityLogger,
                                        ObjectMapper objectMapper) {
        this.requestRepository = requestRepository;
        this.phase43SuggestionPort = phase43SuggestionPort;
        this.phase21CompatibilityPort = phase21CompatibilityPort;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AiActionRequestResponse execute(CreateAiActionRequestCommand command) {
        // Idempotency: return existing if same key+workspace+user
        Optional<AiActionRequest> existing = requestRepository
                .findByWorkspaceAndUserAndIdempotencyKey(
                        command.workspaceId(), command.actorId(), command.idempotencyKey());
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        String requestHash = computeRequestHash(command);

        // Conflict: same idempotency key but different content
        if (requestRepository.existsByWorkspaceAndUserAndIdempotencyKeyAndDifferentHash(
                command.workspaceId(), command.actorId(), command.idempotencyKey(), requestHash)) {
            throw AiActionExceptions.idempotencyConflict(command.idempotencyKey());
        }

        // Resolve actions if SUGGESTION or PHASE21_LEGACY origin
        resolveActionsIfNeeded(command);

        String requestedActionsJson = null;
        if (command.requestedActions() != null && !command.requestedActions().isEmpty()) {
            try {
                requestedActionsJson = objectMapper.writeValueAsString(command.requestedActions());
            } catch (Exception e) {
                throw new IllegalStateException("Failed to serialize requestedActions", e);
            }
        }

        AiActionRequest request = AiActionRequest.create(
                command.workspaceId(), command.projectId(), command.actorId(),
                command.originType(), command.originSuggestionRef(),
                command.originConversationId() != null ? command.originConversationId().toString() : null,
                command.originMessageId() != null ? command.originMessageId().toString() : null,
                command.originTurnId() != null ? command.originTurnId().toString() : null,
                command.legacyPhase21SuggestionId(), command.intentSummary(),
                command.idempotencyKey(), requestHash, requestedActionsJson
        );

        AiActionRequest saved = requestRepository.save(request);

        activityLogger.logSuccess(AiActionEntityTypes.REQUEST, saved.id(),
                AiActionActivityActions.CREATE_REQUEST, "AI action request created: " + saved.id());

        return toResponse(saved);
    }

    private void resolveActionsIfNeeded(CreateAiActionRequestCommand command) {
        if (command.originType() == AiActionOriginType.SUGGESTION
                && command.originSuggestionRef() != null) {
            if (!phase43SuggestionPort.isValidAndApplicable(command.originSuggestionRef())) {
                throw AiActionExceptions.requestNotFound(null);
            }
        }
        if (command.originType() == AiActionOriginType.PHASE21_LEGACY
                && command.legacyPhase21SuggestionId() != null) {
            if (!phase21CompatibilityPort.isSupported(command.legacyPhase21SuggestionId())) {
                throw AiActionExceptions.legacyPayloadUnsupported(command.legacyPhase21SuggestionId());
            }
        }
    }

    private String computeRequestHash(CreateAiActionRequestCommand cmd) {
        String canonical = String.join("|",
                String.valueOf(cmd.workspaceId()),
                String.valueOf(cmd.actorId()),
                String.valueOf(cmd.originType()),
                String.valueOf(cmd.originSuggestionRef()),
                String.valueOf(cmd.intentSummary()));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
            return "areq:v1:sha256:" + HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private AiActionRequestResponse toResponse(AiActionRequest r) {
        return new AiActionRequestResponse(
                r.id(), r.workspaceId(), r.projectId(), r.initiatedByUserId(),
                r.originType().name(), r.originSuggestionRef(), r.status().name(),
                r.intentSummary(), r.latestPlanId(), r.createdAt(), r.updatedAt());
    }
}
