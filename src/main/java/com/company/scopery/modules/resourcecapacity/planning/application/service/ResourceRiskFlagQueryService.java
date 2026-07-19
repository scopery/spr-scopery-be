package com.company.scopery.modules.resourcecapacity.planning.application.service;

import com.company.scopery.modules.resourcecapacity.planning.application.response.ResourceRiskFlagResponse;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ResourceRiskFlagQueryService {
    private final ResourceRiskFlagRepository repo;

    public ResourceRiskFlagQueryService(ResourceRiskFlagRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ResourceRiskFlagResponse> listByProject(UUID projectId) {
        return repo.findByProjectId(projectId).stream()
                .map(ResourceRiskFlagResponse::from)
                .toList();
    }
}
