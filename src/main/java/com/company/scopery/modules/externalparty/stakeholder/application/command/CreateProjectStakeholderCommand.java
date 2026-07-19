package com.company.scopery.modules.externalparty.stakeholder.application.command;
import java.util.UUID;
public record CreateProjectStakeholderCommand(UUID projectId, UUID contactId, UUID organizationId, UUID internalUserId, String stakeholderRole, boolean clientFacing){}
