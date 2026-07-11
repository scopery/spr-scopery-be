package com.company.scopery.modules.aiagent.prompt.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromptVersionRepository {

    PromptVersion save(PromptVersion version);

    Optional<PromptVersion> findById(UUID id);

    int getMaxVersionNumber(UUID templateId);

    int archiveOtherActiveVersions(UUID templateId, UUID excludeId);

    List<PromptVersion> findAllByStatus(PromptVersionStatus status);

    PageResult<PromptVersion> findAll(UUID templateId, PromptVersionStatus status,
                                      PromptContentFormat contentFormat, PageQuery pageQuery);
}
