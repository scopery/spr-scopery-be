package com.company.scopery.modules.externalparty.address.application.action;

import com.company.scopery.modules.externalparty.address.application.command.CreateExternalPartyAddressCommand;
import com.company.scopery.modules.externalparty.address.application.response.ExternalPartyAddressResponse;
import com.company.scopery.modules.externalparty.address.domain.enums.AddressType;
import com.company.scopery.modules.externalparty.address.domain.model.ExternalPartyAddress;
import com.company.scopery.modules.externalparty.address.domain.model.ExternalPartyAddressRepository;
import com.company.scopery.modules.externalparty.shared.activity.ExternalPartyActivityLogger;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyActivityActions;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyEntityTypes;
import com.company.scopery.modules.externalparty.shared.error.ExternalPartyExceptions;
import com.company.scopery.modules.externalparty.shared.util.ExternalPartyEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateExternalPartyAddressAction {
    private final ExternalPartyAddressRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    private final ExternalPartyActivityLogger activityLogger;

    public CreateExternalPartyAddressAction(ExternalPartyAddressRepository repo,
            ExternalPartyAuthorizationService authorization, ExternalPartyActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }

    @Transactional
    public ExternalPartyAddressResponse execute(CreateExternalPartyAddressCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        if (c.externalOrganizationId() == null && c.externalContactId() == null) {
            throw ExternalPartyExceptions.addressOwnerRequired();
        }
        var type = ExternalPartyEnumParser.parseRequired(AddressType.class, c.addressType(), "addressType");
        var saved = repo.save(ExternalPartyAddress.create(c.workspaceId(), c.externalOrganizationId(),
                c.externalContactId(), type, c.line1(), c.line2(), c.city(),
                c.stateRegion(), c.postalCode(), c.countryCode(), c.primaryFlag()));
        activityLogger.logSuccess(ExternalPartyEntityTypes.ADDRESS, saved.id(), ExternalPartyActivityActions.ADDRESS_CREATED, "Address created");
        return ExternalPartyAddressResponse.from(saved);
    }
}
