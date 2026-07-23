package com.company.scopery.modules.traceability.functionscreen.application.service;

import com.company.scopery.modules.traceability.functionscreen.application.response.FunctionScreenResponse;
import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FunctionScreenQueryService {

    private final FunctionScreenRepository repo;

    public FunctionScreenQueryService(FunctionScreenRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<FunctionScreenResponse> listByFunction(UUID functionId) {
        return repo.findByFunctionId(functionId).stream().map(FunctionScreenResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<FunctionScreenResponse> listByScreen(UUID screenId) {
        return repo.findByScreenId(screenId).stream().map(FunctionScreenResponse::from).toList();
    }
}
