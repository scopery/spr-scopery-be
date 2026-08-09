package com.company.scopery.modules.elicitation.question.application.service;

import com.company.scopery.modules.elicitation.question.application.response.ElicitationQuestionResponse;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ElicitationQuestionQueryService {

    private final ElicitationQuestionRepository questionRepository;

    public ElicitationQuestionQueryService(ElicitationQuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public List<ElicitationQuestionResponse> listBySession(UUID sessionId) {
        return questionRepository.findAllBySessionId(sessionId)
                .stream()
                .map(ElicitationQuestionResponse::from)
                .toList();
    }
}
