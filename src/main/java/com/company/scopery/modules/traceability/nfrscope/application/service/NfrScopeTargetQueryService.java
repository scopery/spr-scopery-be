package com.company.scopery.modules.traceability.nfrscope.application.service;

import com.company.scopery.modules.traceability.nfrscope.application.response.NfrScopeTargetResponse;
import com.company.scopery.modules.traceability.nfrscope.domain.model.NfrScopeTargetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NfrScopeTargetQueryService {

    private final NfrScopeTargetRepository repo;

    public NfrScopeTargetQueryService(NfrScopeTargetRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<NfrScopeTargetResponse> listByNfr(UUID nfrId) {
        return repo.findByNfrId(nfrId).stream().map(NfrScopeTargetResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<NfrScopeTargetResponse> listByTarget(UUID targetId) {
        return repo.findByTargetId(targetId).stream().map(NfrScopeTargetResponse::from).toList();
    }
}
