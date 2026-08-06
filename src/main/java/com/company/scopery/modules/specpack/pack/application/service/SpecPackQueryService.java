package com.company.scopery.modules.specpack.pack.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.specpack.pack.application.query.SearchSpecPackQuery;
import com.company.scopery.modules.specpack.pack.application.response.SpecPackResponse;
import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackStatus;
import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackType;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPackRepository;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import com.company.scopery.modules.specpack.shared.util.SpecPackEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SpecPackQueryService {

    private final SpecPackRepository repository;

    public SpecPackQueryService(SpecPackRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public SpecPackResponse getById(UUID projectId, UUID packId) {
        return repository.findById(packId)
                .filter(p -> p.projectId().equals(projectId))
                .map(SpecPackResponse::from)
                .orElseThrow(() -> SpecPackExceptions.specPackNotFound(packId));
    }

    @Transactional(readOnly = true)
    public PageResult<SpecPackResponse> search(SearchSpecPackQuery query) {
        SpecPackType packType = SpecPackEnumParser.parseOptional(SpecPackType.class, query.packType(), "packType");
        SpecPackStatus status = SpecPackEnumParser.parseOptional(SpecPackStatus.class, query.status(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), query.sortBy(), query.ascending());
        return repository.findAllByProject(query.projectId(), query.keyword(), packType, status, pageQuery)
                .map(SpecPackResponse::from);
    }
}
