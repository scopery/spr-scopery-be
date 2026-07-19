package com.company.scopery.modules.eventregistry.eventdefinition.application.service;

import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Probe active Event Registry consumers (Notification EmailRule, AI EventConfig).
 */
@Service
public class EventDefinitionConsumerSafetyService {

    private final EmailRuleRepository emailRuleRepository;
    private final EventConfigRepository eventConfigRepository;

    public EventDefinitionConsumerSafetyService(EmailRuleRepository emailRuleRepository,
                                                EventConfigRepository eventConfigRepository) {
        this.emailRuleRepository = emailRuleRepository;
        this.eventConfigRepository = eventConfigRepository;
    }

    @Transactional(readOnly = true)
    public boolean hasActiveConsumers(UUID eventDefinitionId) {
        return emailRuleRepository.existsActiveEnabledByEventDefinitionId(eventDefinitionId)
                || eventConfigRepository.existsActiveByEventDefinitionId(eventDefinitionId);
    }
}
