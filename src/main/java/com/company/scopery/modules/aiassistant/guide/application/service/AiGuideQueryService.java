package com.company.scopery.modules.aiassistant.guide.application.service;

import com.company.scopery.common.exception.NotFoundException;
import com.company.scopery.modules.aiassistant.application.service.AiAssistantGuideQueryService;
import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinitionRepository;
import com.company.scopery.modules.aiassistant.guide.application.response.AdminGuideDefinitionResponse;
import com.company.scopery.modules.aiassistant.guide.application.response.AiSuggestedQuestionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AiGuideQueryService {

    private final AiAssistantGuideQueryService guideQueryService;
    private final AiGuideDefinitionRepository repository;

    public AiGuideQueryService(AiAssistantGuideQueryService guideQueryService,
                                AiGuideDefinitionRepository repository) {
        this.guideQueryService = guideQueryService;
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AiSuggestedQuestionResponse> getSuggestedQuestions(String pageCode,
                                                                    String entityType,
                                                                    String locale) {
        String resolvedLocale = locale != null ? locale : "en-US";
        return guideQueryService.getSuggestedQuestions(pageCode, entityType, resolvedLocale)
                .stream()
                .map(AiSuggestedQuestionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminGuideDefinitionResponse> findAll() {
        return repository.findAll().stream()
                .map(AdminGuideDefinitionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminGuideDefinitionResponse findById(UUID id) {
        return repository.findById(id)
                .map(AdminGuideDefinitionResponse::from)
                .orElseThrow(() -> new NotFoundException("Guide definition not found: " + id));
    }
}
