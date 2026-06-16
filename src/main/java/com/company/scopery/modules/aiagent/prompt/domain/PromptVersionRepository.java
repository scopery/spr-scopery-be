package com.company.scopery.modules.aiagent.prompt.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromptVersionRepository {

    PromptVersion save(PromptVersion version);

    Optional<PromptVersion> findById(UUID id);

    int getMaxVersionNumber(UUID templateId);

    int archiveOtherActiveVersions(UUID templateId, UUID excludeId);

    List<PromptVersion> findAllByStatus(PromptVersionStatus status);

    Page<PromptVersion> findAll(UUID templateId, PromptVersionStatus status,
                                PromptContentFormat contentFormat, Pageable pageable);
}