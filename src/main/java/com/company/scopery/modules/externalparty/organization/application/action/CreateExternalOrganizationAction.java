package com.company.scopery.modules.externalparty.organization.application.action;
import com.company.scopery.modules.externalparty.organization.application.command.CreateExternalOrganizationCommand;
import com.company.scopery.modules.externalparty.organization.application.response.ExternalOrganizationResponse;
import com.company.scopery.modules.externalparty.organization.domain.enums.OrganizationType;
import com.company.scopery.modules.externalparty.organization.domain.model.ExternalOrganization;
import com.company.scopery.modules.externalparty.organization.domain.model.ExternalOrganizationRepository;
import com.company.scopery.modules.externalparty.shared.activity.ExternalPartyActivityLogger;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyActivityActions;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyEntityTypes;
import com.company.scopery.modules.externalparty.shared.util.ExternalPartyEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateExternalOrganizationAction {
    private final ExternalOrganizationRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    private final ExternalPartyActivityLogger activityLogger;
    public CreateExternalOrganizationAction(ExternalOrganizationRepository repo, ExternalPartyAuthorizationService authorization, ExternalPartyActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public ExternalOrganizationResponse execute(CreateExternalOrganizationCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var type = ExternalPartyEnumParser.parseRequired(OrganizationType.class, c.organizationType(), "organizationType");
        var saved = repo.save(ExternalOrganization.create(c.workspaceId(), c.code(), c.name().trim(), type, c.taxId(), c.website(), c.notes()));
        activityLogger.logSuccess(ExternalPartyEntityTypes.ORGANIZATION, saved.id(), ExternalPartyActivityActions.ORGANIZATION_CREATED, "Organization created");
        return ExternalOrganizationResponse.from(saved);
    }
}
