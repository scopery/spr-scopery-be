package com.company.scopery.modules.productivity.savedsearch.application.service;
import com.company.scopery.modules.productivity.savedsearch.application.response.SavedSearchResponse;
import com.company.scopery.modules.productivity.savedsearch.domain.model.SavedSearchRepository;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class SavedSearchQueryService {
    private final SavedSearchRepository repo; private final ProductivityAuthorizationService authorization;
    public SavedSearchQueryService(SavedSearchRepository repo, ProductivityAuthorizationService authorization) { this.repo=repo; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public List<SavedSearchResponse> list(UUID workspaceId) {
        authorization.requireSavedSearchManage(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().filter(s -> !"ARCHIVED".equals(s.status())).map(SavedSearchResponse::from).toList();
    }
}
