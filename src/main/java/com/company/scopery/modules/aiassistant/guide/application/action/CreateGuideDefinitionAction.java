package com.company.scopery.modules.aiassistant.guide.application.action;

import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinition;
import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinitionRepository;
import com.company.scopery.modules.aiassistant.guide.application.command.CreateGuideDefinitionCommand;
import com.company.scopery.modules.aiassistant.guide.application.response.AdminGuideDefinitionResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateGuideDefinitionAction {

    private final AiGuideDefinitionRepository repository;

    public CreateGuideDefinitionAction(AiGuideDefinitionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AdminGuideDefinitionResponse execute(CreateGuideDefinitionCommand command) {
        String shortId = UUID.randomUUID().toString().substring(0, 8);
        String code = "guide::" + command.pageCode() + "::" + command.locale() + "::" + shortId;

        AiGuideDefinition guide = AiGuideDefinition.create(
                UUID.randomUUID(),
                code,
                command.pageCode(),
                command.fieldCode(),
                command.actionCode(),
                command.locale(),
                command.title(),
                command.bodyMarkdown(),
                "MANUAL"
        );

        AiGuideDefinition saved = repository.save(guide);
        return AdminGuideDefinitionResponse.from(saved);
    }
}
