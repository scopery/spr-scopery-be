package com.company.scopery.modules.aiassistant.conversation.application.service;

import com.company.scopery.modules.aiassistant.application.service.AiAssistantConversationQueryService;
import com.company.scopery.modules.aiassistant.conversation.application.response.AiConversationResponse;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AiConversationQueryService {

    private final AiAssistantConversationQueryService conversationQueryService;

    public AiConversationQueryService(AiAssistantConversationQueryService conversationQueryService) {
        this.conversationQueryService = conversationQueryService;
    }

    @Transactional(readOnly = true)
    public AiConversationResponse getById(UUID id, UUID actorId) {
        return AiConversationResponse.from(conversationQueryService.getByIdAndOwner(id, actorId));
    }

    @Transactional(readOnly = true)
    public Page<AiConversationResponse> listByActor(UUID workspaceId, UUID actorId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastMessageAt"));
        var result = conversationQueryService.listByOwner(workspaceId, actorId, ConversationStatus.ACTIVE, pageable);
        var responses = result.items().stream().map(AiConversationResponse::from).toList();
        return new PageImpl<>(responses, pageable, result.totalElements());
    }
}
