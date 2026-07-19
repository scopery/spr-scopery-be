package com.company.scopery.modules.resourcereference.resourcetype.application.service;

import com.company.scopery.modules.resourcereference.resourcetype.application.response.ResourceTypeResponse;
import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeRepository;
import com.company.scopery.modules.resourcereference.shared.error.ResourceReferenceExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ResourceTypeQueryService {

    private final MentionResourceTypeRepository repo;

    public ResourceTypeQueryService(MentionResourceTypeRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ResourceTypeResponse> listAll() {
        return repo.findAll().stream().map(ResourceTypeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ResourceTypeResponse> listEnabled() {
        return repo.findAllEnabled().stream().map(ResourceTypeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ResourceTypeResponse get(UUID id) {
        return repo.findById(id)
                .map(ResourceTypeResponse::from)
                .orElseThrow(() -> ResourceReferenceExceptions.resourceTypeNotFound(id.toString()));
    }
}
