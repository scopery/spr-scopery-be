package com.company.scopery.modules.specpack.version.application.service;

import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import com.company.scopery.modules.specpack.version.application.response.SpecPackVersionResponse;
import com.company.scopery.modules.specpack.version.domain.model.SpecPackVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SpecPackVersionQueryService {

    private final SpecPackVersionRepository repository;

    public SpecPackVersionQueryService(SpecPackVersionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<SpecPackVersionResponse> listByPack(UUID specPackId) {
        return repository.findAllBySpecPackId(specPackId).stream()
                .map(SpecPackVersionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SpecPackVersionResponse getById(UUID specPackId, UUID versionId) {
        return repository.findById(versionId)
                .filter(v -> v.specPackId().equals(specPackId))
                .map(SpecPackVersionResponse::from)
                .orElseThrow(() -> SpecPackExceptions.versionNotFound(versionId));
    }
}
