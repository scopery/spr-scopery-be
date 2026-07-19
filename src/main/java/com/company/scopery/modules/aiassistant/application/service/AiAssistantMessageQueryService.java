package com.company.scopery.modules.aiassistant.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.aiassistant.domain.enums.MessageRole;
import com.company.scopery.modules.aiassistant.domain.model.AiMessage;
import com.company.scopery.modules.aiassistant.domain.model.AiMessageRepository;
import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AiAssistantMessageQueryService {

    private final AiMessageRepository messageRepository;

    public AiAssistantMessageQueryService(AiMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public AiMessage getById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> AiAssistantExceptions.messageNotFound(id));
    }

    public AiMessage getByIdAndConversationId(UUID id, UUID conversationId) {
        return messageRepository.findByIdAndConversationId(id, conversationId)
                .orElseThrow(() -> AiAssistantExceptions.messageNotFound(id));
    }

    public PageResponse<AiMessage> listByConversation(UUID conversationId, Pageable pageable) {
        List<MessageRole> excluded = List.of(MessageRole.TOOL_REQUEST, MessageRole.TOOL_RESULT);
        Page<AiMessage> page = messageRepository
                .findByConversationIdAndRoleNotIn(conversationId, excluded, pageable);
        return PageResponse.from(page);
    }

    public int countByConversation(UUID conversationId) {
        return messageRepository.countByConversationId(conversationId);
    }

    public List<AiMessage> getRecentMessages(UUID conversationId, int limit) {
        List<MessageRole> roles = List.of(MessageRole.USER, MessageRole.ASSISTANT);
        return messageRepository
                .findByConversationIdAndRoleInOrderBySequenceDesc(conversationId, roles, limit);
    }
}
