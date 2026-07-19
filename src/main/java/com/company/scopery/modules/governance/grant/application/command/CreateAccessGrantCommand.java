package com.company.scopery.modules.governance.grant.application.command;
import java.util.UUID;
public record CreateAccessGrantCommand(UUID projectId, String objectTypeCode, UUID targetId, String granteeType, UUID granteeId, String grantRole) {}
