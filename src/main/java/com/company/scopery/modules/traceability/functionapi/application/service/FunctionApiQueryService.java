package com.company.scopery.modules.traceability.functionapi.application.service;

import com.company.scopery.modules.traceability.functionapi.application.response.FunctionApiResponse;
import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FunctionApiQueryService {

    private final FunctionApiRepository repo;

    public FunctionApiQueryService(FunctionApiRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<FunctionApiResponse> listByFunction(UUID functionId) {
        return repo.findByFunctionId(functionId).stream().map(FunctionApiResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<FunctionApiResponse> listByApiEndpoint(UUID apiEndpointId) {
        return repo.findByApiEndpointId(apiEndpointId).stream().map(FunctionApiResponse::from).toList();
    }
}
