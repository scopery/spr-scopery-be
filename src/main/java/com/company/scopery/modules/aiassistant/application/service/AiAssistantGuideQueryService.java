package com.company.scopery.modules.aiassistant.application.service;

import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinition;
import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinitionRepository;
import com.company.scopery.modules.aiassistant.domain.model.AiSuggestedQuestion;
import com.company.scopery.modules.aiassistant.domain.model.AiSuggestedQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AiAssistantGuideQueryService {

    private final AiGuideDefinitionRepository guideDefinitionRepository;
    private final AiSuggestedQuestionRepository suggestedQuestionRepository;

    public AiAssistantGuideQueryService(AiGuideDefinitionRepository guideDefinitionRepository,
                                        AiSuggestedQuestionRepository suggestedQuestionRepository) {
        this.guideDefinitionRepository = guideDefinitionRepository;
        this.suggestedQuestionRepository = suggestedQuestionRepository;
    }

    public Optional<AiGuideDefinition> findGuide(String pageCode, String fieldCode,
                                                  String actionCode, String locale) {
        return guideDefinitionRepository.findByPageCodeAndFieldCodeAndActionCodeAndLocaleAndStatus(
                pageCode, fieldCode, actionCode, locale, "ACTIVE");
    }

    public List<AiSuggestedQuestion> getSuggestedQuestions(String pageCode, String entityType, String locale) {
        if (entityType != null) {
            return suggestedQuestionRepository.findByPageCodeAndEntityTypeAndLocaleAndStatus(
                    pageCode, entityType, locale, "ACTIVE");
        }
        return suggestedQuestionRepository.findByPageCodeAndLocaleAndStatus(pageCode, locale, "ACTIVE");
    }
}
