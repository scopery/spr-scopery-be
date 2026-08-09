package com.company.scopery.modules.profitability.summary.application.service;

import com.company.scopery.modules.profitability.summary.application.response.ProfitabilitySummaryResponse;
import com.company.scopery.modules.profitability.summary.domain.model.ProfitabilitySummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfitabilitySummaryQueryService {

    private final ProfitabilitySummaryRepository summaries;

    public ProfitabilitySummaryQueryService(ProfitabilitySummaryRepository summaries) {
        this.summaries = summaries;
    }

    @Transactional(readOnly = true)
    public ProfitabilitySummaryResponse getSummary(UUID projectId) {
        return summaries.findByProjectId(projectId)
                .map(ProfitabilitySummaryResponse::from)
                .orElse(null);
    }
}
