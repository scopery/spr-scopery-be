package com.company.scopery.modules.aiassistant.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AiAssistantConversationQueryService {

    private final AiConversationRepository conversationRepository;

    public AiAssistantConversationQueryService(AiConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public AiConversation getByIdAndOwner(UUID id, UUID ownerUserId) {
        return conversationRepository.findByIdAndOwnerUserId(id, ownerUserId)
                .orElseThrow(() -> AiAssistantExceptions.conversationNotFound(id));
    }

    public AiConversation getById(UUID id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> AiAssistantExceptions.conversationNotFound(id));
    }

    public PageResponse<AiConversation> listByOwner(UUID workspaceId, UUID ownerUserId,
                                                     ConversationStatus status, Pageable pageable) {
        Page<AiConversation> page = conversationRepository
                .findByWorkspaceIdAndOwnerUserIdAndStatus(workspaceId, ownerUserId, status, pageable);
        return PageResponse.from(page);
    }
}
