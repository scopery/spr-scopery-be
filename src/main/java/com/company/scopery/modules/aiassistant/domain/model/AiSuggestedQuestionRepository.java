package com.company.scopery.modules.aiassistant.domain.model;

import java.util.List;

public interface AiSuggestedQuestionRepository {

    List<AiSuggestedQuestion> findByPageCodeAndEntityTypeAndLocaleAndStatus(
            String pageCode, String entityType, String locale, String status);

    List<AiSuggestedQuestion> findByPageCodeAndLocaleAndStatus(
            String pageCode, String locale, String status);
}
