package com.company.scopery.modules.knowledge.retrieval.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SearchKnowledgeRequest(
        @NotBlank String query,
        UUID projectId,
        Integer topK
) {}
