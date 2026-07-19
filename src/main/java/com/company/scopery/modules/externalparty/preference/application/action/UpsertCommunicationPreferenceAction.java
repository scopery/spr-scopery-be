package com.company.scopery.modules.externalparty.preference.application.action;

import com.company.scopery.modules.externalparty.preference.application.command.UpsertCommunicationPreferenceCommand;
import com.company.scopery.modules.externalparty.preference.application.response.CommunicationPreferenceResponse;
import com.company.scopery.modules.externalparty.preference.domain.model.CommunicationPreference;
import com.company.scopery.modules.externalparty.preference.domain.model.CommunicationPreferenceRepository;
import com.company.scopery.modules.externalparty.shared.activity.ExternalPartyActivityLogger;
import com.company.scopery.modules.externalparty.shared.authorization.ExternalPartyAuthorizationService;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyActivityActions;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Component
public class UpsertCommunicationPreferenceAction {
    private final CommunicationPreferenceRepository repo;
    private final ExternalPartyAuthorizationService authorization;
    private final ExternalPartyActivityLogger activityLogger;

    public UpsertCommunicationPreferenceAction(CommunicationPreferenceRepository repo,
            ExternalPartyAuthorizationService authorization, ExternalPartyActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }

    @Transactional
    public CommunicationPreferenceResponse execute(UpsertCommunicationPreferenceCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        Optional<CommunicationPreference> existing = c.externalContactId() != null
                ? repo.findByContactId(c.workspaceId(), c.externalContactId())
                : repo.findByOrganizationId(c.workspaceId(), c.externalOrganizationId());
        boolean wasDoNotContact = existing.map(CommunicationPreference::doNotContact).orElse(false);
        CommunicationPreference pref = existing.isPresent()
                ? existing.get().update(c.preferredChannelType(), c.preferredLanguage(), c.doNotContact(), c.notes())
                : CommunicationPreference.create(c.workspaceId(), c.externalOrganizationId(), c.externalContactId(),
                        c.preferredChannelType(), c.preferredLanguage(), c.doNotContact(), c.notes());
        var saved = repo.save(pref);
        activityLogger.logSuccess(ExternalPartyEntityTypes.PREFERENCE, saved.id(),
                ExternalPartyActivityActions.PREFERENCE_UPDATED, "Communication preference updated");
        if (!wasDoNotContact && saved.doNotContact()) {
            var ownerId = c.externalContactId() != null ? c.externalContactId() : c.externalOrganizationId();
            activityLogger.logSuccess(ExternalPartyEntityTypes.PREFERENCE, ownerId,
                    ExternalPartyActivityActions.DO_NOT_CONTACT_SET, "Marked do-not-contact");
        }
        return CommunicationPreferenceResponse.from(saved);
    }
}
