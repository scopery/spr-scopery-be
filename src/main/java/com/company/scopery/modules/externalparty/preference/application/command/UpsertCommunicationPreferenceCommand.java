package com.company.scopery.modules.externalparty.preference.application.command;

import java.util.UUID;

public record UpsertCommunicationPreferenceCommand(
        UUID workspaceId,
        UUID externalOrganizationId,
        UUID externalContactId,
        String preferredChannelType,
        String preferredLanguage,
        boolean doNotContact,
        String notes) {}
