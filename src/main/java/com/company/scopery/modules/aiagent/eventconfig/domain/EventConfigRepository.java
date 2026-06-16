package com.company.scopery.modules.aiagent.eventconfig.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Page<EventConfig> findAll(String keyword, UUID eventDefinitionId,
                               EventConfigEnvironment environment, EventTriggerType triggerType,
                               EventConfigStatus status, UUID agentId, Pageable pageable);
}
