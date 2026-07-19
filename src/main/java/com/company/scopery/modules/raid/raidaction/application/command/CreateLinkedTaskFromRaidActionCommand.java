package com.company.scopery.modules.raid.raidaction.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLinkedTaskFromRaidActionCommand(
        UUID projectId,
        UUID raidActionId,
        UUID phaseId,
        UUID wbsNodeId,
        BigDecimal estimateHours
) {}
