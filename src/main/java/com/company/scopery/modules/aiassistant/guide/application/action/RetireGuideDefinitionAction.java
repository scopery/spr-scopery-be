package com.company.scopery.modules.aiassistant.guide.application.action;

import com.company.scopery.common.exception.NotFoundException;
import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinitionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RetireGuideDefinitionAction {

    private final AiGuideDefinitionRepository repository;

    public RetireGuideDefinitionAction(AiGuideDefinitionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(UUID id) {
        repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Guide definition not found: " + id));
        repository.retireById(id);
    }
}
