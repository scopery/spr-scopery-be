package com.company.scopery.modules.configuration.taxonomy.application.action;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.taxonomy.application.response.TaxonomyTermResponse;
import com.company.scopery.modules.configuration.taxonomy.domain.model.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.taxonomy.application.command.CreateTaxonomyTermCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTaxonomyTermAction {
    private final TaxonomyRepository taxonomies; private final TaxonomyTermRepository terms; private final ConfigurationAuthorizationService authorization;
    public CreateTaxonomyTermAction(TaxonomyRepository taxonomies, TaxonomyTermRepository terms, ConfigurationAuthorizationService authorization) {
        this.taxonomies=taxonomies; this.terms=terms; this.authorization=authorization;
    }
    @Transactional
    public TaxonomyTermResponse execute(CreateTaxonomyTermCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        taxonomies.findByIdAndWorkspaceId(c.taxonomyId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.taxonomyNotFound(c.taxonomyId()));
        return TaxonomyTermResponse.from(terms.save(TaxonomyTerm.create(c.taxonomyId(), c.parentId(), c.code(), c.label())));
    }
}
