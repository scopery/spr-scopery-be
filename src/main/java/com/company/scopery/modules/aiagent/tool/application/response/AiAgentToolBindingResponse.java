package com.company.scopery.modules.aiagent.tool.application.response;

import com.company.scopery.modules.aiagent.tool.domain.model.AiAgentToolBinding;

import java.time.Instant;
import java.util.UUID;

public record AiAgentToolBindingResponse(
        UUID id,
        UUID agentId,
        UUID toolId,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiAgentToolBindingResponse from(AiAgentToolBinding binding) {
        return new AiAgentToolBindingResponse(
                binding.id(),
                binding.agentId(),
                binding.toolId(),
                binding.status().name(),
                binding.createdAt(),
                binding.updatedAt()
        );
    }
}
