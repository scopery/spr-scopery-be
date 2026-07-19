package com.company.scopery.modules.configuration.taxonomy.application.service;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.taxonomy.application.response.*;
import com.company.scopery.modules.configuration.taxonomy.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TaxonomyQueryService {
    private final TaxonomyRepository taxonomies; private final TaxonomyTermRepository terms; private final ConfigurationAuthorizationService authorization;
    public TaxonomyQueryService(TaxonomyRepository taxonomies, TaxonomyTermRepository terms, ConfigurationAuthorizationService authorization) {
        this.taxonomies=taxonomies; this.terms=terms; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<TaxonomyResponse> list(UUID workspaceId) {
        authorization.requireFieldView(workspaceId);
        return taxonomies.findByWorkspaceId(workspaceId).stream().map(TaxonomyResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public List<TaxonomyTermResponse> listTerms(UUID workspaceId, UUID taxonomyId) {
        authorization.requireFieldView(workspaceId);
        taxonomies.findByIdAndWorkspaceId(taxonomyId, workspaceId).orElseThrow(() -> ConfigurationExceptions.taxonomyNotFound(taxonomyId));
        return terms.findByTaxonomyId(taxonomyId).stream().map(TaxonomyTermResponse::from).toList();
    }
}
