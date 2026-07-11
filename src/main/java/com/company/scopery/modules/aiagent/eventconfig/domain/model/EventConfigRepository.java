package com.company.scopery.modules.aiagent.eventconfig.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigEnvironment;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventTriggerType;
import com.company.scopery.modules.aiagent.eventconfig.domain.valueobject.EventConfigCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventConfigRepository {

    EventConfig save(EventConfig eventConfig);

    Optional<EventConfig> findById(UUID id);

    boolean existsByCode(EventConfigCode code);

    boolean existsActiveByEventDefinitionIdAndEnvironment(UUID eventDefinitionId,
                                                          EventConfigEnvironment environment,
                                                          UUID excludeId);

    Optional<EventConfig> findActiveByEventDefinitionIdAndEnvironment(UUID eventDefinitionId,
                                                                       EventConfigEnvironment environment);

    List<EventConfig> findAllByStatus(EventConfigStatus status);

    PageResult<EventConfig> findAll(String keyword, UUID eventDefinitionId,
                               EventConfigEnvironment environment, EventTriggerType triggerType,
                               EventConfigStatus status, UUID agentId, PageQuery pageQuery);
}
