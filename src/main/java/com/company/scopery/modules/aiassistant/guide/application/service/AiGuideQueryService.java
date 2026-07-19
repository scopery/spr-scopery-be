package com.company.scopery.modules.aiassistant.guide.application.service;

import com.company.scopery.modules.aiassistant.application.service.AiAssistantGuideQueryService;
import com.company.scopery.modules.aiassistant.guide.application.response.AiSuggestedQuestionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AiGuideQueryService {

    private final AiAssistantGuideQueryService guideQueryService;

    public AiGuideQueryService(AiAssistantGuideQueryService guideQueryService) {
        this.guideQueryService = guideQueryService;
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
}
