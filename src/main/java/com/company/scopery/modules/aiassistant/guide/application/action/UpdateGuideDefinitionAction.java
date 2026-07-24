package com.company.scopery.modules.aiassistant.guide.application.action;

import com.company.scopery.common.exception.NotFoundException;
import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinition;
import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinitionRepository;
import com.company.scopery.modules.aiassistant.guide.application.command.UpdateGuideDefinitionCommand;
import com.company.scopery.modules.aiassistant.guide.application.response.AdminGuideDefinitionResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateGuideDefinitionAction {

    private final AiGuideDefinitionRepository repository;

    public UpdateGuideDefinitionAction(AiGuideDefinitionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AdminGuideDefinitionResponse execute(UpdateGuideDefinitionCommand command) {
        AiGuideDefinition guide = repository.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Guide definition not found: " + command.id()));

        guide.update(command.title(), command.bodyMarkdown(), command.status());

        AiGuideDefinition saved = repository.save(guide);
        return AdminGuideDefinitionResponse.from(saved);
    }
}
