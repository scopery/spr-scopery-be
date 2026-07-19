package com.company.scopery.modules.configuration.taxonomy.application.action;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.taxonomy.application.response.TaxonomyResponse;
import com.company.scopery.modules.configuration.taxonomy.domain.model.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.taxonomy.application.command.CreateTaxonomyCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTaxonomyAction {
    private final TaxonomyRepository taxonomies; private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public CreateTaxonomyAction(TaxonomyRepository taxonomies, ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.taxonomies=taxonomies; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TaxonomyResponse execute(CreateTaxonomyCommand c) {
        authorization.requireFieldCreate(c.workspaceId());
        var t = taxonomies.save(Taxonomy.create(c.workspaceId(), c.code(), c.name()));
        activityLogger.logSuccess(ConfigurationEntityTypes.TAXONOMY, t.id(), ConfigurationActivityActions.TAXONOMY_CREATED, "Taxonomy created");
        return TaxonomyResponse.from(t);
    }
}
