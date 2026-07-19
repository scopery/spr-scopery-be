package com.company.scopery.modules.governance.objecttype.application.service;
import com.company.scopery.modules.governance.objecttype.application.response.GovernedObjectTypeResponse;
import com.company.scopery.modules.governance.objecttype.domain.model.GovernedObjectTypeRepository;
import com.company.scopery.modules.governance.shared.error.GovernanceExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class GovernedObjectTypeQueryService {
    private final GovernedObjectTypeRepository repo;
    public GovernedObjectTypeQueryService(GovernedObjectTypeRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<GovernedObjectTypeResponse> listAll() {
        return repo.findAllEnabled().stream().map(GovernedObjectTypeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public GovernedObjectTypeResponse getByCode(String code) {
        return GovernedObjectTypeResponse.from(repo.findByCode(code)
                .orElseThrow(() -> GovernanceExceptions.objectTypeNotFound(code)));
    }
}
