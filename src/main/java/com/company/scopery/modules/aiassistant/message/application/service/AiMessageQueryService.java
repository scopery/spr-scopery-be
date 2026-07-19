package com.company.scopery.modules.aiassistant.message.application.service;

import com.company.scopery.modules.aiassistant.application.service.AiAssistantMessageQueryService;
import com.company.scopery.modules.aiassistant.message.application.response.AiMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AiMessageQueryService {

    private final AiAssistantMessageQueryService messageQueryService;

    public AiMessageQueryService(AiAssistantMessageQueryService messageQueryService) {
        this.messageQueryService = messageQueryService;
    }

    @Transactional(readOnly = true)
    public AiMessageResponse getById(UUID messageId, UUID conversationId) {
        return AiMessageResponse.from(messageQueryService.getByIdAndConversationId(messageId, conversationId));
    }

    @Transactional(readOnly = true)
    public AiMessageResponse getById(UUID messageId) {
        return AiMessageResponse.from(messageQueryService.getById(messageId));
    }

    @Transactional(readOnly = true)
    public Page<AiMessageResponse> listByConversation(UUID conversationId, int page, int size) {
        // Excludes TOOL_REQUEST and TOOL_RESULT per spec — handled inside AiAssistantMessageQueryService
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sequenceInConversation"));
        var result = messageQueryService.listByConversation(conversationId, pageable);
        var responses = result.items().stream().map(AiMessageResponse::from).toList();
        return new PageImpl<>(responses, pageable, result.totalElements());
    }
}
