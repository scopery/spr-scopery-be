package com.company.scopery.modules.aiagent.prompt.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PromptTemplateRepository {

    PromptTemplate save(PromptTemplate template);

    Optional<PromptTemplate> findById(UUID id);

    boolean existsByAgentIdAndCode(UUID agentId, PromptTemplateCode code);

    Page<PromptTemplate> findAll(UUID agentId, String keyword, PromptTemplateStatus status,
                                 Pageable pageable);
}