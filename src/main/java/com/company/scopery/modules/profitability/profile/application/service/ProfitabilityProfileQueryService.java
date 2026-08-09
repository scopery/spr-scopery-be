package com.company.scopery.modules.profitability.profile.application.service;

import com.company.scopery.modules.profitability.profile.application.response.ProfitabilityProfileResponse;
import com.company.scopery.modules.profitability.profile.domain.model.ProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfitabilityProfileQueryService {

    private final ProfitabilityProfileRepository profiles;

    public ProfitabilityProfileQueryService(ProfitabilityProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Transactional(readOnly = true)
    public ProfitabilityProfileResponse getProfile(UUID projectId) {
        return profiles.findByProjectId(projectId)
                .map(ProfitabilityProfileResponse::from)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public ProfitabilityProfileResponse requireProfile(UUID projectId) {
        return profiles.findByProjectId(projectId)
                .map(ProfitabilityProfileResponse::from)
                .orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
    }
}
