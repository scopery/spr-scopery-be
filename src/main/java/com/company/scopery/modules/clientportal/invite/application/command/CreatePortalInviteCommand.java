package com.company.scopery.modules.clientportal.invite.application.command;
import java.util.UUID;
public record CreatePortalInviteCommand(UUID projectId, String email, Integer expiresInDays) {}
