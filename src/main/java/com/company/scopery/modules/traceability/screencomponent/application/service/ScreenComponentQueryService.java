package com.company.scopery.modules.traceability.screencomponent.application.service;

import com.company.scopery.modules.traceability.screencomponent.application.response.ScreenComponentResponse;
import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ScreenComponentQueryService {

    private final ScreenComponentRepository repo;

    public ScreenComponentQueryService(ScreenComponentRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ScreenComponentResponse> listByScreen(UUID screenId) {
        return repo.findByScreenId(screenId).stream()
                .map(ScreenComponentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScreenComponentResponse> listByComponent(UUID componentId) {
        return repo.findByComponentId(componentId).stream()
                .map(ScreenComponentResponse::from)
                .toList();
    }
}
