package com.company.scopery.modules.externalparty.contact.application.command;
import java.util.UUID;
public record CreateExternalContactCommand(UUID workspaceId, UUID organizationId, String firstName, String lastName, String email, String phone, String title, Boolean primaryFlag) {}
